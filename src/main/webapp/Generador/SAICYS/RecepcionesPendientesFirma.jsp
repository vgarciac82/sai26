<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<meta charset="UTF-8">
<title>Insert title here</title>
	
<!-- 	<script type="text/javascript" src="https://code.jquery.com/jquery-3.7.1.js"></script> -->
<!-- 	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script> -->
<!-- 	<script type="text/javascript" src="https://cdn.datatables.net/2.1.8/js/dataTables.js"></script> -->
<!-- 	<script type="text/javascript" src="https://cdn.datatables.net/2.1.8/js/dataTables.bootstrap5.js"></script> -->
<!-- 	<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css"/> -->
<!-- 	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/2.1.8/css/dataTables.bootstrap5.css"/> -->
<!-- 	<script type="text/javascript" src="../js/crud.js"></script> -->
<!-- 	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script> -->
<!-- 	<script type="text/javascript" src="../js/funciones.js"></script> -->
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
		function  initTabla(){
			var qw="1=1 ";
			if($("#cIdUnidadEjecutora").val()!="0"){
				qw=" cUnidadEjecutora like'%25"+$("#cIdUnidadEjecutora").val()+"%25'";	
			}
			if($("#cIdDefinitivo").val()!=''){
				qw=qw+" and contratoSAI LIKE '%25"+$("#cIdDefinitivo").val()+"%25'";
			}
			oTableConsulta = $("#tblConsulta").DataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRecepPendientesFirma&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ],[1, "asc"]] ,
				aoColumns: [
					{sName: "contratoSAI"},
					{sName: "cIdRecepMat"},
					{sName: "contratoCNET"},
					{sName: "cFolioNota"},
					{sName: "firmante"},
					{sName: "usuarioCaptura"},
					{sName: "dMotivoNota"},
					{sName: "reenviarCorreo"}
				]
			});
		}
		function reenviarEmail(cIdContDef,cidRecep,dMotivoNota,folioNota,numEmpleado,esAlmacenCentral,cContratoCNET){
			swal({
				title: "Reenviar Email",
				text: "¿Seguro que se requiere el reenvío de Email?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$.ajax({
						url : '../../fiel/solicitaAutRM',
						dataType : 'json',
						type : "POST",
						beforeSend : function() {
							$.blockUI({
								message : 'Enviando, espere ...'
							});
						},
						data : {
							"cIDContrato" : cContratoCNET,
							"cIDRecepMat" : cidRecep,
							"dMotivoNota" : dMotivoNota,
							"cFolioNota" : folioNota,
							"cNumeroEmpleado" : numEmpleado,
							"nIdEntraAlmacen" : esAlmacenCentral,
							"nIdEstatusAtentaNotaFirmada" : 0,
							"cIdContratoDefinitivo" : cIdContDef,
							"reenviaEmail" : true
						},
						async : true,
						success : function(objResp) {
							var success = objResp.success;
							if( success == true ){
								swal("Solicitud de reenvío de email exitoso!",{icon:"info",button: "Cerrar"});
							}else{
								swal(objResp.message,{icon:"info",button: "Cerrar"});
							}
							$.unblockUI();
						},
						error : function(xhr, textStatus, errorThrown) {
							try {
								var obj = eval(xhr.responseText);
								var msg = obj.errCause;
								swal("No fue posible reenvíar el email debido al error: "+ msg,{icon:"info",button: "Cerrar"});
							} catch (e) {
								swal("Advertencia: " + xhr.responseText
										+ "\nEstatus: " + textStatus + "\n"
										+ errorThrown,{icon:"error",button: "Cerrar"});
							}

							$.unblockUI();
						}
					});
				}
			});
		}
		
	</script>
</head>
<body>
	<form action="">
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Consulta Recepciones Pendientes de Firma</legend>
	 				<div class="form-group">
						<div class="form-group row">
							<div class="form-group col-md-5">
	 							<label for="cIdUnidadEjecutora">Unidad Ejecutora</label>
								<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
									<option value="<%=usuario.getU_UR()%>" selected="selected">
								</select>
							</div> 
						</div>
					</div>
					<div class="form-group">
						<div class="form-group row">
							<div class="form-group col-md-5">
	 							<label for="cIdDefinitivo">Contrato SAI</label>
								<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CC-A04-1/2024" aria-label="Número de Contrato SAI, Ejemplo CV-A04-1/2024" aria-describedby="basic-addon1"  
									name="cIdDefinitivo" id="cIdDefinitivo"  />
							</div> 
						</div>
					</div>
					<div class="form-group">
						<div class="form-group row">
							<div class="form-group col-md-5">
	 							<label for="cNoContratoCNET">Contrato CNET</label>
								<input type="text" class="form-control" placeholder="Número de Contrato CNET, Ejemplo CNF-D42-AA-16-RHQ-016RHQ001-N-435-2024/23" aria-label="Número de Contrato CNET, Ejemplo CNF-D42-AA-16-RHQ-016RHQ001-N-435-2024/23"
									aria-describedby="basic-addon1"  name="cNoContratoCNET" id="cNoContratoCNET"  />
							</div> 
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="input-group">
								<div class="col-5">
									<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnConsultaRecepcionesPendientes" name="btnConsultaRecepcionesPendientes" 	value="Buscar"	onclick="initTabla();" />
								</div>
							</div>
						</div>
					</div>
					<div class="form-group row" >
						<div class="col">
							<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th align="center">Contrato SAI</th>
										<th align="center">Recepci&oacute;n</th>
										<th align="center">Contrato CNET</th>
										<th align="center">Folio <br/>Nota </th>
										<th align="center">Firmante</th>
										<th align="center">Usuario Captura</th>
										<th align="center">Atenta Nota</th>
										<th></th>
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