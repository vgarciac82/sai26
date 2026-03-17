<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
	<title>Lista Layouts Beneficiarios</title>
	
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var isBoton1click = false;
		var isBoton2click = false;
		var isBoton3click = false;
		var isBoton4click = false;

		function inicio(){
			$('#envioP').val("");
			$('#cFolios').val("");
		}

		$(document).ready(function(){
		
			$("#buttonBen").button();
			$("#buttonDC").button();
			$("#buttonRB").button();
			$("#buttonCB").button();
			$("#buttonFin").button();
			
			$("#esperar").dialog({
				autoOpen : false,
				height : 110,
				width : 200,
				modal : true,
				open: function(event, ui){
					$(".ui-dialog-titlebar").hide();
				},
				close : function() {
				}
			});
			
			$("#chkTodos").change(function() {
				if ($("#chkTodos").prop("checked")) {
					$("input:checkbox").attr('checked', 'checked');
				}else{
					$("input:checkbox").removeAttr('checked');
				}	
			});
		});

		function fnClickAddRow(row) {
			var table2 = $('#dt_envioBeneficiarios').dataTable().fnAddData([	
								row.find('td:eq(1)').html()!=null?row.find('td:eq(1)').html():"",
								row.find('td:eq(2)').html()!=null?row.find('td:eq(2)').html():"",
								row.find('td:eq(3)').html()!=null?row.find('td:eq(3)').html():"",
								row.find('td:eq(4)').html()!=null?row.find('td:eq(4)').html():""
								
						]);
		}

		function fnClickDellRows(){
			$('#envioP').val("");
			$('#cFolios').val("");
			var table2 = $('#dt_envioBeneficiarios').dataTable().fnClearTable();				
		}

		function enviar(){
			try {
				fnClickDellRows();
					$('#tblBeneficiarios tbody tr input:checked').each(function(idx, elm){
						var cBeneficiario = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						var cFolio = $(this).parent('td').parent('tr').find('td:eq(5)').html();
						$('#envioP').val($('#envioP').val() + "'" + cBeneficiario + "',");
						$('#cFolios').val($('#cFolios').val() + "'" + cFolio + "',");
						fnClickAddRow($(this).parent('td').parent('tr'));
				});

				if($('#envioP').val() == ""){
					Swal.fire({ icon: "warning",
								text: "No se ha seleccionado ningún registro..."});														
				}
				
			}catch(e) {
				Swal.fire({ icon: "error",
							text: e});
				location.reload(true);
			}
		}

		function generarB(){
			isBoton1click = true;
			var filter = $('#envioP').val();
			if(filter != ""){
				document.location.href='../gstnmngr/LayoutBeneficiarios?envio=' + filter + '&archivo=1';
			}
			else{				
				Swal.fire({ icon: "error",
							text: "No puede generarse el layout del Beneficiario, no existe un registro seleccionado."});
			}
		}

		function generarBDC(){
			isBoton2click = true;
			var filter = $('#envioP').val();
			if(filter != ""){
				document.location.href='../gstnmngr/LayoutBeneficiarios?envio=' + filter + '&archivo=2';
			}
			else{
				Swal.fire({ icon: "error",
							text: "No puede generarse el layout de Documentación complementaria del Beneficiario, no existe un registro seleccionado."});				
			}
		}

		function generarRB(){
			isBoton3click = true;
			var filter = $('#envioP').val();
			if(filter != ""){
				document.location.href='../gstnmngr/LayoutBeneficiarios?envio=' + filter + '&archivo=3';
			}
			else{
				Swal.fire({ icon: "error",
							text: "No puede generarse el layout de Roles del Beneficiario, no existe un registro seleccionado."});				
			}
		}

		function generarBCB(){
			isBoton4click = true;
			var filter = $('#envioP').val();
			if(filter != ""){
				document.location.href='../gstnmngr/LayoutBeneficiarios?envio=' + filter + '&archivo=4';
			}
			else{
				Swal.fire({ icon: "error",
							text: "No puede generarse el layout de Cuentas Bancarias asociadas al Beneficiario, no existe un registro seleccionado."});				
			}
		
		}

		function cambiarEstatus(){
			$("#esperar").dialog("open");
			var mensaje = "Esta acci\u00f3n provocara cambios en la Base de Datos \n";
			if(isBoton1click != true){
				mensaje += "Aun no ha descargado el Layout de Beneficiarios\n";
			}
			if(isBoton2click != true){
				mensaje += "Aun no ha descargado el Layout de Documentaci\u00f3n Complementaria de Beneficiarios\n";
			}
			if(isBoton3click != true){
				mensaje += "Aun no ha descargado el Layout de Roles de Beneficiarios\n"; 
			}
			if(isBoton4click != true){
				mensaje += "Aun no ha descargado el Layout de Cuentas Bancarias asociadas a Beneficiarios\n"; 
			}

			mensaje += "¿Desea continuar?";

			if(confirm(mensaje)) {
				//$('#envioBeneficiariosSICOP').submit();
				if(finalizaDescarga()){
					$('#dt_envioBeneficiarios').dataTable().fnClearTable();
					location.reload(true);
				}
			}else{
				$("#esperar").dialog("close");
			}
		}
		function finalizaDescarga(){
			resp=false;
			var object=llenaObjectDat();
			$.ajax({url: "../gstnmngr/LayoutBeneficiarios" , type:'post' , async: false
				,data:object
				,dataType: 'json', success: 
					function(j){
					Swal.fire({ icon: "info",
								text: j[0].MENSAJE});						
					resp=j[0].RESPUESTA;
					$("#esperar").dialog("close");
				}, error: function( jqXHR, textStatus, errorThrown ) {
					$("#esperar").dialog("close");
				}
			});
			return resp;
		}
		function llenaObjectDat(){
			var data0= {
				envioP:$("#envioP").val(),
				cFolios:$("#cFolios").val()
			};
			return data0;
		}
		function muestraDialog(){
			$("#esperar").dialog("open");
		}
	</script>
	</head>
	<br/>
<body id="dt_example" onLoad="inicio();" >
	<div  id="container" class="ms-5" class="container" style="width: 90%">
		<div class="card-header"> <h3> Layout Beneficiarios </h3> </div>
			<div class="mt-3 row d-flex justify-content-center">
	      							
	       		<ul class="nav nav-tabs" id="list-opciones">
	       			 <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" onClick="javascript:location.reload(true);" data-bs-toggle="tab" data-bs-target="#tabs-1-layout" type="button" role="tab" aria-controls="tabs-layout" aria-selected="true">Beneficiarios sin Layout</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#tabs-2-enviar" type="button" role="tab" aria-controls="tabs-enviar" aria-selected="false">Beneficiarios para enviar a SICOP</button>
		            </li>								            
				</ul>
				
				<div class="tab-content mt-3" id="tabContent">		
					
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout">
						<fieldset>
							<jsp:include page="listaBeneficiarios.jsp"></jsp:include>
						</fieldset>	
					</div>
					
					<div class="tab-pane fade" id="tabs-2-enviar" role="tabpanel" aria-labelledby="tabs-enviar">
						<fieldset>
							<jsp:include page="listaBeneficiariosFiltrados.jsp"></jsp:include>
						</fieldset>
					</div>
				</div>
			</div>
			<div id="esperar">
				<fieldset>
					<table>
						<tr>
							<td>Espere por favor.... <img border="0"src="../imagenes/espera.gif" height="30"></td>
						</tr>
					</table>
				</fieldset>
			</div>
		</div>
	</body>
</html>