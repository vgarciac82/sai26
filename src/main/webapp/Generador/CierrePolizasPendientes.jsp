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
		<title>Cierre Pólizas Pendientes</title>

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

		<script type="text/javascript" charset="utf-8">
		var oTable;
		var sIdRFC;
		var szCrud = "/crud?rt=t&ql=vConsultaPolizaGestion";
		
		var lengParams = {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtado de _MAX_ registros)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Buscar:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}
			};
			

			
			
		$(document).ready(function() {
		
			$("#cbCentroContable")
                .change(function() {
                try
                {	
 					creaTblPCuentas();

                }              	
                catch (ex)
                {
                }
            });	
            
            $("#cbMesAplica")
                .change(function() {
                try
                {	
 					creaTblPCuentas();

                }              	
                catch (ex)
                {
                }
            });	
		
			querySelectPost("BALCTROCONTABLE", "cbCentroContable", {async : false});	
			
			$("#tblPolizasPendientes tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});

			oTable = $("#tblPolizasPendientes").dataTable({
				bAutoWidth : true,
				oLanguage: lengParams,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + szCrud,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cDocumento" 		,sWidth: "140px" },
					{ sName: "fCarga" 			, sClass: "alignCenter", sWidth:"100px" },
					{ sName: "fAplicacion" 		, sClass: "alignCenter", sWidth:"100px" },
					{ sName: "cCentroContable" 	, sClass: "alignCenter", sWidth:"60px" },
					{ sName: "cTipoPoliza" 		, sClass: "alignCenter", sWidth:"60px" },
					{ sName: "nFolioPoliza"		, sClass: "alignCenter", sWidth:"60px" },
					{ sName: "CO_RESPONSABLE" },
					{ sName: "nMesAplicacion"	, sClass: "alignCenter", sWidth:"60px" }
				]
        	});			
			
		});
		

		function creaTblPCuentas() {
		
			var szWhere = "";			
			
			if ($("#cbCentroContable").val() != '00') 
				szWhere =  "&qw=cCentroContable='" + $("#cbCentroContable").val() +"'";
			

			if ($("#cbMesAplica").val() != 0)
				if (szWhere == "")
					szWhere =  "&qw=nMesAplicacion=" + $("#cbMesAplica").val() ;
				else
					szWhere = szWhere +" AND nMesAplicacion=" + $("#cbMesAplica").val() ;

			var oSettings = oTable.fnSettings();
			
 			oSettings.sAjaxSource  = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + szCrud + szWhere;
		
			oTable.fnDraw(); 
	 	
		}


		
		</script>
		

</head>

<body id="dt_example" >
	<form> 
		<div id="container" class="container" style="width:1000px">	
			<h1>Consulta P&oacute;lizas Pendientes para Cierre</h1>
	
			<table width="100%" border="0">
				<tr>
					<td align="right" width="130px">Centro Contable:</td>
					<td align="left"  width="500px">
						<select id="cbCentroContable" style="width:100%">
						</select>
					</td>	
					<td align="right">Mes Aplicaci&oacute;n:</td>
					<td align="left"  width="70px">
						<select id="cbMesAplica" style="width:100%">
							<option value="0" selected>Todos</option>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
							<option value="11">11</option>
							<option value="12">12</option>
						</select>
					</td>			
				</tr>			
			</table>	
			<br/>
			<table id="tblPolizasPendientes" class="display"  >
	            <thead>
	                <tr>
	                	<th width="60px">Folio Documento</th>
	                	<th width="130px" align="center">Fecha Captura</th>
	                    <th width="130px" align="center">Fecha Aplicación</th>
	                    <th align="center">Centro Contable</th>
	                    <th align="center">Tipo P&oacute;liza</th>
	                    <th align="center">Folio P&oacute;liza</th>
	                    <th>Responsable</th>
	                    <th align="center">Mes Aplicación</th>
	                </tr>
	            </thead>
	        </table>
			<br/>
		</div>

		
	</form>
	</body>
</html>