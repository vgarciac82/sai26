<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String ur = usuario.getU_UR();
	String usuLogin = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
       
    <title>Cierre de Cuentas Por Pagar</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
	</style>
	<style media="all" type="text/css">     
		.alignRight { text-align: right; } 
		.alignCenter { text-align: center; }
	</style> 
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript">
$(document).ready(function(){
			$("#btnProcesar").click(function () { procesar(); });		
	
			pParam = "<%=ur%>"; 
			var tablaCierre = $('#dataCierreCuentasPorPagar').dataTable({         
							bRetrive: false,
							bPaginate: true,
							bDestroy: true,
							bLengthChange: false,
							iDisplayLength: 100,
		        			bFilter: true,
		        			bSort: true,
		        			bInfo: false,
		        			bAutoWidth: false,
		        			oLanguage: {
								sProcessing: "Procesando... Espere Por Favor",
								sLengthMenu: "Mostrar _MENU_ registros",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
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
							},
							bServerSide: true,
							//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCierreCuentasPorPagar&qw=" + " Estatus != 'Cancelado' AND caNoContrarrecibo != '0' " +(pParam == 'B00' ? " " : " AND cUnidadResponsable = '"+pParam+"'"),
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCierreCuentasPorPagarEncabezado&qw=" + " Estatus != 'Cancelado' AND caNoContrarrecibo != '0' ",
							bProcessing: true,
							sPaginationType: "full_numbers",
							bJQueryUI: true,
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "id",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"200px"},
								{ sName: "docto",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cIdRFC",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cnombre",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft"},
								{ sName: "cUnidadResponsable",  bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignCenter"},
								{ sName: "aEjercicioFiscal",	bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft"},
								{ sName: "fAplicacionF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "mImporteNetoF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"}
								//{ sName: "mImporteTotal",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"}
								
							]
		        		});	
});

function tablaCierre(tipo)
{
	if(tipo != ""){ tipo = $("#tipoDocumento").val(); }
	alert(tipo);
			   $('#dataCierreCuentasPorPagar').dataTable({         
							
							bRetrive: false,
							bPaginate: true,
							bDestroy: true,
							bLengthChange: false,
							iDisplayLength: 100,
		        			bFilter: true,
		        			bSort: true,
		        			bInfo: false,
		        			bAutoWidth: true,
		        			oLanguage: {
								sProcessing: "Procesando... Espere Por Favor",
								sLengthMenu: "Mostrar _MENU_ registros",
								sZeroRecords: "No hay registros a mostrar",
								sEmptyTable: "No hay datos en la tabla",
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
							},
							bServerSide: true,
							//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCierreCuentasPorPagar&qw=" + "Estatus != 'Cancelado' AND caNoContrarrecibo != '0' " +(pParam == 'B00' ? " " : " AND cUnidadResponsable = '"+pParam+"'" + (tipo == '' ? " " : " AND docto = '"+tipo+"'")),
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCierreCuentasPorPagarEncabezado&qw=" + "Estatus != 'Cancelado' AND caNoContrarrecibo != '0' " + (tipo == '' ? " " : " AND docto = '"+tipo+"'"),
							bProcessing: true,
							sPaginationType: "full_numbers",
							bJQueryUI: true,
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "id",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"200px"},
								{ sName: "docto",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cIdRFC",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cnombre",			    bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft"},
								{ sName: "cUnidadResponsable",  bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignCenter"},
								{ sName: "aEjercicioFiscal",	bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft"},
								{ sName: "cnombre",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft"},
								{ sName: "fAplicacionF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "mImporteNetoF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"}
								//{ sName: "mImporteTotal",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"}
								
							]
		        		});	
}

function procesar()
{
	var table = document.getElementById("dataCierreCuentasPorPagar");
	var data = $('#dataCierreCuentasPorPagar').dataTable().fnGetNodes();
	var docSelec = 0;
	var tipoDocumento = "";
	var caNoContrarrecibo = "";
	var idCierreCXP = "";
	
	for(var i=0; i<=data.length; i++)
	{
		var row = table.rows[i];
		var chkbox = row.cells[0].childNodes[0];
		
		if(null != chkbox && true == chkbox.checked)
		{
			getNextSequenceVal({seqName: "CIERRE-CXP" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
			tipoDocumento += row.cells[1].childNodes[0].toString()+"/";
			caNoContrarrecibo += row.cells[2].childNodes[0].toString()+"/";
			idCierreCXP += $("#nFolioCierreCuentas").val()+"/";
			docSelec = 1;
			
		}
		
	}
	if(docSelec == 0){
		alert("Seleccione un Documento");
		return;
	}else{
		if(confirm("Esta Seguro Hacer El Cierre De Cuentas Por Pagar")){
			
			
			$.ajax({
					url: './cierrePresupuestal.jsp',
					type: 'post',
					dataType: 'json',
					data: {tipo:'guardarCierreCuentasPorPagar', tipoDocumento:tipoDocumento,caNoContrarrecibo:caNoContrarrecibo,idCierreCXP:idCierreCXP},
					success: function(data){
							if(data.sinSesion == "sinSesion"){
								location.href = "../index.jsp";
							}else if(data.estatus == "guardado"){
								alert("Guardado Correctamente");
							}else{
								alert("No Guardado");
							}
							location.reload();
					}
				
			});
			
		}
		
	}
}

function setSequenceVal(seqValue) 
{
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioCierreCuentas").val( seqValue );
}




</script>
  </head>
  <body id="dt_example">
		<form id="formCierre" name="formCierre">
  			<div id="container" >
  				<h1>Cierre de Cuentas Por Pagar<label style="font-size: 8pt"></label></h1>
  					<div id="tabsl">
  						<table id="tblCierreCXP" align="center" width="950px">
  							<td><input type="hidden" id="nFolioCierreCuentas" name="nFolioCierreCuentas"></td>
  							<!-- tr>
  								<td>
  									<fieldset>
  										<table id="tblCXP" align="center" width="800px" height="90px">
		   									<tr>
												<tr><td>Tipo de Documento</td><td><select id="tipoDocumento" name="tipoDocumento" onchange="tablaCierre(this.value)">
																						<option value="seleccione">Seleccione</option>
																						<option value="Directo">Pago Directo</option>
																						<option value="Rel_Gastos">Relacion Gastos</option>
																						<option value="Obra">Pago de Obra</option>
																						<option value="Federalizado">Federalizado</option>
																					</select></td>
												<td><input type="button" id="btnProcesar" name="btnProcesar" value="Procesar"></td>
												<td></td><td></td><td><input type="button" id="btnExportar" name="btnBuscar" value="Exportar"></td></tr>
  										</table>
  									</fieldset>	
  								</td>
  							</tr -->		
  							<tr><td>&nbsp;</td></tr>	
  							<tr>
  								<td>	
  									<fieldset>
					   						<legend>Cuenta Por Pagar ( Pasivos )</legend>
					   							<table align="right">
		   												<td><input type="button" id="btnProcesar" name="btnProcesar" value="Procesar"></td><!-- td><input type="button" id="btnExportar" name="btnBuscar" value="Exportar"></td -->
		   										</table>
							   					<table id="dataCierreCuentasPorPagar" name="dataCierreCuentasPorPagar" class="display" align="center" width="900px" >
							   							<tbody>
							   								<thead>
							   									<th>Seleccionar</th>
							   									<th>Tipo Documento</th>
																<th>caNoContrarrecibo</th>	
																<th>RFC</th>
																<th>Beneficiario</th>
																<th>U. Responsable</th>
																<th>E. Fiscal</th>
																<th>Fecha Aplicacion</th>
																<th>Importe Neto</th>	
																<!-- >th>Importe Total</th -->	
							   								</thead>
							   							</tbody>
					   							</table>
					   				</fieldset>
  								</td>
  							</tr>
  						</table>
  					</div>
  
  			</div>
		</form>
  </body>
</html>
