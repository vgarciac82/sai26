<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
	Iterator it1 = rol.entrySet().iterator();
	String role="";
	String roles="";
	while (it1.hasNext()) {
		Map.Entry r = (Map.Entry)it1.next();
		role=(String)r.getKey();
		roles += r.getKey().toString()+",";
	}
 %>

<!DOCTYPE html>
<html>
  <head>
    
    <title>Bitacora Materiales</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
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
	
	<script type="text/javascript">
		var oTableConsulta="";
		$(document).ready(function() {
			roles="<%=roles%>";
			agregaDatePickerFechas();
			$("#buscarRecepMat").button();
			if (roles.indexOf("ADMIN_RECMAT") >= 0 || roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0){
				$("#isAdmin").val(0);
				querySelectPost("UnidadBusca2", "cIdUnidadEjecutora", {async: false,
					callback : function() {
						$("#cIdUnidadEjecutora").val("<%=usuario.getU_UR()%>");
						initTabla();
					}	
				});
			}else{
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
					callback : function() {
						$("#cIdUnidadEjecutora").val("<%=usuario.getU_UR()%>");
						initTabla();
					}
				});
			}
		});
		function  initTabla(){
			var qw="1=1 ";
			if($("#cIdUnidadEjecutora").val()!="0"){
				qw=" cIdDocumento like'%25"+$("#cIdUnidadEjecutora").val()+"%25'";	
			}
			var rangoFechas=" and fRegistro BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";
			qw=qw+rangoFechas;
			if($("#u_login").val()!=''){
				qw=qw+" and cIdUsuario LIKE '%25"+$("#u_login_search").val()+"%25'";
			}
			if($("#documento").val()!=''){
				qw=qw+" and cIdDocumento LIKE '%25"+$("#documento").val()+"%25'";
			}
			oTableConsulta = $("#tblConsulta").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mBitacoraMovimientos&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ],[1, "asc"]] ,
				aoColumns: [
					{sName: "nIdMovimiento",bVisible: false},
					{sName: "cIdDocumento"},
					{sName: "cAccion"},
					{sName: "cIdUsuario"},
					{sName: "fRegistro"}
					
				]
			});
		}
		function openARCH(ext){
			var documento='';
			var usuario='';
			var rangoFechas='';
			if($("#documento").val()!=''){
				documento=" and cIdDocumento LIKE '%25"+$("#documento").val()+"%25' ";
			}
			if($("#u_login").val()!=''){
				usuario=" and cIdUsuario LIKE '%25"+$("#u_login").val()+"%25' ";
			}
			rangoFechas=" and fRegistro BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";
			var myWindow=window.open("../../servlet/SeguridadCatalogosMateriales?"
			        + "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn=reporteBitacora.jasper"
					+"&formato="+ext
					+ "&usuario=" + usuario
					+ "&documento_=" + documento
					+ "&rangoFechas=" + rangoFechas, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function openCSV(){
			var documento='';
			var usuario='';
			var rangoFechas='';
			if($("#documento").val()!=''){
				documento=" and cIdDocumento LIKE '%25"+$("#documento").val()+"%25' ";
			}
			if($("#u_login").val()!=''){
				usuario=" and cIdUsuario LIKE '%25"+$("#u_login").val()+"%25' ";
			}
			rangoFechas=" and fRegistro BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";
			window.open(
				"../../servlet/CatalogosCSV?"
				+ "rn=reporteBitacora.jasper"
				+ "&usuario=" + usuario
				+ "&documento_=" + documento
				+ "&rangoFechas=" + rangoFechas,
				 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function agregaDatePickerFechas(){
			$("#fInicial").datetimepicker({
				format: 'DD/MM/YYYY',
				altField: "#actualDate",
			 	currentText: "Now",
				changeYear: true
				
			});
			$("#fFinal").datetimepicker({
				format: 'DD/MM/YYYY',
				altField: "#actualDate",
			 	currentText: "Now",
				changeYear: true
				
			});
		}
		function cambiaCentrocontableUsuario(){
			if($("#cIdUnidadEjecutora").val()=="0"){
				return;
			}
			$.ajax({
				url: '../../servlet/CambiaPropiedadesUsuario',
				dataType: 'json',
				data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
				async : false,
				success : function(j) {
					if(j[0].error){
						swal("No se hizo el cambio de centro contable y unidad ejecutora",{icon:"warning",button: "Cerrar"});
					}else{
						$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
					}
				}
			});
		}
	</script>

  </head>
  
  <body>
  	<form id="formBitacora">
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Consulta Bit&aacute;cora</legend>
	 				<div class="form-group">
						<div class="form-group row">
							<div class="form-group col-md-4">
	 							<label for="cIdUnidadEjecutora">Unidad Ejecutora</label>
								<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
									<option value="<%=usuario.getU_UR()%>" selected="selected">
								</select>
							</div> 
						</div>
					</div>
	 				<div class="form-group">
	 					<div class="form-group row">
	 						<div class="form-group col-md-4">
	 							<label for="u_login">Login</label>
								<input type="text" class="form-control" placeholder="Login" aria-label="Login" aria-describedby="basic-addon1"  id="u_login_search" name="u_login_search">
							</div> 
	 					</div>
	 				</div>
	 				<div class="form-group">
	 					<div class="form-group row">
	 						<div class="form-group col-md-4">
		 						<label for="documento">Documento</label>
								<input type="text" class="form-control" placeholder="documento" aria-label="documento" aria-describedby="basic-addon1"  id="documento" name="documento">
							</div> 
	 					</div>
	 				</div>
	 				<div class="form-group row">
						<div class="form-group col-md-2">
							<label for="fInicial">Fecha Inicio</label>
							<div class="input-group date" id="fInicial" data-target-input="nearest">
					          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha Inicial" id="fInicio" name="fInicio" value="<%=todayAnt %>"/>
					          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha Inicial">
					            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          </div>
					        </div>
				        </div>
				        <div class="form-group col-md-2">
							<label for="fFinal">Fecha Fin</label>
							<div class="input-group date col-xs-2" id="fFinal" data-target-input="nearest" >
								<input type="text" class="form-control datetimepicker-input" data-target="#fFinal" id="fFin" name="fFin" title="Fecha Final" value="<%=today %>"  />
								<div class="input-group-append" data-target="#fFinal" data-toggle="datetimepicker" title="Fecha Final">
								  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
								</div>
							</div>
						</div>
				    </div>
				    <div class="form-group row">
				    	<div class="col">
							<input type="button" id="buscarRecepMat" name="buscarRecepMat" value="Buscar" onclick="initTabla()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
						</div>
				    	<div class="col" >
				    		<input type="button" class="btnInterfacePDF ui-button ui-widget ui-state-default ui-corner-all float-right" 	id="cmdPdfReporteProg" 		name="cmdPdfReporteProg" 	value="PDF"	onclick="openARCH('pdf');" />
							<input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all float-right" 	id="cmdxlsReporteProg" 		name="cmdxlsReporteProg" 	value="Excel"	onclick="openARCH('xls');" />
							<input type="button" class="btnInterfaceCSV ui-button ui-widget ui-state-default ui-corner-all float-right" 	id="cmdcsvReporteProg" 		name="cmdcsvReporteProg" 	value="CSV"	onclick="openCSV();" />
							<input type="button" class="btnInterfaceDOC ui-button ui-widget ui-state-default ui-corner-all float-right" 	id="cmdwordReporteProg" 	name="cmdwordReporteProg" 	value="Word"	onclick="openARCH('doc');" />
						</div>
				    </div>
					<div class="form-group row" >
						<div class="col">
							<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th style="display: none;"></th>
										<th align="center">Documento</th>
										<th align="center">Aci&oacute;n</th>
										<th align="center">Usuario</th>
										<th align="center">Fecha <br/>Movimiento </th>
									</tr>										
								</thead>
							</table>
							
						</div>
					</div>
	 			</fieldset>
	 		</div>
	 	</div>
	 	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
		<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
		<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>	
	 </form>
  </body>
</html>
