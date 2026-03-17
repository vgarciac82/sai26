<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="com.syc.contable.AdministracionMensajesBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@page import="java.util.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.syc.gestion.documental.Documental"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="com.syc.gestion.util.PaginaData"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.contable.core.AdecuacionManager"%>
<%@page import="com.syc.contable.AdministracionMensajesBusinessLogic"%>
<html>
<%!private Logger log = Logger.getLogger(getClass());
	private String jniName = null;

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = GestionInterface.ATT_CONEXION;
				log
						.info("Environment Entry \"dataSourceRefName\" nula usando default \""
								+ jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log
					.info("Environment Entry \"dataSourceRefName\" no definida usando default \""
							+ jniName + "\"");
		}
	}%>
<%
	int[] totales = { 0, 0, 0, 0 };
	String dateFormat = "dd-MMM-yyyy HH:mm:ss";
	String msg = request.getParameter(GestionInterface.PRM_USER_MSG);

	String ipNombreServidor = java.net.InetAddress.getByName(
			request.getServerName()).toString();
	String[] ipServidor = ipNombreServidor.split("/");
	String mensajeVersion = "";
	if (ipServidor[1].equals("127.0.0.1")) {
		mensajeVersion = "LOCALHOST";
	} else if (ipServidor[1].equals("172.29.150.94")) {
		mensajeVersion = "PREPRODUCCION";
	} else if (ipServidor[1].equals("172.29.150.26")) {
		mensajeVersion = "PRUEBAS";
	}
	String ejercicio="";
/*
	String mensajeAvisos = "";
	mensajeAvisos +="Atención: El módulo de Adecuaciones Presupuestales se suspenderá por cierre del mes de Julio,  "+
					"a partir del día miércoles 25 de julio a las 18:00 y se reiniciará el día miércoles primero de agosto a las 09:00. "+
					"No se omite señalar que las adecuaciones en tramite serán concluidas. Favor de cerrar su sesión antes del horario indicado.";
	boolean alertMensajeAvisos = true;
	
*/
	Usuario u = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
   // System.out.println(u);
	if (u == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	List<?> v = (List<?>) session.getAttribute(GestionInterface.ATT_INBOX);
	CasoBusinessLogic ct = new CasoBusinessLogic("jdbc/gestion");

	int promFilter = -1;
	String strPromFltr = request
			.getParameter(GestionInterface.PRM_PROM_FILTER);
	if (strPromFltr != null)
		promFilter = Integer.parseInt(strPromFltr);

	String UR = u.getU_UR();
	String cCentroContable = null;
	if (u.getPropiedades() != null
			&& u.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
	}

	if (UR == null || cCentroContable == null) {
		if (UR == null)
			session
					.setAttribute("login.message",
							"UR no asignada al usuario, favor de contactar al administrador");
		if (cCentroContable == null)
			session
					.setAttribute("login.message",
							"Centro Contable no asignado al usuario, favor de contactar al administrador");
		if (UR == null && cCentroContable == null)
			session
					.setAttribute(
							"login.message",
							"Centro Contable y UR no asignados al usuario, favor de contactar al administrador");
		response.sendRedirect("../index.jsp");
		return;
	}
	Map preferencias_cliente = null;
	try{
		Connection conn_prop_cte=ct.getConnection();
		preferencias_cliente = GrupoPropiedadesManager.select(conn_prop_cte,"PREFERENCIAS_CLIENTE");
		ejercicio=AdecuacionManager.obtenEjercicioFiscal(conn_prop_cte); 
		conn_prop_cte.close();
		conn_prop_cte=null;
	}
	catch(Exception expropcte){log.error("Error leyendo propiedades del cliente o ejercicio: ",expropcte);}
	
	/*	Esta seccion de mensajes con la tabla de parametros del sistema se comenta porque ahora existe opción en especial para manejar mensajes.
	String mensajeAvisos = "";
	boolean alertMensajeAvisos = false;
	if(preferencias_cliente!=null&&preferencias_cliente.containsKey("mensaje_inbox")&&preferencias_cliente.get("mensaje_inbox")!=null){
		mensajeAvisos += ((GrupoPropiedades)preferencias_cliente.get("mensaje_inbox")).getValor();
	}
	if(preferencias_cliente!=null&&preferencias_cliente.containsKey("alert_mensaje_inbox")&&preferencias_cliente.get("alert_mensaje_inbox")!=null){	
		alertMensajeAvisos = new Boolean(((GrupoPropiedades)preferencias_cliente.get("alert_mensaje_inbox")).getValor());
	}*/
	
	//mensajes de inbox y alerta.
	String u_login = u.getLogin();
	String mensajeAvisos = "";
	boolean alertMensajeAvisos = false;
	
	AdministracionMensajesBusinessLogic amBL = new AdministracionMensajesBusinessLogic(jniName);
	mensajeAvisos += amBL.mensajeInbox( UR, cCentroContable, u_login );
	alertMensajeAvisos = new Boolean( amBL.mensajeInboxAlerta (UR, cCentroContable, u_login)); 

	Caso cc = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if(cc != null)
		session.removeAttribute( GestionInterface.ATT_CASE );
%>
<head>
<title>Operaciones de<%=u.getNombre()%></title>

<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<style type="text/css" title="currentStyle">
		@import "../Generador/css/demo_page.css";
		@import "../Generador/css/demo_table_jui.css";
		@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
	</style>


<link type="text/css" href="../css/gestion.css" rel="stylesheet">
<link type="text/css" href="../css/scrolltable.css" rel="stylesheet">
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript">
function filtraInbox(){
	location.href = '../gstnmngr/gestion?cmd='+<%=GestionInterface.CMD_FILTRA_INBOX%>
	+ "&fSAI=" + $("#col1_filter").val() 
	+ "&FolioSICOP="+($("#col2_filter").length > 0 ? $("#col2_filter").val():"")
	+ "&PolizaDocumento="+($("#col2_2filter").length > 0 ? $("#col2_2filter").val():"")
	+ "&Fecha="+$("#col3_filter").val()
	+ "&Tramite="+$("#col4_filter").val()
	+ "&Operacion="+$("#col5_filter").val()
	+ "&Aplicacion="+$("#col6_filter").val()
	+ "&Importe="+$("#col7_filter").val()
	+ "&SICOP="+$("#col8_filter").val()
	+ "&MAP="+$("#col9_filter").val()
	+ "&Operador="+$("#col10_filter").val()
	+ "&Tipo="+($("#col11_filter").length > 0 ? $("#col11_filter").val():"")
	+ "&Nivel="+($("#col12_filter").length > 0 ? $("#col12_filter").val():"")
	+ "&FirmaElectronica="+($("#col13_filter").length > 0 ? $("#col13_filter").val():"");
}

$(document).ready(function() {
	$("#pb_actualizar").button();
	$("#pb_crear").button();
	<% if( request.getParameter("fSAI") != null ){ %>
    <% 	out.println("\t\t$(\"#col1_filter\").val(\"" + request.getParameter("fSAI") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("FolioSICOP") != null ){ %>
    <% 	out.println("\t\t$(\"#col2_filter\").val(\"" + request.getParameter("FolioSICOP") + "\")"); %>
    <%}%>
    <% if( request.getParameter("PolizaDocumento") != null ){ %>
    <% 	out.println("\t\t$(\"#col2_2filter\").val(\"" + request.getParameter("PolizaDocumento") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("Fecha") != null ){ %>
    <% 	out.println("\t\t$(\"#col3_filter\").val(\"" + request.getParameter("Fecha") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("Tramite") != null ){ %>
    <% 	out.println("\t\t$(\"#col4_filter\").val(\"" + request.getParameter("Tramite") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("Operacion") != null ){ %>
    <% 	out.println("\t\t$(\"#col5_filter\").val(\"" + request.getParameter("Operacion") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("Aplicacion") != null ){ %>
    <% 	out.println("\t\t$(\"#col6_filter\").val(\"" + request.getParameter("Aplicacion") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("Importe") != null ){ %>
    <% 	out.println("\t\t$(\"#col7_filter\").val(\"" + request.getParameter("Importe") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("SICOP") != null ){ %>
    <% 	out.println("\t\t$(\"#col8_filter\").val(\"" + request.getParameter("SICOP") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("MAP") != null ){ %>
    <% 	out.println("\t\t$(\"#col9_filter\").val(\"" + request.getParameter("MAP") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("Operador") != null ){ %>
    <% 	out.println("\t\t$(\"#col10_filter\").val(\"" + request.getParameter("Operador") + "\")"); %>
    <%}%>
     
    <% if( request.getParameter("Tipo") != null ){ %>
    <% 	out.println("\t\t$(\"#col11_filter\").val(\"" + URLDecoder.decode( request.getParameter("Tipo")) + "\")"); %>
    <%}%>

    <% if( request.getParameter("Nivel") != null ){ %>
    <% 	out.println("\t\t$(\"#col12_filter\").val(\"" + request.getParameter("Nivel") + "\")"); %>
    <%}%>
    
    <% if( request.getParameter("FirmaElectronica") != null ){ %>
    <% 	out.println("\t\t$(\"#col13_filter\").val(\"" + request.getParameter("FirmaElectronica") + "\")"); %>
    <%}%>

	/*$("#col3_filter").datepicker({
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});
			
		$("#col6_filter").datepicker({
		showOn : "button",
		dateFormat : "dd/mm/yy",
		buttonImage : "../Generador/images/calendar.gif",
		buttonImageOnly : true
	});*/	
	
	//validaciones para el mensaje de aviso para los usuarios
	$("#aMensajeUsuario").val('<%=u.getLogin()%>');
	//revisa si es un usuario de materiales
	queryFormPost("rolAsignado", {async : false});
	if($("#rolAsignado").val()=='ADMIN_RECMAT'){
	    queryFormPost("mensajeAvisoUsuariosRole", {async : false});
	    validaMensajeMateriales();
		 queryFormPost("mensajeAvisoVigenciaSolicitudes", {async : false});
		if($("#mensajeUsuario").val()!= "0") {
		var mensaje="";
		var res=$("#mensajeUsuario").val();
		var res1=res.split("\,")
		 for(var i=0;i<res1.length;i++){
		 mensaje+=res1[i]+"\n";
		 }
		  alert(mensaje)
		}
		
		//mensaje de precompromisos vencidos
		queryFormPost("mensajeAvisoVigenciaPrecompromisosConsolidados", {async : false});
		if($("#mensajeUsuarioPrecom").val()!= "0") {
		var mensaje="";
		var res=$("#mensajeUsuarioPrecom").val();
		var res1=res.split("\,")
		 for(var i=0;i<res1.length;i++){
		 mensaje+=res1[i]+"\n";
		 }
		  alert(mensaje)
		}
		
		
	
	}

	//fin validaciones
	var filtro = '<%=(request.getParameter("valorFiltro") != null ? request
					.getParameter("valorFiltro") : "")%>';
	
	var oTable = $('#dt_inbox').dataTable({
			"aaSorting": [[<%=(u.getPropiedades() != null
					&& u.getPropiedades()
							.containsKey("INTEGRADOR_ADECUACIONES") && "SI"
					.equals(u.getPropiedad("INTEGRADOR_ADECUACIONES")
							.getValor())) ? 2 : 1%>, "asc"]],
			"bJQueryUI": true,
			//"iDisplayLength": 20,
			"sPaginationType": "full_numbers",
			//"sScrollY": 100,
			//"sScrollY": 432,
			
			"oLanguage": {
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
				"oSearch": {"sSearch": filtro}
		});

    $("#dt_inbox tbody").click(function(event) { 
        $(oTable.fnSettings().aoData).each(function (){ 
            $(this.nTr).removeClass('row_selected'); 
        }); 
        $(event.target.parentNode).addClass('row_selected'); 
    }); 
    
});

function validaMensajeMateriales(){
	var mensajeUsuarios = document.getElementById('cValor').value;
	if(mensajeUsuarios=="mensaje"){
		setTimeout("validaMensajeMateriales()",2000);
		mensajeUsuarios = document.getElementById('cValor').value;
	}
	else if(mensajeUsuarios != ""){
		mensajeUsuarios=mensajeUsuarios.replace("%0A","\n");
		alert(decodeURIComponent(mensajeUsuarios));
	}
}

function filtra(frm, type) {
	var action = frm.action;
	switch (type) {
		case 0:
			break;
		case 1:
		case 2:
		case 3:
			frm.action = action + "&<%=GestionInterface.PRM_PROM_FILTER%>=" + type
	}
	frm.submit();
}

function abrecaso( urlcaso ) {
	document.frmabrecaso.action += urlcaso;
	document.frmabrecaso.submit();	
}

function armaListaCasos(){
	document.frmIntegraAdec.listaCasos.value="";
	$("input[type=checkbox][checked]").each(
	      function() {
	      		document.frmIntegraAdec.listaCasos.value+=this.value+",";
	      }
	);
	if(document.frmIntegraAdec.listaCasos.value==""||document.frmIntegraAdec.listaCasos.value==",")
		alert("Debe marcar al menos una fila");
	else
		document.frmIntegraAdec.submit();
}

function toggleReactivar(status) {
	$("input:checkbox").each(
		function() {
			$(this).attr("checked",status.checked);
		}
	);
}

function filtroSubmit(){
	var filterValue = $("#dt_inbox_filter input").val();
	$("#valorFiltro").val(filterValue);
	frmInbx.submit();
}

			function fnFilterColumn (evt)
			{
				var value, myWhere, token;
				var column_name = [
						"SAI",
						"Folio SICOP",
						"Fecha",
						"Tramite",
						"Operacion",
						"Aplicacion", 
						"Importe",
						"SICOP",
						"MAP",
						"Operador",
						"Tipo",
						"Nivel",
						""
				];
				myWhere = "";
				token = "&qw=";

				var charCode = evt.which ? evt.which : window.event.keyCode;
				if ((charCode != 8) && (charCode != 46)) { // No es backspace (8) y delete (46)?
					if (charCode <= 13) return true;

					if (charCode < 96 || charCode > 106) { // Es teclado numerico?
						var keyChar = String.fromCharCode(charCode);
						var re = /[a-zA-Z0-9.]/;
						if (!re.test(keyChar)) return true;
					}
				}

				for (var i = 1; i < 11; i++) {
					value = $("#col"+i+"_filter" ).val();
					if (value !== "") {
						myWhere += token + column_name[i - 1] + " LIKE '%25" + value + "%25'";
						token = " AND ";
					}
				}
				oTable = createDataTable(myWhere);
				return true;
			}


		function windowStatus( texto )
				{
					window.status=texto
				}


				function createDataTable(w) {

		return $("#dt_inbox").dataTable({
				"bPaginate": true,
				"bDestroy": true,
				fnDrawCallback: function() {
					$(oCurrentFocus).focus(function() {
						if (this.createTextRange) {
							var r = this.createTextRange();
							r.collapse(false);
							r.select();
						}
						this.focus();
					});
					$(oCurrentFocus).focus();
				},
				bAutoWidth : false,
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
       			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/gstnmngr/CargaInbox?fSAI="
       			+ $("#col1_filter").val() 
       			+ "&FolioSICOP="+($("#col2_filter").length > 0 ? $("#col2_filter").val():"")
       			+ "&PolizaDocumento="+($("#col2_2filter").length > 0 ? $("#col2_2filter").val():"")
       			+ "&Fecha="+$("#col3_filter").val()
       			+ "&Tramite="+$("#col4_filter").val()
       			+ "&Operacion="+$("#col5_filter").val()
       			+ "&Aplicacion="+$("#col6_filter").val()
       			+ "&Importe="+$("#col7_filter").val()
       			+ "&SICOP="+$("#col8_filter").val()
       			+ "&MAP="+$("#col9_filter").val()
       			+ "&Operador="+$("#col10_filter").val()
       			+ "&Tipo="+($("#col11_filter").length > 0 ? $("#col11_filter").val():"")
       			+ "&Nivel="+($("#col12_filter").length > 0 ? $("#col12_filter").val():"")
       			+ "&FirmaElectronica="+($("#col13_filter").length > 0 ? $("#col13_filter").val():""),
       			//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCatalogoEPGrid" + w,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				//aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "fSAI"   },
					{ sName: "FolioSICOP" },
					//{ sName: "PolizaDocumento"},
					{ sName: "Fecha"	},
					{ sName: "Tramite" },
					{ sName: "Operacion"   },
					{ sName: "Aplicacion" },
					{ sName: "Importe"	},
					{ sName: "SICOP" },
					{ sName: "MAP"   },
					{ sName: "Operador" },
					
			<%
				if (u.getPropiedades() != null
						&& u.getPropiedades()
								.containsKey("INTEGRADOR_ADECUACIONES")) {
					if ("SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES")
							.getValor())) {
			%>
					{ sName: "Tipo" },
					{ sName: "Nivel" },
			<%
				}
				
				}
			%>              
 				{ sName: "Seleccion"}
				]
        	});
		}
</script>

<style>
div.tableContainer {
	height: 100%;
}
</style>

</head>
<body  <%=(!"".equals(mensajeAvisos) && alertMensajeAvisos ? "onload=\"alert('"	+ mensajeAvisos + "');\"": "")%>>
<%
	mensajeVersion+= " EJERCICIO: " + ejercicio;
	if (!"".equals(mensajeAvisos) || !"".equals(mensajeVersion)) {
%>

<span style="color: red; float:left; text-align:left; margin-top:10px;"><%="<B>"+mensajeVersion + "</B><br>" + mensajeAvisos%></span>
<%
	}
%>

<span style="color: blue; float:left; text-align:center; margin-top:10px;"><%="&nbsp &nbsp &nbsp &nbsp<B> UNIDAD EJECUTORA:  " + UR + "</B><br>"%></span>
                  
<table width="100%" cellpadding="0" cellspacing="0"  border="0">
  <tr>
    <td height="1px" align="right" bordercolor="#FF0000">
    	<table cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<form id="frmUpd"
						action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_INIT_CASE%>"
						method="post">
						<table width="100%" cellpadding="0" cellspacing="0">
							<tr>
								<td nowrap="nowrap">
									<span style="color: red;"><%=(msg != null ? msg : "&nbsp;")%></span>
									<select name="id_tc">
										<option value="-1" selected="selected">
											&lt;Seleccione un tipo de Tr&aacute;mite&gt;
										</option>
										<%
											Map m = ct.getAllTipoCaso(u.getLogin());
											for (Iterator iter = m.keySet().iterator(); iter.hasNext();) {
												String name = (String) iter.next();
												TipoCaso tc = (TipoCaso) m.get(name);
										%>
										<option value="<%=tc.getIdTC()%>"><%=name%></option>
										<%
											}
										%>
									</select>
								</td>
								<td>
									&nbsp;<input type="submit" class="btnInterfaceBG" id="pb_crear" name="pb_crear" value="Iniciar Tr&aacute;mite">
								</td>

							</tr>
						</table>
					</form>
				</td>
				<td>
					<form id="frmInbx"
						action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_OPEN_INBOX%>"
						method="post">
						&nbsp;
						<input type="button" class="btnInterfaceBG" id="pb_actualizar" name="pb_actualizar" value="Actualizar" onclick="filtroSubmit()">
						<input type="hidden" id="valorFiltro" name="valorFiltro" value="" />
						<input type="hidden" id="aMensajeUsuario" name="aMensajeUsuario" />
						<input type="hidden" id="cValor" name="cValor" value="mensaje" />
						<input type="hidden" id="mensajeUsuario" name="mensajeUsuario" />
						<input type="hidden" id="mensajeUsuarioPrecom" name="mensajeUsuarioPrecom" />
						<input type="hidden" id="rolAsignado" name="rolAsignado" />
						
						
						
						
					</form>
				</td>



			</tr>
		</table>
  	</td>
  </tr>
  <!--Ethiel, se ocultan botones de filtrado de tiempos
  <tr>
    <td height="1px" ><table width="100%">
        <tr>
          <td width="85%" nowrap align="left"><table>
              <tr>
                <td class="normal"><a id="todas" href="javascript:filtra(document.getElementById('frmInbx'),0);" onmouseover="window.status='Despliega todas las tareas';return true;" onmouseout="window.status='';return true;"> <strong>&nbsp;</strong></a> </td>
              </tr>
            </table></td>
          <td width="05%" nowrap class="green"><a id="inTime" href="javascript:filtra(document.getElementById('frmInbx'),1);" onmouseover="window.status='Despliega tareas en tiempo';return true;" onmouseout="window.status='';return true;">En tiempo</a></td>
          <td width="05%" nowrap class="yellow"><a id="xVencer" href="javascript:filtra(document.getElementById('frmInbx'),2);" onmouseover="window.status='Despliega tareas por vencer';return true;" onmouseout="window.status='';return true;">Por vencer</a></td>
          <td width="05%" nowrap class="red"><a id="vencida" href="javascript:filtra(document.getElementById('frmInbx'),3);" onmouseover="window.status='Despliega tareas vencidas';return true;" onmouseout="window.status='';return true;">Vencidas</a></td>
        </tr>
      </table></td>
  </tr>
   -->
  <tr>
    <td width="100%">
<!--    	<div id="tableContainer" class="tableContainer">-->
        <table id="dt_inbox" class="display">
          <thead>
            <tr>

              <th>SAI</th>
              <% if(u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES") && "SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())){%>
              <th>Folio Sicop</th>
              <% }else{ %>
              	<th>Poliza/Documento/RFC</th>
              	<%} %>
              <th>Fecha</th>
              <th>Tr&aacute;mite</th>
              <th>Operaci&oacute;n</th>
              <th>Aplicaci&oacute;n Contable/Nombre</th>
              <th>Importe</th>
              <th>SICOP</th>
              <th>MAP</th>
              <th>Operador</th>
			<%
				if (u.getPropiedades() != null
						&& u.getPropiedades()
								.containsKey("INTEGRADOR_ADECUACIONES")) {
					if ("SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES")
							.getValor())) {
			%>
              <th>Tipo</th>
              <th>Nivel</th>
			<%
					}
				}
			%>   
			  <th>Firma Electronica</th>
			           
              <!-- th>Mensaje</th-->
              <th>&nbsp;</th>
            </tr>
          </thead>
          <tbody>
            <%
            		int row = 0;
            		
            		for (Iterator<?> iter = v.iterator(); iter.hasNext(); row++) {
            			Caso c = (Caso)iter.next();
            			CasoOperacion co = c.getCasoOperacion( 0 );
            			int prom = co.getTiempoPromedio();

            %>
			            <!-- tr class="<%=((row % 2) == 0 ? Util.getColorName(prom)
							+ "AlternateRow" : Util.getColorName(prom)
							+ "NormalRow")%>" -->
			           
			            <tr ondblclick="javascript:abrecaso('<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_EXEC_CASE%>&<%=GestionInterface.PRM_CASE%>=<%=co.getIdCaso()%>&<%=GestionInterface.PRM_CASE_OPER%>=<%=co.getIdCasoOper()%>');" >
			              	<td>
			              		<%= StringUtils.isBlank( c.getFolio() ) ? "&nbsp;" : c.getFolio() %> 
			              	</td>
							<td>
								<%= StringUtils.isBlank( co.getDocumento() ) ? "&nbsp;" : co.getDocumento()%>
							</td>		              	
			              	<td>
			              		<%= c.getFechaInicio() == null ? "&nbsp;" : Util.dateToString( c.getFechaInicio(), "dd/MM/yyyy" ) %>
			              	<td>
			              		<%=c.getTipoCaso().getDescripcion()%>
			              	</td>
			              	<td>
			              		<%=co.getOperacion(  ).getDescripcion(  )%>
			              	</td>
							<td>
								<%= StringUtils.isBlank(  co.getfechaAppCont() ) ? "&nbsp;" : co.getfechaAppCont() %>
							</td>
			              	<td>
			              		<%=(co.getImporte() != null? co.getImporte():"&nbsp;" )%>
			              	</td>
			              	<td>
			              		<%=StringUtils.trimToEmpty(co.getFolioSicop())%>
			              	</td>
			              	<td>
			              		<%=StringUtils.trimToEmpty(co.getFolioMap())%>
			              	</td>
			              	<td>
			              		<%= StringUtils.isBlank( co.getOperador() ) ?  "&nbsp;" : co.getOperador()%>
			              	</td>
							<%
								if (u.getPropiedades() != null
												&& u.getPropiedades().containsKey(
														"INTEGRADOR_ADECUACIONES")) {
											if ("SI".equals(u.getPropiedad(
													"INTEGRADOR_ADECUACIONES").getValor())) {
							%>
							<td><%=(co.getTipoAdecuacion() != null ? co.getTipoAdecuacion() : "")%></td>
							<td><%=(co.getNivelAdecuacion() != null ? co.getNivelAdecuacion() : "")%></td>
							<%
								}
							}
							%>
							<td><%=(co.getFirmaElectrionica())%></td>   			              	
			              	<td align="center" valign="middle">
			              		<form action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_EXEC_CASE%>&<%=GestionInterface.PRM_CASE%>=<%=co.getIdCaso()%>&<%=GestionInterface.PRM_CASE_OPER%>=<%=co.getIdCasoOper()%>" method="post">
			                  		<input name="pb_ejecutar" type="image" id="pb_ejecutar" src="../images/run.gif" alt="Ejecutar operaci&oacute;n">
			                	</form>
			                </td>
			            </tr>
	            <%
	            	}
	            %>
          </tbody>
		  <tfoot>
					<tr>
						<th><input type="text" name="col1_filter" id="col1_filter" /></th>
 		            <% if(u.getPropiedades() != null && u.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES") && "SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())){%>
						<th><input type="text" name="col2_filter" id="col2_filter" /></th> 
		            <% }else{ %>
						<th><input type="text" name="col2_2filter" id="col2_2filter" /></th>
             		<%} %>
						<th><input type="text" name="col3_filter" id="col3_filter" /></th>
						<th><input type="text" name="col4_filter" id="col4_filter" /></th>
						<th><input type="text" name="col5_filter" id="col5_filter" /></th>
						<th><input type="text" name="col6_filter" id="col6_filter" /></th>
						<th><input type="text" name="col7_filter" id="col7_filter" /></th>
						<th><input type="text" name="col8_filter" id="col8_filter" /></th>
						<th><input type="text" name="col9_filter" id="col9_filter" /></th>
						<th><input type="text" name="col10_filter" id="col10_filter" /></th>
			<%
				if (u.getPropiedades() != null
						&& u.getPropiedades()
								.containsKey("INTEGRADOR_ADECUACIONES")) {
					if ("SI".equals(u.getPropiedad("INTEGRADOR_ADECUACIONES")
							.getValor())) {
			%>
						<th><input type="text" name="col11_filter" id="col11_filter"/></th>
						<th><input type="text" name="col12_filter" id="col12_filter"/></th> 
			<%
				}
					}
			%>          
						<th><input type="text" name="col13_filter" id="col13_filter"/></th>    
						<th align="center" > <a href="#" onclick="filtraInbox()"> <img src="../imagenes/buscar.gif" alt="Filtrar Inbox..." width="16" height="16"> </a></th>
					</tr>
 		 </tfoot>
          
        </table>
        <form name="frmabrecaso" action="../gstnmngr/gestion?" method="post">
        <!-- no se necesitan campos porque se usa para abrir el caso -->
        </form>
<!--      </div>-->
    </td>
  </tr>
</table>
<!-- Ethiel, ya no se ocupa porque ocultamos los filtros de tiempos
<script type="text/javascript">
	var todas = document.getElementById("todas");
	var inTime = document.getElementById("inTime");
	var xVencer = document.getElementById("xVencer");
	var vencida = document.getElementById("vencida");
	todas.innerText = <!%=totales[3]--%> + " Tarea<!%=(totales[3] != 1 ? "s" : "")%>"+" Pendiente<!%=(totales[3] != 1 ? "s" : "")%>";
	inTime.innerText = <!%=totales[0]%> + " " + inTime.innerText;
	xVencer.innerText = <!%=totales[1]%> + " " + xVencer.innerText;
	vencida.innerText = <!%=totales[2]%> + " " + vencida.innerText;
</script>
 -->
</body>
</html>
