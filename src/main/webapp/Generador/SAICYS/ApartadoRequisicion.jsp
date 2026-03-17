<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.NegativaBoton"%>
<%@ page import="java.util.*"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user = usuario.getLogin();
	String idRol = "0";
	
	Map rol = usuario.getRoles();
	String roles = "";
	
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Calendar c2= Calendar.getInstance();
	c2.add(Calendar.DATE, 8);
	String vigencia=sdf.format(c2.getTime());
	String today= sdf.format(c1.getTime());
	int mesActual=c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String cCentroContable="";
	String cUR = "";
	String cRamo = "";
	String grupoMat="";
	boolean bAplicadoCont=false;
	//Se pone temporalmente en true, pero debe inicializarse en false cuando 
	//Se hayan agregado grupos
	boolean isRecMat=false;
	
	Iterator it1 = rol.entrySet().iterator();
	while (it1.hasNext()) {
		Map.Entry r = (Map.Entry)it1.next();
		roles += r.getKey().toString()+",";
	}
	if(roles.length()>0){
		roles = roles.substring(0,roles.length()-1);
	}
	
	Map grupo=usuario.getGrupos();
	Iterator it2 = grupo.entrySet().iterator();
	while (it2.hasNext()) {
		Map.Entry r = (Map.Entry)it2.next();		
		grupoMat=(String)r.getKey();
		
		if ("RECURSOS_MATERIALES".equalsIgnoreCase(grupoMat)) {
			isRecMat=true;
			break;
		}
	}
	
	
	System.out.println("valor del grupo" + grupoMat );
	//Valida Centro de Costos
	cCentroContable = "";
	if (usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
        cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR();
	String cEjercicio = "";
	String cIdTipoSolicitud = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";

	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null) {
		cEjercicio = 			(String) session.getAttribute(GestionInterface.ATT_ReqEjercicio);
		cIdTipoSolicitud = 		(String) session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
		cIdUnidadEjecutora = 	(String) session.getAttribute(GestionInterface.ATT_ReqUnidadEjec);
		nIdConsecutivo = 		(String) session.getAttribute(GestionInterface.ATT_ReqConsecutivo);
	} 
	else
		response.sendRedirect("Requisiciones.jsp?tab=0");
	
	String role = "";
	NegativaBoton NegBoton= new NegativaBoton();
	NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
	int editar=0;
	int imgAprobar=0;
	int imgDevolver=0;
	
	Map botones=nb.getBotones(roles,"Requisiciones","ApartadoRequisiciones");
	Iterator btn = botones.entrySet().iterator();
	
	String js = "";
	while (btn.hasNext()) {
		Map.Entry b = (Map.Entry)btn.next();
		
		//js += "$('#" + b.getValue() + "').attr('disabled', true); \n";
		js +="$('#"+ b.getValue() + "').css('display', 'none');\n";
		System.out.println("js : "+js);
		String img=(String) b.getValue();
		/*if ("imgApartar".equals(img)) {
			js +="$('#spanApartar').css('display', 'none');\n";
		}
		if ("imgAutorizar".equals(img)) {
			js +="$('#spanAutorizar').css('display', 'none');\n";
		}*/
		if ("trEditar".equals(img)) {
			editar=1; 
		}
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Presupuesto</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		
		
		<script type="text/javascript" charset="utf-8">
			var totalEP = 0;
			var vcaNoCompromiso;
			var vEstado;
			
			var CASO_ATT = 0;
			var CASO_BD  = 1;
			var CASO_CREADO  = 2;
			
			$(document).ready(function() {
				$("#tbs").val(6);
				showHideTabs();
				$("#usuarioRoleSolicitud").val('<%=roles%>');
				$("#usuarioLogin").val('<%=usuario.getLogin()%>');
				$("#adjuntoFieldset").hide();
				
				$("input.AyudaSyC").subIniciaDlg();
			    $("input.autoCompletaSyC").subIniciaAutoCompleta();
			    $("#esperar").dialog({
					autoOpen : false,
					height : "310px",
					width : "400 px",
					modal : true,
					open: function(event, ui){
						$(".ui-dialog-titlebar").hide();
					},
					close : function() {
					}
				});
								
				//Prepara campos de encabezado y los carga, tambien carga hiddens
				//y desactiva botones de acuerdo al idEstado de la Solicitud
				init(false);
				//Con Java genera la desactivación jQuery de botones
				<%=js%>
				//Llena de DT con el presupuesto de la EP
				initDTPresupuesto();
				
				//oDTPresupuesto es una global hadle para la dtPresupuesto
				oDTPresupuesto = $("#tblPresupuesto").dataTable();
				
				//Listeners de botones aprobar, devolver y ...
				$("#imgApartar").click( imgApartarClickHandler );
				$("#imgAutorizar").click( autorizarApartado );
				$("#imgReEnviarEmail").click( sendEmail );
				
				$("#imgExtensionVigencia").click( imgVigenciaClickHandler );
				$("#imgDevolverApartado").click( imgDevolverClickHandler );
				//devuelvePrecompromiso
				//Para Fecha de vencimiento
				cambiaColorFecha();
				
				muestraEtiqueta300SalMin();
				createDialogQuestionnaire();
			});//Fin del document ready
			
			function init(isUpdating) {
				//Si no está llamando la funcion para actualizar la vista,
				//estos valores no pueden haber cambiado.
				if (!isUpdating) {
					document.getElementById("lblUnidadEjecutora")	.style.readonly=true;
					document.getElementById("lblRequisicion")		.style.readonly=true;
					document.getElementById("lblEstado")			.style.readonly=true;
					document.getElementById("lblDescripcion")		.style.readonly=true;
					document.getElementById("lblPartida")			.style.readonly=true;
					document.getElementById("lblMesRequisicion")	.style.readonly=true;
					document.getElementById("lblNotas")				.style.readonly=true;
					
					queryFormPost({ 
						queryName: "mSolicitud_cIdSubPartidaRead",  
						async:false,
						callback:function(){
							showQuestionnaire();
						}
					});
					
					queryFormPost("cg_roleRead", {async: false});
					//Montos en encabezado
					queryFormPost("mSolicitudLineas_MontosRead", { async:false });
				}
				
				//Encabezado y algunos hiddens
				queryFormPost({
					queryName:"mSolicitud_LabelRead",  
					async:false,
					callback:function(){
						showQuestionnaire();
					}
				});
				
				$("#lblUnidadUsuarioAux").val($("#lblUnidadUsuario").val());
				queryFormPost("mSolicitudDescripcionUnidadAux", { async:false });
				
				//Si no hay notas, esconde el campo
				if($.trim($("#lblNotas").val()).length == 0 ){
					$("#trNotas").hide();
				}
				
				//Guarda el estado de la solicitud en var global
				vEstado = parseInt($("#nIdEstadoSolicitud").val(),10);
				
				if (vEstado == 1 || vEstado==5 || vEstado==6 || vEstado==7){
				//Desactiva devolver si la requisicion esta en CAPTURADO (1) o en habilitado por consolidado
					$("#imgDevolverApartado").hide();
					if(vEstado == 1){
						$("#imgApartar").show();
						
					}
				}
				//Se deshabilita el boton y se esconde adjunto en caso que ya se haya enviado a ventanilla o viceversa
				if ((vEstado > 1 && vEstado<5) || vEstado==6 || vEstado==7 ) {
					document.getElementById("imgApartar").disabled = true;
					$("#adjuntoFieldset").hide();
					$("#imgApartar").hide();
					$("#imgDevolverApartado").show();
					if(vEstado==6 || vEstado==7){
						$("#imgDevolverApartado").hide();
					}
				}
				else{
					document.getElementById("imgApartar").disabled = false;
					showQuestionnaire();
				}
				
				//Si aun no se ha apartado, esconde la vigencia
				if (vEstado < 3) {
					$("#vigenciaFieldset").hide();
				}
				
				//Esconde los letreros de adjunto y extension de vigencia, se muestran bajo condiciones
				$("#adjuntoNotice").hide();
				$("#extensionNotice").hide();
				
				///Mostrar y ocultar el botón de autorizar 
				if(vEstado!=2 ){
					$("#imgAutorizar").hide();
					
				}else{
					$("#imgAutorizar").show();
				}
				///Mostrar y ocultar el botón de envio de correos
				if(vEstado==6){
					$("#imgReEnviarEmail").show();
				}else{
					$("#imgReEnviarEmail").hide();
				}
				if (vEstado >= 3) {
					$("#vigenciaFieldset").hide();
					queryFormPost("mSolicitudApartadoVigencia", { async:false });
					$("#lblVigencia").html($("#venceApartado").val());
					
					
					//Fechas de vigencia y ultimo = 31 dic para ver si puede extender vigencia
					var aVence = $("#venceApartado").val().split("-");
					//new Date(year, month, day, hours, minutes, seconds, milliseconds)
					
					var vence = new Date(aVence[0], aVence[1]-1 , aVence[2]);
					var ultimo = new Date($("#cEjercicio").val(), 12 - 1, 31);
					var today = new Date();
					today.setHours(0,0,0,0);
					
					
					//Si el apartado ya vence el ultimo dia del año, desactiva la extension
					
					if(vEstado!=5){
						if(parseInt(vence.getTime(),10) == parseInt(ultimo.getTime(),10)){
						   	document.getElementById("imgExtensionVigencia").disabled = true;
							$("#vigenciaFieldset").hide();
							$("#extensionNoticeVigencia").css("visibility","visible");
						}
					}else{
							document.getElementById("imgExtensionVigencia").disabled = true;
							$("#vigenciaFieldset").hide();
					}
					//Si el apartado ya está vencido, desactiva el boton y lo desaparece
					if(vEstado!=5){
						if(parseInt(vence.getTime(),10) <  parseInt(today.getTime(),10)){
							document.getElementById("imgExtensionVigencia").disabled = true;
							$("#vigenciaFieldset").hide();
							$("#extensionNoticeVigencia").css("visibility","visible");
							
						}	
					}else{
						document.getElementById("imgExtensionVigencia").disabled = true;
							$("#vigenciaFieldset").hide();
					}
					
					queryFormPost("mSolicitudApartadoOperacion", { async:false });
					//Si el caso esta en operacion == solicitud_de_vigencia (4) se muestra letrero y
					//desactiva boton de extension
					if ($("#nCasoOperacion").val() == 4){
						document.getElementById("imgExtensionVigencia").disabled = true;
						$("#extensionNotice").show();
						$("#subeAdjunto").css("display","none");
					}
					
				}
				
				
				//Si ya existe un caso, puede haber adjunto
				if ($("#folioCasoApartado").val() != null) {
					queryFormPost("apartadoComprobante", { async:false });
					
					//Si hay adjunto, se muestra letrero
					if ($("#nPaginas").val() > 0)
						$("#adjuntoNotice").show();
						
				}
				
				//Lee la configuración de mes (desfase)
				//queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
				if (!isUpdating && parseInt($("#nIdEstadoSolicitud").val(),10)!=3) {
					if(getUrlParameter("status") == 1){
						swal("Apartado enviado exitosamente.",{icon:"info",button: "Cerrar"});
						var folio=getUrlParameter("folio");
						var consecutivoFolio=getUrlParameter("folio").substring(getUrlParameter("folio").lastIndexOf("-")+1);
						$("#nFolioApartado").val(consecutivoFolio);
						$("#folioCasoApartado").val(folio);
						//Actualiza la tabla de Requisición con los folios
						queryFormPost("mSolicitudApartadoUpdate", {async: false });
						$("#autorizaApartado").val(1);
							
						//Guarda en la Bitácora
						$("#cAccion").val("APARTA_REQUISICION");
						$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
						queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
								
						//Actualiza Caratula
						init(true);	
								
					}
					else if (getUrlParameter("status") == -1){
						swal("Ha ocurrido un error al crear el folio de apartado, devuelva el proceso y luego vuelva a avanzar el proceso.",{icon:"warning",button: "Cerrar"});
						queryFormPost("mSolicitudApartadoUpdateonError", {async: false });
						
					}

				}
			}
			
			function initDTPresupuesto() {
				var url = "cEjercicio='" + $("#cEjercicio").val() + "' " +
					"AND cIdUnidadEjecutora='" + $("#cIdUnidadEjecutora").val() + "' " +
					"AND cIdSolicitud='" + $("#cIdSolicitud").val() + "' ";
				
				oDTPresupuesto = $("#tblPresupuesto").dataTable({
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
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
					bAutoWidth: false,
					sScrollX: "100%",
					//sScrollY: 200,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true, 
					"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vPresupuestoSolicitudEP&qw="+url,
					"aoColumns": [
						{ sName: "ClaveSIAFF" ,bSortable: false},
						{ sName: "ClaveInterna",bSortable: false },
						{ sName: "MontoEnero" ,bSortable: false},
						{ sName: "MontoFebrero" ,bSortable: false},
						{ sName: "MontoMarzo" ,bSortable: false},
						{ sName: "MontoAbril",bSortable: false },
						{ sName: "MontoMayo" ,bSortable: false},
						{ sName: "MontoJunio",bSortable: false },
						{ sName: "MontoJulio" ,bSortable: false},
						{ sName: "MontoAgosto" ,bSortable: false},
						{ sName: "MontoSeptiembre" ,bSortable: false},
						{ sName: "MontoOctubre",bSortable: false },
						{ sName: "MontoNoviembre" ,bSortable: false},
						{ sName: "MontoDiciembre" ,bSortable: false},
						{ sName: "MontoAnual",bSortable: false }]
	      		});
			}
			
			function guardarApartado_RT(){
				var  imgAprobar='<%=imgAprobar%>';
				var nMes = "<%=today%>";
				//Se pone el centro contable de la requi no del usuario
				$("#cCentroContable").val($("#cCentroContableUEReq").val());
				nMes = nMes.substring(5, 7) ;
				$("#nMes").val( nMes );
			    if (imgAprobar==0) {
			    	$.ajax({
					url: '../../servlet/SolicitudServlet', 
					type:'get',
					async: false,
					data:"operacion=7&nFolioApartado=0"
					+"&cEjercicio="+$("#cEjercicio").val()
					+"&cIdSolicitud="+$("#cIdSolicitud").val()
					+"&caNoPreCompromiso="+$("#caNoPreCompromiso").val()
					+"&cTipoPoliza="+$("#cTipoPoliza").val()
					+"&fAplicacion="+$("#fAplicacion").val()
					+"&fCarga="+$("#fCarga").val()
					+"&fVigencia="+$("#fVigencia").val()
					+"&nEnviadoSICOP="+$("#nEnviadoSICOP").val()
					+"&nMes="+$("#nMes").val()
					+"&nStatusFinanciero="+$("#nStatusFinanciero").val(), 
					dataType: 'json', 
					success: function(json){
						folioPre  = json[0].Folio1;
						folioCaso = json[0].Folio2;
						//Si no hay folio en la respuesta json
						if (folioPre == -1) {
							swal(json[0].msg,{icon:"warning",button: "Cerrar"});
							return -1;
						}
						//Guarda el folio del caso
						else {
							swal(json[0].msg,{icon:"info",button: "Cerrar"});
							$("#nFolioApartado").val(folioPre);
							$("#folioCasoApartado").val(folioCaso);
						}
						$("#aplicaCuestionario").val($("#applyQuestionnaire").val());
						//Sube el adjunto
						//$("#upform").submit();
					}
				});
			    }else {
					swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
				}
			}
			
			function guardarApartado(){
				var  imgAprobar='<%=imgAprobar%>';
			    if (imgAprobar==0) {
					//Trata de recuperar el caso desde sesion, BD o genera uno nuevo en caso de que no lo tenga
					$.ajax({
						url: '../../servlet/SolicitudServlet', 
						type:'get', 
						async: false, 
						data:'operacion=0'
							+"&cIdSolicitud="+$("#cIdSolicitud").val(), 
						dataType: 'json', 
						success: function(json){
							folioPre = -1;
							folioPre  = json[0].Folio1;
							folioCaso = json[0].Folio2;
							//Si no hay folio en la respuesta json
							if (folioPre == -1) {
								swal("Ha ocurrido un error al crear el folio de apartado, devuelva el proceso y luego vuelva a avanzar el proceso",{icon:"warning",button: "Cerrar"});
								return -1;
							}
							//Guarda el folio del caso
							else {
								$("#nFolioApartado").val(folioPre);
								$("#folioCasoApartado").val(folioCaso);
								window.location = "Requisiciones.jsp?tab=6";
							}
						},
						error: function(json){
							swal("Ha ocurrido un error al crear el folio de apartado, devuelva el proceso y luego vuelva a avanzar el proceso",{icon:"error",button: "Cerrar"});
							if($("#nIdEstadoSolicitud").val()==5){	
								queryFormPost("mSolicitudUpdateonErrorConsolidada", {async: false });
							}else{
								queryFormPost("mSolicitudApartadoUpdateonError", {async: false });
							}
						}
					});
				}else {
					swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
				}
			}
			function devuelveRequi(){
				var  imgDevolver='<%=imgDevolver%>';
			    if (imgDevolver==0) {
			    	$.ajax({
						url: '../../servlet/SolicitudServlet', 
						type:'get',
						async: false,
						data:"operacion=8&nFolioApartado=0"
						+"&cEjercicio="+$("#cEjercicio").val()
						+"&cIdSolicitud="+$("#cIdSolicitud").val()
						+"&cIdTipoSolicitud="+$("#cIdTipoSolicitud").val()
						+"&caNoPreCompromiso="+$("#caNoPreCompromiso").val(), 
						dataType: 'json', 
						success: function(json){
							swal(json[0].msg,{icon:"info",button: "Cerrar"});
							init(true);
							initDTPresupuesto()
						}
					});
			    }else{
			    	swal("No tiene permisos para realizar esta acción",{icon:"info",button: "Cerrar"});
					return;
			    }
			}
			function devuelveApartado(){
				//if (<%=isRecMat%>) {
					var  imgDevolver='<%=imgDevolver%>';
				    if (imgDevolver==0) {
						
						//En caso de ser una solicitud de Modificación, se valida que no este vinculada a algun modificatorio
						if ($("#cIdTipoSolicitud").val() == 'RM') {
				
							$("#cIdSolicitudMod").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());					
							$("#existeMod").val('');
							queryFormPost("mPedidoModificadoPartidaSolicitudRead", { async : false});
							if ($("#existeMod").val() == 'EXISTE'){
								swal("No se puede devolver la requicisión porque está asociada a un pedido modificatorio",{icon:"info",button: "Cerrar"});
								return;
							}
							
							$("#existeMod").val('');
							queryFormPost("mContratoModificadoPartidaSolicitudRead", { async : false});
							if ($("#existeMod").val() == 'EXISTE'){
								swal("No se puede devolver la requicisión porque está asociada a un contrato modificatorio",{icon:"info",button: "Cerrar"});
								return;
							}
						}
						//Recarga el valor de vEstado
						queryFormPost({
							queryName:"mSolicitud_LabelRead",  
							async:false,
							callback:function(){
								showQuestionnaire();
							}
						});
						
						vEstado = parseInt($("#nIdEstadoSolicitud").val(),10);
						//valida si la RT ya está aprobada, no se puede devolver por cuestión de que se sube a SICOP
						if ($("#cIdTipoSolicitud").val() == 'RT') {
							if(vEstado==3){
								swal("No se puede devolver por cuestión de que el apartado se sube a SICOP.",{icon:"info",button: "Cerrar"});
								return;
							}else{
								//validar que no este integrada la requi
								queryFormPost("mExisteRequiIntegradaRead", {async : false,
									callback : function() 
									{
										if($("#existeRequiIntegrada").val()==1){
											swal("No se puede devolver porque ya está integrada la requisici\u00f3n.",{icon:"info",button: "Cerrar"});
											return;
										}
									}
								});
							}
							
						}
				    	
						//Si el estado es "CAPTURADO"
						//Esto ya no es asi
						if (vEstado == 1) {
							swal("No se puede devolver porque el apartado fue rechazado por el usuario en ventanilla.",{icon:"info",button: "Cerrar"});
							return;
						}
						
						//Si el pedido está aprobado 
						if (vEstado > 1) {
							if($("#existeRequiIntegrada").val()==1){
								return;
							}
							if (vEstado == 3) {
								queryFormPost("mApartadoConsolidadoSolicitud", {async: false});
								//Si ya hay lineas en el consolidado
								if ($("#nLineasConsolidado").val() > 0) {
									swal("No se puede devolver el apartado porque ya está en un consolidado.",{icon:"info",button: "Cerrar"});
									return;
								}
							}
							
							//Estado 2 o 3
							swal({
								title: "Está seguro que quiere devolver el apartado?",
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
									//se checa si el apartado ya ha sido cancelado previamente por vencimiento de vigencia
									queryFormPost("mApartadosCancelados", {async: false});
									if(parseInt($("#canceladoApartado").val(),10) > 0){
										swal("El apartado ya ha sido cancelado previamente,se actualizara el estado de la requisición a capturado.",{icon:"info",button: "Cerrar"});
										$("#nIdEstadoSolicitud").val("1");
										$("#nIdEstadoPrecomprometido").val("1");
										queryFormPost("mSolicitudEstadoUpdate", {async: false});
										queryFormPost("mSolicitudPrecomprometidoUpdate", {async: false});
										location.reload();
										return;
									}
								
								  	//Bitácora
									$("#cAccion").val("DEVUELVE_REQUISICION");
									$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
									queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
																
									if (vEstado == 2) {
										//Retrocede el caso
										$.ajax({
											url: '../../servlet/SolicitudServlet' , 
											type:'get' , 
											async: false,
											data:'operacion=4', 
											dataType: 'json', 
											success: function(json){
												//sp_deleteEyDApartado solamente pone la req en estados 1. MF
												queryFormPost("sp_deleteEyDApartado", {async: false,
													callback : function() {
														swal("Requisici\u00f3n devuelta correctamente.",{icon:"info",button: "Cerrar"});
														init(true);
													}
												} );
											}
										});
									}else if (vEstado == 3) {
										$.ajax({
											url: '../../servlet/SolicitudServlet' , 
											type:'get' , 
											async: false, 
											data:"operacion=5&nFolioApartado="+$("#nFolioApartado").val()+"&cEjercicio="+$("#cEjercicio").val()+"&cIdSolicitud="+$("#cIdSolicitud").val(), 
											dataType: 'json', 
											success: function(json){
												var mensaje = json[0].Contable1;
												
												if (json[0].Success == "true"){
													swal(mensaje,{icon:"info",button: "Cerrar"});
													oDTPresupuesto = $("#tblPresupuesto").dataTable();
													cambiaColorFecha();
													muestraEtiqueta300SalMin();
													init(true);
												}
												else{
													swal(mensaje,{icon:"info",button: "Cerrar"});
												}
											}
										});
									}								
									init(true);
								}
							});//termina el swal
						}else
							return;
					}
					else {
						swal("No tiene permisos para realizar esta acci\u00f3n.",{icon:"info",button: "Cerrar"});
						return;
					}
				//}
				init(true);
			}
			
			function solicitarExtension(){
				$.ajax({
					url: '../../servlet/SolicitudServlet', 
					type:'get', 
					async: false, 
					data:'operacion=2', 
					dataType: 'json', 
					//Si el ajax fue success
					success: function(json){
						var result  = json[0].Success;
						
						//Si no hay folio en la respuesta json
						if (result == "true") {
							swal("Enviado a ventanilla para extension de vigencia.",{icon:"info",button: "Cerrar"});
							init(true);
							$("#upform2").submit();
					      	window.location = "Requisiciones.jsp?tab=" + 6;
							//Bitácora
							$("#cAccion").val("AMPLIACION_VIGENCIA");
							$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
						}
					}
				});
			}
			
			function imgApartarClickHandler() {
				//Calcula el total apartado desde la tabla de EPs
				var presupuesto = 0;
				var nodos = oDTPresupuesto.fnGetNodes();
				var col = ($(nodos[0]).children().length) - 1;
				
				$(nodos).each(function(index){
					presupuesto += parseFloat($(this).children().eq(col).html().replace(/,/g, "").replace("$","").replace(" ",""));
				});
				var neto = parseFloat($("#montoNeto").val().replace("$","").replace(/,/g, "").replace(" ",""));
				//Se permite una discrepancia no mayor a .01 centavos por la imprecisión binaria de floats
				if (Math.abs(presupuesto - neto) > 0.0001 && $("#cIdCapitulo").val()!='1') {
					swal("El importe total es mayor al monto presupuestado.",{icon:"info",button: "Cerrar"});
					return -1;
				}
				guardarApartado();
			}
			
			function imgDevolverClickHandler(){
				devuelveRequi();
			}
			
			function imgVigenciaClickHandler(){
			
			
			
			
				//Usuario creado o ADMIN_RECMAT				
				if($('#cIdUsuarioCreacion').val().toLowerCase() == $("#usuarioLogin").val().toLowerCase() || 
				   ($('#usuarioRoleSolicitud').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
				  				   
				   //revisamos si la requisicion ya tiene un pedido o contrato y esta comprometida
				  		if($("#cIdTipoSolicitud").val()=='RS'){
				    	queryFormPost("existeCompromisoServicio", { async : false});
				    	 if(parseInt($("#compromisoServicio").val(),10) > 0){
				    		 swal("No se puede extender la Vigencia, la Requisición ya tiene un Compromiso.",{icon:"info",button: "Cerrar"});
				    	   	 return;
				    	 }
				  
					  }else if($("#cIdTipoSolicitud").val()=='RC'){
				   		queryFormPost("existeCompromisoCompras", { async : false});
				   		if(parseInt($("#compromisoCompras").val(),10) > 0){
				   			swal("No se puede extender la Vigencia, la Requisición ya tiene un Compromiso.",{icon:"info",button: "Cerrar"});
				    	 	return;
				    	 }
				  
				  }
				   
										
					var aDt = $("#venceApartado").val().split("-");
					var vence = new Date(aDt[0],aDt[1]-1,aDt[2]);
					
					var fin = new Date($("#cEjercicio").val(),11,31);
	
					if(vence.getTime() == fin.getTime()){
						swal("No se puede extender la vigencia más alla del 31 de diciembre.",{icon:"info",button: "Cerrar"});
					}else{
					
						if ($("#uploadfile2").val() != ""  || (  $("#uploadfile").val() == "" && $("#applyQuestionnaire").val() == "S"  ) ) {
								solicitarExtension();
						} else {
							swal("Debe adjuntar una Requisición.",{icon:"info",button: "Cerrar"});
						}					  
					 }
				}
				else
					swal("Solo el usuario creador de la Requisición puede solicitar la ampliación de la vigencia, el usuario creador es: " + $('#cIdUsuarioCreacion').val().toUpperCase(),{icon:"info",button: "Cerrar"});
			}
			
			
			function openPDF(ext){
				if($("#nIdEstadoSolicitud").val() != "3" && $("#nIdEstadoSolicitud").val() != "4" ){
				  $("#cIdSolicitudRep").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());
			    //se revisa si ya existe una linea en la tabla de msolicitudlineasapartado
			  	queryFormPost("numeroLineasApartadoSolicitud", { async : false});
				 	if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)> 0){
				  //checamos si el numero de lineas de msolicitud es igual a msolicitudlineasapartado
				   		queryFormPost("numeroLineasSolicitud", { async : false});
				   		if(parseInt($("#numeroLineasApartadoSolicitud").val(),10)== parseInt($("#numeroLineasSolicitud").val(),10)){
				   		      queryFormPost("sumaLineasSolicitud", { async : false}); 
					   		   queryFormPost("sumaLineasApartadoSolicitudLineas", { async : false});
					   		   var lineasSolicitud=parseFloat($("#sumaLineasSolicitud").val());
					   		   var lineasApartadoSolicitud=parseFloat($("#sumaLineasApartadoSolicitudLineas").val());
					   		    //var total=lineasSolicitud-lineasApartadoSolicitud;
					   		  if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 1.0){
				   		    	//nos puede ser mayor o menor a 1.0
				   		    	swal("La suma de las líneas presupuestadas no es igual a la suma de las líneas de solicitud, favor de revisar.",{icon:"info",button: "Cerrar"});
				   		    	return;
				   		   }
				   		    else 				   		    
				   		      if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 0.0001){
				   		      //se ajusta la requisiscion
				   		       queryFormPost("pa_ajustePresupuestoRequisiscion", {async: false});
				   		      
				   		      }
				   		    
				   		    
				   		    }else{
				   		    	swal("La requisición no se puede imprimir porque no ha presupuestado el total de las líneas de la requisición.",{icon:"info",button: "Cerrar"});
				   		      	return;
				   		    }
				   		}
				   }	
				   
				//Bitácora
				$("#cAccion").val("IMPRIME_REQUISICION");
				$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				   	
			
				if(ext!='csv'){
					window.open(
						"../../servlet/SeguridadCatalogosMateriales?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn=rptRequisiciones.jasper"
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
							+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
							+ "&formato=" +ext
							+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
				}
				else{
					window.open(
						"../../servlet/CatalogosCSV?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn=rptRequisiciones.jasper"
							+ "&cEjercicio=" + $("#cEjercicio").val()
							+ "&cIdUnidadEjecutora=" + $("#cIdUnidadEjecutora").val()
							+ "&cIdTipoSolicitud=" + $("#cIdTipoSolicitud").val()
							+ "&formato=" +ext
							+ "&nIdConsecutivo=" + $("#nIdConsecutivo").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
				}
			
		}
			
	function cambiaColorFecha(){
		var longitudReq=$("#lblRequisicion").val().length;
		$("#Req").val($("#lblRequisicion").val().substring(2,(parseInt(longitudReq,10)-2)));
		queryFormPost("mFechaVencimiento", { async:false });
		$("#fechaVence").val($("#vence").val());
	}
	function aplicacionContableApartado(){
		$("#esperar").dialog("open");
		$.ajax({url: '../../servlet/SolicitudServlet' , type:'get' , async: false
			,data:"operacion=6&nFolioApartado="+$("#nFolioApartado").val()
			+"&cEjercicio="+$("#cEjercicio").val()
			+"&cIdSolicitud="+$("#cIdSolicitud").val()
			+"&caNoPreCompromiso="+$("#caNoPreCompromiso").val()
			+"&cTipoPoliza="+$("#cTipoPoliza").val()
			+"&fAplicacion="+$("#fAplicacion").val()
			+"&fCarga="+$("#fCarga").val()
			+"&fVigencia="+$("#fVigencia").val()
			+"&nEnviadoSICOP="+$("#nEnviadoSICOP").val()
			+"&nMes="+$("#nMes").val()
			+"&nStatusFinanciero="+$("#nStatusFinanciero").val()
			,dataType: 'json'
			,success:function(j){
				var mensaje=j[0].Contable1;
				swal(mensaje,{icon:"info",button: "Cerrar"});
				if (j[0].Success == "true"){
					init2(true);
				}
				document.getElementById("imgAutorizar").disabled = false;
				//document.getElementById("imgDevolverApartado").disabled = false;
				$("#esperar").dialog("close");
				location.reload();
			}, error: function(jqXHR, textStatus, errorThrown){
				swal("Error contacte a su Administrador.",{icon:"error",button: "Cerrar"});
				$("#esperar").dialog("close");
			}
		});
	}
	
	function init2(isUpdating) {
		//Encabezado y algunos hiddens
		//lblRequisicion, lblFolio, lblDescripcion, cEjercicio,	cIdUnidadEjecutora,	cIdSolicitud
		queryFormPost("mApartadoVentanillaLabels", {async:false});
		queryFormPost("mApartadoTotalRead", {async:false});
		$("#lblTotal").formatCurrency();

	}
	
	function successCreation(){
		swal("Se ha enviado un correo de autorizaci\u00f3n o rechazo al firmante de la requisici\u00f3n.\nFavor de dar seguimiento con su firmante.",{icon:"success",button: "Cerrar"});
		$( "#dlgQuestionnaire" ).dialog("close");
		init(false);
		document.getElementById("imgAutorizar").disabled = true;		
	}
	function sendEmail(){
		swal({
			title: "",
			text: "¿Está seguro que desea proceder con el reenvío de correo?",
			
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
			}else{
				document.getElementById("imgReEnviarEmail").disabled = true;
				$("#esperar").dialog("open");
				$.ajax({
					url : '../../contratos/RegistraCuestionario',
					dataType : 'json',
					type : "GET",
					data:"requestID="+$("#cIdSolicitud").val()
						+"&empleadoFirmante="+$("#idSignatureEmployee").val()
						,
					async : false,
					success : function(json) {
						var success = json.success;
						if( success == true || success == "true" ){
							swal({
								title: "",
								text: "Se ha reenviado un correo de autorizaci\u00f3n o rechazo al firmante de la requisici\u00f3n.\nFavor de dar seguimiento con su firmante.",
								icon: "success",
								buttons: {
									confirm : "Cerrar"
									},
								}).then((continuar) => {
									$("#esperar").dialog("close");
									init(false);
							});
						}else{
							alert("No se logro registrar sus respuestas debido al error:\n" + json.errorMsg + "\nIntente nuevamente o reporte al administrador." );
							$("#esperar").dialog("close");
						}
					},
					error : function(xhr, textStatus, errorThrown) {
						alert("Advertencia: " + xhr.responseText + "\nEstatus: "
								+ textStatus + "\n" + errorThrown);
						r = true;
						$("#esperar").dialog("close");
					}
				});
			}
		});
	}
	function autorizarApartado(){
		$("#imgDevolverApartado").hide();
		document.getElementById("imgAutorizar").disabled = true;
		if( $("#applyQuestionnaire").val() === "S" ){
			$( "#dlgQuestionnaire" ).dialog("open");
		}else{
			enviaRequiFirmaFIEL();
		}
	}
	function enviaRequiFirmaFIEL(){
		swal({
			title: "",
			text: "¿Podría confirmar si desea proceder con el envío de la requisición para firma mediante FIEL a nombre de "+$("#nomCompletoFirmante").val()+"?",
			
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
			}else{
				document.getElementById("imgAutorizar").disabled = true;
				$("#esperar").dialog("open");
				$.ajax({
					url : '../../contratos/RegistraCuestionario',
					dataType : 'json',
					type : "POST",
					data:"requestID="+$("#cIdSolicitud").val()
						+"&empleadoFirmante="+$("#idSignatureEmployee").val(),
					async : false,
					success : function(json) {
						var success = json.success;
						if( success == true || success == "true" ){
							swal({
								title: "",
								text: "Se ha enviado un correo de autorizaci\u00f3n o rechazo al firmante de la requisici\u00f3n.\nFavor de dar seguimiento con su firmante.",
								icon: "success",
								buttons: {
									confirm : "Cerrar"
									},
								}).then((continuar) => {
									$("#esperar").dialog("close");
									init(false);
							});
						}else{
							alert("No se logro registrar sus respuestas debido al error:\n" + json.errorMsg + "\nIntente nuevamente o reporte al administrador." );
							$("#esperar").dialog("close");
						}
					},
					error : function(xhr, textStatus, errorThrown) {
						alert("Advertencia: " + xhr.responseText + "\nEstatus: "
								+ textStatus + "\n" + errorThrown);
						r = true;
						$("#esperar").dialog("close");
					}
				});
			}
		});
		
	}
	function muestraEtiqueta300SalMin(){
		var neto = parseFloat($("#montoNeto").val().replace("$","").replace(/,/g, "").replace(" ",""));
		var montoBruto = parseFloat($("#montoBruto").val().replace("$","").replace(/,/g, "").replace(" ",""));
		
		queryFormPost('montoTotalSalarioMinimo', {async: false });
		if($('#usuarioRoleSolicitud').val().toString().indexOf('ENLACE') >= 0 
			&& parseInt($("#nIdEstadoSolicitud").val(),10)==3 && montoBruto>=$("#montoSalarioMinimo").val() ){
			$("#EnlacesReqMayorA300SalMin").css("visibility","visible");
		}
	
	}


	function showQuestionnaire(){
		queryFormPost({
			queryName:"applyQuestionnaire",
			async:false,
			callback:function(){
				$("#adjuntoFieldset").hide();				
			}
		});
		
	}
	
	function createDialogQuestionnaire(){
		
		$( "#dlgQuestionnaire" ).dialog({
			autoOpen: false,
			height: 472,
			width: 850,
			modal: true,
			open: function(){
				$('#questionnaire').attr('src', "Cuestionario.jsp?empleadoFirmante=" + $("#idSignatureEmployee").val() + "&requestID=" + $("#cIdSolicitud").val() + "&nomCompletoFirmante=" + $("#nomCompletoFirmante").val() );
			}
		});

	}
	
		</script>

	</head>
	<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
		<div id="container" class="container" style="width: 90%;">
			<fieldset >
  			<legend>Informaci&oacute;n de la Requisiciones</legend>
  				<table  align="left" width="100%">
					<tr>
						<td align="right" colspan="2">
							<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdPdfFirmantesReq" 		name="cmdPdfFirmantesReq" 	value="PDF"		onclick="openPDF('pdf');" />&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdxlsFirmantesReq" 		name="cmdxlsFirmantesReq" 	value="Excel"	onclick="openPDF('xls');" />&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdcsvFirmantesReq" 		name="cmdcsvFirmantesReq" 	value="CSV"		onclick="openPDF('csv');" />&nbsp;&nbsp;
							<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 	id="cmdwordFirmantesReq" 		name="cmdwordFirmantesReq" 	value="Word"	onclick="openPDF('doc');" />&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgApartar" 	name="imgApartar" 	value="Apartar"	/>&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAutorizar" 	name="imgAutorizar" 	value="Autorizar"	/>&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceSendEmail ui-button ui-widget ui-state-default ui-corner-all" 	id="imgReEnviarEmail" 	name="imgReEnviarEmail" 	value="Re-Enviar"	title="No abusar con el reenvio de correos, se duplicara el correo y marcara error a la hora de firmar si se hace mas de una vez el reenvio."/>&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverApartado" 	name="imgDevolverApartado" 	value="Devolver"	/>&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	onclick="window.location = 'Requisiciones.jsp?tab=1&ses=0';" />&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 30px;border-width:0;background:#FEFEFE" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly value="<%=unidadUsuarioLogeado%>"/>
				    		<input type="text" style="width: 690px;border-width:0;background:#FEFEFE" id="lblDescUsuario" name="lblDescUsuario" readonly />
				    	</td>
			    	</tr>
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 110px;border-width:0;background:#FEFEFE" id="lblRequisicion" name="lblRequisicion" readonly />
				    		<input type="text" style="width: 700px;border-width:0;background:#FEFEFE" id="lblDescripcion" name="lblDescripcion" readonly />
				    	</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px;border-width:0;background:#FEFEFE" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly />
			    		</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0;background:#FEFEFE" name="lblEstado" id="lblEstado" /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0;background:#FEFEFE" name="lblPartida" id="lblPartida" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0;background:#FEFEFE" name="lblMesRequisicion" id="lblMesRequisicion" readonly /></td>
			    	</tr>
			    	<tr>
						<td align="left" colspan="2">Fecha de Apartado: 
							<input type="text" style="width: 90px;border-width:0;background:#FEFEFE"
							name="fechaVence" id="fechaVence" value="" readonly />
						</td>
					</tr>
					<tr id="trNotas">
						<td align="left" colspan="2" >
							<label>Motivo del rechazo:</label>
								 <textarea rows="3" cols="1" style="color:red; " name="lblEstado" id="lblNotas" readonly
								style="border-width:0; background-color:transparent"> </textarea>
								
						</td>
					</tr>
					<tr>
						<td align="left"  colspan="2">
						<label>Monto bruto de la requisici&oacute;n:</label> <input type="text"  id="montoBruto" name="montoBruto" readonly style="border-width:0; background-color:transparent"/>
						</td>
					</tr>
					<tr>
						<td align="left"  colspan="2">
						<label>Monto neto de la requisici&oacute;n:</label> <input type="text"  id="montoNeto" name="montoNeto" readonly style="border-width:0; background-color:transparent"/>
						</td>
					</tr>
					<tr><td>
						
						<div  align="left" id="EnlacesReqMayorA300SalMin" style="visibility: hidden;" >
							
							<span style="color:red;">Recuerda que para está Requisición deberas de presentar 
							tu solicitud de contratación a GRM (Gerencia de Recursos Materiales).</span>
						</div>
				
					</td></tr>
					<tr><td>
						<div  align="left" id="extensionNoticeVigencia" style="visibility: hidden;">
							
							<span style="color:red;">La vigencia del apartado expiro.</span>
						</div>
					</td></tr>
				</table>
  				
				
  			</fieldset>
  			<table width="100%"><tr><td> 
			<fieldset id="vigenciaFieldset"  align="left">
				<legend>
					Vigencia del apartado
				</legend>
				
				
				<div  align="left" id="extensionNotice">
					<br />
					<span style="color:red;">El apartado se encuentra en ventanilla para extensión de vigencia.</span>
				</div>
				<div id="subeAdjunto" align="left">
				<form name="upform2" id="upform2" action="../../servlet/SolicitudServlet?operacion=5" enctype="multipart/form-data" method="post" >
					<table>
						<tr>
							<td>
								Fecha de vencimiento de apartado: <span id="lblVigencia"></span>
							</td>
						</tr>	
						<tr>
							<td>Elegir archivo:<input id="uploadfile2" name="uploadfile2" type="file" /></td>
						</tr>
						
						<tr>
							<td align="center">
								<img id="imgExtensionVigencia" src="../imagenes/enviar.png" style="cursor: pointer" />&nbsp;Solicitar extensión de vigencia
							</td>
						</tr>
					</table>
				</form>
				</div>
			</fieldset></td></tr>
			<tr ><td >
			
			<fieldset id="adjuntoFieldset"  align="left" >
				<legend>
					Requisición Adjunta
				</legend>
				<div  align="left" id="adjuntoNotice" >
					<br />
					<span style="color:red;">Ya existe un archivo adjunto al apartado. Si elige uno nuevo, se reemplazará el anterior.</span>
				</div>
				<form name="upform" id = "upform" action="../../servlet/SolicitudServlet"  enctype = "multipart/form-data" method = "post" >
					<input type="hidden" id="aplicaCuestionario" name="aplicaCuestionario" value="N">
				<!--  <input id="operacion" name="operacion" type="hidden" value="2" />-->
					<table width="100%">
						<tr>
							<td>
								Elegir archivo:
								<input id="uploadfile" name="uploadfile" type="file" />
							</td>
						</tr>
					</table>
				</form>
			</fieldset>
			
			
			</td></tr></table>
			<fieldset  >
				<legend> Presupuesto de gasto </legend>
				<div>
				<table id="tblPresupuesto" class="display" >
					<thead>
						<tr align="center">
							<th>Estructura Program&aacute;tica</th>
							<th>Clave Interna</th>
							<th>Enero</th>
							<th>Febrero</th>
							<th>Marzo</th>
							<th>Abril</th>
							<th>Mayo</th>
							<th>Junio</th>
							<th>Julio</th>
							<th>Agosto</th>
							<th>Septiembre</th>
							<th>Octubre</th>
							<th>Noviembre</th>
							<th>Diciembre</th>
							<th>Anual</th>
						</tr>
					</thead>
					
				</table></div>
			
		</fieldset>
  		</div>
  		<form id="hiddens" name="hiddens">
  			<div id="esperar" align="center" title="Espera">
				<fieldset>
					<table>
						<tr>
							<td>Espere por favor.... <img border="0"src="../../imagenes/espera.gif" height="30"></td>
						</tr>
					</table>
				</fieldset>
			</div>
				<!-- Usados para poder consultar la info de la Solicitud -->
				
				<input type="hidden" name="idSignatureEmployee" id="idSignatureEmployee" value=""/>
				<input type="hidden" name="cIdTipoSolicitud" id="cIdTipoSolicitud" value="<%=cIdTipoSolicitud%>"/>
				<input type="hidden" name="applyQuestionnaire" id="applyQuestionnaire" value=""/>
				<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
				<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
				<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
				<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" />
				<input type="hidden" name="cIdSolicitud" id="cIdSolicitud"/>
				<input type="hidden" name="nIdEstadoSolicitud" id="nIdEstadoSolicitud" />
				<input type="hidden" name="nIdEstadoPrecomprometido" id="nIdEstadoPrecomprometido" />
				<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
				
				
				<!-- Variables de Folio Apartado -->
				<input type="hidden" id="nFolioApartado" name="nFolioApartado" />
				<input type="hidden" name="folioCasoApartado" id="folioCasoApartado" />
				<input type="hidden" name="canceladoApartado" id="canceladoApartado" />
				
				
				

				
				<!--
				<input type="hidden" name="mesDisponible" id="mesDisponible" value="<%=mesActual%>"/>
				<input type="hidden" id="cDocumento" name="cDocumento" value="PRECOMPROMISO">
				<input type="hidden" name="mensajeFinanciero" id="mensajeFinanciero" />
								
				<!-- Login y rol -->
				<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
				<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
				<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >
				
				<!-- Utilizado para llenar automaticamente el filtro en el popup de eps -->
				<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" />
				
				<!-- Para cargar el numero de paginas que tiene el Documento -->
				<input type="hidden" name="nPaginas" id="nPaginas" />
				
				<!-- Para funcionalidad de apartado vigencia  -->
				<input type="hidden" name="venceApartado" id="venceApartado" />
				<input type="hidden" name="nCasoOperacion" id="nCasoOperacion" />
				<input type="hidden" name="nLineasConsolidado" id="nLineasConsolidado" />
				
				<!-- Para validar que una solicitud de Modificación no este e un Modificatorio al devolver -->
				<input type="hidden" name="existeMod" id="existeMod"/>
				<input type="hidden" name="cIdSolicitudMod" id="cIdSolicitudMod"/>
				
				<!-- Roles  -->
				<input type="hidden"  id="usuarioRoleSolicitud" name="usuarioRoleProcedimiento" />
				<input type="hidden" id="usuarioLogin" name="usuarioLogin"  />
				
				<input type="hidden" name="existeLineaApartadoSolicitud" id="existeLineaApartadoSolicitud"/>
				<input type="hidden" name="sumaLineasSolicitud" id="sumaLineasSolicitud"/>
				<input type="hidden" name="sumaLineasApartadoSolicitudLineas" id="sumaLineasApartadoSolicitudLineas"/>
				<input type="hidden" name="numeroLineasApartadoSolicitud" id="numeroLineasApartadoSolicitud"/>
				<input type="hidden" name="numeroLineasSolicitud" id="numeroLineasSolicitud"/>
				<input type="hidden" name="cIdSolicitudRep" id="cIdSolicitudRep"/>
				<input type="hidden" name="cAccion" id="cAccion"/>
				<input type="hidden" name="compromisoServicio" id="compromisoServicio"/>
				<input type="hidden" name="compromisoCompras" id="compromisoCompras"/>
				<input type="hidden" name="cIdDocumento" id="cIdDocumento"/>
				
				<input type="hidden" name="vence" id="vence"/>
				<input type="hidden" name="Req" id="Req"/>
				<input type="hidden" name="difFecha" id="difFecha"/>
				<input type="hidden" name="lblUnidadUsuarioAux" id="lblUnidadUsuarioAux"/>
				
				<!-- Hiddens para la aprovación del apartado automatico-->
				<input type="hidden" id="caNoPreCompromiso" name="caNoPreCompromiso" value="">
				<input type="hidden" id="fCarga" name="fCarga" value="<%=today%>"><!-- java -->
				<input type="hidden" id="fAplicacion" name="fAplicacion" value="<%=today%>"><!-- java -->
				<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>"><!-- java -->
				<input type="hidden" id="cRamo" name="cRamo" value="<%=cRamo%>"><!-- java -->
				<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>"><!-- java -->
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="PR"><!-- fijo -->
				<input type="hidden" id="nMes" name="nMes" value="">
				<input type="hidden" id="nStatusFinanciero" name="nStatusFinanciero" value="0"><!-- fijo -->
				<input type="hidden" id="fVigencia" name="fVigencia" value="<%=vigencia%>"><!-- java -->
				<input type="hidden" id="nEnviadoSICOP" name="nEnviadoSICOP" value="0"><!-- fijo -->
				<input type="hidden" id="lblTotal" name="lblTotal" value="0">
				<input type="hidden" id="autorizaApartado" name="autorizaApartado" value="0">
				<input type="hidden" id="montoSalarioMinimo" name="montoSalarioMinimo" value="0">
				<input type="hidden" id="cCentroContableUEReq" name="cCentroContableUEReq" value="10">
				<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="" />
				<input type="hidden" name="existeRequiIntegrada" id="existeRequiIntegrada" value="" />
				<input type="hidden" name="nomCompletoFirmante" id="nomCompletoFirmante" value="" />
				<input type="hidden" name="cIdCapitulo" id="cIdCapitulo" value="0" />
				
  		
  		</form>
  		
  		<div id="dlgQuestionnaire" title="Cuestionario de Contratacion">
  			<iframe id="questionnaire" src="about:blank" align="top" frameborder="0" height="360" width="800"> </iframe>
  		</div>
  		
	</body>
</html>
