<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.implementacion.tesoreria.EgresosInterface"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Role"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="com.syc.gestion.core.Empleado"%>
<%@ page import="com.syc.gestion.core.Grupo"%>
<%@ page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@ page import="com.syc.gestion.core.EmpleadoArea"%>
<%@ page import="com.syc.gestion.CasoOperacionBusinessLogic"%>
<%@ page import="com.syc.gestion.core.AcumuladoInbox"%>
<%@ page import="com.syc.gestion.OpcionBusinessLogic"%>
<%@ page import="com.syc.gestion.core.Opcion"%>
<%@ page import="com.syc.gestion.core.Producto"%>

<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	Usuario su = (Usuario) session.getAttribute(GestionInterface.ATT_SUPPLANT_USER); // Usuario suplantacion
	
	session.removeAttribute(GestionInterface.ATT_MSG);
	
	Map mg = u.getGrupos();
	Set s = mg.keySet();
	Iterator it = s.iterator();
	boolean capturaPoliza=false;
	
	capturaPoliza =s.contains("CAPTURA_POLIZA");
	
	boolean isOficialia = false;
	while (!isOficialia &&
		   it.hasNext()) {
		String key = (String) it.next();
		Grupo g = (Grupo) mg.get(key);
		isOficialia = g.getNombre().equals("OFICIALIA");
		
	}

	Empleado e = (Empleado) session.getAttribute( GestionInterface.ATT_EMPLEADO );

	// Leer las opciones de Menu del usuario
	OpcionBusinessLogic ol = new OpcionBusinessLogic(GestionInterface.ATT_CONEXION);
	Vector entries = ol.getOpcionByUser(u.getLogin(), Opcion.OPC_EN_MENU, Producto.PRD_GESTION);

	/*
	 * Si el usuario pertenece a contabilidad, se mostrara el combo para seleccionar el CC en el que desea realizar las operaciones.
	 * > En adelante se refiere a este cambio como V1
	 */
	 boolean esUsuarioContabilidad = u.getPropiedad("ROL_CONTABLE")!=null && u.getPropiedad("ROL_CONTABLE").getValor() != null;
	 boolean esAdministradorContabilidad = u.getPropiedad("ROL_CONTABLE")!=null && u.getPropiedad("ROL_CONTABLE").getValor() != null && "ADMIN_CONTABILIDAD".equals(u.getPropiedad("ROL_CONTABLE").getValor());
	 boolean esUsuarioVentanilla = u.getPropiedad("ADMIN_VENTANILLA")!=null && u.getPropiedad("ADMIN_VENTANILLA").getValor() != null;
	 String cCentroContable = (u.getPropiedad("CCENTROCONTABLE") != null? (u.getPropiedad("CCENTROCONTABLE").getValor() != null? u.getPropiedad("CCENTROCONTABLE").getValor():"" ):"" );

	 boolean esAdminNormatividad = u.getPropiedad("ADMIN_NORMATIVIDAD")!=null && u.getPropiedad("ADMIN_NORMATIVIDAD").getValor() != null;
	/* FIN V1 */
	
	/*VGC20190404 Para el acceso directo de autorizadores de pagos.*/
	String action = StringUtils.trimToEmpty((String) session.getAttribute(GestionInterface.ATT_CMD_AUT));
	session.removeAttribute( GestionInterface.ATT_CMD_AUT );

	boolean logueoParaAutorizacion = false;
	
	logueoParaAutorizacion = action.toUpperCase(  ).contains( "AUT" ) || action.toUpperCase(  ).contains( "VOBO" ) || "FirmaReporte".equalsIgnoreCase(action);
	
	String ul = StringUtils.trimToEmpty((String) session.getAttribute(GestionInterface.ATT_LOGIN));
	String un = StringUtils.trimToEmpty((String) session.getAttribute(EgresosInterface.USER_PRM));
	String d = StringUtils.trimToEmpty((String) session.getAttribute(EgresosInterface.DOCUMENT_PRM));
	Integer f = (Integer) session.getAttribute(EgresosInterface.FOLIO_PRM);
	Integer orden = (Integer) session.getAttribute(EgresosInterface.ORDEN);

	session.removeAttribute(GestionInterface.ATT_LOGIN);
	session.removeAttribute(EgresosInterface.USER_PRM);
	session.removeAttribute(EgresosInterface.DOCUMENT_PRM);
	session.removeAttribute(EgresosInterface.FOLIO_PRM);

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if(c != null)
		session.removeAttribute( GestionInterface.ATT_CASE );
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="es" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="robots" content="all" />

		<!--  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />-->
		<title>SAI - Principal</title>
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
		<link href="../css/gestion.css" rel="stylesheet" type="text/css" media="all" />
		<link href="../css/interfaz.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="../css/themes/base/jquery.ui.base.css" type="text/css" media="all" />
		<link rel="stylesheet" href="https://code.jquery.com/ui/1.8.21/themes/base/jquery-ui.css" type="text/css" media="all" />
		<link rel="stylesheet" href="https://static.jquery.com/ui/css/demo-docs-theme/ui.theme.css" type="text/css" media="all" />
		<link href="../css/gestion.css" rel="stylesheet" type="text/css" media="all" />
		<link rel="stylesheet" href="../css/mktree.css" type="text/css" media="all" />
		<link type="text/css" rel="stylesheet" href="../css/themes/base/jquery.ui.all.css" />
		<link rel="stylesheet" href="../demos.css"/>
		<link href="../css/menuContabilidad.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		
		<style type="text/css">
		</style>
		<style>
			html, body {
			overflow: hidden;
			//overflow - x: hidden;
			height: 99.7%;
		}
		div.a {
			  width: auto;
			  border: 1px solid black;
			}

		</style>

		<script src="../js/funciones-interfaz.js" type="text/javascript"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/ContabilidadCentroContable.js"></script>
		<script type="text/javascript" src="../js/mktree.js"></script>

		<!-- ************************************************************************** -->
		<!-- Se agregan librerías para utilizar el método accordion de jquery  -->
		<script src="../js/jquery-1.6.2.min.js" type="text/javascript"></script>
		<script src="../js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
		<script src="../js/ui/jquery.ui.accordion.js"></script>
		<script src="../js/ui/jquery.ui.core.js"></script>
		<script src="../js/ui/jquery.ui.widget.js"></script>
		<!-- ************************************************************************** -->
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript">
		if (top.location != document.location)
		    top.location.href = document.location.href;
		</script>
		<script language="javascript" type="text/javascript">
		<!--
		var monthNames = new Array('Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre');
		var dayNames = new Array('Domingo','Lunes','Martes','Mi\u00E9rcoles','Jueves','Viernes','S\u00e1bado');
		var logueoParaAutorizacion = <%=logueoParaAutorizacion%>;
		var  ul = "<%=ul%>";
		var  un = "<%=un%>";
		var  d =  "<%=d%>";
		var  f =  "<%=(f == null ? "" : f.toString())%>";
		var  a =  "<%=action%>";
		var  o =  "<%=orden%>";
		
		/* V1 */
		var usuarioContabilidad = <%=esUsuarioContabilidad%>;
		var administradorContabilidad = <%=esAdministradorContabilidad%>;
		var usuarioCentroContable = '<%=cCentroContable%>';
		/* Fin V1 */
	
		Number.prototype.zf = function(n) {
			var s = "" + this;
			var r = "";
			if (n < s.length)
				return s;
			for (var i = 0; i < (n - s.length); i++)
				r += "0";
			return r + this;
		}

		Date.prototype.format = function(f) {
			if (!this.valueOf())
				return '&nbsp;';

			var d = this;

			return f.replace(/(yyyy|mmmm|mmm|mm|dddd|ddd|dd|hh|mi|ss|a\/p)/gi,
		        function($1) {
		            switch ($1.toLowerCase()) {
						case 'yyyy': return d.getFullYear();
						case 'mmmm': return monthNames[d.getMonth()];
						case 'mmm':  return monthNames[d.getMonth()].substr(0, 3);
						case 'mm':   return (d.getMonth() + 1).zf(2);
						case 'dddd': return dayNames[d.getDay()];
						case 'ddd':  return dayNames[d.getDay()].substr(0, 3);
						case 'dd':   return d.getDate().zf(2);
						case 'hh':   return ((h = d.getHours() % 12) ? h : 12).zf(2);
						case 'mi':   return d.getMinutes().zf(2);
						case 'ss':   return d.getSeconds().zf(2);
						case 'a/p':  return d.getHours() < 12 ? 'AM' : 'PM';
					}
				}
			);
		}

		var myInnerText = setInterval("document.getElementById('fecha').innerText = (new Date()).format('dddd dd de mmmm de yyyy hh:mi:ss a/p    ');",1000);
		
		// 1. Llama a gestionservlet con comando y parametro necesario
		// (supone que la llamada esta en frm.action solo faltan los comandos)
		//
		// 2. Marca la opcion de menu seleccionada y desmarca las demas.
		//    Esto requiere que los tags <a> de todas las opciones de menu:
		//    a. tenga su propiedad: class="Menu"
		//    b. tenga su propiedad: name="menu".
		//    c. tengan un id unico.
		
		var make_button_active = function()
		{
		  $("#menup li").each(function (index)
		    {
		      $(this).removeClass('active');
		    }
		  )
		  $(this).addClass('active');
		}

		function actionForm (comando, params, id) {
			// Variables
			var mnu = document.getElementsByName('menu');
			var frm = document.getElementById("frmMenu");
			var frm2 = document.getElementById("frmUpd");
			var action;
			var oldAction = action = frm.action;

			// Comandos
			if (comando == 'CMD_EXIT') {
				frm.target = "";
				frm.action = action + <%=GestionInterface.CMD_EXIT%>;
				frm.submit();

			    frm.action = oldAction;
			} else 	if (comando == 'CMD_OPEN_INBOX') {
				action += <%=GestionInterface.CMD_OPEN_INBOX%>;
				action += '&<%=GestionInterface.PRM_OPER%>=' + params + "&pagina=0";
				frm.action = action;
				frm.submit();

			    frm.action = oldAction;
			    
			} else 	if (comando == 'CMD_OPEN_INBOX2') {
				action += <%=GestionInterface.CMD_OPEN_INBOX2%>;
				action += '&<%=GestionInterface.PRM_OPER%>=' + params + "&pagina=0";
				frm.action = action;
				frm.submit();

			    frm.action = oldAction;

			} else if ( comando == 'CMD_INIT_CASE') {
				
				action += '<%=GestionInterface.CMD_INIT_CASE%>';
				frm.action = action;
				frm.submit();
		    	frm.action = oldAction;
			} else if ( comando == 'CONTEXT_PATH') {
				action = '<%=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/" %>'
					+ (params.indexOf('?') >= 0? params + "&" : params + "?" )
					+ 'u_login=<%=u.getLogin()%>';
					window.frames['content-iframe'].location.href = action ;

			}else if ( comando == 'CONTEXT_PATH2') {
			    //$("#frmMenu").attr("action", $("#frmMenu").attr("action") + <%=GestionInterface.CMD_INIT_CASE%>);
                $("#id_tc").val("13");
                
                action += '<%=GestionInterface.CMD_INIT_CASE%>';
				frm.action = action;
				frm.submit();
		    	frm.action = oldAction;
                
			    
			}else if ( comando == 'CMD_FUN_MANTO') {
			funManto (params);
			}else if ( comando == 'CMD_FUN_MANTO_MATERIALES') {
			funMantoMateriales (params);
			} else if ( comando == 'CMD_SEND_CHNG_PWD') {
				action += <%=GestionInterface.CMD_SEND_CHNG_PWD%>;
				frm.action = action;
				frm.submit();
				frm.action = oldAction;
			} else if ( comando == 'CMD_SEND_RESET_PWDS') {
				action += <%=GestionInterface.CMD_SEND_CHNG_PWD%>;
				frm.action = action;
				frm.submit();
				frm.action = oldAction;
			} 
			  else if ( comando == 'PRM_CMD') {
				action += <%=GestionInterface.CMD_INIT_CASE%>;
				frm2.action = action;
				frm2.submit();
				frm2.action = oldAction;
			} 
			else {
				/* NO IMPLEMENTADO */
			}

			// Marcar opcion seleccionada
			for (var i = 0; i < mnu.length; i++)
			{
				if (id == mnu[i].id)
					mnu[i].className = "marcado";
				else
					mnu[i].className = "Menus";
			}
		}

		function changeTableWidth(someWidth) {
			var tbl  = document.getElementById("prTable");
			var obj1 = document.getElementById("smallCell");
			var obj2 = document.getElementById("bigCell");
			obj1.width=someWidth;
			obj2.width=tbl.offsetWidth-someWidth;
		}

		function funMantoMateriales(strXML)
		{
			
			var szID   = strXML;
			var szTemp = "";
			szAnchoDlg = ", width=400";
			szAltoDlg  = ", height=400";

			var arrContexto = document.location.pathname.split('/');
			var URL = document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/" + arrContexto[1] + "/Ayudas/";
			
			AyudaXML = loadXML(URL + "xml-ayudas/" +strXML + ".xml");

			if (AyudaXML)
			{
				szAnchoDlg = ",width=" + AyudaXML.find("Ayuda>anchoWin").text;
				szAltoDlg = ", height=" + AyudaXML.find("Ayuda>altoWin").text;
			}
			else
			{
				alert("Error..!\rNo existe la definici\u00F3n de la ayuda \r\r" + szID);
				return;
			}
			AyudaXML = null;
			 wndLCD = window.open(URL + "dlg2.0.jsp?id=" + szID,szID,"directories = no, resizable = no, menubar = no, titlebar =no, status=no, scrollbars = no " + szAnchoDlg + szAltoDlg, false );
			 wndLCD.focus();
		}
		
		function openCenteredWindow(url, name, height, width, parms) {
			var left = Math.floor((screen.width - width) / 2);
			var top = Math.floor((screen.height - height) / 2);
			var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";
			if (parms) {
				winParms += "," + parms;
			}
			var win = window.open(url, name, winParms);
			if (parseInt(navigator.appVersion) >= 4) {
				win.window.focus();
			}
			return win;
		}
		
		function loadXML(urlSource){
			var xml = "";
			
			$.ajax({
				    type :"GET",
				    url : urlSource,
					async: false,
				    success : function(dataXML){
						 xml = $(dataXML);
					}
			});
			
			return xml;
		}
		
		function funManto(strXML)
		{
			var szID   = strXML;
			var szTemp = "";
			szAnchoDlg = ", width=400";
			szAltoDlg  = ", height=400";

			AyudaXML = null;
			var arrContexto = document.location.pathname.split('/');
			var URL = document.location.protocol + "//" + (document.location.host==""?"localhost":document.location.host) + "/" + arrContexto[1] + "/Ayudas/";

			AyudaXML = loadXML(URL + "xml-ayudas/" + szID + ".xml");
			
			szAnchoDlg = ",width=" + AyudaXML.find("Ayuda>anchoWin").text();
			szAltoDlg = ", height=" + AyudaXML.find("Ayuda>altoWin").text();
		 
			AyudaXML = null;
			window.frames['content-iframe'].location.href = URL + "dlg2.0.jsp?id=" + szID ;
		}
		
		//-->
		/* **************************************************************************
		* Para utilizar el metodo accordion del api de jquery (jquery-1.6.2.min.js) *
		* Martha Aurora Sánchez Valdivieso							13/06/2012  *
		*************************************************************************** */
		$(function() {
			$( '#menup' ).accordion({
				collapsible: true,
				autoHeight: false
			});
			
			var es_ie = navigator.userAgent.indexOf("MSIE") > -1 ;
			if(es_ie){
				$("#menup").css({'overflow':'hidden'});
			}else{
				$("#menup").css({'overflow':'scroll'});
			} 
			//$("#menup").css({'overflow':'scroll'});
		});

		/* ************************************************************************ */
		/* V1 */
		function loadInit(){
		   
		   $("#navcontainer").hide();
		   
		    if( logueoParaAutorizacion ){
		    
		    	if ( d == "REQFUELWALLET" ){
					window.frames['content-iframe'].location.href =   "../SICOVE/VehicleFuelRequest.jsp?REQUEST_FOLIO=" + f + "&ACTION=3";
		    	}else if ( d == "REQFUELACCOUNT" ){
					window.frames['content-iframe'].location.href =   "../SICOVE/ReviewFuelRequest.jsp?REQUEST_FOLIO=" + f + "&ACTION=3";
		    	}else{
			    	var page = "";
			    	if( d== "CAJA" ){
						page = "../Generador/" + "ResumenCajaNoPresupuestal.jsp"
					}else if ( d == "COMSINVIATICOS"){
						page = "../Generador/" +"ComisionesSinComprobacionFirma.jsp"
					}else if ( d == "REPORTE"){
						page = "../Generador/" + "ReportesFirma.jsp"
					}else if ( a.toUpperCase().indexOf("MASIVO") > 0 ){
						page = "../Generador/" + "ResumenPagosMasivo.jsp"
					}else if ( d == "POLIZA" ){
						page = "../plantillasCasos/ResumenPolizaManualFIEL.jsp"
					}else if ( d == "CONTRATODIVERSO" ){
						page = "../FIEL/ResumenRMFIEL.jsp"
					}else if ( d == "ENTERASATISFACCION" ){
						page = "../FIEL/ResumenENSAFIEL.jsp"
					}else if ( d == "APARTADO" ){
						page = "../FIEL/ResumenApartado.jsp"
					}else if ( d == "REINTEGROCAJA" ){
						page = "../Generador/ResumenReintegrosCaja.jsp"
					}else if ( d == "OBRAPUBLICA" ){
						page = "../FIEL/ResumenEstimacionFIEL.jsp"
					}else if ( d == "VIATICOS" ){
						page = "../Generador/ResumenComisionViaticos.jsp"
					}else if ( d == "CVIATICOS" ){
						page = "../Generador/CompruebaComisionViaticos.jsp"
					}else if ( d == "CONCILIABANCOS" ){
						page = "../Generador/ConciliacionBancariaFirma.jsp"
					}else if ( d == "COMPROMISO" ){
						page = "../Generador/ResumenCompromiso.jsp"
						
					}else {
						page = "../Generador/" + "ResumenPagos.jsp";
					}
			    	window.frames['content-iframe'].location.href =   page + "?a=" + a + "&d=" + d + "&f=" + f + "&o=" + o;
		    	}
		    	
				
			}
			if(usuarioContabilidad==true){
				if(administradorContabilidad){
					querySelectPost('BALCTROCONTABLE', 'hPolCtroContableC', {async: false });
					$("#hPolCtroContableC").val(usuarioCentroContable);
					$("#navcontainer").show();
				}else{
					$("#usuarioCentroContable").val(usuarioCentroContable);
					querySelectPost('catalogoCentContRedu', 'hPolCtroContableC', {async: false });
				}
				setCentroContable(usuarioCentroContable);
			}else{
				$("#cContable").hide();
			}
			
			$("#control_menu").live("click",function(){
				toggleVerMain(this,'smallCell', 'apMenu');
				});
			
		}
		
		function changeCC(){
			setCentroContable( $("#hPolCtroContableC").val() );
			var CentroContable = document.getElementById('hPolCtroContableC').value;
			var param = "CentroContable=" + CentroContable;
			$.ajax({
								url: './PrincipalCCResultado.jsp',
								dataType: 'json',
								data: {"CentroContable" : CentroContable},
								async : false,
								success : function(json) {
										r = json.status== 'OK';
								}
							});
			actionForm ('CMD_OPEN_INBOX','\'ATENCION\',\'ATENCION_COPIA\',\'COPIA_PARA\',\'RECHAZO_RESPUESTA\',\'RECHAZO_PRORROGA\',\'ACEPTAR_PRORROGA\'','mnu_entrada');
		}
		
		
		function loadXML(urlSource){
			var xml = "";
			
			$.ajax({
				    type :"GET",
				    url : urlSource,
					async: false,
				    success : function(dataXML){
						 xml = $(dataXML);
					}
			});
			
			return xml;
		}
	  function capturaPoliza()
	  {
	   var frm = document.getElementById("frmMenu");
	   var action;
	   var oldAction = action = frm.action;
       $("#frmMenu").attr("action", $("#frmMenu").attr("action") + <%=GestionInterface.CMD_INIT_CASE%>);
       $("#id_tc").val("13");
       $("#frmMenu").submit();
       frm.action = oldAction;
      }
	  
	  function resizeContent(){
          setTimeout(function(){
                          var win = $("[name='content-iframe']"); //this = window
                          var bar = 0;
                          if ($("#menup").is(":visible")){
                                          bar = $("#menup").width();
                          }else bar = 0;
                          win.width($( window ).width()-(bar+50));           
          },500);
          
		}

	$(document).ready(function(){
		
		actNotif();
		
		 	});
	
	function actNotif(){
		queryFormPost("NotificacionCargasMasivasPendientes",{async : false});
		var notif = parseInt($("#nNot").val(),10); 
		if(notif > 0){
			$(".clsNotificacion").show(); 
			$("#nNotifiacion").text($("#nNot").val());	
		}else{
			$(".clsNotificacion").hide();
		}
	}
		/* FIN V1 */
		</script>
	</head>

	<!--Ethiel, desde el onload ocultamos el menu. body onload="actionForm ('CMD_OPEN_INBOX','\'ATENCION\',\'ATENCION_COPIA\',\'COPIA_PARA\',\'RECHAZO_RESPUESTA\',\'RECHAZO_PRORROGA\',\'ACEPTAR_PRORROGA\'','mnu_entrada');changeTableWidth(100); acumulado();"-->
	<body onload="actionForm ('CMD_OPEN_INBOX','\'ATENCION\',\'ATENCION_COPIA\',\'COPIA_PARA\',\'RECHAZO_RESPUESTA\',\'RECHAZO_PRORROGA\',\'ACEPTAR_PRORROGA\'','mnu_entrada');changeTableWidth(1);toggleVerMain('none','smallCell', 'apMenu');loadInit();">
	

		<form id="frmMenu" action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=" method="post" target="content-iframe" style="height: 97%;">
			<input type="hidden" name="id_tc"     id="id_tc"     value="1"/>
			<input type="hidden" name="h_u_login" id="h_u_login" value="<%=u.getLogin()%>"/>
			<input type="hidden" name="h_id_area" id="h_id_area" value="<%=e.getClaveArea() == null ? "" : e.getClaveArea() %>"/>
			<input type="hidden" name="usuarioCentroContable" id="usuarioCentroContable" value=""/>
			<input type="hidden" name="tipoReporte" id="tipoReporte" value=""/>
			<input type="hidden" name="nNot" id="nNot" value="0"/>			
			

			<table id="prTable" width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
				<tr style="height: 5%;">
					<td colspan="3" >
					    <% if(esUsuarioContabilidad) {%>
					    <table id="tabla3" border="0" width="100%" cellpadding="0" cellspacing="0" style="BORDER-BOTTOM: solid 1px silver; background-image:url('../imagenes/bckgnd-header2.png');">
					    <%}else{ %>
						<table id="tabla3" border="0" width="100%" cellpadding="0" cellspacing="0" style="BORDER-BOTTOM: solid 1px silver; background-image:url('../imagenes/bckgnd-header.png');">
						<%} %>
							<tr>
							    
								<td width="216px" height="50px"> <img src="../imagenes/logotipo-sistema_FR.png" /></td>
								
								<td style="FONT-SIZE: 8pt; Color:gray" >
									<%=e.getNombreCompleto()%><br/>
									<%=e.getCargo()%>
									<div id="cContable" >
									<span>CENTRO CONTABLE:
									<select id="hPolCtroContableC" name="hPolCtroContableC" onchange="changeCC();" ></select>
									</span>
									</div>
								</td>
									
<% if(esUsuarioContabilidad) {%>
								<td width="350px" align="left" >
	
					   								
<div id="horizontalmenu">

<ul>
	
<li ><a class="Enlace" id="balanza" href="../Generador/BalanzaDetalle.jsp" target="content-iframe" onclick="toggleVerMain('none','smallCell', 'apMenu');" title="Balanza"></a></li>
<li ><a class="Enlace" id="consultaP" href="../admin/reporte_frame.jsp?id=12" target="content-iframe" onclick="actionForm('CONTEXT_PATH','',this.id); toggleVerMain('none','smallCell', 'apMenu');" title="Consulta Polizas"></a></li>
<%if(capturaPoliza)
    out.println("<li ><a class=\"Enlace\" id=\"crearP\" href=\"#\" onclick=\"capturaPoliza();\" title=\"Captura Poliza\"></a></li>");								 
%>
<li ><a class="Enlace" id="auxiliar" href="../admin/reporte_frame.jsp?id=14&id2=5" target="content-iframe"  onclick="actionForm('CONTEXT_PATH','',this.id); toggleVerMain('none','smallCell', 'apMenu');" title="Auxiliar"></a></li>  
  <li ><a class="Enlace" id="detalle" href="../admin/reporte_frame.jsp?id=14&id2=4" target="content-iframe"  onclick="actionForm('CONTEXT_PATH','',this.id); toggleVerMain('none','smallCell', 'apMenu');" title="Detalle"></a></li>  
<%if(esUsuarioVentanilla){%>   
  <li>
	  <div class="contenedorNot">
	  	<a class="Enlace" id="notificacion" href="../Generador/cargaRelacionGastos.jsp" onclick="toggleVerMain('none','smallCell','apMenu');" target="content-iframe" title="Cargas Pendientes"></a>	  	
	  	<div class="clsNotificacion" title="Cargas Pendientes" style="cursor:pointer;"><span id="nNotifiacion"></span></div>		
  	</div> 
  </li>
<% } %>	  
</ul>
</div>
								</td>

<%} %>								
								<td width="280px" align="right">
									<div id="fecha" style="FONT-SIZE: 8pt; Color:gray"></div><br/>

									<input type="button" class="btnInterfaceSALIR" id="btnSalir" name="btnSalir" value="Cerrar Sesión" onclick="javascript:actionForm ('CMD_EXIT','','')" />
								</td>								
							</tr>
						</table>
					</td>
				</tr>
				<tr style="height: 95%;">
					<td id="smallCell"  width="5%" valign="top">
						<table id="tabla4" width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
							<tr>								
								<td id="apMenu" nowrap="nowrap">
									<div class="margenA" id="menup" style="overflow: auto; height: 100%; width: 320px; ">
									<%
										Enumeration ent = entries.elements();
										Opcion o;

										/* *****************************************************************************
										* Se rehace este JSP para utilizar el metodo accordion del api de jquery
										* (jquery-1.6.2.min.js)
										* Martha Aurora S\u00E1nchez Valdivieso							13/06/2012
										*************************************************************************** */
										String str = "";
										String padreAnterior = "";
										String padreActual = "";
										String hijoActual = "";
										String hijoAnterior = "";
										boolean cerrar = false;
										while (ent.hasMoreElements()) {
											o = (Opcion) ent.nextElement();

											padreActual = str.valueOf(o.getO_orden()).substring(0,3);
											hijoActual = str.valueOf(o.getO_orden()).substring(3,6);

											//pinta solo etiquetas principales
											if( !padreActual.equals(padreAnterior)){
												padreAnterior = padreActual;
												if(cerrar){
									%>
											</ul>
										</div>
									<%
												cerrar = false;
											}
									%>
										<h3><a href="#" onclick="resizeContent();"><%=o.getO_descripcion()%></a></h3>
										<div style="overflow: auto; overflow-x:auto; max-height: 300px; padding-top: 0px; padding-bottom: 0px;">										
											<ul>
									<%
												cerrar = true;
												continue;
											}
									%>
												<li><%= o.getO_action().indexOf("/normatividad") > 0 ? ( o.getO_action().replace("/normatividad?loginFromSAI=true","/normatividad?loginFromSAI=true&txtUsuario=" + (esAdminNormatividad ? "normatividad":"cnormatividad")  ) ) :  ( o.getO_action().replace("/login?loginFromSAI=true","/login?loginFromSAI=true&txtUsuario=nmontagut") )%></li>
									<%
										}

										if(cerrar){
									%>
											</ul>
										</div>
									<%
										}
									%>
									</div>
								</td>
							</tr>
						</table>
					</td>
					<td class="C2" width="50px" valign="top">
						<img  src="../imagenes/mAbrir.png" alt=" " width="30px" height="30px" style="padding: 5px 10px 5px" title="Ocultar men&uacute;" id="control_menu" /></td>
					<td id="bigCell"  width="95%" valign="top" align="center">
						<iframe name="content-iframe" width="99%" marginwidth="0" height="100%" marginheight="0" align="top" scrolling="auto" frameborder="0"></iframe>
					</td>
				</tr>
			</table>
		</form>

	</body>
</html>
