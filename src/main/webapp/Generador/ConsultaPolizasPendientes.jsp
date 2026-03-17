<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cCentroContable = "";
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	if (usuario.getPropiedades() != null&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
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
	
var ready=false;
	//READY
	$(document).ready(function(){
		ready =true;
		$("#cCentroContable").val("");
		cargaGrid();
		
		document.getElementById("cCentroContable").focus();	
			
		$("#dt_polizasPendientes tbody").click(function(event){
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
				
		oTable = $("#dt_polizasPendientes").dataTable({
	        "bPaginate": true,
	        "iDisplayLength":"25",
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_POLIZAS_PENDIENTES&qw=" + " "+ encodeURI($("#cWhere").val()),//+"  ORDER BY CENTRO_CONTABLE,ESTATUS,FOLIO_POLIZA"),
			aoColumns: [
				{ sName: "FOLIO_DOCUMENTO",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "FOLIO_POLIZA",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "CONCEPTO",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "FECHA_CAPTURA",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "SUMAS_IGUALES",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "CENTRO_CONTABLE",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "USUARIO_CAPTURO",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "ESTATUS",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"}
				
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
		$("#cCentroContable").val("");
		
		document.getElementById("cCentroContable").focus();
		
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
		
		cargaGrid();
	}
	
	function generaCondicion(){
		var cCentroContable = $("#cCentroContable").val();
		var token="%";
		var where = "";


		if (ready==false)
			return where=" CENTRO_CONTABLE like  '"+cCentroContable+token+"'";
		else{
			
			ready=false;
			return "1=1";
			}
	}
	
</script>
	
</head>
	<body id="dt_example"> 
	<br/>
		<div  id="container" style="width: 80%" class="container">		
	  		<form id="formPolizaPendiente" name="formPolizaPendiente">
	  			<input name="cWhere" type="hidden" id="cWhere">
  			
				<div class="card-header"> <h3> Polizas directas inconclusas a nivel consolidado </h3> </div>
				<hr class="mt-3">
				
				<div class="row">
	        		<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="cCentroContable" class="form-label"> Centro Contable: </label>						
						<input type="text" name="cCentroContable" id="cCentroContable" class="form-control form-control-sm" onkeyup="cargaGrid()" placeholder="10"/>
					</div>					
				</div>
				
				<br/>
				
				<div class="row">
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">
						<input type="button" id="btn_busca" name="btn_busca" value="Buscar" class="btn btn-secondary btn-sm" onclick="cargaGrid()"/>								
						<input type="button" id="btn_limpia" name="btn_limpia" value="Limpiar" class="btn btn-secondary btn-sm" onclick="limpiarDatos()"/>								
					</div>						
				</div>
  				
  				<br/>
	  			        	
	        	<a href="../Generador/ConsultaPolizasPendientes.jsp" onclick="actionForm('CONTEXT_PATH','',this.id); toggleVerMain('none','smallCell', 'apMenu');" title="POLIZAS PENDIENTES" target="content-iframe" class="Menus" name="menu" id="mnu_polizas_incompletas"></a>
				<div id="polizas" class="table-responsive">	    
					<table id="dt_polizasPendientes" class="table table-striped">
						<thead>
							<tr>
								<th><font size="2">Folio de Documento</font></th>
								<th><font size="2">Folio de Poliza</font></th>
								<th><font size="2">Concepto</font></th>
								<th><font size="2">Fecha de Captura</font></th>
								<th><font size="2">Sumas Iguales</font></th>
								<th><font size="2">Centro Contable</font></th>
								<th><font size="2">Usuario Capturó</font></th>
								<th><font size="2">Status</font></th>
							</tr>
						</thead>
					</table>
				</div>												
			</form>	
		</div>
	</body>	
</html>