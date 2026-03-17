<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String login = usuario.getLogin();

	String cUR = "";
	cUR = usuario.getU_UR();
	String inputName = request.getParameter("inputName") == null || "".equals(request.getParameter("inputName"))
			? "numEmpleadoBeneficiario"
			: request.getParameter("inputName");
	String formName = request.getParameter("formName") == null || "".equals(request.getParameter("formName"))
			? "FormViaticos"
			: request.getParameter("formName");
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Seleccion de Empleados</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"> </script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"> </script>
<script type="text/javascript">

	var inputName = "<%=inputName%>";
	var formName ="<%=formName%>";
	
	$(document).ready(function() {

		$("#cnombre").val("");
		$("#cNumeroEmpleado").val("");
		$("#cIdRFC_RelacionGasto").val("");

		cargaGrid();

		document.getElementById("cIdRFC_RelacionGasto").focus();

		$("#dt_Beneficiario tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});

			$(event.target.parentNode).addClass('row_selected');

			var aPos = oTable.fnGetPosition(event.target.parentNode);
			var aData = oTable.fnGetData(aPos);
			
			$("#campoNoEmpleado").val(aData[ 0 ]);
			$("#camporfc").val(aData[ 1 ]);
			$("#camponombre").val(aData[ 2 ]);
			
			$("#cIdRFC_RelacionGasto").val($("#camporfc").val());
			$("#cnombre").val( $("#camponombre").val() );
			$("#cNumeroEmpleado").val( $("#campoNoEmpleado").val() );
			
		});

		$("#dt_Beneficiario tbody").dblclick(function() {
			enviar();
		});
		
	});

	function cargaGrid() {

		var sV_CatalogoRFC = "vEmpleadoViaticante";

		$("#cWhere").val(generaCondicion());

		oTable = $("#dt_Beneficiario").dataTable({
			"bPaginate" : true,
			"iDisplayLength" : "25",
			"bLengthChange" : true,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : false,
			"sScrollY" : 300,
			"sScrollYInner" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"sScrollX" : "100%",
			"sScrollXInner" : "100%",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=" + sV_CatalogoRFC 
			              + ( $("#cWhere").val() == ""? "": ( "&qw=" + encodeURI( $("#cWhere").val() ) )  ),
			aoColumns : [
				{
					sName : "c_empleado",
					bSearchable : false,
					bSortable : false,
					bVisible : true,
					sClass : "alignLeft"
				},
				{
					sName : "rfc",
					bSearchable : false,
					bSortable : false,
					bVisible : true,
					sClass : "alignLeft"
				},
				{
					sName : "nombreCompleto",
					bSearchable : false,
					bSortable : true,
					bVisible : true,
					sClass : "alignLeft"
				}
			],
			
			oLanguage : {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtered from _MAX_ total entries)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			}
		});
	//alert("Where: " + $("#cWhere").val());
	}

	function limpiarDatos() {
		$("#cIdRFC_RelacionGasto").val("");
		$("#cnombre").val("");
		$("#cNumeroEmpleado").val("");
		
		document.getElementById("cIdRFC_RelacionGasto").focus();

		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});

		$("#camponombre").val("");
		$("#camporfc").val("");

		cargaGrid();
	}

	function generaCondicion() {
		var where = "";
		var token = "";

		if( $("#cnombre").val() != "" ) {
			where += token + " nombreCompleto LIKE '%" + $.trim($("#cnombre").val()) + "%'";
			token = " AND ";
		}

		if( $("#cIdRFC_RelacionGasto").val() != "" ) {
			where += token + " rfc LIKE '%" + $.trim($("#cIdRFC_RelacionGasto").val()) + "%'";
			token = " AND ";
		}
		
		if( $("#cNumeroEmpleado").val() != "" ) {
			where += token + " c_empleado LIKE '%" + $.trim( $("#cNumeroEmpleado").val()) + "%'";
			token = " AND ";
		}
		
		return where;
	}

	function Buscar() {
	
		if( $.trim( $("#cnombre").val() ) == "" && $.trim( $( "#cIdRFC_RelacionGasto" ).val() ) == "" && $.trim( $( "#cNumeroEmpleado" ).val() ) == "" ) {
			alert("Para hacer una busqueda favor de ingresar datos ya sea en el RFC o en el Nombre o el Numero de Empleado");
			return;
		}
		
		$(oTable.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		
		cargaGrid();
	}

	function enviar() {
	
		if( $.trim($("#camponombre").val()) == "" && $.trim($("#camporfc").val()) == "" ) {
			alert("Por favor Selecciona la fila del Beneficiario deseado.");
			return false;
		}

		var inputTrgt;

		eval( 'window.opener.' + formName + '.' + inputName + '.value=window.formBenef.campoNoEmpleado.value' );
		eval( 'inputTrgt = window.opener.' + formName + '.' + inputName );

		if( inputTrgt.onchange ) {
			inputTrgt.onchange();
		}

		window.close();
	}
</script>

</head>
<body id="dt_example">
	<div id="container">
		<form id="formBenef" name="formBenef">
			<input type="hidden" value="" name="camponombre" id="camponombre">
			<input type="hidden" value="" name="camporfc" id="camporfc">
			<input type="hidden" value="" name="campoNoEmpleado" id="campoNoEmpleado">
			<input type="hidden" value="" name="cWhere" id="cWhere">

			<br>
			
			<fieldset>
				<legend> Datos del Beneficiario </legend>
				<table>
					<tr>
						<td align="left">N&uacute;mero de Empleado:</td>
						<td><input name="cNumeroEmpleado" type="text" id="cNumeroEmpleado" size="15" maxlength="15" style="text-transform:uppercase" onkeyup="Buscar()"></td>
					</tr>
					<tr>
						<td align="left">RFC:</td>
						<td><input name="cIdRFC_RelacionGasto" type="text" id="cIdRFC_RelacionGasto" size="20" maxlength="15" style="text-transform:uppercase" onkeyup="Buscar()"></td>
					</tr>
					<tr>
						<td align="left">Nombre:</td>
						<td><input name="cnombre" type="text" id="cnombre" size="50" style="text-transform:uppercase" onkeyup="Buscar()"></td>
					</tr>
				</table>
				<table align="right">
					<tr>
						<td><input type="button" id="btn_busca" name="btn_busca" value="Buscar" onclick="Buscar()"></td>
						<td><input type="button" id="btn_limpia" name="btn_limpia" value="Limpiar" onclick="limpiarDatos()"></td>
						<td><input type="button" id="btn_aceptar" name="btn_aceptar" value="Aceptar" onclick="enviar()"></td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table id="dt_Beneficiario" class="display" align="center">
				<thead>
					<tr>
						<th><font size="2">N&uacute;mero Empleado</font></th>
						<th><font size="2">RFC</font></th>
						<th><font size="2">Nombre</font></th>
					</tr>
				</thead>
			</table>

		</form>
	</div>
</body>
</html>