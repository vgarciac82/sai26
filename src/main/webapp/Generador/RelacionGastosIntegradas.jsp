<%@ page 
	import="java.util.Calendar"
	import="com.syc.gestion.core.Usuario"
	import="com.syc.gestion.servlet.GestionInterface"
%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String login=usuario.getLogin();
	
	String cUR = "";
	
	if (usuario == null) {
		System.out.println("entro");
		response.sendRedirect("../index.jsp");
		return;
	}
	cUR = usuario.getU_UR();

%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Relaci&oacute;n de Gastos Integradas</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/datatables.min.css"></link>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
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
	
	var oTableIntegrados;
	var oTableIntD;
	
	//READY		
	$(document).ready(function(){
		$( "#cIdUnidadEjecutora" ).val('<%=cUR%>');
		querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
		if ('<%=cUR%>' != 'A02'){
			$("#cIdUnidadEjecutora").attr("disabled", true);
		}
		dtLoad($("#UR").val(),$("#bsq_campo").val(),$("#bsq_valor").val());
		
		$("#cIdUnidadEjecutora").change(function(){
			$("#UR").val($("#cIdUnidadEjecutora").val());
			limpiarDatos();
		});
		$("#dt_Detalle tbody").click(function(event) {
			$(oTableIntD.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
			
		});
		
		$("#btn_busca").button();
		$("#btn_limpia").button();
	});
	
	function dtLoad(UR,campo,valor){
		//ENCABEZADO
		$('#dt_Integrados').dataTable({
	        "bPaginate": true,
   			"bLengthChange": true,
   			"bFilter": false,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": false,
			"sScrollY": 270,
			"sScrollYInner": "100%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"sScrollXInner": "100%",
			"bScrollCollapse": true,	
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vEncabezadoIntegracionRG&qw= NoIntegracion = CASE '" +campo+ "' WHEN 'Integracion' THEN '"+valor+ "' ELSE (SELECT DISTINCT NoIntegracion FROM dbo.vDetalleIntegracionRG WHERE CXP='"+valor+"') END",
			aoColumns: [
				{ sName: "UR",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "NoIntegracion",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "nFolioAutSICOP",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "CBEN",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "CtaBancRFC",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "CuentaBancaria",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "NombreCtaBanc",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "Importe",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
				{ sName: "fCreacionLayout",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
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
   		
   		//DETALLE
		oTableIntD = $('#dt_Detalle').dataTable({
	        "bPaginate": true,
   			"bLengthChange": true,
   			"bFilter": false,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": true,
			"sScrollY": 270,
			"sScrollYInner": "100%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"sScrollXInner": "100%",
			"bScrollCollapse": true,	
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDetalleIntegracionRG&qw= NoIntegracion = CASE '"+campo+ "' WHEN 'Integracion' THEN '"+valor+ "' ELSE (SELECT DISTINCT NoIntegracion FROM dbo.vDetalleIntegracionRG WHERE CXP='"+valor+"') END",
			aoColumns: [
				{ sName: "UR",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "CXP",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "FolioRG",				bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "Beneficiario",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "BeneficiarioRFC",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "Concepto",			bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "EP",					bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
				{ sName: "mMonto",		bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
				{ sName: "fechaAplicacionCXP",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"}
			],
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "Mostrar _MENU_ registros",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No se encontraron resultados con esos filtros", //"No hay datos en la tabla",
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
	
	
		function valFecha(txtFecha) 
	{
		
	   buscar();
	   //dtLoad($("#UR").val(),$("#FechaInicial").val(),$("#FechaFinal").val(),$("#bsq_campo").val(),$("#bsq_valor").val());
	}
	
	function buscar(){
		if ($("#bsq_campo").val()=="UNIDAD_EJECUTORA"){
			alert("Selecciona un campo de búsqueda");
			return;
		}
		else{
			if ($("#bsq_campo").val()=="Integracion")
				$("#bsq_valor").val($("#BusqIntegra").val());
			else
				$("#bsq_valor").val($("#BusquedaCxp").val());
			
			if ($.trim($("#bsq_valor").val())==""){
				if ($("#bsq_campo").val()=="Integracion"){
					$("#BusqIntegra").val("");
					alert("Favor de Capturar el No. de Integración que deseas buscar!");
					document.getElementById("BusqIntegra").focus();
				}
				else{
					$("#BusquedaCxp").val("");
					alert("Favor de Capturar la CxP que deseas buscar!");
					document.getElementById("BusquedaCxp").focus();
				}
				return;
			}
			dtLoad($("#UR").val(),$("#bsq_campo").val(),$("#bsq_valor").val());
		}
		
		$("#dt_Detalle tbody").show();
	}
	
	function habilita(op){
		$("#bsq_campo").val(op);
		if (op=="Integracion"){		
			$("#BusqIntegra").removeClass("notEditable");
			$("#BusqIntegra").addClass("Editable");
			
			$("#BusquedaCxp").removeClass("Editable");			
			$("#BusquedaCxp").addClass("notEditable");		
		}
		else{			
			$("#BusquedaCxp").removeClass("notEditable");
			$("#BusquedaCxp").addClass("Editable");
			
			$("#BusqIntegra").removeClass("Editable");			
			$("#BusqIntegra").addClass("notEditable");		
		}
	}
	
	function limpiarDatos(){
		if ($("#bsq_campo").val()=="Integracion"){
			$("#BusqIntegra").removeClass("Editable");
			$("#BusqIntegra").addClass("notEditable");
		}
		else{
			$("#BusquedaCxp").removeClass("Editable");
			$("#BusquedaCxp").addClass("notEditable");
		}
		$("#bsq_campo").val("UNIDAD_EJECUTORA");
		$("#bsq_valor").val("");
		$("#BusqIntegra").val("");
		$("#BusquedaCxp").val("");
		cargafecha();
		dtLoad($("#UR").val(),$("#bsq_campo").val(),$("#bsq_valor").val());	
	}

</script>
	
</head>
	<body id="dt_example" >
	<br/>  	
		<div  id="container" style="width:80%" class="container">
	  		<form>
	  			<input type="hidden" value="UNIDAD_EJECUTORA" id="bsq_campo" name="bsq_campo">
	  			<input type="hidden" value="" id="bsq_valor" name="bsq_valor">
	  			<input type="hidden" value="<%=cUR%>" id="UR" name="UR">
	  			<input type="hidden" value="<%=cUR%>" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora">
        	
				<div class="card-header"> <h3> Consulta de Relaci&oacute;n de Gastos Integradas </h3> </div>
				<hr class="mt-3">
				
	        	<div class="row">
	        		<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="BusqIntegra" class="form-label"> No. de Integración: </label>						
						<input type="text" name="BusqIntegra" id="BusqIntegra" class="form-control form-control-sm" onclick="habilita('Integracion');" onfocus="habilita('Integracion');" placeholder="A01000000000000"/>
					</div>
				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="BusquedaCxp" class="form-label"> CxP: </label>					
						<input type="text" name="BusquedaCxp" id="BusquedaCxp" class="form-control form-control-sm" onclick="habilita('CXP');" onfocus="habilita('CXP');" placeholder="10CP0000000000"/>
					</div>
				</div>
				
				<br/>
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<input type="button" id="btn_busca" name="btn_busca" value="Buscar" class="btn btn-secondary btn-sm" onclick="buscar()"/>													
						<input type="button" id="btn_limpia" name="btn_limpia" value="Limpiar" class="btn btn-secondary btn-sm" onclick="limpiarDatos()"/>								
					</div>						
				</div>
			       
		        <br/>
		        
				<h5> Integraci&oacute;n </h5>
				<hr class="mt-3">
				
				<div id="dv_integra">
					<div id="integrada" class="table-responsive">
						<table id="dt_Integrados" class="table table-striped" cellspacing="0" cellpadding="2" align="center">
							<thead>
								<tr>
									<th><font size="2">UR</font></th>
									<th><font size="2">Folio Integraci&oacute;n</font></th>
									<th><font size="2">No. Compromiso</font></th>
									<th><font size="2">Clave Beneficiario</font></th>
									<th><font size="2">RFC</font></th>
									<th><font size="2">Cuenta</font></th>
									<th><font size="2">Nombre</font></th>
									<th><font size="2">Importe</font></th>
									<th><font size="2">Fecha Layout</font></th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
				
				<br/>
				
				<h5> Detalle de la Integraci&oacute;n </h5>
				<hr class="mt-3">
				
				<div id="detalle" class="table-responsive">
					<table id="dt_Detalle" class="table table-striped" align="center">
						<thead>
							<tr>
								<th><font size="2">UR</font></th>
								<th><font size="2">CxP</font></th>
								<th><font size="2">Folio</font></th>
								<th><font size="2">Nombre</font></th>
								<th><font size="2">RFC</font></th>
								<th><font size="2">Concepto</font></th>
								<th><font size="2">EP</font></th>
								<th><font size="2">Importe</font></th>
								<th><font size="2">Fecha CxP</font></th>
							</tr>
						</thead>
					</table>
				</div>
				
				<br/>
				
			</form>	
		</div>
	</body>	
</html>