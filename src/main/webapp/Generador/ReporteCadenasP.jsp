<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cadenas Productivas</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
			
		$(document).ready(function() {
		$("#esperardet").hide();	
			$(function() {
			$( "#fEmisionInc" ).datepicker({
				showOn: "button",
				showAnim:"slideDown",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});	
			$(function() {
			$( "#fEmisionFin" ).datepicker({
				showOn: "button",
				showAnim:"slideDown",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});	

	$("input.AyudaSyC").subIniciaDlg();

	$("#tReporteCadenas").dataTable({
						ScrollY : "500px",
		sScrollX : "100%",
		bPaginate : true,
		bLengthChange : false,
		bFilter : false,
		bSort : false,
		bInfo : false,
		bAutoWidth : true,
		bJQueryUI : true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType : "full_numbers",
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
			sSearch : "Buscar:"
						}
			});		
});

function DataTable(){
	$("#esperardet").show();	
	
	var nRows = $("#tReporteCadenas tr").length - 1;

	if (nRows > 0) {
		var DtCAdenas = $("#tReporteCadenas").dataTable();
		DtCAdenas.fnClearTable();
	}

	var campos = " '" + $("#cIDRFC").val() + "','"+ $("#cEstatus").val() +"','"+ $("#cCentroContable").val() +"','"+ $("#nDigitoID").val()+"','" + $("#fEmisionInc").val() + "','" + $("#fEmisionFin").val() + "'"			
	var elParametro2 = '';
	var szTabla = "REPORTECADENASP";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla : szTabla,Param : elParametro2,Campos : campos,MaxReg : "",ajax : 'false'},function(j) {

		if (j.length == 0){
			alert("No Existe Informacion a mostrar");
			$("#esperardet").hide();
		}else{
			var arrData = new Array();
			for ( var i = 0; i < j.length; i++) {
				
				arrData[ i ] = [ j[i].Col0, j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9 ];
			}
			$('#tReporteCadenas').dataTable().fnAddData( arrData );			
			$("#esperardet").hide();
		}		
	});	
}		
		
function Generar (){

	document.ExportarForm.submit();
	
}	

function anexo(){
	
	window.open(
		"../admin/SeguridadCatalogos?"
			+ "catalogo=ANEXO"
			+ "&accion=run"
			+ "&rn=cadenasp.jasper"
			+ "&cClaveProv=" + $("#cIDRFC").val()
			+ "&cEstatus=" + $("#cEstatus").val()
			+ "&cCentroContable=" + $("#cCentroContable").val()
			+ "&cDigitoIde=" + $("#nDigitoID").val()
			+ "&pFechaIni=" + $("#fEmisionInc").val()
			+ "&pFechaFin=" + $("#fEmisionFin").val(),
		"Anexo",
		"scrollbars=1, resizable=yes, width=1024, height=768"
	);
	
}
		
		</script>
		

</head>

<body id="dt_example" >
	<form name="ExportarForm" id="ExportarForm" action="../gstnmngr/generaLayoutCadenasP_PrototipoReporteExcel" method="post"> 
		<div id="container" style="width:1000px; padding-left:100px" class="SyCData">	
			<h1>Cadenas Productivas</h1>			
			<table align="center" id="clvcont" border=0>
				<tr>
					<td align="right">Clave del Provedor: </td>
					<td>
						<input style="background-color:#F5F5F5;" type="text" size="30" name="cIDRFC" id="cIDRFC" class="AyudaSyC obligatorio" >
					</td>
				</tr>
					<td align="right">Fecha de Emision: </td>
					<td >
						<input style="background-color:#F5F5F5;" type="text" size="8" name="fEmisionInc" id="fEmisionInc" readonly >
						&nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a
					</td>
					<td>
						<input style="background-color:#F5F5F5;" type="text" size="8" name="fEmisionFin" id="fEmisionFin" readonly >
					</td>
				<tr align="left">
					<td align="right">Estatus: </td>
					<td >
						<select id="cEstatus"  name="cEstatus" style="width: 150px; background-color:#F5F5F5;" >
			              <option value="Pagado sin Operar">Pagado sin Operar</option>
			              <option value="Pagado Anticipado">Pagado Anticipado</option>
			              <option value="Baja">Baja</option>
			              <option value="Descartado">Descartado</option>
			              <option value="Errores">Errores</option>
			              <option value="Vencido sin Operar">Vencido sin Operar</option>
			              <option value="No Negociable">No Negociable</option>
			              <option value="Operada Pagada">Operada Pagada</option>
			              <option value="Operada Pagada">Operada Pagada</option>
			              <option value="NO APLICA">NO APLICA</option>NO APLICA
			              <option value="" selected ></option>
	            		</select>
<%--						<input style="background-color:#F5F5F5;" type="text" size="30" name="cEstatus" id="cEstatus" >--%>
					</td>		
				</tr>
				<tr align="right">
					<td align="right">Entidad Contable: </td>
					<td align="left">
						<input style="background-color:#F5F5F5;" type="text" size="2" name="cCentroContable" id="cCentroContable" >
					</td>		
				</tr>
				</tr>
				<tr align="right">
					<td align="right">Digito Identificador: </td>
					<td align="left">
						<input style="background-color:#F5F5F5;" type="text" size="14" name="nDigitoID" id="nDigitoID" >
					</td>		
				</tr>
			</table>
			<br/>
			<center>
				<button id="cConsulta" onclick="DataTable()">Consulta</button>
				<button id="cidImprimer" onclick="anexo()">Imprimir PDF</button>
				<button id="cExportar" onclick="Generar()">Exportar</button>
			</center>
			<label id="esperardet">
							<div align="center">
								Espere por favor....
								<img border="0" src="../imagenes/espera.gif" height="30"/>
							</div>
						</label>
			<br/>
			<table id="tReporteCadenas"  width="100%" class="display">
							<thead>
								<tr align="center">
									<th>
										Nombre del Provedor
									</th>
									<th>
										No.Documento
									</th>
									<th>
										Fecha Vencimiento
									</th>
									<th>
										Monto
									</th>
									<th>
										Estatus
									</th>
									<th>
										Entidad Contable
									</th>
									<th>
										No.Proveedor
									</th>
									<th>
										Digito Identificador Partida
									</th>
									<th>
										Partida
									</th>
									<th>
										Motivo
									</th>
								</tr>
							</thead>
						</table>
		</div>
	</form>
	</body>
</html>