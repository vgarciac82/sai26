<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>

<% 	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
	<title>Lista Cuentas Bancarias</title>
	
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
		function inicio(){
			$('#envioP').val("");
			$('#cFolios').val("");
		}

		$(document).ready(function(){			

			$("#generaCuentas").button();
			
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
			try {
				
				var table2 = $('#dt_envioCuentasB').dataTable().fnAddData( [	
						row.cells[1].innerHTML,
	      				row.cells[2].innerHTML,
	      				row.cells[3].innerHTML,
	      				row.cells[4].innerHTML,
	      				row.cells[5].innerHTML,
	      				row.cells[6].innerHTML	      				
	      	            ] );		
			}
			catch (ex)
			{Swal.fire({ icon: "error",
						 text: ex.message});				
				}
		}
		
		function fnClickDellRows(){
		    $('#envioP').val("");
		    $('#cFolios').val("");
			var table2 = $('#dt_envioCuentasB').dataTable().fnClearTable();				
		}

		function enviar(){
			try {
				fnClickDellRows();
        		var table = document.getElementById('tblCuentasBancariasB');
 				var aTrs = $('#tblCuentasBancariasB').dataTable().fnGetNodes();
				
 				for ( var i=1; i<=aTrs.length;  i++ ){   
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];
					
					if(null != chkbox && true == chkbox.checked){ 					
						var BeneficiarioCuentasBancarias = "'" + row.cells[1].innerHTML + "'|'" + row.cells[2].innerHTML + "'|'" + row.cells[4].innerHTML + "'"; // el 11 corresponde al folio
						var cFolio = "'" + row.cells[7].innerHTML + "'"; // el 11 corresponde al folio
						
						$('#envioP').val($('#envioP').val() + BeneficiarioCuentasBancarias + ",");
						$('#cFolios').val($('#cFolios').val() + cFolio + ",");
												
			      		fnClickAddRow(row);
			    	}
				}
 				
				

			}catch(e) {
				Swal.fire({ icon: "error",
							text: e});				
				location.reload(true);
			}
		}

		function generarLCuentasB(){
			try {
				$('#envioCuentasBSICOP').submit();
				$('#dt_envioCuentasB').dataTable().fnClearTable();				
			}catch(e) {
				Swal.fire({ icon: "error",
							text: e});
			}
		}
			
	</script>
	</head>
	<br/>
<body id="dt_example" onLoad="inicio();" >
	<div  id="container" class="ms-5" class="container" style="width: 90%">
		<div class="card-header"> <h3> Layout Cuentas Bancarias</h3> </div>
			<div class="mt-3 row d-flex justify-content-center">		
	       		<ul class="nav nav-tabs" id="list-opciones">
	       			 <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" onClick="javascript:location.reload(true);" data-bs-toggle="tab" data-bs-target="#tabs-1-layout" type="button" role="tab" aria-controls="tabs-layout" aria-selected="true">Cuentas Bancarias asociadas a Beneficiarios</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#tabs-2-enviar" type="button" role="tab" aria-controls="tabs-enviar" aria-selected="false">Cuentas Bancarias para enviar a SICOP</button>
		            </li>								            
				</ul>
				
				<div class="tab-content mt-3" id="tabContent">					
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout">
						<fieldset>
							<jsp:include page="listaCuentasBancarias.jsp"></jsp:include>
						</fieldset>	
					</div>
					
					<div class="tab-pane fade" id="tabs-2-enviar" role="tabpanel" aria-labelledby="tabs-enviar">
						<fieldset>
							<jsp:include page="listaCuentasBFiltradas.jsp"></jsp:include>
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