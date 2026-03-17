﻿<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab.getLogin();
	String idRol="0";
	String roles="";
	Map rol =usuarioTab.getRoles();
	String cEjercicio = "";
	String cIdTipoSolicitud = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ReqEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ReqEjercicio);
		cIdTipoSolicitud = (String)session.getAttribute(GestionInterface.ATT_ReqTipoSolicitud);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ReqUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ReqConsecutivo);
	}
	
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuarioTab.getU_UR();
	Calendar c1 = Calendar.getInstance(); // today
	int ejercicioActual=c1.get(Calendar.YEAR);//Porque empieza en 0: Enero

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>Edici&oacute;n Requisici&oacute;n</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
		var roles="";
		$(document).ready(function() {
			$("#tbs").val(2);
			showHideTabs();
			var apartables = [ "RC", "RM", "RO", "RS","RT" ];
			if($.inArray($("#cIdTipoSolicitud").val(),apartables) !=-1){
				$("#imgAprobarCaratulaReq").hide();
				$("#imgDevolverCaratulaReq").hide();
			}
	
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				
				Map botones=nb.getBotones(roles,"Requisiciones","CaratulaRequisiciones");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAnularCaratulaReq".equals(img)){  
						imgAnular=1; 
					}
					if ("imgAprobarCaratulaReq".equals(img)){
						imgAprobar=1;
					}
					if ("imgDevolverCaratulaReq".equals(img)){
						imgDevolver=1;
					}
					if ("cmdPdfCaratulaReq ".equals(img)){
						imgPdf=1;
					}
				}

			%>
			roles="<%=roles%>";
			document.getElementById("lblRequisicion").style.readonly=true;
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblDescripcion").style.readonly=true;
			document.getElementById("lblPartida").style.readonly=true;
			document.getElementById("lblMesRequisicion").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;			
			queryFormPost("cg_roleRead", { async:false });
			queryFormPost("mNombreBDSiguienteEjercicioRead", { async:false });
			queryFormPost("mServerBDSiguienteEjercicioRead", { async:false });
			querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutoraConsultar", {async: false });
			querySelectPost("CopiaRequisicionEjercicios", "cboEjercicio", {async: false });
			$("#cboEjercicio").val("<%=ejercicioActual%>");
			//llenado de los catálogos
			//querySelectPost("tCatalogoUnidadEjecutoraRead", "cIdUnidadEjecutora", {async: false});  //solo para combobox
			querySelectPost("mCatalogoTipoSolicitudRead", "cIdTipoSolicitud", {async: false});
			
			if($("#cIdTipoSolicitud").val()!='RC' && $("#cIdTipoSolicitud").val()!='RR'){
				$("#capitulos").val('1,3,4');
			}
			if($("#cIdTipoSolicitud").val()=='RO'){
				$("#capitulos").val('6');
			}
			if($("#cIdTipoSolicitud").val()=='RT'){
				$("#capitulos").val('2');
			}
			if($("#cIdTipoSolicitud").val()=='RM'){
				$("#capitulos").val('1,2,3,4,5');
			}
			querySelectPost("mCatalogoCapituloCMBRead", "cIdCapitulo", {async: false});
			queryFormPost("mSolicitud_cIdSubPartidaRead", {async: false});
			querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
			querySelectPost("mCatalogoAlcanceRead", "nIdAlcance", {async: false});  //traer de SACEL
			querySelectPost("mCatalogoAlmacenReadSimca", "cIdAlmacenEntrega", {async: false}); //traer de SACEL
			querySelectPost("mCatalogoAlmacenRead", "cIdAlmacen", {async: false}); //traer de SACEL
			querySelectPost("mCatalogoCategoriaProcedimientoRead", "nIdCategoria", {async: false});
			//querySelectPost("mCatalogoAgnosRead", "cIdAgnos", {async: false});  //traer de SACEL
			querySelectPost("mSistema_cEjercicioRead", "cEjercicio", {async: false});
			querySelectPost("mCatalogoTipoGarantia", "cTipoGarantia", {async: false});
			querySelectPost("mCatalogoTipoGarantiaPorcentaje", "mPorcentajeGarantia", {async: false});
			queryFormPost("mSolicitudLineasCuentaRead", { async:false });
			
			//selección de valores por default en los campos
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraRequisicion").val());
			$("#cIdTipoSolicitud").val($("#cIdTipoSolicitudRequisicion").val());
			
			queryFormPost("mSolicitud_LabelRead", { async:false });
			queryFormPost("mSolicitud_cIdSubPartidaRead", {async: false});
			queryFormPost("mSolicitud_CaratulaRead", {async: false});
			queryFormPost("mSolicitudDescripcionUnidad", {async: false});
			
			$("#cIdSubPartidaOriginal").val($("#cIdSubPartida").val());
			$("#nIdPeriodo").val($("#nIdPeriodoRequisicion").val());
			$("#nIdAlcance").val($("#nIdAlcanceRequisicion").val());
			$("#cIdAlmacenEntrega").val($("#cIdAlmacenEntregaRequisicion").val());
			$("#cIdAlmacen").val($("#cIdAlmacenRequisicion").val());
			$("#nIdCategoria").val($("#nIdCategoriaRequisicion").val());
			
			querySelectPost("mCatalogoTipoGarantiaPorcentaje", "mPorcentajeGarantia", {async: false});
			
			 //$("#mPorcentajeGarantia option[value=9]").attr("selected",true); 
			var cadena = $("#mPorcentajeGarantia").val();
			var pos = $("#mPorcentajeGarantia").val().indexOf(".");
			cadena = cadena.substring(0, pos + 3);
			cadena = cadena + '%';
			$("#mPorcentajeGarantia").val(cadena);
			
			//$("#mImportePoliza").val('$' + $("#mImportePoliza").val());
			var cadena = $("#mImportePoliza").val();
			cadena = '$' + cadena;
			var pos = $("#mImportePoliza").val().indexOf(".");
			cadena = cadena.substring(0, pos + 4);
			$("#mImportePoliza").val(cadena);
			
			$("#mImportePoliza").formatCurrency();
			
			
			//$("#fCreacion").val($("#fCreacion").val().substring(0,10));
			$("#fechaRequerida").val($("#fRequerida").val());
			
			queryFormPost("cuentaConsolidadosRead", { async:false});
			
			queryFormPost("mSolicitud_EstadoSolicitudRead", {async:false});
			if (!(roles.indexOf("ADMIN_RECMAT") >= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
				document.getElementById("imgAprobarCaratulaReq").disabled = true;
				document.getElementById("imgDevolverCaratulaReq").disabled = true;
				document.getElementById("imgAnularCaratulaReq").disabled = true;
				$("#imgAnularCaratulaReq").hide();
			}
			else if ($("#nIdEstadoReq").val() == "1") { //capturada
				document.getElementById("imgAprobarCaratulaReq").disabled = false;
				document.getElementById("imgDevolverCaratulaReq").disabled = true;
				document.getElementById("imgAnularCaratulaReq").disabled = false;
				$("#imgAnularCaratulaReq").show();
			}
			else if ($("#nIdEstadoReq").val() == "2") { //solicitada
				document.getElementById("imgAprobarCaratulaReq").disabled = true;
				document.getElementById("imgDevolverCaratulaReq").disabled = true;
				document.getElementById("imgAnularCaratulaReq").disabled = true;
				$("#imgAnularCaratulaReq").hide();
			}
			else if ($("#nIdEstadoReq").val() == "3" ) { //Aprobada
				document.getElementById("imgAprobarCaratulaReq").disabled = true;
				document.getElementById("imgDevolverCaratulaReq").disabled = false;
				document.getElementById("imgAnularCaratulaReq").disabled = true;
				$("#imgAnularCaratulaReq").hide();
			} 
			else if ($("#nIdEstadoReq").val() == "4") { //anulada
				document.getElementById("imgAprobarCaratulaReq").disabled = true;
				document.getElementById("imgDevolverCaratulaReq").disabled = true;
				document.getElementById("imgAnularCaratulaReq").disabled = true;
				$("#imgAnularCaratulaReq").hide();
			}else if($("#nIdEstadoReq").val() == "6" || $("#nIdEstadoReq").val() == "7"){
				$("#imgAnularCaratulaReq").hide();
				
				
			}
			
			if ((roles.indexOf("ADMIN_RECMAT") >= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val()) && $("#nIdEstadoReq").val() == "1") {
				$("#fechaRequerida").datepicker({
					beforeShowDay: nonWorkingDates,						
					dateFormat: "dd/mm/yy",
					currentText: "Now",
					showOn: 'button',
					altField: "#actualDate",
					buttonImageOnly: true,	
				    buttonImage: '../images/calendar.gif',			    					 
					changeYear: true
				});	
			}
			
			$( "#cIdCapitulo" ).change(function() {
				querySelectPost("mCatalogoSubPartidaCMBRead", "cIdSubPartida", {async: false});
			});
			
			if ($("#lAnexos").val() == "1")
				document.getElementById("chkAnexo").checked = true;
			
			$("#chkAnexo").change(function() {
				if ($("#lAnexos").val() == "0")
					$("#lAnexos").val("1");
				else
					$("#lAnexos").val("0");
			});
			
			if (!(roles.indexOf("ADMIN_RECMAT") >= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val()))
				deshabilitarCampos();
			else if ($("#nIdEstadoReq").val() == "2" ||$("#nIdEstadoReq").val() == "3" || $("#nIdEstadoReq").val() == "4" ||  $("#nIdEstadoReq").val() == "5" ||  $("#nIdEstadoReq").val() == "6"||  $("#nIdEstadoReq").val() == "7")
				deshabilitarCampos();
			//Para Fecha de vencimiento
			cambiaColorFecha(); 
		});//Fin del Document ready
		
		
		 ///Desabilita sabados y domingos del datepicker
			 function nonWorkingDates(date){
		        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
		        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
		        var closedDays = [[Sunday], [Saturday]];
		        for (var i = 0; i < closedDays.length; i++) {
		            if (day == closedDays[i][0]) {
		                return [false];
		            }
		
		        }
		        return [true];
		    }
		function deshabilitarCampos() {
			document.getElementById("cIdCapitulo").setAttribute("disabled", "disabled");
			document.getElementById("cIdSubPartida").setAttribute("disabled", "disabled");
			document.getElementById("nIdPeriodo").setAttribute("disabled", "disabled");
			document.getElementById("nIdAlcance").setAttribute("disabled", "disabled");
			document.getElementById("cIdAlmacen").setAttribute("disabled", "disabled");
			document.getElementById("cIdAlmacenEntrega").setAttribute("disabled", "disabled");
			document.getElementById("nIdCategoria").setAttribute("disabled", "disabled");
			document.getElementById("chkAnexo").setAttribute("disabled", "disabled");
			document.getElementById("btnGuardarCaratulaReq").setAttribute("disabled", "disabled");
			document.getElementById("btnCancelarCaratulaReq").setAttribute("disabled", "disabled");
			document.getElementById("cDescripcion").setAttribute("disabled", "disabled");
			document.getElementById("nIdPlazo").setAttribute("disabled", "disabled");
			document.getElementById("cTipoGarantia").setAttribute("disabled", "disabled");
			document.getElementById("mPorcentajeGarantia").setAttribute("disabled", "disabled");
			document.getElementById("mImportePoliza").setAttribute("disabled", "disabled");
			document.getElementById("cPlurianualidad").setAttribute("disabled", "disabled");
			document.getElementById("cObservaciones").setAttribute("disabled", "disabled");
			document.getElementById("fechaRequerida").setAttribute("disabled", "disabled");
		}
		function guardar() {
			$("#mImportePoliza").val($("#mImportePoliza").val().replace(/,/g, ''));
		    
			var booleano = true;
			booleano &= numberValidator(document.getElementById("nIdPlazo"),document.getElementById("trPlazo"));
			//booleano &= floatPercentValidator(document.getElementById("mPorcentajeGarantia"),document.getElementById("trPorGarantia")); 
			booleano &= floatMoneyValidator(document.getElementById("mImportePoliza"),document.getElementById("trImpPoliza"));
			booleano &= dateValidator(document.getElementById("fechaRequerida"), document.getElementById("trFechaRequerida"));
			if (booleano) {
					if( parseInt($("#cIdSubPartida").val(),10) != parseInt($("#cIdSubPartidaOriginal").val() )) {
						swal({
							title: "¿Desea continuar?",
							text: "Cambiar la Partida provocará que se borren todas las líneas de la Solicitud!",
							icon: "info",
							buttons: {
								confirm : "Aceptar",
								cancel: "Cancelar"
								},
							}).then((continuar) => {
								if (!continuar) {
									return;
							}else{
								$("#mPorcentajeGarantia").val($("#mPorcentajeGarantia").val().replace("%", ""));
								$("#mImportePoliza").val($("#mImportePoliza").val().replace("$", ""));
								$("#fRequerida").val($("#fechaRequerida").val());
								queryFormPost("mSolicitudLineasCompletaDelete,mSolicitudLineasPeriodoDelete", {async: false});
								$("#cIdSubPartidaOriginal").val($("#cIdSubPartida").val());
								queryFormPost("mSolicitudUpdate", {async: false});
								window.location = "Requisiciones.jsp?tab=3";
							}
						});
					}else{
						swal({
							title: "¿Desea continuar?",
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
								$("#mPorcentajeGarantia").val($("#mPorcentajeGarantia").val().replace("%", ""));
								$("#mImportePoliza").val($("#mImportePoliza").val().replace("$", ""));
								$("#fRequerida").val($("#fechaRequerida").val());
								$("#cIdSubPartidaOriginal").val($("#cIdSubPartida").val());
								queryFormPost("mSolicitudUpdate", {async: false});
								window.location = "Requisiciones.jsp?tab=3";
							}
						});
					}
			}
			 else
				swal("Favor de revisar los campos antes de continuar.",{icon:"info",button: "Cerrar"});
		}
		
		function aprobarRequisicion() {
			
			var imgAprobar='<%=imgAprobar%>';
			
			if (imgAprobar==0){
				queryFormPost("mSolicitudLineasCuentaRead", { async:false });
				if ($("#nLineas").val() > 0) {
					//Regla de negocio
					var currentTime = new Date();
					$("#fAprobacion").val(currentTime.getFullYear() + '-' + (currentTime.getMonth()+1) + '-' + currentTime.getDate() + ' ' + currentTime.getHours() + ':' + currentTime.getMinutes() + ':' + currentTime.getSeconds() + '.' + currentTime.getMilliseconds());
					$("#nIdEstado").val("3"); //aprobada
					$("#nIdEstadoPrecomprometido").val("3"); //para regularización
					queryFormPost("mSolicitudAprobarUpdate", { async:false });
					queryFormPost("sp_mSolicitudEstadoPrecomprometido", {async:false});
					//Bitácora
					$("#cAccion").val("APROBAR");
					//$("#cIdUsuario").val("< %=usuarioTab.getLogin()%>");
					$("#cIdDocumento").val($("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					swal("La requicisi\u00f3n ha sido aprobada.",{icon:"info",button: "Cerrar"});
					window.location = "Requisiciones.jsp?tab=2";
				}
				else
					swal("Por lo menos debe haber una l\u00ednea capturada para aprobar la requisici\u00f3n.",{icon:"info",button: "Cerrar"});
			}else{
				swal("No tiene permisos para realizar esta acci\u00f3n.",{icon:"info",button: "Cerrar"});
			}
		}
		
		function devolverRequisicion () {
			var imgDevolver ='<%=imgDevolver %>';
			if (imgDevolver==0){
				
				if ($("#cIdTipoSolicitud").val() == 'RM') {
				
					$("#cIdSolicitudMod").val($("#cIdTipoSolicitud").val() + "-" + $("#cIdUnidadEjecutora").val() + "-" + $("#nIdConsecutivo").val());					
					$("#existeMod").val('');
					queryFormPost("mPedidoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						swal("No se puede devolver la requicisi\u00f3n porque esta asociada a un pedido modificatorio",{icon:"info",button: "Cerrar"});
						return;
					}
					
					$("#existeMod").val('');
					queryFormPost("mContratoModificadoPartidaSolicitudRead", { async : false});
					if ($("#existeMod").val() == 'EXISTE'){
						swal("No se puede devolver la requicisi\u00f3n porque esta asociada a un contrato modificatorio",{icon:"info",button: "Cerrar"});
						return;
					}
				}
				if ($("#nConsolidados").val() == "0" || $("#nConsolidados").val() == "") {
					var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
					window.open("LineasSolicitudNotas.jsp?tipo=1&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
				}else
					swal("Esta requisici\u00f3n est\u00e1 asociada a un consolidado. Para devolverla, primero es necesario eliminarla del consolidado.",{icon:"info",button: "Cerrar"});
			}
			else{
				swal("No tiene permisos para realizar esta acci\u00f3n.",{icon:"info",button: "Cerrar"});
			}
		}
		
		function anularRequisicion () {
			var imgAnular='<%=imgAnular%>';
			if (imgAnular==0){
				
				if ($("#nConsolidados").val() == "0" || $("#nConsolidados").val() == "") {
					var id=$("#cIdTipoSolicitud").val() + '-' + $("#cIdUnidadEjecutora").val() + '-' + $("#nIdConsecutivo").val();
					window.open("LineasSolicitudNotas.jsp?tipo=1&id="+id, 'Notas', 'status=1, width=700px, height=280px, left=150px');
				}
				else
					swal("Esta requisici\u00f3n est\u00e1 asociada a un consolidado. Para devolverla, primero es necesario eliminarla del consolidado.",{icon:"info",button: "Cerrar"});
			}else{
			   swal("No tiene permisos para realizar esta acci\u00f3n.",{icon:"info",button: "Cerrar"});
			}
		}
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		function dateValidator (field, printfield) {
			var pattern=/^[0123]\d[\/][012]\d[\/][12]\d\d\d$/;
			if (field.value.replace(/^\s+|\s+$/g,"") == "") {
				printfield.style.display = "table-row";
				field.value = field.value.replace(/^\s+|\s+$/g,"");
			}
			else if (!pattern.test(field.value))
				printfield.style.display = "table-row";
			else
			{
				printfield.style.display = "none";
				return true;
			}
			return false;
		}
		function numberValidator( field, printfield ) {
			var pattern=/^\d+$/;
			if (!pattern.test(field.value))
				printfield.style.display = "table-row";
			else
			{
				printfield.style.display = "none";
				return true;
			}
			return false;
		}
		function floatPercentValidator( field, printfield ) {
			var pattern=/^[1][0][0]([.][0][0]?)?[%]?$/;
			if (field.value == "")
				printfield.style.display = "table-row";
			else if (!pattern.test(field.value)) {
				pattern=/^\d?\d([.]\d\d?)?[%]?$/;
				if (!pattern.test(field.value)) {
					printfield.style.display = "table-row";
				}
				else {
					printfield.style.display = "none";
					return true;
				}
			}
			else
			{
					printfield.style.display = "none";
					return true;
			}
			return false;
		}
		function floatMoneyValidator( field, printfield ) {
			var pattern=/^[$]?\d+([.]\d\d?)?$/;
			if (field.value == "")
				printfield.style.display = "table-row";
			else if (!pattern.test(field.value))
				printfield.style.display = "table-row";
			else
			{
				printfield.style.display = "none";
				return true;
			}
			return false;
		}
		
		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function onlyPercentage(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed == 46 || keyPressed == 37)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}

		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function openPDF(ext){
			var imgPdf='<%=imgPdf%>';

			if (imgPdf ==0){
			  if($("#nIdEstadoReq").val() != "3" && $("#nIdEstadoReq").val() != "4" &&  $("#nIdEstadoReq").val() == "5" ){
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
				   		 		swal("La suma de las l\u00edneas presupuestadas no es igual a la suma de las l\u00edneas de solicitud favor de revisar.",{icon:"info",button: "Cerrar"});
				   		    	return;
				   		   	}
				   		    else if(Math.abs(lineasSolicitud-lineasApartadoSolicitud) > 0.0001){
				   		       queryFormPost("pa_ajustePresupuestoRequisiscion", {async: false});
							}
						}else{
							swal("La requisici\u00f3n no se puede imprimir porque no ha presupuestado el total de las lineas de la requisici\u00f3n.",{icon:"info",button: "Cerrar"});
			   		      return;
			   		    }
					}
				}
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
			}else{
				swal("No tiene permisos para ejecutar esta acci\u00f3n.",{icon:"info",button: "Cerrar"});
			}
		}
		
		function copiarRequisicion(){
			swal({
				title: "Estas seguro de copiar la requsici\u00f3n?",
				text: "Se procedera a guardar la información!",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					var proc=""+$("#cIdTipoSolicitud").val()+" ,"+$("#cIdUnidadEjecutora").val()+","+$("#nIdConsecutivo").val()+","+$("#cboEjercicio").val()+","+$("#cboUnidadEjecutoraConsultar").val()+",1";
					$.getJSON("../../servlet/CopiaRequisicionServlet?"+new Date().getTime(),{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
						for(var i = 0; i < j.length; i++){
				                 var col=j[i].Col1;
				        }
				        switch(col){
							case "1":  
								swal("Falta agregar el parametro cEjercicio a la tabla mSistema.",{icon:"info",button: "Cerrar"});
							break;
							case "2":
								swal("Falta insertar los datos de conexi\u00f3n del ejercicio seleccionado a la tabla mSistema.",{icon:"info",button: "Cerrar"});
							break;
							case "3":
								swal("Falta agregar el parametro mNombreBDSiguienteEjercicio a la tabla mSistema.",{icon:"info",button: "Cerrar"});
							break;
							case "4":
								swal("No existe el CAMB o el saldo es insuficiente.",{icon:"info",button: "Cerrar"});
							break;
							case "5":  
								swal("No se pudo obtener el consecutivo de la Requisici\u00f3n a crear.",{icon:"info",button: "Cerrar"});
							break;
							case "6":
								swal("Error al insertar la Requisici\u00f3n.",{icon:"info",button: "Cerrar"});
							break;
							case "7":
								swal("Error al insertar las l\u00edneas de la Requisici\u00f3n.",{icon:"info",button: "Cerrar"});
							break;
							case "8":
								swal("Error al insertar los firmantes de la Requisici\u00f3n.",{icon:"info",button: "Cerrar"});
							break;
							case "0":
								swal("Se realiz\u00e1 la copia de la Requisici\u00f3n correctamente.",{icon:"info",button: "Cerrar"});
							break;
							default:
								swal(col,{icon:"info",button: "Cerrar"});
						}
			   		});
				}
			});
			
			
			
		}
		function cambiaColorFecha(){
			var longitudReq=$("#lblRequisicion").val().length;
			$("#Req").val($("#lblRequisicion").val().substring(2,(parseInt(longitudReq,10)-2)));
			queryFormPost("mFechaVencimiento", { async:false });
			
			$("#fechaVence").val($("#vence").val());
// 			if(parseInt($("#difFecha").val(),10)<4 ) 
// 				$("#fechaVence").css("color","red");
// 			if(parseInt($("#difFecha").val(),10)>3 & parseInt($("#difFecha").val(),10)<11)
// 				$("#fechaVence").css("color","orange");
// 			if(parseInt($("#difFecha").val(),10)>10)	
// 				$("#fechaVence").css("color","green");
		}
		
		function Sinfrmt(fld){
			var valcol = $("#"+fld).val();
			valcol = valcol.replace("$", "");
			valcol = valcol.replace(",", "");
			$("#"+fld).val(valcol);
		}		
		function cambiafrmt(fld){
	   		$("#"+fld).formatCurrency();
		}
		
		function SinfrmtPorc(fld){
			var valcol = $("#"+fld).val();
			valcol = valcol.replace("%", "");
			valcol = valcol.replace(",", "");
			$("#"+fld).val(valcol);
		}		
		function cambioTipoGarantia(){
			querySelectPost("mCatalogoTipoGarantiaPorcentaje", "mPorcentajeGarantia", {async: false});
			cambioTipoGarantiaId();
		}
		function cambioTipoGarantiaId(){
			$("#cIdPorcentaje").val($("#mPorcentajeGarantia option:selected").text());
			queryFormPost("mCatalogoTipoGarantiaId", {async: false});
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form action="">
  		<div id="container" class="container" style="width: 98%;">
  			<fieldset>
				<legend>Informaci&oacute;n de la Requisici&oacute;n</legend>
				<table align="left" cellpadding="2" width="100%">
			    	<tr>
			    		<td align="right" colspan="2">
							<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdPdfCaratulaReq" 		name="cmdPdfCaratulaReq" 	value="PDF"		onclick="openPDF('pdf');">&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdxlsCaratulaReq" 		name="cmdxlsCaratulaReq" 	value="Excel"	onclick="openPDF('xls');">&nbsp;&nbsp;
			    			<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdcsvCaratulaReq" 		name="cmdcsvCaratulaReq" 	value="CSV"		onclick="openPDF('csv');">&nbsp;&nbsp;
							<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all" 		id="cmdwordCaratulaReq" 	name="cmdwordCaratulaReq" 	value="Word"	onclick="openPDF('doc');">&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarCaratulaReq" 	name="imgAprobarCaratulaReq" 	value="Aprobar"	onclick="aprobarRequisicion();" style="display: none;">&nbsp;&nbsp;
                        	<input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverCaratulaReq" name="imgDevolverCaratulaReq" 	value="Devolver"	onclick="devolverRequisicion();" style="display: none;">&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 		id="imgAnularCaratulaReq" 	name="imgAnularCaratulaReq" 	value="Anular"	onclick="anularRequisicion();">&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	onclick="window.location = 'Requisiciones.jsp?tab=1&ses=0';">&nbsp;&nbsp;
						</td>
			    	</tr>
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 30px;border: 0px none ;background:#FEFEFE" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly  value="<%=unidadUsuarioLogeado%>"/>
				    		<input type="text" style="width: 690px;border: 0px none ;background:#FEFEFE" id="lblDescUsuario" name="lblDescUsuario" readonly />
				    	</td>
			    	</tr>
			    	<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 95px;border: 0px none ;background:#FEFEFE" id="lblRequisicion" name="lblRequisicion" readonly />
				    		<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" id="lblDescripcion" name="lblDescripcion" readonly />
				    	</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2">
			    			<input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly />
			    		</td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblEstado" id="lblEstado" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblPartida" id="lblPartida" readonly /></td>
			    	</tr>
			    	<tr>
			    		<td align="left" colspan="2"><input type="text" style="width: 700px;border: 0px none ;background:#FEFEFE" name="lblMesRequisicion" id="lblMesRequisicion" readonly/></td>
			    	</tr>
			    	<tr>
						<td align="left" colspan="2">Fecha de Apartado: 
							<input type="text" style="width: 90px;border: 0px none ;background:#FEFEFE"
							name="fechaVence" id="fechaVence" value="" readonly  />
						</td>
					</tr>
			    </table>
			</fieldset>
			<fieldset>
				<legend>Generar Copia de Requisici&oacute;n</legend>
					<table align="left" cellpadding="2" width="100%">
						<tr>
				    		<td align="left">Unidad Ejecutora:</td>
				    		<td align="left">
				    			<select id="cboUnidadEjecutoraConsultar" name="cboUnidadEjecutoraConsultar" style="width: 30em;">
									<option value="<%=usuarioTab.getU_UR()%>" selected="selected"></option>
								</select>
							</td>
				    	</tr>
				    	<tr>
				    		<td align="left">Ejercicio:</td>
				    		<td align="left">
				    			<select id="cboEjercicio" name="cboEjercicio">
								</select>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td colspan="2" align="center">
				    		<input type="button" id="btnCopiarRequisicion" name="btnCopiarRequisicion" value="Copiar" onclick="copiarRequisicion();" class="btnInterfaceBG ui-button  ui-corner-all" />
				    		</td>
				    	</tr>
				    </table>
			</fieldset>
		    <fieldset>
		    	<legend>Edici&oacute;n de la Requisici&oacute;n</legend>
		    		<table align="left" cellpadding="2" width="100%">
				    	<tr>
				    		<td colspan="2" style="color: red"><b>Cambiar la Partida y el Capítulo provocar&aacute; que se borren todas las l&iacute;neas de la Solicitud</b></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Cap&iacute;tulo</td>
				    		<td align="left"><select name="cIdCapitulo" id="cIdCapitulo" style="width: 550px"></select></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Partida</td>
				    		<td align="left"><select name="cIdSubPartida" id="cIdSubPartida" style="width: 550px"></select></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Mes</td>
				    		<td align="left"><select name="nIdPeriodo" id="nIdPeriodo">
				    				<option value="1">Enero</option>
				    				<option value="2">Febrero</option>
				    				<option value="3">Marzo</option>
				    				<option value="4">Abril</option>
				    				<option value="5">Mayo</option>
				    				<option value="6">Junio</option>
				    				<option value="7">Julio</option>
				    				<option value="8">Agosto</option>
				    				<option value="9">Septiembre</option>
				    				<option value="10">Octubre</option>
				    				<option value="11">Noviembre</option>
				    				<option value="12">Diciembre</option>
				    			</select>
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left">Alcance</td>
				    		<td align="left"><select name="nIdAlcance" id="nIdAlcance"></select></td>
				    	</tr>
				    	
				    	<tr>
				    		<td align="left">Descripci&oacute;n</td>
				    		<td align="left"><textarea rows="6" cols="75" name="cDescripcion" id="cDescripcion" onkeypress="textCounter(this,255);"></textarea></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Lugar de Entrega</td>
				    		<td align="left"><select name="cIdAlmacenEntrega" id="cIdAlmacenEntrega" style="width: 550px"></select></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Lugar de Compra</td>
				    		<td align="left"><select name="cIdAlmacen" id="cIdAlmacen" style="width: 550px"></select></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Tipo de Procedimiento</td>
				    		<td align="left"><select name="nIdCategoria" id="nIdCategoria" style="width: 550px"></select></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Fecha de elaboraci&oacute;n</td>
				    		<td align="left"><input type="text" id="fCreacion" name="fCreacion" disabled="disabled"/></td>
				    	</tr>
				    	<tr id="trFechaRequerida" style="display: none">
							<td align="left" colspan="2"><input type="text" style="width: 700px" id="rfvFechaRequerida" name="rfvFechaRequerida" readonly style="color: red; border-width:0; background-color:transparent" value="Necesita indicar una fecha válida. Selecciónela con el icono rojo de la derecha."/></td>
						</tr>
				    	<tr>
				    		<td align="left">Fecha Requerida</td>
				    		<td align="left"><input type="text" id="fechaRequerida" name="fechaRequerida" readonly/></td>
				    	</tr>
				    	<tr id="trPlazo" style="display: none">
							<td colspan="2" align="left"><input type="text" style="width: 700px" id="rfvPlazo" name="rfvPlazo" readonly style="color: red; border-width:0; background-color:transparent" value="Necesita indicar un plazo de días naturales con un número entero."/></td>
						</tr>
				    	<tr>
				    		<td align="left">Plazo</td>
				    		<td align="left"><input type="text" id="nIdPlazo" name="nIdPlazo" value="90" onkeypress="return onlyIntegers(event);"/>d&iacute;as naturales</td>
				    	</tr>
				    	<tr>
				    		<td align="left">Anexos</td>
				    		<td align="left"><input type="checkbox" id="chkAnexo"/> Incluye anexos</td>
				    	</tr>
				    	<tr>
				    		<td align="left">Tipo de Garantía</td>
				    		<td align="left"><select name="cTipoGarantia" id="cTipoGarantia" onchange="cambioTipoGarantia();"></select>
				    		
				    	</tr>
				    	<tr id="trPorGarantia" style="display: none">
							<td align="left" colspan="2"><input type="text" style="width: 700px" id="rfvGarantia" name="rfvGarantia" readonly style="color: red; border-width:0; background-color:transparent" value="Necesita indicar un porcentaje para la garantía válido."/></td>
						</tr>
				    	<tr>
				    		<td align="left">Porcentaje de Garant&iacute;a</td>
				    		<td align="left"> <select name="mPorcentajeGarantia" id="mPorcentajeGarantia" onchange="cambioTipoGarantiaId();"></select>
				    		</td>
				    	</tr>
				    	<tr id="trImpPoliza" style="display: none">
							<td align="left" colspan="2"><input type="text" style="width: 700px" id="rfvImportePoliza" name="rfvImportePoliza" readonly style="color: red; border-width:0; background-color:transparent" value="Necesita indicar un porcentaje de póliza válido."/></td>
						</tr>
				    	<tr>
				    		<td align="left">Importe de la p&oacute;liza de la responsabilidad civil</td>
				    		<td align="left"><input type="text" id="mImportePoliza" name="mImportePoliza" value="$0.00" onkeypress="return onlyMoney(event);"  onblur="cambiafrmt(this.name);"/></tr>
				    	<tr>
				    		<td align="left">Plurianualidad</td>
				    		<td align="left"><input type="text" id="cPlurianualidad" name="cPlurianualidad" onkeypress="textCounter(this, 79);"/>
				    	</tr>
				    	<tr>
				    		<td align="left">Usuario creaci&oacute;n</td>
				    		<td align="left"><input type="text" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" disabled="disabled"/>
				    	</tr>
				    	<tr>
				    		<td align="left">Fecha de vigencia</td>
				    		<td align="left"></td>
				    	</tr>
				    	<tr>
				    		<td align="left">Observaciones</td>
				    		<td align="left"><textarea rows="2" cols="50" name="cObservaciones" id="cObservaciones" onkeypress="textCounter(this,255);"></textarea></td>
				    	</tr>
				    	<tr>
				    		<td colspan="2" align="center">
				    		<input type="button" id="btnGuardarCaratulaReq" name="btnGuardarCaratulaReq" value="Guardar" onclick="guardar();" class="btnInterfaceBG  ui-button ui-corner-all" />
				    		<input type="button" id="btnCancelarCaratulaReq" name="btnCancelarCaratulaReq" value="Cancelar" onclick="window.location = 'Requisiciones.jsp?tab=2'" class="btnInterfaceBG  ui-button  ui-corner-all" />
				    	</tr>
				    </table>
		    </fieldset>
  		</div>
  		<!-- Zona de hidden's -->
	    <input type="hidden" name="nIdEstado" id="nIdEstado" value="1"/> <!--  ??? -->
	    
	    <input type="hidden" name="cIdEntidadContalbe" id="cIdEntidadContable" value="<%=usuarioTab.getU_Ramo()%>"/>
	    <input type="hidden" name="lAnexos" id="lAnexos" /> <!-- viene del checkbox -->
	    <input type="hidden" name="fSolicitud" id="fSolicitud" value="1900-01-01 00:00:00.000"/> <!-- ??? -->
	    <input type="hidden" name="fAprobacion" id="fAprobacion" value="1900-01-01 00:00:00.000"/> <!-- ???? -->
	    
	    <input type="hidden" name="fAnulacion" id="fAnulacion" value="1900-01-01 00:00:00.000"/> <!-- ??? -->
	    
	    <input type="hidden" name="cIdSolicitud" id="cIdSolicitud" value="RR-0000-1"/> <!-- es la concatenacion de tipoSolicitud + - + UnidadEjecutora + - + idconsecutivo -->
	    <input type="hidden" name="cIdFuenteFinanciamiento" id="cIdFuenteFinanciamiento" value="FF"/> <!-- ??? se puede editar? -->
	    <input type="hidden" name="cIdEntidadContable" id="cIdEntidadContable" value="10"/> <!-- usuario.getU_Ramo() -->
	    <input type="hidden" name="fRequerida" id="fRequerida" value="1900-01-01 00:00:00.000"/> <!-- se calcula con los datos de la forma -->
	    
	    <!-- Valores auxiliares para la selección del campo en el combobox -->
	    <input type="hidden" name="cIdUnidadEjecutoraRequisicion" id="cIdUnidadEjecutoraRequisicion" value="<%=cIdUnidadEjecutora%>" />
	    <input type="hidden" name="cIdTipoSolicitudRequisicion" id="cIdTipoSolicitudRequisicion" value="<%=cIdTipoSolicitud%>" />
	    <input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuarioTab.getU_UR()%>" />
	    <input type="hidden" name="nIdPeriodoRequisicion" id="nIdPeriodoRequisicion" />
	    <input type="hidden" name="nIdAlcanceRequisicion" id="nIdAlcanceRequisicion" />
	    <input type="hidden" name="nIdCategoriaRequisicion" id="nIdCategoriaRequisicion" />
	    <input type="hidden" name="isAdmin" id="isAdmin" value="<%=usuarioTab.getRole("ADMIN_RECMAT")%>" />
	    
	    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoSolicitud" id="cIdTipoSolicitud" value="<%=cIdTipoSolicitud%>" />
	    
	    <input type="hidden" name="cIdSubPartidaOriginal" id="cIdSubPartidaOriginal" />
	    
	    <input type="hidden" name="nIdEstadoReq" id="nIdEstadoReq" />
	    <input type="hidden" name="nLineas" id="nLineas" />
	    <!-- <input type="hidden" name="nIdEstado" id="nIdEstado" /> -->
	    <input type="hidden" name="nIdEstadoPrecomprometido" id="nIdEstadoPrecomprometido" /> 
	    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
	    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>
	    <input type="hidden" name="nConsolidados" id="nConsolidados" />
	    
	    <input type="hidden" name="cIdEstadoLinea" id="cIdEstadoLinea" />
	    
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuarioTab.getLogin()%>"/>
		<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
		
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
		<input type="hidden" name="cIdDocumento" id="cIdDocumento"/>
		<input type="hidden" name="cAccion" id="cAccion"/>
		<input type="hidden" name="mNombreBDSiguienteEjercicio" id="mNombreBDSiguienteEjercicio"/>
		<input type="hidden" name="mServerBDSiguienteEjercicio" id="mServerBDSiguienteEjercicio"/>
		<input type="hidden" name="vence" id="vence"/>
		<input type="hidden" name="Req" id="Req"/>
		<input type="hidden" name="difFecha" id="difFecha"/>
		<input type="hidden" name="existeMod" id="existeMod"/>
		<input type="hidden" name="cIdSolicitudMod" id="cIdSolicitudMod"/>
		<input type="hidden" name="existeLineaApartadoSolicitud" id="existeLineaApartadoSolicitud"/>
		<input type="hidden" name="sumaLineasSolicitud" id="sumaLineasSolicitud"/>
		<input type="hidden" name="sumaLineasApartadoSolicitudLineas" id="sumaLineasApartadoSolicitudLineas"/>
		<input type="hidden" name="numeroLineasApartadoSolicitud" id="numeroLineasApartadoSolicitud"/>
		<input type="hidden" name="numeroLineasSolicitud" id="numeroLineasSolicitud"/>
		<input type="hidden" name="cIdSolicitudRep" id="cIdSolicitudRep"/>
		<input type="hidden" name="capitulos" id="capitulos" value="1,2,4,5" />
		<input type="hidden" name="nId" id="nId" value="1" />
		<input type="hidden" name="cIdPorcentaje" id="cIdPorcentaje" value="" />
		<input type="hidden" name="cIdAlmacenEntregaRequisicion" id="cIdAlmacenEntregaRequisicion" value="." />
		<input type="hidden" name="cIdAlmacenRequisicion" id="cIdAlmacenRequisicion" value="." />
		
		
  	</form>
  </body>
</html>
