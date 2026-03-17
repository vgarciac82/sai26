<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type"	content="text/html; charset=ISO-8859-1">
		<title>B&uacute;squeda de Polizas</title>
		
		<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>	
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>	
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>      
		

<%									
 java.util.Calendar fecha = java.util.Calendar.getInstance();
 int mesaux= fecha.get(java.util.Calendar.MONTH);
 mesaux=mesaux+1;
 Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
 String nomUsuario = usuario.getLogin();
 String nomLargoUsuario = usuario.getNombre();
 String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
%>

<script type="text/javascript">
		
	var dTable;
	var ArrayDatos;
	var nFolioPoliza;
	var cCentroContable;
	var cTipoPoliza;
	var operacion='';
	var done=false;
	
	function ejecutaConsulta()
	{		
		var prm = "PolCtroContable="+ $("#PolCtroContable").val()
				+ "&PolEjercicioFiscal="+ $("#PolEjercicioFiscal").val()
				+ "&PolStatus="+ $("#PolStatus").val()
				+ "&PolOrigen="+ $("#PolOrigen").val()
				+ "&PolFechCapturaIni="+ $("#PolFechCapturaIni").val()
				+ "&PolFechCapturaFin="+ $("#PolFechCapturaFin").val()
				+ "&PolTipo="+ $("#PolTipo").val()
				+ "&PolFechAplicacionIni="+ $("#PolFechAplicacionIni").val()
				+ "&PolFechAplicacionFin="+ $("#PolFechAplicacionFin").val()
				+ "&PolNumeroIni="+ $("#PolNumeroIni").val()
				+ "&PolNumeroFin="+ $("#PolNumeroFin").val()
				+ "&PolMontoIni="+ $("#PolMontoIni").val()
				+ "&PolMontoFin="+ $("#PolMontoFin").val()
				+ "&PolAutomatica="+ $("#PolAutomatica").val();
	
		var ajxSource = '../CuentaContable/ConsultaPoliza?'+ prm;
			dTable.fnSettings().sAjaxSource = ajxSource;
			dTable.fnReloadAjax();											
	}
											
	function abrirDialog()
	{	
		$("#dialog-procesar").dialog( "open" );		
	}
											
	function reseteaPoliza()
	{
	  try
	  {
			reseteaCaptura();
		
			var tipo = "aplicarMotorPoliza";
			var nFolioDocPoliza = $("#folioDocumento").val();
			campo="nFolioDocPolizaCancel";
			tablaEnc="tDocPolizaCancelEncabezado";
			tablaDet="tDocPolizaCancelDetalle";
			tipoAplicar="DOCPOLIZACANCEL";		
			
			$.ajax({
				url:'../Generador/cierrePresupuestal.jsp',
				type:'post',
				dataType: 'json',
				async: false,
				data:{tipo:tipo,nFolioDocPoliza:nFolioDocPoliza,campo:campo,tablaEnc:tablaEnc,tablaDet:tablaDet,tipoAplicar:tipoAplicar},
				success:function(data)
				{ 
					if(data.sinSesion == 'sinSesion')
					{
							location.href = "../index.jsp";
					}
					if(data.estatus == "guardado")
					{	
						var xCrud="";
						if(operacion=='EDICION')
						{
							$("#Status").val(1);  		
					  		$("#resp").val("CAPTURA_POLIZA"); 
					  		$("#nCambio").val(10);  
					  		xCrud="ActualizaOPERADORPolizaUpdate,ActualizaCOPERPolizaUpdate2,";
				  		}
						      
						queryFormPost("Restauratpoliza,RestauraEncabezado,"+xCrud+"tmovimientoDocPolDelete,tmovimientoDocPolCancelDelete,tDocPolizaCancelDetalleDelete,tDocPolizaCancelEncabezadoDelete,tPolizaCancelDelete", 
						{	async : false, 
							callback : function() 
							 {			   	  
								  
								  done=true;
								  
								  if(operacion=='CANCELAMESACTUAL')
									{
									   querySelectPost("tdocPolizaCancelaEncabezado", {
										   async : false,
										   callback : function() 
								 			{
										   		Swal.fire({ icon: 'success',
															text: "Documento Cancelado correctamente en el mes abierto." });										      
										      
											  ejecutaConsulta();
											  $("#dialog-procesar" ).dialog( "close" );
											}
							 			});
									  
									}
								  else
								  {
									Swal.fire({ icon: 'success',
												text: "Documento regresado a estatus de Captura correctamente" });
									document.redireccionar.submit();
								  }
								  		
								  return;										         
							}
						});    			        
								
						if(!done)
					 	{
							queryFormPost("RestauraCasoDatosAplicado", { async : false});		
							if(operacion=='CANCELAMESACTUAL')
								Swal.fire({ icon: 'warning',
											text: "No se pudo Cancelar el documento. Reintente en un momento." });								
							else
								Swal.fire({ icon: 'warning',
											text: "No se pudo regresar le documento a estatus de Captura.\r Reintente en un momento." });  	 
				   			return;
						}
						
					}
					else
					{
						Swal.fire({ icon: 'warning',
									text: "No se pudo regresar le documento a estatus de Captura.\r Reintente en un momento." + data.estatus });						
					}	
				}});
			
				if(!done)
		  		 {
					alert(ERROPER);   	  
		      		$("#dialog-Procesando" ).dialog( "close" );	
		      		return;
				 }
      }
	  catch(ex) {
					Swal.fire({ icon: 'error',
								text: "Error 0001js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });					
				}
	}	
	
	function cancelaPolizaMesActual()
  	{		
		try
		{
			$.ajax({
			
			type: "GET",
			async: false,
			url: "../Generador/polizaManager.jsp",
			data: {"movimiento":"cancelacion","nfolioPoliza":nFolioPoliza,"cCentroContable":cCentroContable,"cTipoPoliza":cTipoPoliza },								
			success: function (msg) 
					{
							 
						 $("#dialog-procesar" ).dialog( "close" );					 
						 ArrayDatos=msg.split("//");					 
					     ArrayDatos=msg.split("//");
						 
					     if(ArrayDatos[0])
						  {
						 	 alert(ArrayDatos[1]);			   
						  }		     
						 else
						  {
							$("#tOperacion").val("Error al cancelar la Poliza en el mes actual");	
					       // $("#id_caso").val($("#nFolioDocumento").val());
					        queryFormPost("tDocPolizaBitacora", {async : false});
					    	 
					    	Swal.fire({ icon: 'info',
										text: ArrayDatos[1] });
							
							return false;			
						  }
					  
				     
					 }
			 });
			 ejecutaConsulta();	
		}
		catch(ex)
				{
					Swal.fire({ icon: 'error',
								text: "Error 0002js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });					
				}
	}


	function cancelaPolizaMesAnterior()
	{
		try{		
			var tipo = "aplicarMotorPolizaCancelacion";			
			var cFolioDocumento="POLI-C"+$("#cCentroContable").val()+"-"+$("#folioDocumento").val();
			var Fecha= $("#fAplicacion").val();
			var Usuario= $("#usuario").val();
			var mesAbierto=$("#mesAbierto").val();
			var mes=$("#mesAbierto").val()-1;			
			var mesOrigen=parseInt($("#fAplicacion").val().split("-")[1],10);
			var meses=["ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"];
			
			                                                                                                                                
			$.ajax({
				url:'../Generador/cierrePresupuestal.jsp',
				type:'post',
				dataType: 'json',
				async: false,
				data:{tipo:tipo,cFolioDocumento:cFolioDocumento,Fecha:Fecha,Usuario:Usuario,mesAbierto:mesAbierto},
				success:function(data)
				{ 
					if(data.sinSesion == 'sinSesion')
					{
							location.href = "../index.jsp";
					}
					if(data.estatus == "guardado")
					{							
						done=true;			
						queryFormPost("PolizaCancelacionContabilidad",{async : false});
						Swal.fire({ icon: 'success',
									text: "Documento cancelado correctamente en el mes: "+meses[mes]+" con el folio de Póliza :"+$("#nFolioPolizaCancelacion").val() });											
					 
						$("#dialog-Procesando" ).dialog( "close" );							
						ejecutaConsulta();					
					}
					else
					{					
						Swal.fire({ icon: 'warning',
									text: "No se pudo Cancelar el documento.\r Reintente en un momento." + data.estatus });						
					}	
				}});
			
				if(!done)
		  		 {
					Swal.fire({ icon: 'warning',
								text: "Reintente en un momento" });					 		      	
				 }
				 $("#dialog-procesar" ).dialog( "close" );
		}
		catch(ex)
				{
					Swal.fire({ icon: 'error',
								text: "Error 0003js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });					
				}
	
	}


	function reseteaCaptura()
	{
		try{	
			queryFormPost("tDocPolizaCancelDetalleDelete,tDocPolizaCancelEncabezadoDelete,tPolizaCancelDelete,tdocpolizaCancelEncabezadoCreate,tdocpolizaCancelDetalleCreate,tPolizaCancelCreate,RestauraCasoDatos", 
				{
				async : false, 
				callback : function()
				{
					done=true; 
					polizaCancel=true;   
				}
			});  
				
			if(!done)
			{
				Swal.fire({ icon: 'error',
					text: ERROPER });				    
			    return;
			} 
				
				    	done=false;
		}
		catch(ex)
				{
					Swal.fire({ icon: 'error',
								text: "Error 0004js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });					
				}
	}
	
	


	$(document).ready(function() 
	 {						
		$('#dialog-procesar').dialog({		
		    autoOpen: false,
		    modal: true,
		    resizable: false,
		    width: 500,
		    heigth: 900,
		    title: 'Procesando',
		    show: "blind",
		    hide: "scale",
		    closeOnEscape: false,
			overlay: { backgroundColor: '#FFF',opacity: 6.5 },
			open: function() 
			{
				if(operacion=='EDICION' || operacion=='CANCELAMESACTUAL')
				setTimeout("reseteaPoliza()",3000);	
				
				/*if(operacion=='CANCELAMESACTUAL')
				{
					setTimeout("reseteaPoliza()",3000);
					if(done)querySelectPost("tdocPolizaCancelaEncabezado", {async : false});
				}*/
			}
		});						
								
		$("#PolFechCapturaIni").datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});
	
		$("#PolFechCapturaFin").datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});

		$("#PolFechAplicacionIni").datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});

		$("#PolFechAplicacionFin").datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});

		getCentroContable();

		if (newCC != '') {
			$("#PolCtroContable").val(newCC);
			queryFormPost("CCentroContablePolizaRead", {
				async : true
			});
		}
		
		
		$("#nMesSistema").val(<%out.println(mesaux);%>);

		querySelectPost("catalogoEjercicioFiscalRead","PolEjercicioFiscal",{async : false});
		querySelectPost("catalogoPolOrigen", "PolOrigen", {async : false});
		querySelectPost("catalogoPolTipo", "PolTipo", {async : false});
		
		$("#PolTipo").change(function(){querySelectPost("catalogoPolOrigen", "PolOrigen", {	async : false});});
		$("#PolOrigen").change(function(){querySelectPost("catalogoPolTipo", "PolTipo", {async : false});});
		$("#PolAutomatica").change(function() {querySelectPost("catalogoPolOrigen", "PolOrigen", {async : false});
		querySelectPost("catalogoPolTipo", "PolTipo",{async : false	});});
		querySelectPost("catalogoPolStatus", "PolStatus");
						
		$('#imprimir').button();
		$('#consultar').button();
		$('#limpiar').button();
		$('#editar').button();
		$('#cancelar').button();

		$("#consultar").button().click(function(){ejecutaConsulta();});

		$("#limpiar").button().click(function() 
		{
				dTable.fnClearTable();
				$(":input").each(function() 
					{
					if (this.type != 'hidden'&& this.type != 'button')
						$(this).val('');
				});
		});

		$("#accordion").accordion();
						
						
	$.fn.dataTableExt.oApi.fnReloadAjax = function(oSettings, sNewSource) 
	{
		$.blockUI({message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"});
						
		if (typeof sNewSource != 'undefined')
			oSettings.sAjaxSource = sNewSource;
	
			this.fnClearTable(this);
			this.oApi._fnProcessingDisplay(oSettings, true);
			var that = this;
	
			$.getJSON(oSettings.sAjaxSource,null,function(json)
				{
								
					for ( var i = 0; i < json.aaData.length; i++) 
					{
						that.oApi._fnAddData(oSettings,json.aaData[i]);
					}
					
					oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
					that.fnDraw(that);
					that.oApi._fnProcessingDisplay(
							oSettings, false);
					$.unblockUI();
				});
		};
	
		dTable = $("#dTbl").dataTable({			
			"sAjaxSource" : '../CuentaContable/ConsultaPoliza',
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bJQueryUI": true,
			"sScrollX": "100%",
			//"sScrollXInner": "100%",
			"sScrollY": "190px",
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			"aoColumnDefs" : [ {
				"bSearchable" : false,
				"bVisible" : false,
				"aTargets" : [ 9 ]
			} ]
		});
	
		$("#dTbl tbody").dblclick(function(evt) 
		{
				var aPos = dTable.fnGetPosition(evt.target.parentNode);
		
				if (aPos instanceof Array)
					currIndex = aPos[0];
				else
					currIndex = aPos;
				
				var val = $(dTable.fnGetData()[currIndex][0]).val();
			
				val=val.replace(",''S''", " ")
					   .replace(",''A''", " ")
				       .replace(",''E''", " ")
				       .replace(",''C''", " ");
		
				if (val != '')				
					var ventimp = window.open("../Generador/polizaPopup.jsp?items=1&condiciones='("+ val + ")'","" ,"scrollbars=1, resizable=no, width=850, height=700");                                                     
				else 					
					Swal.fire({ icon: 'warning',
								text: "No se tiene informacion para imprimir la poliza." });
		
		});

		$("#checkAll").change(
			function(){
				
				var table = document.getElementById('dTbl');
				var aTrs = $('#dTbl').dataTable().fnGetNodes();
				var todos = false;
				
				if ($('#checkAll').is(':checked')){
					todos = true;						
					$("input:checkbox").attr('checked', 'checked');
				}else{
					$("input:checkbox").removeAttr('checked');
				}
			});
		

		$("#editar").button().click(function() 
		{
			try
			{					
				var n = $("[checked]").length;
				
				if (n == 0 || !n) 
				{					
					Swal.fire({ icon: 'warning',
								text: "Debe seleccionar al menos una p\u00F3liza" });
					return;
				}
			
				if (n > 1) 
				{
					Swal.fire({ icon: 'warning',
								text: "No puede editar m\u00E1s de una p\u00F3liza" });					
					return;
				} 
				else 
				{						
					var val = $("input[name='selection']:checked").val().replace(/\[/g,'')
																		.replace(/\]/g, '')
																		.replace(/\s/g, '')
																		.replace(/and/g, ',')
																		.replace(/'/g, '');				
					var elmnts = val.split(',');
					
					nFolioPoliza = elmnts[0];
					cCentroContable = elmnts[1];
					cTipoPoliza = elmnts[2];
					var cStatus = elmnts[3];
					
					$("#nFolioPoliza").val(nFolioPoliza);
					$("#cTipoPoliza").val(cTipoPoliza);
					$("#cCentroContable").val(cCentroContable);
					$("#aEjercicioFiscal").val($("#PolEjercicioFiscal").val());											
			
			
					queryFormPost("readNFolioDocumento",{async : false});
					$("#folioDocumento").val($("#nFolioDocumento").val());	
			
			
					if ('DOCPOLIZA' == $("#cTipoDocumento").val()&& $("#nFolioDocumento").val() != '') 
					{
						queryFormPost("MesContableAbiertoEdit,VerificaPolizCancelacion,readIdOper",{async : false});
					
								
						var aux = $("#nAux").val();	
					
						if(aux != 0)
						{				
							Swal.fire({ icon: 'warning',
										text: "La póliza que intenta EDITAR es una póliza de cancelación aplicada a la póliza No. "+aux+" por lo cual no se puede EDITAR" });							
							return;							
						}
						else if($("#id_oper").val()=="1" )
						{	
							Swal.fire({ icon: 'warning',
										text: "La póliza está en captura. No se puede regresar a EDICION" });							
							return;
						}
						else if($("#id_oper").val()=="2" )
						{	
							Swal.fire({ icon: 'warning',
										text: "La póliza está en REVISION. No se puede regresar a EDICION" });							
							return;
						}
						else if($("#id_oper").val()=="3" )
						{	
							Swal.fire({ icon: 'warning',
										text: "La póliza está en AUTORIZACION. No se puede regresar a EDICION" });							
							return;
						}							
						else if ($("#nMes").val() == $("#mesAbierto").val())
						{	
							Swal.fire({
								  title: '¿Desea continuar?',
								  text: "Se regresara al estatus de CAPTURA la poliza No. "+$("#nFolioPoliza").val()+"?",
								  icon: 'warning',
								  showCancelButton: true,
								  confirmButtonColor: '#288BA8',
								  cancelButtonColor: '#e6e6e6',
								  confirmButtonText: 'Aceptar',
								  cancelButtonText: 'Cancelar'
							}).then((result) => {
							  if (result.isConfirmed) {
								  	$("#tOperacion").val("Regresando a edicion");													        
									queryFormPost("tDocPolizaBitacora", {	async : false});
									
									operacion='EDICION';
									abrirDialog();		
							  } 
							})
						}						
						else 
							Swal.fire({ icon: 'warning',
										text: "Para editar una poliza el mes debe estar abierto" });																									
					}				
					else
						Swal.fire({ icon: 'warning',
									text: "Solo es posible editar p\u00F3lizas manuales" });																																
				}
				
			}
			catch(ex)
					{
						Swal.fire({ icon: 'error',
									text: "Error 0005js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });						
					}
			});
			
			
			
		$("#imprimir").button().click(function() 
		{
				var vcheckbox = "";
				var flagI = 0;
				var sel=0;
			try
			{
				$(":checkbox").each(function() 
				{				
					if(this.id != 'checkAll'	&& $(this).attr("checked")) 
					{			
						if (flagI == 0)
						{
							vcheckbox +=$(this).val().replace(",''S''", " ").replace(",''A''", " ").replace(",''E''", " ").replace(",''C''", " ");
							flagI = 1;
							sel++ ;							
						} 
						else 
						{			
							vcheckbox +=$(this).val().replace(",''S''", " ").replace(",''A''", " ").replace(",''E''", " ").replace(",''C''", " ");		
											
						}			
					}
				
				});
				
				var val = $("input[name='selection']:checked").val().replace(/\[/g,'')
																		.replace(/\]/g, '')
																		.replace(/\s/g, '')
																		.replace(/and/g, ',')
																		.replace(/'/g, '');				
					var elmnts = val.split(',');
					
					nFolioPoliza = elmnts[0];
					cCentroContable = elmnts[1];
					cTipoPoliza = elmnts[2];
					var cStatus = elmnts[3];
					
					$("#nFolioPoliza").val(nFolioPoliza);
					$("#cTipoPoliza").val(cTipoPoliza);
					$("#cCentroContable").val(cCentroContable);
					$("#aEjercicioFiscal").val($("#PolEjercicioFiscal").val());											
					queryFormPost("readNFolioDocumento",{async : false});
					$("#folioDocumento").val($("#nFolioDocumento").val());

			     if ('DOCPOLIZA' == $("#cTipoDocumento").val()&& $("#nFolioDocumento").val() != '') 				
					{				
					
						queryFormPost("readIdOper",	{async : false});				
						 if($("#id_oper").val()=="3")
						{					
							Swal.fire({ icon: 'warning',
										text: "No se puede IMPRIMIR una póliza en AUTORIZACIÓN" });							
							return;						
						}
						else if($("#id_oper").val()=="2")
						{	
							Swal.fire({ icon: 'warning',
										text: "No se puede IMPRIMIR una póliza en REVISIÓN" });														
							return;						
						}
						else if($("#id_oper").val()=="1")
						{						
							Swal.fire({ icon: 'warning',
										text: "No se puede IMPRIMIR una póliza en EDICIÓN" });							
							return;						
						}
						
						}
			     
			     
			     
			     
				if (vcheckbox != '') 
				{  
					var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=PolizaContable.jasper&condiciones='("+ vcheckbox + ")'";					
					var ventimp = window.open(url, "popacuse","scrollbars=1, resizable=yes, width=1024, height=768");
				}
				else		
					Swal.fire({ icon: 'warning',
								text: "Debe seleccionar al menos una poliza para imprimir." });					
			}
			catch(ex)
					{
						Swal.fire({ icon: 'error',
									text: "Error 0006js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });						
					}
		});		
			
							   
		$("#vistaPrevia").button().click(function() 
		 {
			try
			{
				var vcheckbox = "";
				var flagI = 0;
				var sel=0;
				
				$(":checkbox").each(function() 
					{
						if (this.id != 'checkAll'	&& $(this).attr("checked")) 
						{
							if (flagI == 0) 
							{						
								vcheckbox = $(this).val();					
								flagI = 1;
								sel++ ;
								
								vcheckbox=vcheckbox.replace(",''S''", " ")
												   .replace(",''A''", " ")
												   .replace(",''E''", " ")
												   .replace(",''C''", " ");						
							} 
							else 
							{
								sel++ ;
								vcheckbox += ""+ $(this).val();
							
								vcheckbox=vcheckbox.replace(",''S''", " ")
												   .replace(",''A''", " ")
												   .replace(",''E''", " ")
												   .replace(",''C''", " ");
							
							
							}
						}			
				});
				if (vcheckbox != '') {
				
				var ventimp = window.open("../Generador/polizaPopup.jsp?items="+sel+"&condiciones=("+ vcheckbox + ")","" ,"scrollbars=1, resizable=no, width=850, height=700");                                                        //'("+ val + ")'","popacuse",
				
				
				} else {
					Swal.fire({ icon: 'warning',
								text: "Debe seleccionar al menos una poliza para su vista previa." });				
				}
			}
			catch(ex)
					{
						Swal.fire({ icon: 'error',
									text: "Error 0006js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });						
					}
			
		});    
		
										
		$("#cancelar").button().click(function(){
			var table = document.getElementById('dTbl');
			var aTrs = $('#dTbl').dataTable().fnGetNodes();
			var todos = false;
			var val = "";
			var n = 0; 
			
			if ($('#checkAll').is(':checked')){
				todos = true;						
				$("input:checkbox").attr('checked', 'checked');
			} else {
				var val = $("input[name='selection']:checked").val();
				if (!val){
					Swal.fire({ icon: 'warning',
						text: "Debe seleccionar al menos una p\u00F3liza." });		
					return;
				}else{
					var elmnts = val.split(',');
					n = elmnts.length;
				}
			}					
							
			try{
				if (todos){
					Swal.fire({ icon: 'warning',
								text: "Solo se  puede cancelar una p\u00F3liza a la vez." });					
					return;
				}
				if (n == 0 || !n){
					Swal.fire({ icon: 'warning',
								text: "Debe seleccionar al menos una p\u00F3liza." });					
					return;
				}								
				else{
					
					var val = $("input[name='selection']:checked").val();
					val = $("input[name='selection']:checked").val().replace(/\[/g,'').replace(/\]/g, '').replace(/\s/g, '').replace(/and/g, ',').replace(/'/g, '');
					
					var elmnts = val.split(',');
					nFolioPoliza = elmnts[0];
					cCentroContable = elmnts[1];
					cTipoPoliza = elmnts[2];
					var cStatus = elmnts[3];
					
					$("#nFolioPoliza").val(nFolioPoliza);
					$("#cTipoPoliza").val(cTipoPoliza);
					$("#cCentroContable").val(cCentroContable);
					$("#aEjercicioFiscal").val($("#PolEjercicioFiscal").val());
					
					queryFormPost("readNFolioDocumento",{async : false});
					$("#folioDocumento").val($("#nFolioDocumento").val());  					
						
					if ('DOCPOLIZA' == $("#cTipoDocumento").val()&& $("#nFolioDocumento").val() != '') 				
					{				
					
						queryFormPost("MesContableAbiertoEdit,VerificaPolizCancelacion,readIdOper",	{async : false});				
													
						var aux = $("#nAux").val();							
						queryFormPost("readNCAMBIO",{async : false});
						var nCambioPoliza=$("#nCambio").val();		
					
						if(cStatus=='C')
						{				
							Swal.fire({ icon: 'info',
										text: "La póliza ya está CANCELADA" });							
							return;				
						}								
						else if(aux != 0)
						{	
							Swal.fire({ icon: 'info',
										text: "La póliza que intenta CANCELAR es una póliza de cancelación aplicada a la póliza No. "+aux });												
							return;						
						}		
						else if($("#id_oper").val()=="3")
						{						
							Swal.fire({ icon: 'warning',
										text: "No se puede CANCELAR una póliza en AUTORIZACIÓN" });							
							return;						
						}
						else if($("#id_oper").val()=="2")
						{						
							Swal.fire({ icon: 'warning',
										text: "No se puede CANCELAR una póliza en REVISIÓN" });							
							return;						
						}
						else if($("#id_oper").val()=="1")
						{	
							Swal.fire({ icon: 'warning',
										text: "No se puede CANCELAR una póliza en EDICIÓN" });							
							return;						
						}
						
						else
						{	
							if ($("#nMes").val() == $("#mesAbierto").val())
							{
								Swal.fire({
									  title: '¿Desea continuar?',
									  text: "Se CANCELARA la poliza No. "+$("#nFolioPoliza").val()+"?",
									  icon: 'warning',
									  showCancelButton: true,
									  confirmButtonColor: '#288BA8',
									  cancelButtonColor: '#e6e6e6',
									  confirmButtonText: 'Aceptar',
									  cancelButtonText: 'Cancelar'
								}).then((result) => {
								  if (result.isConfirmed) {
										$("#tOperacion").val("Cancelando Poliza del mes actual: "+$("#nMes").val());	
										//$("#id_caso").val($("#nFolioDocumento").val());
										queryFormPost("tDocPolizaBitacora", {	async : false});
										
										operacion='CANCELAMESACTUAL';
										abrirDialog();		
								  }
								  else {
									  return;
								  }
								})												
							}
							else 
							{	
								Swal.fire({
									  title: '¿Desea continuar?',
									  text: "Se CANCELAR la poliza No. "+$("#nFolioPoliza").val()+" DE UN MES CONTABLE CERRADO ?",
									  icon: 'warning',
									  showCancelButton: true,
									  confirmButtonColor: '#288BA8',
									  cancelButtonColor: '#e6e6e6',
									  confirmButtonText: 'Aceptar',
									  cancelButtonText: 'Cancelar'
								}).then((result) => {
								  if (result.isConfirmed) {
									  $("#tOperacion").val("Cancelando Poliza del mes  "+$("#nMes").val()+"  en el mes  "+$("#mesAbierto").val());	
										//$("#id_caso").val($("#nFolioDocumento").val());
										queryFormPost("tDocPolizaBitacora", {	async : false});
										
										abrirDialog();
										setTimeout("cancelaPolizaMesAnterior()", 500);				
								  }
								  else {
									  return;
								  }
								})													
							}
						
						}
					} 
					else
						Swal.fire({ icon: 'warning',
									text: "Solo es posible cancelar p\u00F3lizas manuales" });						
				}
			}
			catch(ex)
					{	
						Swal.fire({ icon: 'error',
									text: "Error 0006js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });					
					}
		});							  
										
										
	});
	
</script>
	</head>
	<br/>
	<body id="dt_example" >
	   
		<form action="../poliza/EditaPoliza" target="content-iframe" method="post" id="mainForm">
			
			<input type="hidden" name="tOperacion" id="tOperacion" />
			<input type="hidden" name="usuario" id="usuario" value="<%=nomUsuario%>" />							
			<input type="hidden" id="mesAbierto" name="mesAbierto" value="" />
			<input type="hidden" id="nMes" name="nMes" value="" />
			<input type="hidden" id="nMesSistema" name="nMes" value="" />
			<input type="hidden" id="nFolioDocumento" name="nFolioDocumento" value="" />
			<input type="hidden" id="nFolioPolizaCancelado" name="nFolioPolizaCancelado" value="" />
			<input type="hidden" id="nFolioDocumentoCancelado" name="nFolioDocumentoCancelado"	value="" />	
			<input type="hidden" id="nFolioPolizaNueva" name="nFolioPolizaNueva" value="" />		
			<input type="hidden" id="nAux" name="nAux" value="" />
			<input type="hidden" id="nCambio" name="nCambio" value="" />	
			<input type="hidden" id="cTipoDocumento" name="cTipoDocumento" value="" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="" />
			<input type="hidden" id="nFolioPoliza" name="nFolioPoliza" value="" />
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="" />
			<input type="hidden" id="hPolCtroContable" name="hPolCtroContable" value=""/>
			<input type="hidden" id="PolCtroContable" name="PolCtroContable" value=""/>
			<input type="hidden" id="action" name="action" value="EDITAR" />
			<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="" />
			<input type="hidden" id="id_oper" name="id_oper" value="" />
			<input type="hidden" id="folioDocumento" name="folioDocumento" value="" />
			<input type="hidden" id="nomLargoUsuario" name="nomLargoUsuario" value="<%=nomLargoUsuario%>" />
			<input type="hidden" id="Status" name="Status" value="0" />
			<input type="hidden" id="resp" name="resp" value=" " />
			<input type="hidden" id="id_caso" name="id_caso" />	
			<input type="hidden" id="fAplicacion" name="fAplicacion" /> 
			<input type="hidden" id="nFolioPolizaCancelacion" name="nFolioPolizaCancelacion" />
			
			<div id="container" style="width: 80%" class="container" >
				<div class="card-header"> <h3> Consulta de Polizas </h3> </div>
				<hr class="mt-3"/>
			
				<h6> Campos de B&uacute;squeda </h6>
				<hr class="mt-3"/>
					
				<input type="hidden" id="hPolCtroContable" name="hPolCtroContable" value=""/>
				<input type="hidden" id="PolCtroContable" name="PolCtroContable" value=""/>
				
				<div class="row">				
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<label for="" class="form-label"> Número de Póliza </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<label for="" class="form-label"> Monto de Póliza </label>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
						<div class="row">				
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolNumeroIni" class="form-label"> Del: </label>
							</div>
							<div class="columna-interior col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<input id="PolNumeroIni" name="PolNumeroIni" value="" class="form-control form-control-sm" type="text"/>
							</div>
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolNumeroFin" class="form-label"> Al: </label>
							</div>
							<div class="columna-interior col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<input id="PolNumeroFin" name="PolNumeroFin" value="" class="form-control form-control-sm" type="text"/>
							</div>							
						</div>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<div class="row">				
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolMontoIni" class="form-label"> De $: </label>
							</div>
							<div class="columna-interior col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<input id="PolMontoIni" name="PolMontoIni" value="" class="form-control form-control-sm" type="text"/>
							</div>
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolMontoFin" class="form-label"> A $: </label>
							</div>
							<div class="columna-interior col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
								<input id="PolMontoFin" name="PolMontoFin" value="" class="form-control form-control-sm" type="text"/>
							</div>							
						</div>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="PolAutomatica" class="form-label"> Autom&aacute;tica </label>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="PolTipo" class="form-label"> Tipo </label>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="PolOrigen" class="form-label"> Origen </label>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="PolEjercicioFiscal" class="form-label"> Ej. Fiscal </label>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<select id="PolAutomatica" name="PolAutomatica" class="form-select form-select-sm">
							<option value=""></option>
							<option value="0"> CONTABILIDAD </option>
							<option value="1"> AUTOMÁTICA </option>							
						</select>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<select id="PolTipo" name="PolTipo" class="form-select form-select-sm">
							<option value=""> </option>
						</select>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<select id="PolOrigen" name="PolOrigen" class="form-select form-select-sm">
							<option value=""> </option>
						</select>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<select id="PolEjercicioFiscal" name="PolEjercicioFiscal" class="form-select form-select-sm">
							<option value=""> </option>
						</select>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<label for="" class="form-label"> Fecha Captura </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<label for="" class="form-label"> Fecha Aplicación </label>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
						<div class="row">				
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolFechCapturaIni" class="form-label"> Del: </label>
							</div>
							<div class="columna-interior col-12 col-lg-4 col-md-4 col-sm-12 d-flex">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-calendar"></i></span>				
									<input id="PolFechCapturaIni" name="PolFechCapturaIni" value="" class="form-control form-control-sm" type="text"/>
								</div>
							</div>
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolFechCapturaFin" class="form-label"> Al: </label>
							</div>
							<div class="columna-interior col-12 col-lg-4 col-md-4 col-sm-12 d-flex">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-calendar"></i></span>
									<input id="PolFechCapturaFin" name="PolFechCapturaFin" value="" class="form-control form-control-sm" type="text"/>
								</div>
							</div>							
						</div>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
						<div class="row">				
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolFechAplicacionIni" class="form-label"> Del: </label>
							</div>
							<div class="columna-interior col-12 col-lg-4 col-md-4 col-sm-12 d-flex">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-calendar"></i></span>
									<input id="PolFechAplicacionIni" name="PolFechAplicacionIni" value="" class="form-control form-control-sm" type="text"/>
								</div>
							</div>
							<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex">
								<label for="PolFechAplicacionFin" class="form-label"> Al: </label>
							</div>
							<div class="columna-interior col-12 col-lg-4 col-md-4 col-sm-12 d-flex">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-calendar"></i></span>
									<input id="PolFechAplicacionFin" name="PolFechAplicacionFin" value="" class="form-control form-control-sm" type="text"/>
								</div>
							</div>							
						</div>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="PolStatus" class="form-label"> Status </label>
					</div>
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<select id="PolStatus" name="PolStatus" class="form-select form-select-sm">		
							<option value=""> </option>											
						</select>
					</div>
				</div>
				
				<div class="row justify-content-end">				
					<div class="columna col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<input type="button" id="imprimir" value="Imprimir" class="btn btn-outline-primary btn-sm"/>								
					</div>
					<div class="columna col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" id="consultar" value="Consultar" class="btn-secondary btn-secondary-sm"/>				
					</div>
					<div class="columna col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						<input type="button" id="limpiar" value="Limpiar" class="btn btn-outline-primary btn-sm"/>						
					</div>
					<!-- <div class="columna col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						<input type="button" id="editar" value="Editar" class="btn btn-outline-primary btn-sm"/>						
					</div> -->
					<div class="columna col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						<input type="button" id="cancelar" value="Cancelar Poliza" class="btn btn-outline-danger btn-sm"/> 
					</div>
				</div>		
				
				<br/>

				<div class="row d-flex">								
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="checkbox" id="checkAll" name="checkAll" class="form-check-input"> Todos
					</div>
				</div>
				
				<div class="table-responsive">	      	
					<table id="dTbl" class="table table-striped" >				
						<thead>
							<tr>
								<th>  </th>
								<th> Tipo </th>
								<th> Folio </th>
								<th> F. de Captura </th>
								<th> F. de Aplicaci&oacute;n </th>
								<th> Estatus </th>
								<th> Origen </th>
								<th> Concepto </th>
								<th> Autorizo </th>
								<th> Centro Contable </th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
				
		</form>
		
		
		
		<form id="redireccionar" name="redireccionar"  action="../gstnmngr/gestion?cmd=1" target="content-iframe"	method="post"></form>
		
		
		<div id="dialog-procesar">
				<div id="esperar" align="center">Espere por favor....
					<div id="ProcMsg" align="center" style="font-size: 10pt">Procesando... </div>
					<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
			
		
	</body>
	
</html>