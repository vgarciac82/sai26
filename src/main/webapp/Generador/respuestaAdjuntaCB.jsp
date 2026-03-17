<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.syc.sai.procesosAutomaticos.AttachResult"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
	if( session == null){
		response.sendRedirect("../index.jsp");
		return;
	}	
	
	Usuario u = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	if( u.getPropiedad("CCENTROCONTABLE") == null || StringUtils.isEmpty(  u.getPropiedad("CCENTROCONTABLE").getValor() ) ){
		response.sendRedirect("../index.jsp");
		return;
	}

	List<?> result = (ArrayList<?>)session.getAttribute("ATT_RESPUESTA");	
	if( result == null )
		result = new ArrayList<AttachResult>();
		
	session.removeAttribute("ATT_RESPUESTA");	
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Resultado de Operacion.</title>
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
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript">
			var es_mx = {
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
			};
						
			$(document).ready(function(){
				oTable = $('#dtResult').dataTable(
				{
					"bPaginate" : false,
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bAutoWidth" : true,
					"sScrollY" : 270,
					"bJQueryUI" : true,
					"bDestroy" : true,
					"sPaginationType" : "full_numbers",
					"bScrollCollapse" : true,
					oLanguage : es_mx
				});
			});
		</script>
	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<div id="container" class="container">
			<h1>Resultado de Operaci&oacute;n</h1>
			<form id="mainFrm" name="mainFrm">
				<div id="mainDiv">
					<img alt="correcto" src="../imagenes/iconos/aceptar.png" width="10" height="10">
					<fieldset>
						<legend>Log:</legend>
						<table class="display" cellspacing="0" cellpadding="0" align="center" id="dtResult" >
							<thead>
								<tr>
									<th width="10%">&nbsp;</th>
									<th width="30%">Archivo</th>
									<th width="60%">Estatus</th>
								</tr>
							</thead>
							<tbody>
							<%for(Iterator<?> i = result.iterator(); i.hasNext();){ %>
								<%AttachResult resultTr = (AttachResult)i.next(); %>
								<%out.println("\t\t\t\t\t\t\t<tr>");%>
								<%if(resultTr.isSuccess()){%>
									<%out.println("\t\t\t\t\t\t\t\t<td><img alt=\"correcto\" src=\"../imagenes/iconos/aceptar.png\" width=\"10\" height=\"10\"></td>");%>
								<%}else{ %>
									<%out.println("\t\t\t\t\t\t\t\t<td><img alt=\"correcto\" src=\"../imagenes/iconos/alerta.png\" width=\"10\" height=\"10\"></td>");%>
								<%}%>
								<%out.println("\t\t\t\t\t\t\t\t<td>" + resultTr.getFileName()+ "</td>");%>
								<%out.println("\t\t\t\t\t\t\t\t<td>" + resultTr.getResult()+ "</td>");%>
								<%out.println("\t\t\t\t\t\t\t</tr>");%>
							<%}%>
							</tbody>
						</table>
					</fieldset>
				</div>
			</form>
		</div>
	</body>
</html>