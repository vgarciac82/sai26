<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String campoNCuenta = StringUtils.isEmpty( request.getParameter("campoNCuenta") )? "nCuenta" : request.getParameter("campoNCuenta");
	String campoDCuenta =  StringUtils.isEmpty( request.getParameter("campoDCuenta") )? "dCuenta" : request.getParameter("campoDCuenta");
	String campoSubCuenta =  StringUtils.isEmpty( request.getParameter("campoSubCuenta") )? "nSubCuenta" : request.getParameter("campoSubCuenta");
	String campoCuentaAplicacion =  StringUtils.isEmpty( request.getParameter("campoCuentaAplicacion") )? "nCuentaAplicacion" : request.getParameter("campoCuentaAplicacion");
	String vista = request.getParameter("vista");
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Catalogo de Cuentas.</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>


<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script type="text/javascript">
	var campoNCuenta = "<%=campoNCuenta%>";
	var campoDCuenta = "<%=campoDCuenta%>";
	var campoSubCuenta = "<%=campoSubCuenta%>";
	var campoCuentaAplicacion = "<%=campoCuentaAplicacion%>";
	
	var oTable;
	
	$(document).ready(
		function() {

				$("#buscarBtn").button().click(function() {
					buscar();
				});

				$("#limpiarBtn").button().click(function() {
					limpiar();
				});
				
				creaDT();
		}
	);
	
	function creaDT( condicion ){
	
		if(!condicion)
			condicion = "1=1";
			
		oTable = $('#dt_catalogo').dataTable({         
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
					},
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=<%=vista%>&qw=" + condicion,
					bProcessing: true,					
		            bAutoWidth : true,
					bRetrive: true,
					bDestroy: true,
		    		bPaginate: true,
		    		sScrollX: "100%",		      		
          			bLengthChange : true,
					bInfo : true,
					bFilter : true,
					bSort : true, 
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
					    { sName: "ncuenta",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter" },
						{ sName: "dcuenta",		bSearchable: false,	bSortable: true, bVisible: true, sClass: "alignCenter"},
						{ sName: "cSubcuenta",	bSearchable: false,	bSortable: true, bVisible: false, sClass: "alignCenter"},
						{ sName: "nCuentaAplicacion",	bSearchable: false,	bSortable: true, bVisible: false, sClass: "alignCenter"}
					]
        });
        
        $("#dt_catalogo tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(
				function (){
					$(this.nTr).removeClass('row_selected');
				});
			
			$(event.target.parentNode).addClass('row_selected');
		});
		
		$("#dt_catalogo tbody").dblclick( function( e ) {
			$(oTable.fnSettings().aoData).each(
				function (){
					$(this.nTr).removeClass('row_selected');
			});
				
			$(e.target.parentNode).addClass('row_selected');
			
			seleccionaCuenta(e);
			
		});
        		
	}
	
	function seleccionaCuenta(event){
		var aPos = oTable.fnGetPosition(event.target.parentNode);
		var aData = oTable.fnGetData(aPos);
		
		var nCuentaVal = aData[0];
		var dCuentaVal = aData[1]; 
		var subCuentaVal = aData[2];  
		var nCuentaAplicacion = aData[3];
		 
		window.opener.document.getElementById(campoNCuenta).value=nCuentaVal;
		window.opener.document.getElementById(campoDCuenta).value=dCuentaVal;
		window.opener.document.getElementById(campoSubCuenta).value=subCuentaVal;
		window.opener.document.getElementById(campoCuentaAplicacion).value=nCuentaAplicacion;
		
		window.opener.document.getElementById(campoNCuenta).onchange();
		
		window.close();
		
	}
	
	function buscar(){
		var condicion = "";
		var token = "";
		
		if( $("#nCuenta").val() != "" ){
			condicion += " nCuenta LIKE '" + $("#nCuenta").val() + "%25' ";
			token = " AND ";
		}
		
		if( $("#dCuenta").val() != "" ){
			condicion += token + " dCuenta LIKE '" + $("#dCuenta").val() + "%25'";
		}
		
		creaDT( condicion );
	}
	
	function limpiar(){
		$("#nCuenta").val("");
		$("#dCuenta").val("");
	}
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form>
		<div id="container" class="container">
			<h5> Catalogo de Cuentas Contables </h5>
			<hr class="mt-3">	
			
			<div class="row d-flex justify-content">				
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<label for="nCuenta"> Cuenta: </label>															
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" id="nCuenta" name="nCuenta" value="" class="form-control form-control-sm">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<label for="dCuenta"> Descripcion: </label>															
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					<input type="text" id="dCuenta" name="dCuenta" value="" class="form-control form-control-sm">
				</div>
			</div>
			
			<div class="row d-flex justify-content">				
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="buscarBtn" value="Buscar" class="btn btn-secondary btn-sm"> 															
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="button" id="limpiarBtn" value="Limpiar" class="btn btn-secondary btn-sm">
				</div>
			</div>

			<b>Doble clic en un renglon para seleccionar la cuenta</b>

			<div id="dv" class="container">
				<table id="dt_catalogo" class="table table-striped" >
					<thead>
						<tr>
							<th>Cuenta</th>
							<th>Descripcion</th>
							<th>Subcuenta</th>
							<th>Cuenta Aplicacion</th>
						</tr>
					</thead>
				</table>
			</div>

		</div>
	</form>
</body>
</html>