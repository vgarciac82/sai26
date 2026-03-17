<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	if (session.getAttribute(GestionInterface.ATT_USER) == null)
		response.sendRedirect("../index.jsp");

	String mensajeRetorno = (String) session
	.getAttribute("MENSAJE_CARGA");
	if (mensajeRetorno != null) {
		session.removeAttribute("MENSAJE_CARGA");
	} else
		mensajeRetorno = "";
		
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic("jdbc/gestion");
	String efiscalActual = adbl.obtenEjercicioFiscal();

	List<List<String>> resultado = (List<List<String>>)session.getAttribute("DIFERENCIAS");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cat&aacute;logo EP Anteproyecto Plurianuales</title>

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
	var mensaje = '<%=mensajeRetorno%>';
	var efiscalActual = '<%=efiscalActual%>';
	$(document)
			.ready(
					function() {
						$("#EP").change(function() {
							//validaCampos();
						});

						$("#Guardar")
								.button()
								.click(
										function() {
											r = validaCampos();
											if (r.valueOf("true")) {
												$("#existe").val("");
												$("#epValidar").val("");
												
												//Sustituye el E.F. en la EP por el actual
													var arrEP = $("#EP").val().split(".");
													arrEP[0] = parseInt( arrEP[0],10 ) - 1;
													   $("#epValidar").val(arrEP.join("."),10 );
												
												queryFormPost({
													queryName : "readExisteEPPlurianual",
													async : false,
													callback : function() {
														if ("" == $("#existe").val())
															alert("La clave " + $("#epValidar").val() + " no existe en el ejercicio actual. Verifique.");
														else
															queryFormPost({
																queryName : "tInsertCatalogoEPAnteproyectoPlurianuales",
																async : false,
																callback : function() {
																	alert("Registro Insertado exitosamente.");
																}
															});

													}
												});
											}
											datatable.fnDraw();
										});

						$("#Limpiar")
								.button()
								.click(
										function() {
											window.location.href = "CatalogoEPAnteproyectoPlurianuales.jsp";
										});

						$("#CargaArchivoButton")
								.button()
								.click(
										function() {
											if ($("#CargaArchivo").val() == "")
												alert("Debe seleccionar un archivo a cargar.");
											else {
												$
														.blockUI({
															message : "Cargando Archivo. Por favor espere ......"
														});
												$("#CargaMasiva").submit();
											}
										});

						if (mensaje != "") {
							$("#resultadoTR").show();
							$("#mensajeTA").val(mensaje);
						} else
							$("#resultadoTR").hide();

						datatable = $('#dt_listado')
								.dataTable(
										{
											"bPaginate" : true,
											"bLengthChange" : true,
											"bFilter" : true,
											"bSort" : true,
											"bInfo" : true,
											"bAutoWidth" : true,
											"sScrollY" : 270,
											"sScrollYInner" : "100%",
											"bJQueryUI" : true,
											"bRetrive" : true,
											"bDestroy" : true,
											"sPaginationType" : "full_numbers",
											//"sScrollX" : "1250",
											"bScrollCollapse" : true,
											"bServerSide" : true,
											sAjaxSource : window.location.protocol
													+ "//"
													+ window.location.host
													+ "/"
													+ window.location.pathname
															.split("/")[1]
													+ "/crud?rt=t&ql=vtvalidacion_anteproyecto_plurianualesdt",
											aoColumns : [ {
												sName : "EP"
											}, {
												sName : "Monto"
											} ],
											oLanguage : {
												sProcessing : "Procesando...",
												sLengthMenu : "Mostrar _MENU_ registros",
												sZeroRecords : "No hay registros a mostrar",
												sEmptyTable : "No hay datos en la tabla",
												sLoadingRecords : "Cargando...",
												sInfo : "Registros _START_ al _END_ de _TOTAL_",
												sInfoEmpty : "Registro 0 al 0 de 0",
												sInfoFiltered : "(filtered from _MAX_ total entries)",
												sInfoPostFix : "",
												sInfoThousands : ",",
												sSearch : "Filtro:",
												oPaginate : {
													sFirst : "Primero",
													sPrevious : "Ant.",
													sNext : "Sigte.",
													sLast : "&Uacute;ltimo"
												}
											}
										});

					});

	function validaCampos() {
		if ($("#Monto").val() > 0) {
			if ($("#EP").val() != '') {
				var elementos = $("#EP").val().split(".");
				var efiscalAnteProyecto = parseInt(efiscalActual,10) + 1;
				var efiscal = elementos[0];
				if (efiscal == efiscalAnteProyecto) {
					var EP = $("#EP").val();
					var Monto = $("#Monto").val();

					if ((elementos.length != 16) || (EP == "" || Monto == "")) {
						alert("No se capturaron correctamente los elementos de la clave EP o el monto");
						return false;
					}
					$("#EP").val(EP);
					$("#Monto").val(Monto);
					return true;
				} else
					alert("El ejercicio fiscal de la ep no es correcto, debe ser: "
							+ efiscalAnteProyecto);
				return false;
			} else
				alert("Los campos EP y Monto son requeridos");
			return false;
		} else
			alert("El Monto debe ser mayor a 0 (cero)");
		return false;
	}

</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<div id="container" class="container">
		<h1>Cat&aacute;logo EP Anteproyecto Plurianuales</h1>
		<form id="CapturaManual" name="CapturaManual" method="post">
			<fieldset>
				<table width="100%">
					<tr>
					</tr>
					<tr>
						<td>
							<fieldset>
								<legend>Captura Manual</legend>
								<table width="100%">
									<tr>
										<td align="left" colspan="2">EP: <input type="text"
											name="EP" id="EP" class="" size="64" maxlength="64">
										</td>
										<td align="left" colspan="2">Monto: <input type="text"
											name="Monto" id="Monto" class="" size="14"></td>

									</tr>
									<tr>
									</tr>
								</table>
								<table align="right">
									<tr>
										<td align="center"><input type="button" value="Guardar"
											id="Guardar">
										</td>
										<td align="center"><input type="button" value="Limpiar"
											id="Limpiar">
										</td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr id="resultadoTR">
						<td>
							<fieldset>
								<legend>Resultado:</legend>
								<table width="100%">
									<tr>
										<td align="center"><textarea rows="10" cols="80"
												readonly="readonly" id="mensajeTA"></textarea></td>
									</tr>
								</table>
							</fieldset></td>
					</tr>
				</table>
			</fieldset>
		</form>
		<form
			action="../gstnmngr/CatalogoEPAnteproyectoPlurianuales/CargaMasiva"
			id="CargaMasiva" name="CargaMasiva" method="post"
			enctype="multipart/form-data">
			<input type="hidden" value="CargaMasiva" name="accion" id="accion" />
			<input type="hidden" value="" name="existe" id="existe" />
			<input type="hidden" value="" name="epValidar" id="epValidar" />
			<fieldset>
				<legend>Carga Masiva</legend>
				<table align="center">
					<tr>

						<td align="right">Archivo Excel:</td>
						<td align="left"><input type="file" size="30"
							name="CargaArchivo" id="CargaArchivo">
						</td>
						<td align="center" colspan="2"><input type="button"
							value="Cargar Catalogo" id="CargaArchivoButton">
						</td>
					</tr>
				</table>
			</fieldset>
		</form>
		<div id="dv">
			<table id="dt_listado" class="display" cellspacing="0"
				cellpadding="2" align="center">
				<thead>
					<tr>
						<th>EP</th>
						<th>Monto</th>
				</thead>
				<tbody>

				</tbody>
			</table>
		</div>
	</div>

</body>
</html>