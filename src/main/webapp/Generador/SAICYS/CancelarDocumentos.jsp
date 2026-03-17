<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=null;
	Map rol =null;
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	name_user=usuario.getLogin();
	rol =usuario.getRoles();
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
   
    
    <title>Cancelación de Documentos para GRMO</title>
    
	 <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" /> 
		<style type="text/css" title="currentStyle"> 
			@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
	 		@import "../css/demo_table_jui.css"; 
			@import "../css/demo_table.css"; 
			
		</style>
		<link rel="stylesheet" type="text/css"	href="../css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
		<link rel="stylesheet" href="../../SISECOP/css/style.css">
	
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	  	<script type="text/javascript" src="../js/Procedimiento.js"></script>
	  	<script type="text/javascript" src="../js/sweetalert2.all.min.js"></script>
	  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>	
		<script type="text/javascript" charset="utf-8">
			var oTableConsulta;
			var oTableConsolidado;
			var oTableProcedimiento;
			var oTablePedCont;
			var roles;
			var mx = {
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
				};
			$(document).ready(function() {
				<%
				    String role="";
				    String roles="";
					Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						role=(String)r.getKey();
						roles += r.getKey().toString()+",";
					}
					
				%>
				roles="<%=roles%>";
				if (roles.indexOf("ADMIN_RECMAT") >= 0){
					$("#isAdmin").val(0);
				} 
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
				
				$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
				if(roles.indexOf("ADMIN_RECMAT") >=0){
					$("#cIdUnidadEjecutora").prepend("<option value='%25'>*</option>");
				}
				busca();
				reloadTablaRequi();
				$("#dialog-form-ep")
					.dialog(
							{
						autoOpen : false,
						height : 230,
						width : 590,
						modal : true,
						buttons : {
							"Guardar" : function() {
								cancela();

								$("#cMotivoCancelacion").val('');
								$(this).dialog("close");
							},
							Cancel : function() {
								$("#cMotivoCancelacion").val('');
								$(this).dialog("close");
							}
						},
						close : function() {
							$("#cMotivoCancelacion").val('');
							
						}
					});	
			});//Fin del document Ready
			function reloadTablaRequi(){
				var qw="cIdUnidadEjecutora LIKE'"+$("#cIdUnidadEjecutora").val()+"'";
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
					oLanguage: mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mSolicitudesAcancelar&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ],[2, "asc"]] ,
					aoColumns: [
						{sName: "cIdSolicitud"},
						{sName: "cIdUnidadEjecutora",bVisible: false},
						{sName: "cIdSubPartida"},
						{sName: "cDescripcion"},
						{sName: "cIdUsuarioCreacion"},
						{sName: "cAlcance"},
						{sName: "cEstado"},
						{sName: "botonElim"},
						{sName: "nIdEstado",bVisible: false}
					]
				});
			}
			function reloadTablaConsolidado(){
				var qw="cIdUnidadEjecutora LIKE'"+$("#cIdUnidadEjecutora").val()+"'";
				oTableConsolidado = $("#tblConsultaConsolidado").dataTable({
					bScrollCollapse: true,
	        		bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mConsolidadosAcancelar&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ],[2, "asc"]] ,
					aoColumns: [
						{sName: "cIdConsolidado"},
						{sName: "cIdUnidadEjecutora",bVisible: false},
						{sName: "cDescripcion"},
						{sName: "cIdUsuarioCreacion"},
						{sName: "cAlcance"},
						{sName: "cEstado"},
						{sName: "botonCancelar"},
						{sName: "nIdEstado",bVisible: false},
						{sName: "requis",bVisible: false}
					]
				});
			}
			function reloadTablaProcedimiento(){
				var qw="nIdEstado=1 and cIdUnidadEjecutora LIKE'"+$("#cIdUnidadEjecutora").val()+"'";
				oTableProcedimiento = $("#tblConsultaProcedimiento").dataTable({
					bScrollCollapse: true,
	        		bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					oLanguage: mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mProcedimientoAcancelar&qw="+qw,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ],[2, "asc"]] ,
					aoColumns: [
						{sName: "cIdProcedimiento"},
						{sName: "cIdUnidadEjecutora",bVisible: false},
						{sName: "cCategoria"},
						{sName: "cDescripcion"},
						{sName: "cIdUsuarioCreacion"},
						{sName: "cEstado"},
						{sName: "nIdEstado",bVisible: false},
						{sName: "nIdCategoria",bVisible: false},
						{sName: "cIdConsolidado"},
						{sName: "botonCancelar"}
					]
				});
			}
			function reloadTablaPedCont(){
				var qw="nidEstado not in(4,5,6) and cIdUnidadEjecutora LIKE'"+$("#cIdUnidadEjecutora").val()+"'";
				var vista="v_mPedContAcancelar";
				if($('#rPedCont-aprobSinPag').is(':checked')){
					qw="cIdUnidadEjecutora LIKE'"+$("#cIdUnidadEjecutora").val()+"'";
					vista="v_mPedContSinPagos";
				}
				oTablePedCont = $("#tblConsultaPedCont").dataTable({
					bScrollCollapse: true,
	        		bInfo: false,
					sScrollX: "100%",
					bAutoWith: true,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage:  mx,
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+vista+"&qw="+qw,
					bJQueryUI: true,
					aaSorting: [[ 0, "asc" ],[2, "asc"]] ,
					aoColumns: [
						{sName: "pedContDef"},
						{sName: "cIdRFC"},
						{sName: "cRazonSocial"},
						{sName: "cConceptoPedido"},
						{sName: "cEstado"},
						{sName: "cIdProcedimiento"},
						{sName: "cIdUsuarioCreacion"},
						{sName: "cIdConsolidado"},
						{sName: "botonCancelar"},
						{sName: "cIdUnidadEjecutora",bVisible: false},
						{sName: "nIdEstado",bVisible: false},
						{sName: "cnumCotizacion",bVisible: false}
						
					]
				});
			}
			function busca(){
				$('#checkNoCancelaRequi').prop('checked', true);
				$('#checkNoCancelaRequi').prop('disabled', false);
				if($('#rRequi').is(':checked')){
					$('#checkNoCancelaRequi').prop('checked', false);
					$('#checkNoCancelaRequi').prop('disabled', true);
					muestraTabla('divTblConsulta');
					reloadTablaRequi();
				}
				else if($('#rConsolidado').is(':checked')){
					muestraTabla('divTblConsultaConsolidado');
					reloadTablaConsolidado();
				}
				else if($('#rProcedimiento').is(':checked')){
					muestraTabla('divTblConsultaProcedimiento');
					reloadTablaProcedimiento();
				}
				else if($('#rPedCont').is(':checked')|| $('#rPedCont-aprobSinPag').is(':checked') ){
					muestraTabla('divTblConsultaPedCont');
					reloadTablaPedCont();
				}
				else {
					Swal.fire("Función no encontrada.",{icon:"info",button: "Cerrar"});
					return;
				}
			}
			function cancelaDocumentos(doc,tipoDoc,cejercicio,requis,consoli,procedim,unoAmuchos){
				Swal.fire({
					  title: 'Desea continuar?',
					  text: " Se cancelará el documento.",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  	copiaInfoEnHiddens(doc, tipoDoc, cejercicio, requis, consoli, procedim, unoAmuchos);
							cancela();
							Swal.fire("OK!","Documento cancelado!", "success");
					  } 
					})
					
			}
			function cancelaPedContAprobado(doc,tipoDoc,cejercicio,requis,consoli,procedim,unoAmuchos){
				Swal.fire({
					  title: 'Desea continuar?',
					  text: " Se cancelará el PEDIDO / CONTRATO.",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  copiaInfoEnHiddens(doc, tipoDoc, cejercicio, requis, consoli, procedim, unoAmuchos);
							$("#dialog-form-ep").dialog("open");
					  } 
					})
			}
			
			function copiaInfoEnHiddens(doc,tipoDoc,cejercicio,requis,consoli,procedim,unoAmuchos){
				$("#noCancelarRequi").val(0);
				$("#doc").val(doc);
				$("#tipoDoc").val(tipoDoc);
				$("#cejercicio").val(cejercicio);
				$("#requis").val(requis);
				$("#consoli").val(consoli);
				$("#procedim").val(procedim);
				$("#unoAmuchos").val(unoAmuchos);
				if($('#checkNoCancelaRequi').is(':checked')){
					$("#noCancelarRequi").val(1);
					
				}
			}
			function cancela(){
				$.ajax({
					url: '../../servlet/ApartadoPrecomCancelarServlet',
					dataType: 'json',
					data: {"Param" : $("#doc").val()+","+$("#tipoDoc").val()+","+$("#requis").val()+",CANCELACIONDEDOCUMENTOS"+","+$("#consoli").val()
					+","+$("#procedim").val()+","+$("#unoAmuchos").val()+","+$("#cejercicio").val()+","+$("#cMotivoCancelacion").val()+","+$("#noCancelarRequi").val()},
					async : false,
					success : function(j) {
						Swal.fire(j[0].mensaje,{icon:"info",button: "Cerrar"});
						busca();
					}
				});
			}
			function ocultaTablas(){
				$("#divTblConsulta").css("display","none");
				$("#divTblConsultaConsolidado").css("display","none");
				$("#divTblConsultaProcedimiento").css("display","none");
				$("#divTblConsultaPedCont").css("display","none");
				
			}
			function muestraTabla(id){
				ocultaTablas();
				$("#"+id).css("display","block");
			}
			function EliminarPegar(e) {
				return !(e.keyCode==86 && e.ctrlKey);
			}
		</script>
  </head>
  
  <body>
	<form id="cancelaDoc">
		<div id="container" class="container" style="width: 90%;">
    		<fieldset>
    			<h4>Listado de documentos que se pueden cancelar</h4>
    			<div class="row">
    				<div class="col-2">
    					Unidad Ejecutora:
    				</div>
    				<div class="col-8">
    					<select name='cIdUnidadEjecutora' id='cIdUnidadEjecutora' class="form-select"></select>
    				</div>
    			</div>
    			<div class="row">
    				<div class="col-4">
    					<input id="checkNoCancelaRequi" name="checkNoCancelaRequi" type="checkbox" value="" checked="checked" class="form-check-input"/>
    						Excluir cancelaci&oacute;n de requisici&oacute;n
    				</div>
    			</div>
    			<div class="row mt-2">
    				<div class="col-2">
    					<input type="radio" name="radioObcion" id="rRequi" checked="checked" class="form-check-input"/>Requisiciones
    				</div>
    				<div class="col-2">
    					<input type="radio" name="radioObcion" id="rConsolidado" class="form-check-input" />Consolidado
    				</div>
    				<div class="col-2">
    					<input type="radio" name="radioObcion" id="rProcedimiento" class="form-check-input"/>Procedimiento
    				</div>
    				<div class="col-2">
    					<input type="radio" name="radioObcion" id="rPedCont" class="form-check-input" />Pedido/contrato
    				</div>
    				<div class="col-3">
    					<input type="radio" name="radioObcion" id="rPedCont-aprobSinPag" class="form-check-input"/>Ped/Cont Aprobado sin Pagos
    				</div>
    			</div>
    			<div class="row mt-2">
    				<div class="d-flex justify-content-center">
    					<input type="button" id="buscar" name="buscar" value="Buscar" onclick="busca();" class="btn btn-secondary" />
    				</div>
    			</div>
    			<div id="divTblConsulta" class="mt-2">
    				<table id="tblConsulta" class="display" >
						<thead >
							<tr>
								<th align="center">Solicitud</th>
								<th style="display: none;">Unidad Ejecutora</th>
								<th align="center">Partida</th>
								<th align="center">Descripcion</th>
								<th align="center">Usuario<br />Creador</th>
								<th align="center">Alcance</th>
								<th align="center">Estatus</th>
								<th align="center">Cancelar</th>
								<th style="display: none;"></th>
																					
							</tr>										
						</thead>
					</table>
    			</div>
    			<div id="divTblConsultaConsolidado" style="display: none;" class="mt-2">
    				<table id="tblConsultaConsolidado" class="display" >
						<thead >
							<tr>
								<th align="center">Consolidado</th>
								<th style="display: none;">Unidad Ejecutora</th>
								<th align="center">Descripcion</th>
								<th align="center">Usuario<br />Creador</th>
								<th align="center">Alcance</th>
								<th align="center">Estatus</th>
								<th align="center">Cancelar</th>
								<th style="display: none;">nIdEstado</th>
								<th style="display: none;">requis</th>													
							</tr>										
						</thead>
					</table>
    			</div>
    			<div id="divTblConsultaProcedimiento" style="display: none;" class="mt-2">
    				<table id="tblConsultaProcedimiento" class="display">
						<thead >
							<tr>
								<th align="center">Procedimiento</th>
								<th style="display: none;">Unidad Ejecutora</th>
								<th align="center">Categoria</th>
								<th align="center">Descripcion</th>
								<th align="center">Usuario<br />Creador</th>
								<th align="center">Estatus</th>
								<th style="display: none;">nIdEstatus</th>
								<th style="display: none;">nIdCategoria</th>
								<th >Consolidado</th>
								<th align="center">Cancelar</th>
							</tr>										
						</thead>
					</table>
    			</div>
    			<div id="divTblConsultaPedCont" style="display: none;" class="mt-2">
    				<table id="tblConsultaPedCont" class="display">
						<thead >
							<tr>
								<th align="center">Pedido/Contrato</th>
								<th align="center">RFC</th>
								<th align="center">Razon Social</th>
								<th align="center">Descripcion</th>
								<th align="center">Estatus</th>
								<th align="center">Procedimiento</th>
								<th align="center">Usuario<br />Creador</th>
								<th align="center">Consolidado</th>
								<th align="center">Cancelar</th>
								<th style="display: none;">Unidad Ejecutora</th>
								<th style="display: none;">Num Cotización<br />Num Compranet</th>
								<th style="display: none;">nIdEstatus</th>
								
							</tr>										
						</thead>
					</table>
    			</div>
    			<div id="dialog-form-ep"
					title="Agregar Motivo de Cancelaci&oacute;n">
					<fieldset>
						<table>
							<tr>
								<td>
									<label for="motivoCancelacion">
										Motivo:
									</label>
								</td>
								<td>
									<textarea rows="5" cols="50" id="cMotivoCancelacion" name="cMotivoCancelacion" onkeydown="return(EliminarPegar(event))"></textarea>
								</td>
							</tr>
						</table>
					</fieldset>
				</div>
    		</fieldset>
    	</div>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    	<input type="hidden" name="doc" id="doc" value="" />
    	<input type="hidden" name="tipoDoc" id="tipoDoc" value="" />
    	<input type="hidden" name="cejercicio" id="cejercicio" value="" />
    	<input type="hidden" name="requis" id="requis" value="" />
    	<input type="hidden" name="consoli" id="consoli" value="" />
    	<input type="hidden" name="procedim" id="procedim" value="" />
    	<input type="hidden" name="unoAmuchos" id="unoAmuchos" value="" />
    	<input type="hidden" name="noCancelarRequi" id="noCancelarRequi" value="0" />
	</form>
  </body>
</html>
