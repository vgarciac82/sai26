<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.core.Role"%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	
%>
<!DOCTYPE html>
<html>
<head>
<title>Montos de Actuación para el formato 14</title>
	<style type="text/css" title="currentStyle"> 
		@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
 		@import "../../css/interfaz.css";
	</style>
	<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
	
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap-dataTables/datatables.css"/>

	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.js"></script>

	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	<style type="text/css" title="currentStyle"> 
		legend.scheduler-border {
		    width:inherit; /* Or auto */
			padding:0 10px; /* To give a bit of padding on the left and right */
		    border-bottom:none;
		}
		
		legend.scheduler-border {
		    font-size: 1.2em !important;
		    font-weight: bold !important;
		    text-align: left !important;
		}
	</style>
	<script type="text/javascript">
		var oTableConsulta="";
		var myModal;
		$(document).ready(function() {
			initTabla();
		});
		function  initTabla(){
			var qw=' 1=1';
			
			oTableConsulta = $("#tblConsulta").dataTable({
				bScrollCollapse: true,
	    		bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_MontosActuacionObra&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 0, "desc" ]] ,
				aoColumns: [
					{sName: "cIdTAdjudicacion",bVisible: false},
					{sName: "cDescripSIPOT"},
					{sName: "mMontoMaximoObra"},
					{sName: "mMontoMaximoServicio"},
					{sName: "editar"}
					
				]
			});
		}
		function editaMontoAct(tipoAdj){
			myModal = new bootstrap.Modal(document.getElementById('modalActMontosAct'), {
			  keyboard: false
			})
			fillItemsDialog(tipoAdj);
			myModal.show();
		}
		function fillItemsDialog(tipoAdj){
			var aData = fnGetRowDataTable( oTableConsulta,tipoAdj );
			if(aData == null){
				swal("Error con la selección de datos.",{icon:"warning",button: "Cerrar"});
				return;
			}
			$("#cTipoAdjudicacion").val(aData[0]);
			$("#cDescripcion").val(aData[1]);
			$("#montoMaximoObra").val(aData[2]);
			$("#montoMaximoServicio").val(aData[3]);
		}
		function updateMounts(){
			if($("#cTipoAdjudicacion").val()==""){
				swal("Error con la selección de datos.",{icon:"warning",button: "Cerrar"});
				return;
			}
			queryFormPost("actualizaMontosActuacionObra,bitacoraMontosActuacionObra", {async: false,
				callback : function() 
				{
					swal("Datos actualizados.",{icon:"success",button: "Cerrar"});
				}
			});
			initTabla();
			$("#cTipoAdjudicacion").val('');
			myModal.hide();
		}
	</script>
</head>
<body>
	<form id="formMontosActuacion">
		<div class="container-fluid">
			<fieldset class="form-group border p-3">
	 			<legend class="w-auto px-2"> Montos de actuaci&oacute;n para obra p&uacute;blica</legend>
	 			
	 			<div class="form-group row" >
					<div class="col">
						<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">cIdTAdjudicacion</th>
									<th align="center">Descripci&oacute;n</th>
									<th align="center">Monto M&aacute;ximo de Obra</th>
									<th align="center">Monto M&aacute;ximo de Servicio</th>
									<th align="center"></th>
								</tr>										
							</thead>
						</table>
						
					</div>
				</div>
				<!-- Modal -->
				<div class="modal fade" id="modalActMontosAct" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
				  <div class="modal-dialog">
				    <div class="modal-content">
				      <div class="modal-header">
				        <h5 class="modal-title" id="exampleModalLabel">Actualizaci&oacute;n de montos</h5>
				        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				      </div>
				      <div class="modal-body">
				        	<div class="form-group row" >
			        			<div class="col-12 ">
  									<label for="cDescripcion" class="form-label">Descripci&oacute;n</label>
									<input type="text" class="form-control" id="cDescripcion" name="cDescripcion" readonly="readonly">
			        			</div>
			        		</div>
			        		<div class="form-group row" >
								<div class="col-6 ">
  									<label for="montoMaximoObra" class="form-label">Monto M&aacute;ximo de obra</label>
									<input type="text" class="form-control" id="montoMaximoObra" name="montoMaximoObra" placeholder="$ 0.00" onKeyPress="return(onlyDoubles(event));" />
			        			</div>
			        			<div class="col-6">
  									<label for="montoMaximoServicio" class="form-label">Monto M&aacute;ximo de servicio</label>
									<input type="text" class="form-control" id="montoMaximoServicio" name="montoMaximoServicio" placeholder="$ 0.00" onKeyPress="return(onlyDoubles(event));" />
			        			</div>
				        	</div>
				      </div>
				      <div class="modal-footer">
				        <button type="button" class="btn btn-primary" id="btnUpdateMounts" name="btnUpdateMounts" onclick="updateMounts();">Actualizar</button>
				      </div>
				    </div>
				  </div>
				</div>
	 		</fieldset>
		</div>
		<input type="hidden" value="" id="cTipoAdjudicacion" name="cTipoAdjudicacion" />
		<input type="hidden" value="<%=name_user%>" id="cLogin" name="cLogin" />
	</form>
</body>
</html>