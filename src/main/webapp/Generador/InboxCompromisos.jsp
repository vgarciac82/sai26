<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%

Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
if(c != null)
	session.removeAttribute( GestionInterface.ATT_CASE );
	
String cLogin = "";
String cUR = "";
String RFCUsuario = "";
String numeroEmpleado = "";
String tipoAutorizacion = StringUtils.trimToEmpty(  request.getParameter("TYPE") );

cLogin = usuario.getLogin();
cUR = usuario.getU_UR();
numeroEmpleado = usuario.getNumeroEmpleado();
RFCUsuario = usuario.getuRFC();

%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Inbox Compromisos</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
</head>
<body id="dt_example" style= "width: 95%">
	<div>
		<form id="inboxViaticos" name="inboxViaticos" >
			<input type="hidden" id="u_Login" 		name="u_Login" 		value="<%=cLogin%>"/>
			<input type="hidden" id="cUR" 			name="cUR" 			value="<%=cUR%>"/>
			<input type="hidden" id="nEmpleadoUsuario" name="nEmpleadoUsuario" value="<%=numeroEmpleado%>"/>
			<input type="hidden" id="cWhere" 		name="cWhere" 		value=" TipoPago = 'DI'"/>
			<input type="hidden" id="RFCUsuario" 	name="RFCUsuario" 	value=""/>
			<input type="hidden" id="idEmpleado" 	name="idEmpleado" 	value=""/>
			<input type="hidden" id="nFolios" 		name="nFolios" 		value=""/>
			<input type="hidden" id="idComision" 	name="idComision" 	value=""/>
			<input type="hidden" id="existenPagos" 	name="existenPagos" value=""/>
			<input type="hidden" id="folioCaso" 	name="folioCaso" 	value=""/>
			<input type="hidden" id="IDproyecto" 	name="IDproyecto" 	value=""/>
			<input type="hidden" id="nivel1" 		name="nivel1" 		value=""/>
			<input type="hidden" id="saldoPagos" 	name="saldoPagos" 	value=""/>
			<input type="hidden" id="cDocHaplicado" name="cDocHaplicado" value=""/>
			<input type="hidden" id="tieneRolPagos" name="tieneRolPagos" value=""/>
			<input type="hidden" id="tieneComSinViat" name="tieneComSinViat" value=""/>
			<input type="hidden" name="cTipoRfc" id="cTipoRfc" value=""/>
			<input type="hidden" name="cPlaza" id="cPlaza" value=""/>
			<input type="hidden" name="cNivel" id="cNivel" value=""/>
			<input type="hidden" name="cRFCEmpleado" id="cRFCEmpleado" value=""/>
			
				
			<div class="row mt-2">
				<div class="card-header text-center"> <h3> Inbox compromisos </h3> </div>
			</div>
			<div class="row mt-2 justify-content-center">
			    <div class="col-md-6 text-center">
			        <label for="uEjecutora" class="form-label">Unidad Ejecutora:</label> 
			        <select id="uEjecutora" name="uEjecutora" onchange="cambioUR();" class="form-select">
			        </select> 
			    </div>
			</div>
			<br/>
			<div class="row mt-2 justify-content-center">
				<div class="col-md-3 d-flex justify-content-center align-items-center gap-2">					
					<input type="checkbox" name="finalizados" id="finalizados" class="form-check-input" value = "S" onclick="filtroStatus()" />
					<label for="finalizados" class="form-check-label">Muestra Finalizados</label>
				</div>
				<div class="col-md-3 d-flex justify-content-center align-items-center gap-2">					
					<input type="checkbox" name="rechazados" id="rechazados" class="form-check-input" value = "C" onclick="filtroStatus()" />
					<label for="rechazados" class="form-check-label">Muestra Rechazados</label>
				</div>
			</div>
			
			<div class="row table-responsive text-nowrap">
				<table id="dt_listaCompromisos" class="table table-striped table-bordered">
					<thead>
						<tr class="encabezado">
							<th><font size="2">#</font></th>
							<th><font size="2">Contrato</font></th>
							<th><font size="2">CXP</font></th>
							<th><font size="2">RFC</font></th>
							<th><font size="2">Nombre</font></th>  
							<th><font size="2">Fecha</font></th>
							<th><font size="2">Estatus</font></th>
							<th><font size="2">Tipo</font></th>
							<th><font size="2">FolioSuf</font></th>
							<th><font size="2">FolioSICOP</font></th>
							<th><font size="2">Importe</font></th>
							<th><font size="2">Origen</font></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
		
		</form>
	</div>


		<script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
		<script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>	
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>	
		
		

	<script type="text/javascript" charset="utf-8">		
		var cUR = "<%=cUR%>";
		var cLogin = "<%=cLogin%>";
		var RFCUsuario = "<%=RFCUsuario%>";
		var numeroEmpleado = "<%=numeroEmpleado%>";
		

		$(document).ready(function() {
			
			$("#u_Login").val(cLogin);
			$("#RFCUsuario").val(RFCUsuario);
			$("#idEmpleado").val(numeroEmpleado);
			
			if($("#cUR").val()=="A02" || $("#cUR").val()=="A04"){
				querySelectPost("cURVistasViaticosTodos", "uEjecutora", { async : false });
			} else
				querySelectPost("cUnidadEjecutoraVistasViaticos", "uEjecutora", { async : false });
				
			$("#uEjecutora").val(cUR);
			$("#cWhere").val("cUnidadResponsable LIKE '" + $("#uEjecutora").val() + "' AND (nenviadosicopsuficiencia <> 2 OR nenviadosicop <> 2 ) " );
			cargaGrid();
			
		});

			
		var es_mx = {
				sProcessing : "Procesando...",
				sLengthMenu : "Mostrar _MENU_ registros",
				sZeroRecords : "No hay registros a mostrar",
				sEmptyTable : "No hay datos en la tabla",
				sLoadingRecords : "Cargando...",
				sInfo : "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty : "Registro 0 al 0 de 0",
				sInfoFiltered : "(filtered from _MAX_ total entries)",
				sInfoPostFix : "",
				sInfoThousands : ",",
				sSearch : "Filtro:",
				oPaginate : {
					sFirst : "Primero",
					sPrevious : "Ant.",
					sNext : "Sigte.",
					sLast : "&Uacute;ltimo"
				}	
			
			};
			

		var f ;
		
		function cambioUR() {
			$("#cWhere").val("cUnidadResponsable LIKE '" + $("#uEjecutora").val() + "' AND (nenviadosicopsuficiencia <> 2 OR nenviadosicop <> 2 ) " );
			cargaGrid();
		}
		
		function cargaGrid() {
			oTable = $("#dt_listaCompromisos").dataTable({
				"bPaginate": false,
				"bLengthChange": true,
				"bFilter": true,
				"bSort": true,
				"bInfo": true,
				"bAutoWidth": false,
				"bJQueryUI": true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers",
				"bServerSide": true,  
				"order": [[ 3, "desc" ]],
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[ 1 ] + "/crud?rt=t&ql=v_InboxCompromisos&qw=" + " " + encodeURI($("#cWhere").val()),
				aoColumns : [
					{
						sName : "nFolioCompromiso",
					},  {
						sName : "cIdContrato",
					}, 	{
						sName : "folioInterno",
					},	{
						sName : "cIdrfc",
					}, 	{
						sName : "nombre",
					},	{
						sName : "fAplicacion",
					},	{
						sName : "estadoSuficiencia",
					},	{
						sName : "cTipoContrato",
					},	{
						sName : "nFolioSuficiencia",
					},	{
						sName : "nFolioAutsicop",
					},	{
						sName : "Importe",
					},	{
						sName : "origen",
					}
				],
				oLanguage : es_mx
			});

			$("#dt_listaCompromisos tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function() {
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
			});
			
		}
		function filtroStatus(){
			if($("#finalizados").prop("checked") && $("#rechazados").prop("checked")) { //check finalizados y rechazados seleccionados		
					$("#cWhere").val("cUnidadResponsable LIKE '" + $("#uEjecutora").val() + "' AND ( ISNULL(cDocumentoHaplicado,'') IN ('C','N') or (nenviadosicopsuficiencia =2 AND nenviadosicop = 2 ) )" ) ;			
					cargaGrid();
			
			} else if($("#finalizados").prop("checked")) { //check finalizados seleccionado
					$("#cWhere").val("cUnidadResponsable LIKE '" + $("#uEjecutora").val() + "' AND cDocumentoHaplicado = 'S' AND nenviadosicopsuficiencia =2 AND nenviadosicop = 2 " );
					cargaGrid();
				
			} else if($("#rechazados").prop("checked")) { //check rechazados seleccionados
					$("#cWhere").val("cUnidadResponsable LIKE '" + $("#uEjecutora").val() + "' AND ISNULL(cDocumentoHaplicado,'') IN ('C','N') " );
					cargaGrid();
					
			} else {
					$("#cWhere").val("cUnidadResponsable LIKE '" + $("#uEjecutora").val() + "' AND ISNULL(cDocumentoHaplicado,'') NOT IN ('C','N') AND nenviadosicopsuficiencia !=2 AND nenviadosicop!= 2");					
					cargaGrid();
				
			}
		}
	</script>
  </body>
</html>
