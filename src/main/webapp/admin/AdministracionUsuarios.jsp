<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Calendar"%>


<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String UR = usuario.getU_UR();

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	String aEjercicioFiscal = adecProy.obtenEjercicioFiscal();

	String U_Login = usuario.getLogin();

	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today

	String today = sdf.format(c1.getTime());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Administración de Usuarios</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

<style type="text/css" title="currentStyle">
#feedback {
	font-size: 1.4em;
}

#selectable .ui-selecting {
	background: #FECA40;
}

#selectable .ui-selected {
	background: #F39814;
	color: white;
}

#selectable {
	list-style-type: none;
	margin: 0;
	padding: 0;
	width: 60%;
}

#selectable li {
	margin: 3px;
	padding: 0.4em;
	font-size: 1.4em;
	height: 18px;
}

.notEditable {
	background-color: #CCCCCC;
}

.monto {
	text-align: right;
	border: 1px solid #aaaaaa;
	color: #222222;
}

</style>




<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../js/catalogo/pagoAnticipado.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
		
	var uLogin = "";
	var aPaterno = "";
	var aMaterno = "";
	var nCompleto = "";
	var nPueto = "";
	var nArea = "";
	var nCobertura = "";
	var nPerfil = "";
	var nEstatus = "";
	var uEmail = "";
	var saludation = "";
	var aEjercicio = "";
	
	$(document).ready(function() {
		$("#buscar").button();
		$("#agregar").button();
		$("#borrar").button();
		$("#editar").button();
		$("#limpiar").button();
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		
		//botones
		$("#borrar").attr('disabled',true);	
		$("#editar").attr('disabled',true);			
		
		$("#limpiar").button().click(function() {
		});
		
		
		$("#buscar").button().click(function() {
				($("#U_LOGIN").val(), //RECUPERACIÓN DE LOS CAMPOS
				$("#CE_AP_PATERNO").val(),
				$("#CE_AP_MATERNO").val(), 
				$("#CE_NOMBRE_COMPLETO").val(),
				$("#txtGridMantoPuesto").val(),
				$("#txtGridMantoArea").val(),
				$("#txtGridMantoCobertura").val(), 
				$("#txtGridMantoPerfil").val(),
				$("#txtGridMantoEstatus").val(),
				$("#U_EMAIL").val(),
				$("#SALUTACION").val(), 
				$("#txtGridaEjercicioFiscal").val()
				);
				
				 
				var token = "";   //armar url
				var cond = "";
		
				if ($("#U_LOGIN").val() != "" || $("#CE_AP_PATERNO").val() != "" || $("#CE_AP_MATERNO").val() != "" || $("#CE_NOMBRE_COMPLETO").val() != "" 
				|| $("#txtGridMantoPuesto").val() != "" || $("#txtGridMantoArea").val() != "" || $("#txtGridMantoCobertura").val() != "" || $("#txtGridMantoPerfil").val() != ""
				|| $("#txtGridMantoEstatus").val() != "" || $("#U_EMAIL").val() != "" || $("#SALUTACION").val() != "" ||  $("#txtGridaEjercicioFiscal").val() != ""){
					if( $("#U_LOGIN").val() != "" ){
						cond = token + cond + " u_login = '" + $("#U_LOGIN").val() + "'";
						token = " AND ";
					}if( $("#CE_AP_PATERNO").val() != "" ){
						cond += token  + " CE_AP_PATERNO = '" + $("#CE_AP_PATERNO").val() + "'";
						token = " AND ";
					}if( $("#CE_AP_MATERNO").val() != "" ){
						cond += token + " CE_AP_MATERNO = '" + $("#CE_AP_MATERNO").val() + "'";
						token = " AND ";
					}if( $("#CE_NOMBRE_COMPLETO").val() != "" ){
						cond += token + " CE_NOMBRE_COMPLETO = '" + $("#CE_NOMBRE_COMPLETO").val() + "'";
						token = " AND ";
					}if( $("#txtGridMantoPuesto").val() != "" ){
						cond += token + " CARGO = '" + $("#txtGridMantoPuesto").val() + "'";
						token = " AND ";
					}if( $("#txtGridMantoArea").val() != "" ){
						cond += token + " d_descripcion = '" + $("#txtGridMantoArea").val() + "'";
						token = " AND ";
					}if( $("#txtGridMantoCobertura").val() != "" ){
						cond += token + " co_descripcion = '" + $("#txtGridMantoCobertura").val() + "'";
						token = " AND ";
					}if( $("#txtGridMantoPerfil").val() != "" ){
						cond += token + " R_NOMBRE = '" + $("#txtGridMantoPerfil").val() + "'";
						token = " AND ";
					}if( $("#txtGridMantoEstatus").val() != "" ){
						cond += token + " U_ESTATUS = '" + $("#txtGridMantoEstatus").val() + "'";
						token = " AND ";
					}if( $("#U_EMAIL").val() != "" ){
						cond += token + " U_EMAIL = '" + $("#U_EMAIL").val() + "'";
						token = " AND ";
					}if( $("#SALUTACION").val() != "" ){
						cond += token + " SALUTACION = '" + $("#SALUTACION").val() + "'";
						token = " AND ";
					}if( $("#txtGridaEjercicioFiscal").val() != "" ){
						cond += token + " EFISCAL = '" + $("#txtGridaEjercicioFiscal").val() + "'";
						token = " AND ";
					}
					}else {
						cond = " 1=1 ";
					}			
				var url = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vimx_general_usuario&qw=" + cond; //nueva ruta con los where
				tb.fnSettings().sAjaxSource = url;
				tb.fnReloadAjax(); // funcion para el recargado de ajax
				
		});
		
		setDblClck();

		//Genera Tabs (Pestañas)
		$(".tabs").tabs();

		// funcion para el recargado de ajax
		$.fn.dataTableExt.oApi.fnReloadAjax = function ( oSettings, sNewSource ) {
			if ( typeof sNewSource != 'undefined' )
			oSettings.sAjaxSource = sNewSource;
		
			this.fnClearTable( this );
			this.oApi._fnProcessingDisplay( oSettings, true );
			var that = this;
				
			$.getJSON( oSettings.sAjaxSource, null, function(json) {
			/* Got the data - add it to the table */
			for ( var i=0 ; i<json.aaData.length ; i++ ) {
			that.oApi._fnAddData( oSettings, json.aaData[i] );
			}
				
			oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
			that.fnDraw( that );
			that.oApi._fnProcessingDisplay( oSettings, false );
			});
		};
		
				//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		tb = $('#dt_general_usuario').dataTable({
			        "bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX" : "1250",
					"bScrollCollapse": true,	
					"bServerSide": true, 
					sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vimx_general_usuario&qw=U_LOGIN='-1'",
			        aoColumns   : [
						{ sName: "U_LOGIN" },
						{ sName: "CE_AP_PATERNO" },
						{ sName: "CE_AP_MATERNO"},
						{ sName: "CE_NOMBRE_COMPLETO"},
						{ sName: "CARGO" },
						{ sName: "d_descripcion" },
						{ sName: "co_descripcion"},
						{ sName: "R_NOMBRE"},
						{ sName: "U_ESTATUS" },
						{ sName: "U_EMAIL" },
						{ sName: "SALUTACION"},
						{ sName: "EFISCAL"}
					], oLanguage: {
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
		


	});
	
	
	function setDblClck() {

		$("#dt_general_usuario tbody").dblclick(function(evt) {

			var aPos = tb.fnGetPosition(evt.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			var arr = tb.fnGetData()[currIndex];
				uLogin = arr[0];
				/*aPaterno = arr[2];
				aMaterno = arr[3];
				nCompleto = arr[4];
				nPueto = arr[5];
				nArea = arr[6];
				nCobertura = arr[7];
				nPerfil = arr[8];
				nEstatus = arr[9];
				uEmail = arr[10];
				saludation = arr[11];
				aEjercicio = arr[12];*/
				
			//cargaGeneralUsuario(uLogin,	aPaterno, aMaterno,	nCompleto, nPuesto, nArea, nCobertura, nPerfil, nEstatus, uEmail, saludation, aEjercicio);
			cargaGeneralUsuario(uLogin);
		});
	}
	
	//function cargaGeneralUsuario(uLogin, aPaterno, aMaterno, nCompleto, nPuesto, nArea, nCobertura, nPerfil, nEstatus, uEmail, saludation, aEjercicio){
	function cargaGeneralUsuario(uLogin){
		$("#uLogin").val(uLogin);
		/*$("#aPaterno").val(aPaterno);
		$("#aMaterno").val(aMaterno);
		$("#nCompleto").val(nCompleto);
		$("#nPuesto").val(nPuesto);
		$("#nArea").val(nArea);
		$("#nCobertura").val(nCobertura);
		$("#txtGridMantoCobertura").val() == $("#nCobertura").val();
		$("#nPerfil").val(nPerfil);
		$("#nEstatus").val(nEstatus);
		$("#uEmail").val(uEmail);
		$("#saludation").val(saludation);
		$("#aEjercicio").val(aEjercicio);*/

		 queryFormPost({
			queryName:"readGeneralUsuario",
			async:false
			});
			
		$("#U_LOGIN").attr('disabled','disabled');
		//botones
		$("#borrar").attr('disabled',false);	
		$("#editar").attr('disabled',false);
		$("#buscar").attr('disabled',true);
		$("#agregar").attr('disabled',true);	
	}
	
	function limpiarSesion(){
			window.location.href="AdministracionUsuarios.jsp";
	}
	

</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormUsuario" name="FormUsuario">
	<input type="hidden" value="" id="uLogin" name="uLogin">
	<input type="hidden" value="" id="aPaterno" name="aPaterno">
	<input type="hidden" value="" id="aMaterno" name="aMaterno">
	<input type="hidden" value="" id="nCompleto" name="nCompleto">
	<input type="hidden" value="" id="nPuesto" name="nPuesto">
	<input type="hidden" value="" id="nArea" name="nArea">
	<input type="hidden" value="" id="nCobertura" name="nCobertura">
	<input type="hidden" value="" id="nPerfil" name="nPerfil">
	<input type="hidden" value="" id="nEstatus" name="nEstatus">
	<input type="hidden" value="" id="uEmail" name="uEmail">
	<input type="hidden" value="" id="saludation" name="saludation">
	<input type="hidden" value="" id="aEjercicio" name="aEjercicio">
		<div id="container" class="container">
		<table align="right" width="100%">
				<tr>
					<td colspan="5" align="right"><input type="button"
						value="Buscar" name="buscar" id="buscar" /><input type="button" 
						value="Agregar" name="agregar" id="agregar" /><input type="button"
						value="Borrar" name="borrar" id="borrar" /><input type="button"
						value="Editar" name="editar" id="editar" /><input type="button" 
						id="limpiar" name="limpiar" value="Limpiar"
						onclick="limpiarSesion();" /></td>
				</tr>
		</table>
			<h1>Administración de Usuarios </h1>
			<div class="tabs">
				<ul>
					<li><a id="Link01" href="#tabs-0">General</a></li>
					<li><a id="Link02" href="#tabs-1">Propiedades</a></li>
					<li><a id="Link03" href="#tabs-2">Grupos</a></li>
					<li><a id="Link04" href="#tabs-3">Roles</a></li>
				</ul>
				<div id="tabs-0">
			<fieldset>
				<table width="100%">
					<tr>
					</tr>
					<tr>
						<td>
				<fieldset>
				<legend>Información general del Usuario</legend>
					<table width="100%" >
						<tr>
							<td align="right">Id Usuario: </td>
							<td align="left"><input type="text"
								name="U_LOGIN" id="U_LOGIN" class="" size="32" maxlength="30">
							</td>
						</tr>
						<tr>	
							<td align="right"> Apellido Paterno: </td>
							<td align="left"><input type="text"
								name="CE_AP_PATERNO" id="CE_AP_PATERNO" size="30" maxlength="40"></td>
						</tr>
						<tr>
							<td align="right"> Apellido Materno: </td>
							<td align="left"><input type="text"
								name="CE_AP_MATERNO" id="CE_AP_MATERNO" size="30" maxlength="40"></td>
						</tr>
						<tr>
							<td align="right"> Nombre(s): </td>
							<td align="left"><input type="text"
								name="CE_NOMBRE_COMPLETO" id="CE_NOMBRE_COMPLETO" size="30" maxlength="40"></td>
						</tr>
						<tr>
							<td align="right"> Cargo: </td>
							<td align="left"><input type="text"
								name="txtGridMantoPuesto" id="txtGridMantoPuesto"
								class="AyudaSyC autoCompletaSyC" size="70" maxlength="200"></td>
						</tr>
						<tr>
							<td align="right"> Area: </td>
							<td align="left"><input type="text"
								name="txtGridMantoArea" id="txtGridMantoArea" class="AyudaSyC autoCompletaSyC" size="70" maxlength="100"></td>
						</tr>
						<tr>
							<td align="right"> Cobertura: </td>
							<td align="left"> <input type="text"
								id="txtGridMantoCobertura" name="txtGridMantoCobertura"  class="AyudaSyC autoCompletaSyC" value="" size="5" maxlength="5"></td>
						</tr>
						<tr>
							<td align="right"> Rol: </td>
							<td align="left"> <input type="text"
								 id="txtGridMantoPerfil" name="txtGridMantoPerfil" class="AyudaSyC autoCompletaSyC" value="" size="70" maxlength="80"></td>
						</tr>
						<tr>
							<td align="right"> Estatus: </td>
							<td align="left"> <input type="text"
								 id="txtGridMantoEstatus" name="txtGridMantoEstatus"  class="AyudaSyC autoCompletaSyC" value="" size="5" maxlength="2"></td>
						</tr>
						<tr>
							<td align="right"> Email: </td>
							<td align="left"> <input type="text"
								 id="U_EMAIL" name="U_EMAIL"  value="" size="70" maxlength="80"></td>
						</tr>						
						<tr>
							<td align="right"> Salutacion: </td>
							<td align="left"> <input type="text"
								 id="SALUTACION" name="SALUTACION"  value="" size="5" maxlength="10"></td>
						</tr>
						<tr>
							<td align="right"> Ejercicio Fiscal: </td>
							<td align="left"> <input type="text"
								 id="txtGridaEjercicioFiscal" name="txtGridaEjercicioFiscal" class="AyudaSyC autoCompletaSyC" value="" size="5" maxlength="10"></td>
						</tr>
					</table>
				</fieldset>
					</td>
				</table>
			</fieldset>
			<table width="100%">
			<tr>
				<th>&nbsp;</th>
				<th>&nbsp;</th>
				<th>&nbsp;</th>
			</tr>
			</table>
			<div id="dv">
				<table width="100%" id="dt_general_usuario" class="display" cellspacing="0"
					cellpadding="2" align="center">
					<thead>
						<tr>
							<th>id Usuario</th>
							<th>Apellido Paterno</th>
							<th>Apellido Materno</th>
							<th>Nombre</th>
							<th>Cargo</th>
							<th>Area</th>
							<th>Cobertura</th>
							<th>Rol</th>
							<th>Estatus</th>
							<th>Email</th>
							<th>Saludation</th>
							<th>Ejercicio Fiscal</th>
						</tr>
					</thead>
				</table>
			</div>

				</div>
				<div id="tabs-1">
					<fieldset>
						<legend>Mantenimiento de relación Usuario Propiedades</legend>
						<div id="dv1">
							<table width="100%">
						<tr>
							<td align="right" > Usuario:  </td>
							<td align="left"> <input id="u_login" name="u_login"  value="" readonly="readonly"
							class="notEditable" size="32" maxlength="30" class="AyudaSyC  autoCompletaSyC" ></td>
						</tr>
						<tr>
							<td align="right" > Nombre Propiedad: </td>
							<td align="left"> <input
								 id="UP_NOMBRE" name="UP_NOMBRE"  value="" size="70" maxlength="255"></td>
						</tr>
						<tr>
							<td align="right" > Valor propiedad:  </td>
							<td align="left"> <input
								 id="UP_VALOR" name="UP_VALOR"  value="" size="70" maxlength="255"></td>
						</tr>
						<tr>
							<td align="right" > Rol Dueño:  </td>
							<td align="left"> <input
								 id="txtGridMantoRolesSup" name="txtGridMantoRolesSup" class="AyudaSyC autoCompletaSyC" value="" size="20" maxlength="32"></td>
						</tr>
						<tr>
							<td align="right" > Ejercicio Fiscal:  </td>
							<td align="left"> <input
								 id="txtGridaEjercicioFiscal" name="txtGridaEjercicioFiscal" class="AyudaSyC autoCompletaSyC" value="" size="5" maxlength="10"></td>
						</tr>
								
							</table>
						</div>
					</fieldset>
				</div>
				<div id="tabs-2">
					<fieldset>
						<legend>Mantenimiento de relación Usuario Grupos</legend>
						<div id="dv2">
					<table width="100%">
						<tr>
							<td align="right" > Usuario:  </td>
							<td align="left"> <input id="u_login" name="u_login"  value="" readonly="readonly"
							class="notEditable" size="32" maxlength="30" class="AyudaSyC  autoCompletaSyC" ></td>
						</tr>
						<tr>
							<td align="right" > Nombre Grupo: </td>
							<td align="left"><input
								 id="txtGridMantoGrupoUsuario" name="txtGridMantoGrupoUsuario" class="AyudaSyC autoCompletaSyC" value="" size="35" maxlength="32"></td>
						</tr>
						<tr>
							<td align="right" > Ejercicio Fiscal: </td>
							<td align="left"> <input
								 id="txtGridaEjercicioFiscal" name="txtGridaEjercicioFiscal" class="AyudaSyC autoCompletaSyC" value="" size="5" maxlength="10"></td>
						</tr>
								
					</table>
						</div>
					</fieldset>
				</div>
				<div id="tabs-3">
					<fieldset>
						<legend>Mantenimiento de relación Usuario Roles </legend>
						<div id="dv3">
							<table width="100%">
						<tr>
							<td align="right" > Usuario: </td>
							<td align="left"> <input
								 id="u_login" name="u_login"  value="" readonly="readonly"
							class="notEditable" ></td>
						</tr>
						<tr>
							<td align="right" > Rol: </td>
							<td align="left"> <input
								 id="txtGridMantoPerfil" name="txtGridMantoPerfil" class="AyudaSyC autoCompletaSyC" value="" size="35" maxlength="32"></td>
						</tr>
						<tr>
							<td align="right" > Ejercicio Fiscal: </td>
							<td align="left"> <input
								 id="txtGridaEjercicioFiscal" name="txtGridaEjercicioFiscal" class="AyudaSyC autoCompletaSyC" value="" size="5" maxlength="10"></td>
						</tr>
								
							</table>
						</div>
					</fieldset>
				</div>

			</div>
		
		</div>

	</form>
</body>
</html>