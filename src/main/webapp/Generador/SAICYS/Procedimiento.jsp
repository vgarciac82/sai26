<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	//String path = request.getContextPath();
	//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	if(usuario==null){
	response.sendRedirect("../index.jsp");
	return;
	}
	String name_user=usuario.getLogin();
	Map rol = usuario.getRoles();
	if (session.getAttribute( GestionInterface.ATT_ReqEjercicio) != null ) {
		session.setAttribute(GestionInterface.ATT_ReqEjercicio, null);
		session.setAttribute(GestionInterface.ATT_ReqTipoSolicitud, null);
		session.setAttribute(GestionInterface.ATT_ReqUnidadEjec, null);
		session.setAttribute(GestionInterface.ATT_ReqConsecutivo, null);
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
		<meta http-equiv="description" content="Procedimiento">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
				@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
				@import "../css/demo_table_jui.css";
				@import "../css/demo_page.css";
		</style>
		
		<!-- Estilos de dialogo -->
		<style>
		
		div#dialog-form fieldset {
			padding: 0;
			border: 0;
			margin-top: 25px;
		}
		div#users-contain {
			width: 350px;
			margin: 20px 0;
		}
		div#users-contain table {
			margin: 1em 0;
			border-collapse: collapse;
			width: 100%;
		}
		div#users-contain table td,div#users-contain table th {
			border: 1px solid #eee;
			padding: .6em 10px;
			text-align: left;
		}
		
		.ui-dialog .ui-state-error {
			padding: .3em;
		}
		
		.validateTips {
			border: 1px solid transparent;
			padding: 0.3em;
		}
		</style>
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTablesSAICYSPA.js"></script>
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
		
		//-----------------------------CARGA DEL DOCUMENTO------------------------
		$(document).ready(function() {
        		
<%
				int tabla=0;
                int proveedor=0;
                int documentos=0;
                int cotizacion=0;
                int caratula=0;
				int evaluacion=0;
				int preguntas=0;
				int archivo=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map pestanas=ebl.getPestana(roles,"Procedimiento");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
					String pestana=(String)e.getValue();
					if ("CaratulaProcedimiento".equals(pestana)){
						 tabla=1;
						 caratula=1;
					}
					if ("ProveedoresProcedimiento".equals(pestana)){
						 proveedor=1;
					}
					if("DocumentosProcedimiento".equals(pestana)){
						documentos=1;
					}
					if ("CotizacionProcedimiento".equals(pestana)){
						 cotizacion=1;
					}
					if ("EvaluacionProcedimiento".equals(pestana)){
						 evaluacion=1;
					}
					if ("PreguntasProcedimiento".equals(pestana)){
						 preguntas=1;
					}
					if ("ArchivosProcedimiento".equals(pestana)){
						 archivo=1;
					}
				}
				///// botones/////
				int imgAdjudicarC=0;
				int imgDesiertoC=0;
				int imgDevolverC=0;
				int btnGuardarCaratulaProcedimiento=0;
				Map botonesCaratula=nb.getBotones(roles,"Procedimiento","CaratulaProcedimiento");
				Iterator btnCaratula = botonesCaratula.entrySet().iterator();
				while (btnCaratula.hasNext()) {
					Map.Entry bCaratula = (Map.Entry)btnCaratula.next();%>
					$("#<%=bCaratula.getValue()%>").attr("disabled", true);<%	
					String img=(String) bCaratula.getValue();
					if ("imgAdjudicarCaratulaProc".equals(img)){
						
						imgAdjudicarC=1; 
					}
					if ("imgDesiertoCaratulaProc".equals(img)){
						imgDesiertoC=1;
					}
					if ("imgDevolverCaratulaProc".equals(img)){
						imgDevolverC=1;
					}
					if ("btnGuardarCaratulaProcedimiento".equals(img)){
						btnGuardarCaratulaProcedimiento=1;
					}
				}
				
				Map botonesConsulta=nb.getBotones(roles,"Procedimiento","ConsultaProcedimiento");
				Iterator btnConsulta = botonesConsulta.entrySet().iterator();
				while (btnConsulta.hasNext()) {
					Map.Entry bConsulta = (Map.Entry)btnConsulta.next();%>
					$("#<%=bConsulta.getValue()%>").attr("disabled", true);<%			
				}
				int imgAdjudicarCP=0;
				int imgDesiertoCP=0;
				int imgDevolverCP=0;
				Map botonesCotizacion=nb.getBotones(roles,"Procedimiento","CotizacionProcedimiento");
				Iterator btnCotizacion = botonesCotizacion.entrySet().iterator();
				while (btnCotizacion.hasNext()) {
					Map.Entry bCotizacion = (Map.Entry)btnCotizacion.next();%>
					$("#<%=bCotizacion.getValue()%>").attr("disabled", true);<%
					String img=(String) bCotizacion.getValue();
					if ("imgAdjudicarCotizacionProc".equals(img)){    
						imgAdjudicarCP=1; 
					}
					if ("imgDesiertoCotizacionProc".equals(img)){
						imgDesiertoCP=1;
					}
					if ("imgDevolverCotizacionProc".equals(img)){
						imgDevolverCP=1;
					}
				}
				
				Map botonesNuevo=nb.getBotones(roles,"Procedimiento","NuevoProcedimiento");
				Iterator btnNuevo = botonesNuevo.entrySet().iterator();
				while (btnNuevo.hasNext()) {
					Map.Entry bNuevo= (Map.Entry)btnNuevo.next();%>
					$("#<%=bNuevo.getValue()%>").attr("disabled", true);<%			
				}
				int imgAdjudicarP=0;
				int imgDesiertoP=0;
				int imgDevolverP=0;
				Map botonesProveedores=nb.getBotones(roles,"Procedimiento","ProveedoresProcedimiento");
				Iterator btnProveedores = botonesProveedores.entrySet().iterator();
				while (btnProveedores.hasNext()) {
					Map.Entry bProveedores = (Map.Entry)btnProveedores.next();%>
					$("#<%=bProveedores.getValue()%>").attr("disabled", true);<%	
					String img=(String) bProveedores.getValue();
					if ("imgAdjudicarProveedoresProc".equals(img)){     
						imgAdjudicarP=1; 
					}
					if ("imgDesiertoProveedoresProc".equals(img)){
						imgDesiertoP=1;
					}
					if ("imgDevolverProveedoresProc".equals(img)){
						imgDevolverP=1;
					}
				}
				
				///////////documentos procedimiento
				
				Map botonesDocumentos=nb.getBotones(roles,"Procedimiento","DocumentosProcedimiento");
				Iterator btnDocumentos = botonesDocumentos.entrySet().iterator();
				while (btnDocumentos.hasNext()) {
					Map.Entry bDocumentos = (Map.Entry)btnDocumentos.next();%>
					$("#<%=bDocumentos.getValue()%>").attr("disabled", true);<%	
					String img=(String) bDocumentos.getValue();
					
				}
		
				///////////Preguntas Procedimiento
				
				Map botonesPreguntas=nb.getBotones(roles,"Procedimiento","PreguntasProcedimiento");
				Iterator btnPreguntas = botonesPreguntas.entrySet().iterator();
				while (btnPreguntas.hasNext()) {
					Map.Entry bPreguntas = (Map.Entry)btnPreguntas.next();%>
					$("#<%=bPreguntas.getValue()%>").attr("disabled", true);<%	
					String img=(String) bPreguntas.getValue();
					
				}
				/////////evaluacion Procedimiento
				int imgAdjudicarE=0;
				int imgDesiertoE=0;
				int imgDevolverE=0;
				Map botonesEvaluacion=nb.getBotones(roles,"Procedimiento","EvaluacionProcedimiento");
				Iterator btnEvaluacion = botonesEvaluacion.entrySet().iterator();
				while (btnEvaluacion.hasNext()) {
					Map.Entry bEvaluacion = (Map.Entry)btnEvaluacion.next();%>
					$("#<%=bEvaluacion.getValue()%>").attr("disabled", true);<%	
					String img=(String) bEvaluacion.getValue();
					if ("imgAdjudicarEvaluacionProc".equals(img)){     
						imgAdjudicarE=1; 
					}
					if ("imgDesiertoEvaluacionProc".equals(img)){
						imgDesiertoE=1;
					}
					if ("imgDevolverEvaluacionProc".equals(img)){
						imgDevolverE=1;
					}
				}
				
				/////////////////archivos procedimiento
				
				Map botonesArchivos=nb.getBotones(roles,"Procedimiento","ArchivosProcedimiento");
				Iterator btnArchivos = botonesArchivos.entrySet().iterator();
				while (btnArchivos.hasNext()) {
					Map.Entry bArchivos = (Map.Entry)btnArchivos.next();%>
					$("#<%=bArchivos.getValue()%>").attr("disabled", true);<%	
					String img=(String) bArchivos.getValue();
				}

				%>
		// se inicializa con la pesta?a de consulta habilitada 
		var $tabs = $(".tabs").tabs();
		$tabs.tabs('select', 1);
		
		$("#CaratulaProcedimiento").attr("disabled", true);
		$("#ProveedoresProcedimiento").attr("disabled", true);
		$("#DocumentosProcedimiento").attr("disabled", true);
		$("#CotizacionProcedimiento").attr("disabled", true);
		$("#PreguntasProcedimiento").attr("disabled", true);
		$("#EvaluacionProcedimiento").attr("disabled", true);
		$("#RequisitosProcedimiento").attr("disabled", true);
		$("#ArchivosProcedimiento").attr("disabled", true);
		//se checa el ejercicio fiscal
		queryFormPost("ejercicioProcedimiento",{async:false});
		$("#usuarioUE").val('<%=usuario.getU_UR()%>');
		queryFormPost("unidadEjecutoraHederProcedimiento",{async:false});
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		queryFormPost("unidadEjecutoraHederProcedimiento",{async:false});
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		
		//checamos el rol del usuario
		queryFormPost("checaRolUsuarioProcedimiento",{async:false});
	
		////inicializacion de las tablas
		
		     $("#tblProcedimientos").dataTable({
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
        	$("#tblRequisitos").dataTable({
				    bPaginate: true,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
        			sScrollY : "100%",
					sScrollX: "100%",
					sScrollXInner: "150%",
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
							
		//inicializacion de tabla de partidas seccion de cotizaciones
		oTable3= $("#tblPartidas").dataTable({
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
		////inicializacion de la tabla de proovedores en la seccion de cotizaciones
		 $("#tblProvedoresCotizaciones").dataTable({
				    bPaginate: false,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
        			sScrollY : "100%",
					sScrollX: "100%",
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

 			
			$("#btnGuardarDocumentoProcedimiento").button().click(function(){
				if(validaModificarProcedimiento()){
					try{
						$("#nIdProcedimiento").val($("#tipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#ConsecutivoProcedimiento").val());
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
			
			//tabla de documentos
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
	       //tabla partidas el procedimientos seccion cotizaciones
             oTable2=$("#tblProovedoresCotizacion").dataTable({
				bAutoWidth : true,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				bServerSide:false,
				sScrollY: "200px",
				sScrollX: "200px",
				//sScrollXInner: "200%",				
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
				},
				//bServerSide: true,
				//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCucopsGrid&qw="+ qw,
				bProcessing: true,
			    sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaConsolidado" ,sType: "string" },
					{ sName: "cIdCABM",sType: "string"  },					
					{ sName: "cDescripcion",sType: "string" 	},
					{ sName: "nCantidad" },
					{ sName: "mMontoMaximoUnitario" },
					{ sName: "mMontoMaximoBruto"},
					{ sName: "nPorcentajeIVA"},
					{ sName: "mMontoNeto" },
					{ sName: "boton1" },
					{ sName: "identificador", bVisible: false }
																		
					
				]
			
        	});
      	//Combo de mi unidad ejecutora
		querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutora", {async: false });
		$("#cIdUnidadEjecutora").val($("#cboUnidadEjecutora").val());
		//Combo del tipo de procedimiento
		querySelectPost("TipoProcedimientoRead", "cboTipoProcedimiento", {async: false });
		querySelectPost("tipoProcesoProcedimiento","tipoProcesoProcedimiento", {async: false });
		eliminaOpcionTipoProcedimiento();
		//Lee el consolidado
		querySelectPost("ConsolidadoRead", "cboConsolidado", {async : false});
		// llena tipo de cambio
		querySelectPost("llenaTipoCambioCotizaciones","cboCambioCotizacion",{async:false});
		// llena combo tipo proceso en busqueda
		querySelectPost("tipoProcesoProcedimientoAll","tipoProcesoProcedimientoBuscar",{async:false});
		
		//obtiene tipo de cambio de la tabla de msistema
		queryFormPost("obtieneTipoCambio",{async:false});
		//Colocar valor del crud en los hidden
		//Categoria en el nuevo
		querySelectPost("CategoriaRead", "cboCategoria", {async : false});
		if($("#cboConsolidado").val()!=null)
		{
			var cadena_campos=$("#cboConsolidado").val().split('|');
			$("#TipoConsolidado").val(cadena_campos[0]);
			$("#ConsecutivoConsolidado").val(cadena_campos[1]);
			$("#descripcion").val(cadena_campos[2]);
			queryFormPost("obtieneCategoriaReq",{async:false});
  			$("#cboCategoria").val($("#nidcategoria").val());
  			
  			if(parseInt($("#nidcategoria").val(),10) <= 4)
  				$("#etiquetaChkActivoNuevo").hide();
  			else
				$("#etiquetaChkActivoNuevo").show();
		}
		//Categoria en caratula
		querySelectPost("CategoriaRead", "cboCategoriaCaratula", {async : false});
		
		//Intenta hasta 3 veces dibujar la tabla de las fechas
		
		
		if($("#cboTipoProcedimiento").val().toString()=="PS" || $("#cboTipoProcedimiento").val().toString()=="PA" || $("#cboTipoProcedimiento").val().toString()=="PL" || $("#cboTipoProcedimiento").val().toString()=="PN"){
			$("#esServicio").val(1);
		}else{
			$("#esServicio").val(0);
		}
		
		muestraFechas();	
		//Se muestra de primera instancia la tabla de proveedores agregados
		agregadosTablaProveedores();
		//
		//mostrarTablaProcedimientos();	
		$("#NuevoProcedimiento").click(function() {
			$("#formProveedores").css("visibility","hidden");
			$("#tblProvCotizacion").css("visibility","hidden");
			$("#tblPartidasCotizacion").css("visibility","hidden");
			querySelectPost("ConsolidadoRead", "cboConsolidado", {async : false});
				//Colocar valor del crud en los hidden
				if($("#cboConsolidado").val()!=null){
					var cadena_campos=$("#cboConsolidado").val().split('|');
					$("#TipoConsolidado").val(cadena_campos[0]);
					$("#ConsecutivoConsolidado").val(cadena_campos[1]);
					$("#descripcion").val(cadena_campos[2]);
				}
				//Desabilita caratula y proveedores
				$("#CaratulaProcedimiento").attr("disabled", true);
				$("#ProveedoresProcedimiento").attr("disabled", true);
				$("#DocumentosProcedimiento").attr("disabled", true);
				$("#CotizacionProcedimiento").attr("disabled", true);
				$("#EvaluacionProcedimiento").attr("disabled", true);
				$("#CotizacionProcedimiento").css("display", "none");
				$("#PreguntasProcedimiento").css("display", "none");
				$("#RequisitosProcedimiento").attr("disabled", true);
				$("#ArchivosProcedimiento").attr("disabled", true);
				//El estado de captura sera en 1
				$("#EstadoCaptura").val('1');
				validaFechaTerminoServicio(document.getElementById("cboTipoProcedimiento"));
		});
		$( "#ConsultaProcedimiento").click(function() {//Consultar
		});
		$( "#CaratulaProcedimiento").click(function() {//Caratula
			muestraInformacionCaratula();
			ponValorCheckBox();
			 validaLicitacion();
		});
		
			
		$("#PreguntasProcedimiento").click(function() {//preguntas
			$("#lblProveedorPreguntas").val("");
			leeProveedoresPreguntas();
			muestraPreguntas();
		});
		$("#EvaluacionProcedimiento").click(function() {//preguntas
			//muestraPartidasEvaluacion();
		if ($("#estadoProcedimiento").val()=='ADJUDICADO'){
			$("#btnGuardaProcedimientoCompleto").css("visibility","hidden");
			$("#btnCopiaObservaciones").css("visibility","hidden");
			$("#btnSeleccionaTodos").css("visibility","hidden");
			 
		}
	 tablaProveedorPartida();
	//	$("#div_tblPartidasProveedorEvaluacion2").css("display","none");
			queryFormPost("llenaIvaEvaluaciones",{async:false});
		    muestraProveedoresEvaluacion();
		    firmantesTblComparativa();
		     mostrarCatalogo ();
		     
		});

		$("#ArchivosProcedimiento").click(function() {//archivos
			enviaDatosArchivoProcedimiento();
		});
		$("#btnSeleccionaPartidas").button().click(function(){
		var aTrs = oTable3.dataTable().fnGetNodes();
		for ( var i=0 ; i<aTrs.length; i++ ){
			aData = oTable3.fnGetData(aTrs[i]);
			try{
               $("#lineaConsolidado").val(aData[0]);
               queryFormPost("obtieneCidProcedimiento", {async: false });
			   queryFormPost("promedioProcedimientoPartidas", {async: false });
	           $("#montoMinimo").val($("#totalPromedioProcedimiento").val());
			   $("#montoMaximo").val($("#totalPromedioProcedimiento").val());
			   $("#descripcionPartida").val(aData[2]);
                //procedimiento almacenado para agregar cucops al programa anual
			    queryFormPost("spAgregaLineaPartida", {async: false });
			 
			   //oTable3.dataTable().fnDeleteRow( i );
			   //se actualiza tabla de partidas
			   //lenamos rfc y razon social
				queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
				//llena montos bruto y neto
				queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
				queryFormPost("llenaMontoNetoCotizaciones",{async:false});
	            // llena iva y tipo de cambio
		        queryFormPost("llenaIvaCotizaciones",{async:false}); 
		        queryFormPost("tipodeCambioProcedimiento",{async:false});
                if($("#idTipoCambio").val()!="01"){
                   $("#cboCambioCotizacion").val($("#idTipoCambio").val());
                   $("#valorTipoCambio").css("visibility","visible");
                   $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
                }else{
				   $("#cboCambioCotizacion").val($("#idTipoCambio").val());
                   $("#valorTipoCambio").css("visibility","hidden");
                   $("#valorTipoCambio").val($("#valorTipoCambio1").val());
                }
				$("#valorTipoCambio").formatCurrency();
		        $("#montoBrutoCotizacion").formatCurrency();
				$("#montoNetoCotizacion").formatCurrency();
			} catch(e){
			  alert("No se pudo agregar Partida...");
			} 
			}							             
			oTable3.dataTable().fnClearTable(); 
			var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'"; 
			var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
			query += "  AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
			 //se borra la tabla antes de realizar la consulta
			muestraPartidasElegidas(query,func);
			// $("#tblProovedoresCotizacion").css("visibility","visible");
		    //se actualiza tabla de cucops
		});

		//cuando se le da click a la tabla de proovedores en la seccion cotizaciones
		$('#tblProvedoresCotizaciones tr').live('click', function() {
			if($("#nIdEstadoImagenCotizacion").val().toString().indexOf("CAPTURADO") >= 0){
				$("#chk_pedidoAbierto").removeAttr("disabled");
			}
			else{
				$("#chk_pedidoAbierto").attr("disabled","true");
			}
			document.getElementById("chk_pedidoAbierto").checked=false;
			$("#btnGuardarCotizacionProc").css("display","");
			$("#btnPartidaCotizacionProc").css("display","");
			$("#tblProvCotizacion").css("display","");
		
			if(validaModificarProcedimiento()){
		   if ( $(this).hasClass('row_selected') ) {            
					$(this).removeClass('row_selected'); 
		   }else{
			  var aTrs = $('#tblProvedoresCotizaciones').dataTable().fnGetNodes();
		      for(var i=aTrs.length;i>=0; i-- ){       
				$(aTrs[i]).removeClass('row_selected'); 
				$(this).addClass('row_selected'); 
			  }
			}    
		      
			//$(this).addClass('row_selected'); 
			//mostramos formulario de proovedores en la seccion de cotizaciones
		   $("#desProvedorCotizacion").css("visibility","visible");
		   $("#btnGuardarCotizacionProc").css("visibility","visible");
		   $("#btnPartidaCotizacionProc").css("visibility","visible");
		   $("#tblProvCotizacion").css("visibility","visible");
			//$("#tablaProveedores").css("visibility","visible");
		   $("#formProveedores").css("visibility","visible");
		   if($("#estadoProcedimiento").val()=="ADJUDICADO"){
	               //$("#tblProvCotizacion").css("visibility","visible");
	               $("#btnGuardarCotizacionProc").css("visibility","hidden");
	               $("#btnPartidaCotizacionProc").css("visibility","hidden");
	               
           }  
                    
             var aTrs = $('#tblProvedoresCotizaciones').dataTable().fnGetNodes();
 			for ( var i=aTrs.length ; i>=0; i-- ){
				if ($(aTrs[i]).hasClass('row_selected')){   
					$(this).addClass('row_selected'); 
					$("#cidRFCOculto").val($("#nIdRFCProveedor_"+i+"").val());
					//lenamos rfc y razon social
					queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
					//llena montos bruto y neto
					queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
					queryFormPost("llenaMontoNetoCotizaciones",{async:false});
		            // llena iva y tipo de cambio
		            queryFormPost("llenaIvaCotizaciones",{async:false}); 
		            queryFormPost("tipodeCambioProcedimiento",{async:false});
		            queryFormPost("cContratoAbiertoRead",{async:false});

		            if($("#IContratoAbiertoProveedor").val()=="TRUE"){
			            document.getElementById("chk_pedidoAbierto").checked=true;
			        }
		            else{
		            	document.getElementById("chk_pedidoAbierto").checked=false;
			        }
                    if($("#idTipoCambio").val()!="01"){
                        $("#cboCambioCotizacion").val($("#idTipoCambio").val());
                        $("#valorTipoCambio").css("visibility","visible");
                      	$("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
                     }else{
                        $("#cboCambioCotizacion").val($("#idTipoCambio").val());
                        $("#valorTipoCambio").css("visibility","hidden");
                        $("#valorTipoCambio").val($("#valorTipoCambio1").val());
                     }
                     $("#valorTipoCambio").formatCurrency();
				     $("#montoBrutoCotizacion").formatCurrency();
					 $("#montoNetoCotizacion").formatCurrency();            
					var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'"; 
					var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
					query += "  AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
								 //se borra la tabla antes de realizar la consulta
					muestraPartidasElegidas	(query,func);          
		}  
	}
			}		
});
	//esto siempre lo hace para los renglones de las tablas
	$('#tblPartidas tr').live('click', function() {
		if(validaModificarProcedimiento()){
			if ( $(this).hasClass('row_selected') )             
				$(this).removeClass('row_selected');         
			else {
				$(this).addClass('row_selected'); 
			   /// se inserta cucop seleccionado
	            var aTrs = $('#tblPartidas').dataTable().fnGetNodes();
			    for ( var i=aTrs.length ; i>=0; i-- ){       
					if ( $(aTrs[i]).hasClass('row_selected') ){  
						aData = oTable3.fnGetData(aTrs[i]);
						try{
							
	                        $("#lineaConsolidado").val(aData[0]);
	                        queryFormPost("obtieneCidProcedimiento", {async: false });
	                        queryFormPost("promedioProcedimientoPartidas", {async: false });
	                        $("#montoMinimo").val($("#totalPromedioProcedimiento").val());
							$("#montoMaximo").val($("#totalPromedioProcedimiento").val());
	                        $("#descripcionPartida").val(aData[2]);
	                        //procedimiento almacenado para agregar cucops al programa anual
						    queryFormPost("spAgregaLineaPartida", {async: false });
						    oTable3.dataTable().fnDeleteRow( i );
						    //se actualiza tabla de partidas
						    //llenamos rfc y razon social
							queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
							//llena montos bruto y neto
							queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
							queryFormPost("llenaMontoNetoCotizaciones",{async:false});
				            // llena iva y tipo de cambio
				            queryFormPost("llenaIvaCotizaciones",{async:false}); 
					        queryFormPost("tipodeCambioProcedimiento",{async:false});
		                    if($("#idTipoCambio").val()!="01"){
		                       $("#cboCambioCotizacion").val($("#idTipoCambio").val());
		                       $("#valorTipoCambio").css("visibility","visible");
		                       $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
		                    }else{
		                       $("#cboCambioCotizacion").val($("#idTipoCambio").val());
		                       $("#valorTipoCambio").css("visibility","hidden");
		                       $("#valorTipoCambio").val($("#valorTipoCambio1").val());
		                    }
		                  	$("#valorTipoCambio").formatCurrency();
						  	$("#montoBrutoCotizacion").formatCurrency();
						  	$("#montoNetoCotizacion").formatCurrency();
					            
							var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'"; 
							var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
							query += "  AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
					             //se borra la tabla antes de realizar la consulta
					        $("#IContratoAbiertoProveedor").val("");
					        queryFormPost("cContratoAbiertoRead",{async:false});					        
							muestraPartidasElegidas(query,func);
						    
			               //se actualiza tabla de cucops
			         
						}catch(e){
						 alert("No se pudo agregar partida...");
						}
					}							    
				} 			
	        }
     	}
	});
		
		$("#btnGuardarProcedimiento" ).button().click(function() {
			if ($("#nPorcentajeIVA").val()==""){
				 alert("EL importe del IVA es necesario para continuar con el proceso");
				 return;
			}
			/* if(parseInt($("#cboCategoria").val()) > 4 && !($("#chkActivoNuevo").is(':checked')) ){
				alert("No Cuenta con Documentos o Autorización del Comite");
				return;
			} */
			
			var inputTablaFechasProcedimiento = $('input','#tablaFechasProcedimiento');
				
			if (inputTablaFechasProcedimiento.length < $("#fechasProcedimiento").val()){
				alert("Error al cargar las fechas necesarias. Contacte a su soporte.");
				return;
			}
			
				var banFechasProcedimiento = true;
				var fechaTem="";
				for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
							if(inputTablaFechasProcedimiento[i].value == ""){
								/* if(inputTablaFechasProcedimiento[i].id == "fechaProcedimiento10"){
									if($("#cboTipoProcedimiento").val().toString()=="PN" || $("#cboTipoProcedimiento").val().toString()=="PS" || $("#cboTipoProcedimiento").val().toString()=="PO" || $("#cboTipoProcedimiento").val().toString()=="PQ"){
										alert("Falta insertar algunas fechas");
										banFechasProcedimiento = false;
										return;
									}
								}
								else{ */
									alert("Falta insertar algunas fechas");
									banFechasProcedimiento = false;
									return;
								//}
							}
						}
					}
				}
				for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
							if(fechaTem != ""){
								/* if(inputTablaFechasProcedimiento[i].id == "fechaProcedimiento10"){
									if($("#cboTipoProcedimiento").val() == "PN" || $("#cboTipoProcedimiento").val()=="PS" || $("#cboTipoProcedimiento").val().toString()=="PO" || $("#cboTipoProcedimiento").val().toString()=="PQ"){
										if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
											alert("Las fechas se deben introducir en orden cronologico");
											banFechasProcedimiento = false;
											return;
										}
									}
								}else{ */
							
									if (inputTablaFechasProcedimiento[i].id == "fechaProcedimiento12"){
										var fechaFormalizacion = $("#fechaProcedimiento12").val().split("/");
										var dateFormalizacion =  new Date(fechaFormalizacion[2],fechaFormalizacion[1],fechaFormalizacion[0]);
										if($("#esServicio").val()==1){
											if($("#cboCategoria").val()!=12){
												var fecha1=$("#fechaProcedimiento2").val();
												var fecha = $("#fechaProcedimiento2").val().split("/");
												/////
												var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
												if(days_between(dateFallo, dateFormalizacion) > 15){
													alert("La fecha de Formalización no debe pasar los 15 dias a partir del fallo");
													banFechasProcedimiento = false;
													return;
												}
												if(!compare_dates( $("#fechaProcedimiento12").val(), fecha1)){
													alert("Las fechas se deben introducir en orden cronologico");
													banFechasProcedimiento = false;
													return;
												}
											}
										}else{
											var fecha1=$("#fechaProcedimiento11").val();
											var fecha = $("#fechaProcedimiento11").val().split("/");
											/////
											var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
											
											if(days_between(dateFallo, dateFormalizacion) > 15){
													alert("La fecha de Formalización no debe pasar los 15 dias a partir del fallo");
													banFechasProcedimiento = false;
													return;
											}
											if(!compare_dates( $("#fechaProcedimiento12").val(), fecha1)){
												alert("Las fechas se deben introducir en orden cronologico");
												banFechasProcedimiento = false;
												return;
											}
										}
									}else{
										if (inputTablaFechasProcedimiento[i].id == "fechaProcedimiento13"){
										// no es de servicios y es convenios
										if($("#esServicio").val()==0 && $("#cboCategoria").val()!=12){
											var fecha = $("#fechaProcedimiento11").val().split("/");
											var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
											
											
											var fechaEntrega=$("#fechaProcedimiento13").val().split("/");
											var dateEntrega= new Date(fechaEntrega[2],fechaEntrega[1],fechaEntrega[0]);
											
											
											if(days_between(dateFallo, dateEntrega) < 1){
												alert("La fecha de Entrega deberá ser a partir del siguiente dia de la fecha de fallo");
												banFechasProcedimiento = false;
												return;
											}
										}
	
										}else{
											if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
												alert("Las fechas se deben introducir en orden cronologico");
												banFechasProcedimiento = false;
												return;
											}
										}
										
									}
									
							//	}
								if (inputTablaFechasProcedimiento[i].id != "fechaProcedimiento12"){
									fechaTem = inputTablaFechasProcedimiento[i].value;
								}
								
							}else{
								fechaTem = inputTablaFechasProcedimiento[i].value;
							}			
						}
					}
				}
				
				if(banFechasProcedimiento){
				 	// validar el tipo de proceso
				 	// $("#ctipoProceso").val($('[name="tipoProcesoP"]:checked').val());
				 	$("#ctipoProceso").val($("#tipoProcesoProcedimiento").val());
					var des = $('#cboConsolidado');
					allFields = $( []).add(des),
					tips = $(".validateTips");
					var bValid = true;
			        tips.text("");
			        allFields.removeClass( "ui-state-error" );
					bValid = bValid&& checkRequerido(des, "Consolidado");
					if(!bValid){return;}
					try{
						//Obtener el siguiente consecutivo del procedimiento y asignarlo al hidden ConsecutivoProcedimiento
						queryFormPost("fn_ConsecutivoProcedimientoRead",{async : false});
						//Procedimiento que inserta en la tabla mProcedimiento
						
						queryFormPost("sp_ProcedimientoCreate",{async : false});			
						//Insertar requisitos al procedimiento cuando se cumplan las condiciones
						$("#cIdProcedimientoRequisitos").val($("#cboTipoProcedimiento").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#ConsecutivoProcedimiento").val());
						$("#cProcedimientoCumple").val(true);
						var cadena_campos = $("#cboConsolidado").val().split('|');
						$("#cIdConsolidadoRequisitos").val(cadena_campos[0] + '-' + $("#cIdUnidadEjecutora").val() + '-' + cadena_campos[1]);
						if ($("#cboTipoProcedimiento").val() == 'PS' || $("#cboTipoProcedimiento").val() == 'PN') {
							$("#cProcedimientoCumple").val(false);
							queryFormPost("mRequisitoProcedimientoCreate",{async : false});
						}
						queryFormPost("mProcedimientoCumpleRequisitosUpdate",{async : false});
						//Insertar Fechas del procedimiento
						for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
							if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
								if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
									if(inputTablaFechasProcedimiento[i].value != ""){
										$("#nIdFechaProcedimiento").val(inputTablaFechasProcedimiento[i].id.replace("fechaProcedimiento",""));
										$("#nFechaProcedimiento").val(inputTablaFechasProcedimiento[i].value);
										queryFormPost("agregaFechasProcedimiento",{async : false});
										inputTablaFechasProcedimiento[i].value = "";
									}
								}
							}
						}
						$("#tipoProcedimiento").val($("#cboTipoProcedimiento").val());
	                	$("#estadoProcedimiento").val('CAPTURADO ');		
	                	
	                	muestraInformacionCaratula();	
					     /* //query para llenar la informacion de la caratula.
					    queryFormPost("llenaCaratulaProcedimiento",{async:false});
					    
					    if($("#tipoProcedimiento").val()=='PS'){
					    	$("#esServicio").val(1);
					    }else{
					    	$("#esServicio").val(0);
					    }
					    queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false}); */
					    queryFormPost("llenaCaratulaProcedimientoProveedor",{async:false});
					    queryInnerDivPost("llenaFechasProcedimientoProveedor",{async:false});
					    queryFormPost("llenaCaratulaProcedimientoCotizacion",{async:false});
					    queryInnerDivPost("llenaFechasProcedimientoCotizacion",{async:false});
					    queryInnerDivPost("llenaFechasProcedimientoArchivos",{async:false});
					    queryFormPost("llenaCaratulaProcedimientoArchivos",{async:false});
					   	
					    queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
					    queryFormPost("llenaUnidadEjecutoraProcedimientoProovedor",{async:false});
					    queryFormPost("llenaUnidadEjecutoraProcedimientoCotizacion",{async:false});
					    queryFormPost("llenaUnidadEjecutoraProcedimientoArchivos",{async:false});
					   
					    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
					    
					    
					    queryFormPost("llenaCaratulaProcedimientoPreguntas", {async:false});
						queryFormPost("llenaUnidadEjecutoraPreguntas", {async:false});
						queryFormPost("llenaImagenEstadoProcedimientoPreguntas", {async:false});
						queryFormPost("llenaEstadoImagenProcedimientoPreguntas", {async:false});
						///informacion mostrada en la pestana de evaluacion
						queryFormPost("llenaCaratulaProcedimientoEvaluacion", {async:false});
						queryFormPost("llenaUnidadEjecutoraEvaluacion", {async:false});
					    queryFormPost("llenaImagenEstadoProcedimientoEvaluacion", {async:false});
					    queryFormPost("llenaEstadoImagenProcedimientoEvaluacion", {async:false});	
					    queryFormPost("llenaEstadoImagenProcedimientoArchivos", {async:false});	    
					    var imagen=$("#imagenEstadoProcedimiento").val();
					    imagen=imagen.substring(11,56);
					    var ima=document.getElementById("imgEstado");
					    var ima1=document.getElementById("imgEstadoProovedor");
					    var ima2=document.getElementById("imgEstadoCotizacion");
					    ima.src=imagen;
					    ima1.src=imagen;
					    ima2.src=imagen;
					    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});
					    queryFormPost("llenaEstadoImagenProcedimientoProveedor",{async:false});
					    queryFormPost("llenaEstadoImagenProcedimientoCotizacion",{async:false});
						

				   queryFormPost("llenaCaratulaProcedimientoRequisitos",{async:false});
				   queryFormPost("llenaUnidadEjecutoraProcedimientoRequisitos",{async:false});
				   var imaRequisitos = document.getElementById("imgEstadoRequisitos");
				   imaRequisitos.src = imagen;
				   queryFormPost("llenaEstadoImagenProcedimientoRequisitos",{async:false});


					    
						var proveedor='<%=proveedor%>';
						
				 		if (proveedor==0){
							$("#ProveedoresProcedimiento").attr("disabled", false);
							 $("#nPorcentajeIVAProveedor").val($("#nPorcentajeIVA").val());
							
							
							
							
							
							
							$("#ProveedoresProcedimiento").click();
						}
						var documentos = '<%=documentos%>';
						if (documentos==0){
							$("#DocumentosProcedimiento").attr("disabled", false);
						}
						    //$("#ProveedoresProcedimiento").click();
						var caratula='<%=caratula%>';
						if (caratula==0){
					    		$("#CaratulaProcedimiento").attr("disabled", false);	
						}
						//	$("#CaratulaProcedimiento").attr("disabled", false);
						var cotizacion='<%=cotizacion%>';
						
						if (cotizacion==0){
							if ($("#ctipoProceso").val()==1){
								if ($("#cProcedimientoCumple").val() == true) {
									$("#CotizacionProcedimiento").attr("disabled", false);
								}
								$("#CotizacionProcedimiento").css("display", "block");
								//$("#CotizacionesProcedimiento").attr("disabled", true);
								
								$("#PreguntasProcedimiento").attr("disabled", false);
								$("#PreguntasProcedimiento").css("display", "none");
								
								$("#EvaluacionProcedimiento").css("display", "none");
								$("#EvaluacionProcedimiento").attr("disabled", true);

								$("#ArchivosProcedimiento").css("display", "none");
								$("#ArchivosProcedimiento").attr("disabled", true);
							}
						}
						var Preguntas='<%=preguntas%>';
						if (Preguntas==0){
							if ($("#ctipoProceso").val()==2){
								$("#PreguntasProcedimiento").attr("disabled", false);
								//$("#CotizacionProcedimiento").attr("disabled", true);
								$("#CotizacionProcedimiento").css("display", "none");
								$("#PreguntasProcedimiento").css("display", "block");
							}
						}
						var Evaluacion='<%=evaluacion%>';
						if (Evaluacion==0){
							if ($("#ctipoProceso").val()==2){
								if ($("#cProcedimientoCumple").val() == true) {
									$("#EvaluacionProcedimiento").attr("disabled", false);
								}
								//$("#CotizacionProcedimiento").attr("disabled", true);
								$("#CotizacionProcedimiento").css("display", "none");
								$("#EvaluacionProcedimiento").css("display", "block");
							}
						}

						var Archivo='<%=archivo%>';
						if (Archivo==0){
							if ($("#ctipoProceso").val()==2){
								$("#ArchivosProcedimiento").attr("disabled", false);
								$("#ArchivosProcedimiento").css("display", "block");
							}
						}
						
						if ($("#cProcedimientoCumple").val() == false) {
							mostrarRequisitos();
							$("#RequisitosProcedimiento").attr("disabled", false);
							var user='<%=name_user%>';
						  if ($("#cIdUsuarioCreacion").val().toUpperCase() == user.toUpperCase()) {
						//	if ($("#cIdUsuarioCreacion").val() == '<%=name_user%>') {
								$("#tblRequisitos").attr("disabled", false);
								$("#btnGuardarRequisitosProcedimiento").attr("disabled", false);
							}
							else {
								$("#tblRequisitos").attr("disabled", true);
								$("#btnGuardarRequisitosProcedimiento").attr("disabled", true);
							}
						}
						
												
						//Paso los valores al div de caratula
						$("#descripcionCaratula").val($('#descripcion').text());
						$("#numero_externoCaratula").val($('#numero_externo').val());
						$("#cboCategoriaCaratula").val($('#cboCategoria').val());
						$("#tipoProcesoCaratula").val($('#ctipoProceso').val());
						//Limpio los campos
						$("#numero_externo").val('');
						$("#descripcion").val('');
						
					}catch(e){
						alert("No se pudo insertar el registro...");
					}
				}
		});
		
		$("#btnBuscarConsultaProcedimiento" ).button().click(function() {
			   $("#btnGuardarCotizacionProc").css("visibility","hidden");
               $("#btnPartidaCotizacionProc").css("visibility","hidden");
				mostrarTablaProcedimientos();
					//window.location.reload();
		});
		$( "#btnGuardarCaratulaProcedimiento" ).button().click(function() {
			if(validaModificarProcedimiento()){

				var inputTablaFechasProcedimiento = $('input','#tablaFechasProcedimientoCaratula');
				var banFechasProcedimiento = true;
				var fechaTem="";
				for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimientoC") >= 0){
							if(inputTablaFechasProcedimiento[i].value == ""){
								alert("Falta insertar algunas fechas.");
								banFechasProcedimiento = false;
								return;
							}
							else{
								if(!validaFormatoFecha(inputTablaFechasProcedimiento[i].value)){
									alert("Alguna de las fechas no cumple con el formato requerido (dd/mm/yyyy o yyyy/mm/dd).");
									banFechasProcedimiento = false;
									return;
								}
							}
						}
					}
				}
			/* 	for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimientoC") >= 0){
							if(fechaTem != ""){
								if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
									alert("Las fechas se deben introducir en orden cronologico.");
									banFechasProcedimiento = false;
									return;
								}
								fechaTem = inputTablaFechasProcedimiento[i].value;
							}
							else{
								fechaTem = inputTablaFechasProcedimiento[i].value;
							}			
						}
					}
				}
				 */
				 
				/*  for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimientoC") >= 0){
							if(fechaTem != ""){
									if (inputTablaFechasProcedimiento[i].id == "fechaProcedimientoC8"){
										var fecha = $("#fechaProcedimientoC7").val().split("/");
										var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
									//	date.setDate(date.getDate() + 16);
										var fechaFormalizacion = $("#fechaProcedimientoC8").val().split("/");
										var dateFormalizacion =  new Date(fechaFormalizacion[2],fechaFormalizacion[1],fechaFormalizacion[0]);
										
										
										if(days_between(dateFallo, dateFormalizacion) > 15){
											alert("La fecha de Formalización no debe pasar los 15 dias a partir del fallo");
											banFechasProcedimiento = false;
											return;
										}
									}else{
										if (inputTablaFechasProcedimiento[i].id == "fechaProcedimientoC9"){
										
											var fecha = $("#fechaProcedimientoC7").val().split("/");
											var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
											
											
											var fechaEntrega=$("#fechaProcedimientoC9").val().split("/");
											var dateEntrega= new Date(fechaEntrega[2],fechaEntrega[1],fechaEntrega[0]);
											
											
											if(days_between(dateFallo, dateEntrega) < 1){
												alert("La fecha de Entrega deberá ser a partir del siguiente dia de la fecha de fallo");
												banFechasProcedimiento = false;
												return;
											}
											
											
										}else{
											if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
												alert("Las fechas se deben introducir en orden cronologico");
												banFechasProcedimiento = false;
												return;
											}
										}
										
									}
									
								
								if (inputTablaFechasProcedimiento[i].id != "fechaProcedimientoC8"){
									fechaTem = inputTablaFechasProcedimiento[i].value;
								}
								
							}else{
								fechaTem = inputTablaFechasProcedimiento[i].value;
							}			
						}
					}
				} */
				for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
					if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
						if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimientoC") >= 0){
							if(fechaTem != ""){
									if (inputTablaFechasProcedimiento[i].id == "fechaProcedimientoC12"){
										var fechaFormalizacion = $("#fechaProcedimientoC12").val().split("/");
										var dateFormalizacion =  new Date(fechaFormalizacion[2],fechaFormalizacion[1],fechaFormalizacion[0]);
										if($("#esServicio").val()==1){
											if($("#cboCategoriaCaratula").val()!=12){
												var fecha1=$("#fechaProcedimientoC2").val();
												var fecha = $("#fechaProcedimientoC2").val().split("/");
												/////
												var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
												if(days_between(dateFallo, dateFormalizacion) > 15){
													alert("La fecha de Formalización no debe pasar los 15 dias a partir del fallo");
													banFechasProcedimiento = false;
													return;
												}
												if(!compare_dates( $("#fechaProcedimientoC12").val(), fecha1)){
													alert("Las fechas se deben introducir en orden cronologico");
													banFechasProcedimiento = false;
													return;
												}
											}
										}else{
											var fecha1=$("#fechaProcedimientoC11").val();
											var fecha = $("#fechaProcedimientoC11").val().split("/");
											/////
											var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
											
											if(days_between(dateFallo, dateFormalizacion) > 15){
													alert("La fecha de Formalización no debe pasar los 15 dias a partir del fallo");
													banFechasProcedimiento = false;
													return;
											}
											if(!compare_dates( $("#fechaProcedimientoC12").val(), fecha1)){
												alert("Las fechas se deben introducir en orden cronologico");
												banFechasProcedimiento = false;
												return;
											}
										}
									}else{
										if (inputTablaFechasProcedimiento[i].id == "fechaProcedimientoC13"){
											// no es de servicios y no es convenios
											if($("#esServicio").val()==0 && $("#cboCategoriaCaratula").val()!=12){
												var fecha = $("#fechaProcedimientoC11").val().split("/");
												var dateFallo =  new Date(fecha[2],fecha[1],fecha[0]);
												
												
												var fechaEntrega=$("#fechaProcedimientoC13").val().split("/");
												var dateEntrega= new Date(fechaEntrega[2],fechaEntrega[1],fechaEntrega[0]);
												
												
												if(days_between(dateFallo, dateEntrega) < 1){
													alert("La fecha de Entrega deberá ser a partir del siguiente dia de la fecha de fallo");
													banFechasProcedimiento = false;
													return;
												}
											}
	
										}else{
											if(!compare_dates(inputTablaFechasProcedimiento[i].value, fechaTem)){
												alert("Las fechas se deben introducir en orden cronologico");
												banFechasProcedimiento = false;
												return;
											}
										}
										
									}
									
							//	}
								if (inputTablaFechasProcedimiento[i].id != "fechaProcedimientoC12"){
									fechaTem = inputTablaFechasProcedimiento[i].value;
								}
								
							}else{
								fechaTem = inputTablaFechasProcedimiento[i].value;
							}			
						}
					}
				}
				
				var ivaaux=$("#ivaProcedimiento").val();
				//Procedimiento que actualiza en la tabla mProcedimiento
				
				//if((parseInt($("#cboCategoriaCaratula").val()) > 4 && $("#chkActivo").is(':checked')) || parseInt($("#cboCategoriaCaratula").val()) <= 4 ){

					queryFormPost("sp_ProcedimientoUpdate",{async : false});
					ponValorCheckBox();
					
				//}
				//else{
					//queryFormPost("sp_ProcedimientoUpdate",{async : false});
				//alert("No Cuenta con Documentos o Autorización del Comité");
				//}
				
				//muestra la tabla de proovedores en la seccion de cotizaciones
				agregaTablaProveedoresCotizaciones();
				leeProveedoresPreguntas();
				var cotizacion='<%=cotizacion%>';
				
				if (cotizacion==0){
					if ($("#tipoProcesoCaratula").val()==1){
						$("#CotizacionProcedimiento").css("display", "block");
					//	$("#PreguntasProcedimiento").css("display", "none"); 
						$("#PreguntasProcedimiento").attr("disabled", false);
						$("#PreguntasProcedimiento").css("display", "none");
						$("#EvaluacionProcedimiento").css("display", "none");
						$("#EvaluacionProcedimiento").attr("disabled", true);
						$("#ArchivosProcedimiento").css("display", "none");
						$("#ArchivosProcedimiento").attr("disabled", true);
					}
				}
				var Preguntas='<%=preguntas%>';
				if (Preguntas==0){
					if ($("#tipoProcesoCaratula").val()==2){
						$("#CotizacionProcedimiento").css("display", "none");
						$("#PreguntasProcedimiento").css("display", "block");
					}
				}
				
				var Evaluacion='<%=evaluacion%>';
				if (Evaluacion==0){
					if ($("#tipoProcesoCaratula").val()==2){
						$("#CotizacionProcedimiento").css("display", "none");
						$("#EvaluacionProcedimiento").css("display", "block");
					}
				}

				var Archivo='<%=archivo%>';
				if (Evaluacion==0){
					if ($("#tipoProcesoCaratula").val()==2){
						$("#ArchivosProcedimiento").css("display", "block");
					}
				}
				var proveedor='<%=proveedor%>';
				if (proveedor==0){
					$("#ProveedoresProcedimiento").attr("disabled", false);
				}
				$("#tblPartidasCotizacion").css("visibility","hidden");
				$("#tblProvCotizacion").css("visibility","hidden");
				queryFormPost("existeProveedorProcedimiento",{async : false});
				
				if ($("#existeProv").val()==0){// no hay proveedores asignados
					$("#ivaProcedimiento").val(ivaaux);
				}else{//existen proveedores
					queryFormPost("actualizaIvaProcedimiento",{async : false});
				}
				 muestraInformacionCaratula();
				/* 	//query para llenar la informacion de la caratula.
				   queryFormPost("llenaCaratulaProcedimiento",{async:false});
				   queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false}); */
				   
				   
				   queryFormPost("llenaCaratulaProcedimientoProveedor",{async:false});
				   queryInnerDivPost("llenaFechasProcedimientoProveedor",{async:false});
				   queryFormPost("llenaCaratulaProcedimientoCotizacion",{async:false});
				   queryInnerDivPost("llenaFechasProcedimientoCotizacion",{async:false});
				   queryInnerDivPost("llenaFechasProcedimientoArchivos",{async:false});
				   queryFormPost("llenaCaratulaProcedimientoArchivos",{async:false});
				   
				   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
				   queryFormPost("llenaUnidadEjecutoraProcedimientoProovedor",{async:false});
				   queryFormPost("llenaUnidadEjecutoraProcedimientoCotizacion",{async:false});
				   queryFormPost("llenaUnidadEjecutoraProcedimientoArchivos",{async:false});
				   
				   // llena pestana evaluacion y preguntas
				   queryFormPost("llenaCaratulaProcedimientoPreguntas", {async:false});
				   queryFormPost("llenaUnidadEjecutoraPreguntas", {async:false});
				   queryFormPost("llenaImagenEstadoProcedimientoPreguntas", {async:false});
				   queryFormPost("llenaEstadoImagenProcedimientoPreguntas", {async:false});
				   
				   ///informacion mostrada en la pestana de evaluacion
				   queryFormPost("llenaCaratulaProcedimientoEvaluacion", {async:false});
				   queryFormPost("llenaUnidadEjecutoraEvaluacion", {async:false});
				   queryFormPost("llenaImagenEstadoProcedimientoEvaluacion", {async:false});
				   queryFormPost("llenaEstadoImagenProcedimientoEvaluacion", {async:false});
				   queryFormPost("llenaEstadoImagenProcedimientoArchivos", {async:false});

					queryFormPost("llenaCaratulaProcedimientoRequisitos",{async:false});
				   queryFormPost("llenaUnidadEjecutoraProcedimientoRequisitos",{async:false});
				   //Insertar Fechas del procedimiento
				 queryFormPost("deleteFechasProcedimiento",{async : false});
				
					for(var i=0;i<inputTablaFechasProcedimiento.length;i++){
						if(inputTablaFechasProcedimiento[i].type == "text" || inputTablaFechasProcedimiento[i].type == "TEXT"){
							if(inputTablaFechasProcedimiento[i].id.indexOf("fechaProcedimiento") >= 0){
								if(inputTablaFechasProcedimiento[i].value != ""){
									$("#nIdFechaProcedimiento").val(inputTablaFechasProcedimiento[i].id.replace("fechaProcedimientoC",""));
									$("#nFechaProcedimiento").val(inputTablaFechasProcedimiento[i].value);
									queryFormPost("agregaFechasProcedimiento",{async : false});
									inputTablaFechasProcedimiento[i].value = "";
								}
							}
						}
					}

					queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false});
					queryInnerDivPost("llenaFechasProcedimientoProveedor",{async:false});
					queryInnerDivPost("llenaFechasProcedimientoCotizacion",{async:false});
					queryInnerDivPost("llenaFechasProcedimientoArchivos",{async:false});
					
				$("#btnBuscarConsultaProcedimiento").click();
				if ($("#existeProv").val()==0){// no hay proveedores asignados
					$("#ivaProcedimiento").val(ivaaux);
				}
				}
		});
		
		$( "#btnBuscarProveedorProcedimiento" ).button().click(function() {
					//alert('Buscar proveedor');
					mostrarTablaProveedores();
					$("#aTab4").attr("disabled", false);
        	        //$("#btnBuscarProveedorProcedimiento").click();	
				
		});
		
		$("#regresarProveedor" ).button().click(function() {
			if(validaModificarProcedimiento()){
				var aTrs = $('#tblProveedoresAgregados').dataTable().fnGetNodes();           
				for ( var i=aTrs.length ; i>=0; i-- ){         
					if ( $(aTrs[i]).hasClass('gradeA') ){             
						var nTr = $('#tblProveedoresAgregados').dataTable().fnGetData(aTrs[i]);
						//Obtengo el RFC
						$("#IdRFCProveedor").val(nTr[0]);
						//nTr[0] = nTr[0].replace("name","nombre");
						queryFormPost("sp_AdjudicacionDelete",{async : false});
						//$('#tblProveedoresDisponibles').dataTable().fnAddData( nTr );
						$('#tblProveedoresAgregados').dataTable().fnDeleteRow( i ); 
					}     
				}
			} 
		});
		
		
		
		
		$( "#btnGuardarCotizacionProc" ).button().click(function() {
				if(validaModificarProcedimiento()){
					if(parseFloat( $("#porcentajeIVACotizacion").val()) > 100  || $("#porcentajeIVACotizacion").val()< 0 ){
				                alert("El porcentaje de IVA no puede ser mayor a 100 ni puede ser negativo");
				                return;
				    }
				    if(parseFloat( $("#valorTipoCambio").val()) < 0 ){
				                alert("El tipo de cambio no puede ser negativo");
				                return;
				    }       
				    //verifica si hay valores menores o iguales a cero
				   
				    
				    var aTrs;   
				    if(document.getElementById("chk_pedidoAbierto").checked){
				    	aTrs = $('#tblProovedoresCotizacion2').dataTable().fnGetNodes();
					}
				    else{
				    	aTrs = $('#tblProovedoresCotizacion').dataTable().fnGetNodes();
					}
		            
					 for(var i=0;i<aTrs.length;i++){
						 aData = oTable2.fnGetData(aTrs[i]);
						 var valida;
						 var ind;
						 
						 if(document.getElementById("chk_pedidoAbierto").checked){
						     //valida=validaNegativos(aData[10]);
							 ind=parseInt(aData[10],10);
						 }
						 else{
							 valida=validaNegativos(aData[9]);
							  ind=parseInt(aData[9],10);
						 }
						 
						 if(valida=="1"){
						   alert("No puede ingresar valores menores o iguales a cero en la partida " + (ind+1));
						   return ;
						 }
					}	      
				
				if(	$("#cboCambioCotizacion").val()!=1){
					if($("#valorTipoCambio").val()==""){
						alert("Ingrese el valor del tipo de cambio")
						return;
					}
					$("#valorTipoCambio").val();
				}else{
					$("#valorTipoCambio").val(1)
				}

				if(document.getElementById("chk_pedidoAbierto").checked){
					$("#IContratoAbiertoProveedor").val('TRUE');
				}
				else{
					$("#IContratoAbiertoProveedor").val('FALSE');
				}
							
				$("#porcentajeIVACotizacion").val();
				$("#rfcCotizacion").val();
				//Mando llamar el procedimiento que me inserta los campos en la tabla mProcedimientoAdjudicacion
				queryFormPost("sp_AdjudicacionCreate1",{async : false});
				//refresca valores del lineas
				var aTrs; 
				if(document.getElementById("chk_pedidoAbierto").checked){
					aTrs = $('#tblProovedoresCotizacion2').dataTable().fnGetNodes(); 
				}
				else{
					aTrs = $('#tblProovedoresCotizacion').dataTable().fnGetNodes(); 
				}
				
				for(var i=0;i<aTrs.length;i++){
					aData = oTable2.fnGetData(aTrs[i]);
					if(document.getElementById("chk_pedidoAbierto").checked){
						$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+aData[10]+"").val());
	                    $("#montoMinimo").val($("#mMontoMinimo_"+aData[10]+"").val());
	  					$("#montoMaximo").val($("#mMontoMaximo_"+aData[10]+"").val());
	                    $("#descripcionPartida").val($("#cDescripcion_"+aData[10]+"").val());
					}
					else{
						$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+aData[9]+"").val());
	                    $("#montoMinimo").val($("#mMontoMaximoUnitario_"+aData[9]+"").val());
	  					$("#montoMaximo").val($("#mMontoMaximoUnitario_"+aData[9]+"").val());
	                    $("#descripcionPartida").val($("#cDescripcion_"+aData[9]+"").val());
					}
					
					var minimo=quitaFmt($("#montoMinimo").val());
					var maximo=quitaFmt($("#montoMaximo").val());
										
					if(document.getElementById("chk_pedidoAbierto").checked){
																
					//  if(parseFloat($("#montoMinimo").val().toString().replace("$","").replace(",",""))){
					if(parseFloat(minimo)> 0){
                        //if(parseFloat($("#montoMinimo").val().toString().replace("$","").replace(",","")) <= parseFloat($("#montoMaximo").val().toString().replace("$","").replace(",",""))){
                         if(parseFloat(minimo) == parseFloat(maximo)){
                           alert("El monto m\xE1ximo debe ser mayor que el monto m\xEDnimo.");
							return;
                         
                         }
                       
                        
                        if(parseFloat(minimo) < parseFloat(maximo)){
                        
                        	//procedimiento almacenado para agregar cucops al programa anual
        					queryFormPost("spAgregaLineaPartida", {async: false });       
                        }else{
							alert("El monto m\xEDnimo debe ser menor que el monto m\xE1ximo.");
							return;
                        }
                        
                        
                     }else{
						alert("El monto m\xEDnimo debe ser mayor a 0.");
						return;
                     }
                     
                     
					}
					else{
						 //if(parseFloat($("#montoMinimo").val().toString().replace("$","").replace(",",""))){
						 if(parseFloat(minimo)> 0){
							 queryFormPost("spAgregaLineaPartida", {async: false }); 
						 }else{
						alert("El monto m\xEDnimo debe ser mayor a 0.");
						return;
                     }
					}
              
				}	
				 //lenamos rfc y razon social
				queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
				//llena montos bruto y neto
				queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
				queryFormPost("llenaMontoNetoCotizaciones",{async:false});
				// llena iva y tipo de cambio
				queryFormPost("llenaIvaCotizaciones",{async:false});
				queryFormPost("tipodeCambioProcedimiento",{async:false});
                if($("#idTipoCambio").val()!="01"){
                   $("#cboCambioCotizacion").val($("#idTipoCambio").val());
                   $("#valorTipoCambio").css("visibility","visible");
                   $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
                }else{
                   $("#cboCambioCotizacion").val($("#idTipoCambio").val()); 
                   $("#valorTipoCambio").css("visibility","hidden");
                   $("#valorTipoCambio").val($("#valorTipoCambio1").val());
                }

                $("#valorTipoCambio").formatCurrency();
				$("#montoBrutoCotizacion").formatCurrency();
				$("#montoNetoCotizacion").formatCurrency();
							    
				var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'"; 
				var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
				query += "  AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
				//se borra la tabla antes de realizar la consulta
				
				muestraPartidasElegidas(query,func);
				
				}	
		});
		
		$( "#btnBorraPartidas").button().click(function() {
			if(validaModificarProcedimiento()){
				if($("#estadoProcedimiento").val()=="ADJUDICADO")
	        		return;	
			if (confirm("¿Está seguro que desea eliminar las líneas? Una vez confirmado, no podrá deshacer los cambios.")) {
				var aTrs;
				if(document.getElementById("chk_pedidoAbierto").checked){
					aTrs = $('#tblProovedoresCotizacion2').dataTable().fnGetNodes();
				} 
				else{
					aTrs = $('#tblProovedoresCotizacion').dataTable().fnGetNodes();
				}
				for(var i=0;i<aTrs.length;i++){
					aData = oTable2.fnGetData(aTrs[i]);
					if(document.getElementById("chk_pedidoAbierto").checked){
						$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+aData[10]+"").val());
					}
					else{
						$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+aData[9]+"").val());
					}
			 		queryFormPost("deletePartidasProcedimiento", {async:false});
				}
			//se actualiza tabla de partidas
					   
                            //lenamos rfc y razon social
							queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
							//llena montos bruto y neto
							queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
							queryFormPost("llenaMontoNetoCotizaciones",{async:false});
				            // llena iva y tipo de cambio
				             queryFormPost("llenaIvaCotizaciones",{async:false}); 
				             
			                          queryFormPost("tipodeCambioProcedimiento",{async:false});
			                            if($("#idTipoCambio").val()!="01"){
			                            $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			                            $("#valorTipoCambio").css("visibility","visible");
			                           $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
			                            
			                            }else{
			                                  $("#cboCambioCotizacion").val($("#idTipoCambio").val()); 
			                                 $("#valorTipoCambio").css("visibility","hidden");
			                                 $("#valorTipoCambio").val($("#valorTipoCambio1").val());
			                            }
			                           $("#valorTipoCambio").formatCurrency();
							               
										$("#montoBrutoCotizacion").formatCurrency();
										$("#montoNetoCotizacion").formatCurrency();
							
				var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'"; 
				var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
				 query += "  AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
				             //se borra la tabla antes de realizar la consulta
				muestraPartidasElegidas(query,func);			
                $("#btnPartidaCotizacionProc").click();
			}
			else{
			return;
			}
			
			}
		});
		
		
		///boton que borra todos los proveedores en cotizaciones
		
		$( "#borraTodoProveedoresCotizacion").button().click(function() {
				
				if(validaModificarProcedimiento()){
				
				
			if($("#estadoProcedimiento").val()=="ADJUDICADO")
	       	 return;	
				
			if (confirm("¿Está seguro que desea eliminar todos los proveedores? Una vez confirmado, no podrá deshacer los cambios.")) {
				for (var i = 0; i < nEditaRegistrosProveedor; i++) {
					$("#cidRFCOculto1").val($("#nIdRFCProveedor_" + i).val()); 
					$("#nIdProcedimiento").val($("#tipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#ConsecutivoProcedimiento").val());
					$("#cIdRfcProveedorDocumento").val($("#cidRFCOculto1").val());    
	                queryFormPost("deleteProveedorProcedimiento", {async:false});
	                queryFormPost("deleteDocumentosProveedor",{async:false});
				}
			  agregaTablaProveedoresCotizaciones();
			  agregaTablaProveedoresPreguntas();
			 //se refresca tabla de proveedores
			  $("#btnBuscarProveedorProcedimiento").click();
			  $("#tblProvCotizacion").css("visibility","hidden");
		      $("#tblPartidasCotizacion").css("visibility","hidden");
		      $("#formProveedores").css("visibility","hidden");
		      $("#btnGuardarCotizacionProc").css("visibility","hidden");
		      $("#btnPartidaCotizacionProc").css("visibility","hidden");
    							
			}else{
			return;
			}
			
			}
		});
		
		$("#btnGuardarRequisitosProcedimiento").button().click(function() {
			//alert($('#cIdProcedimientoRequisitos').val());
			var aTrs = $('#tblRequisitos').dataTable().fnGetNodes();	
			for ( var i = 0 ; i < aTrs.length; i++){ 	 	
				var nTr = $('#tblRequisitos').dataTable().fnGetData(i);	
     			var id = nTr[0];
				$("#cIdRequisito").val(id);
				if ($("#requisitoR" + id).is(':checked')) $("#cRequisitoRequerido").val('checked="checked"');
				else $("#cRequisitoRequerido").val('');
				if ($("#requisitoC" + id).is(':checked')) $("#cRequisitoCumple").val('checked="checked"');
				else $("#cRequisitoCumple").val('');
				$("#cRequisitoObs").val($("#requisitoO" + id).val());
				//alert($("#cIdRequisito").val() + '-' + $("#cRequisitoRequerido").val() + '-' + $("#cRequisitoCumple").val() + '-' + $("#cRequisitoObs").val());
				queryFormPost("mRequisitosProcedimientoUpdate", {async : false});
			}
			$("#cProcedimientoCumple").val(1);
			queryFormPost("mRequisitosProcedimientoCumpleRead", {async : false});
			if ($("#cProcedimientoCumple").val() == 1) {
				$("#cProcedimientoCumple").val(true);
				queryFormPost("mProcedimientoCumpleRequisitosUpdate", {async : false});
				if ($("#tipoProcesoProveedor").val() == 1) {
					$("#CotizacionProcedimiento").attr("disabled", false);
					alert("Requisitos guardados correctamente. Se cumplen todos los requeridos ahora puede realizar la cotización.");
				}
				else if ($("#tipoProcesoProveedor").val() == 2) {
					$("#EvaluacionProcedimiento").attr("disabled", false);
					alert("Requisitos guardados correctamente. Se cumplen todos los requeridos ahora puede realizar la evaluación.");
				}
			}
			else {
				$("#CotizacionProcedimiento").attr("disabled", true);
				$("#EvaluacionProcedimiento").attr("disabled", true);
				$("#cProcedimientoCumple").val(false);
				queryFormPost("mProcedimientoCumpleRequisitosUpdate", {async : false});
				if ($("#tipoProcesoProveedor").val() == 1) {
					alert("Requisitos guardados correctamente. Es necesario cumplir con todos los requeridos para poder realizar la cotización.");
				}
				else if ($("#tipoProcesoProveedor").val() == 2) {
					alert("Requisitos guardados correctamente. Es necesario cumplir con todos los requeridos para poder realizar la evaluación.");
				}
			}
		});
		
		$( "#btnPartidaCotizacionProc" ).button().click(function() {
			if(validaModificarProcedimiento()){
			$("#tblPartidasCotizacion").css("display","")
			var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'";
			var where="	nIdLineaConsolidado NOT IN( SELECT nIdLineaConsolidado FROM mProcedimientoAdjudicacionPartidas WHERE cEjercicio ='"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento ='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora ='"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo = '"+$("#ConsecutivoProcedimiento").val()+"' AND cIdTipoConsolidado ='"+$("#TipoConsolidado").val()+"' AND nIdConsecutivoConsolidado = '"+$("#ConsecutivoConsolidado").val()+"')";
			
			oTable3.dataTable().fnClearTable();
		
		    $("#tblPartidasCotizacion").css("visibility","visible");
		    
			//Mostramos la tabla de proveedores agregados
			$('#tblPartidas').dataTable({    
			"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
			"bFilter" : false,
			"bDestroy" : true,
			"bJQueryUI": true,
			"bAutoWidth" : false,
			sScrollY : "100%",
			sScrollX: "200%",
			sScrollXInner: "100%",
			"sPaginationType": "full_numbers",
			//"sScrollY": 250,
					"oLanguage": {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					//sLengthMenu: "<h2><b>PROVEEDORES AGREGADOS</b></h2><h5>Doble click sobre el proveedor a cotizar</h5>",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidas("+func+")&qw="+where,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaConsolidado" },
					{ sName: "cidCABM"   },
					{ sName: "cDescripcion"   },
					{ sName: "nCantidad"   }
				]
			});
			}
		});

		$( "#DocumentosProcedimiento").click(function() {//Buscar documentos
			mostrarTablaDocumentos();
		});
		
		//Se muestra el ombo de la unidad ejecutora en el div de consultar
		//querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutoraConsultar", {async: false });
		querySelectPost("UnidadEjecutoraReadFiltro", "cboUnidadEjecutoraConsultar", {async: false });
		//Tipo de procedimiento en el div de consultar
		querySelectPost("TipoProcedimientoConsultarRead", "cboTipoProcedimientoConsultar", {async: false });
		//Muestra los estados del procedimientos en el div de consultar
		querySelectPost("EstadoProcedimientoRead", "cboEstadoConsultar", {async: false });
		
		//querySelectPost("ConsolidadoRead", "cboConsolidado", {async : false});
		///////////AGREGAR FIRMANTES//
		$('#tblCatalogoFirmantes tr').live('click', function() {        
			if(validaModificarProcedimiento()){
			$(oTableCatalogo.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});						
			$(this).addClass('row_selected');   
			var anSelected = fnGetSelected( oTableCatalogo );						
			var aData = oTableCatalogo.fnGetData(anSelected[0]);					
			$("#nIdFirmante").val(aData[1]);
			queryFormPost("pa_mAgregaFirmanteComparativaCreate", {async: false});
			oTableFirmantes.fnClearTable(oTableFirmantes);
			firmantesTblComparativa();	
				}
		});	
		
		///////////QUITAR FIRMANTES//////////////
		$('#tblFirmantesTblComparativa tr').live('click', function(){
			if(validaModificarProcedimiento()){
				//Click handler para quitar un firmante, al momento de dar de baja un firmante se ejecuta un trigger para renumerar
					$(oTableFirmantes.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableFirmantes );
					var aData = oTableFirmantes.fnGetData(anSelected[0]);
					$("#nNumeroFirmante").val(aData[0]);		
					$("#nIdFirmante").val(aData[1]);	
					queryFormPost("firmanteComparativaDelete", {async: false});	
					queryFormPost("procedimientoComparativaFirmantesUpdate", {async:false});
					oTableFirmantes.fnClearTable(oTableFirmantes);	
					firmantesTblComparativa();
			}			
		});
});

		//----------TERMINA LA CARGA DEL DOCUMENTO
      
		var imgAdjudicarC='<%=imgAdjudicarC%>';
		var imgDesiertoC='<%=imgDesiertoC%>';
		var imgDevolverC='<%=imgDevolverC%>';
		var imgAdjudicarCP='<%=imgAdjudicarCP%>';
		var imgDesiertoCP='<%=imgDesiertoCP%>';
		var imgDevolverCP='<%=imgDevolverCP%>';
		var imgAdjudicarP='<%=imgAdjudicarP%>';
		var imgDesiertoP='<%=imgDesiertoP%>';
		var imgDevolverP='<%=imgDevolverP%>';
		
		/// evaluacion
		var imgAdjudicarE='<%=imgAdjudicarE%>';
		var imgDesiertoE='<%=imgDesiertoE%>';
		var imgDevolverE='<%=imgDevolverE%>';
		//// evaluacion
		///
		function validaBotones(boton){
			switch (boton){
			case "imgAdjudicarCaratulaProc":
				if (imgAdjudicarC==0){
	
					adjudicarProcedimiento();  
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDesiertoCaratulaProc":
				if (imgDesiertoC==0){
				
					desiertoProcedimiento(); 
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDevolverCaratulaProc":
				if (imgDevolverC==0){
				
					devolverProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgAdjudicarProveedoresProc":
				if (imgAdjudicarP==0){
					adjudicarProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDesiertoProveedoresProc":
				if (imgDesiertoP==0){
					desiertoProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDevolverProveedoresProc":
				if (imgDevolverP==0){
					devolverProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgAdjudicarCotizacionProc":
				if (imgAdjudicarC==0){
					adjudicarProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDesiertoCotizacionProc":
				if (imgDesiertoC==0){
					desiertoProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDevolverCotizacionProc":
				if (imgDevolverC==0){
					devolverProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			
			    ///EVALUACION   
			case "imgAdjudicarEvaluacionProc":
				if (imgAdjudicarE==0){
					adjudicarProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDesiertoEvaluacionProc":
				if (imgDesiertoE==0){
					desiertoProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			case "imgDevolverEvaluacionProc":
				if (imgDevolverE==0){
					devolverProcedimiento();
				}else{
					alert("No tiene permiso para esta accion");
				}
			break;
			}/// FIN SWITCH
		} 
		//------FUNCION--------En Combo consolidado de pestaña Nuevo que toma los consolidados aprobados de moduo-diana
		function muestraConsolidado(){
		
		        $("#cIdUnidadEjecutora").val($("#cboUnidadEjecutora").val());
				querySelectPost("ConsolidadoRead", "cboConsolidado", {async : false});
				//Colocar valor en los hiiden
				if($("#cboConsolidado").val()!=null){
				var cadena_campos=$("#cboConsolidado").val().split('|');
				$("#TipoConsolidado").val(cadena_campos[0]);
				$("#ConsecutivoConsolidado").val(cadena_campos[1]);
				$("#descripcion").val(cadena_campos[2]);
				// categoria de le requisicion
  				queryFormPost("obtieneCategoriaReq",{async:false});
  				$("#cboCategoria").val($("#nidcategoria").val());
				
				if(parseInt($("#nidcategoria").val(),10) <= 4)
					$("#etiquetaChkActivoNuevo").hide();
				else
					$("#etiquetaChkActivoNuevo").show();
		     }
		     else{
		     $("#descripcion").val(" ");
		     return;
		     }
		
		}

		//---------FUNCION-------------TABLA DE PROCEDIMIENTOS AGREGADOS----------------------
		var oTable;
		var oTableProveedoresDocumentos;
		var nEditaRegistros;
		var nEditaRegistrosProveedor;
		
		function mostrarTablaProcedimientos(){
        	//Mostramos la tabla de procedimientos
			$("#tblProcedimientos").css("display", ""); 
			$("#tblProcedimientos tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
					//$("#pbDesplegar").css("visibility","visible");
				});
			
			//Parametros de busqueda opcionales
			var consulta=" cEjercicio='"+$("#cEjercicio").val()+"'" ;
				if (($.trim($("#cboTipoProcedimientoConsultar").val()))!='0')
				{
					consulta += "AND cIdTipoProcedimiento LIKE '%25" + $.trim($("#cboTipoProcedimientoConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				
				if (($.trim($("#cboEstadoConsultar").val()))!=0)
				{
					consulta += "AND nIdEstado LIKE '%25" + $.trim($("#cboEstadoConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}

				if (($.trim($("#procedimientoConsultar").val()).length)>0)
				{
					consulta += "AND cIdProcedimiento LIKE '%25" + $.trim($("#procedimientoConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if(($.trim($("#numeroConsultar").val()).length)>0){
					consulta += " AND nIdConsecutivo LIKE '%25" + $.trim($("#numeroConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if(($.trim($("#descripcionConsultar").val()).length)>0){
					consulta += " AND cDescripcion LIKE '%25" + $.trim($("#descripcionConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if(($.trim($("#consolidadoConsultar").val()).length)>0){
					consulta += " AND cIdConsolidado LIKE '%25" + $.trim($("#consolidadoConsultar").val())+"%25'";
					consulta=consulta.replace("\&","%26");
				}
				if($("#tipoProcesoProcedimientoBuscar").val() != 0){
					consulta += " AND IdProceso = "+$("#tipoProcesoProcedimientoBuscar").val();
				}
							
				if($("#cboUnidadEjecutoraConsultar").val() != 0){
					consulta += " AND cIdUnidadEjecutora= '"+$("#cboUnidadEjecutoraConsultar").val()+"'";
				}
				
				
			consulta += " AND cIdTipoProcedimiento NOT IN (SELECT cIdTipoProcedimiento from mCatalogoTipoProcedimiento WHERE cIdTipoProcedimiento = 'PI' OR cIdTipoProcedimiento = 'PF') ";
			oTable = $('#tblProcedimientos').dataTable({
				"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
				"bFilter" : false,
				"bDestroy" : true,
				"bJQueryUI": true,
				"bAutoWidth" : true,
				sScrollY : "100%",
				sScrollX: "100%",
				sScrollXInner: "150%",
				"iDisplayLength": 5, //Cuantos registros se despliegan
				"sPaginationType": "full_numbers",
				"oLanguage": {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros <h5>Doble click para seleccionar procedimiento</h5>",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneProcedimientos&qw="+consulta,
					aaSorting: [[ 0, "asc" ],[ 1, "asc" ],[2,"asc"]] ,			
					aoColumns: [
						{ sName: "cIdTipoProcedimiento" },
						{ sName: "cIdUnidadEjecutora"   },
						{ sName: "nIdConsecutivo" },
						{ sName: "cDescripcion"	},
						{ sName: "cEstado"	},
						{ sName: "cCategoria"	},
						{ sName: "cIdConsolidado"	},
						{ sName: "cIdProcedimiento"	},
						{ sName: "tipoProceso"	},
						//Campos ocultos
						{ sName: "nIdCategoria",bVisible: false},
						{ sName: "cIdUsuarioCreacion",bVisible:false},
						{ sName: "nIdEstado",bVisible:false},
						{ sName: "cOficio",bVisible:false}
					]
				
			});
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
			var cIdProcedimiento =$("#tipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#ConsecutivoProcedimiento").val();
			
			//Tabla Proveedores Documentos
			oTableProveedoresDocumentos=$('#tblProveedoresDocumentos').dataTable(
			{
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
		
		//para agregar datos en la tabla de proovedores en la seccion de cotizacion
		function agregaTablaProveedoresCotizaciones(){
		
		
		var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
	   
	     $('#tblProvedoresCotizaciones').dataTable().fnClearTable();
	    
	         szTabla = "PROVEEDORESCOTIZACION";                                                                                         
				$.getJSON("../../catalogos/SelectJson.jsp?"+new Date(),{Tabla: szTabla, Param: query, MaxReg:"" , ajax: 'false'}, 
					function(j)
					{     
					nEditaRegistrosProveedor=j.length
					arrayCompleto=new Array();
						for (var i = 0; i < j.length; i++) 
    					{
    					   if(j[i].Col0!=$("#nIdRFCProveedor_"+i+"").val()){
    				       
	    					arrayCompleto [i]=[
							  "<input type='text' id='nIdRFCProveedor_"+i+"' name='nIdRFCProveedor_"+i+"' value='"+ j[i].Col0 +"' readonly style='width:120px; border-width:0; background-color:transparent'/>", 
							  "<input type='text' id='cIdRazonSocialProveedor_"+i+"' name='cIdRazonSocialProveedor_"+i+"' value='" + j[i].Col1 + "' readonly style='width:350px; border-width:0; background-color:transparent'/>",
							  "<button id='borraProveedor_" + i +"' name='borraProveedor_" + i + "' onclick='eliminaProveedor(" + i + ");'>Elimina</button>"
	                            
							]; 
							}	     
    					
						}
						$('#tblProvedoresCotizaciones').dataTable().fnAddData(arrayCompleto);
    					  
		         });   
		         		   
	    
		}
		
		////EVENTO DOBLECLICK EN LA TABLA DE PROCEDIMIENTOS DISPONIBLES
		
			//Al darle doble click a la tabla de porcedimientos
			$('#tblProcedimientos tr').live('dblclick', function() {
				//Actualizar las variables necesarias
				var aTrs = $('#tblProcedimientos').dataTable().fnGetNodes();           
				for ( var i=aTrs.length ; i>=0; i-- )     
				{         
						if ($(aTrs[i]).hasClass('gradeA'))         
						{   
		   					$("#btnGuardarCotizacionProc").css("display","none");
		   					$("#btnPartidaCotizacionProc").css("display","none");
		   					$("#tblProvCotizacion").css("display","none");
		   					$("#tblPartidasCotizacion").css("display","none");
		   					
							var nTr = $('#tblProcedimientos').dataTable().fnGetData(aTrs[i]);   
							//$('#tblProcedimientos').dataTable().fnAddData( nTr );
							$("#cboTipoProcedimiento").val(nTr[0]);
							$("#cboUnidadEjecutora").val(nTr[1]);
						    $("#cIdUnidadEjecutora").val($("#cboUnidadEjecutoraConsultar").val());
							$("#ConsecutivoProcedimiento").val(nTr[2]);
							
							var columConsolidado = nTr[6];
							
							$("#TipoConsolidado").val($.trim(columConsolidado).split("-")[0]);
							$("#ConsecutivoConsolidado").val($.trim(columConsolidado).split("-")[2]);
							//Descripcion del procedimiento
							$("#descripcionCaratula").val(nTr[3]);
							
							//Campo ocultos
							$("#cboCategoriaCaratula").val(nTr[9]);
							$("#cIdUsuarioCreacion").val(nTr[10]);
							$("#EstadoCaptura").val(nTr[11]);
							$("#numero_externoCaratula").val(nTr[12]);
							$("#tipoProcedimiento").val(nTr[0]);
							$("#estadoProcedimiento").val(nTr[4]);
							
							muestraInformacionCaratula();
						  /*  //query para llenar la informacion de la caratula.
						   queryFormPost("llenaCaratulaProcedimiento",{async:false});
						   alert("tipo de procedimiento "+$("#tipoProcedimiento").val());
						   if($("#tipoProcedimiento").val()=='PS'){
						   	 $("#esServicio").val(1);
						   }else{
						   	 $("#esServicio").val(0);
						   }
						   queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false}); */
						   queryFormPost("llenaCaratulaProcedimientoProveedor",{async:false});
						   queryInnerDivPost("llenaFechasProcedimientoProveedor",{async:false});
						   queryFormPost("llenaCaratulaProcedimientoCotizacion",{async:false});
						   queryInnerDivPost("llenaFechasProcedimientoCotizacion",{async:false});
						   queryInnerDivPost("llenaFechasProcedimientoArchivos",{async:false});
						   queryFormPost("llenaCaratulaProcedimientoArchivos",{async:false});
						   
						   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
						   queryFormPost("llenaUnidadEjecutoraProcedimientoProovedor",{async:false});
						   queryFormPost("llenaUnidadEjecutoraProcedimientoCotizacion",{async:false});
						   queryFormPost("llenaUnidadEjecutoraProcedimientoArchivos",{async:false});
						   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
						   queryFormPost("llenaCaratulaProcedimientoPreguntas", {async:false});
						   queryFormPost("llenaUnidadEjecutoraPreguntas", {async:false});
						   queryFormPost("llenaImagenEstadoProcedimientoPreguntas", {async:false});
						   queryFormPost("llenaEstadoImagenProcedimientoPreguntas", {async:false});
						   
						   ///informacion mostrada en la pestana de evaluacion
						   queryFormPost("llenaCaratulaProcedimientoEvaluacion", {async:false});
						   queryFormPost("llenaUnidadEjecutoraEvaluacion", {async:false});
						   queryFormPost("llenaImagenEstadoProcedimientoEvaluacion", {async:false});
						   queryFormPost("llenaEstadoImagenProcedimientoEvaluacion", {async:false});
						   queryFormPost("llenaEstadoImagenProcedimientoArchivos", {async:false});
						    var imagen=$("#imagenEstadoProcedimiento").val();
						    imagen=imagen.substring(11,56);
						    var ima=document.getElementById("imgEstado");
						    var ima1=document.getElementById("imgEstadoProovedor");
						    var ima2=document.getElementById("imgEstadoCotizacion");
						    ima.src=imagen;
						    ima1.src=imagen;
						    ima2.src=imagen;
						    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});
						    queryFormPost("llenaEstadoImagenProcedimientoProveedor",{async:false});
						    queryFormPost("llenaEstadoImagenProcedimientoCotizacion",{async:false});
						    agregaTablaProveedoresCotizaciones();
							leeProveedoresPreguntas();

							queryFormPost("llenaCaratulaProcedimientoRequisitos",{async:false});
						   queryFormPost("llenaUnidadEjecutoraProcedimientoRequisitos",{async:false});
						   var imaRequisitos = document.getElementById("imgEstadoRequisitos");
						   imaRequisitos.src = imagen;
						   queryFormPost("llenaEstadoImagenProcedimientoRequisitos",{async:false});
						   queryFormPost("mConsolidadoAlcanceRead",{async:false});
						   
						   if ($("#tipoProcedimiento").val() == 'PS' || $("#tipoProcedimiento").val() == 'PN'){
						   		mostrarRequisitos();
						   }

									    
						    //se ocultan
						   $("#tblPartidasCotizacion").css("visibility","hidden");
						   $("#tblProvCotizacion").css("visibility","hidden");
						   $("#formProveedores").css("visibility","hidden");
							
						   	//Me habilita la caratula y proveedores
						   var  proveedor='<%=proveedor%>';
						   var  documentos='<%=documentos%>';
						   var  cotizacion='<%=cotizacion%>';
						   var  caratula='<%=caratula%>';
						   var Evaluacion='<%=evaluacion%>';
						   var Preguntas='<%=preguntas%>';
						   var Archivo='<%=archivo%>';
						   
							if (Evaluacion==0){
								if ($("#tipoProcesoProveedor").val()==2){
									if ($("#cProcedimientoCumple").val() == true) {
										$("#EvaluacionProcedimiento").attr("disabled", false);
									}else{
										$("#EvaluacionProcedimiento").attr("disabled", true);
									}
									$("#EvaluacionProcedimiento").css("display", "block");
									$("#CotizacionProcedimiento").css("display", "none");
								}
							}
							if (Preguntas==0){
								if ($("#tipoProcesoProveedor").val()==2){
									$("#PreguntasProcedimiento").attr("disabled", false);
									$("#PreguntasProcedimiento").css("display", "block");
									
									$("#CotizacionProcedimiento").css("display", "none");
								}
							}

							if(Archivo==0){
								if ($("#tipoProcesoProveedor").val()==2){
									$("#ArchivosProcedimiento").attr("disabled", false);
									$("#ArchivosProcedimiento").css("display", "block");
								}
							}
                			
						   if (caratula==0){
							   $("#CaratulaProcedimiento").attr("disabled", false);
						   }
						   if (cotizacion==0){
							 if ($("#tipoProcesoProveedor").val()==1){
								if ($("#cProcedimientoCumple").val() == true) {
									$("#CotizacionProcedimiento").attr("disabled", false);
								}
								$("#CotizacionProcedimiento").css("display", "block");
								
								$("#PreguntasProcedimiento").attr("disabled", false);
								$("#PreguntasProcedimiento").css("display", "none");
								
								$("#EvaluacionProcedimiento").css("display", "none");
								$("#EvaluacionProcedimiento").attr("disabled", true);

								$("#ArchivosProcedimiento").css("display", "none");
								$("#ArchivosProcedimiento").attr("disabled", true);
							}
						   }
						   if (proveedor==0){
							 $("#ProveedoresProcedimiento").attr("disabled", false);
						   }
						   if (documentos==0){
							   $("#DocumentosProcedimiento").attr("disabled", false);
						   }

						  if ($("#tipoProcedimiento").val() == 'PS' || $("#tipoProcedimiento").val() == 'PN'){
						   		$("#RequisitosProcedimiento").attr("disabled", false);
						   		var user='<%=name_user%>';
						   		
								if ($("#cIdUsuarioCreacion").val().toUpperCase() == user.toUpperCase()) {
									$("#tblRequisitos").attr("disabled", false);
									$("#btnGuardarRequisitosProcedimiento").attr("disabled", false);
								}
								else {
									$("#tblRequisitos").attr("disabled", true);
									$("#btnGuardarRequisitosProcedimiento").attr("disabled", true);
								}
						   }
						   else {
						   		$("#RequisitosProcedimiento").attr("disabled", true);
						   }
									
							 $("#desProvedorCotizacion").css("visibility","hidden");
						     $("#CaratulaProcedimiento").click();
                      		  var btnGuardarCaratulaProcedimiento='<%=btnGuardarCaratulaProcedimiento %>';
                      		  if (btnGuardarCaratulaProcedimiento==0){
                      			 $( "#btnGuardarCaratulaProcedimiento" ).css("visibility","visible");  
                      		  }
						   
						      if($("#estadoProcedimiento").val()=="ADJUDICADO"){
							      $("#tblPartidasCotizacion").css("visibility","hidden");
							      $("#ProveedoresProcedimiento").attr("disabled", true);
								  $("#tblRequisitos").attr("disabled", true);
								  $("#btnGuardarRequisitosProcedimiento").attr("disabled", true);						      
							      $( "#btnGuardarCaratulaProcedimiento" ).css("visibility","hidden");
							      $("#tblProvCotizacion").css("visibility","hidden");
						      }
						       $("#btnBuscarProveedorProcedimiento").click();
						       validaLicitacion();
						       ponValorCheckBox();
						}     
				}	
			});
			
		$('#tblProveedoresDocumentos tr').live('click', function() { 
			var aTrs = $('#tblProveedoresDocumentos').dataTable().fnGetNodes();

			for ( var i=aTrs.length ; i>=0; i-- ){  
				if ( $(aTrs[i]).hasClass('row_selected') ){
					var nTr = $('#tblProveedoresDocumentos').dataTable().fnGetData(aTrs[i]);
					$("#cIdRfcProveedorDocumento").val(nTr[0]);
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mDocumentosProveedor('"+$("#tipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#ConsecutivoProcedimiento").val()+"','"+$("#cIdRfcProveedorDocumento").val()+"')",
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cReferencia" },
					{ sName: "checkbox" },
					{ sName: "checkbox2" },
					{ sName: "descripcion" }
				]
			});
			
		});
		/////EVENTO CLICK EN EL RENGLON DE LA TABLA DE PROVEEDORES DISPONIBLES
		$('#tblProveedoresDisponibles tr').live('dblclick', function() {
			
			if(validaModificarProcedimiento()){
		    	if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else  
				{
 

					$(this).addClass('row_selected');
					var aTrs = $('#tblProveedoresDisponibles').dataTable().fnGetNodes();
					for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							var nTr = $('#tblProveedoresDisponibles').dataTable().fnGetData(aTrs[i]);   
							//Obtengo el campo de la tabla
							$("#IdRFCProveedor").val(nTr[0]);

							if( !esProveedorAdquisiciones( nTr[0] ) ){
		
								swal("El proveedor seleccionado no es un proveedor de adquisiciones. Debe actualizarlo para que pueda ser adjudicado."
								,{icon:"info",button: "Cerrar"});

								return false;
							}
							//Asigno Valor a los campos hidden que necesito
							$("#nIdTipoCambioProveedor").val('01');
							$("#mTipoCambioProveedor").val('1');
							//$("#IContratoAbiertoProveedor").val('FALSE');
							$("#nPorcentajeIVAProveedor").val($("#ivaProcedimiento").val());
							//Mando llamar el procedimiento que me inserta los campos en la tabla mProcedimientoAdjudicacion
							queryFormPost("sp_AdjudicacionCreate",{async : false});
							oTableMP.dataTable().fnDeleteRow( i );
							alert("Registro insertado");
							agregaTablaProveedoresCotizaciones();
							$("#btnBuscarProveedorProcedimiento").click();
							
						}
					}
				}
			}	
	    });
			/// al dar clic sobre la tabla de preguntas
			
			$('#tblProvedoresPreguntas tr').live('click', function() { 
				$("#cIdProcedimientoPreguntas").val($("#cIdProcedimientoProveedor").val());
				$(ProvedoresPreguntas.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
				var aTrs = $('#tblProvedoresPreguntas').dataTable().fnGetNodes();           
				for ( var i=aTrs.length ; i>=0; i-- ){         
					if ($(aTrs[i]).hasClass('row_selected')){        
						var nTr = $('#tblProvedoresPreguntas').dataTable().fnGetData(aTrs[i]);
						$("#IdRFCPreguntas").val(nTr[5]);
						$("#razonSocialPreguntas").val(nTr[6]);
						$("#lblProveedorPreguntas").val($("#IdRFCPreguntas").val()+"-"+$("#razonSocialPreguntas").val());
					    $("#numPreguntas").val("");
						//
						muestraPreguntas();
						//agregaPreguntas();
					}     
				}	
			});
			/// termina tabla de preguntas
			
		
		//////////// evento de agregar partidas a los proveedores
		
			$('#tblProveedoresEvaluacion tr').live('click', function() { 
					if($("#nIdEstadoImagenEvaluacion").val().toString().indexOf("CAPTURADO") >= 0){
						$("#chk_pedidoAbiertoCompleto").removeAttr("disabled");
					}
					else{
						$("#chk_pedidoAbiertoCompleto").attr("disabled","true");
					}	
					$(ProveedoresEvaluacion.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
					});						
					$(this).addClass('row_selected');
            	    var aTrs = $('#tblProveedoresEvaluacion').dataTable().fnGetNodes();
            		for (var i=aTrs.length;i>=0; i-- ){       
						if ( $(aTrs[i]).hasClass('row_selected') ){  
							var nTr = $('#tblProveedoresEvaluacion').dataTable().fnGetData(aTrs[i]);
							var RFC=nTr[5];
							var razonSocial=nTr[6];
							$("#lblPartidaProveedor").val(RFC+" -  "+razonSocial);
							$("#cidRFCOculto").val(RFC);	
							queryFormPost("sp_existePartidaProcedimiento", {async: false });
							queryFormPost("cContratoAbiertoRead", {async: false });
							if($("#IContratoAbiertoProveedor").val()=="TRUE"){
				            	document.getElementById("chk_pedidoAbiertoCompleto").checked=true;
				        	}
			           	 	else{
			            		document.getElementById("chk_pedidoAbiertoCompleto").checked=false;
				        	}
						}
					}
          	tablaProveedorPartida();
            	});
		
		//--------FUNCION-----------TABLA DE MOSTRAR PROVEEDORES DISPONIBLES-----------------------
		var oTableMP;
		function mostrarTablaProveedores(){
		    $("#tblProveedoresDisponibles").css("display", "");  			
			///////////////////////CLICK EN EL TR DE tblSolicitudDispPreSel////////////////////////////////////////
			//row_selected  ----Color gris al pasar el click	
			//gradeA   -----Color verdeson
			var consulta="qw= 1=1";
			if (($.trim($("#rfcProveedor").val()))!="")
			{
				consulta += " AND cIdRFC LIKE '%25" + $.trim($("#rfcProveedor").val())+"%25'";
				consulta=consulta.replace("\&","%26");
 				
			}
			if(($.trim($("#rSocialProveedor").val()))!="" )
			{
				consulta += " AND cRazonSocial LIKE '%25" + $.trim($("#rSocialProveedor").val())+"%25'";
				consulta=consulta.replace("\&","%26");
			}
			var queryNot= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
			consulta+= " AND  CIDRFC NOT IN (select CIDRFC from v_obtieneProveedoresCotizacion where "+queryNot+")";
		
		   	//Mostramos la tabla de procedimientos
			oTableMP=$('#tblProveedoresDisponibles').dataTable({
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
					sLengthMenu: "<h2><b>PROVEEDORES DISPONIBLES</b></h2><h5>Click para agregar el proveedor</h5>",
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
				"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_GetMostrarProveedoresProcedimiento()&"+consulta,
				"aaSorting": [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial"   },
					{ sName: "alta_rapida"   }
				]
			});
		}
		//--------FUNCION-----------TABLA DE PROVEEDORES AGREGADOS---------------------------
		var oTableSP;
		function agregadosTablaProveedores(){
			var cotizacion='<%=cotizacion%>';
			$("#tblProveedoresAgregados").css("display", "");
			
			$("#tblProveedoresAgregados tbody").click(function(event) {
					$(oTableSP.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('gradeA');
					});
					$(event.target.parentNode).addClass('gradeA');
			});

			//Al darle doble click a la tabla de porcedimientos
			$('#tblProveedoresAgregados tr').live('dblclick', function() {    
				//Habilitar la pestaña de cotizaciones
				var aTrs = $('#tblProveedoresAgregados').dataTable().fnGetNodes();           
				for ( var i=aTrs.length ; i>=0; i-- )     
				{         
						if ($(aTrs[i]).hasClass('gradeA'))         
						{        
							
							var nTr = $('#tblProveedoresAgregados').dataTable().fnGetData(aTrs[i]);   
							$("#IdRFCProveedor").val(nTr[0]);
							
							$("#lblRFCcotizacion").text($.trim(nTr[0]));
							$("#lblNombreProveedorCotizacion").text($.trim(nTr[1]));
							
						if (cotizacion==0){
							  $("#CotizacionProcedimiento").attr("disabled", false);
						   }
						$("#CotizacionProcedimiento").click();
						return false;
						}     
				}	
			});
			
        	//Mostramos la tabla de proveedores agregados
			oTableSP=$('#tblProveedoresAgregados').dataTable(
			{    
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
					sLengthMenu: "<h2><b>PROVEEDORES AGREGADOS</b></h2><h5>Doble click sobre el proveedor a cotizar</h5>",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_GetAgregadosProveedoresProcedimiento('"+$("#cEjercicio").val()+"','"+$("#cboTipoProcedimiento").val()+"','"+$("#cboUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"')",
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdRFC" },
					{ sName: "razon_social"   }
				]
			});
		}
				
		function checkRequerido( o, n) {
				var sTemp = $.trim(o.val());
				o.val(sTemp);
				if ( sTemp.length == 0  ) {
					o.addClass( "ui-state-error" );
					updateTipsDlg(  n + " es un dato requerido." );
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
				
				
	function muestraPartidasElegidas(query,func){
		query=encodeURIComponent(query);
		func=encodeURIComponent(func);
	
	    if(document.getElementById("chk_pedidoAbierto").checked){
	    	$("#dtblProovedoresCotizacion").css("display","none");
	    	$("#dtblProovedoresCotizacion2").css("display","block");
		 	$('#tblProovedoresCotizacion2').dataTable().fnClearTable(); 
		 
		       //tabla partidas el procedimientos seccion cotizaciones
	             oTable2= $("#tblProovedoresCotizacion2").dataTable({
					bAutoWidth : true,
					bPaginate:true,
					bDestroy:true,
					bRetrive : true,
					bServerSide:false,
					sScrollY: "200px",
					sScrollX: "200px",
					//sScrollXInner: "200%",				
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
					},
					
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
					bProcessing: true,
				    sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 9, "asc" ]] ,
					aoColumns: [
						{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
						{ sName: "cIdCABM1",bSortable:false },					
						{ sName: "cDescripcion1",bSortable:false},
						{ sName: "nCantidad1",bSortable:false },
						{ sName: "mMontoMinimo1",bSortable:false },
						{ sName: "mMontoMaximo1",bSortable:false },
						{ sName: "mMontoMaximoBruto1",bSortable:false},
						{ sName: "nPorcentajeIVA1",bSortable:false},
						{ sName: "mMontoNeto",bSortable:false },
						{ sName: "boton1" ,bSortable:false},
						{ sName: "identificador", bVisible: false }														
						
					],fnInitComplete: function(oSettings, json) {
					$(".hola1").formatCurrency();
	    			$(".hola2").formatCurrency();
	                $(".hola3").formatCurrency();}
				
	       	});
	    }
	    else{
	    	$("#dtblProovedoresCotizacion").css("display","block");
	    	$("#dtblProovedoresCotizacion2").css("display","none");
	    	$('#tblProovedoresCotizacion').dataTable().fnClearTable(); 
			 
		       //tabla partidas el procedimientos seccion cotizaciones
	             oTable2= $("#tblProovedoresCotizacion").dataTable({
					bAutoWidth : true,
					bPaginate:true,
					bDestroy:true,
					bRetrive : true,
					bServerSide:false,
					sScrollY: "200px",
					sScrollX: "200px",
					//sScrollXInner: "200%",				
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
					},
					
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
					bProcessing: true,
				    sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 9, "asc" ]] ,
					aoColumns: [
						{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
						{ sName: "cIdCABM1",bSortable:false },					
						{ sName: "cDescripcion1",bSortable:false},
						{ sName: "nCantidad1",bSortable:false },
						{ sName: "mMontoMaximoUnitario1",bSortable:false },
						{ sName: "mMontoMaximoBruto1",bSortable:false},
						{ sName: "nPorcentajeIVA1",bSortable:false},
						{ sName: "mMontoNeto",bSortable:false },
						{ sName: "boton1" ,bSortable:false},
						{ sName: "identificador", bVisible: false }														
						
					],fnInitComplete: function(oSettings, json) {
					$(".hola1").formatCurrency();
	    			$(".hola2").formatCurrency();
	                $(".hola3").formatCurrency();}
				
	       	});
		}

	}			
		
 function  eliminaProveedor(indice){///proceso recortado
 
 if(validaModificarProcedimiento()){
 if($("#estadoProcedimiento").val()=="ADJUDICADO")
	    return;
	    
 $("#nIdProcedimiento").val($("#tipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#ConsecutivoProcedimiento").val());	    
 $("#cidRFCOculto1").val($("#nIdRFCProveedor_" + indice).val());
 $("#cIdRfcProveedorDocumento").val($("#cidRFCOculto1").val());  
 queryFormPost("deleteProveedorProcedimiento", {async:false});
 queryFormPost("deleteDocumentosProveedor",{async:false});
 agregaTablaProveedoresCotizaciones();
 //se refresca tabla de proveedores
 $("#btnBuscarProveedorProcedimiento").click();
 
    $("#tblProvCotizacion").css("visibility","hidden");
    $("#tblPartidasCotizacion").css("visibility","hidden");
    $("#formProveedores").css("visibility","hidden");
    $("#btnGuardarCotizacionProc").css("visibility","hidden");
     $("#btnPartidaCotizacionProc").css("visibility","hidden");
    
    }
 }
  

function devolverProcedimiento(){
	if(validaModificarProcedimiento()){
		if($("#estadoProcedimiento").val()=="CAPTURADO "){
			alert("No se puede devolver el procedimiento, porque su estado no lo permite");
			return;
		}
		var proc=""+$("#cEjercicio").val()+" ,"+$("#tipoProcedimiento").val()+","+$("#cIdUnidadEjecutora").val()+","+$("#ConsecutivoProcedimiento").val()+"";
		$.getJSON("../../servlet/ProcedimientoServlet?cmd=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){

			for(var i = 0; i < j.length; i++){
	                 var col=j[i].Col1
	        }
	
	        switch(col){
				case "1":  
				alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS CONTRATOS APROBADOS');
				break;
				case "2":  
				alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS CONTRATOS TIENE UNO O MAS OFICIOS APROBADOS');
				break;
				case "3":  
				alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS PEDIDOS APROBADOS');
				break;
				case "4":  
				alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS PEDIDOS TIENEN UNO O MAS OFICIOS APROBADOS');
				break;
				case "5":  
				alert('ERROR DE BORRADO DE TABLA');
				break;
				case "6":  
				alert("Se devuelve correctamente el procedimiento");
				actualizaDatosProcedimientoDevuelto();
				break;
			}
   		});
	}
}

  function eliminaPartida(indiceTabla){

	if(validaModificarProcedimiento()){
	  if($("#estadoProcedimiento").val()=="ADJUDICADO")


	    return;

	  $("#lineaConsolidado").val($("#nIdLineaConsolidado_" + indiceTabla).val());
	  queryFormPost("deletePartidasProcedimiento", {async:false});

	  //se actualiza tabla de partidas

	  //lenamos rfc y razon social
	  queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
	  //llena montos bruto y neto
	  queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
	  queryFormPost("llenaMontoNetoCotizaciones",{async:false});
      // llena iva y tipo de cambio
      queryFormPost("llenaIvaCotizaciones",{async:false}); 

      queryFormPost("tipodeCambioProcedimiento",{async:false});
	  if($("#idTipoCambio").val()!="01"){
          $("#cboCambioCotizacion").val($("#idTipoCambio").val());
          $("#valorTipoCambio").css("visibility","visible");
          $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 

      }else{
           $("#cboCambioCotizacion").val($("#idTipoCambio").val());
           $("#valorTipoCambio").css("visibility","hidden");
           $("#valorTipoCambio").val($("#valorTipoCambio1").val());
	   }

	   $("#valorTipoCambio").formatCurrency();

	   $("#montoBrutoCotizacion").formatCurrency();
	   $("#montoNetoCotizacion").formatCurrency();

	   var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'"; 
	   var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
	   query += "  AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
				             //se borra la tabla antes de realizar la consulta
	   muestraPartidasElegidas(query,func);
	   $("#btnPartidaCotizacionProc").click();
	}
 }		



				
				
  function cambiaTipoMoneda(){
    if($("#cboCambioCotizacion").val()!=1){
		$("#valorTipoCambio").css("visibility","visible")
		if($("#cboCambioCotizacion").val()==3){
			$("#valorTipoCambio").val($("#tipoCambio").val())
		}else{
			var doc=document.getElementById("valorTipoCambio");
			doc.value="";

		}


	}else{

		$("#valorTipoCambio").css("visibility","hidden")
		$("#valorTipoCambio").val(1);
	}
  }	
  function adjudicarProcedimiento(){
	if(validaModificarProcedimiento()){
		if($("#estadoProcedimiento").val()=="ADJUDICADO")
			return;	
	queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
	if($("#cProcedimientoCumple").val() == false){
		alert("El procedimiento no cumple los requisitos.");
		return;
	}

	queryFormPost("checaProveedoresAsignados",{async:false});
	if($("#tieneProveedor").val()=="0"){
		alert("El procedimiento no tiene ningun proveedor asociado");
		return;
	}
			//valida el procedimiento de invitacion a tres
			queryFormPost("fn_mVerificaAplicaPartidaDesiertaRead",{async:false});
			if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("tres personas") >= 0 && ($("#ctipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#ctipoProceso").val(),10) == 0)){			
				if($("#tieneProveedor").val() < 3){
					if(confirm("El procedimiento solo tienes "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?")){
					    $("#EstadoCaptura").val(3);
						queryFormPost("sp_ProcedimientoDesierto",{async:false});
						actualizaDatosProcedimientoDesierto();
						return;
					}
				}
				else{
					//valida que se hayan cargado mas de 3 cotizaciones				
					if($("#cPartidaDesierta").val() != ""){
						if($("#cPartidaDesierta").val().toUpperCase() == "SI"){
							if(confirm("Existen partidas que tienen menos de 3 cotizaciones si contin\xFAa se cambiaran a desiertas autom\xE1ticamente.\n\xBFDesea continuar?")){
								queryFormPost("sp_mAplicaPartidaDesierta",{async:false});
								return;
							}
						}
					}
				}
			}
	
			if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("adjudicaci") >= 0 && $("#cCategoriaDescripcion").val().toLowerCase().indexOf("n directa") >= 0 && ($("#ctipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#ctipoProceso").val(),10) == 0)){
				if($("#tieneProveedor").val() < 3){
					if(confirm("El procedimiento solo tiene "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?")){
					    $("#EstadoCaptura").val(3);
						queryFormPost("sp_ProcedimientoDesierto",{async:false});
						actualizaDatosProcedimientoDesierto();
						return;
					}
				}
			}
			
			var proc=""+$("#cEjercicio").val()+" ,"+$("#tipoProcedimiento").val()+","+$("#cIdUnidadEjecutora").val()+","+$("#ConsecutivoProcedimiento").val()+","+$("#tipoProcesoEvaluacion").val()+"";
			$.getJSON("../../servlet/ProcedimientoServlet?cmd=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
		       for(var i = 0; i < j.length; i++){
	             var col=j[i].Col1
	           }
	
	                switch(col){
					case "0":  
					alert("El Procedimiento  se ha Adjudicado Correctamente");
					actualizaDatosProcedimientoAdjudicado();
					break;
					case "1":  
					alert("Hubo un error en la Adjudicacion");
					break;
					case "2":  
					alert("Existen Proveedores sin Partidas Adjudicadas,eliminelos o agregue lineas");
					break;
					case "15":  
					alert("No puede adjudicar el procedimiento completo,le falta asignar ganador y cumple con la evaluacion tecnica ");
					break;
					
				}      
			});
		}
  	}  
		
	function actualizaDatosProcedimientoDesierto(){
		$("#estadoProcedimiento").val("DESIERTO  ")

		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	    imagen=imagen.substring(11,56);
	    var ima=document.getElementById("imgEstado");
	    var ima1=document.getElementById("imgEstadoProovedor");
	    var ima2=document.getElementById("imgEstadoCotizacion");
	    ima.src=imagen;
	    ima1.src=imagen;
	    ima2.src=imagen;
	    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});
	    queryFormPost("llenaEstadoImagenProcedimientoProveedor",{async:false});
	    queryFormPost("llenaEstadoImagenProcedimientoCotizacion",{async:false});
	    		
		var imaRequisitos = document.getElementById("imgEstadoRequisitos");
		imaRequisitos.src = imagen;
		queryFormPost("llenaEstadoImagenProcedimientoRequisitos",{async:false});
	    
		//lenamos rfc y razon social
		queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
		//llena montos bruto y neto
		queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
		queryFormPost("llenaMontoNetoCotizaciones",{async:false});
        // llena iva y tipo de cambio
        queryFormPost("llenaIvaCotizaciones",{async:false});

        queryFormPost("tipodeCambioProcedimiento",{async:false});
		if($("#idTipoCambio").val()!="01"){
			 $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			 $("#valorTipoCambio").css("visibility","visible");
			 $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 

		}else{
             $("#cboCambioCotizacion").val($("#idTipoCambio").val());
             $("#valorTipoCambio").css("visibility","hidden");
             $("#valorTipoCambio").val($("#valorTipoCambio1").val());
		}
		$("#valorTipoCambio").formatCurrency();
		$("#montoBrutoCotizacion").formatCurrency();
		$("#montoNetoCotizacion").formatCurrency();
		
		$("#tblRequisitos").attr("disabled", true);
		$("#btnGuardarRequisitosProcedimiento").attr("disabled", true);		
  }

  function desiertoProcedimiento(){
	if(validaModificarProcedimiento()){
		if($("#estadoProcedimiento").val()=="ADJUDICADO" ||$("#estadoProcedimiento").val()=="DESIERTO  " )
		return;	
		
		if (confirm("¿Desea declarar desierto el Procedimiento " + $("#cIdProcedimientoCaratula").val())) {
			$("#EstadoCaptura").val('3'); 
			queryFormPost("sp_ProcedimientoDesierto",{async:false});
			actualizaDatosProcedimientoDesierto();
		}else{
			return;
		}
	}
 }		

		function actualizaDatosProcedimientoAdjudicado(){
		             $("#estadoProcedimiento").val("ADJUDICADO")
		          	    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
						    var imagen=$("#imagenEstadoProcedimiento").val();
						    imagen=imagen.substring(11,56);
						    var ima=document.getElementById("imgEstado");
						    var ima1=document.getElementById("imgEstadoProovedor");
						    var ima2=document.getElementById("imgEstadoCotizacion");
						    ima.src=imagen;
						    ima1.src=imagen;
						    ima2.src=imagen;
						    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});
						    queryFormPost("llenaEstadoImagenProcedimientoProveedor",{async:false});
						    queryFormPost("llenaEstadoImagenProcedimientoCotizacion",{async:false});
		
		var imaRequisitos = document.getElementById("imgEstadoRequisitos");
		imaRequisitos.src = imagen;
		queryFormPost("llenaEstadoImagenProcedimientoRequisitos",{async:false});
								   
						
		//lenamos rfc y razon social
			queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
			//llena montos bruto y neto
			queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
	           // llena iva y tipo de cambio
	        queryFormPost("llenaIvaCotizaciones",{async:false});
			queryFormPost("tipodeCambioProcedimiento",{async:false});
			if($("#idTipoCambio").val()!="01"){
			    $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			    $("#valorTipoCambio").css("visibility","visible");
			    $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 

			}else{
			    $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			    $("#valorTipoCambio").css("visibility","hidden");
			    $("#valorTipoCambio").val($("#valorTipoCambio1").val());
			 }
			 $("#valorTipoCambio").formatCurrency();
			 $("#montoBrutoCotizacion").formatCurrency();
			 $("#montoNetoCotizacion").formatCurrency();
			 $("#btnGuardarCotizacionProc").css("visibility","hidden");
			 $("#btnPartidaCotizacionProc").css("visibility","hidden");
			
			$("#tblRequisitos").attr("disabled", true);
			$("#btnGuardarRequisitosProcedimiento").attr("disabled", true);
			$("#btnGuardaProcedimientoCompleto").attr("disabled", true);
			
   }	


		function actualizaDatosProcedimientoDevuelto(){
		    $("#estadoProcedimiento").val("CAPTURADO ")
		    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		    var imagen=$("#imagenEstadoProcedimiento").val();
		    imagen=imagen.substring(11,56);
		    var ima=document.getElementById("imgEstado");
		    var ima1=document.getElementById("imgEstadoProovedor");
		    var ima2=document.getElementById("imgEstadoCotizacion");
		    ima.src=imagen;
		    ima1.src=imagen;
		    ima2.src=imagen;
		    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});
		    queryFormPost("llenaEstadoImagenProcedimientoProveedor",{async:false});
		    queryFormPost("llenaEstadoImagenProcedimientoCotizacion",{async:false});
			
			var imaRequisitos = document.getElementById("imgEstadoRequisitos");
			imaRequisitos.src = imagen;
			queryFormPost("llenaEstadoImagenProcedimientoRequisitos",{async:false});
									
			//lenamos rfc y razon social

			queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
			//llena montos bruto y neto
			queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
            // llena iva y tipo de cambio
            queryFormPost("llenaIvaCotizaciones",{async:false});

            queryFormPost("tipodeCambioProcedimiento",{async:false});
			if($("#idTipoCambio").val()!="01"){
                 $("#cboCambioCotizacion").val($("#idTipoCambio").val());
                 $("#valorTipoCambio").css("visibility","visible");
                 $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
			                            
			}else{
                 $("#cboCambioCotizacion").val($("#idTipoCambio").val());
                 $("#valorTipoCambio").css("visibility","hidden");
                 $("#valorTipoCambio").val($("#valorTipoCambio1").val());
			 }

			$("#valorTipoCambio").formatCurrency();    

			$("#montoBrutoCotizacion").formatCurrency();
			$("#montoNetoCotizacion").formatCurrency(); 

			$("#btnGuardarCotizacionProc").css("visibility","hidden");
			$("#btnPartidaCotizacionProc").css("visibility","hidden");
			
			//Revisar si el usuario es el creador del procedimiento
			//si no deshabilitar la tabla de requisitos
				var user='<%=name_user%>';
			if ($("#cIdUsuarioCreacion").val().toUpperCase() == user.toUpperCase()) {
			//if ($("#cIdUsuarioCreacion").val() == '<%=name_user%>') {
				$("#tblRequisitos").attr("disabled", false);
				$("#btnGuardarRequisitosProcedimiento").attr("disabled", false);
			}
			else {
				$("#tblRequisitos").attr("disabled", true);
				$("#btnGuardarRequisitosProcedimiento").attr("disabled", true);
			}			
 }	
function salir(){
	$( "#ConsultaProcedimiento").click();
}
			

function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	var strCheck = '-0123456789.';

	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1)
		return false; // Valida que sea numero y punto decimal

	return true 
	//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
}

function onlyNumbers2(evt) 
{
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		if (keyPressed == 47) { return false; }
		return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}			
			
function checkShortcut(){
	if(event.keyCode==27){
		return false;
	}
	if((event.srcElement.tagName.toUpperCase() != 'INPUT'|| document.getElementById(event.srcElement.id).style.readonly ) && (event.keyCode==8 || event.keyCode==13)){ 
		return false;
	}
}

	//cambia tipo formato cuando se le da click a la paginacion primero,anterior,siguiente,ultimo
function cambiaFormatoPA(){
		setTimeout("$('.hola1').formatCurrency()",500);
 		setTimeout("$('.hola2').formatCurrency()",500);
        setTimeout("$('.hola3').formatCurrency()",500);
}

	//cambia tipo de formato cuando en la paginacion de numeros
function metodo(indice){
	setTimeout("$('.hola1').formatCurrency()",500);
    setTimeout("$('.hola2').formatCurrency()",500);
    setTimeout("$('.hola3').formatCurrency()",500);
}
	

function borraDatos1(indice){
	var br=$("#mMontoMaximoUnitario_"+indice+"").val();
	br=br.replace("$","");
	br=br.replace(",","");
	$("#mMontoMaximoUnitario_"+indice+"").val(br);	
}

function borraDatos2(indice){
	var br=$("#mMontoMinimo_"+indice+"").val();
	br=br.replace("$","");
	br=br.replace(",","");
	$("#mMontoMinimo_"+indice+"").val(br);	
}

function borraDatos3(indice){
	var br=$("#mMontoMaximo_"+indice+"").val();
	br=br.replace("$","");
	br=br.replace(",","");
	$("#mMontoMaximo_"+indice+"").val(br);	
}
function borraDatos4(obj){
	var br=obj.value;
	br=br.replace("$","");
	br=br.replace(",","");
	obj.value=br;	
}

function validaNegativos(indice){
 
   var maxUnitario= quitaFmt($("#mMontoMaximoUnitario_"+indice+"").val())
   	if(parseFloat(maxUnitario) < 0){
		//alert("No puede ingresar valores menores o iguales a cero");
		return "1";
	}else{
	  return "2";
	}
}
function leeProveedoresPreguntas(){
	/// tabla de preguntas a proveedores
	var consulta= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";   
	//$("#tblProvedoresPreguntas").dataTable({
		ProvedoresPreguntas=$("#tblProvedoresPreguntas").dataTable({
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
					 bAutoWidth: false,
					 bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneProveedoresPreguntas&qw="+consulta,
				aaSorting: [[ 1, "asc" ]],			
				aoColumns: [
					{ sName: "cEjercicio", bVisible: false },
					{ sName: "cIdTipoProcedimiento", bVisible: false},
					{ sName: "cIdUnidadEjecutora", bVisible: false},
					{ sName: "nIdConsecutivo", bVisible: false  },
					{ sName: "cIdProcedimiento", bVisible: false },
					{ sName: "cIdRFC"  },
					{ sName: "cRazonSocial" },
					{ sName: "boton" }
				]
				
		});
}
function agregaPreguntas(){
	queryFormPost("mProcedimientoSigPregunta",{async:false});
	queryFormPost("mProcedimientoSigPreguntaProveedor",{async:false});
	/// insertar en la tabla d preguntas
	if($("#numSiguientePreguntaProveedor").val()==0){
		var numSigPregunta=$("#numSiguientePregunta").val();
		var numPreg=$("#numPreguntas").val();
		$("#cIdProcedimientoPreguntas").val($("#cIdProcedimientoProveedor").val());
		var cont=parseInt(numPreg,10)+parseInt(numSigPregunta,10);
		for (var x=parseInt(numSigPregunta,10)+1; x<parseInt(cont,10)+1;x++){
			/// rfc:IdRFCPreguntas
			$("#numConsecPreguntas").val(x);
			queryFormPost("procedimientoPreguntasInsert",{async:false});
		}
	}else{
		var numSigPregunta=$("#numSiguientePreguntaProveedor").val();
		var numPreg=$("#numPreguntas").val();
		$("#cIdProcedimientoPreguntas").val($("#cIdProcedimientoProveedor").val());
		var cont=parseInt(numPreg,10)+parseInt(numSigPregunta,10);
		queryFormPost("procedimientoPreguntasUpdateConsec",{async:false});
		for (var x=parseInt(numSigPregunta,10)+1; x<parseInt(cont,10)+1;x++){
           $("#numConsecPreguntas").val(x);
		   queryFormPost("procedimientoPreguntasInsert",{async:false});
		}
	}
	muestraPreguntas();
}
function muestraPreguntas(){
	var consulta= "cIdProcedimiento = '"+$("#cIdProcedimientoPreguntas").val()+"' AND cIdRFC='"+$("#IdRFCPreguntas").val()+"'";
	$('#tblProvedoresPreguntasRespuestas').dataTable({         
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
							},				
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					 bAutoWidth: false,
					 bServerSide: true,
				     sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtienePreguntasProveedores&qw="+consulta,
					 aaSorting: [[ 1, "asc" ]] ,
					 aoColumns: [
					{ sName: "numPreg",bVisible:false},
					{ sName: "cIdProcedimiento",bVisible: false},
					{ sName: "cIdRFC", bVisible: false},
					{ sName: "consecutivoPreg" },
					{ sName: "pregunta"},
					{ sName: "respuesta"},
					{ sName: "boton"}
					
					]
				});
  }
function guardaPreguntas(){
	$("#cIdProcedimientoPreguntas").val($("#cIdProcedimientoProveedor").val());
  /// rfc:IdRFCPreguntas
	var aTrs = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetNodes();								
    for ( var i=aTrs.length-1 ; i>=0; i-- ){ 
	    var nTr = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetData(aTrs[i]);										
		var valor = nTr[0];
		$("#descPregunta").val($("#cdescPregunta_"+valor).val()); //pregunta
		$("#descRespuesta").val($("#cdescRespuesta_"+valor).val()); // respuesta  
		$("#numConsecPreguntas").val(valor);
		queryFormPost("procedimientoPreguntasUpdate", {async : false});
	}
    alert("Registro insertado");	
}
function eliminaPregunta(consecPregunta){
	if(validaModificarProcedimiento()){
		$("#numPreguntas").val("");
		$("#cIdProcedimientoPreguntas").val($("#cIdProcedimientoProveedor").val());
		$("#consecPreguntaDelete").val(consecPregunta);
	    // elimina pregunta seleccionada
		queryFormPost("mProcedimientoPreguntaDelete",{async:false});
	    // actualizar el consecutivo de las preguntas
	    queryFormPost("mProcedimientoPreguntaConsecutivoUpdate",{async:false});
		//agregaPreguntas();
		muestraPreguntas();
	}
}

function onlyNumbers1(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	var strCheck = '0123456789.';

	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1){
		// Valida que sea numero y punto decimal
		return false; 
	}
	
	//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	  return true;
}

function borrarTodasPreguntas(){
	 var aTrs = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetNodes();	
	 $("#numPreguntasEliminar").val(aTrs.length);
//	 queryFormPost("mProcedimientoSigPreguntaProveedorPrimero", {async : false});
//	 queryFormPost ("mProcedimientoSigPreguntaProveedor",{async : false});
	 
	 for ( var i=aTrs.length-1 ; i>=0; i-- ){         									
		nTr = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetData(aTrs[i]);										
		var valor = nTr[0];
		$("#consecPreguntaDelete").val(valor);
		queryFormPost("mProcedimientoPreguntaDelete", {async : false});
	 }
	 $("#numSiguientePreguntaProveedor").val($("#consecPreguntaDelete").val());
	 
	 	
     queryFormPost("procedimientoPreguntasUpdateConsecBorra", {async : false});
     muestraPreguntas();
	// agregaPreguntas();
}

function compare_dates(fecha, fecha2)
{
	var xFecha = fecha.split("/");
	var yFecha = fecha2.split("/");
	var xMonth;
	var xDay;
	var xYear;
	var yMonth;
	var yDay;
	var yYear;

	xMonth = xFecha[1];
	yMonth = yFecha[1];
	//verifica en que posision biene en anio en fecha1
	if(xFecha[0].toString>2){
		xDay = xFecha[2];
		xYear = xFecha[0];
	}
	else{
		xDay = xFecha[0];
		xYear = xFecha[2];
	}
	
	//verifica en que posision biene en anio en fecha2
	if(yFecha[0].toString>2){
		yDay = yFecha[2];
		yYear = yFecha[0];
	}
	else{
		yDay = yFecha[0];
		yYear = yFecha[2];
	}
	
  	if (xYear> yYear){
      return(true)
  	}
  	else{
    	if (xYear == yYear){ 
      		if (xMonth> yMonth){
          		return(true)
     		}
      		else{ 
        		if (xMonth == yMonth){
          			if (xDay >= yDay)
            			return(true);
          			else
            			return(false);
        		}
        		else
          			return(false);
      		}
    	}
    	else
      		return(false);
  	}
}
function muestraProveedoresEvaluacion(){
	/// tabla de preguntas a proveedores
	var consulta= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"' AND cEstadoProveedor=1";   
		ProveedoresEvaluacion=$("#tblProveedoresEvaluacion").dataTable({
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
					 bAutoWidth: false,
					 bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneProveedoresPreguntas&qw="+consulta,
				aaSorting: [[ 1, "asc" ]],			
				aoColumns: [
					{ sName: "cEjercicio", bVisible: false },
					{ sName: "cIdTipoProcedimiento", bVisible: false  },
					{ sName: "cIdUnidadEjecutora", bVisible: false},
					{ sName: "nIdConsecutivo", bVisible: false  },
					{ sName: "cIdProcedimiento", bVisible: false },
					{ sName: "cIdRFC"  },
					{ sName: "cRazonSocial" },
					{ sName: "boton",bVisible: false   }
				]
				
		});
}
	///// tabla de relacion partida-proveedor
	function tablaProveedorPartida(){
		//var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"','"+$("#lineaConsolidado").val()+"','"+$("#estadoPartida").val()+"'"; 
		var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"','"+$("#cidRFCOculto").val()+"'";//,'"+$("#estadoPartida").val()+"'";
		var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";  
		//query += " AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"' AND nIdLineaConsolidado='"+$("#lineaConsolidado").val()+ "'" ;
		query += " AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'";
		$('#tblPartidasProveedorEvaluacion2').dataTable().fnClearTable();
	    $('#tblPartidasProveedorEvaluacion').dataTable().fnClearTable(); 
		
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
		//	alert("abierto");
	 		$("#div_tblPartidasProveedorEvaluacion").css("display","none");
			$("#div_tblPartidasProveedorEvaluacion2").css("display","");
		    $("#tblPartidasProveedorEvaluacion2").dataTable({
					 sScrollX: "100%",
					 sScrollXInner: "500%",
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
					 bAutoWidth: false,
					 bServerSide: true,
				
				 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasCompletoBD(" + func + ")&qw="+ query ,
				 aaSorting: [[ 16, "asc" ]] ,
				 aoColumns: [
					{ sName: "ganadorAbt" },
					{ sName: "ganadorSugerido",bVisible: false},										
					{ sName: "ganadorSugOrden", bVisible: false},
					{ sName: "nIdLineaConsolidadoAbt"  ,bSortable:false},
					{ sName: "cDescripcionAbt",bSortable:false},
					{ sName: "nCantidad1",bSortable:false },
					{ sName: "cUnidad",bSortable:false },					
					//{ sName: "mMontoMaximoUnitario1",bSortable:false },
					{ sName: "tipoCambioAbt",bSortable:false },
					{ sName: "mMontoMinimoF",bSortable:false },
					{ sName: "mMontoMaximoF",bSortable:false },
					{ sName: "mMontoMaximoBruto1",bSortable:false},
					{ sName: "mMontoMaximo1",bSortable:false },
					{ sName: "evaluacionTecnicaAbt" }, 
					{ sName: "observacionesAbt" },
					{ sName: "botonNoCotiza" },
					{ sName: "boton" },					
					{ sName: "identificador", bVisible: false}
				] 
        	});
		}
		else{
			$("#div_tblPartidasProveedorEvaluacion2").css("display","none");
			$("#div_tblPartidasProveedorEvaluacion").css("display","");
		    $("#tblPartidasProveedorEvaluacion").dataTable({
				sScrollX: "100%",
						 sScrollXInner: "500%",
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
						 bAutoWidth: false,
						 bServerSide: true,
					
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasCompletoBD(" + func + ")&qw="+ query ,

					 aaSorting: [[ 15, "asc" ]] ,
					 aoColumns: [
						{ sName: "ganador" },
						{ sName: "ganadorSugerido",bVisible: false},										
						{ sName: "ganadorSugOrden", bVisible: false},
						{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
						{ sName: "cDescripcion1",bSortable:false},
						{ sName: "nCantidad1",bSortable:false },
						{ sName: "cUnidad",bSortable:false },					
						{ sName: "mMontoMaximoUnitario1",bSortable:false },
						{ sName: "tipoCambio",bSortable:false },
						{ sName: "mMontoMaximoBruto1",bSortable:false},
						{ sName: "mMontoMaximo1",bSortable:false },
						{ sName: "evaluacionTecnica" }, 
						{ sName: "observaciones" },
						{ sName: "botonNoCotiza" },
						{ sName: "boton" },					
						{ sName: "identificador", bVisible: false}
			
					] 
	        	});
		}
		
		
		///////////////////////////////MODIFICACION //////////////////////////////////
	/*	var abierto;
		var cerrado;
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			abierto=true;
			cerrado=false;
		}else{
			cerrado=true;
			abierto=false;
		}
		 $("#tblPartidasProveedorEvaluacion").dataTable({
			sScrollX: "100%",
					 sScrollXInner: "500%",
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
					 bAutoWidth: false,
					 bServerSide: true,
				
				 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasCompletoBD(" + func + ")&qw="+ query ,

				 aaSorting: [[ 17, "asc" ]] ,
				 aoColumns: [
			
					{ sName: "ganador" },
					{ sName: "ganadorSugerido",bVisible: false},			
					{ sName: "ganadorSugOrden", bVisible: false},
					{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
					{ sName: "cDescripcion1",bSortable:false},
					{ sName: "nCantidad1",bSortable:false },
					{ sName: "cUnidad",bSortable:false },					
					{ sName: "mMontoMaximoUnitario1",bVisible:cerrado },
					{ sName: "tipoCambio",bSortable:false },
					///PEDIDOS O CONTRATOS ABIERTOS
					{ sName: "mMontoMinimoF",bVisible:abierto },
					{ sName: "mMontoMaximoF",bVisible:abierto },
					//////
					{ sName: "mMontoMaximoBruto1",bSortable:false},
					{ sName: "mMontoMaximo1",bSortable:false },
					{ sName: "evaluacionTecnica" }, 
					{ sName: "observaciones" },
					{ sName: "botonNoCotiza" },
					{ sName: "boton" },					
					{ sName: "identificador", bVisible: false}
		
				] 
        	}); 
		*/
		//////////////TERMINA MODIFICACION////////////////////////////////
		
		
		
	}
	function CopiarObservaciones(){
		var aTrs;
		var obs;
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
		    obs= $("#observacionesAbt_0").val();
		}else{
			aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
			obs= $("#observaciones_0").val();
		}

		for ( var i=1 ; i<aTrs.length; i++ ) {
			var nTr;
			var n;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[i]);
				n=nTr[17];
				$("#observacionesAbt_"+n+"").val(obs);
			}else{
				nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[i]);
				n=nTr[16];
				$("#observaciones_"+n+"").val(obs);
			}
			
		}
	}
	
	function SeleccionaTodosEvaluacion(){
		var chk=1;
		var aTrs;
		var nTr;
		var n;

		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
			nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[0]);
			n=nTr[16];
			if(document.getElementById("evaluacionTecnicaAbt_"+n+"").checked){
			  chk=0;
		    }
		}
		else{
			aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
			nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[0]);
			n=nTr[15];
			if(document.getElementById("evaluacionTecnica_"+n+"").checked){
			  chk=0;
		    }
		}
		for ( var i=0 ; i<aTrs.length; i++ ) {
			var nTr;
			var n;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[0]);
				n=nTr[16];
				if(chk==1){
					document.getElementById("evaluacionTecnicaAbt_"+n+"").checked=true;
					$("#cumpleTecnicaAbt_"+n+"").val(1);
				}else{
					document.getElementById("evaluacionTecnicaAbt_"+n+"").checked=false;
					$("#cumpleTecnicaAbt_"+n+"").val(0);
				}
			}else{
				nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[0]);
				n=nTr[15];
				if(chk==1){
					document.getElementById("evaluacionTecnica_"+n+"").checked=true;
					$("#cumpleTecnica_"+n+"").val(1);
				}else{
					document.getElementById("evaluacionTecnica_"+n+"").checked=false;
					$("#cumpleTecnica_"+n+"").val(0);
				}
			}
		}
	}
	function guardaProcedimientoCompleto(){
		if(validaModificarProcedimiento()){
			var aTrs;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
			}
			else{
				aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
				
				}
		
    	for ( var i=0 ; i<aTrs.length; i++ ){
    		var aTrs;
		    var nTr;	
		    var n;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				 aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
				 nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[i]);
				 n=nTr[16];
			}
			else{
				aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
				nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[i]);
				n=nTr[15];
			}
		    $("#numGanador").val("");
		    calcularMonto(n);
		    if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
		    	aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
		    	nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[i]);
		   		n=nTr[16];
		   		
		    	//$("#montoMaxUnit").val($("#mMontoMinimoC_"+n+"").val().replace("$","").replace(",",""));
				//$("#montoMaxBrut").val($("#mMontoMaximoC_"+n+"").val().replace("$","").replace(",",""));
				
			var montoMaxUnit=$("#montoMaxUnit").val(quitaFmt($("#mMontoMinimoC_"+n+"").val()));
			var montoMaxBrut=$("#montoMaxBrut").val(quitaFmt($("#mMontoMaximoC_"+n+"").val()));
				
				
				//if(parseFloat($("#montoMaxUnit").val().toString().replace("$","").replace(",",""))){
	         //  if(parseFloat($("#montoMaxUnit").val().toString().replace("$","").replace(",","")) >= parseFloat($("#montoMaxBrut").val().toString().replace("$","").replace(",",""))){
	              if(parseFloat(montoMaxUnit) >= parseFloat(montoMaxBrut)){
	         
	                	alert("El monto m\xEDnimo debe ser menor que el monto m\xE1ximo.");
						return;       
	                }
            	//}
            	
			} else{
				aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
		    	nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[i]);
		    	n=nTr[15];
		    	
		    	$("#montoMaxUnit").val(quitaFmt($("#mMontoMaximoUnitario_"+n+"").val()));
				$("#montoMaxBrut").val(quitaFmt($("#mMontoMaximoUnitario_"+n+"").val()));
					    	
		    	//$("#montoMaxUnit").val($("#mMontoMaximoUnitario_"+n+"").val().replace("$","").replace(",",""));
				//$("#montoMaxBrut").val($("#mMontoMaximoUnitario_"+n+"").val().replace("$","").replace(",",""));
			}
		  /*  if(parseFloat($("#montoMaxUnit").val().toString().replace("$","").replace(",",""))){
                if(parseFloat($("#montoMaxUnit").val().toString().replace("$","").replace(",","")) >= parseFloat($("#montoMaxBrut").val().toString().replace("$","").replace(",",""))){
                	alert("El monto m\xEDnimo debe ser menor que el monto m\xE1ximo.");
					return;       
                }
            }*//*else{
				alert("El monto m\xEDnimo debe ser mayor a 0.");
				return; 
            }*/
            var montoMaxUnit=quitaFmt($("#montoMaxUnit").val());
			//montoMaxUnit=montoMaxUnit.replace("$","");
			var gan;
		   
		    if (document.getElementById("chk_pedidoAbiertoCompleto").checked){
		    	if(document.getElementById("ganadorAbt_"+n+"").checked){
				  gan=1;
				}else{
				  gan=0;
			    }
		    	 $("#lineaConsolidado").val($("#nIdLineaConsolidadoAbt_"+n+"").val());
		    	$("#descripcionPartida").val($("#cDescripcionAbt_"+n+"").val());
		    	$("#evaluacionTec").val($("#cumpleTecnicaAbt_"+n+"").val());
		    	$("#Observaciones").val($("#observacionesAbt_"+n+"").val());
		    }else{
		    	if(document.getElementById("ganador_"+n+"").checked){
				  gan=1;
				}else{
				  gan=0;
			    }
		    	$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+n+"").val());
		    	$("#descripcionPartida").val($("#cDescripcion_"+n+"").val());
		    	$("#evaluacionTec").val($("#cumpleTecnica_"+n+"").val());
		    	$("#Observaciones").val($("#observaciones_"+n+"").val());
		    }
			
			
			
			
			
		//	if (document.getElementById("ganador_"+n+"").checked){
			if(gan==1){
		//		if (document.getElementById("evaluacionTecnica_"+n+"").checked){ ///cumple evaluacion tecnica
				if ($("#evaluacionTec").val()==1){ ///cumple evaluacion tecnica
					queryFormPost("mProcedimientoContarGanador", {async : false});
				
					if ($("#numGanador").val()==0){
					    if (parseFloat(montoMaxUnit)==0.00){
					    	//	alert("El proveedor "+$("#lblPartidaProveedor").val()+" no puede cotizar la partida No "+$("#nIdLineaConsolidado_"+n+"").val()+" en cero y ser ganador");
					    	alert("El proveedor "+$("#lblPartidaProveedor").val()+" no puede cotizar la partida No "+$("#lineaConsolidado").val()+" en cero y ser ganador");
					    		return;
					    }else{
						    $("#ganador").val(1);
					    	queryFormPost("spAgregaLineaPartidaCompleto", {async : false});
					    }
					}else{
				    //	if (confirm("La partida No. "+$("#nIdLineaConsolidado_"+n+"").val()+" ya cuenta con un proveedor ganador.¿Desea Cambiarlo?")) {
				    		if (confirm("La partida No. "+$("#lineaConsolidado").val()+" ya cuenta con un proveedor ganador.¿Desea Cambiarlo?")) {
				    		$("#ganador").val(1);
					    	queryFormPost("procedimientoGanadorSeleccionadoUpdate", {async : false});
					    	 queryFormPost("mProcedimientoPartidaBorraProv", {async : false});
					    	// inserta en mprocedimientoAdjudicacionPartidas
					    	queryFormPost("spAgregaLineaPartidaCompleto", {async : false});
						}
				    }
				}else{
					//alert("El proveedor "+$("#lblPartidaProveedor").val()+" no puede ser ganador de la partida No "+$("#nIdLineaConsolidado_"+n+"").val()+" ya que no cumple evaluacion tecnica");
					alert("El proveedor "+$("#lblPartidaProveedor").val()+" no puede ser ganador de la partida No "+$("#lineaConsolidado").val()+" ya que no cumple evaluacion tecnica");
					    		return;
				}
    
			}else{
				$("#ganador").val(0);
			    queryFormPost("mProcedimientoPartidaDesierta", {async : false});
			}
			$("#ganadorSug").val(0);
			//alert("valor  de ganador para "+n + $("#ganador").val());
			queryFormPost("procedimientoPartidasEvaluacionUpdate", {async : false});/////actualiza la tabla
		}

    	tablaProveedorPartida();
	}
}
	function firmantesTblComparativa(){
		var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
		 oTableFirmantes = $("#tblFirmantesTblComparativa").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mFirmantesComparativa&qw=" + query,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nNumeroFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" },
					{ sName: "cTipoFirmante" }
				]
        	});
	}
	function mostrarCatalogo () {
			var qw = "cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'"; 
			oTableCatalogo = $("#tblCatalogoFirmantes").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catalogoFirmantesPedido&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" }
				]
        	});
		}
	function fnGetSelected( oTableLocal ){
		var aReturn = new Array();
		var aTrs = oTableLocal.fnGetNodes();
		for ( var i=0 ; i<aTrs.length ; i++ ){
			if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
			}
		}
			return aReturn;
	}
	
	function cambiaTipoMonedaEvaluacion(idCambio){
		 if (document.getElementById("chk_pedidoAbiertoCompleto").checked){
			 if($("#cboCambioCotizacionAbt_"+idCambio+"").val()!=1){
			   $("#valorTipoCambioAbt_"+idCambio+"").css("visibility","visible");		
		       if($("#cboCambioCotizacionAbt_"+idCambio+"").val()==3){
			     $("#valorTipoCambioAbt_"+idCambio+"").val($("#tipoCambio").val())
		      }else{
			     var doc=document.getElementById("valorTipoCambioAbt_"+idCambio+"");
			     doc.value="";
		      }
	        }else{
		      $("#valorTipoCambioAbt_"+idCambio+"").css("visibility","hidden");
	          $("#valorTipoCambioAbt_"+idCambio+"").val(1);
	        }
		 }else{
			 if($("#cboCambioCotizacion_"+idCambio+"").val()!=1){
		        $("#valorTipoCambio_"+idCambio+"").css("visibility","visible");
		        if($("#cboCambioCotizacion_"+idCambio+"").val()==3){
			        $("#valorTipoCambio_"+idCambio+"").val($("#tipoCambio").val())
		        }else{
			       var doc=document.getElementById("valorTipoCambio_"+idCambio+"");
			       doc.value="";
		        }
	        }else{
				$("#valorTipoCambio_"+idCambio+"").css("visibility","hidden");
				$("#valorTipoCambio_"+idCambio+"").val(1);
	        }
		 }
  }
	
	function calcularMonto(idCambio){
	  	
		if(validaModificarProcedimiento()){
		 	 if (document.getElementById("chk_pedidoAbiertoCompleto").checked){
				//var valida=validaNegativos($("#mMontoMinimoC_"+idCambio+"").val());
				var valida=validaNegativos(idCambio);
				var ind=parseInt(idCambio);
			 	if(valida=="1"){
					 alert("No puede ingresar valores menores o iguales a cero en la partida " + (ind+1));
				 	return ;
				}     
			    if($("#cboCambioCotizacionAbt_"+idCambio+"").val()!=1){
					if($("#valorTipoCambioAbt_"+idCambio+"").val()==""){
						alert("Ingrese el valor del tipo de cambio")
						return;
					}
					$("#valorTipoCambioAbt_"+idCambio+"").val();
				}else{
					$("#valorTipoCambioAbt_"+idCambio+"").val(1)
				}
				$("#IContratoAbiertoProveedor").val('TRUE'); 
				$("#tipoCambioEvaluacion").val($("#cboCambioCotizacionAbt_"+idCambio+"").val());
				$("#cantidadCambioEvaluacion").val($("#valorTipoCambioAbt_"+idCambio+"").val()); 	
    			$("#lineaConsolidado").val($("#nIdLineaConsolidadoAbt_"+idCambio+"").val()); 
			 }else{
			    //var valida=validaNegativos($("#mMontoMaximoUnitario_"+idCambio+"").val());
			 	var valida=validaNegativos(idCambio);
			 	var ind=parseInt(idCambio,10);
			 	if(valida=="1"){
					 alert("No puede ingresar valores menores o iguales a cero en la partida " + (ind+1));
				 	return ;
				}     
			    if($("#cboCambioCotizacion_"+idCambio+"").val()!=1){
					if($("#valorTipoCambio_"+idCambio+"").val()==""){
						alert("Ingrese el valor del tipo de cambio")
						return;
					}
					$("#valorTipoCambio_"+idCambio+"").val();
				}else{
					$("#valorTipoCambio_"+idCambio+"").val(1)
				}
				$("#IContratoAbiertoProveedor").val('FALSE'); 
				$("#tipoCambioEvaluacion").val($("#cboCambioCotizacion_"+idCambio+"").val());
				$("#cantidadCambioEvaluacion").val($("#valorTipoCambio_"+idCambio+"").val()); 	
    			$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+idCambio+"").val());
				 
			 }
			
    		/// actualiza tipo de cambio y monto en tabla mprocedimientoCompleto
    		queryFormPost("procedimientoCompletoAdjudicacionUpdate",{async : false});
		}
	}

	
	function cambiaEvaluacion(idPosicion){
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			var costo=$("#mMontoMinimoC_"+idPosicion+"").val();
		}else{
			var costo=$("#mMontoMaximoUnitario_"+idPosicion+"").val();
		}
		
		
		costo=costo.replace("$","");
		if (costo==0 || costo==0.00 ){
			alert("El precio no puede ser igual a $0.00");
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				document.getElementById("evaluacionTecnicaAbt_"+idPosicion+"").checked=false;
			}else{
				document.getElementById("evaluacionTecnica_"+idPosicion+"").checked=false;
			}
			
		}else{
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				if(document.getElementById("evaluacionTecnicaAbt_"+idPosicion+"").checked){
					$("#cumpleTecnicaAbt_"+idPosicion+"").val(1);
				}else{
					$("#cumpleTecnicaAbt_"+idPosicion+"").val(0);
				}
			}else{
				if(document.getElementById("evaluacionTecnica_"+idPosicion+"").checked){
					$("#cumpleTecnica_"+idPosicion+"").val(1);
				}else{
					$("#cumpleTecnica_"+idPosicion+"").val(0);
				}
			}
			
		}
	//alert($("#cumpleTecnica_"+idPosicion+"").val());
	}
	
	function  eliminaProveedorRFC(indice){ //preguntas
	 	if(validaModificarProcedimiento()){
	 		if($("#estadoProcedimiento").val()=="ADJUDICADO")
		   	 return;
			$("#cidRFCOculto1").val(indice);
			$("#IdRFCPreguntas").val(indice);
			queryFormPost("deleteProveedorProcedimiento", {async:false}); // borra de mprocedimientoAdjudicacion
		    //se refresca tabla de proveedores
		    $("#btnBuscarProveedorProcedimiento").click();
		    muestraProveedoresEvaluacion();
		    leeProveedoresPreguntas();
			// elimina preguntas
			queryFormPost("numeroPreguntasProveedor", {async : false});
			queryFormPost("mProcedimientoSigPreguntaProveedor", {async : false});			
			queryFormPost("mProcedimientoProveedorPreguntaDelete", {async : false});		
			
			queryFormPost("procedimientoPreguntasUpdateConsecBorra", {async : false});
			queryFormPost("mProcedimientoProveedorCotizacionesDelete", {async : false}); // borra de procedimiento completo
			queryFormPost("mProcedimientoDocumentosProveedorDelete", {async : false});
			queryFormPost("mProcedimientoPreguntasProveedorDelete", {async : false});
			muestraProveedoresEvaluacion();
		    leeProveedoresPreguntas();
			tablaProveedorPartida();
			muestraPreguntas();
			//agregaPreguntas();
	    }
   }
	
	function declararDesierta(linea,valor){
		
		if($("#estadoProcedimiento").val()=="ADJUDICADO")
		   	 return;
		
		$("#lineaConsolidado").val(linea);
		if (valor==0){ //// partida desierta
			/////// actualizar a 0 el ganador
			$("#estadoPartida").val(0);
			queryFormPost("procedimientoPartidaDesiertaUpdate", {async:false});
			// eliminar de la tabla de madjudicacion partidas al proveedor ganador en caso de que exista
			queryFormPost("mProcedimientoPartidaDesierta", {async:false});
			queryFormPost("consolidadoLineasNoProveedor", {async:false});
		}else{// partida activada
			$("#estadoPartida").val(1);
			queryFormPost("consolidadoLineaSiProveedor", {async:false});
			
		}
		tablaProveedorPartida();
	}
	
	function checkShortcut(){			
				if(event.keyCode==8){
					return true;
				}
	}
	function generaTablaComparativa(){
		///// ordenar proveedores
		$("#flag").val(1); 
		queryFormPost("sp_ordenaProveedor", {async:false});
		$("#flag").val(0);
		queryFormPost("sp_ordenaProveedor", {async:false});
		var numFirmantes = oTableFirmantes.dataTable().fnGetNodes().length;
		if (numFirmantes==2){
			var cEjercicio=$("#cEjercicio").val(); 
		var cIdTipoProcedimiento=$("#tipoProcedimiento").val(); 
		var cIdUnidadEjecutora=$("#cIdUnidadEjecutora").val(); 
		var nIdConsecutivo=$("#ConsecutivoProcedimiento").val(); 
		param="cEjercicio=" + cEjercicio
			+ "&cIdTipoProcedimiento=" + cIdTipoProcedimiento
			+ "&cIdUnidadEjecutora=" + cIdUnidadEjecutora
			+ "&nIdConsecutivo=" + nIdConsecutivo
		+"&tipoReporte="+$('[name="reporte"]:checked').val();
		//alert(param);
		 window.open('tablaComparativaResultado.jsp?' + param, 'tablaComparativaResultado', 'status=1, width=900px, height=500px,scrollbars=yes,resizable=yes');
		}else{
			alert("Eliga los firmantes correspondientes");
		}
		
	}
	
	function mostrarRequisitos() {
		$('#tblRequisitos').dataTable( {    
		"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
		"bFilter" : false,
		"bDestroy" : true,
		"bJQueryUI": true,
		"bAutoWidth" : false,
		"iDisplayLength": 20,
		"sPaginationType": "full_numbers",
		//"sScrollY": 250,
				"oLanguage": {
				sProcessing: "Procesando...",
				sLengthMenu: "<h2><b>Requisitos</b></h2>",
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mRequisitosProcedimiento('" + $("#cIdProcedimientoRequisitos").val() + "')",
			aaSorting: [[ 0, "asc" ]] ,
			aoColumns: [
				{ sName: "idRequisito" },
				{ sName: "requisito" },
				{ sName: "descripcion" },
				{ sName: "requerido" },
				{ sName: "cumple" },
				{ sName: "obs" }
			]
		});	
	}
	function validaModificarProcedimiento(){
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
	function NoAdjudicaProveedor(i){
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			if (document.getElementById("ganadorAbt_"+i).checked){
				if (confirm("La partida No. "+$("#nIdLineaConsolidadoAbt_"+i+"").val()+" ya cuenta con un proveedor ganador.¿Desea eliminarlo?")){
					$("#mMontoMinimoC_"+i+"").val(0.00);
				}else{
					return;
				}
			}else{
				$("#mMontoMinimoC_"+i+"").val(0.00);
			
			}
		$("#montoMaxUnit").val($("#mMontoMinimoC_"+i+"").val());
		$("#montoMaxBrut").val($("#mMontoMaximoC_"+i+"").val());
		$("#descripcionPartida").val($("#cDescripcionAbt_"+i+"").val());
		$("#evaluacionTec").val(0);
		$("#Observaciones").val($("#observacionesAbt_"+i+"").val());
		$("#ganadorSug").val(0);
		$("#ganador").val(0);
		$("#lineaConsolidado").val($("#nIdLineaConsolidadoAbt_"+i+"").val());
		}else{
			if (document.getElementById("ganador_"+i).checked){
			if (confirm("La partida No. "+$("#nIdLineaConsolidado_"+i+"").val()+" ya cuenta con un proveedor ganador.¿Desea eliminarlo?")){
				$("#mMontoMaximoUnitario_"+i+"").val(0.00);
			}else{
				return;
			}
		}else{
			$("#mMontoMaximoUnitario_"+i+"").val(0.00);
			
		}
		$("#montoMaxUnit").val($("#mMontoMaximoUnitario_"+i+"").val());
		$("#montoMaxBrut").val($("#mMontoMaximoUnitario_"+i+"").val());
		$("#descripcionPartida").val($("#cDescripcion_"+i+"").val());
		$("#evaluacionTec").val(0);
		$("#Observaciones").val($("#observaciones_"+i+"").val());
		$("#ganadorSug").val(0);
		$("#ganador").val(0);
		$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+i+"").val());
		}
		
		queryFormPost("procedimientoPartidasEvaluacionUpdate", {async : false});
		tablaProveedorPartida();
	}
	
	function validaFechaTerminoServicio(select){
		if(select.value.toString()=="PN" || select.value.toString()=="PS" || $("#cboTipoProcedimiento").val().toString()=="PO" || $("#cboTipoProcedimiento").val().toString()=="PQ"){
			$("#trfechaProcedimiento10").css("display","");
		}
		else{
			$("#trfechaProcedimiento10").css("display","none");
		}
		
		if(select.value.toString()=="PS"){
			$("#esServicio").val(1);
		}else{
			$("#esServicio").val(0);
		}
		
		muestraFechas();
	} 
	function enviaDatosArchivoProcedimiento(){
		$("#cEjercicioArchivo").val($("#cEjercicio").val());
		$("#cIdProcedimientoArchivo").val($("#cIdProcedimientoCaratula").val());
		$("#cTipoArchivo").val($("#tipoArchivo").val());
		$("#cIdConsolidadoArchivo").val($("#cIdConsolidadoCaratula").val());
		document.formArchivo.submit();
	}
	function generaRptAclaracion(){
			var cIdProc =$("#cIdProcedimientoCaratula").val();
			var cIdCons =$("#cIdConsolidadoCaratula").val();
			var servletPath = "../../servlet/SeguridadCatalogosMateriales?" + "rn=rptJuntaAclaracion.jasper" +"&formato=dsdoc" + "&cIdProcedimiento=" + cIdProc + "&cIdConsolidado=" + cIdCons;
			window.open(servletPath, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
	}
	function cambiaEstadoProveedor(object){
		var datos = object.value.toString().split("|");
		$("#cIdRfcProveedorDocumento").val(datos[0]);
		$("#nIdProcedimiento").val(datos[1]);

		if(object.checked){
			$("#cEstadoProveedor").val("1");
		}
		else{
			$("#cEstadoProveedor").val("0");	
		}
		queryFormPost("mUpdateEstadoProveedor",{async : false});
	}


//---#247----------------------------------------------------------------------------------------------------------
	function ponValorCheckBox(){
		queryFormPost("esActivoProcedimiento",{async:false});
		
        if($("#Activo").val() == 1){
    		//$("#comite1").attr("checked", "checked");
    		document.getElementById("doctosComite1").checked = true;
    		document.getElementById("doctosComite2").checked = false;
    		//$("#doctosComite1").attr("checked", "checked");
    	} else{
    		document.getElementById("doctosComite1").checked = false;
    		document.getElementById("doctosComite2").checked = true;
    		//$("#doctosComite2").attr("checked", "checked");
	    	//$("#comite2").attr("checked", "checked");
	    	//$("#chkActivo").removeAttr("checked");
	    }
	    validaCaratula();
	}

	function ponValorCheckBoxNuevo(){
		queryFormPost("esActivoProcedimiento",{async:false});
		
        if($("#Activo").val() == 1){
    		//$("#chkActivoNuevo").attr("checked", "checked");
    		document.getElementById("doctosComite11").checked = true;
    		document.getElementById("doctosComite22").checked = false;
    		//$("#doctosComite11").attr("checked", "checked");
	    } else{
	    	document.getElementById("doctosComite11").checked = false;
    		document.getElementById("doctosComite22").checked = true;
	    	//$("#doctosComite22").attr("checked", "checked");
	    }
	    validaCaratula();
	}
	function obtenCategoriaProcedimiento(){
		queryFormPost("ObtenCategoriaProcedimiento", {async:false});
		if(parseInt($("#cboCategoriaCaratula").val(),10) == $("#Categoria").val()){
		//nIdCaratula de BD es igual al actual del comboBox
			ponValorCheckBox();
		}
	}

	//function obtenCategoriaProcedimientoNuevo(){
	//	queryFormPost("ObtenCategoriaProcedimiento", {async:false});
	//	if(parseInt($("#cboCategoria").val()) == $("#Categoria").val()){
	//	//nIdCaratula de BD es igual al actual del comboBox
	//		ponValorCheckBoxNuevo();
	//	}
	//}
	
function validaCaratula(){
		if($("#doctosComite1").is(':checked')){
			document.getElementById("ProveedoresProcedimiento").disabled = false;
			document.getElementById("CotizacionProcedimiento").disabled = false;
			document.getElementById("DocumentosProcedimiento").disabled = false;
			document.getElementById("EvaluacionProcedimiento").disabled = false;
			document.getElementById("ArchivosProcedimiento").disabled = false;
			document.getElementById("PreguntasProcedimiento").disabled = false;
			$("#Activo").val(1);
		}
		else{
			document.getElementById("ProveedoresProcedimiento").disabled = true;
			document.getElementById("CotizacionProcedimiento").disabled = true;
			document.getElementById("DocumentosProcedimiento").disabled = true;
			document.getElementById("EvaluacionProcedimiento").disabled = true;
			document.getElementById("ArchivosProcedimiento").disabled = true;
			document.getElementById("PreguntasProcedimiento").disabled = true;
			$("#Activo").val(0);				
		}				
	}

function validaCaratulaNuevo(){
		if($("#doctosComite11").is(':checked')){
//			document.getElementById("CaratulaProcedimiento").disabled = false;
//			document.getElementById("ProveedoresProcedimiento").disabled = false;
//			document.getElementById("CotizacionProcedimiento").disabled = false;
//			document.getElementById("DocumentosProcedimiento").disabled = false;
//			document.getElementById("EvaluacionProcedimiento").disabled = false;
//			document.getElementById("ArchivosProcedimiento").disabled = false;
//			document.getElementById("PreguntasProcedimiento").disabled = false;
			$("#Activo").val(1);
		}
		else{
//			document.getElementById("CaratulaProcedimiento").disabled = true;
//			document.getElementById("ProveedoresProcedimiento").disabled = true;
//			document.getElementById("CotizacionProcedimiento").disabled = true;
//			document.getElementById("DocumentosProcedimiento").disabled = true;
//			document.getElementById("EvaluacionProcedimiento").disabled = true;
//			document.getElementById("ArchivosProcedimiento").disabled = true;
//			document.getElementById("PreguntasProcedimiento").disabled = true;
			$("#Activo").val(0);				
		}				
	}
	
		function validaLicitacion(){
		if(parseInt($("#cboCategoriaCaratula").val(),10) <= 4){
		//es licitación
			//$("#chkActivo").hide();
			$("#doctosComite1").hide();
			$("#doctosComite2").hide();
			$("#etiquetaChkActivo").hide();
			document.getElementById("ProveedoresProcedimiento").disabled = false;
			document.getElementById("CotizacionProcedimiento").disabled = false;
			document.getElementById("DocumentosProcedimiento").disabled = false;
			document.getElementById("EvaluacionProcedimiento").disabled = false;
			document.getElementById("ArchivosProcedimiento").disabled = false;
			document.getElementById("PreguntasProcedimiento").disabled = false;
			$("#Activo").val(0);	
		}
		else{
			//validación del valor del checkbox en BD para la Categoría				
				$("#doctosComite1").show();
			 	$("#doctosComite2").show();
				$("#doctosComite1").attr("checked", false);
				$("#doctosComite2").attr("checked", true);
				//$("#chkActivo").show();
				//$("#chkActivo").attr("checked", false);
				$("#Activo").val(0);	
				
				$("#etiquetaChkActivo").show();
				obtenCategoriaProcedimiento();
				validaCaratula();
			}
			
			//queryInnerDivPost("fn_mFechasProcedimientoCaratulaRead", {async : false});
	}

	function validaLicitacionNuevo(){
	
		//alert($("#cboCategoria").val());
		if(parseInt($("#cboCategoria").val(),10) <= 4){
		//es licitación
			 //$("#chkActivoNuevo").hide();
			 $("#doctosComite11").hide();
			 $("#doctosComite22").hide();
			 $("#etiquetaChkActivoNuevo").hide();
			// document.getElementById("CaratulaProcedimiento").disabled = false;
			// document.getElementById("ProveedoresProcedimiento").disabled = false;
			// document.getElementById("CotizacionProcedimiento").disabled = false;
			// document.getElementById("DocumentosProcedimiento").disabled = false;
			// document.getElementById("EvaluacionProcedimiento").disabled = false;
			// document.getElementById("ArchivosProcedimiento").disabled = false;
			// document.getElementById("PreguntasProcedimiento").disabled = false;
			$("#Activo").val(0);	
		}
		else{
			//validación del valor del checkbox en BD para la Categoría				
				//$("#chkActivoNuevo").hide();
			 	$("#doctosComite11").show();
			 	$("#doctosComite22").show();
				$("#doctosComite11").attr("checked", false);
				$("#doctosComite22").attr("checked", true);
				$("#Activo").val(0);	
				
				$("#etiquetaChkActivoNuevo").show();
				//obtenCategoriaProcedimientoNuevo();
				validaCaratulaNuevo();
			}
				
			
		muestraFechas();
	
			
	}

	function validaCategoriaNuevo(){
		//if(parseInt($("#cboCategoria").val()) = 1){
			document.getElementById("ProveedoresProcedimiento").disabled = false;
			document.getElementById("CotizacionProcedimiento").disabled = false;
			document.getElementById("DocumentosProcedimiento").disabled = false;
			document.getElementById("EvaluacionProcedimiento").disabled = false;
			document.getElementById("ArchivosProcedimiento").disabled = false;
			document.getElementById("PreguntasProcedimiento").disabled = false;
		//}
	}
	function validaContratoPedidoAbierto(check){
		if(validaModificarProcedimiento()){    
			if(check.checked){
				$("#IContratoAbiertoProveedor").val('TRUE');
			}
			else{
				$("#IContratoAbiertoProveedor").val('FALSE');
			}
						
			$("#porcentajeIVACotizacion").val();
			$("#rfcCotizacion").val();
			//Mando llamar el procedimiento que me inserta los campos en la tabla mProcedimientoAdjudicacion
			queryFormPost("sp_AdjudicacionCreate1",{async : false});	
			 //lenamos rfc y razon social
			queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
			//llena montos bruto y neto
			queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
			// llena iva y tipo de cambio
			queryFormPost("llenaIvaCotizaciones",{async:false});
			//queryFormPost("tipodeCambioProcedimiento",{async:false});
	        
	        //$("#valorTipoCambio").formatCurrency();
			$("#montoBrutoCotizacion").formatCurrency();
			$("#montoNetoCotizacion").formatCurrency();
						    
			var func="'"+$("#cEjercicio").val()+"','"+$("#tipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#ConsecutivoProcedimiento").val()+"'"; 
			var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#tipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#ConsecutivoProcedimiento").val()+"'";
			query += "  AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
			//se borra la tabla antes de realizar la consulta
			
			muestraPartidasElegidas(query,func);
		}
	}
	function validaContratoPedidoAbiertoCompleto(check){
		if(check.checked){
			$("#IContratoAbiertoProveedor").val("TRUE");
		}
		else{
			$("#IContratoAbiertoProveedor").val("FALSE");
		}
		queryFormPost("mUpdateContratoPedidoAbierto",{async:false});
		tablaProveedorPartida();
	}
	function eliminaOpcionTipoProcedimiento(){
		//elimina la opcion del combo tipo procedimiento FONDEN
		var objTipoProc = document.getElementById("cboTipoProcedimiento");
		for(var l=0;l<objTipoProc.options.length;l++){
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE FONDEN")
				objTipoProc.options[l]=null;
		}
		//elimina la opcion del combo tipo procedimiento CAPITULO 1000
		for(var l=0;l<objTipoProc.options.length;l++){
			if(objTipoProc.options[l].text.toString().toUpperCase() == "DE CAPITULO1000")
				objTipoProc.options[l]=null;			
		}
	}
	function validaFormatoFecha(fecha){
		var fechaS=fecha.split("/");
			//alert("fechaS[0] "+fechaS[0]);
		if(fechaS[0].length == 4){//Verifica el formato del anio
			
			if(fechaS[1].length == 2 && fechaS[2].length == 2){
				if(isInteger(fechaS[0]) && isInteger(fechaS[1]) && isInteger(fechaS[2]))
					return true;
				else
					return false;
			}
			else
				return false;
		}
		if(fechaS[2].length == 4){//verifica el formato del anio
			if(fechaS[0].length == 2 && fechaS[1].length == 2){
				if(isInteger(fechaS[0]) && isInteger(fechaS[1]) && isInteger(fechaS[2]))
					return true;
				else
					return false;
			}
			else
				return false;
		}
	}
	
	function isInteger(s){
        var i;
        var c;
        for (i = 0; i < s.length; i++){
            c = parseInt(s.charAt(i),10);
            if((c != 0) && (c != 1) && (c != 2) && (c != 3) && (c != 4) && (c != 5) && (c != 6) && (c != 7) && (c != 8) && (c != 9))
           		return false;
        }
        return true;
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
//--------------------------------------------------------------------------------------------------------------



function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
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
	

	function muestraFechas(){
		queryFormPost("fechasProcedimientoCuentaRead",{async:false}); //Cuantas fechas hay en la bd
		var intentos = 0;
		do{
			queryInnerDivPost("fn_mFechasProcedimientoRead", {async : false}); //Trae el html con los inputs
			var inputDateCreados = $('input','#tablaFechasProcedimiento').size(); //Cuenta los inputs creados
			intentos++;
		}
		while(inputDateCreados < $("#fechasProcedimiento").val() && intentos < 3);
		
		if (inputDateCreados < $("#fechasProcedimiento").val()){
			alert("Error al cargar las fechas necesarias. Contacte a su soporte.");
		}
		$("#tblProveedoresDisponibles").css("display", "none"); 
	}

///////////////////////////////fechas en caratula

function validaLicitacion2(){

	
	if(confirm("Al cambiar de categoria se borrarán las fechas, \n¿Desea continuar?")){

		if(parseInt($("#cboCategoriaCaratula").val(),10) <= 4){
		//es licitación
			//$("#chkActivo").hide();
			$("#doctosComite1").hide();
			$("#doctosComite2").hide();
			$("#etiquetaChkActivo").hide();
			document.getElementById("ProveedoresProcedimiento").disabled = false;
			document.getElementById("CotizacionProcedimiento").disabled = false;
			document.getElementById("DocumentosProcedimiento").disabled = false;
			document.getElementById("EvaluacionProcedimiento").disabled = false;
			document.getElementById("ArchivosProcedimiento").disabled = false;
			document.getElementById("PreguntasProcedimiento").disabled = false;
			$("#Activo").val(0);	
		}
		else{
			//validación del valor del checkbox en BD para la Categoría				
				$("#doctosComite1").show();
			 	$("#doctosComite2").show();
				$("#doctosComite1").attr("checked", false);
				$("#doctosComite2").attr("checked", true);
				//$("#chkActivo").show();
				//$("#chkActivo").attr("checked", false);
				$("#Activo").val(0);	
				
				$("#etiquetaChkActivo").show();
				obtenCategoriaProcedimiento();
				validaCaratula();
			}
			
			}
			
			queryInnerDivPost("llenaFechasProcedimientoModificacion", {async : false});
	}

	function muestraInformacionCaratula (){
		 //query para llenar la informacion de la caratula.
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   
		   if($("#tipoProcedimiento").val()=='PS'){
		   	 $("#esServicio").val(1);
		   }else{
		   	 $("#esServicio").val(0);
		   }
		   queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false});
	}
	
	function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			return (keyPressed >= 48 && keyPressed <= 57);
		}

	function esProveedorAdquisiciones( rfc ){
		
		$("#rfcValidar").val(rfc);
		var esProveedorAdquisiciones = false;
		var queryName = "esProveedorAdquisiciones";
		queryFormPost({
			queryName:queryName,
			async:true,
			callback:function(){
				esProveedorAdquisiciones = "true" === $("#proveedorAdquisiciones").val();
			}
		})
		return esProveedorAdquisiciones;
	}
	
</script>
 </head>
  
  <body id="dt_example" onkeydown="return checkShortcut();" >
    <form>
		<input id="proveedorAdquisiciones" name="proveedorAdquisiciones" value=""  type="hidden">
		<input id="rfcValidar" name="rfcValidar" value=""  type="hidden">
    	<input id="cEjercicio" name="cEjercicio" type="hidden" size="4">
    	<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="4">
    	<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="4">
    	<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
    	<input id="cPartidaDesierta" name="cPartidaDesierta" type="hidden" size="4">
    	
    	<input id="nidcategoria" name="nidcategoria" type="hidden" size="4">
    	
    	<input id="TipoConsolidado" name="TipoConsolidado" type="hidden" size="10">
    	<input id="ConsecutivoConsolidado" name="ConsecutivoConsolidado" type="hidden" size="10">
    	<input id="ConsecutivoProcedimiento" name="ConsecutivoProcedimiento" type="hidden" size="10">
    	<input id="EstadoCaptura" name="EstadoCaptura" value="1"  type="hidden" size="10">
    	<input id="tipoProcedimiento" name="tipoProcedimiento"  type="hidden" size="10">
    	<input id="estadoProcedimiento" name="estadoProcedimiento"  type="hidden" size="10">
    	<input id="nIdestadoProcedimiento" name="nIdestadoProcedimiento"  type="hidden" size="10">
    	<input id="imagenEstadoProcedimiento" name="imagenEstadoProcedimiento"  type="hidden" size="10">
    	
    	<input id="lineaConsolidado" name="lineaConsolidado" type="hidden" size="10">
    	<input id="montoMinimo" name="montoMinimo" type="hidden" size="10">
    	<input id="montoMaximo" name="montoMaximo" type="hidden" size="10">
    	<input id="descripcionPartida" name="descripcionPartida" type="hidden" size="10">
    	
    	<!-- AL IR AGREGANDO PROVEEDORES -->
    	<input id="IdRFCProveedor" name="IdRFCProveedor" value=""  type="hidden" size="10">
    	
    	<input id="nIdTipoCambioProveedor" name="nIdTipoCambioProveedor" value=""  type="hidden" size="10">
    	<input id="mTipoCambioProveedor" name="mTipoCambioProveedor" value=""  type="hidden" size="10">
    	<input id="IContratoAbiertoProveedor" name="IContratoAbiertoProveedor" value=""  type="hidden" size="10">
    	<input id="nPorcentajeIVAProveedor" name="nPorcentajeIVAProveedor" value=""  type="hidden" size="10">
    	<input id="cidRFCOculto" name="cidRFCOculto" value=""  type="hidden" size="10">
    	<input id="cidRFCOculto1" name="cidRFCOculto1" value=""  type="hidden" size="10">
    	<input id="tipoCambioOculto" name="tipoCambioOculto" value=""  type="hidden" size="10">
    	<input id="tieneProveedor" name="tieneProveedor" value=""  type="hidden" size="10">
    	<input id="usuarioUE" name="usuarioUE" value=""  type="hidden" size="10">
    	<input id="usuarioLogin" name="usuarioLogin" value=""  type="hidden" size="10">
    	<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" value=""  type="hidden" size="10">
    	<input id="idTipoCambio" name="idTipoCambio" value=""  type="hidden" size="10">
    	<input id="valorTipoCambio1" name="valorTipoCambio1" value=""  type="hidden" size="10">
    	<input id="totalPromedioProcedimiento" name="totalPromedioProcedimiento" value=""  type="hidden" size="10">
    	<input id="idconsolidado" name="idconsolidado" value=""  type="hidden" size="10">
    	<input id="tipoCambio" name="tipoCambio" value=""  type="hidden" size="10">
    	
    	<!-- Tipo de proceso -->
    	<input id="ctipoProceso" name="ctipoProceso" value=""  type="hidden" size="10">
    	<input id="tipoProcesoCaratula" name="tipoProcesoCaratula" value=""  type="hidden" size="10">
    	<input id="tipoProcesoProveedor" name="tipoProcesoProveedor" value=""  type="hidden" size="10">
        <!-- Preguntas -->
    	<input id="razonSocialPreguntas" name="razonSocialPreguntas" value=""  type="hidden" size="10">
    	<input id="IdRFCPreguntas" name="IdRFCPreguntas" value=""  type="hidden" size="10">
    	<input id="cIdProcedimientoPreguntas" name="cIdProcedimientoPreguntas" value=""  type="hidden" size="10">
    	<input id="numConsecPreguntas" name="numConsecPreguntas" value=""  type="hidden" size="10">
    	<input id="descRespuesta" name="descRespuesta" value=""  type="hidden" size="10">
    	<input id="descPregunta" name="descPregunta" value=""  type="hidden" size="10">
    	<input id="numSiguientePregunta" name="numSiguientePregunta" value=""  type="hidden" size="10">
    	<input id="consecPreguntaDelete" name="consecPreguntaDelete" value=""  type="hidden" size="10">
    	<input id="numSiguientePreguntaProveedor" name="numSiguientePreguntaProveedor" value=""  type="hidden" size="10">
    	<input id="numPreguntasEliminar" name="numPreguntasEliminar" value=""  type="hidden" size="10">
    	<!--Evaluacion <input id="descripcionPartidaEvaluacion" name="descripcionPartidaEvaluacion" value=""  type="hidden" size="10"> -->
    	
    	<!--<input id="descripcionPartidaEvaluacionNuevo" name="descripcionPartidaEvaluacionNuevo" value=""  type="hidden" size="10">
    	<!--  <input id="cGanador" name="cGanador" value=""  type="hidden" size="10">-->
    	<input id="evaluacionTec" name="evaluacionTec" value=""  type="hidden" size="10">
    	<input id="ganadorSug" name="ganadorSug" value=""  type="hidden" size="10">
    	<input id="Observaciones" name="Observaciones" value=""  type="hidden" size="10">
    	<input id="montoMaxUnit" name="montoMaxUnit" value=""  type="hidden" size="10">
    	<input id="montoMaxBrut" name="montoMaxBrut" value=""  type="hidden" size="10"> 
    	<input id="rfcEvaluacion" name="rfcEvaluacion" value=""  type="hidden" size="10">
    	<input id="nIdFirmante" name="nIdFirmante" value=""  type="hidden" size="10">
    	<input id="nNumeroFirmante" name="nNumeroFirmante" value=""  type="hidden" size="10">
    	<input id="tipoCambioEvaluacion" name="tipoCambioEvaluacion" value=""  type="hidden" size="10"> 
    	<input id="cantidadCambioEvaluacion" name="cantidadCambioEvaluacion" value=""  type="hidden" size="10">
    	<input id="tipoProcesoEvaluacion" name="tipoProcesoEvaluacion" value=""  type="hidden" size="10">
    	<input id="existeProv" name="existeProv" value=""  type="hidden" size="10">
    	<input id="existeProveedorAdjudicacion" name="existeProveedorAdjudicacion" value=""  type="hidden" size="10">
    	<input id="estadoPartida" name="estadoPartida" value="1"  type="hidden" size="10">      	
    	<!-- //////////////////////////////////////////// <input id="numPartidas" name="numPartidas" value=""  type="hidden" size="10">  -->
    	
    	<input id="flag" name="flag" value=""  type="hidden" size="10"> 
    	<input id="ganador" name="ganador" value=""  type="hidden" size="10"> 
    	<input id="numGanador" name="numGanador" value=""  type="hidden" size="10"> 
    	
    	<!-- Checklist de Requisitos -->
    	<input type="hidden" id="cProcedimientoCumple" name="cProcedimientoCumple" >
    	<input type="hidden" id="cIdRequisito" name="cIdRequisito" >
    	<input type="hidden" id="cRequisitoRequerido" name="cRequisitoRequerido" >
    	<input type="hidden" id="cRequisitoCumple" name="cRequisitoCumple" >
    	<input type="hidden" id="cRequisitoObs" name="cRequisitoObs" >
    	<input id="Activo" name="Activo" value="false" type="hidden" size="10">
    	<input id="Categoria" name="Categoria" value="" type="hidden" size="10">
    	
    	<input type="hidden" id="cEstadoProveedor" name="cEstadoProveedor" >
    	
    	<input type="hidden" id="fechasProcedimiento" name="fechasProcedimiento" >
    	
    	<input type="hidden" id="esServicio" name="esServicio" >
    		
    	<div id="container" class="container">
    		<h1>Procedimientos<label id="lbOperacion" style="font-size: 8pt"></label></h1>
			<div class="tabs" id="tabsId">
				<ul>
						<li><a id="NuevoProcedimiento" href="#tabs-0">Nuevo</a></li>
						<li><a id="ConsultaProcedimiento" href="#tabs-1">Consultar</a></li>
						<li><a id="CaratulaProcedimiento" href="#tabs-2">Car&aacute;tula</a></li>
						<li><a id="ProveedoresProcedimiento" href="#tabs-3">Proveedores</a></li>
						<li><a id="RequisitosProcedimiento" href="#tabs-8">Requisitos</a></li>						
						<li><a id="CotizacionProcedimiento" href="#tabs-4">Cotizaci&oacute;n</a></li>
						<li><a id="PreguntasProcedimiento" href="#tabs-5">Preguntas</a></li>
						<li><a id="DocumentosProcedimiento" href="#tabs-6">Documentos</a></li>
						<li><a id="EvaluacionProcedimiento" href="#tabs-7">Evaluaci&oacute;n</a></li>
						<li><a id="ArchivosProcedimiento" href="#tabs-9">Actas Archivos</a></li>
				</ul>
				<br/>
				<table>
				<tr>
				<td align="right">
				Unidad Ejecutora:<input type="text" id="ueProc" name="ueProc" style="border: 0px solid black; color:red;width: 30em;" readonly="readonly"  >
				</td>
				
				</tr>
				
				</table>


				<div id="tabs-0" align="center">
					<table border="0" align="center" width="800px" height="20">

						<tr>
							<td align="right">Unidad Ejecutora:</td>
							<td><select id="cboUnidadEjecutora" name="cboUnidadEjecutora" style="width: 30em;" onchange="muestraConsolidado();">
									<option value="<%=usuario.getU_UR()%>" selected="selected"></option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">Tipo de Procedimiento:</td>
							<td><select id="cboTipoProcedimiento" name="cboTipoProcedimiento"style="width: 30em;" onchange="muestraConsolidado();validaFechaTerminoServicio(this);">
									<option value="" selected="selected"></option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">Consolidado:</td>
							<td><select id="cboConsolidado" name="cboConsolidado" style="width: 30em;" onchange="muestraConsolidado();">
									<option value="" selected="selected"></option>
								</select>
							</td>
						</tr>
						<tr>
							<td align="right">IVA del procedimiento(%):</td>
							<td><input type="text" id="nPorcentajeIVA" name="nPorcentajeIVA" style="width: 30em;" onKeyPress="return(onlyNumbers2(event))"  /></td>
						</tr>
						<tr>
						
							<td align="right">N&uacute;mero Externo:</td>
							<td><input type="text" id="numero_externo"  onkeypress="return onlyIntegers(event);"   name="numero_externo" style="width: 30em;" maxlength="30" /></td>
						</tr>
						<tr>
							<td align="right">Categoria:</td>
							<td><select id="cboCategoria" name="cboCategoria" onchange="validaLicitacionNuevo();" style="width: 30em;">
									<option value="" selected="selected"></option>
								</select>
							</td>
							<td id="etiquetaChkActivoNuevo" style="width:12em;" align="center">
							
							
							<%-- input type="checkbox" id="chkActivoNuevo" name="chkActivo" value="false" onclick="validaCaratulaNuevo();" align="right" --%>
							
							¿Cuenta con documentos <br>o autorización <br>del comité?
							<BR>
								Si<input type="radio" id="doctosComite11" name="doctosComite" value="1" onclick="document.getElementById('Activo').value=this.value;validaCaratulaNuevo();"/>
						  &nbsp;No<input type="radio" id="doctosComite22" name="doctosComite" value="0" onclick="document.getElementById('Activo').value=this.value;validaCaratulaNuevo();"/>
									
						</tr>
						<tr>
								<td align="right">Descripci&oacute;n:</td>
                   				<td><textarea id="descripcion" name="descripcion"  style="height: 91px; width: 400px" ></textarea></td>
							</tr>	
							<tr>
								<td align="right">Tipo de Proceso:</td>
								<td><select id="tipoProcesoProcedimiento" name="tipoProcesoProcedimiento"	style="width: 30em;">
									<option value="" selected="selected"></option>
								</select>
							</td>
							<!-- 	<td align='right'><input type="radio" name="tipoProcesoP" checked id="tipoProcesoRecortado" value="1"> Proceso Recortado </td>
								<td><input type="radio" name="tipoProcesoP" id="tipoProcesoCompleto" value="0"  >Proceso Completo</td> -->
								
							</tr>		
							<tr>
								<td colspan="2">
									<input type="hidden" name="nIdProcedimientoNuevo" id="nIdProcedimientoNuevo" />
									<input type="hidden" name="nIdFechaProcedimiento" id="nIdFechaProcedimiento" />
									<input type="hidden" name="nFechaProcedimiento" id="nFechaProcedimiento" />
									<div id="tablaFechasProcedimiento" style="width:100%;">
										<!-- Carga las fechas leidas de la base de datos -->
									</div>
								</td>
							</tr>
							<tr>
								<td width="33%">&nbsp;</td>
								<td width="33%" align="center"><button id="btnGuardarProcedimiento">GUARDAR	</button></td>
								<td width="33%" align="right">&nbsp;</td>
							</tr>
						</table>
					</div>					
				<!-- <label class="validateTips ui-state-error" ></label><br/><br/> -->	
				<!-- inicia nuevo procedimiento -->	
					<div id="tabs-1" align="center">						
						<table border="0" align="center" width="800px">
							<tr>
								<td align="right">Unidad Ejecutora:</td>
								<td align="left">
									<select id="cboUnidadEjecutoraConsultar" name="cboUnidadEjecutoraConsultar" style="width: 30em;">
										<option value="<%=usuario.getU_UR()%>" selected="selected"></option>
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">Tipo de Procedimiento:</td>
								<td align="left">
									<select id="cboTipoProcedimientoConsultar" name="cboTipoProcedimientoConsultar"style="width: 30em;" onchange="">


										<option value="" selected="selected"></option>

									</select>

								</td>

							</tr>

							<tr>
								<td align="right">Estado:</td>


								<td align="left">
									<select id="cboEstadoConsultar" name="cboEstadoConsultar" style="width: 30em;" onchange="">

										<option value="" selected="selected"></option>


									</select>

								</td>

							</tr>
							<tr>
								<td align="right">Procedimiento:</td>


								<td align="left">
									<input type="text" id="procedimientoConsultar" name="procedimientoConsultar"style="width: 30em;" />


								</td>

							</tr>
							<tr>
								<td align="right">N&uacute;mero:</td>



								<td align="left"><input type="text" id="numeroConsultar"  onkeypress="return onlyIntegers(event);"  name="numeroConsultar"style="width: 30em;" /></td>



							</tr>
							<tr>
								<td align="right">Descripci&oacute;n:</td >




								<td align="left"><input type="text" id="descripcionConsultar" name="descripcionConsultar"style="width: 30em;" /></td>



							</tr>

							<tr><td align="right">Consolidado:</td>



								<td align="left"><input type="text" id="consolidadoConsultar" name="consolidadoConsultar"style="width: 30em;" /></td>



							</tr>
							<tr>
								<td align="right">Tipo de Proceso:</td>
								<td align="left"><select id="tipoProcesoProcedimientoBuscar" name="tipoProcesoProcedimientoBuscar"	style="width: 30em;">
									<option value="" selected="selected"></option>
								</select>
							</td>
							</tr>
							<tr>
								<td colspan="3" align="center">
								
									<button id="btnBuscarConsultaProcedimiento">
										BUSCAR
									</button>
									
								</td>
								
								
							</tr>

						</table>

						<div id= "div_tblProcedimientos">

							<table id="tblProcedimientos" class="display" width="800px"  height="50px">
							   <thead>
							      <tr >

				                	<th width="35px">&nbsp;</th>
				                	<th  width="35px">&nbsp;</th>
				                    <th width="35px">&nbsp;</th>
				                    <th width="200px" >Descripci&oacute;n</th>
				                    <th width="85px">Estado</th>
				                    <th width="85px" >Categor&iacute;a</th>
				                    <th width="85px">Consolidado</th>
				                    <th width="85px">Procedimiento</th>
				                    <th width="85px">Tipo Proceso</th>
				                    <th width="20px">IdCategoria</th>
				                    <th width="20px">IdUsuarioCreacion</th>
				                    <th width="20px">IdEstado</th>
				                    <th width="20px">cOficio</th>
				                 </tr>
				             </thead>


				         </table>
				       </div>


					  </div>
					  <!-- termina   consulta-->
					  <!-- inicia caratula -->
					<div id="tabs-2" >
					
					<fieldset>
					
						<legend>
					Informaci&oacute;n del Procedimiento
						
						</legend>
					
					
						<table border="0" align="left" width="100%">
						<tr >							
									<td align="right" colspan="2"  >
									<img id="imgPlayStop" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="adjudicarProcedimiento();"/>Adjudicar
									<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
									<img id="imgPlayStop" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="devolverProcedimiento();"/>Devolver
									<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
									</td>
								</tr>
								<tr align="left">
									<td colspan="2">
										[[<input name="cIdProcedimientoCaratula" id="cIdProcedimientoCaratula" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"/>]]&nbsp;<input name="desProcedimientoCaratula" id="desProcedimientoCaratula" type="text" maxlength="150" style="border: 0px solid black; width: 40em;" readonly="readonly"></input>  
									</td>								
								</tr>
						
						  
						   <tr align="left">
									<td colspan="2">
										<input name="cIdUnidadEjecutoraCaratula" id="cIdUnidadEjecutoraCaratula" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
									</td>								
							</tr>
						<tr align="left">
									<td colspan="2">
									Consolidado: [[<input name="cIdConsolidadoCaratula" id="cIdConsolidadoCaratula" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"></input>]]  
									</td>								
						</tr>
						<tr align="left">
									<td colspan="2">
									Tipo de Proceso: [[<input name="lbltipoProceso" id="lbltipoProceso" type="text" size ="18" style="border: 0px solid black;" readonly="readonly"></input>]]  
									</td>								
						</tr>
						<tr align="left">
									<td colspan="2">
							<img id="imgEstado"/><input name="nIdEstadoImagenCaratula" id="nIdEstadoImagenCaratula" type="text"  style="border: 0px solid black; width: 45em;"></input>  
									</td>								
						</tr>
						<tr>
							<td colspan="2">
						 		<div id="tablaFechasProcedimientoCaratula" style="width:100%;">
									<!-- Carga las fechas -->
								</div>
							</td>
						</tr>								
						<tr>
							<td>
						 	<br/>
							</td>
						</tr>
					   
					   </table>
					   	</fieldset>
					   	<br/>
					   
					   <table>
					   		<tr>
								<td align="right">IVA:</td>
                   				<td align="left"><input type="text" id="ivaProcedimiento" name="ivaProcedimiento"style="width: 30em;" value=""/></td>

						  </tr>
						  <tr>



								<td align="right">Descripci&oacute;n:</td>
                   				<td align="left"><textarea id="descripcionCaratula" name="descripcionCaratula"  style="height: 91px; width: 400px" ></textarea></td>

						  </tr>
						  <tr>
								<td  align="right">N&uacute;mero Externo:</td>
								<td align="left"><input type="text" id="numero_externoCaratula" onkeypress="return onlyIntegers(event);" name="numero_externoCaratula"style="width: 30em;" maxlength="30" /></td>
						 </tr>
						 <tr>
								<td align="right">	Categoria:</td>
								<td align="left">
									<input type="hidden" name="cCategoriaDescripcion" id="cCategoriaDescripcion">
									<select id="cboCategoriaCaratula" name="cboCategoriaCaratula" onchange="validaLicitacion2();"
										style="width: 30em;">
										<option value="" selected="selected">
										</option>
									</select>

								</td>
								<td id="etiquetaChkActivo">
								
								¿Cuenta con documentos o autorización del comité?
							<BR>
								Si&nbsp;<input type="radio" id="doctosComite1" name="doctosComite" value="1" onclick="document.getElementById('Activo').value=this.value;validaCaratula();"/>
								No&nbsp;<input type="radio" id="doctosComite2" name="doctosComite" value="0" onclick="document.getElementById('Activo').value=this.value;validaCaratula();"/>
						
								<%-- 
								<input type="checkbox" id="chkActivo" name="chkActivo" value="false" onclick="validaCaratula();" align="right" >
								
								
								¿Cuenta con documentos o autorización del comité?
								--%>
								</td>
						</tr>
						<tr>
								<td width="33%">&nbsp;</td>
								<td width="33%" align="center"><button id="btnGuardarCaratulaProcedimiento">GUARDAR</button></td>
								<td width="33%" align="right">&nbsp;</td>
						</tr>
					</table>
					</div>
					<!-- termina caratula  -->
					<!-- inicia proveedores -->
					<div id="tabs-3" align="center">
					<fieldset>
						<legend>Informaci&oacute;n del Procedimiento</legend>
							<table align="left" width="100%">
						        <tr >
									<td align="right" colspan="2"  >
									<img id="imgAdjudicarProveedoresProc" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="validaBotones('imgAdjudicarProveedoresProc');"/>Adjudicar
									<img id="imgDesiertoProveedoresProc" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="validaBotones('imgDesiertoProveedoresProc');" />Desierto
									<img id="imgDevolverProveedoresProc" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="validaBotones('imgDevolverProveedoresProc');"/>Devolver
									<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
									</td>
								</tr>
								
								<tr align="left">
									<td  colspan="2">
										[[<input name="cIdProcedimientoProveedor" id="cIdProcedimientoProveedor" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"/>]]&nbsp;<input name="desProcedimientoProveedor" id="desProcedimientoProveedor" type="text" maxlength="150" style="border: 0px solid black; width: 30em;" readonly="readonly"></input>  
									</td>								
								</tr>
						
						  
						   <tr align="left">
									<td colspan="2">
										<input name="cIdUnidadEjecutoraProveedor" id="cIdUnidadEjecutoraProveedor" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
									</td>								
								</tr>
						
						
						<tr align="left">
									<td colspan="2">
									Consolidado: [[<input name="cIdConsolidadoProveedor" id="cIdConsolidadoProveedor" type="text" size ="10" style="border: 0px solid black;"`readonly="readonly"></input>]]  
									</td>								
								</tr>
						
						
								<tr align="left">
									<td colspan="2">
							Tipo de Proceso: [[<input name="lbltipoProcesoProveedor	" id="lbltipoProcesoProveedor" type="text" size ="18" style="border: 0px solid black;" readonly="readonly"></input>]]
 
									</td>								
								</tr>	
								<tr align="left">
									<td colspan="2">
							<img id="imgEstadoProovedor"/><input name="nIdEstadoImagenProveedor" id="nIdEstadoImagenProveedor" type="text"  style="border: 0px solid black; width: 45em;" readonly="readonly"></input>  
									</td>								
								</tr>	
						<tr align="left">
							<td>
						 		<div id="tablaFechasProcedimientoProveedor" style="width:100%;">
									<!-- Carga las fechas -->
								</div>
							</td>
						</tr>
						<tr>
						<td>
						 <br/>
						</td>
						</tr>
						</table>
							</fieldset>
							<br/>
						
						<table>
							<tr>
								<td colspan="2"><h4>Solo puede seleccionar Proveedores con documentacion completa autorizada. Los proveedores con alta rapida se excluyen</h4></td>
							</tr>
							<tr><td align="right">RFC:</td>
								<td><input type="text" id="rfcProveedor" name="rfcProveedor"style="width: 30em;" /></td>
							</tr>
							<tr><td align="right">Raz&oacute;n Social:</td>
								<td><input type="text" id="rSocialProveedor" name="rSocialProveedor"style="width: 30em;" /></td>
							</tr>
							<tr><td width="33%"></td>
								<td width="33%" align="center"><button id="btnBuscarProveedorProcedimiento">BUSCAR</button></td>
								<td width="33%" align="right">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3" >
									<table id="tblProveedoresDisponibles" class="display">
								       <thead>
								          <tr>
								             <th>RFC</th>
								             <th>Raz&oacute;n Social</th>
											 <th>Alta Rapida</th>
								          </tr>
								        </thead>
								     </table>
								</td>						
							</tr>
						</table>

					</div>
					<!-- termina proveedores -->
					<div id="tabs-6" align="center">						
					<fieldset>
						<table border="0" >
					    	<tr>
					    		<td  align="center">
					    			<div id="demo">
					    				<input type="hidden" name="cIdRfcProveedorDocumento" id="cIdRfcProveedorDocumento" />
					    				<input type="hidden" name="nIdProcedimiento" id="nIdProcedimiento" />
					    				<input type="hidden" name="cIdDocumento" id="cIdDocumento" />
					    				<input type="hidden" name="cPresentado" id="cPresentado" />
					    				<input type="hidden" name="cDescripcionDocumento" id="cDescripcionDocumento" />
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
								        	<button id="btnSeleccionarDocumentosRequeridos">Seleccionar Requeridos</button>&nbsp;&nbsp;&nbsp;&nbsp;
								        	<button id="btnSeleccionarTodoDocumentos">Seleccionar Todo</button>&nbsp;&nbsp;&nbsp;&nbsp;
								        	<button id="btnDeseleccionarTodoDocumentos">Deseleccionar Todo</button>
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
												        		<td align="center"><button id="btnGuardarDocumentoProcedimiento">Guardar</button></td>
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
					</div>
					
					
					<div id="tabs-8" >
						<fieldset>
							<legend> Informaci&oacute;n del Procedimiento </legend>
							<table border="0" align="left" width="100%">
								<tr >
									<td align="right" colspan="2"  >
										<img id="imgPlayStop" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="adjudicarProcedimiento();"/>Adjudicar
										<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
										<img id="imgPlayStop" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="devolverProcedimiento();"/>Devolver
										<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
									</td>
								</tr>
								<tr align="left">
									<td colspan="2">
										[[<input name="cIdProcedimientoRequisitos" id="cIdProcedimientoRequisitos" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"/>]]&nbsp;<input name="desProcedimientoRequisitos" id="desProcedimientoRequisitos" type="text" maxlength="150" style="border: 0px solid black; width: 40em;" readonly="readonly"></input>  
									</td>								
								</tr>
								<tr align="left">
									<td colspan="2">
										<input name="cIdUnidadEjecutoraRequisitos" id="cIdUnidadEjecutoraRequisitos" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
									</td>								
								</tr>
								<tr align="left">
									<td colspan="2">
										Consolidado: [[<input name="cIdConsolidadoRequisitos" id="cIdConsolidadoRequisitos" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"></input>]]  
									</td>								
								</tr>
								<tr align="left">
									<td colspan="2">
										<img id="imgEstadoRequisitos"/><input name="nIdEstadoImagenRequisitos" id="nIdEstadoImagenRequisitos" type="text"  style="border: 0px solid black; width: 45em;"></input>  
									</td>								
								</tr>								
								<tr>
									<td>
										<br/>
									</td>
								</tr>
							</table>
						</fieldset>
						
						<table id="tblRequisitos" class="display" style="width:755px;" border="2">
							<thead>
								<tr>
									<th width="20px" >#</th>
									<th >Requisito</th>
									<th >Descripci&oacute;n</th>
									<th >Requerido</th>
									<th >Cumple</th>
									<th>Observaciones</th>
								 </tr>
							</thead>
						</table>
						
						<div style="text-align: center; margin-top: 20px;" >
							<button id="btnGuardarRequisitosProcedimiento">GUARDAR</button>
						</div>
					</div>
					<!-- inicia cotizacion -->

					<div id="tabs-4" >

						<div id="descripcionCotizacion">
						  <fieldset> <legend>Informaci&oacute;n del Procedimiento</legend>



							<table align="left" width="100%">
					 		  <tr >



								<td align="right" colspan="2"  >
									<img id="imgAdjudicarCotizacionProc" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="validaBotones('imgAdjudicarCotizacionProc');"/>Adjudicar
									<img id="imgDesiertoCotizacionProc" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="validaBotones('imgDesiertoCotizacionProc');"/>Desierto
									<img id="imgDevolverCotizacionProc" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="validaBotones('imgDevolverCotizacionProc');"/>Devolver
									<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
								</td>
						    </tr>
							<tr align="left"><td colspan="2"></td></tr>
							<tr align="left">
								<td colspan="2">
									<input name="cIdUnidadEjecutoraCotizacion" id="cIdUnidadEjecutoraCotizacion" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
								</td>		
							</tr>
							<tr align="left">														
								<td colspan="2">
									Consolidado: [[<input name="cIdConsolidadoCotizacion" id="cIdConsolidadoCotizacion" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"></input>]]  
								</td>								
							</tr>
							<tr align="left">

								<td colspan="2">
									<img id="imgEstadoCotizacion"/><input name="nIdEstadoImagenCotizacion" id="nIdEstadoImagenCotizacion" type="text"  style="border: 0px solid black; width: 45em;"></input>  
								</td>								
							</tr>
							<tr align="left">
							<td>
						 		<div id="tablaFechasProcedimientoCotizacion" style="width:100%;">
									<!-- Carga las fechas -->
								</div>
							</td>
						</tr>	
							<tr><td><br/></td></tr>
					    </table>
					 </fieldset>
				  </div><br/>
				  <div id="tablaProveedores">
					<table>
					  <tr>
						<td align="left"><button id="borraTodoProveedoresCotizacion" name="borraTodoProveedoresCotizacion">BORRA PROVEEDORES</button></td>
					  </tr>
					</table>
					<table id="tblProvedoresCotizaciones" class="display"  width="800px" border="2">
						<thead>
						  <tr>
							<th width="400px">RFC</th><th width="400px">Raz&oacute;n Social</th><th></th>     
						  </tr>
						</thead>
					</table>
				 </div>
				 <div id="formProveedores">
					<table border="0" align="center" >
						<tr><td align="left"><input type="text" id="rfcCotizacion" name="rfcCotizacion"style="width: 10em; border:0px; solid:black"  readonly="readonly"/>
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2"><input type="text" id="razonSocialCotizacion" name="razonSocialCotizacion"style="width: 30em; border:0px; solid:black"  readonly="readonly"/>
							</td>
						</tr>
						<tr>
							<td colspan="2"><input id="chk_pedidoAbierto" name="chk_pedidoAbierto" type="checkbox" onClick="validaContratoPedidoAbierto(this);" />&nbsp; Contrato o Pedido Abierto							
							</td>
						</tr>
						<tr><td align="right">Monto Bruto:</td>
							<td align="left"><input type="text" id="montoBrutoCotizacion" name="montoBrutoCotizacion"style="width: 30em; border:0px; solid:black"  readonly="readonly"/>
							</td>
						</tr>
						<tr><td align="right">Monto Neto:</td>
							<td align="left"><input type="text" id="montoNetoCotizacion" name="montoNetoCotizacion"style="width: 30em; border:0px; solid:black"  readonly="readonly"/>
							</td>
						</tr>
						<tr><td align="right">Porcentaje de IVA:</td>
							<td align="left"><input type="text" id="porcentajeIVACotizacion" name="porcentajeIVACotizacion" style="width: 5em; border:0px; solid:black"  readonly="readonly"/>%
							</td>
						</tr>
						<tr><td align="right">Tipo de Cambio:</td>
							<td align="left" colspan="2"><select id="cboCambioCotizacion" name="cboCambioCotizacion" onchange="cambiaTipoMoneda();"style="width: 15em;">
														<option value="" selected="selected"></option>
														</select>
							<input type="text" id="valorTipoCambio" name="valorTipoCambio" onKeyPress="return(onlyNumbers(event));" style="width: 5em; visibility: hidden; " />	
						    </td>
					    </tr>
						<tr><td><br/></td></tr>
						<tr><td width="33%">&nbsp;</td>
							<td  align="center"><button id="btnGuardarCotizacionProc">GUARDAR</button>&nbsp;&nbsp;&nbsp;
												<button id="btnPartidaCotizacionProc">NUEVA PARTIDA</button>
							</td>
							<td width="33%">&nbsp;</td>
						</tr>
					</table>
				  </div><br/>
				 <div id="tblProvCotizacion">
					<table>
						<tr><td  align="left"><button id="btnBorraPartidas">BORRAR TODAS LAS PARTIDAS</button>
							</td>
						</tr>
						</table>
						<div id="dtblProovedoresCotizacion">
							<table id="tblProovedoresCotizacion" border="2"  class="display" width="800"  height="50"  >
								<thead >
										<tr>
											<th width="70px">Partida</th>
											<th width="60px">CUCOP</th>
											<th width="200px">Descripcion</th>
											<th width="50px">Cantidad</th>
											<th width="150px">Precio Unitario</th>
											<th width="150px">Monto Bruto</th>
											<th width="40px">IVA</th>
											<th width="150px">Monto Neto</th>
											<th></th>
											<th></th>
										</tr>
									</thead>
								</table>
							</div>
							<div id="dtblProovedoresCotizacion2">	
								<table id="tblProovedoresCotizacion2" border="2"  class="display" width="800"  height="50"  >
									<thead >
										<tr>
											<th width="70px">Partida</th>
											<th width="60px">CUCOP</th>
											<th width="200px">Descripcion</th>
											<th width="50px">Cantidad</th>
											<th width="150px">M&iacute;nimo</th>
											<th width="150px">M&aacute;ximo</th>
											<th width="150px">Monto Bruto</th>
											<th width="40px">IVA</th>
											<th width="150px">Monto Neto</th>
											<th></th>
											<th></th>
										</tr>
									</thead>
								</table>
							</div>
					</div><br/>
					<div id="tblPartidasCotizacion">
						<fieldset><legend>Partidas</legend>

							<table>
							  <tr><td  align="left" ><button id="btnSeleccionaPartidas">TODAS LAS PARTIDAS</button></td></tr>
							</table>
							<table id="tblPartidas" border="2"  class="display" width="800"  height="50"  >

								<thead >
									<tr>
										<th >Partida</th>
										<th >CUCOP</th>
										<th >Descripcion</th>
										<th >Cantidad</th>
									</tr>
								</thead>
							</table>
						</fieldset>
					</div>		
				</div>
				<!-- termina cotizacion -->
					<!-- Preguntas -->
					<div id="tabs-5" align="center">
					<fieldset>
						<legend>Informaci&oacute;n del Procedimiento</legend>
						<table align="left" width="100%">
						<!--  <tr >
								<td align="right" colspan="2"  >
									<img id="imgAdjudicarCotizacionProc" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="validaBotones('imgAdjudicarCotizacionProc');"/>Adjudicar
									<img id="imgDesiertoCotizacionProc" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="validaBotones('imgDesiertoCotizacionProc');"/>Desierto
									<img id="imgDevolverCotizacionProc" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="validaBotones('imgDevolverCotizacionProc');"/>Devolver
									<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
								</td>
						    </tr> -->
						  <tr align="left">
							<td  colspan="2">
								[[<input name="cIdProcedimientoProcPreguntas" id="cIdProcedimientoProcPreguntas" type="text" size ="10" style="border: 0px solid black;" readonly="readonly">]]</input>&nbsp;<input name="desProcedimientoPreguntas" id="desProcedimientoPreguntas" type="text" maxlength="150" style="border: 0px solid black; width: 40em;" readonly="readonly"></input>  
							</td>								
						  </tr>
						  <tr align="left">
							<td colspan="2">
								<input name="cIdUnidadEjecutoraPreguntas" id="cIdUnidadEjecutoraPreguntas" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
							</td>								
						  </tr>
						  <tr align="left">
							<td colspan="2">
								Consolidado: [[<input name="cIdConsolidadoPreguntas" id="cIdConsolidadoPreguntas" type="text" size ="10" style="border: 0px solid black;"`readonly="readonly"></input>]]  
							</td>								
						  </tr>
						  <tr align="left">
							<td colspan="2">
								<img id="imgEstadoPreguntas"/><input name="nIdEstadoImagenPreguntas" id="nIdEstadoImagenPreguntas" type="text"  style="border: 0px solid black; width: 45em;" readonly="readonly"></input>  
							</td>								
						  </tr>
						  <tr align="left">
							<td colspan="2">
								Tipo de Proceso: [[<input name="lbltipoProcesoPreguntas" id="lbltipoProcesoPreguntas" type="text" size ="18" style="border: 0px solid black;" readonly="readonly"></input>]]
							</td>								
						 </tr>		
						 <tr><td><br/></td></tr>
					</table>
					</fieldset><br/>
						<fieldset>
						<legend>Proveedores</legend>
						<table id="tblProvedoresPreguntas" class="display"  width="1000px" border="2">
							<thead>
							  <tr>
							  	<th></th>
							  	<th></th>
							  	<th></th>
							  	<th></th>
							  	<th></th>
								<th width="400px">RFC</th>
								<th width="400px">Raz&oacute;n Social</th>
								<th>Boton</th>    
							  </tr>
							 </thead>
						</table>
					</fieldset>	
					<br/>
					<br/>
					<table>
					<tr><td>No de Preguntas &nbsp;<input type="text" name="numPreguntas" id="numPreguntas" />&nbsp;&nbsp;
					<button name="btnAgregaPreguntas" id="btnAgregaPreguntas" onclick="agregaPreguntas();">Agregar</button>
					</td></tr></table>
					<fieldset>
						<legend>Preguntas</legend>
						<input name="lblProveedorPreguntas" id="lblProveedorPreguntas" type="text" size="80" style="border: 0px solid black;" readonly="readonly"></input>
						<table id="tblProvedoresPreguntasRespuestas" class="display"  width="1000px" border="2">
							<thead>
							  <tr>
							    <th></th>
							    <th></th>
							    <th></th>
								<th width="30px">No. de pregunta</th>
								<th width="400px">Pregunta</th>
								<th width="400px">Respuesta</th>    
								<th width="400px">Elimina</th> 
							  </tr>
							 </thead>
						</table>
						<table>
							<tr>
							  <td><button name="btnBorrarTodasPreguntas" id="btnBorrarTodasPreguntas" onclick="borrarTodasPreguntas();">Eliminar Preguntas</button></td>
							  <td><button name="btnGuardaPreguntas" id="btnGuardaPreguntas" onclick="guardaPreguntas();">Guardar Preguntas</button></td>
							</tr>
						</table>
						<table>
							<tr>
								<td><button name="btngeneraRptAclaracion" id="btngeneraRptAclaracion" onclick="generaRptAclaracion();">Reporte</button></td>
							</tr>
						</table>
					</fieldset>
					</div>
					<!--  termina preguntas -->

					<!--  inicia evaluacion -->
					<div id="tabs-7" align="center">
					<fieldset>
						<legend>Informaci&oacute;n del Procedimiento</legend>
						<table align="left" width="100%">
						 <tr>
								<td align="right" colspan="2"  >
									<img id="imgAdjudicarEvaluacionProc" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="validaBotones('imgAdjudicarEvaluacionProc');"/>Adjudicar
									<img id="imgDesiertoEvaluacionProc" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="validaBotones('imgDesiertoEvaluacionProc');"/>Desierto
									<img id="imgDevolverEvaluacionProc" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="validaBotones('imgDevolverEvaluacionProc');"/>Devolver
									<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
								</td>
						    </tr>
						  <tr align="left">
						      <td  colspan="2">
						         [[<input name="cIdProcedimientoProcEvaluacion" id="cIdProcedimientoProcEvaluacion" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"/>]]&nbsp;<input name="desProcedimientoEvaluacion" id="desProcedimientoEvaluacion" type="text" maxlength="150" style="border: 0px solid black; width: 40em;" readonly="readonly"></input>  
							  </td>								
						  </tr>
						  <tr align="left">
							  <td colspan="2">
								   <input name="cIdUnidadEjecutoraEvaluacion" id="cIdUnidadEjecutoraEvaluacion" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
							  </td>								
						  </tr>
						  <tr align="left">
							  <td colspan="2">
								   Consolidado: [[<input name="cIdConsolidadoEvaluacion" id="cIdConsolidadoEvaluacion" type="text" size ="10" style="border: 0px solid black;"`readonly="readonly"></input>]]  
							  </td>								
						  </tr>
						  <tr align="left">
							  <td colspan="2">
								   IVA: [[<input name="porcentajeIVAEvaluacion" id="porcentajeIVAEvaluacion" type="text" size ="5" style="border: 0px solid black;"`readonly="readonly">%</input>]]   
							  </td>								
						  </tr>
						  <tr align="left">
							  <td colspan="2">
							    <img id="imgEstadoEvaluacion"/><input name="nIdEstadoImagenEvaluacion" id="nIdEstadoImagenEvaluacion" type="text"  style="border: 0px solid black; width: 45em;" readonly="readonly"></input>  
							  </td>								
						 </tr>
						 <tr align="left">
							 <td colspan="2">
							    Tipo de Proceso:[[<input name="lbltipoProcesoEvaluacion" id="lbltipoProcesoEvaluacion" type="text" size ="18" style="border: 0px solid black;" readonly="readonly"></input>]]
 							 </td>								
						 </tr>								
						 <tr><td><br/></td></tr>
					  </table>
					</fieldset>
				<!--  tabla que muestra los proveedores de la tabla mprocedimientoadjudicacion   -->
					<fieldset>
							<table id="tblProveedoresEvaluacion" border="2"  class="display" width="800"  height="50"  >
								<thead>
								  <tr>
						  			<th></th>
								  	<th></th>
								  	<th></th>
								  	<th></th>
								  	<th></th>
									<th width="400px">RFC</th>
									<th width="400px">Raz&oacute;n Social</th>
									<th></th>
								  </tr>
								</thead>
							</table>
														
					</fieldset>
					<br/><br/>
					<fieldset>
					<!--  tabla que muestra la relacion entre la partida y los proveedores seleccionados -->
						<legend>Partida-Proveedor</legend>
						    <br/>
						    <div style="width:100%;text-align:left;">
						    <input name="lblPartidaProveedor" id="lblPartidaProveedor" type="text" size="80" style="border: 0px solid black;" readonly="readonly"/>
						    <br/>
						    <input name="chk_pedidoAbiertoCompleto" id="chk_pedidoAbiertoCompleto" type="checkbox" onClick="validaContratoPedidoAbiertoCompleto(this);"/>Pedido o Contrato Abierto
						    </div>
						    <table>
						    	<tr>
						    		<td colspan="2">
							   		  <input name="lblEstadoPartida" id="lblEstadoPartida" type="text" size ="38" style="border: 0px solid black;" readonly="readonly"></input>
 							 		</td>
 							 	</tr>
						    	<tr>
						    		<td><button name="btnSeleccionaTodos" id="btnSeleccionaTodos" onclick="SeleccionaTodosEvaluacion();">Todos Cumplen E.T.</button></td>
						    		<td>&nbsp;</td>
									<td><button name="btnCopiaObservaciones" id="btnCopiaObservaciones" onclick="CopiarObservaciones();">Copiar Observación</button>
								</tr>
						    </table>
						    <div id= "div_tblPartidasProveedorEvaluacion">
								<table id="tblPartidasProveedorEvaluacion" class="display" border="1" width="800"  height="50">
									<thead >
										<tr>
											<th>Ganador</th>
											<th>Ganador Sugerido</th>
							                <th></th> 
							                <th>L&iacute;nea Consolidado</th>
							                <th>Descripci&oacute;n</th>
									  		<th>Cantidad</th>
									  		 <th>Unidad de Medida</th> 									  		
											<th>Precio Unitario</th>
											<th>Tipo Cambio</th>
											<th>Monto Bruto</th>
											<th>Monto Neto</th> 
											<th>Cumple evaluaci&oacute;n T&eacute;cnica</th>
											<th>Observaciones</th>
											<th>No cotiza</th>
											<th>Elimina</th>											
											<th></th>
										</tr>
									</thead>
								</table>
							</div>
							<div id= "div_tblPartidasProveedorEvaluacion2">
								<table id="tblPartidasProveedorEvaluacion2" class="display" border="1" width="800"  height="50">
									<thead >
										<tr>
											<th>Ganador</th>
											<th>Ganador Sugerido</th>
							                <th></th> 
							                <th>L&iacute;nea Consolidado</th>
							                <th>Descripci&oacute;n</th>
									  		<th>Cantidad</th>
									  		 <th>Unidad de Medida</th> 									  		
											<th>Tipo Cambio</th>
											<th>M&iacute;nimo</th>
											<th>M&aacute;ximo</th>
											<th>Monto Bruto</th>
											<th>Monto Neto</th> 
											<th>Cumple evaluaci&oacute;n T&eacute;cnica</th>
											<th>Observaciones</th>
											<th>No cotiza</th>
											<th>Elimina</th>											
											<th></th>
										</tr>
									</thead>
								</table>
							</div>
							<table>
								<tr>
								    <td><button name="btnGuardaProcedimientoCompleto" id="btnGuardaProcedimientoCompleto" onclick="guardaProcedimientoCompleto();">Guardar Tabla Comparativa</button></td>
								</tr>

							</table>
					</fieldset>
					
					<br/><br/>
					
					<fieldset>
					<legend>Firmantes</legend>
					<table width="97%" align="left">									
									<tr>
										<td>
											<table align="center" id="tblFirmantesTblComparativa" width="100%" class="display">
									        	<thead>
									        		<tr>
									        			<th>#</th>
									        			<th>NOMBRE</th>
									        			<th>PUESTO</th>
									        			<th>CONDICI&Oacute;N</th>									        			
									        		</tr>
									        	</thead>
									        </table>
										</td>
									</tr>
								</table>
						</fieldset>
						<fieldset>
							<legend>Cat&aacute;logo de Firmantes</legend>
								<table width="97%" align="left">
									<tr>
										<td>
											<table align="center" id="tblCatalogoFirmantes" width="100%" class="display">
									        	<thead>
									        		<tr>
									        			<th>UNIDAD EJECUTORA</th>
									        			<th>#</th>
									        			<th>NOMBRE</th>
									        			<th>PUESTO</th>
									        		</tr>
									        	</thead>
									        </table>
										</td>
									</tr>
								</table>
						</fieldset>
						<table>
							<tr>
								<td><input type="radio" name="reporte" value="tablaComparativa" checked>Reporte tabla Comparativa</td>
								<td><input type="radio" name="reporte" value="partidasComparativa">Reporte Partidas Comparativa </td>
							</tr>
						</table>
						<button name="btngeneraTablaComparativa" id="btngeneraTablaComparativa" onclick="generaTablaComparativa();">Reporte</button>
						
				</div>
				<!-- termina evaluacion -->
				<!-- inicia archivos -->
				<div id="tabs-9" >
					<div id="agregarArchivos">
						<fieldset><legend>Informaci&oacute;n del Procedimiento</legend>
							<table align="left" width="100%">						    
								<tr align="left">
									<td  colspan="2">
										[[<input name="cIdProcedimientoArchivos" id="cIdProcedimientoArchivos" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"/>]]&nbsp;<input name="desProcedimientoArchivos" id="desProcedimientoArchivos" type="text" maxlength="150" style="border: 0px solid black; width: 30em;" readonly="readonly"></input>  
									</td>								
								</tr>
						   		<tr align="left">
									<td colspan="2">
										<input name="cIdUnidadEjecutoraArchivos" id="cIdUnidadEjecutoraArchivos" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
									</td>								
								</tr>
								<tr align="left">
									<td colspan="2">
									Consolidado: [[<input name="cIdConsolidadoArchivos" id="cIdConsolidadoArchivos" type="text" size ="10" style="border: 0px solid black;"`readonly="readonly"></input>]]  
									</td>								
								</tr>
								<tr align="left">
									<td colspan="2">
										Tipo de Proceso: [[<input name="lbltipoProcesoArchivos" id="lbltipoProcesoArchivos" type="text" size ="18" style="border: 0px solid black;" readonly="readonly"></input>]]
									</td>
								</tr>	
								<tr align="left">
									<td colspan="2">
										<img id="imgEstadoProovedor"/><input name="nIdEstadoImagenArchivos" id="nIdEstadoImagenArchivos" type="text"  style="border: 0px solid black; width: 45em;" readonly="readonly"></input>  
									</td>								
								</tr>	
								<tr align="left">
									<td>
						 				<div id="tablaFechasProcedimientoArchivos" style="width:100%;">
											<!-- Carga las fechas -->
										</div>
									</td>
								</tr>
								<tr>
									<td>
						 				<br/>
									</td>
								</tr>
							</table>
					 	</fieldset>
					 	<br>
					 	<table>
							<tr>
		  						<td style="width:100px;">
		  							Tipo:
		  						</td>
		  						<td>
		  							<select name="tipoArchivo" id="tipoArchivo" onChange="enviaDatosArchivoProcedimiento();">
										<option value="aperturaProcedimiento" selected>Apertura</option>
										<option value="falloProcedimiento">Fallo</option>
									</select>
		  						</td>
		  					</tr>
		  				</table>
					 	<iframe name="iframe-archivos" width="100%" marginwidth="0" height="100%" marginheight="0" align="top" scrolling="auto" frameborder="0"></iframe>
					</div>
				</div>
			</div>
    	</div>
    </form>
    <form name="formArchivo" id="formArchivo" action="ArchivosProcedimiento.jsp" target="iframe-archivos">
    	<input type="hidden" id="cEjercicioArchivo" name="cEjercicioArchivo" />
    	<input type="hidden" id="cIdProcedimientoArchivo" name="cIdProcedimientoArchivo" />
    	<input type="hidden" id="cTipoArchivo" name="cTipoArchivo"/>
    	<input type="hidden" id="cMensaje" name="cMensaje" value=""/>
    	<input type="hidden" id="cIdConsolidadoArchivo" name="cIdConsolidadoArchivo" value=""/>
	</form>
  </body>
</html>