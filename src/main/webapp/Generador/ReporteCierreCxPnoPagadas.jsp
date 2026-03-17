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

	var campos = " '" + $("#cTipoDoc").val() + "','"+ $("#cContra").val() +"','"+ $("#cRFC").val() +"' "			
	var elParametro2 = '';
	var szTabla = "REPORTECIERRECXPPAGADAS";
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla : szTabla,Param : elParametro2,Campos : campos,MaxReg : "",ajax : 'false'},function(j) {

		if (j.length == 0){
			alert("No Existe Informacion a mostrar");
			$("#esperardet").hide();
		}else{
			for ( var i = 0; i < j.length; i++) {
							
				$('#tReporteCadenas').dataTable().fnAddData([ j[i].Col0, j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7]);			
				$("#esperardet").hide();
			}
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
			+ "&rn=ReporteCierreCxPPagadas.jasper"
			+ "&TipoDoc=" + $("#cTipoDoc").val()
			+ "&contra=" + $("#cContra").val()
			+ "&RFC=" + $("#cRFC").val(),

		"Anexo",
		"scrollbars=1, resizable=yes, width=1024, height=768"
	);
	
}
		
		</script>
		

</head>

<body id="dt_example" >
	<form name="ExportarForm" id="ExportarForm" action="../gstnmngr/generaLayoutCierreCxPPagadas" method="post"> 
		<div id="container" style="width:1000px; padding-left:100px" class="SyCData">	
			<h1>Cierre Cuentas Por Pagar Pagadas</h1>			
			<table align="center" id="clvcont" border=0>
				<tr>
					<td align="right">Tipo Documento: </td>
					<td>
						<select id="cTipoDoc"  name="cTipoDoc" style="width: 140px;" >
							<option value="">-Tipo Documento-</option>
							<option value="Directo">Directo</option>
							<option value="Obra">Obra</option>
							<option value="Rel_Gastos">Rel_Gastos</option>
							<option value="Federalizado">Federalizado</option>
							<option value="Diverso">Diverso</option>
							<option value="Ajenas">Ajenas</option>
							<option value="Registro Pasivo">Registro Pasivo</option>
							
						</select> 
<%--						<input style="background-color:#F5F5F5;" type="text" size="30" name="cTipoDoc" id="cTipoDoc" >--%>
					</td>
				</tr>
					
				
				<tr align="right">
					<td align="right">Contrarrecibo: </td>
					<td align="left">
						<input style="background-color:#F5F5F5;" type="text" size="15" name="cContra" id="cContra" >
					</td>		
				</tr>
				
				<tr align="right">
					<td align="right">RFC: </td>
					<td align="left">
						<input style="background-color:#F5F5F5;" type="text" size="15" name="cRFC" id="cRFC" >
					</td>		
				</tr>
			</table>
			<br/>
			<center>
				<button id="cConsulta" onclick="DataTable()">Consulta</button>
				<button id="cidImprimer" onclick="anexo()">Imprimir</button>
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
										Tipo Documento
									</th>
									<th>
										CanoContrarrecibo
									</th>
									<th>
										RFC
									</th>
									<th>
										Beneficiario
									</th>
									<th>
										U.Responsable
									</th>
									<th>
										E. Fiscal
									</th>
									<th>
										Feha Aplicacion
									</th>
									<th>
										Importe Neto
									</th>
									
								</tr>
							</thead>
						</table>
		</div>
	</form>
	</body>
</html>