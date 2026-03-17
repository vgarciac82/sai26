<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Descarga de Formatos de Adquisiciones</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  	<style type="text/css" title="currentStyle"> 
		@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
 
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
	
	<script type="text/javascript">
		var oTableConsulta;
	  	$(document).ready(function() {
	  		initTabla();
	  		setDblClck();
		});//Fin del document ready
		
		function  initTabla(){
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mFormatosGRM",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ],[1, "asc"]] ,
				aoColumns: [
					{sName: "nIdFormato"},
					{sName: "cNombreArchivo"},
					{sName: "cNombreMod"},
					{sName: "cURL",bVisible: false}
					
				]
			});
		}
		function setDblClck() {
            $("#tblConsulta tbody").dblclick(function(evt) {
                var aPos = oTableConsulta.fnGetPosition(evt.target.parentNode);
                if (aPos instanceof Array){
                	currIndex = aPos[0];	
                }else{
                	currIndex = aPos;	
                }
                var arr = oTableConsulta.fnGetData()[currIndex];
                var url = arr[3];
                window.open(url);
            });
        }
	</script>	
</head>
<body>
	<form action="" id="formDownloadFormatosADQ">
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12 mt-3">
	 			
	 			<div class= "card">
						<div class="card-header">
						   Doble clic para la descarga de formatos de adquisiciones
						</div>
						<div class="card-body"> 
							<div class="row">
				 				<div class="form-group row" style="width: 90%">
									<div class="col">
										
										<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
											<thead >
												<tr>
													<th align="center">#</th>
													<th align="center">Nombre</th>
													<th align="center">M&oacute;dulo</th>
													<th style="display: none;"></th>
												</tr>										
											</thead>
										</table>
									</div>
								</div>
							</div>
						</div>
	 			</div>
	 		</div>
	 	</div>
	
	</form>
</body>
</html>