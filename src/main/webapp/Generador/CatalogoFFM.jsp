<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String login=usuario.getLogin();
	
	String cUR = "";
	cUR = usuario.getU_UR();
	String archivoPadre=request.getParameter("nombreArchivoPadre")==null || "".equals(request.getParameter("nombreArchivoPadre"))? "":request.getParameter("nombreArchivoPadre");
	
	String formName = request.getParameter("formName")==null || "".equals(request.getParameter("formName"))? "formPagos":request.getParameter("formName");
	
	String inputFFMTarget = request.getParameter("inputFFMTarget")==null || "".equals(request.getParameter("inputFFMTarget"))? "subFFM":request.getParameter("inputFFMTarget");
	String inputDFFMTarget = request.getParameter("inputDFFMTarget")==null || "".equals(request.getParameter("inputDFFMTarget"))? "cnombre":request.getParameter("inputDFFMTarget");
	
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Catalogo de Beneficiarios</title>

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
	
	var formName ="<%=formName%>";
	var inputDFFMTarget = "<%=inputDFFMTarget%>";
	var inputFFMTarget = "<%=inputFFMTarget%>";
	var archivoPadre= "<%=archivoPadre%>";	


	//READY
	$(document).ready(function(){
		
		$("#cnombre").val("");
		$("#subFFM").val("");
		
		cargaGrid();
		
		document.getElementById("subFFM").focus();	
			
		$("#dt_FFM tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			
			$(event.target.parentNode).addClass('row_selected');
			
			var aPos = oTable.fnGetPosition(event.target.parentNode);
     		var aData = oTable.fnGetData(aPos);
			$("#campoffm").val(aData[0]);
			$("#camponombre").val(aData[1]);
			$("#subFFM").val($("#campoffm").val());
			$("#cnombre").val($("#camponombre").val());
		});
		
		$("#dt_FFM tbody").dblclick(function(){
			enviar();
		});
	});
	
	function cargaGrid(){
				
		var sV_CatalogoFFM = "tSubcuentasFFM";
		
		$("#cWhere").val(generaCondicion());
				
		oTable = $("#dt_FFM").dataTable({
	        "bPaginate": true,
	        "iDisplayLength":"25",
   			"bLengthChange": true,
   			"bFilter": false,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": false,
			"sScrollY": 300,
			"sScrollYInner": "100%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"sScrollXInner": "100%",
			"bScrollCollapse": true,	
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+ sV_CatalogoFFM +"&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns: [
				{ sName: "cSubcuenta",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cDescripcion",	bSearchable: false,	bSortable: true, bVisible: true, sClass: "alignLeft"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtered from _MAX_ total entries)",
				sInfoPostFix: "",
				sInfoThousands: ",",
				sSearch: "Buscar:",
				oPaginate: {
					sFirst:    "Primero",
					sPrevious: "Ant.",
					sNext:     "Sigte.",
					sLast:     "&Uacute;ltimo"
				}
			}
   		});
   		//alert("Where: " + $("#cWhere").val());
   	}
	
	function limpiarDatos(){
		$("#subFFM").val("");
		$("#cnombre").val("");
		document.getElementById("subFFM").focus();
	
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
			
		$("#camponombre").val("");
		$("#campoffm").val("");
		
		cargaGrid();
	}
	
	function generaCondicion(){
		var where = " 1=1 ";
		var token = " AND ";
		
		if( $("#cnombre").val() != "" ){
			where += token + " cDescripcion LIKE '%" + $.trim($("#cnombre").val()) + "%'";
			token = " AND ";
		}
			
		if( $("#subFFM").val() != "" ){
			where += token + " cSubcuenta LIKE '" + $.trim($("#subFFM").val()) + "%'"; 
			token = " AND ";
		}
				
		return where;
	}
	
	function Buscar(){
		/*if ($.trim($("#cnombre").val())=="" && $.trim($("#subFFM").val())==""){
			alert("Para hacer una busqueda favor de ingresar datos ya sea en el RFC o en el Nombre.");
			return;
		}*/
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
		cargaGrid();
	}
	
	function enviar(){
		if ($.trim($("#camponombre").val())=="" && $.trim($("#campoffm").val())==""){
			alert("Por favor Selecciona la fila de la subcuenta deseada.");
			return false;
		}
		
		eval( 'window.opener.' + formName + '.' + inputFFMTarget + '.value=window.formFFM.camponombre.value' );
		eval( 'window.opener.' + formName + '.' + inputFFMTarget + '.value=window.formFFM.campoffm.value' );
		//eval( 'window.opener.' + formName + '.' + inputFFMTarget + '.onchange()' );
		//verificar que archivo lo está llamando
		if(archivoPadre!="")
			window.opener.cat_FFM();
	
		window.close();
	}
	
</script>
	
</head>
	<body id="dt_example"> 
		<div  id="container" >
	  		<form id="formFFM" name="formFFM">	  			
	  			<input type="hidden" value="" name="camponombre" id="camponombre">
	  			<input type="hidden" value="" name="campoffm" id="campoffm">
	  			<input type="hidden" value="" name="cWhere" id="cWhere">
	  			<br>
	  			<fieldset>
	  				<legend> Datos de las Sub Cuentas del Fondo Forestal Mexicano </legend>
					<table>
			        	<tr>
		        			<td align="left">SubCuenta:</td>
				        	<td>
				        		<input name="subFFM" type="text" id="subFFM" size="20" maxlength="15" style="text-transform:uppercase" onkeyup="Buscar()">
				        	</td>
		        		</tr>
			        	<tr>
				        	<td align="left">Descripción:</td>
				        	<td>
				        		<input name="cnombre" type="text" id="cnombre" size="50" style="text-transform:uppercase" onkeyup="Buscar()">
				        	</td>
			        	</tr>
		        	</table>
		        	<table align="right">
		        		<tr>
			        		<td>
				        		<input type="button" id="btn_busca" name="btn_busca" value="Buscar" onclick="Buscar()">
				        	</td>
				        	<td>
				        		<input type="button" id="btn_limpia" name="btn_limpia" value="Limpiar" onclick="limpiarDatos()">
				        	</td>
				        	<td>
				        		<input type="button" id="btn_aceptar" name="btn_aceptar" value="Aceptar" onclick="enviar()">
				        	</td>
			        	</tr>
		        	</table>
	        	</fieldset>
	        	<br>
				<table id="dt_FFM" class="display" align="center">
					<thead>
						<tr>
							<th><font size="2">SubCuenta</font></th>
							<th><font size="2">Descripción</font></th>
						</tr>
					</thead>
				</table>
				
			</form>	
		</div>
	</body>	
</html>