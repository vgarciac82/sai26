<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if(usuario==null){
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuario.getRoles();
	String role="";
	String roles="";
	Iterator it1 = rol.entrySet().iterator();
	while (it1.hasNext()) {
		Map.Entry r = (Map.Entry)it1.next();
		role=(String)r.getKey();
		roles += r.getKey().toString()+",";
	}
 %>

<!DOCTYPE html>
<html>
  <head>
    
    <title>Mantenimiento de Requisiciones</title>
    
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
		$(document).ready(function() {
			//Llena los combos
			querySelectPost("UnidadEjecutoraRead", "cboUnidadEjecutoraConsultar", {async: false });
			querySelectPost("mCatalogoTipoSolicitudReaAll", "cboTipoRequisicionConsultar", {async: false });
			querySelectPost("mCatalogoPeriodoRead", "cboPeriodoConsultar", {async: false });
			querySelectPost("mCatalogoAlcanceCMBRead", "cboAlcanceConsultar", {async: false });
			querySelectPost("mCatalogoEstadoSolicitudRead", "cboEstadoConsultar", {async: false });

			eliminaOpcionTipoRequisicion();
			initTbls();
			$("#btnBuscarConsultaSolicitud" ).button().click(function() {
				mostrarTablaSolicitudes();
			});
			
			$('#tblSolicitudes').on('dblclick','tr', function() {
				var aTrs = $('#tblSolicitudes').dataTable().fnGetNodes();

				for ( var i=aTrs.length ; i>=0; i-- ){						 
					if ( $(aTrs[i]).hasClass('row_selected') ){
						$(aTrs[i]).removeClass('row_selected');
					}
				}
				$(this).addClass('row_selected');
				var nTr = $('#tblSolicitudes').dataTable().fnGetData(this);
				$("#cIdTipoSolicitud").val(nTr[0]);
				$("#cIdUnidadEjecutora").val(nTr[1]);
				$("#cIdConsecutivo").val(nTr[2]);
				mostrarTablaLineas(nTr[0],nTr[1],nTr[2]);

			});
			$('#tblLineas').on('dblclick','tr', function() {
				var aTrs = $('#tblLineas').dataTable().fnGetNodes();
				for ( var i=aTrs.length ; i>=0; i-- ){						 
					if ( $(aTrs[i]).hasClass('row_selected') ){
						$(aTrs[i]).removeClass('row_selected');
					}
				}
				$(this).addClass('row_selected');
			});
			
		});//Fin del document ready
		function initTbls(){
			$("#tblSolicitudes").dataTable({
				bScrollCollapse: true,
				bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
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
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					aoColumns: [
						{ sName: "cIdTipoSolicitud" },
						{ sName: "cIdUnidadEjecutora"   },
						{ sName: "nIdConsecutivo" },
						{ sName: "cPeriodo"	},
						{ sName: "vence"	},
						{ sName: "cDescripcion"	},
						{ sName: "cAlcance"	},
						{ sName: "cEstado"	},
						{ sName: "nIdPeriodo",bVisible: false},
						{ sName: "nIdAlcance",bVisible: false},
						{ sName: "nIdEstado",bVisible: false}
					],
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}			
				
			});
			$("#tblLineas").dataTable({
				bScrollCollapse: true,
				bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
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
					sInfoFiltered: "(filtrado de _MAX_ registros)",
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
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaSolicitud" },
					{ sName: "cIdCABM"   },
					{ sName: "cDescripcion"	},
					{ sName: "cIdConsolidado" },
					{ sName: "cIdProcedimiento" },
					{ sName: "cIdPedido"	},
					{ sName: "cEstadoPC"	},
					{ sName: "cLiberar"	},
					{ sName: "cAnular"	},
					{ sName: "cEstado"	}
				]			
	    	});
		}
		function mostrarTablaSolicitudes(){
			var qw = " cIdUnidadEjecutora = '"+$("#cboUnidadEjecutoraConsultar").val()+"'";
			if($("#cboTipoRequisicionConsultar").val() != 0){
				qw += " AND cIdTipoSolicitud = '"+$("#cboTipoRequisicionConsultar").val()+"'";
			}
			if($("#cboPeriodoConsultar").val() != 0){
				qw += " AND nIdPeriodo = "+$("#cboPeriodoConsultar").val();
			}
			if($("#cboAlcanceConsultar").val() != 0){
				qw += " AND nIdAlcance = "+$("#cboAlcanceConsultar").val();	
			}
			if($("#cboEstadoConsultar").val() != 0){
				qw += " AND nIdEstado = "+$("#cboEstadoConsultar").val();	
			}
			if($("#numeroConsultar").val() != ""){
				qw += " AND nIdConsecutivo = "+$("#numeroConsultar").val();
			}
			if($("#descripcionConsultar").val() != ""){
				qw +=" AND cDescripcion LIKE '%25" + $.trim($("#descripcionConsultar").val())+"%25'"
			}
   			$("#tblSolicitudes").dataTable({
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
   					sEmptyTable: "No hay datos",
   					sLoadingRecords: "Cargando...",
   					sInfo: "Registros _START_ al _END_ de _TOTAL_",
   					sInfoEmpty: "Registro 0 al 0 de 0",
   					sInfoFiltered: "(filtrado de _MAX_ registros)",
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
   				bProcessing: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneSolicitudes&qw="+qw,
				aaSorting: [[ 0, "asc" ],[ 1, "asc" ],[2,"asc"]] ,			
				aoColumns: [
					{ sName: "cIdTipoSolicitud" },
					{ sName: "cIdUnidadEjecutora"   },
					{ sName: "nIdConsecutivo" },
					{ sName: "cPeriodo"	},
					{ sName: "vence"	},
					{ sName: "cDescripcion"	},
					{ sName: "cAlcance"	},
					{ sName: "cEstado"	},
					{ sName: "nIdPeriodo",bVisible: false},
					{ sName: "nIdAlcance",bVisible: false},
					{ sName: "nIdEstado",bVisible: false}
				]
			});
		}
		function mostrarTablaLineas(cIdTipoSolicitud, cIdUnidadEjecutora, cIdConsecutivo){
			$("#cIdTipoSolicitud").val(cIdTipoSolicitud);
			$("#cIdUnidadEjecutora").val(cIdUnidadEjecutora);
			$("#cIdConsecutivo").val(cIdConsecutivo);
			$('#tblLineas').dataTable().fnClearTable(); 
			var qw = "'"+cIdTipoSolicitud+"'"
				+",'"+cIdUnidadEjecutora+"'"
				+","+cIdConsecutivo;  
			                                                      
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: "CONSULTALINEASSOLICITUD", Param:"",Campos:qw, MaxReg:"" , ajax: 'false'}, 
				function(j){     
					arrayCompleto=new Array(); 
					for (var i = 0; i < j.length; i++){
						arrayCompleto [i]=[
							"<input type='text' name='nIdLinea"+i+"' id='nIdLinea"+i+"' value='" + j[i].Col0 + "' readonly style='width:50px; border-width:0; background-color:transparent'/>",
							"<input type='text' name='CAMP"+i+"' id='CAMP"+i+"' value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
							"<input type='text' name='Descripcion"+i+"' id='Descripcion"+i+"' value='" + j[i].Col5 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
							"<input type='text' name='cIdConsolidado"+i+"' id='cIdConsolidado"+i+"' value='" + j[i].Col2 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
							"<input type='text' name='cIdProcedimiento"+i+"' id='cIdProcedimiento"+i+"' value='" + j[i].Col3 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
							"<input type='text' name='cIdPedido"+i+"' id='cIdPedido"+i+"' value='" + j[i].Col4 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
			                "<input type='text' name='cEstadoPC"+i+"' id='cEstadoPC"+i+"' value='" + j[i].Col9 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
			                "" + j[i].Col6 + "",
			                "" + j[i].Col7 + "",
			                "<input type='text' name='cEstado"+i+"' id='cEstado"+i+"' value='" + j[i].Col8 + "' readonly style='width:100px; border-width:0; background-color:transparent'/>"
						];
					}
					if(j.length>0){
						$('#tblLineas').dataTable().fnAddData(arrayCompleto);
						$("#tblLineas").dataTable().fnAdjustColumnSizing();
					}
					
				}
			);
		}
		function actualizaAlcanceSolicitud(cAlcanceSolicitud, cIdTipoSolicitud, cIdUnidadEjecutora, cIdConsecutivo){
			if($("#R_NOMBRE").val().indexOf("ADMIN_RECMAT") >= 0 ){
				$("#cAlcanceSolicitud").val($("#"+cAlcanceSolicitud).val());
				$("#cIdTipoSolicitud").val(cIdTipoSolicitud);
				$("#cIdUnidadEjecutora").val(cIdUnidadEjecutora);
				$("#cIdConsecutivo").val(cIdConsecutivo);
				queryFormPost("mUpdateAlcanceSolicitud", {async:false,
					callback : function() {
						swal("Alcance actualizado.",{icon:"success",button: "Cerrar"});
					}
				});
			}else{
				swal({
					title: "",
					text: "Solo el Administrador puede realizar esta operacion",
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						location.reload();
						 return;
				});
			}
		}
		function liberarLineaSolicitud(cIdSolicitud, cIdConsolidado, cIdProcedimiento, cIdPedidoContrato, nIdLinea, nIdLineaConsolidado){
			if($("#R_NOMBRE").val().indexOf("ADMIN_RECMAT") >= 0){
				var cIdSolicitudTemp = cIdSolicitud.split("-");
				$("#cIdTipoSolicitud").val(cIdSolicitudTemp[0]);
				$("#cIdUnidadEjecutora").val(cIdSolicitudTemp[1]);
				$("#cIdConsecutivo").val(cIdSolicitudTemp[2]);
				$("#nIdLineaSolicitud").val(nIdLinea);
				$("#nIdLineaConsolidado").val(nIdLineaConsolidado);
				$("#cIdSolicitud").val(cIdSolicitud);
				$("#cIdConsolidado").val(cIdConsolidado);
				$("#cIdProcedimiento").val(cIdProcedimiento);
				$("#cIdPedido").val(cIdPedidoContrato);
				$("#cEstadoLineaSolicitud").val("L");
				if(confirm("\xBFEstas seguro de liberar la l\xEDnea?")){
					liberaLineaTem();
				}
			}else{
				swal({
					title: "",
					text: "Solo el Administrador puede realizar esta operacion.",
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						location.reload();
						 return;
				});
			}
		}
		function liberaLineaTem(){
			queryFormPost("totalLineasConsolidado", {async:false});
			queryFormPost("mLiberaLineaSolicitudUpdate", {async:false});
			queryFormPost("deleteConsolidadoSolicitud", {async:false});
			queryFormPost("deleteConsolidadoPreseleccionLineaSolicitud", {async:false});
			queryFormPost("mTotalLineasPreseleccionRead", {async:false});

			if(parseInt($("#cTotalLineasPreseleccion").val(),10) == 0){
				queryFormPost("deleteSolicitudPreseleccion", {async:false});
			}
			if(parseInt($("#cTotalLineasConsolidado").val(),10) == 1){
				if(confirm("La l\xEDnea del consolidado ya no tiene ninguna l\xEDnea de solicitud si contin\xFAa se eliminar\xE1 la l\xEDnea del consolidado. \xBFDesea continuar?")){
					queryFormPost("deleteProcedimientoAdjudicacionPartidas", {async:false});
					queryFormPost("deleteContratoPartidas", {async:false});
					queryFormPost("deletePedidoPartidas", {async:false});
					queryFormPost("deleteConsolidadoLineas", {async:false});
				}
			}
			mostrarTablaLineas($("#cIdTipoSolicitud").val(),$("#cIdUnidadEjecutora").val(),$("#cIdConsecutivo").val());
		}
		function anularLineaSolicitud(cIdSolicitud, cIdConsolidado, cIdProcedimiento, cIdPedidoContrato, nIdLinea, nIdLineaConsolidado){
			if($("#R_NOMBRE").val().indexOf("ADMIN_RECMAT") >= 0){
				var cIdSolicitudTemp = cIdSolicitud.split("-");
				$("#cIdTipoSolicitud").val(cIdSolicitudTemp[0]);
				$("#cIdUnidadEjecutora").val(cIdSolicitudTemp[1]);
				$("#cIdConsecutivo").val(cIdSolicitudTemp[2]);
				$("#nIdLineaSolicitud").val(nIdLinea);
				$("#nIdLineaConsolidado").val(nIdLineaConsolidado);
				$("#cIdSolicitud").val(cIdSolicitud);
				$("#cIdConsolidado").val(cIdConsolidado);
				$("#cIdProcedimiento").val(cIdProcedimiento);
				$("#cIdPedido").val(cIdPedidoContrato);
				$("#cEstadoLineaSolicitud").val("C");

				if(confirm("\xBFEstas seguro de anular la l\xEDnea?")){
					$.getJSON("../../servlet/LiberaCompromisoEP?"+new Date().getTime()+"&cIdSolicitud="+cIdSolicitud+"&nIdLineaSolicitud="+nIdLinea,{Tabla: "", MaxReg: "", ajax: 'false'}, function(j){
						for(var i = 0; i < j.length; i++)
							 var col=j[i].Col1
						switch(col){
							case "1": 
								swal("Error al guardar el encabezado del apartado.",{icon:"error",button: "Cerrar"});
							break;
							case "2": 
								swal("Error al guardar el detalle del apartado.",{icon:"error",button: "Cerrar"});
							break;
							case "3": 
								swal("Error al repaldar el apartado de la linea.",{icon:"error",button: "Cerrar"});
							break;
							case "4": 
								swal("Error al eliminar las lineas de la solictud.",{icon:"error",button: "Cerrar"});
							break;
							case "5": 
								swal("Error al obtener el total de las lineas del apartado.",{icon:"error",button: "Cerrar"});
							break;
							case "6": 
								swal("Error al buscar el folio del apartado.",{icon:"error",button: "Cerrar"});
							break;
							case "7": 
								swal("Error al elminiar las vigencias del apartado.",{icon:"error",button: "Cerrar"});
							break;
							case "8": 
								swal("Error al actualizar el folio del apartado en la tabla de solicitudes.",{icon:"error",button: "Cerrar"});
							break;
							case "9": 
								swal("La l\xEDnea se anulo correctamente (No se realiz\xF3 ninguna afectaci\xF3n contable ya que no tenia ningun Apartado).",{icon:"error",button: "Cerrar"});
								liberaLineaTem();
							break;
							case "-1": 
								swal("Ocurrio un error al realizar la aplicacion contable.",{icon:"error",button: "Cerrar"});
							break;
							case "0":
								swal("DOCUMENTO DE APARTADO APLICADO CONTABLEMENTE.",{icon:"error",button: "Cerrar"});
								liberaLineaTem();
							break;
							
						}
					});
				}
			}else{
				swal({
					title: "",
					text: "Solo el Administrador puede realizar esta operacion.",
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						location.reload();
						 return;
				});
			}
		}
		function eliminaOpcionTipoRequisicion(){
			var objTipoProc = document.getElementById("cboTipoRequisicionConsultar");
			for(var l=0;l<objTipoProc.options.length;l++){
				if(objTipoProc.options[l].text.toString().toUpperCase() == "DE FONDEN")
					objTipoProc.options[l]=null;
			}
			for(var l=0;l<objTipoProc.options.length;l++){
				if(objTipoProc.options[l].text.toString().toUpperCase() == "DE CAPITULO1000")
					objTipoProc.options[l]=null;			
			}
		}
        function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			return (keyPressed >= 48 && keyPressed <= 57);
		}
	</script>
</head> 
  <body>
  	<form id="formMantenimientoRequis">
  		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Mantenimiento Solicitudes</legend>
	 				<div class="form-group" id="tabsId">
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cboUnidadEjecutoraConsultar">Unidad Ejecutora: </label>
								</div>
								<div class="col-6">
									<select class="custom-select" id="cboUnidadEjecutoraConsultar" name="cboUnidadEjecutoraConsultar" onchange="cambiaCentrocontableUsuario();">
										<option value="<%=usuario.getU_UR()%>" selected="selected">
									</select>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cboTipoRequisicionConsultar">Tipo de Requisici&oacute;n: </label>
								</div>
								<div class="col-6">
									<select class="custom-select" id="cboTipoRequisicionConsultar" name="cboTipoRequisicionConsultar" onchange="">
									</select>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cboPeriodoConsultar">Periodo:</label>
								</div>
								<div class="col-6">
									<select class="custom-select" id="cboPeriodoConsultar" name="cboPeriodoConsultar" onchange="">
									</select>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cboAlcanceConsultar">Alcance:</label>
								</div>
								<div class="col-6">
									<select class="custom-select" id="cboAlcanceConsultar" name="cboAlcanceConsultar" onchange="">
									</select>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="cboEstadoConsultar">Estado:</label>
								</div>
								<div class="col-6">
									<select class="custom-select" id="cboEstadoConsultar" name="cboEstadoConsultar" onchange="">
									</select>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="numeroConsultar">N&uacute;mero: </label>
								</div>
								<div class="col-6">
									<input type="text" class="form-control" aria-describedby="basic-addon1"  
									name="numeroConsultar" id="numeroConsultar" onkeypress="return onlyIntegers(event);"  />
								</div>
							</div>
						</div>
						<div class="row">
							<div class="input-group">
								<div class="col-2">
									<label for="descripcionConsultar">Descripci&oacute;n: </label>
								</div>
								<div class="col-6">
									<input type="text" class="form-control" aria-describedby="basic-addon1"  
									name="descripcionConsultar" id="descripcionConsultar"  />
								</div>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="input-group">
								<div class="col-4">
									<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnBuscarConsultaSolicitud" name="btnBuscarConsultaSolicitud" 	value="Buscar"	 />
								</div>
							</div>
						</div>
					</div>
					<div class="form-group" id="div_tblSolicitudes">
						<div class="row">
							<div class="col">
								<table id="tblSolicitudes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr>
											<th>&nbsp;</th>
						                	<th>&nbsp;</th>
						                    <th>&nbsp;</th>
						                    <th>Periodo</th>
						                    <th>FechaVence</th>
						                    <th>Descripci&oacute;n</th>
						                    <th>Alcance</th>
						                    <th>Estado</th>
						                    <th>nIdPeriodo</th>
						                    <th>nIdAlcance</th>
						                    <th>nIdEstado</th>
										</tr>									
									</thead>
								</table>
							</div>
						</div>
					</div>
	 				<div class="form-group" id="div_tblLineas">
						<div class="row">
							<div class="col">
								<table id="tblLineas" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
									<thead >
										<tr>
											<th>L&iacute;nea</th>
						                    <th>CUCOP</th>
						                    <th>Descripci&oacute;n</th>
						                    <th>Consolidado</th>
						                    <th>Procedimiento</th>
						                    <th>Pedido/Contrato</th>
						                    <th>Estado Pedido/Contrato</th>
						                    <th>&nbsp;</th>
						                    <th>&nbsp;</th>
						                    <th>Estado</th>
										</tr>									
									</thead>
								</table>
							</div>
						</div>
					</div>
	 			</fieldset>
	 		</div>
	 	</div>
  		<input id="cAlcanceSolicitud" name="cAlcanceSolicitud" type="hidden">
  		<input id="cIdTipoSolicitud" name="cIdTipoSolicitud" type="hidden">
  		<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden">
  		<input id="cIdConsecutivo" name="cIdConsecutivo" type="hidden">
  		<input id="nIdLineaSolicitud" name="nIdLineaSolicitud" type="hidden">
  		<input id="nIdLineaConsolidado" name="nIdLineaConsolidado" type="hidden">
  		<input id="cIdSolicitud" name="cIdSolicitud" type="hidden">
  		<input id="cIdConsolidado" name="cIdConsolidado" type="hidden">
  		<input id="cIdProcedimiento" name="cIdProcedimiento" type="hidden">
  		<input id="cIdPedidoContrato" name="cIdPedidoContrato" type="hidden">
  		<input id="cTotalLineasConsolidado" name="cTotalLineasConsolidado" type="hidden">
  		<input id="cTotalLineasPreseleccion" name="cTotalLineasPreseleccion" type="hidden">
  		<input id="cEstadoLineaSolicitud" name="cEstadoLineaSolicitud" type="hidden">
  		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuario.getLogin() %>"/>
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" value="<%=roles%>">
	</form>
  </body>
</html>