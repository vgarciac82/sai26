<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}
String uR = usuario.getU_UR(); 
String DATE_FORMAT = "dd/MM/yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today = sdf.format(c1.getTime()); 

AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

String aEjercicioFiscal = adecProy.obtenEjercicioFiscal();

int id_oper = c.getCasoOperacion(0).getIdOperacion();
Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);

int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

Map grupos =  usuario.getGrupos();
boolean esCapturistaUN = false;
boolean esCapturistaOC = false;
boolean esCapturistaDL = false;

for( Iterator i = grupos.keySet().iterator(); i.hasNext(); ){
	String key = (String)i.next();
	if( key.indexOf( "CAP_ANTPROYECTO_UN" ) >= 0 )
		esCapturistaUN = true;
	if( key.indexOf( "CAP_ANTPROYECTO_OC" ) >= 0 )
		esCapturistaOC = true;
	if( key.indexOf( "CAP_ANTPROYECTO" ) >= 0 )
		esCapturistaDL = true;
}

boolean esRevisorUN = false;
boolean esRevisorOC= false;
boolean esRevisorDL = false;

for( Iterator i = grupos.keySet().iterator(); i.hasNext(); ){
	String key = (String)i.next();
	if( key.indexOf( "INT_ANTPROYECTO_UN" ) >= 0 )
	    esRevisorUN = true;
	if( key.indexOf( "INT_ANTPROYECTO_OC" ) >= 0 )
	    esRevisorOC= true;
	if( key.indexOf( "INT_ANTPROYECTO" ) >= 0 )
	    esRevisorDL = true;
}

boolean esIntegradorUN = false;
boolean esIntegradorOC= false;
boolean esIntegradorDL = false;

for( Iterator i = grupos.keySet().iterator(); i.hasNext(); ){
	String key = (String)i.next();
	if( key.indexOf( "INTEGRADOR_ANTPROYECTO_UN" ) >= 0 )
	    esIntegradorUN = true;
	if( key.indexOf( "INTEGRADOR_ANTPROYECTO_OC" ) >= 0 )
	    esIntegradorOC= true;
	if( key.indexOf( "INTEGRADOR_ANTPROYECTO_DL" ) >= 0 )
	    esIntegradorDL = true;
}

String grupo="";
String grupoInt="";
if(id_oper==1){
	if(usuario.getGrupo("CAP_ANTPROYECTO_UN_"+usuario.getU_UR())!=null){
	    grupo = "CAP_ANTPROYECTO_UN_"+usuario.getU_UR();
	    grupoInt = "INT_ANTPROYECTO_UN_"+usuario.getU_UR();
	}
	if(usuario.getGrupo("CAP_ANTPROYECTO_OC_"+usuario.getU_UR())!=null){
	    grupo = "CAP_ANTPROYECTO_OC_"+usuario.getU_UR();
	    grupoInt = "INT_ANTPROYECTO_OC_"+usuario.getU_UR();
	}
	if(usuario.getGrupo("CAP_ANTPROYECTO")!=null){
    	grupo = "CAP_ANTPROYECTO";
    	grupoInt = "INT_ANTPROYECTO_DL_"+usuario.getU_UR();
	}
}

String mensajeRetorno = (String) session.getAttribute("MENSAJE_CARGA");
if (mensajeRetorno != null) {
	session.removeAttribute("MENSAJE_CARGA");
} else
	mensajeRetorno = "";

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Integracion AnteProyecto</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="Control de Accesos">

<style type="text/css" title="currentStyle">
@import "../Generador/css/demo_page.css";

@import "../Generador/css/demo_table_jui.css";

@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<script type="text/javascript" src="../Generador/js/jquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/catalogo/general.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script type="text/javascript"
	src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

	<script type="text/javascript" charset="utf-8">
	
	var mensaje = "<%=mensajeRetorno%>";
	
	var esCapturistaUN = <%=esCapturistaUN%>;
	var esCapturistaOC = <%=esCapturistaOC%>;
	var esCapturistaDL = <%=esCapturistaDL%>;
	
	var esRevisorUN = <%=esRevisorUN%>;
	var esRevisorOC= <%=esRevisorOC%>;
	var esRevisorDL = <%=esRevisorDL%>;
	
	var esIntegradorUN = <%=esIntegradorUN%>;
	var esIntegradorOC= <%=esIntegradorOC%>;
	var esIntegradorDL = <%=esIntegradorDL%>;
	
	var oTable;
		function enviaUN(){
			if( confirm("Esta seguro que desea enviar a Unidad Normativa. Esta operacion no se puede revertir") ){
				var nfolio = $("#nfolio").val();
						$.ajax({
							url : '../anteproyecto/enviaUN',
							dataType : 'json',
							type :"POST",
							data : {
								"accion" : "ENVIA_UN",
								"FOLIO"	: nfolio
							},
							async : false,
							success : function(json) {
								var exito = json.success;
								if( exito == "true"){
									alert( "Envio completado exitosamente." );
									top.frames[0].location.href="../caso/principal.jsp";
								}else{
									alert("ATENCION! No fue posible generar el envío:\n" + json.data_1.result + 
											"\nIntente nuevamente. Si el problema persiste reportelo al administrador del sistema.");
								}
							},
							error : function(xhr, textStatus, errorThrown) {
								alert("Advertencia: " + xhr.responseText + "\nEstatus: "
										+ textStatus + "\n" + errorThrown);
								r = true;
							}
						});	
			}
			
		}
		function onPostSubmit(id_oper){//validaciones del boton enviar
			if($("#mensajesMotivo").val()!="")
				queryFormPost("UpdateLimpiaMotivoAnteProyecto", {async:false});
			return true;
		 }
		
		function ResponsableSiguiente(id_oper){
			var p = window.parent;
			if(id_oper==1){
				return $("#responsableSigInt").val();
  			}
	  		if(id_oper==2){
	  			 if($("#checkbox").is(':checked')){
	  				if(esIntegradorUN)
	  					return "INTEGRADOR_ANTPROYECTO_UN_"+$("#ueoc").val(); //Sacar la UN que le corresponde a la OC
	  				if(esIntegradorOC)
	  					return "INTEGRADOR_ANTPROYECTO_OC_"+$("#ueoc").val();
	  				if(esRevisorDL)
	  					return "INTEGRADOR_ANTPROYECTO_DL_"+$("#ueoc").val();
	  			 }
	  			 else{
	  				return $("#responsableSig").val();
	  			}
	  		}
	  		if(id_oper==3){
	  			 //return "INT_ANTPROYECTO_DL_"+p.gestion.getIdDlOrigen();
	  			if(esRevisorDL)
	  				return "INT_ANTPROYECTO_OC_"+$("#ueoc").val();
	  		}
	  		if(id_oper==4)
	  			return "INT_ANTPROYECTO_GRF";
	  		if(id_oper==5){
	  			if($("#checkbox").is(':checked')){
	  				return "INTEGRADOR_ANTPROYECTO_GRF";
	  			}else{
	  				return $("#responsableSigInt").val();
	  			}
	  		}
		 }
		
		function OperacionSiguiente(id_oper){
			if(id_oper==1)
				return "int_revisor";
			if(id_oper==2){
	  			 if($("#checkbox").is(':checked'))
	  				return "int_integrador";
	  			 else{
	  				 if($("#responsableSig").val().indexOf('INTEGRADOR')!=-1)
	  					 return "int_integrador";
	  				 else	
	  				 	return "captura_anteproyecto";
	  			}
	  		}
	  		if(id_oper==3)
	  			 return "int_revisor";
	  		if(id_oper==4)
	  			 return "int_grf";
	  		if(id_oper==5){
	  			if($("#checkbox").is(':checked')){
	  				return "integrador_grf";
	  			}else{
	  				return "int_integrador_un";
	  			}
	  		}
  		 }
		
		function onPostDisplay(id_oper){
		}

		function onSubmit(id_oper){
			var p = window.parent;
		  	if (id_oper==1){
		  			p.gestion.setFolio($("#FOLIO").val());
					p.gestion.setOperador($("#OPERADOR").val());
					p.gestion.setFechaDocumento($("#FECHA_SOLICITUD").val());
					p.gestion.setEjercicioFiscal($("#EJERCICIO_FISCAL").val());
					p.gestion.setConceptoMov("Integracion Anteproyecto");
					p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
					p.gestion.setIdAreaOrigen('<%=e.getClaveArea()%>');
					p.gestion.setIdDlOrigen('<%=usuario.getU_UR()%>');
				}
		  	if(id_oper==2 || id_oper==5){
		  		if($("#tmotivoRechazo").val()!=null && $("#tmotivoRechazo").val()!="")
		  			guardaMotivoRevision();
		  	}
		  	parent.document.getElementById("pb_send").disabled=false;
		  	return true;
	  	}
	
		  function onLoadPlantilla(id_oper){
			  if(id_oper==2){
			  	parent.document.getElementById("pb_save").disabled=true;
			  	parent.document.getElementById("pb_send").disabled=true;
			  }
			  if(id_oper==3 && esIntegradorOC){
				parent.document.getElementById("pb_save").disabled=true;
			  	parent.document.getElementById("pb_send").disabled=true;
			  }
			  if(id_oper==5 || id_oper==6){
				parent.document.getElementById("pb_save").disabled=true;
			  	parent.document.getElementById("pb_send").disabled=true;
			  }
			  parent.document.getElementById("pb_cancel").disabled=true;
			  queryFormPost("MotivoRechazoIntAPRead", {async:false});
			  if(mensaje!=null && mensaje!=""){
					$("#textmensajeError").val(mensaje);
					$('#dlgError').dialog('option', 'modal', true).dialog('open');
				}
			  
			  if($("#mensajesMotivo").val()!="")
				  $("#motivos").show();
			  /*if(id_oper==2 || id_oper==3){
				  parent.document.getElementById("pb_save").disabled=true;
				  parent.document.getElementById("pb_send").disabled=true;
				  parent.document.getElementById("pb_cancel").disabled=true;
			  }*/
			  queryFormPost("uedlUeoc", {async:false});
			  queryFormPost("intantarea", {async:false});
			  queryFormPost("intantdl", {async:false});
			  queryFormPost("intantoc", {async:false});
			  queryFormPost("intantun", {async:false});
			  queryFormPost("responsableIANT", {async:false});
			  queryFormPost("responsableINTIANT", {async:false});
				
				$("#CargaArchivoButton")
						.button()
						.click(
							function() {
									if ($("#CargaArchivo").val() == "")
											alert("Debe seleccionar un archivo a cargar.");
									else {
											$.blockUI({
														message : "Cargando Archivo. Por favor espere ......"
													});
											$("#ianteproyectoForm").submit();
									}
				});
				
				$("#integrar").button();
				$("#enviarUN").button();
				$("#enviarGRF").button();
				$("#integrarGRF").button();				
		  }
		  
		function validaCheck(){
			if($("#checkbox").is(':checked')){ //document.datosReintegro.autorizaRein[1].checked
				$("#integrar").show();
				$("#motivoRechazo").hide();
			}
			else{
				$("#integrar").hide();
				$("#motivoRechazo").show();
			}
		}
		
		function actualizaMontos(){
			$("#montoCEdicion").val($("#montoCalEdicion").val());
			$("#montoOEdicion").val($("#montoOptEdicion").val());
			$("#montoIEdicion").val($("#montoIrrEdicion").val());
			queryFormPost("UpdateDetalleIAnteProyecto",{async:false});
			$("#dlgEditar").dialog("close");
			$("#dt_detalleAnteProyecto").dataTable().fnDraw();
		}
		
		function creaAnte(){
			var strAction="../gstnmngr/IAnteproyecto";
			var incremento = $("#nIncremento").val();
			var decremento = $("#nDecremento").val();
			var tipoOperacion = $("#cIdTipoOperacion").val();
			var cDescripcion = $("#cDescripcion").val();
			var folio = $("#FOLIO").val();
			var id = $("#id_caso").val();
			var clArea = $("#area").val();
			var udl = $("#uedl").val();
			var uoc = $("#ueoc").val();
			var bdl = $("#dl").val();
			var boc = $("#oc").val();
			var bun = $("#un").val();
			var ugrupo = '<%= grupo%>';
		   	$.blockUI({message: "Procesando espere ......"});
	   		$.ajax({
	   			datatype:"html",
				type: "POST",
				url: strAction,
				data:{inserta:1,nIncremento:incremento,nDecremento:decremento,nCuenta:tipoOperacion,descripcion:cDescripcion,FOLIO:folio,id_caso:id,area:clArea,uresponsable:udl,unormativa:uoc,dl:bdl,oc:boc,un:bun,grupo:ugrupo},
				success: function (data,textStatus){
					$.unblockUI();
					alert("Creacion Exitosa.");
				},
				error: function (par) {alert(par);}
			});
		}
		
		function editar(){
			$('#dt_detalleAnteProyecto tr td ').click(function () {
				var aPos = oTable.fnGetPosition(this);
				var aData = oTable.fnGetData( aPos[0] );
				var folioEdicion = aData[0];
				var ep = aData[1];
				var montoc = aData[2];
				var montoo = aData[3];
				var montoi = aData[4];
				$("#folioEdicion").val(folioEdicion);
				$("#epEdicionR").val(ep);
				$("#claveSiaffE").val(ep.substring(0,55));
				$("#claveInternaE").val(ep.substring(56,64));
				$("#montoCalEdicion").val(montoc);
				$("#montoOptEdicion").val(montoo);
				$("#montoIrrEdicion").val(montoi);
				$('#dlgEditar').dialog('option', 'modal', true).dialog('open');
     		});
		}
		
		$(document).ready(function(){
			$("#motivos").hide();
			$("#motivoRechazo").hide();
			var id_oper=<%=id_oper%>;
			
			$("#checkbox").change(function() {
  				if(!$("#checkbox").is(':checked'))
  					parent.document.getElementById("pb_save").disabled=false;
  				else
  					parent.document.getElementById("pb_save").disabled=true;
			});
			
			 $(function() {		
				$('#dlgError').dialog({
					autoOpen: false,
					width: 1200,
					heigth: 1800
				});
			});
			 $(function() {		
				$('#dlgMotivo').dialog({
					autoOpen: false,
					width: 1200,
					heigth: 1800
				});
			});
			 $(function() {		
				$('#dlgEditar').dialog({
					autoOpen: false,
					width: 1200,
					heigth: 1800
				});
			});
			 var vista = "";
			 var nombreFolio="";
			 queryFormPost("esIntegradoAPRead", {async:false});
			 if($("#bIntegrado").val()=='S'){
				 vista = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_IntAnteProyectoIntegradoDetalle&qw=nFolioAnteProyecto="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>;
				 nombreFolio="nFolioCaptura";
			 }else{
				 vista = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_IAnteProyectoDetalle&qw=nFolioAnteProyecto="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>;
				 nombreFolio="nFolioAnteProyecto";
			 }
			oTable = $('#dt_detalleAnteProyecto').dataTable({
					"bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "100%",
					"bScrollCollapse": true,	
					"bServerSide": true,   
					sAjaxSource: vista,
					aoColumns: [
						{ sName: nombreFolio,bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter", sWidth:"40px" },			
						{ sName: "EP" },
						{ sName: "mCalculado" },
						{ sName: "mOptimo"},
						{ sName: "mIreductible"},
						{ sName: "editar"}
					],
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}
					
				});
			
				if(id_oper==3 && !esIntegradorUN){
					$('#dt_detalleAnteProyecto tr td').live('dblclick',function(){
						if(confirm("Desea desintegrar el trámite sus claves asociadas?")){
							$('#dlgMotivo').dialog('option', 'modal', true).dialog('open');
							var aPos = oTable.fnGetPosition(this);
							var aData = oTable.fnGetData( aPos[0] );
							var folioCaptura = aData[0];
							$("#nFolioCaptura").val(folioCaptura);
							queryFormPost("tIntAnteProyectoIntCapDelete",{async:false});
							queryFormPost("UpdateCasoIAnteProyecto",{async:false});
							oTable.fnDraw();
						}else {
							return;
						}
					});
				}
			
		});
		
		function integrarAnt(){
			if(confirm('¿Desea Integrar?'))
				$('#ianteproyectoForm').attr('action', "../gstnmngr/IntegraAnteproyecto").submit();
		}
		
		function intGRF(){
			$("#integraGRF").val('1');
			if(confirm('¿Desea Integrar?'))
				$('#ianteproyectoForm').attr('action', "../gstnmngr/IntegraAnteproyecto").submit();
		}
		
		function guardaMotivoRevision(){
			queryFormPost("UpdateMotivoRIAnteProyecto", {async:false});
		}
		
		function guardaMotivoIntegracion(){
			$("#dlgMotivo").dialog("close");
			$("#motivoRechazoI").val($("#motivoRechazoInt").val());
			queryFormPost("UpdateMotivoIIAnteProyecto", {async:false});
		}
	
		function cerrarMotivo(){
			$("#motivos").hide();
			queryFormPost("UpdateLimpiaMotivoAnteProyecto", {async:false});
		}
		
		function enviaGRF(){ 
			parent.document.getElementById("pb_save").click();
			parent.document.getElementById("pb_send").click();
		}
		</script>
</head>
 	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1>
				Integracion de Anteproyecto
			</h1>
		<form action="../gstnmngr/IAnteproyecto/CargaAnteProyecto" id="ianteproyectoForm" name="ianteproyectoForm" method="post"
			enctype="multipart/form-data">
		<input type="hidden" value="ianteproyectoForm" name="accion" id="accion" />
		<input type="hidden" id="nfolio" name="nfolio" value="<%=folio%>"/>
		<input type="hidden" name="nFolioCaptura" id="nFolioCaptura"/>
		<input type="hidden" id="uedl" name="uedl" value="<%=usuario.getU_UR() %>"/>
		<input type="hidden" id="ueoc" name="ueoc"/>
		<input type="hidden" id="bArea" name="bArea"/>
		<input type="hidden" id="dl" name="dl"/>
		<input type="hidden" id="oc" name="oc"/>
		<input type="hidden" id="un" name="un"/>
		
		<div id="motivos">
			<textarea id="mensajesMotivo" name="mensajesMotivo" rows="5" cols="30"></textarea>
			<!-- <input type="button" id="btnCerrarMotivo" value="Cerrar Mensaje" onclick="cerrarMotivo();"/>-->
		</div>  
		
			<%if(id_oper==2 && !esRevisorUN){ %>
				<div id="autoriza" >
					Autoriza: 
					<input name="checkbox" id="checkbox" type="checkbox" value="1" checked="checked" onclick="validaCheck();" />
				</div>
			<%} %>
			<%if(id_oper==5){ %>
				<div id="autoriza" >
					Autoriza: 
					<input name="checkbox" id="checkbox" type="checkbox" value="1" checked="checked" onclick="validaCheck();" />
				</div>
				<input type="button" name="integrarGRF" id="integrarGRF" value="Integrar" onclick="intGRF()"/>
				<div id="motivoRechazo" >
					Motivo del rechazo: 
					<textarea id="tmotivoRechazo" name="tmotivoRechazo" rows="3"  cols="50"></textarea>
				</div>
			<%} %>
			<%if(id_oper==2){ %>
				<input type="button" name="integrar" id="integrar" value="Integrar" onclick="integrarAnt()"/>
			<%} %>
			<%if(id_oper==2 && !esRevisorUN){ %>
				<div id="motivoRechazo" >
					Motivo del rechazo: 
					<textarea id="tmotivoRechazo" name="tmotivoRechazo" rows="3"  cols="50"></textarea>
				</div>
			<%} %>
			<%if(id_oper==3){ %>
				Para desintegrar una clave debe dar doble click.<br/> 
				Nota: Al desintegrar la clave se desintegrar&aacute; todo el trámite que contiene dicha clave, por lo que las demás claves de ese trámite también se desintegrar&aacute;n
			<%} %>
			<input type="hidden" id="cRamo" name="cRamo" value="16">
			<input type="hidden" id="id_caso" name="id_caso" value="<%=c.getIdCaso() %>">
			<input type="hidden" id="area" name="area" value="<%=e.getClaveArea()%>">
			<input type="hidden" id="grupo" name="grupo" value="<%=grupo %>">
			<input type="hidden" id="grupoInt" name="grupoInt" value="<%=grupoInt %>">
			<input type="hidden" id="responsableSig" name="responsableSig">
			<input type="hidden" id="bIntegrado" name="bIntegrado">
			<input type="hidden" id="motivoRechazoI" name="motivoRechazoI">
			<input type="hidden" id="responsableSigInt" name="responsableSigInt">
			<input type="hidden" id="integraGRF" name="integraGRF">
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>">
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
			<input type="hidden" id="EJERCICIO_FISCAL" name="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal %>"/>
			<table align="center">
					<tr><td>Folio SAI</td><td><input type="text" id="FOLIO" name="FOLIO" value="<%=c.getFolio() %>" readonly="readonly"/></td></tr>
					<%if(id_oper==1){ %>
					<tr>
						<td align="right">Archivo Excel:</td>
						<td align="left"><input type="file" size="30"
							name="CargaArchivo" id="CargaArchivo"></td>
						<td align="center" colspan="2"><input type="button"
							value="Cargar Archivo Anteproyecto" id="CargaArchivoButton"></td>
					</tr>
					<%} %>
					<tr>
						<td></td>
						<td></td>
					</tr>
			</table>
			<table id="dt_detalleAnteProyecto" class="display" cellspacing="0" cellpadding="2" align="center">
				<thead>
					<tr>
						<th>folio</th>
						<th>EP</th>
						<th>Monto Calculado</th>
						<th>Monto Óptimo</th>
						<th>Monto Irreductible</th>
						<th>Editar</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
					<%if(id_oper==3 && esIntegradorOC){ %>
					<input type="button" name="enviarUN" id="enviarUN" value="Enviar a Unidad Normativa" onclick="enviaUN()"/>
		<%} %>
		<%if(id_oper==3){ %>
			<div id="dlgMotivo" title="Motivo rechazo sistema Anteproyecto">
				<p>Motivo de Rechazo </p> <a rel=""></a>
				<table   class="display" id="grdAnteProyecto">
					<tr> <td>
						<textarea id="motivoRechazoInt" name="motivoRechazoInt" rows="16" cols="140"></textarea>
						<input type="button" id="btnMotivo" name="btnMotivo" value="Aceptar" onclick="guardaMotivoIntegracion()"/>
					</td></tr>
				</table>
			</div>
		<%} %>
		<%if(id_oper==4){ %>
				<!-- <input type="button" name="enviarGRF" id="enviarGRF" value="Enviar a Gerencia de Recursos Financieros" onclick="enviaGRF()"/>-->
			<div id="dlgMotivo" title="Motivo rechazo sistema Anteproyecto">
				<p>Motivo de Rechazo </p> <a rel=""></a>
				<table   class="display" id="grdAnteProyecto">
					<tr> <td>
						<textarea id="motivoRechazoInt" name="motivoRechazoInt" rows="16" cols="140"></textarea>
						<input type="button" id="btnMotivo" name="btnMotivo" value="Aceptar" onclick="guardaMotivoIntegracion()"/>
					</td></tr>
				</table>
			</div>
		<%} %>
			</form>
		</div>
		<div id="dlgError" title="Mensajes del sistema Anteproyecto">
			<p>Mensajes del sistema </p> <a rel=""></a>
			<table   class="display" id="grdAnteProyecto">
				<tr> <td>
					<textarea id="textmensajeError" name="textmensajeError" rows="16" cols="140"></textarea>
				</td></tr>
			</table>
		</div>
		<form method="post" name="frmEdicion" id="frmEdicion">
			<input type="hidden" name="folioEdicion" id="folioEdicion" value="<%=folio%>"/>
			<input type="hidden" name="claveSiaffE" id="claveSiaffE"/>
			<input type="hidden" name="claveInternaE" id="claveInternaE"/>
			<input type="hidden" name="montoCEdicion" id="montoCEdicion"/>
			<input type="hidden" name="montoOEdicion" id="montoOEdicion"/>
			<input type="hidden" name="montoIEdicion" id="montoIEdicion"/>
			<div id="dlgEditar" title="Edicion Anteproyecto">
				<p>Edicion Anteproyecto</p> <a rel=""></a>
				<table  id="AnteProyectoEdicion">
					<tr> <td>
						EP:</td><td><input type="text" readonly="readonly" id="epEdicionR" name="epEdicionR" size="70"/></td></tr>
						<tr><td>Monto Calculado:</td><td><input type="text" id="montoCalEdicion" name="montoCalEdicion" size="25"/></td></tr>
						<tr><td>Monto Optimo:</td><td><input type="text" id="montoOptEdicion" name="montoOptEdicion" size="25"/></td></tr>
						<tr><td>Monto Irreductible:</td><td><input type="text" id="montoIrrEdicion" name="montoIrrEdicion" size="25"/></td></tr>
						<tr><td><input type="button" id="aceptarE" name="aceptarE" value="Aceptar" onclick="actualizaMontos()"/>
					</td></tr>
				</table>
			</div>
		</form>
  </body>
</html>