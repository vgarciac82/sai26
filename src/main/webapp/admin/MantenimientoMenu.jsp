<%@page import="java.util.Vector"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.OpcionBusinessLogic"%>
<%@page import="com.syc.gestion.core.Producto"%>
<%@page import="java.util.List"%>
<%@page import="com.syc.gestion.core.Opcion"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Formatos para la toma de decisión</title>

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

<!-- se agregaron -->
<link rel="stylesheet" href="../js/jquery.treeview/jquery.treeview.css" />
<link rel="stylesheet" href="../js/jquery.treeview/red-treeview.css" />
<link rel="stylesheet" href="../js/jquery.treeview/demo/screen.css" />

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
	src="../Generador/js/jquery.ui.datepicker.js">
	
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
<!-- se agregaron -->
<script src="../js/jquery.treeview/lib/jquery.js" type="text/javascript"></script>
<script src="../js/jquery.treeview/lib/jquery.cookie.js"
	type="text/javascript"></script>
<script src="../js/jquery.treeview/jquery.treeview.js"
	type="text/javascript"></script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>

<script type="text/javascript">
var idOpcion = -1;
	$(document)
			.ready(
					function() {
					
						$("#O_DESCRIPCION").val("");
						$("#O_DESCRIPCION_NUEVA").val("");
						$("#O_DESCRIPCION").css("backgroundColor", "#CCCCCC");

						
						$("#dialog-requerido").dialog({
								autoOpen : false, 
								height : 180,
								width : 400,
								modal : true,
								buttons : {
									"Aceptar" : function() {
										$(this).dialog("close");
										$("#div_renombrar").dialog("open");
									}
								}
							});
						
					$("#dialog-mensaje").dialog({
								autoOpen : false, 
								height : 180,
								width : 400,
								modal : true,
								buttons : {
									"Aceptar" : function() {
										$(this).dialog("close");
										recargaPagina();
									}
								}
							});
	


						$("#div_renombrar").dialog({
								autoOpen : false, 
								height : 180,
								width : 400,
								modal : true,
								buttons : {
									"Aceptar" : function() {
										if ($("#O_DESCRIPCION_NUEVA").val() != ""){
											$("#descripcionNueva").val($("#O_DESCRIPCION_NUEVA").val());
											 queryFormPost({
												queryName:"updateDesOpcion",
												async:false,
									            callback:function(){
															$("#div_renombrar").dialog("open");
														}
												});
											$(this).dialog("close");
											$("#dialog-mensaje").dialog("open");
										}else{
											$("#dialog-requerido").dialog("open");
										}
									}
								}
							});
					

						$("#menup").treeview(
								{
									collapsed : true,
									animated : "medium",
									control : "#sidetreecontrol",
									persist : "location",
									toggle : function() {
										console.log("%s was toggled.", $(this)
												.find(">span").text());
									}
								});
								
						$("#dialog-pregunta").dialog({
								autoOpen : false, 
								height : 180,
								width : 400,
								modal : true,
								buttons : {
									"Aceptar" : function() {
									$(this).dialog("close");
									$("#O_DESCRIPCION").val("");
									$("#idOpcion").val(idOpcion);
									 queryFormPost({
										queryName:"readDesOpcion",
										async:false,
							            callback:function(){
													$("#div_renombrar").dialog("open");
												}
										});
									},
									"Cancelar" : function() {
										$(this).dialog("close");
									}
								}
							});
							
								
					});
					
					
	function  cambiaNombre(id_Opcion){
				$("#dialog-pregunta").dialog("open");
				idOpcion = 	id_Opcion;
		}
		
		
	function recargaPagina()
		{
			window.location.href="MantenimientoMenu.jsp";
		}	
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato">
		<div id="div_renombrar" title="Administración de etiquetas del menú">
			<table align="center">
				<tr>
					<td align="right"><font class="LabelSalida">Nombre
							Actual</font></td>
					<td><input type="text" align="left" id="O_DESCRIPCION"
						name="O_DESCRIPCION" value="" readonly="readonly" /></td>
				</tr>
				<tr>
					<td align="right"><font class="LabelSalida">Nuevo
							Nombre</font></td>
					<td><input type="text" align="left" id="O_DESCRIPCION_NUEVA"
						name="O_DESCRIPCION_NUEVA" value="" /></td>
				</tr>

			</table>
		</div>
		<input type="hidden" value="" id="idOpcion" name="idOpcion">
		<input type="hidden" value="" id="descripcionNueva" name="descripcionNueva">
		<div id="container" class="container">
			<h1>Mantenimiento de Men&uacute;</h1>
			<label style="font-size: 11px; font-weight:bold; font-style: italic; text-align: left;">
										*Los cambios en el men&uacute; principal se ver&aacute;n reflejados al reiniciar sesi&oacute;n.
									</label>
			<div id="sidetreecontrol" align="right">
				<a href="?#">Contraer</a> | <a href="?#">Expandir</a>
			</div>
			<div id="menup" class="filetree treeview-famfamfam">
				<ul>
					<%
						Usuario u = (Usuario) session
								.getAttribute(GestionInterface.ATT_USER);
						OpcionBusinessLogic ol = new OpcionBusinessLogic(
								GestionInterface.ATT_CONEXION);
						Vector<?> entries = ol.getOpcionByUser(u.getLogin(),
								Opcion.OPC_EN_MENU, Producto.PRD_GESTION);

						Enumeration<?> ent = entries.elements();
						Opcion o;

						String str = "";
						String padreAnterior = "";
						String padreActual = "";
						String hijoActual = "";
						String hijoAnterior = "";
						boolean cerrar = false;
						while (ent.hasMoreElements()) {
							o = (Opcion) ent.nextElement();

							padreActual = String.valueOf(o.getO_orden()).substring(0, 3);
							hijoActual = String.valueOf(o.getO_orden()).substring(3, 6);

							//pinta solo etiquetas principales
							if (!padreActual.equals(padreAnterior)) {
								padreAnterior = padreActual;
								if (cerrar) {
					%>
				</ul>
				</li>
				<%
					cerrar = false;
							}
				%>
				<li><span class="folder"><a href="#"
						onclick="return false" id=<%=o.getId_opcion()%>><%=o.getO_descripcion()%></a>
				</span>
					<ul>
						<%
							cerrar = true;
									continue;
								}
						%>
						<li><span class="file"><a href="?#"
								onclick="cambiaNombre(<%=o.getId_opcion()%>);return false"
								id=<%=o.getId_opcion()%>><%=o.getO_descripcion()%></a> </span></li>
						<%
							}

							if (cerrar) {
						%>
					</ul>
				</li>
				<%
					}
				%>
				</ul>
			</div>
		</div>
		<div id="dialog-pregunta" title="Pregunta:">Está usted seguro de
			realizar los cambios?</div>

		<div id="dialog-requerido" title="Campo requerido">No fue
			capturado el nuevo nombre</div>

		<div id="dialog-mensaje" title="Mensaje">Operación realizada exitosamente</div>

	</form>

</body>
</html>