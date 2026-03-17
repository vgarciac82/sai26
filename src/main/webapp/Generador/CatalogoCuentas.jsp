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
	String archivoPadre=request.getParameter("nombreArchivoPadre")==null || "".equals(request.getParameter("nombreArchivoPadre"))? "":request.getParameter("nombreArchivoPadre");
	
	String formName = request.getParameter("formName")==null || "".equals(request.getParameter("formName"))? "formPagos":request.getParameter("formName");
	String inputName = request.getParameter("inputName")==null || "".equals(request.getParameter("inputName"))? "cTipoRfc":request.getParameter("inputName");
	
	String inputRFCTarget = request.getParameter("inputRFCTarget")==null || "".equals(request.getParameter("inputRFCTarget"))? "nRFC":request.getParameter("inputRFCTarget");
	String inputDCTABTarget = request.getParameter("inputDCTABTarget")==null || "".equals(request.getParameter("inputDCTABTarget"))? "nCTAB":request.getParameter("inputDCTABTarget");
	
	String DestinoGasto = request.getParameter("DESTINO_GASTO");
	String rfc = request.getParameter("RFC");
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Catalogo de Beneficiarios</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>
	
<style type="text/css" title="currentStyle">
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	@import "css/demo_table_jui.css";
	@import "css/demo_page.css";
	
	input[readOnly]{
		background-color:#f0f0f0;
		color:#333;
	}
</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"> </script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"> </script>
<script type="text/javascript">
	
	var formName ="<%=formName%>";
	var inputName ="<%=inputName%>";
	var inputDCTABTarget = "<%=inputDCTABTarget%>";
	var inputRFCTarget = "<%=inputRFCTarget%>";
	var archivoPadre= "<%=archivoPadre%>";	
	var destino = "<%=DestinoGasto == null ? "" : DestinoGasto%>";
	var rfc = "<%=rfc == null ? "" : rfc%>";

	//READY
	$(document).ready(function(){
		
		var tipo = "";
		eval( "tipo = window.opener."  + formName + "." + inputName + ".value");
		$("#tipo").val(tipo);
		
		$("#cCTA").val("");
		$("#cIdRFC_RelacionGasto").val("");		
		
		cargaGrid();
		
		document.getElementById("cIdRFC_RelacionGasto").focus();	
			
		$("#dt_Beneficiario tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			
			$(event.target.parentNode).addClass('row_selected');
			
			var aPos = oTable.fnGetPosition(event.target.parentNode);
     		var aData = oTable.fnGetData(aPos);
			$("#camporfc").val(aData[0]);
			$("#campocta").val(aData[1]);
			$("#cIdRFC_RelacionGasto").val($("#camporfc").val());
			$("#cCTA").val($("#campocta").val());
		});
		
		$("#dt_Beneficiario tbody").dblclick(function(){
			enviar();
		});
		
	});
	
	function cargaGrid(){
		
		// se agrega para saber que modulo lo esta llamando ya que en los modulos de pagos ( directos y relacion de gastos) no debe mostrar los beneficiarios de capitulo mil.
		var sV_CatalogoRFC = "v_CTA_BENEFICIARIO";
		
		if(formName != "FormContrato" && (destino == "RCRE" || destino == "CCRE")){//ARLA Se filtran solo los empleados a los que se les asigna la Caja Chica Devengado y Comprobacion
			sV_CatalogoRFC = "v_CTA_BENEFICIARIO";
		}		
		else if(formName != "FormContrato"){
			sV_CatalogoRFC = "v_CTA_BENEFICIARIO";
		}
						
		$("#cWhere").val(generaCondicion());
				
		oTable = $("#dt_Beneficiario").dataTable({
	        "bPaginate": true,
	        "iDisplayLength":"25",
   			"bLengthChange": true,
   			"bFilter": false,
   			"bSort": true,
   			"bInfo": true,
   			"bAutoWidth": false,
			"sScrollY": 300,
			"sScrollYInner": "100%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"sScrollX": "100%",
			"sScrollXInner": "100%",
			"bScrollCollapse": true,	
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+ sV_CatalogoRFC +"&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns: [
				{ sName: "cIDRFC",	bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
				{ sName: "cCTA",	bSearchable: false,	bSortable: true, bVisible: true, sClass: "alignLeft"}
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
   		//alert("Where: " + $("#cWhere").val());
   	}
	
	function generaCondicion(){
		var where = "";
		var token = "";
		
		if( $("#cCTA").val() != "" ){
			where += token + " cCTA LIKE '%" + $.trim($("#cCTA").val()) + "%'";
			token = " AND ";
		}
			
		if( $("#cIdRFC_RelacionGasto").val() != "" ){
			where += token + " cIDRFC LIKE '" + $.trim($("#cIdRFC_RelacionGasto").val()) + "%'"; 
			token = " AND ";
		}
		
		if( $("#tipo").val() != "" ){
			where += token + " cIDTipoRFC IN (" + $("#tipo").val() + ")";
			token = " AND ";
		}
		
		if ($("#destino").val() == "RCRE"){
			where += token + " cIDTipoRFC IN (" + $("#tipo").val() + ")";
			token = " AND ";
		}
		
		where += token + " cIDRFC LIKE '" + rfc + "%'";		
		
		return where;
	}
	
	function Buscar(){
		if ($.trim($("#cCTA").val())=="" && $.trim($("#cIdRFC_RelacionGasto").val())==""){
			alert("Para hacer una busqueda favor de ingresar datos ya sea en el RFC o en el Nombre.");
			return;
		}
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
		cargaGrid();
	}
	
	function enviar(){
		if ($.trim($("#campocta").val())=="" && $.trim($("#camporfc").val())==""){
			alert("Por favor Selecciona la fila del Beneficiario deseado.");
			return false;
		}
		
		var inputTrgt;
		
		eval( 'window.opener.' + formName + '.' + inputDCTABTarget + '.value=window.formBenef.campocta.value' );
		eval( 'window.opener.' + formName  + '.' + inputRFCTarget + '.value=window.formBenef.camporfc.value' );
		eval( 'inputTrgt = window.opener.' + formName  + '.' + inputRFCTarget  );
		
		if( inputTrgt.onchange ){
			inputTrgt.onchange();
		}	
		//verificar que archivo lo está llamando
		if(archivoPadre!="")
			window.opener.cat_CuentaBeneficiario();
	
		window.close();
	}
	
</script>
	
</head>
	<body id="dt_example"> 
		<div  id="container" >
	  		<form id="formBenef" name="formBenef">
	  			<input type="hidden" value="" name="tipo" id="tipo">
	  			<input type="hidden" value="" name="campocta" id="campocta">
	  			<input type="hidden" value="" name="camporfc" id="camporfc">
	  			<input type="hidden" value="" name="cWhere" id="cWhere">
	  			
	  			<br>
	  			<fieldset>
	  				<legend> Datos del Beneficiario </legend>
					<table>
			        	<tr>
		        			<td align="left">RFC:</td>
				        	<td>
				        		<input name="cIdRFC_RelacionGasto" type="text" id="cIdRFC_RelacionGasto" size="20" maxlength="15" style="text-transform:uppercase" readonly>
				        	</td>
		        		</tr>
			        	<tr>
				        	<td align="left">CUENTA BANCARIA:</td>
				        	<td>
				        		<input name="cCTA" type="text" id="cCTA" size="50" style="text-transform:uppercase" readonly>
				        	</td>
			        	</tr>
		        	</table>
		        	<table align="right">
		        		<tr>			        		
				        	<td>
				        		<input type="button" id="btn_aceptar" name="btn_aceptar" value="Aceptar" onclick="enviar()">
				        	</td>
			        	</tr>
		        	</table>
	        	</fieldset>
	        	<br>
				<table id="dt_Beneficiario" class="display" align="center">
					<thead>
						<tr>
							<th><font size="2">RFC</font></th>
							<th><font size="2">CUENTA BANCARIA</font></th>
						</tr>
					</thead>
				</table>
				
			</form>	
		</div>
	</body>	
</html>