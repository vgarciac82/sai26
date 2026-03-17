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
	<title>Lista Layouts Procesos de Fin de Anio</title>
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

		function inicio(){
			$('#evento').val("");
			$('#cFolios').val("");
			$('#cTipoPago').val("");
		}

		$(document).ready(function(){
			$("#tabs").tabs( {
				"show": function(event, ui) {
					var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
					if ( oTable.length > 0 ) {
						oTable.fnAdjustColumnSizing();
					}
				}
			} );
			
			$("#buttonLayOut").button();
			
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
			
			$("#checkAll").change(function(){
				var table = document.getElementById('tblListadoPagos');
				var aTrs = $('#tblListadoPagos').dataTable().fnGetNodes();
				if ($('#checkAll').is(':checked')){
					$("input:checkbox").attr('checked', 'checked');
			    }else{
			    	$("input:checkbox").removeAttr('checked');
			    }
			});
		});

		function fnClickAddRow(row) {
			var table2 = $('#tbl_envioListadoPagos').dataTable().fnAddData([	
							row.find('td:eq(2)').html()!=null?row.find('td:eq(2)').html():"",
							row.find('td:eq(3)').html()!=null?row.find('td:eq(3)').html():"",
							row.find('td:eq(5)').html()!=null?row.find('td:eq(5)').html():"",
							row.find('td:eq(6)').html()!=null?row.find('td:eq(6)').html():"",
							row.find('td:eq(7)').html()!=null?row.find('td:eq(7)').html():"",
							row.find('td:eq(8)').html()!=null?row.find('td:eq(8)').html():"",
							row.find('td:eq(9)').html()!=null?row.find('td:eq(9)').html():"",
							row.find('td:eq(10)').html()!=null?row.find('td:eq(10)').html():""								
						]);
		}

		function fnClickDellRows(){
			$('#evento').val("");
			$('#cFolios').val("");
			$('#cTipoPago').val("");
			var table2 = $('#tbl_envioListadoPagos').dataTable().fnClearTable();				
		}

		function enviar(){
			try {
				fnClickDellRows();
					$('#tblListadoPagos tbody tr input:checked').each(function(idx, elm){
						var cEvento = $(this).parent('td').parent('tr').find('td:eq(10)').html();
						var cFolio = $(this).parent('td').parent('tr').find('td:eq(2)').html();
						var cTipoPago = $(this).parent('td').parent('tr').find('td:eq(3)').html();
						$('#evento').val($('#evento').val() + "'" + cEvento + "',");
						$('#cFolios').val($('#cFolios').val() + "'" + cFolio + "',");
						$('#cTipoPago').val($('#cTipoPago').val() + "'" + cTipoPago + "',");
						fnClickAddRow($(this).parent('td').parent('tr'));
				});

				if($('#evento').val() == ""){
					Swal.fire({ icon: "warning",
								text: "No se ha seleccionado ningún registro..."});										
				}
				
			}catch(e) {
				Swal.fire({ icon: "error",
							text: e});
			}
		}

		function generar(){
			isBoton1click = true;
			var filter = $("#evento").val();
			var folios = $("#cFolios").val();
			var tipoPago = $("#cTipoPago").val();
			if(filter != "" && folios != ""){
				document.location.href='../gstnmngr/LayoutProcesosFinAnio?evento=' + filter + '&cFolio=' + folios + '&cTipoPago=' + tipoPago +'&archivo=1';
			}
			else{
				Swal.fire({ icon: "warning",
							text: "No puede generarse el layout, no existe un registro seleccionado"});				
			}
		}

		function muestraDialog(){
			$("#esperar").dialog("open");
		}
	</script>
	</head>
	<br/>
	<body id="dt_example" onLoad="inicio();" >
		<div id="container" class="ms-5" class="container ms-5" style="width: 90%">
			<div class="card-header"> <h3> Tramites Fin de Año a Comprobar </h3> </div>
			<hr class="mt-3"/>
		
			<div class="row d-flex justify-content-center">
	      							
	       		<ul class="nav nav-tabs" id="list-opciones">
	       			 <li class="nav-item" role="presentation">
		            	<button class="nav-link active" id="tabs-1" onClick="javascript:location.reload(true);" data-bs-toggle="tab" data-bs-target="#tabs-1-pendientes" type="button" role="tab" aria-controls="tabs-pendientes" aria-selected="true">Procesos sin Layout</button>
		            </li>
		            <li class="nav-item" role="presentation">
		            	<button class="nav-link" id="tabs-2" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#tabs-2-proceso" type="button" role="tab" aria-controls="tabs-proceso" aria-selected="false">Para Generar Lay Out</button>
		            </li>								            
				</ul>
				
				<div class="tab-content mt-3" id="tabContent">		
					<div class="tab-pane fade show active" id="tabs-1-pendientes" role="tabpanel" aria-labelledby="tabs-pendientes">
						
						<div class="row d-flex">								
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
			          			<input type="checkbox" id="checkAll" name="checkAll">Todos
			          		</div>
			          	</div>		
			          	
						<fieldset>
							<jsp:include page="listaProcesosFinDeAnio.jsp"></jsp:include>
						</fieldset>	
					</div>
				
					<div class="tab-pane fade" id="tabs-2-proceso" role="tabpanel" aria-labelledby="tabs-proceso">
						<fieldset>
							<jsp:include page="listaProcesosFinDeAnioFiltrados.jsp"></jsp:include>
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