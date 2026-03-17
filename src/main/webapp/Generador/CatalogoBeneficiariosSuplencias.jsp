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
		
	String formName = request.getParameter("formName")==null || "".equals(request.getParameter("formName"))? "formViaticos":request.getParameter("formName");

	String inputRFCTarget = request.getParameter("inputRFCTarget")==null || "".equals(request.getParameter("inputRFCTarget"))? "cIdRFC_Titular":request.getParameter("inputRFCTarget");
	String inputNoEmpTarget = request.getParameter("inputNoEmpTarget")==null || "".equals(request.getParameter("inputNoEmpTarget"))? "noEmpleadoTitular":request.getParameter("inputNoEmpTarget");
%>

<%@ page import="java.util.*,java.sql.*,com.syc.gestion.*,com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.Util" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Catalogo de Beneficiarios</title>

<!-- Estilos estandar para los controles JQuery -->

<link rel="stylesheet" type="text/css"	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>

<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript">
	
	var formName ="<%=formName%>";
	var inputRFCTarget = "<%=inputRFCTarget%>";
	var inputNoEmpTarget = "<%=inputNoEmpTarget%>";
		
	//READY
	$(document).ready(function(){
		
		$("#cnombre").val("");
		$("#cIdRFC").val("");
		$("#nEmpleado").val("");
		$("#btn_busca").button();
		$("#btn_limpia").button();
		$("#btn_aceptar").button();
		
		cargaGrid();
		
		$("#nEmpleado").focus();	
			
		$("#dt_Beneficiario tbody").click(function(event) {
			$(oTable.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});
			
			$(event.target.parentNode).addClass('row_selected');
			
			var aPos = oTable.fnGetPosition(event.target.parentNode);
     		var aData = oTable.fnGetData(aPos);
			$("#camporfc").val(aData[0]);
			$("#camponombre").val(aData[1]);
			$("#camponoEmp").val(aData[2]);
			$("#cUR").val(aData[3]);
			$("#cPlaza").val(aData[4]);
			$("#cNivel").val(aData[5]);
			$("#cIdRFC").val($("#camporfc").val());
			$("#cnombre").val($("#camponombre").val());
			$("#nEmpleado").val($("#camponoEmp").val());
			
		});
		
		$("#dt_Beneficiario tbody").dblclick(function(){
			enviar();
		});
	});
	
	function cargaGrid(){
		
		$("#cWhere").val(generaCondicion());
				
		oTable = $("#dt_Beneficiario").dataTable({
	        "bPaginate": true,
	        "iDisplayLength":"10",
   			"bFilter": false,
   			"bInfo" : false,
   			"bAutoWidth": false,
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			"bServerSide": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_Empleados&qw=" + " " + encodeURI($("#cWhere").val()),
			aoColumns: [
				{ sName: "cIDRFC"},
				{ sName: "cnombre"},
				{ sName: "nEmpleado"},
				{ sName: "cUR", visible:false},
				{ sName: "cPlaza", visible:false},
				{ sName: "cNivel", visible:false}
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
	
	function limpiarDatos(){
		$("#cIdRFC").val("");
		$("#cnombre").val("");
		$("#nEmpleado").val("");
		$("#cUR").val("");
		$("#cPlaza").val("");
		$("#cNivel").val("");
		
		document.getElementById("#nEmpleado").focus();
	
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
			
		$("#camponombre").val("");
		$("#camporfc").val("");
		$("#camponoEmp").val("");
		
		cargaGrid();
	}
	
	function generaCondicion(){
		var where = " 1 = 1 ";
		var token = " AND ";
		
		if( $("#cnombre").val() != "" ){
			where += token + " cnombre LIKE '%" + $.trim($("#cnombre").val()) + "%'";
		}
			
		if( $("#cIdRFC").val() != "" ){
			where += token + " cIDRFC LIKE '" + $.trim($("#cIdRFC").val()) + "%'";
		}
		
		if( $("#nEmpleado").val() != "" ){
			where += token + " nEmpleado LIKE '" + $.trim($("#nEmpleado").val()) + "%'";
		}
		return where;
	}
	
	function Buscar(){
		
		cargaGrid();
	}
	
	function selecciona() {

		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
	}
	
	function enviar(){
		if ($.trim($("#camponombre").val())=="" && $.trim($("#camporfc").val())=="" && $.trim($("#nEmpleado").val())==""){
			alert("Por favor Selecciona la fila del Beneficiario deseado.");
			return false;
		}
		
		
		eval( 'window.opener.' + formName  + '.' + inputRFCTarget + '.value=window.formBenef.camporfc.value' );
		eval( 'window.opener.' + formName  + '.' + inputNoEmpTarget + '.value=window.formBenef.camponoEmp.value' );
		
		window.close();
		
	}
	
</script>
	
</head>
	<body id="dt_example"> 
		<div  id="container" class="container-fluid">
	  		<form id="formBenef" name="formBenef">
	  			<input type="hidden" value="" name="camponombre" id="camponombre">
	  			<input type="hidden" value="" name="camporfc" id="camporfc">
	  			<input type="hidden" value="" name="campoRFC" id="campoRFC">
	  			<input type="hidden" value="" name="ctaBancaria" id="ctaBancaria">
	  			<input type="hidden" value="" name="camponoEmp" id="camponoEmp">
	  			<input type="hidden" value="" name="cWhere" id="cWhere">	
				<input type="hidden" value="" name="cUR" id="cUR">
				<input type="hidden" value="" name="cPlaza" id="cPlaza">	  
				<input type="hidden" value="" name="cNivel" id="cNivel">	  	  			  				  		
	  			
	  			<br>
	  			<div class= "card">
	  				<div class="card-header">
				    	Busqueda Empleado
					</div>
					<div class="card-body"> 
					<table>
						<tr>
		        			<td align="left">No Empleado:</td>
				        	<td>
				        		<input class="form-control" name="nEmpleado" type="text" id="nEmpleado" size="20" maxlength="15" style="text-transform:uppercase" onkeyup="Buscar()" onclick="selecciona()"/>
				        	</td>
		        		</tr>
			        	<tr>
		        			<td align="left">RFC:</td>
				        	<td>
				        		<input class="form-control" name="cIdRFC" type="text" id="cIdRFC" size="20" maxlength="15" style="text-transform:uppercase" onkeyup="Buscar()" onclick="selecciona()"/>
				        	</td>
		        		</tr>
			        	<tr>
				        	<td align="left">Nombre:</td>
				        	<td>
				        		<input class="form-control" name="cnombre" type="text" id="cnombre" size="50" style="text-transform:uppercase" onkeyup="Buscar()" onclick="selecciona()"/>
				        	</td>
			        	</tr>
		        	</table>
		        	<table align="right">
		        		<tr>
			        		<td>
				        		<input type="button" id="btn_busca" name="btn_busca" value="Buscar" onclick="Buscar()" class="btn btn-secondary"/>
				        	</td>
				        	<td>
				        		<input type="button" id="btn_limpia" name="btn_limpia" value="Limpiar" onclick="limpiarDatos()" class="btn btn-secondary"/>
				        	</td>
				        	<td>
				        		<input type="button" id="btn_aceptar" name="btn_aceptar" value="Aceptar" onclick="enviar()" class="btn btn-secondary"/>
				        	</td>
			        	</tr>
		        	</table>
		        	</div>
	        	</div>
	        	<br>
				<table id="dt_Beneficiario" class="display" >
					<thead>
						<tr>
							<th>RFC</th>
							<th>Nombre</th>
							<th>No Empleado</th>
							<th>UR</th>
							<th>Plaza</th>
							<th>Nivel</th>
						</tr>
					</thead>
				</table>
				
			</form>	
		</div>
	</body>	
</html>