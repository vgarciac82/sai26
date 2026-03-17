<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}
String usu = usuario.getLogin();
String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Ejercido Pagado</title>

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

$(document).ready(function () {
	
	//$('#demo9').click(function() {  $.blockUI();  $('.blockOverlay').attr('title','Click to unblock').click($.unblockUI);  }); 
	//$("#btnValidar").click(function (){ validarDatos(); });
	//$("#btnAplicar").attr("disabled","disabled");
	
	$("#btnAplicar").click(function () { aplicar(); });
	
	$("#tblAplicacion").show();
	$("#esperar").hide();	
	
	// dataTable CXP Enc	
	var TableMovCXP = $('#movimientoCXP').dataTable({         
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
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
					bAutoWidth: false,
					sScrollX: 100,
					sScrollY: 300,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tRegularizaEjercidoPagado&qw=1=1",
			aoColumns: [{ sName: "caNOcontrarrecibo"}]
		});

});		
												

	
	function aplicar(){	
		if(confirm("Esta seguro que desea aplicar contablemente?")){
			document.getElementById("btnAplicar").disabled = false;
			$("#esperar").show();
			$("#tipoDoc").removeAttr("disabled");
			var oTable = $('#movimientoCXP').dataTable();
			var nRows = $('#movimientoCXP tr').length -1 ;
			var aData = oTable.fnGetData();
			var ciclo = 0;
			
			for ( var i = 0; i < nRows; i++) {
				$("#caNOcontrarrecibo").val( aData[ i ] );
				
				$("#cEstatusPagado").val( "" );
				queryFormPost("BuscaEjercidoRead",{async: false });
				if ($("#cEstatusPagado").val() == "" ){
					$("#nFolioContable").val( $("#nFolioAdefa").val() );
					$("#selectApliContable").val( "EJERCIDO" );
					aplicarContable();		
				}
				
				ciclo = 0;
				$("#cEstatusPagado").val( "" );
		    	queryFormPost("BuscaPagadoRead",{async: false });
				if ($("#cEstatusPagado").val() == "" ){
					$("#nFolioContable").val( $("#nFolioAdefa").val() );
					$("#selectApliContable").val( "PAGADO" );
					aplicarContable();
				}
			}
			$("#esperar").hide();
			alert("Proceso Terminado");
		}
	}

	function aplicarContable(){
		$.ajax({
			url: './ejercidoPagadoValidar.jsp',
			type: 'post',
			async: false, 
			data: {tipo:'aplicarContable',tipoAplicacion:$("#selectApliContable").val() , nFolio:$("#nFolioContable").val() ,parametro:$("#condicion").val()},
			success: function(data){
				if(data.estatus == "correcto"){
					return true;
					//alert("Aplicado Correctamente");
					//location.reload();
				}else{
					return false;
					//alert("No Se Aplico Correctamente o Ya Esta Aplicado");
				}
			}
		});
	}
	
	
</script>	
  </head>
  <body id="dt_example">
	<form action="">
		<div  id="container">
			<h1>Ejercido Pagado<label style="font-size: 8pt"></label></h1>
			<input type="hidden" name="caNOcontrarrecibo" id="caNOcontrarrecibo" />
			<input type="hidden" name="cEstatusPagado" id="cEstatusPagado" />
			<input type="hidden" name="nFolioAdefa" id="nFolioAdefa" />
						
        	<div id="tabsl">    		
        		<table id="tblEjecidoPagado" align="center" width="1000px">	
        		<tr>
					<td>
						<div id="esperar" align="center">Espere por favor....
							<img border="0" src="../imagenes/espera.gif" height="30">
						</div>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>	
					<td><input type="button" id="btnAplicar" name="btnAplicar" value="Aplicar"></td>
					
				</tr>
        		<tr>	
					<td>
						<fieldset>
							<legend>Datos Cuentas Por Pagar</legend>
								<table id="movimientoCXP" class="display">
									<thead>
									<tr>
										<th>No CXP</th>
									</tr>
									</thead>							
									<tbody>
									</tbody>	
								</table>
						  </fieldset>	
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>				
				<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<fieldset>
							<legend id="lgndDetalles">Datos Para Aplicacion Contable</legend>
								<table id="tblAplicacion" align="center">
									<td>Tipo De Aplicacion Contable</td><td><select id="selectApliContable" name="selectApliContable"><option value="EJERCIDO">EJERCIDO</option><option value="PAGADO">PAGADO</option></select></td>
									<td>Numero Folio Cuentas Por Pagar</td><td><input type="text" name="nFolioContable" id="nFolioContable"></td>
									
									<td><input type="button" id="btnContable" name="btnContable" value="APLICAR" onclick="aplicarContable();" ></td>
								</table>
						</fieldset>		
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
			</table>				
		</div>
	</div>	
</form>
</body>
</html>
