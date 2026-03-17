<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.contable.AdministracionAccesoBusinessLogic"%>
<%@page import="com.syc.contable.PresupuestoBusinessLogic"%>
<%@page import="com.syc.contable.core.AplicacionContable"%>
<%@page import="com.syc.contable.AccountingEngine"%>
<%@page import="java.text.DateFormat"%>

<%
	ArrayList<String> arrLResult = new ArrayList<String>();
	String mensaje=request.getParameter("mensaje")!= null ? request.getParameter("mensaje"):"";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/");
	
	
	String msj="";
	msj = (String)session.getAttribute("mensaje");
	
	if (msj != null)
		session.removeAttribute("mensaje");
	else 
		msj = "";
%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>administracionAccesos</title>

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

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>
<script type="text/javascript">
$(document).ready(
	function(){
		init();

	}
);
function opcionesDelegadas(){
		var listadoUsuario = $("#listadoUsuario").val();
		if (listadoUsuario != "") {
			querySelectPost("readCargaOpcionDelegada", {
				async : false
			});
		}
	}


	function selecChecks() {
		$
				.ajax({
					url : '../gstnmngr/AdministracionAcceso',
					dataType : 'json',
					data : {
						"seleccionarOpcion" : "seleccionarOpcion",
						"listadoUsuario" : $("#listadoUsuario").val(),
						"accion":"LISTA_OPCIONES"
					},
					async : false,
					success : function(json) {
						r = json.data_1;

						$("input[type=checkbox]").each(function(){
							$(this).removeAttr("checked");
						});
						
						for ( var c = 0; c < r.length; c++) {
							$("#" + r[c].data ).attr('checked', true);

						}

					},
					error : function(xhr, textStatus, errorThrown) {
						alert("Advertencia: " + xhr.responseText
								+ "\nEstatus: " + textStatus + "\n"
								+ errorThrown);
						r = true;
					}
				});

			}


		$(document).ready(
			function()
			{
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();
				
				cargaOpcionDelegable();
		});

	
	   function limpiarSesion()
		{
			window.location.href="administracionAccesos.jsp?id=<%=request.getParameter("id")%>";
	}

	function init() {
		$("#asigna").button().click(function() {
			$("#dialog-pregunta").dialog("open");
		});

		$("#cancelar").button().click(function() {
		});

		$("#dialog-pregunta").dialog({
			autoOpen : false, // se juega con el true o false para que se muestre o no
			height : 180,
			width : 400,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$("#adminAccesos").submit();

				},
				"Cancelar" : function() {
					$(this).dialog("close");
				}
			}
		});

		$("#dialog-mensaje").dialog({
			autoOpen : false, // se juega con el true o false para que se muestre o no
			height : 250,
			width : 400,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});
		
		<%if(msj != null && !"".equals(msj)){%>
			$("#dialog-mensaje").dialog("open");
		<%}%>
	}
	
	function cargaOpcionDelegable() {
		$
				.ajax({
					url : '../gstnmngr/AdministracionAcceso',
					dataType : 'json',
					data : {
						"accion" : "listarOpcion"
					},
					async : false,
					success : function(json) {
						r = json.data_1;

						var trText = "";
						var tdText = "";
						
						var i = 0;
						var j = 0;
						while (i < r.length){
							while( j  < 5 ){
								if( r[i] ){
									var arr = r[i].data.split("|");
									tdText = tdText +"<td><input type=\"checkbox\" id=\"" +  arr[0] + "\" name=\"checks\" value=\"" +  arr[0] + "\"  >" + arr[1] + " </td>";
									}
								else
									tdText = tdText + "<td>&nbsp;</td>";
								j++;
								i++;
							}
							$('#opdelegar > tbody:first').append("<tr>"+ tdText + "</tr>");
							j=0;
							tdText ="";
						}	
					},
					error : function(xhr, textStatus, errorThrown) {
						alert("Advertencia: " + xhr.responseText
								+ "\nEstatus: " + textStatus + "\n"
								+ errorThrown);
						r = true;
					}
				});

	}
</script>

</head>
<body id="dt_example">
	<form id="adminAccesos" name="adminAccesos" method="post"
		action="../gstnmngr/AdministracionAcceso">
		<input type="hidden" value="INSERTA_ACTUALIZA" name="accion"
			id="accion" />
		<div id="container" class="container">
			<h1 align="left">Administraci&oacute;n de Accesos</h1>
			<table id="usuario" width="90%" align="center">
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr>
					<td align="right">Usuario:</td>
					<td><input id="listadoUsuario" name="listadoUsuario"
						type="text" value="" size="64" maxlength="64"
						class="AyudaSyC  autoCompletaSyC" onblur="selecChecks()"
						onchange="selecChecks()" /></td>
				</tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
				<tr></tr>
			</table>

			<fieldset>
				<legend align="right">Opciones a Delegar </legend>
				<table id="opdelegar">
					<thead>
						<tr>
							<th>&nbsp;</th>
						</tr>
					</thead>
				</table>
			</fieldset>
			<table align="right">
				<tr>
					<td colspan="2" align="right"><input type="button"
						value="Aceptar" name="asigna" id="asigna" /> <input type="button"
						id="cancelar" name="cancelar" value="Cancelar"
						onclick="limpiarSesion();" /></td>
				</tr>
			</table>
		</div>
	</form>
	<div id="dialog-pregunta" title="Pregunta:">Est&aacute; usted
		seguro de realizar los cambios? </div>
	<div id="dialog-mensaje" title="Mensaje de Sistema">
		<table align="center">
			<tr> 
				<td>
					<textarea cols="60" rows="10" id="mensaje"><%=msj%></textarea>
				</td>
			</tr>
		
		</table>
		
	</div>


</body>
</html>