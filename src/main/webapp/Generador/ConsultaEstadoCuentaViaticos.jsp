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
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Catalogo de Beneficiarios</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"> </script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"> </script>

<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
	

	//READY
	$(document).ready(function(){
		
		$("#nFolio").val("");
		$("#cIdRFC").val("");
		
		cargaGrid();
		
		document.getElementById("nFolio").focus();	
			
		$("#dt_SolicitudesEncabezado tbody").click(function(event){
			$(oTable.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			
			$(event.target.parentNode).addClass('row_selected');
			
			var aPos = oTable.fnGetPosition(event.target.parentNode);
     		var aData = oTable.fnGetData(aPos);			
     					
		});
		
		$("#btn_busca").button();
		$("#btn_limpia").button();
		
	});
	
	function cargaGrid(){
		
		$("#cWhere").val(generaCondicion());
				
		oTable = $("#dt_SolicitudesEncabezado").dataTable({
	        "bPaginate": true,
	        "iDisplayLength":"10",
   			"bLengthChange": true,
   			"bFilter": false,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": false,
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"bScrollCollapse": true,	
			"bServerSide": true,
			"fnInitComplete": function() {   
								oTable.fnAdjustColumnSizing();
			    			},
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_Solicitudes_Pendientes&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns: [
				{ sName: "nFolioCaja",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cDescripcionSolicitud",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "mMontoViaticos",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "mMontoRemanente",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "Nombre",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cBeneficiario",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cUnidadEjecutora",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros",
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
   	}
	
	function limpiarDatos(){
		$("#cIdRFC").val("");
		$("#nFolio").val("");
		
		document.getElementById("nFolio").focus();
		
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
		
		cargaGrid();
	}
	
	function generaCondicion(){
		var unidadEjec = $("#cUE").val();
		
		var where = "";
		var token = "";
		
		if (unidadEjec!="A02"){
			where += token + " cUnidadEjecutora = '"+unidadEjec+"'";
			token = " AND ";
		}
		
		if( $("#nFolio").val() != "" ){
			where += token + " nFolioCaja = " + $.trim($("#nFolio").val()) + " ";
			token = " AND ";
		}
			
		if( $("#cIdRFC").val() != "" ){
			where += token + " cBeneficiario LIKE '" + $.trim($("#cIdRFC").val()) + "%'"; 
			token = " AND ";
		}
		
		if (where!="")
			return where;
		else
			return "1=1";
	}
	
</script>
	
</head>
	<body id="dt_example"> 
	<br/>
		<div  id="container" style="width: 80%" class="container">
	  		<form id="formViaticos" name="formViaticos">
	  			<input name="cWhere" type="hidden" id="cWhere">
	  			<input name="cUE" type="hidden" id="cUE" value="<%=cUR%>">
	  				  			
				<div class="card-header"> <h4> Consulta Estado de Cuenta Viaticos </h4> </div>
				<hr class="mt-3">
								
				<div class="row">
	        		<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="nFolio" class="form-label"> Folio Solicitud: </label>						
						<input type="text" name="nFolio" id="nFolio" class="form-control form-control-sm" onclick="habilita('Integracion');" onfocus="habilita('Integracion');" placeholder="0"/>
					</div>
				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="cIdRFC" class="form-label"> RFC: </label>					
						<input type="text" name="cIdRFC" id="cIdRFC" class="form-control form-control-sm" onclick="habilita('CXP');" onfocus="habilita('CXP');" placeholder="AABC220101ABC"/>
					</div>
				</div>
				
				<br/>
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="button" id="btn_busca" name="btn_busca" value="Buscar" class="btn btn-secondary btn-sm" onclick="cargaGrid()"/>														
						<input type="button" id="btn_limpia" name="btn_limpia" value="Limpiar" class="btn btn-secondary btn-sm" onclick="limpiarDatos()"/>								
					</div>						
				</div>
				
				<br/>
				
	        	
	        	<br/>
	        	
	        	<div id="detalle" class="table-responsive">
		        	<table id="dt_SolicitudesEncabezado" class="table table-striped" >
						<thead>
							<tr>
								<th><font size="2">Folio</font></th>
								<th><font size="2">Descripcion</font></th>
								<th><font size="2">Monto Total</font></th>
								<th><font size="2">Remanente</font></th>
								<th><font size="2">Nombre</font></th>
								<th><font size="2">RFC</font></th>
								<th><font size="2">UE</font></th>
							</tr>
						</thead>
					</table>
				</div>	
			</form>	
		</div>
	</body>	
</html>