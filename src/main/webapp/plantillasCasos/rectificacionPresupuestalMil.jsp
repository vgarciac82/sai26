<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosBusinessLogic"%>
<%@page import="com.syc.contable.RectificacionPresupuestariaMilBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.RectificacionEncabezado"%>
<%@page import="com.syc.contable.core.RectificacionDetalle"%>
<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String aEjercicioFiscal="";
	String cCentroContable="";
	String msg="";
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	RectificacionPresupuestariaMilBusinessLogic rpmbl = new RectificacionPresupuestariaMilBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	boolean bAplicadoCont = false;
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String cUR = usuario.getU_UR();
	String cRamo = usuario.getU_Ramo();
	String login = usuario.getLogin();
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] archivoExcel = null; //id_oper 1 (EXCEL)	
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1); //el numero que está primero indica el num de carpeta
	

	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String mensajeError="";
	
	try{
	    if (request.getParameter("leeExcel") != null && request.getParameter("leeExcel").equals("1")) {
			if(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1).length>0)
				rpmbl.leeArchivoExcel(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0].getAbsolutePath(),c,usuario,folio,c.getIdCaso(),"Mil");
		}
	}catch(Exception e){
	 	mensajeError = e.getMessage();   
	}
	
	ReintegrosBusinessLogic reintegro = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String mensaje = "";
	if (session.getAttribute("mensaje") != null)
		msg = (String) session.getAttribute("mensaje");
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	String mensajeCLC = "";
	//if ("si".equals(request.getParameter("mensajeclc")) && request.getParameter("mensajeclc")!=null)
		//mensajeCLC = "No todas las CLCs se encuentran pagadas o existen. O la EP no se encuentra en el catalogo";
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	RectificacionEncabezado reMil = rpmbl.getRectificacionEncabezadoMil(folio);
	
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);
		
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Recitificaciones Presupuestales Capitulo Mil</title>

		<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
		<script type="text/javascript" src="../Generador/js/jquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>

		<script type="text/javascript" charset="utf-8">
		
		var clickEnviar = false;
		
		function limpiarDatos(){
			$.blockUI({message : "Procesando espere ......"});
			limpiaDatos.submit();
		}
		
		function onPostSubmit(id_oper){//validaciones del boton enviar
		  		return true;
		  	}
		
			function ResponsableSiguiente(id_oper){
				if(id_oper==1)
					return "REVISOR_RECTIFICACIONMIL";

				if(id_oper==2){
					if(document.rechazo.autorizaRein[1].checked)
						return "CAPTURISTA_RECTIFICACIONMIL";
					else
						return "AUTORIZADOR_RECTIFICACIONMIL";
				}

				if(id_oper==3)
					return "AUTORIZADOR_RECTIFICACIONMIL";

				if(id_oper==4){
					return "AUTORIZADOR_RECTIFICACIONMIL";
				}
				
				if(id_oper==5){
					return "CONSULTA_RECTIFICACIONMIL";
				}
				//********************************************
			}

			function OperacionSiguiente(id_oper){
				if(id_oper==1)
					return "rev_rectificacion_m";

				if(id_oper==2){
					if(document.rechazo.autorizaRein[1].checked)
						return "capt_rectificacion_m";
					else
						return "layout_rect_m";
				}

				if(id_oper==3){
					return "aut_rectificacion_m";
				}
				
				if(id_oper==4){
					return "aut_rectificacion_m";
				}
				
				if(id_oper==5){
					return "cons_rectificacion_m";
				}
				//*********************************************************************************************
			}

		function onPostDisplay(){
			}


		 function onSubmit(id_oper){
				var p = window.parent;
				var valida_campos = true;
				try{
					//validaciones de la forma
					var msgAlert="";
					if(msgAlert!=""){
						alert(msgAlert);
						return false;
					}
					if (id_oper==1){
						var dblTotalDice = $("#totalDice").val() * 1;
						var dblTotalDebeDecir = $("#totalDebeDecir").val() * 1;
						//SAQUE LOS SETEOS DE LAS VARIABLES DEL IF PORQUE SI FALLABA INTENTABA CREAR EL DOCUMENTO CON COSAS VACÍAS Y TRONABA FMC
						p.gestion.setFolio($("#FOLIO").val());
						p.gestion.setFechaDocumento($("#FECHA_SOLICITUD").val());
						p.gestion.setEjercicioFiscal($("#EJERCICIO_FISCAL").val());
						p.gestion.setOperador($("#OPERADOR").val());
						p.gestion.setConceptoMov("Rectificacion Presupuestal con FOLIO " + $("#FOLIO").val() + ".");
						p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
						//**************************************************
						
						//ESTOS SON PARA REDONDEAR A DOS DECIMALES Y NO META ERRORES
						dblTotalDice=Math.round(dblTotalDice*100)/100 ;
						dblTotalDebeDecir=Math.round(dblTotalDebeDecir*100)/100 ;
						//*********************************************************
						if(<%=reMil!=null%>){
							dialogFirmantes();
							/*parent.document.getElementById("pb_send").disabled=false;
							parent.document.getElementById("pb_save").disabled=true;*/
						}
					}
					
					
					
					//SALVA EL MENSAJE DE ERROR Y VALIDA QUE EXISTA EL MENSAJE EN CASO DE NO ACEPTARSE, SI NO PONE NADA EL USUARIO SE LE PIDE QUE 
					//PONGA ALGO, SI DICE QUE ESTA CORRECTO, SELE DA LA OPCIÓN DE AVANZAR EL CASO
					//FMC 31/oct**
					if (id_oper==2){
			  			if(document.rechazo.autorizaRein[1].checked && $("#motivoRechazo").val()==""){
				  			alert("Motivo de rechazo es requerido");
				  			parent.document.getElementById("pb_send").disabled=true;
				  			return false;
			  			}else if(document.rechazo.autorizaRein[1].checked && $("#motivoRechazo").val()!=""){
				  			parent.document.getElementById("pb_save").disabled=true;
			  				parent.document.getElementById("pb_send").disabled=false;
			  			}else if(!(document.rechazo.autorizaRein[0].checked || document.rechazo.autorizaRein[1].checked)){
			  				alert("Favor de marcar si los datos son correctos o no");
			  				parent.document.getElementById("pb_save").disabled=false;
			  				return false;
			  			}else{
			  				if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>)
			  					parent.document.getElementById("pb_send").disabled=false;
			  				parent.document.getElementById("pb_save").disabled=true;
			  				$("#motivoR").val($("#motivoRechazo").val());
			  				$("#mensajeError").val($("#motivoRechazo").val());
			  				p.gestion.setMensaje($("#motivoR").val());
			  			}
		  			}
		  			
					//************************************
					
					
					//APLICACION CONTABLE DE AUTORIZACION FMC 2/Nov 
					if(id_oper==4){
						if(<%=!"true".equals(c.getCasoDato("AUTORIZADO_CONT").getValor())%>)
							mostrarDialogAut();
		  				else{
		  					alert("El documento ya se encuentra autorizado");
		  					parent.document.getElementById("pb_send").disabled=false;
		  				}
					}
					
					if(id_oper==3){
						parent.document.getElementById("pb_send").disabled=false;
						parent.document.getElementById("pb_save").disabled=true;
					}
					
					if(id_oper==5){
						parent.document.getElementById("pb_send").disabled=true;
						parent.document.getElementById("pb_save").disabled=true;
					}
					
				} catch (e) {
					window.alert("onSubmit: Error: " + e.message);
					return false;
				}
				return valida_campos;
			}
		
		  	function onLoadPlantilla(id_oper){
		  		
		  		if(id_oper==5){
					$("#divImprimePoliza").show();
					$("#EditaFirmas").css('visibility', 'visible');
				}else{
					$("#divImprimePoliza").hide();
					$("#EditaFirmas").css('visibility', 'hidden');
				}
				
		  		if(<%=c.getIdGabinete()%>==-1)
		  				parent.document.getElementById("pb_save").click();//esto es para asegurar que se crea el expediente antes de subir el archivo
		  				
		  		if(<%=reMil!=null%>){
		  			$("#CatMovimientoRectificacion").val('<%=reMil!=null?reMil.getcTipoMovto():""%>');
		  			$("#concepto").val('<%=reMil!=null?reMil.getcConceptoRectificacion():""%>');
		  			$("#origPresupuesto").replaceWith('<input type="text" name="origPresupuesto" id="origPresupuesto" value="<%=reMil!=null?reMil.getnOrigenPPTO():""%>"');
		  			$("#ctr_int").val('<%=reMil!=null?reMil.getCtr_int():""%>');
		  			$("#totalDice").val('<%=reMil!=null?reMil.getTotalDice():""%>');
		  			$("#totalDebeDecir").val('<%=reMil!=null?reMil.getTotalDebe():""%>');
		  			$("#nFolioSICOP").val('<%=reMil!=null?reMil.getnFolioSicop():""%>');
		  			$("#nFolioSIAFF").val('<%=reMil!=null?reMil.getnFolioSIAFF():""%>');
		  			$("#caNoContrarrecibo").val('<%=reMil!=null?reMil.getCaNoContrarrecibo():""%>');
		  			$("#dTipoPago").val('<%=reMil!=null?reMil.getcTipoRectificacion():""%>');
		  			$("#fExp").val('<%=reMil!=null?reMil.getfExp():""%>');
		  			if(<%=id_oper>2%>){
		  				$("#fApl").val('<%=reMil!=null?reMil.getfAplicacion():""%>');
		  			}
				}
		  		if(<%=mensajeError!=null && !"".equals(mensajeError)%>){
					$("#mensajeError").val('<%=mensajeError%>');
					$('#dialogMensaje').dialog('option', 'modal', true).dialog('open');
				}
		  		
		  		if(id_oper==1){
			  		if(<%=reMil!=null%>)
						parent.document.getElementById("pb_save").disabled=false;
				}
		  		
		  		if(id_oper>=2){
						parent.document.getElementById("pb_cancel").disabled=true;
				}
		  		
		  		if(id_oper==4){
		  			parent.document.getElementById("pb_send").disabled=true;
		  		}
		  }
			
			function onPostSubmit(id_oper){
				return true;
			}
			
  	function setDatePickers(){
	  	$( "#fExp" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
		$( "#fApl" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
	}

  	
  	function fnImportarExcel() {
  		$.blockUI({message : "Procesando espere ......"});
		var msgAlert = "";
		if ($("#importExcel").val() == "") {
			msgAlert += "El archivo Excel es requerido";
		}
		if (msgAlert != "") {
			alert(msgAlert);
			return false;
		} else {
			guardaExp();
			document.upExcel.submit();
			return true;
		}
	}
	
	function guardaExp(){
  		parent.document.getElementById("pb_save").disabled=false;
  		parent.document.getElementById("pb_save").click();
  		parent.document.getElementById("pb_save").disabled=true;
	}
			
	$(document).ready(function(){
		$("#borrarTodo").button();
		$("#aplicarContable").button();
		$("#aplicarContableAut").button();
		$("#ImportarExcel").button();
		
		$(function() {		
		      		$('#dialog').dialog({
		      			autoOpen: false,
		      			width: 900,
		      			heigth: 2900
		    		});
		    	});
					    
				$(function() {		
					$('#dialogAut').dialog({
						autoOpen: false,
						width: 900,
						heigth: 2900
					});
				});
				
				$(function() {		
		      		$('#dialogMensaje').dialog({
		      			autoOpen: false,
		      			width: 900,
		      			heigth: 2900,
		      			close: function() {
		      				document.location.reload();
						}
		    		});
		    	});
		    	
		    	$(function() {		
		      		$('#dialogMotor').dialog({
		      			autoOpen: false,
		      			width: 900,
		      			heigth: 2900
		    		});
		    	});
				
				oTable = $("#tblPagadoFiltrado").dataTable({
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bPaginate" : false,
					"bAutoWidth" : true,
					"bScrollCollapse" : true,
					"sScrollXInner": "100%", 
					"sScrollX": "100%",
					"sPaginationType" : "full_numbers",
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide": true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vtRectificacionDetMil&qw=nFolioRectificacionMil="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>,
						aoColumns: [
							{ sName: "nDocRenglon"},
							{ sName: "cMes" },
							{ sName: "EP"},
							{ sName: "cEvento"},
							{ sName: "mImporte"},
							{ sName: "tipoConcepto"},
							{ sName: "tipoMovimiento"}
						],
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
				$('#autorizaRein').click(function(){
					if(<%=id_oper==2%>){
						if(<%=!("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
	 						var p = window.parent;
		 					mostrarDialog();
		 					$("#fApl").val($("#FECHA_APLICACION_CONTABLE").val());
		 					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
		 					parent.document.getElementById("pb_send").disabled=false;
		 					parent.document.getElementById("pb_save").disabled=false;
	 					}else{
	 						alert("El documento ya se encuentra aplicado contablemente");
	 						if(!clickEnviar){
								parent.document.getElementById("pb_send").disabled=true;
								clickEnviar = true;
							}else{
								parent.document.getElementById("pb_send").disabled=false;
							}
	 						document.rechazo.autorizaRein[0].disabled=true;
	 						document.rechazo.autorizaRein[1].disabled=true;
 						}
	 				}
	 			});
	 			$("#dialog-firmantesUpdate").dialog({
					autoOpen: false,
					height: 420,
					width: 500,
					modal: true,
					buttons: {
						"Aceptar": function() {
							if($.trim($("#cNombreVoBoUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Vº Bº"); return; } 
							else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Vº Bº"); return; }
							else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Vº Bº"); return; }
							else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Vº Bº"); return; }					
							
							if($.trim($("#cNombreAutUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Autorizar"); return; } 
							else if($.trim($("#cPaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return; }
							else if($.trim($("#cMaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return; }
							else if($.trim($("#cPuestoAutUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Autorizar"); return; }	
							
							$("#cNombreVo").val($("#cNombreVoBoUpdate").val());
							$("#cPaternoVo").val($("#cPaternoVoBoUpdate").val());
							$("#cMaternoVo").val($("#cMaternoVoBoUpdate").val());
							$("#cPuestoVo").val($("#cPuestoVoBoUpdate").val());
							$("#cNombreA").val($("#cNombreAutUpdate").val());
							$("#cPaternoA").val($("#cPaternoAutUpdate").val());
							$("#cMaternoA").val($("#cMaternoAutUpdate").val());
							$("#cPuestoA").val($("#cPuestoAutUpdate").val());
							
							$("#firmanteVoBo").val($("#cNombreVo").val()+" "+$("#cPaternoVo").val()+" "+$("#cMaternoVo").val());
							$("#firmanteAut").val($("#cNombreA").val()+" "+$("#cPaternoA").val()+" "+$("#cMaternoA").val());
							try{
								queryFormPost({
									queryName:"tRectificacionEncabezadoMilFirmante_Update", 
									async : false, 
									callback:function(){
										$("#cNombreVoBoUpdate").val("");
										$("#cPaternoVoBoUpdate").val("");
										$("#cMaternoVoBoUpdate").val("");
										$("#cPuestoVoBoUpdate").val("");
										$("#cNombreAutUpdate").val("");
										$("#cPaternoAutUpdate").val("");
										$("#cMaternoAutUpdate").val("");
										$("#cPuestoAutUpdate").val("");;
										cmdImprimir("RECTIFICACIONMIL");
										
										if (<%=id_oper%>==1){
											parent.document.getElementById("pb_send").disabled=false;
											parent.document.getElementById("pb_save").disabled=true;
										}
									}
								});
							}catch(e){
								alert("No se pudo actualizar los firmantes, intente mas tarde.");
							}
							$(this).dialog("close");
						},
						"Cancelar": function() {
							$(this).dialog("close");
						}
					},
					close: function(){
					}							
				});
 	});  //fin del ready

		
			function cmdRegresar(){
				self.location="../caso/principal.jsp";
			}
		 
		
		function fnExportaSicop() {
				$.blockUI( {
					message : "Procesando espere ......"
				});
				document.ExportaSicop.submit();
				$.unblockUI();
				$("#Exportar").attr('disabled','disabled');
				return true;
		}
		
		function mostrarDialog(){
			$('#dialog').dialog('option', 'modal', true).dialog('open');	 
		}
		 
		function mostrarDialogAut(){
			$('#dialogAut').dialog('option', 'modal', true).dialog('open');	 
		}
			
		function aplicaCont(){
			$("#dialog").dialog("close");
			if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {
				return false;
			}
		   	var strAction="../gstnmngr/RectificacionPresupuestaria";
		   	var cFechaAplicacion=$("#FECHA_APLICACION_CONTABLE").val();
	   		$.ajax({
	   			datatype:"html",
				type: "POST",
				url: strAction,
				data:{aplica:1},
				success: function (data,textStatus){
					$("#mensajeMotor").val(data);
					$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
					
	   				$.unblockUI();
				},
				error: function (par) {alert('<%=mensaje%>');}
			});
		   		
   		}
	
	function aplicaContAut(){
			$("#dialogAut").dialog("close");
			if(!confirm("Enviara el Documento a Autorizar Contablemente.  \n \n  ¿desea continuar?")) {
				return false;
			}
		   	var strAction="../gstnmngr/RectificacionPresupuestaria";
		   	$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{aplica:2},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
						parent.document.getElementById("pb_send").disabled=false;
						parent.document.getElementById("pb_save").disabled=true;
						$.unblockUI();
					},
					error: function (par) {alert('<%=mensaje%>');}
				});
   	}
   	function cmdImprimir(elFormato){
		window.open(
			"../admin/SeguridadCatalogos?"
			+ "catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=PolizaRectificacion.jasper"
			+ "&whereFolio= '" + <%=folio %> +"'"
			+ "&whereTipo= '" + elFormato+"'", 			
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
   	function dialogFirmantes(){
		$("#dialog-firmantesUpdate").dialog("open");
	}
	
	</script>

	</head>
	
	<br/>

	<body id="dt_example">
		<div id="container" class="container" style="width: 100%">
			<div class="card-header"> <h3> Rectificaci&oacute;n Presupuestaria Capitulo Mil </h3> </div>
			<hr class="mt-3">
			
			<%if (id_oper == 1) {%>
				<% if(!(archivoExcel.length > 0)){%>
					<form id="upExcel" name="upExcel" action="../caso/firmardoc?carpeta=2" enctype="multipart/form-data" method="post">
							<input type="file" id="importExcel" name="importExcel" size="32" value=""></input>
							<input type="button" id="ImportarExcel" name="ImportarExcel" value="Subir Excel" onclick="fnImportarExcel();"></input>
					</form>
				<%} %>
			<%} %>
		
		<%if (id_oper == 2) {%>
			<form id="rechazo" name="rechazo">
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label class="form-label">Datos Correctos:</label>	
						<div class="form-check">								
							<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value = "1"/>
							<label for="autorizaRein" class="form-check-label">Si</label>
						</div>
						<div class="form-check">
							<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value = "0"/>								
							<label for="patrimonial" class="form-check-label">No</label>
						</div>							
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12">									
						<label for="motivoRechazo" class="form-label">Motivo:</label>				
						<textarea id="motivoRechazo" name="motivoRechazo" class="form-control form-control-sm" rows="2" cols="50" onkeypress="return event.keyCode!=13"></textarea>			
						<input type="hidden" id="motivoR" name="motivoR" /> 
					</div>
				</div>				
			</form>
			<br/>
			<%}%>
			<%if (id_oper == 3) {%>
				<form id="ExportaSicop" name="ExportaSicop" action="../gstnmngr/RectificacionesSicop" method="POST">
					<input type="hidden" name="mil" id="mil" value="1" />
					<div class="row">
						<div class="col-12 col-lg-5 col-md-5 col-sm-12">
							<input type="button" id="Exportar" value="Exportar SICOP" onclick="fnExportaSicop()" class="btn btn-secondary btn-sm"></input>
						</div>
					</div>
				</form>
			<%}%>
			<!-- ----------------------------------------------- -->
			
			<div id="container2" >
				<form id="salvarRectificacion" name="salvarRectificacion" method="POST" action="../gstnmngr/CapturaRectificacionPresupuestaria" />
					<input type="hidden" id="mensaje" name="mensaje" value="<%=msg %>" />
					<input type="hidden" id="cEjercicio_C" name="cEjercicio_C" value="<%=adecProy.obtenEjercicioFiscal()%>" />
					<input type="hidden" id="cRamo_C" name="cRamo_C" value="<%=usuario.getU_Ramo()== null ? "": usuario.getU_Ramo() %>" />
					<input type="hidden" id="cUnidad_C" name="cUnidad_C" value="<%=usuario.getU_UR()== null ? "": usuario.getU_UR() %>" />
					<input type="hidden" id="operador_C" name="operador_C" value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo": c.getCasoOperacion(0).getResponsable()%>" />
					<input type="hidden" id="u_login_C" name="u_login_C" value="<%=usuario.getLogin() == null ? "usuario nulo": usuario.getLogin()%>" />
					<input type="hidden" id="cCentroContable_C" name="cCentroContable_C" value="<%=cCentroContable == null ? "centro contable nulo": cCentroContable%>" />
					<input type="hidden" id="ID_Caso_C" name = "ID_Caso_C" value = "<%= c.getIdCaso()%>"  />
					<input type="hidden" id="cMes_C" name = "cMes_C" value = "10" />
					<input type="hidden" id="padreRectificacion" name="padreRectificacion" />
					<input type="hidden" id="epGrid" name="epGrid" />
					<input type="hidden" id="epConResto" name="epConResto" />
					<input type="hidden" id="eventoDICE" name="eventoDICE" />
					<input type="hidden" id="eventoDEBE_DECIR" name="eventoDEBE_DECIR" />
					<input type="hidden" id="mesTemporal" name="mesTemporal" /><!-- Este sirve cuando se calcula la EP para ponerle su mes original y no el calculado -->
					<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo" : c.getCasoOperacion(0).getResponsable()%>" />
					<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
					<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" />	
					<input type="hidden" name="FECHA_APLICACION_CONTABLE" id="FECHA_APLICACION_CONTABLE" value="<%=today%>" readonly="readonly" maxlength="10" size="10"/>
					<input type="hidden" name="FOLIO" id="FOLIO" size="15" value="<%=c.getFolio() %>">
					
					<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
					<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
					<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
					<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
					<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
					<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
					<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
					<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
					<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
					<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
					<input type="hidden" id="folioRectif" name="folioRectif" size=40 value="<%=folio%>">			
					
					<div id="container" class="container" style="width: 100%">
					
						<div class="row">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">											
								<label for="folio" class="form-label">*Folio</label>
								<input type="text" name="folio" id="folio" class="form-control form-control-sm" readOnly value="<%=c.getFolio()%>" />	
								<%if(id_oper==5){ %>								
									<a href="#" onclick="window.open('../reportes?cmd=<%=GestionInterface.RPT_MOVIMIENTOS_RECTMIL%>&folio_rectificacion=<%=c.getFolio()%>&nfolio=<%=folio %>');" >Oficio de Rectificaci&oacute;n</a>								
								<%} %>						
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<label for="fExp" class="form-label">*F. Expedici&oacute;n</label>
								<input type="text" id="fExp" name="fExp" class="form-control form-control-sm" readOnly />							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<label for="fApl" class="form-label">*Fecha Aplicaci&oacute;n</label>							
								<input type="text" id="fApl" name="fApl" class="form-control form-control-sm" readOnly /> 
							</div>
						</div>	
						
						<div class="row">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
								<label for="CatMovimientoRectificacion" class="form-label">Movto.</label>
								<div class="input-group">
									<input type="text" class="form-control AyudaSyC form-control-sm" name="CatMovimientoRectificacion" id="CatMovimientoRectificacion" readOnly onKeyDown="return false;" value=""/>
								</div>							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-7 col-md-7 col-sm-12">									
								<label for="origPresupuesto" class="form-label">*Origen ppto.</label>														
								<select id="origPresupuesto" name="origPresupuesto" class="form-select form-select-sm">
									<option value="Z:">A</option>
									<option value="Y:">B</option>
									<option value="X:">C</option>
									<option value="xx" selected>--</option>
								</select> 
							</div>
						</div>											
						
						<div class="row">
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>					
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<label for="oficioRectif" class="form-label">Oficio rectif.</label>	
								<input type="text" id="oficioRectif" name="oficioRectif" class="form-control form-control-sm" readOnly value="<%=c.getFolio()%>"/>							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<label for="ctr_int" class="form-label">CTR_INT</label>	
								<input type="text" id="ctr_int" name="ctr_int" class="form-control form-control-sm"/>							
							</div>										
						</div>	
						
						<div class="row">						
							<div class="col-12 col-lg-1 col-md-1 col-sm-12">
							</div>
							<div class="col-12 col-lg-10 col-md-10 col-sm-12">								
								<label for="concepto" class="form-label">*Concepto</label>							
								<textarea id="concepto" name="concepto" class="form-control form-control-sm" rows="3" cols="50" onkeypress="return event.keyCode!=13"></textarea>
							</div>
						</div>
						
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
								<label for="totalDice" class="form-label">Total DICE</label>	
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>						
									<input type="text" id="totalDice" name="totalDice" class="form-control form-control-sm" onKeyDown="return false;" readOnly/> 
									<input id="totalDiceValor" name="totalDiceValor" type="hidden"/>
								</div>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">					
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
								<label for="totalDebeDecir" class="form-label">Total DEBE DECIR</label>
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>									
									<input type="text" id="totalDebeDecir" name="totalDebeDecir" class="form-control form-control-sm" onKeyDown="return false;" readOnly/>
								</div> 							
							</div>
						</div>	
						
						<table style="width: 850px;" border="0">
							<tr>
								<td>													
									<table>
										<tr>
											<td>
												<div id="divImprimePoliza">
													<img src="../Generador/imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('RECTIFICACIONMIL');" > Poliza
												</div>
											</td>
											<td>
												<span id="EditaFirmas" ><a href="#" onclick="dialogFirmantes();">Firmas*</a></span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
					
					<div class="container" style="width: 100%">
						<h6> CLC a Rectificar </h6>
						<hr class="mt-3">
						
						<div class="row">												
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">						
								<label for="dTipoPago" class="form-label">Tipo CLC:</label>		
								<input type="text" id="dTipoPago" name="dTipoPago" class="form-control form-control-sm" readOnly/>							
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">					
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<label for="nFolioSICOP" class="form-label">Folio CLC:</label>		
								<input type="text" id="nFolioSICOP" name="nFolioSICOP" class="form-control form-control-sm" readOnly/>												
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<label for="caNoContrarrecibo" class="form-label">No. CxP</label>	
								<input type="text" id="caNoContrarrecibo" name="caNoContrarrecibo" class="form-control form-control-sm" readOnly/>							 					
							</div>
						</div>	
						
						<div class="row">													
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
								<label for="nFolioSIAFF" class="form-label">Folio SIAFF:</label>	
								<input type="text" id="nFolioSIAFF" name="nFolioSIAFF" class="form-control form-control-sm" readOnly/>							
							</div>						
						</div>
						
						<br/>

						<table id="tblPagadoFiltrado" class="table table-striped table-sm">
							<thead>
								<tr>
									<td style="text-align: center;">Renglon</td>
									<td style="text-align: center;">Mes</td>
									<td style="text-align: center;">EP</td>
									<td style="text-align: center;">Evento</td>
									<td style="text-align: center;">Importe Neto</td>
									<td style="text-align: center;">Tipo Concepto</td>
									<td style="text-align: center;">Tipo Movimiento</td>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
																				
						<%if (id_oper == 2 && id_oper==3) { //temporalmente lo pongo así para que nunca se muestre, después veo si quito todo esto%>
						<fieldset>
							<legend>
								Revisor
							</legend>
							<div id="filtro">
								<fieldset id="radiobuttons">
									<div id="buttongroup1" align="center">
										Avanza a Autorizaci&oacute;n:
										<p>
											<input type="radio" name="esquemaDocumento" id="avanza" value="si"> Si
											<input type="radio" name="esquemaDocumento" id="regresa" value="no"> No
										</p>
									</div>
								</fieldset>
							</div>
							<div>
								<table>
									<tr>
										<td>
											Motivo de rechazo:
										</td>
										<td style="text-align: left; width: 75%">
											<textarea id="motivoRechazo" name="motivoRechazo" rows="3" cols="54"></textarea>
										</td>
									</tr>
								</table>
							</div>
						</fieldset>
						<%}%>
					</div>
				</form>
			</div>
			
			<br/>
			
			<%if(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1).length>0 && id_oper==1){%>
				<form id="limpiaDatos" name="limpiaDatos" method="POST" action="../gstnmngr/RectificacionPresupuestaria" />
					<input type="hidden" name="folioRectificacion" id="folioRectificacion" value="<%=folio%>"/>
					<input type="hidden" name="borraTodo" id="borraTodo" value="1"/>
					<input type="button" name="borrarTodo" id="borrarTodo" value="Limpiar Datos" onclick="limpiarDatos();" class="btn btn-secondary btn-sm"/>
				</form>
			<%}%>
			<!-- DIALOGOS (POPUPS) QUE SALEN PARA HACER LAS APLICACIONES CONTABLES AL ESTILO DE REINTEGROS FMC 2/NOV -->
			
			<%if(id_oper==3){ %>				
				<input type="button" id="Cancelar" value="Cancelar Documento" onclick="javascript:cancelarDoc();" class="btn btn-outline-danger btn-sm" /><br />
			<%} %>
			
			<% if(mensaje==null || "".equals(mensaje)){%>
				<div id="dialog" title="Operaciones de Rectificaciones" class="container" style="width: 50%">
					<p class="text-sm-start">Aplica apartado de rectificaciones del presupuesto</p>
					<input type="button" id="aplicarContable" value="Aplicar contablemente" onclick="aplicaCont();" class="btn btn-secondary btn-sm"/>
				</div>
				
				<div id="dialogAut" title="Detalle de Rectificaciones" class="container" style="width: 50%">
					<p class="text-sm-start">Autorización de rectificaciones del presupuesto</p>
					<input type="button" id="aplicarContableAut" value="Autorizar contablemente" onclick="aplicaContAut()" class="btn btn-secondary btn-sm"/>
				</div>			
			<%} %>
			<!-- -------------------------------------------- -->
			</div>
			<div id="dialogMensaje" title="Mensajes del sistema Oficio de Rectificaciones" class="container">
				<p>Mensajes del sistema </p> <a rel=""></a>
				<textarea id="mensajeError" name="mensajeError" rows="8" cols="100" class="form-control form-control-sm"></textarea>
			</div>
			
			<div id="dialogMotor" title="Mensajes del sistema Oficio de Rectificaciones" class="container">
				<p>Mensajes del sistema </p> <a rel=""></a>
				<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="100" class="form-control form-control-sm"></textarea>
			</div>
			
			<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
				
				<h5> Datos  VºBº</h5>
				<hr class="mt-3">
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cNombreVoBoUpdate" class="form-label">Nombre:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cPaternoVoBoUpdate" class="form-label">Apellido Paterno:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cMaternoVoBoUpdate" class="form-label">Apellido Materno:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cPuestoVoBoUpdate" class="form-label">Puesto:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
							<input type="text" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<h5> Datos  Autoriza</h5>
				<hr class="mt-3">
				
				<div class="row">			
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cNombreAutUpdate" class="form-label">Nombre:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cNombreAutUpdate" name="cNombreAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cPaternoAutUpdate" class="form-label">Apellido Paterno:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cPaternoAutUpdate" name="cPaternoAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>				
					
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cMaternoAutUpdate" class="form-label">Apellido Materno:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cMaternoAutUpdate" name="cMaternoAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cPuestoAutUpdate" class="form-label">Puesto:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
							<input type="text" id="cPuestoAutUpdate" name="cPuestoAutUpdate" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>						

			</div>
		</body>
</html>
