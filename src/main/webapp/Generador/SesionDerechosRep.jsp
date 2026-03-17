<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%String Control[] = {"EjercicioFiscal","RamoEP",
			"UnidadResponsableEP",
			"GrupoFuncional","Funcion","SubFuncion","ProgramaGeneral","ActividadInstitucional",
			"ProgramaPresupuestario","Partida",
			"TipoGasto","FuenteFinanciamiento","EntidadFederativa","Cartera","UnidadNormativa",
			"cUnidadEjecutora"
			};%>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Consulta Sesión de Derechos</title>
    
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
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	
 
 <script type="text/javascript">
 $(document).ready(function (){
	 
	$("#btnConsultar").click(function(){ 
		var cUR = "'" + $("#cUnidadResponsable").val() + "'";
		var cTP = "'" + $("#cTipoContrato").val() + "'"; 
		
		$('#SesionDetalle').dataTable( {
    					bAutoWidth : true,
						sScrollX: "100%",
						sScrollY: "400",
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtado de _MAX_ registros)",
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
						bRetrive: true,
						bDestroy: true,
						bServerSide: true,
						bProcessing: true,
						sPaginationType: "full_numbers",
						bJQueryUI: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSesionDerechos&qw=(UA = " + cUR + " OR " + cUR + " = 'RHQ' ) AND (TIPO = " + cTP + " OR " + cTP + " = 'TODOS')",
			aoColumns: [
				{ sName: "caNoContrarrecibo"},
				{ sName: "fAplicacion"},
				{ sName: "mImporteNeto"},
				{ sName: "RFC"},
				{ sName: "NOMBRE"},
				{ sName: "cNoFactura"},
				{ sName: "TIPO"},
				{ sName: "UA"},
				{ sName: "cIdContrato"},
				{ sName: "cIdRFCSesion"},
				{ sName: "fSesionVigencia"},
				{ sName: "u_login"}]
			} ) ;

	});
	
	//$("#btnConsultar").attr("disabled","disabled");
	querySelectPost("tEjercicioRead", "aEjercicioFiscal", {async: false });
	querySelectPost("UnidadresponsableRead","cUnidadResponsable", {async: false });
	$("#btnImprimir").click(function(){ cmdImprimir(); });
	if("<%=ur%>" == "A02"){
		$("#cUnidadResponsable").attr("disabled", false);
	}else{
		$("#cUnidadResponsable").attr("disabled", true);
	}
	$("#cUnidadResponsable").val("<%=ur%>");
	
	var oTable = $('#SesionDetalle').dataTable({         
			bSortClasses: false,
			ScrollY: "500px",
			sScrollX: "702px",
			bPaginate: false,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: true,
        	bInfo: false,
        	bAutoWidth: false,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			bScrollCollapse: true,
			aaSorting: [[ 1, "asc" ]] ,
			bRetrive: true,
			oLanguage: {
						sProcessing: "Procesando...",
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
				}
});
	
	
});

	

function cmdImprimir(){
	window.open(
		"../admin/SeguridadCatalogos?"
			+ "catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=SesionDerechos.jasper"
			+ "&sUA=" + $("#cUnidadResponsable").val()
 			+ "&sTipo=" + $("#cTipoContrato").val(),
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");
					
}


function Consultar(){

/*	$('#SesionDetalle').dataTable( {
		bPaginate: false,
     	bLengthChange: false,
     	bFilter: false,
     	bInfo: false,
		bAutoWidth: false,
		//sScrollX: 200,
		sScrollY: 100,
		bScrollCollapse: true,
		bJQueryUI: true,
		bDestroy : true,
		bServerSide: true,   
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSesionDerechos&qw=(UA = '" + $("#cUnidadResponsable").val() + "' OR + '" + $("#cUnidadResponsable").val() "' = 'RHQ' ) AND (TIPO = '" + $("#cTipoContrato").val() + "' OR '" + $("#cTipoContrato").val() + "' = 'TODOS')",
		aoColumns: [
			{ sName: "TIPO"},
			{ sName: "cIdContrato"},
			{ sName: "cIdRFCSesion"},
			{ sName: "fSesion"},
			{ sName: "u_login"}]
		} ) ;
*/
}


</script>
</head>
<body id="dt_example">
	  <form id="formPagos" name="formPagos">
	  		<div id="container" class="container SyCData">
				<div id="container">
				
				  	<h1>Consulta Sesión de Derechos<label style="font-size: 8pt"></label></h1>
				  	
				  	<input type="hidden" id="estatus" name="estatus" value="Activo">
				  	<select style="visibility: hidden" name="aEjercicioFiscal" id="aEjercicioFiscal"></select>
				  	<input type="hidden" id="U_LOGIN" name="U_LOGIN" value="<%=usuLogin%>">
				  	<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>">
				  	
					<div id="tabsl"> 
					  	<fieldset> 
					  		<table id="tbl" width="1000px" height ="100px">
					  			<tr>
					  				<td id="uResponsable">Unidad Responsable</td>
					  				<td>
					  					<select id="cUnidadResponsable" name="cUnidadResponsable">
					  					</select>
					  				</td>
					  			</tr>
					  			<tr>
					  				<td>Tipo Contrato</td>
					  				<td>
					  					<select id="cTipoContrato" name="cTipoContrato">
					  						<option>TODOS</option>
					  						<option>Obra</option>
					  						<option>Diverso</option>
					  					</select>
					  				</td>
					  			</tr>
					  		</table>
							<table align ="center">
					  			<tr>
					  				<td>&nbsp;</td><td><input type="button" id="btnConsultar" name="btnConsultar" value="Consulta" ></td>
					  				<td>&nbsp;</td><td><input type="button" id="btnImprimir" name="btnImprimir" value="Imprimir"></td>
					  			</tr>
					  		</table>
						</fieldset>	
					</div>
					  	<div>
					  	<table id="Detalle">	
							<tr>	
								<td>
									<table id="SesionDetalle" class="display" align="center" width="600px">
										<thead>
											<tr align="center"> 
												<th>Cuenta x Pagar</th>	
												<th>Fecha</th>	
												<th>Importe Neto</th>	
												<th>RFC</th>	
												<th>Nombre o Razón Social</th>	
												<th>Factura</th>	
												<th>Tipo</th>	
												<th>UR</th>	
												<th>Contrato</th>
												<th>RFC Sesión</th>
												<th>Fecha Vigencia</th>
												<th>Usuario</th>
											</tr>	
										</thead>							
										<tbody>
										</tbody>	
									</table>						
								</td>
							</tr>
						</table>
				  	</div>
	  			</div> 
	  		</div>
	  </form>
</body>
</html>
