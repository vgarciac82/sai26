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
	$(document).ready(function() {
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		$('#dt_catalogo').dataTable({
			"bScrollCollapse" : true,
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			"bAutoWidth" : true,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sScrollY" : "400",
			"sScrollX" : "1020"
		});


      $("#IdFechaInicio").datepicker
		({
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true
		});
		
		$("#IdFechaFin").datepicker
		({
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "../Generador/images/calendar.gif",
			buttonImageOnly : true
		});
		
		
	});
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato">
	<div id="container" class="container">
	<h1>Reporte Consoc</h1>

		<table id="reAplica_Docto" width="50%" align ="center" >
						<tr>
							<td align="right">Tipo Reporte:</td>
								<td>
								<select id= "opcion" name="opcion"   >
									<option value="1" selected="selected">
										CARÁTULA </option>
									<option value="2">
										ANALÍTICO  3700 S-M</option>
									<option value="3">
										ANALÍTICO 33605 </option>
								</select>
							</td>
						</tr>
						<tr>
								<td align="right">Fecha Inicio: </td> 
								<td> <input align="right" type="text" id="IdFechaInicio" name="IdFechaInicio"  readonly="readonly" value="" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="17" /></td>
						</tr>
						<tr>
								<td align="right">Fecha Fin: </td> 
								<td> <input align="right" type="text" id="IdFechaFin" name="IdFechaFin"  readonly="readonly" value="" datepicker_format="DD/MM/YYYY" datepicker="true" maxlength="10" size="17" /></td>
						</tr>
						<tr>	
										
							<td align="center" colspan="3">
							    <input type="button" id="btnBuscar" name ="btnBuscar" value="Buscar"  ></input>
							    <input type="button" id="btnLimpiar" name ="btnLimpiar" value="Limpiar" onclick="limpiaDatos();"></input>
							    
							</td>
						</tr>
						
		</table>

        <p>&nbsp;</p>
		<table id="dt_catalogo" class="display" cellspacing="0" border="0" cellpadding="2" align="center">
					<thead>
						<tr>
							
							<th>Folio</th>
							<th>Contrarecibo</th>
							<th>F.Autorización</th>
							<th>Usuario</th>
							<th>Factura</th>
							<th>Importe</th>
							
						</tr>
					</thead>
					<tbody>
						<tr>
							
							<td>CGCCA-018/2013</td>
							<td>10CP2013001071</td>
							<td>30/01/2013</td>
							<td>HCAMACHO</td>
							<td>115400</td>
							<td>$75,600.00</td>
						
						</tr>
						<tr>
							
							<td>CGCCA-002/2013</td>
							<td>10CP2013001073</td>
							<td>30/02/2013</td>
							<td>ADMIN23</td>
							<td>AXAA-3119</td>
							<td>$41,438.40</td>
							
							
						</tr>
						<tr>
							
							<td>CGCCA-040/2013</td>
							<td>10CP2013001023</td>
							<td>30/02/2013</td>
							<td>JHERNANDEZ</td>
							<td>59</td>
							<td>$35,000.00</td>
							
							
						</tr>
						<tr>
						
							<td>CGCCA-023/2013</td>
							<td>10CP2013001022</td>
							<td>30/02/2013</td>
							<td>EREYES</td>
							<td>158</td>
							<td>$27,500.00</td>
							
							
						</tr>
						<tr>
							
							<td>CGCCA-051/2013</td>
							<td>10CP2013001125</td>
							<td>30/02/2013</td>
							<td>JHERNANDEZ</td>
							<td>426</td>
							<td>$15,000.00</td>
							
						</tr>
						<tr>
							
							<td>CGCCA-051/2013</td>
							<td>10CP2013001125</td>
							<td>15/04/2013</td>
							<td>HCAMACHO</td>
							<td>426</td>
							<td>$15,000.00</td>
						</tr>
						
					</tbody>
				</table>
			</div>
		</form>
</body>
</html>