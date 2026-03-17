<%@page language="java" import="java.util.*"contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.ArchivoExcel"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%! private static Logger log = LoggerFactory.getLogger("com.syc.plantillas.casos.integraAdecuaciones.jsp"); %>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
	String aEjercicioFiscal = ""; //c1.getTime().getYear();
	String UR="";
	
	int id_oper = -1;
	int nFolio =0;
	int nDesIntegrado=0;

	String mensaje = "";
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	UR = usuario.getU_UR();
	
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (c.getCasoDato("MENSAJE").getValor() != null) {
		mensaje = c.getCasoDato("MENSAJE").getValor();
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace("'", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	mensaje=Util.encodeJS(mensaje);

	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
		
	nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

	//Valida Centro de Costos
	Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
	String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
	try{
		aEjercicioFiscal = adecua.obtenEjercicioFiscal();
	}catch (Exception ex) {
			log.warn(ex);
			mensaje = Util.encodeJS(ex.getMessage());
	}
	
	// Autorizacion
	if (request.getParameter("cAplicaDocto") != null && "SI".equals(request.getParameter("cAplicaDocto"))) {
			System.out.println("antes de validar Aplicacion");
				String ipNombreServidor = java.net.InetAddress.getByName(request.getServerName()).toString();
				String[] ipServidor = ipNombreServidor.split("/");
				if (ipServidor[1].equals(GestionInterface.SYS_IP_PRODUCCION))
					adecua.correoProduccion=true;
				//adecua.autorizaIntegracion(nFolio, nNumSicop, cRecMotivSicop, nNumMAP, cRecMotivMAP, m, prefixPath, usuario, cSuperReduccion, cCentroContable, c);
	}
	
	mensaje= Util.encodeJS(mensaje);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Integración de Adecuaciones Presupuestales Foraneas</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle"> 
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../js/masks.js"></script>
		<script type="text/javascript" charset="utf-8">
		
		//Las siguientes acciones se usan en el srvlet para identificar lo que se va a realizar con las llamadas ajax
		//accion 1 INTEGRA
		//accion 2 APLICA CONTABLEMENTE
		//accion 3 DESINTEGRA
		//accion 4 CAPTURA JUSTIFICACIONES 
		//accion 5 CANCELA FIAF
		//accion 6 AUTORIZA FIAF
		//*********************************************************************************************************//
		var hayDatos = false;

		var oTablePA;
		
		$(document).ready(function() {
			
			queryFormPost("iadeFIAF", {async:false});
			
			if($("#folioIADE").val()!=""){
				alert("Advertencia: Esta FIAF está incluída en la Integración con Folio IADE-A02-" + $("#folioIADE").val()
				    + " para realizar una Autorización o Cancelación se requiere desintegrarla para poder realizar cualquiera de estas opciones de Forma individual.");
			}
			
			if(parent.document.getElementById("pb_cancel"))
				parent.document.getElementById("pb_cancel").disabled=true;
			
			$("#btnCapturaJ").button();
			$("#btnAgregaJ").button();
			$("#btnAplica").button();
			
			$('input[name=cCancelar]').removeAttr('checked', 'checked');
			$('.currency').blur(function(){
				$('.currency').formatCurrency();
			});
			oTableBusIntg = $('#grdBuscaIntegracion').dataTable( );

			$('#grdBuscaIntegracion').dataTable(
				{
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": true,
					"sScrollY": 10,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}
				} );
				
			oTablePA = $('#grdIntegra').dataTable({
				"bPaginate": false,
       			"bLengthChange": true,
       			"bFilter": true,
       			"bSort": false,
       			"bInfo": true,
       			"bAutoWidth": false,
				"sScrollY": 200,
				"sScrollYInner": "60%",
				"bJQueryUI": true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers",
				"sScrollX": "100%",
				"sScrollXInner": "110%",
				"bScrollCollapse": true,	
				/*"bServerSide": true,   
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_AdecuacionDetFIAF&qw=nFolioFIAF="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>,
				aoColumns: [
						{ "bSortable":false,sName: "nFolioAdecuacion" },
						{ "bSortable":false,sName: "cTipoAdecuacion" },
						{ "bSortable":false,sName: "nNivel"},
						{ "bSortable":false,sName: "importe"},
						{ "bSortable":false,sName: "U_LOGIN"},
						{ "bSortable":false,sName: "fCarga"},
						{ "bSortable":false,sName: "cUnidadResponsable"}
					],*/
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtered from _MAX_ total entries)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Filtro:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}
				
			});
			
			/* Add a click handler to the rows - this could be used as a callback */
			$("#grdIntegra tbody").click(function(event) {
				$(oTablePA.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
					var aPos = oTablePA.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablePA.fnGetData( aPos[0] );
				});
				$(event.target.parentNode).addClass('row_selected');
			});
			
			$(function(){
				$('#dlgError').dialog({
				autoOpen: false,
					width: 1200,
					heigth: 1800
				});
			});
			
			$(function(){
				$('#divBuscaIntegracion').dialog({
				autoOpen: false,
					width: 1200,
					heigth: 900
				});
			});
			
		 	$(function() {		
			    $('#dialogMotor').dialog({
			      	autoOpen: false,
		  			width: 900,
		 			heigth: 2900
			    });
			});
		 	
		 	$(function() {		
			    $('#justificaciones').dialog({
			      	autoOpen: false,
		  			width: 900,
		 			heigth: 2900,
		 			close: function(ev, ui) 
	                	{
							agregaJustificaciones();
	                	}
			    });
			});
					
			$("#DPC_fFechaSicop").datepicker({
				showOn:"button",
				dateFormat:"dd/mm/yy",
				buttonImage:"../Generador/images/calendar.gif",
				buttonImageOnly:true
			});
	
			$("#DPC_fFechaMAP").datepicker({
				showOn:"button",
				dateFormat:"dd/mm/yy",
				buttonImage:"../Generador/images/calendar.gif",
				buttonImageOnly:true
			});
			if(<%=id_oper%>==1){
				var muestraDesintegra=false;
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "ADECUACIONENCABEZADOFIAF", Campos:"nFolioFIAF", Param:"nFolioFIAF="+<%=nFolio%>, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
					for (var i = 0; i < j.length; i++){
						fnClickAddRowB(j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,"");
							muestraDesintegra = true;
					}
					if(muestraDesintegra){
						$("#bBusca").hide();
						$("#divDesintegrar").show();
						$('input[name=cCancelar]').removeAttr('checked', 'checked');
					}else{
						$("#bBusca").show();
						$("#divDesintegrar").hide();
					}
				});
			}else{
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "ADECUACIONENCABEZADOFIAF", Campos:"nFolioFIAF", Param:"nFolioFIAF="+<%=nFolio%>, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
					for (var i = 0; i < j.length; i++){
						fnClickAddRowB(j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,"");
					}
				});
			}
		});

		
		function exportaIntegracion(){
			var num = $("#exporta").val();
			$("#ExportaExcel").attr('action','../gstnmngr/FIAFServlet?accion=7&documento='+num +"&nFolioFIAF=" + $("#nFolioFIAF").val());
			$("#ExportaExcel").submit();
		}

		function onSubmit(id_oper){
			var p = window.parent;
			var valida_campos = true;
			try{
				if (id_oper==1){
					parent.document.getElementById("pb_save").disabled=true;
					p.gestion.setFolio($("#nFolioFIAF").val());
					p.gestion.setOperador($("#OPERADOR").val());
					p.gestion.setFechaDocumento("<%=today %>");
					p.gestion.setEjercicioFiscal("<%=aEjercicioFiscal %>");
					p.gestion.setConceptoMov("Adecuacion Presupuestal");
					p.gestion.setMoneda("MXP");
					if($("#cCancelar:checked").length>0 && oTablePA.fnGetData().length>0 && integrado())
						desintegra();
					else if($("#cCancelar:checked").length==0 && oTablePA.fnGetData().length>0 && !integrado()){
						integra();
						integrado();
					}
				}
				//aplicaCont();
			}catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}

		function onPostSubmit(id_oper){
			obtenJustificaciones();
			if($.trim($("#justificacionA").val())!="" && $.trim($("#justificacionR").val())!="" && $.trim($("#justificacionNormativa").val())!="" && id_oper==1){
				//parent.document.getElementById("pb_send").disabled=false;
			}
			else if(id_oper==1){
				alert("Favor de llenar las justificaciones antes de enviar");
				return false;
			}
			
			return true;
		}

		function onLoadPlantilla(id_oper){
			if(<%=c.getIdGabinete() == -1%>)
				parent.document.getElementById("pb_save").click();
			integrado();
			obtenJustificaciones();
			/*if(<%=c.getIdGabinete() == -1%>)
				$("#Exportar").attr('disabled','disabled');
			*/
			if(id_oper!=1)
				$("#btnAplica").hide();
			
			if(id_oper==2)
				parent.document.getElementById("pb_cancel").disabled=true;
			
			if(id_oper>=2){
				$("#btnAgregaJ").hide();
				$("#divDesintegrar").hide();
				$("#bBusca").hide();
				$("#justificacionA").attr('disabled','disabled');
				$("#justificacionR").attr('disabled','disabled');
				$("#justificacionNormativa").attr('disabled','disabled');
			}
			if(id_oper==2 || id_oper==1)
				parent.document.getElementById("pb_send").disabled=true;
		}
	
		function onPostDisplay(id_oper){
			guardaExp();
		}
	
		function ResponsableSiguiente(id_oper){			
			//if(id_oper==1 && document.formIntegracion.cCancelar.checked)
					//return "CONSULTA_FIAF";
			if(id_oper==1 )
				return "JEFATURA_ADECUACIONES";
			if(id_oper==2){
				return "CONSULTA_FIAF";
			}
		}
	
		function OperacionSiguiente(id_oper){
			//if(id_oper==1 && document.formIntegracion.cCancelar.checked)
					//return "consulta_fiaf";
			if(id_oper==1)
				return "validar_normatividad";
			if(id_oper==2){
				return "consulta_fiaf";
			}
		}
	
		function guardaExp(){
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_save").click();
		}

		function aplica(){
			obtenJustificaciones();
			if($.trim($("#justificacionA").val())=="" || $.trim($("#justificacionR").val())=="" || $.trim($("#justificacionNormativa").val())==""){
				alert("Favor de proporcionar todas las justificaciones");
				return false;
			}
		
			if(!integrado()){
				alert("No hay adecuaciones integradas");
				return false;
			}
			var folio = <%=nFolio%>;
			$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: "../gstnmngr/FIAFServlet",
					data:{accion:2, "folioFIAF":folio},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
						parent.document.getElementById("pb_save").disabled=true;
		   				$.unblockUI();
					},
					error: function (par,e) {alert(e);}
				});
		   		//parent.document.getElementById("frmLeave").submit();
		}
	
		function cancela(){
			if($.trim($("#motivoRechazo").val())==""){
				alert("Favor de proporcionar el motivo de cancelación");
				return false;
			}
		
			var folio = <%=nFolio%>;
			var motivo = $("#motivoRechazo").val();
			$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: "../gstnmngr/FIAFServlet",
					data:{accion:5, "folioFIAF":folio, "motivoRechazo":motivo},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   				$.unblockUI();
					},
					error: function (par,e) {alert(e);}
				});
		   		parent.document.getElementById("frmLeave").submit();
		}
		
		function autoriza(){
			if($.trim($("#DPC_fFechaMAP").val())=="" || $.trim($("#DPC_fFechaSicop").val())=="" || $.trim($("#nNumSicop").val())=="" || $.trim($("#nNumMAP").val())==""){
				alert("Favor de proporcionar los datos Sicop y MAP");
				return false;
			}
		
			var foliosAdecuaciones = new Array();
			var arrData = $("#grdIntegra").dataTable().fnGetData();
			for(var i = 0; i < arrData.length; i++){
				foliosAdecuaciones[i] = arrData[i][0];
			}
			var folio = <%=nFolio%>;
			var fMap = $("#DPC_fFechaMAP").val();
			var nMap = $("#nNumMAP").val();
			var fSicop = $("#DPC_fFechaSicop").val();
			var nSicop = $("#nNumSicop").val();
			$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: "../gstnmngr/FIAFServlet",
					data:{accion:6, "folioFIAF":folio, "fMap":fMap,"nMap":nMap,"fSicop":fSicop,"nSicop":nSicop,"foliosAdec":foliosAdecuaciones},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   				$.unblockUI();
					},
					error: function (par,e) {alert(e);}
				});
		   		parent.document.getElementById("frmLeave").submit();
		}
		
		function MostrarDialogError() {
			$('#dlgError').dialog('option', 'modal', true).dialog('open');
			return true;
		}
		function buscaAIntegrar(){
			if($("#nivelAdecu").val()=="" && $("#tipoAdecu").val()==""){
				alert("Favor de llenar al menos uno de los dos filtros");
				return;
			}
			$('input[name=cCancelar]').removeAttr('checked', 'checked');
		 	var cDataQuery=" cUnidadResponsable = '<%=UR%>'";
			
		 	if ($("#nivelAdecu").val() != ""){
				cDataQuery+=" AND nNivel = '"+$("#nivelAdecu").val()+"' "
			} 
			if ($("#tipoAdecu").val() != ""){
				cDataQuery+=" AND cTipoAdecuacion = '"+$("#tipoAdecu").val()+"' "
			} 
			//alert(cDataQuery);
			$("#grdBuscaIntegracion").dataTable({
				"bProcessing": true,
				"bServerSide": true,
				"bDestroy": true,
				"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_AdecuacionFIAF&qw="+cDataQuery,
				"bJQueryUI": true,
				"sScrollX": "1000",
				"sScrollXInner": "100%",
				"bPaginate": false,
				"bAutoWidth": false,
				"bInfo": true,
				"aoColumns": [
					{ sName: "cDescartar"},
					{ sName: "nFolioAdecuacion"},
					{ sName: "cTipoAdecuacion" },
					{ sName: "nNivel" },
					{ sName: "mImporte"},
					{ sName: "cUsuarioCreador"},
					{ sName: "fAplicacion"},
					{ sName: "cUnidadResponsable"}
    			]
			});
			$('#divBuscaIntegracion').dialog('option', 'modal', true).dialog('open');
			
			//document.formIntegracion.submit();
		}
		function fnClickAddRowB(B,C,D,E,F,G,H,I) {
				$('#grdIntegra').dataTable().fnAddData([B,C,D,E,F,G,H,I]);
		}
		
		function descartar(row){
				var total = 0.00;
				var valorEliminado = 0.00;
				var evento = row.find('td:eq(3) font').html();
				var index = row.index();
				aTrs = oTablePA.fnGetNodes();
				index = row.index();
				oTablePA.fnDeleteRow( index );
				$(row).remove();
				return false;
		}

	
	function fnIntegrar(){
		var info="";
		parent.document.getElementById("pb_cancel").disabled=true;
		$('#grdBuscaIntegracion input:checked').each(function(idx, elm){
			var B = $(this).parent('td').parent('tr').find('td:eq(1)').html();
			var C = $(this).parent('td').parent('tr').find('td:eq(2)').html();
		 	var D = $(this).parent('td').parent('tr').find('td:eq(3)').html();
		  	var E = $(this).parent('td').parent('tr').find('td:eq(4)').html();
		  	var F = $(this).parent('td').parent('tr').find('td:eq(5)').html();
		  	var G = $(this).parent('td').parent('tr').find('td:eq(6)').html();
		  	var I = $(this).parent('td').parent('tr').find('td:eq(7)').html();
		  	var H = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Descartar\" onClick=\"descartar($(this).parent('td').parent('tr'));\">";
		  	
		  	fnClickAddRowB(B,C,D,E,F,G,I,H);
		});
		$('#divBuscaIntegracion').dialog('option', 'modal', true).dialog('close');	
		parent.document.getElementById("pb_save").disabled=false;
	}
	
		function integra(){
			var i=0;
			var foliosAdecuaciones = new Array();
			var folio = <%=nFolio%>;
			var arrData = $("#grdIntegra").dataTable().fnGetData();
			for(var i = 0; i < arrData.length; i++){
				foliosAdecuaciones[i] = arrData[i][0];
			}
			if(foliosAdecuaciones.length>0){
				$.blockUI({message: "Procesando espere ......"});
			   	$.ajax({
			   		datatype:"html",
					type: "POST",
					url: "../gstnmngr/FIAFServlet",
					data:{"accion":1, "foliosAdec":foliosAdecuaciones, "folioFIAF":folio},
					success: function (data,textStatus){
						$("#bBusca").hide();
						$("#divDesintegrar").show();
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
			   			$.unblockUI();
			   			//parent.document.getElementById("pb_send").disabled=false;
						parent.document.getElementById("pb_save").disabled=true;
						$("#cCancelar").attr('disabled','disabled');	
					},
					error: function (par) {alert('<%=mensaje%>');}
				});
			 }
			hayDatos=true;
		}
		
		function desintegra(){
			if (confirm("Favor de confirmar si realmente desea desintegrar.")){
				var foliosAdecuaciones = new Array();
				var arrData = $("#grdIntegra").dataTable().fnGetData();
				for(var i = 0; i < arrData.length; i++){
					foliosAdecuaciones[i] = arrData[i][0];
				}
				if(foliosAdecuaciones.length>0){				
					var folio = <%=nFolio%>;
					$.blockUI({message: "Procesando espere ......"});
					 $.ajax({
					   	datatype:"html",
						type: "POST",
						url: "../gstnmngr/FIAFServlet",
						data:{"accion":3, "folioFIAF":folio, "foliosAdec":foliosAdecuaciones},
						success: function (data,textStatus){
							oTablePA.fnClearTable();
							$("#bBusca").show();
							$("#divDesintegrar").hide();
							$("#mensajeMotor").val(data);
							$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
					   		$.unblockUI();
						},
						error: function (par) {alert('<%=mensaje%>');}
					});
				}else
					alert("no hay adecuaciones integradas");
			}
		}
		
		function integrado(){
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "ADECUACIONENCABEZADOFIAF", Campos:"nFolioFIAF", Param:"nFolioFIAF="+<%=nFolio%>, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				if(j.length>0)
					hayDatos=true;
			});
			return hayDatos;
		}
	
		function capturaJustificaciones(){
			if(!integrado()){
				alert("Favor de integrar primero sus adecuaciones");
				return false;
			}
				
			obtenJustificaciones();
			$('#justificaciones').dialog('option', 'modal', true).dialog('open');
		}
		
		function agregaJustificaciones(){
			if(<%=id_oper%>==1){
			var folioFIAF = <%=nFolio%>;
			var justificacionA = $('#justificacionA').val();
			var justificacionR = $('#justificacionR').val();
			var justificacionNormativa = $('#justificacionNormativa').val();
			$.blockUI({message: "Procesando espere ......"});
			   	$.ajax({
			   		datatype:"html",
					type: "POST",
					url: "../gstnmngr/FIAFServlet",
					data:{"accion":4,  "folioFIAF":folioFIAF, "justificacionA":justificacionA, "justificacionR":justificacionR, "justificacionNormativa":justificacionNormativa},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
			   			$.unblockUI();
					},
					error: function (par) {}
				});
		}
			 //$('#justificaciones').dialog('close');  	
		}
		
		function obtenJustificaciones(){
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "FIAFENCABEZADO", Campos:"nFolioFIAF", Param:"nFolioFIAF="+<%=nFolio%>, Order:"", MaxReg: "10", ajax: 'true'}, function(j){
				if(j.length>0){
					$("#justificacionA").val(j[0].Col9);
					$("#justificacionR").val(j[0].Col10);
					$("#justificacionNormativa").val(j[0].Col11);
				}
			});
		}
	</script>

	</head>

	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1>Integración de Adecuaciones Presupuestarias Foráneas</h1>
			<form id="ExportaExcel" name="ExportaExcel" method="POST" target="_blank"><input type="hidden" id="folioFIAF" name="folioFIAF" value="<%=nFolio %>"/></form>
						
			<form id="formIntegracion" name="formIntegracion" method="post" >
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=usuario.getLogin() %>"/>
					<input type="hidden" id="nfolio" name="nfolio" value="<%=nFolio %>"/>
				<% if (id_oper==2 ) {//if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()) && id_oper==2 ) {%>
					<div style="text-align: center;"><input type="button" id="Cancelar" value="Cancelar Documento" name="Cancelar Documento" onclick="cancela();"></input><br/>
					Motivo de cancelaci&oacute;n: <input type="text" id="motivoRechazo" name="motivoRechazo" size="80" maxlength="200"><br/>
					<input type="button" id="Autoriza" value="Autorizar Documento" onclick="autoriza();"></input></div><br/>
				<% } %>
				
			<% if (id_oper == 2) { %>
				<table>
					<tr>
						<td>Numero de Autorizacion SICOP:<input id="nNumSicop" name="nNumSicop" value="" maxlength="20"size="20" ></input></td>
						<td width="100px"></td><td>Fecha SICOP:<input type="text" id="DPC_fFechaSicop"  name="DPC_fFechaSicop"  datepicker_format="DD/MM/YYYY" datepicker="false" maxlength="10" size="10"/></td>
					</tr>
					<tr>
						<td>Numero de Autorizacion MAP:<input id="nNumMAP" name="nNumMAP" value="" maxlength="20" size="22"></input></td>
						<td width="100px"></td><td>Fecha MAP:<input type="text" id="DPC_fFechaMAP" name="DPC_fFechaMAP"  datepicker_format="DD/MM/YYYY" datepicker="false" maxlength="10" size="12"/></td>
					</tr>
				</table>
			<%} %>
				<table id="tabFiltros">
					<tr>
						<td>Folio FIAF</td>
						<td><input type="text" name="nFolioFIAF" id="nFolioFIAF" value="<%=c.getFolio() %>"/></td>
						<td>Folio IADE</td>
						<td><input type="text" name="folioIADE" id="folioIADE" value="" /></td>
						<%if(id_oper!=3){ %>
							<td><select name="exporta" id="exporta">
								<option value="1">Archivo Excel</option>
								<%if(id_oper==2){ %>
									<option value="2">Layout SICOP</option>
								<%} %>
								<option value="3">Formato FAP01</option>
								<option value="4">Adecuaciones Integradas</option>
								<option value="5">Adecuaciones Integradas PDF</option>
								<option value="6">Formato FAP02</option>
							</select></td>
						
						<td><input name="Exportar" id="Exportar" value="Exportar" type="button" onclick="exportaIntegracion();"><!--<img src="../imagenes/iconos/recur.png" width="25" height="21" alt="Refrescar Folio SICOP" onClick="recarga();">--></td>
						<%} %>
					</tr>
					<tr><%if(id_oper==1){ %>
						<td>Nivel de la adecuación</td>
						
							<td><select id="nivelAdecu" name="nivelAdecu">
								<option value=""></option>
								<option value="1">Interna</option>
								<option value="2">Interna SICOP</option>
								<option value="3">Interna SHCP</option>
								<option value="4">Externa SHCP sin restricci&oacute;n</option>
								<option value="5">Externa SHCP con restricci&oacute;n</option>
							</select>
						</td>
						<td>Tipo de Adecuación</td>
							<td><select id="tipoAdecu" name="tipoAdecu">
								<option value=""></option>
								<option value="Ampliación">Ampliación</option>
								<option value="Reducción">Reducción</option>
								<option value="Transferencia">Transferencia</option>
								<option value="Calendario">Calendario</option>
							</select></td>
						<td><input name="bBusca" id="bBusca" value="Busca" type="button" onclick="buscaAIntegrar();"></input></td>
						<%} %>
					</tr>
					<tr>
						<td><div id="divDesintegrar"><input type="checkbox" name="cCancelar"  id="cCancelar"  value="Cancelar">Des-Integrar</div></td>
					</tr>
				</table>
			</form>
						
						<table id="grdIntegra" cellspacing="0">
							<thead style="text-align: center;">
								<tr>
									<th>Folio Adecuación</th>
									<th>Tipo</th>
									<th>Nivel</th>
									<th>$ Monto/Importe</th>
									<th>Usuario Creador</th>
									<th>Fecha Creación</th>
									<th>Unidad Usuario</th>
									<th>Descartar</th>
								</tr>
							</thead>
							<tbody style="text-align: center;">
							</tbody>
						</table>
						<div id="justificaciones" title="Justificaciones">
							<table>
								<tr>
									<td>Justificación de Ampliación</td><td><textarea id="justificacionA" name="justificacionA" rows="5" cols="80"></textarea></td>
								</tr>
								<tr>
									<td>Justificación de Reducción</td><td><textarea id="justificacionR" name="justificacionR" rows="5" cols="80"></textarea></td>
								</tr>
								<tr>
									<td>Justificación Normativa</td><td><textarea id="justificacionNormativa" name="justificacionNormativa" rows="5" cols="80"></textarea></td>
								</tr>
							</table>
							<!-- <input type="button" name="btnAgregaJ" id="btnAgregaJ" onClick="agregaJustificaciones()" value="Agregar" />-->
						</div>
						<input type="button" name="btnCapturaJ" id="btnCapturaJ" onClick="capturaJustificaciones()" value="Justificaciones" />
						
						<input type="button" name="btnAplica" id="btnAplica" onClick="aplica()" value="Aplica Contablemente" />
		</div>
		<div id="divBuscaIntegracion">
			<input name="bIntegrar" id="bIntegrar" value="Integrar" type="button" onclick="fnIntegrar();"></input>
			<table   id="grdBuscaIntegracion">
				<thead>
					<tr>
						<th>Sec.</th>
						<th>Folio Adecuación</th>
						<th>Tipo</th>
						<th>Nivel</th>
						<th>$ Monto</th>
						<th>Usuario Creador</th>
						<th>Fecha Aplicacion</th>
						<th>Unidad Usuario</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
				<tfoot>
				</tfoot>
			</table>
			
		</div>
		
		
	<div id="dlgError" title="Detalle de Errores de Adecuaciones Presupuestales">
		<p>Detalle de Errores </p> <a rel=""></a>
		<table   class="display" id="grdAnteProyecto">
			<tr> 
				<td><textarea id="mensajeError" name="mensajeError" rows="16" cols="140"></textarea></td>
			</tr>
		</table>
	</div>
	<div id="dialogMotor" title="Mensajes del sistema">
		<p>Mensajes del sistema </p> <a rel=""></a>
		<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="120"></textarea>
	</div>
	</body>
</html>
