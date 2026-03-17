<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
	   return;
	}
	String roles="";
	Map rol =usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoProcedimiento= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	
	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdUnidadEjecutora= (String)session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		cIdTipoProcedimiento=(String)session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo=(String)session.getAttribute(GestionInterface.ATT_ProConsecutivo);
	}
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  
    
    <title>Asistentes a la Junta de Aclaraciones</title>
    
	<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
			var roles='';
			var daTable;
			$(document).ready(function() {
				$("#tbs").val(14);
				showAndHideTabs();
			<%
					
				NegativaPestana NegPestana=new NegativaPestana();
				//NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				///// botones/////
				int imgAdjudicarC=0;
				int imgDesiertoC=0;
				int imgDevolverC=0;
				Iterator it1 = rol.entrySet().iterator();
				Role role = new Role();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Procedimiento","CaratulaProcedimiento");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAdjudicarCaratulaProc".equals(img)){
						imgAdjudicarC=1; 
					}
					if ("imgDesiertoCaratulaProc".equals(img)){
						imgDesiertoC=1;
					}
					if ("imgDevolverCaratulaProc".equals(img)){
						imgDevolverC=1;
					}
					
				}
			%>	
			roles="<%=roles%>";
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
			queryFormPost("informacionProcedimiento",{async:false});
			queryInnerDivPost("minimoMaximoTipoProcedimiento", {async : false});
			querySelectPost("tCatalogoSexo", "nIdSexo", {async: false});
			ocultaPestanas();
			asitentesJuntaAcla();
		});//Fin document ready
		function ocultaPestanas(){
			$("#EvaluacionProcedimiento" ).css("display", "none");
			$("#PreguntasProcedimiento" ).css("display", "none");
			$("#ArchivosProcedimiento" ).css("display", "none");
			$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			$("#presupuestoProcedimiento" ).css("display", "none");
			$("#precompromisoProcedimiento" ).css("display", "none");
		}
		function asitentesJuntaAcla(){
			var qw="cEjercicio='"+$('#cEjercicio').val()+"' and cIdTipoProcedimiento='"+$('#cIdTipoProcedimiento').val()+"' and cIdUnidadEjecutora='"
					+$('#cIdUnidadEjecutora').val()+"' and nIdConsecutivoProced="+$('#nIdConsecutivo').val()+" and nIdTipoAsistente="+$('#nIdTipoAsistente').val() ;
			oTable = $("#tblAsistentesJuntaAclaraciones").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mAsistentesProcedimiento&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{sName: "nombre"},
					{sName: "apellidoPat"},
					{sName: "apellidoMat"},
					{sName: "cSexo"},
					{sName: "rfc"},
					{sName: "razonSocial"},
					{sName: "borrar"}
				]
			});
		}
		function borrar(rfc){
			$("#rfcEliminar").val(rfc);
			swal({
				title: "\xBFEst\xE1s seguro de borrar el aistente?",
				text: "",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
				}else{
					queryFormPost("eliminaPosiblesContratantes",  {async : false, 
						callback : function() 
						{
							asitentesJuntaAcla();
							swal("Asistente Borrado.",{icon:"info",button: "Cerrar"});
							//Guarda en la Bitácora
							guardaBitacora("ELIMINA_ASITENTE_JUNTA_ACRARACIONES_"+rfc);
						}
					});
				}
			});
		}
		function guardaBitacora(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function agregaAsistente(){
			if(parseInt($("#nIdCategoria").val(),10)<10){
				if(validaCampos()){
					queryFormPost("insertAsistentesJuntaAclaraciones",  {async : false, 
						callback : function() 
						{
							asitentesJuntaAcla();
							//Guarda en la Bitácora
							guardaBitacora("GUARDA_ASITENTE_JUNTA_ACRARACIONES");
							cleanCampos();
						}
					});
				}
			}else{
				swal("Aplica solo para licitación e invitación a cuando menos 3.",{icon:"info",button: "Cerrar"});
			}
		}
		function cleanCampos(){
			$("#catalogoProveedores").val('');
			$("#nombreRepresentante").val('');
			$("#apellidoPatRepresentante").val('');
			$("#apellidoMatRepresentante").val('');
		}
		function validaCampos(){
			var resp=true;
			var msg="";
			var token="";
			if($("#catalogoProveedores").val()==""){
				msg="Favor de seleccionar un proveedor.";
				token="\n";
			}
			if($("#nombreRepresentante").val()==""){
				msg+=token+"Favor de agregar un nombre.";
				token="\n";
			}
			if($("#apellidoPatRepresentante").val()==""){
				msg+=token+"Favor de agregar el apellido paterno.";
				token="\n";
			}
			if($("#apellidoMatRepresentante").val()==""){
				msg+=token+"Favor de agregar el apellido materno.";
			}
			if(msg!=""){
				resp=false;
				swal(msg,{icon:"info",button: "Cerrar"});
			}
			return resp;
		}
	</script>
  </head>
  
  <body id="dt_example">
	<form action="">
		<div  id="container" class="container" style="width: 98%;">
			<fieldset>
				<legend>Informaci&oacute;n del Procedimiento</legend>
					<div style="width: 98%;">
					<table border="0" align="center" style="width: 100%">
						<tr>
					    	<td align="left">
					    		<input type="text"  id="lblUnidadUsuario" name="lblUnidadUsuario" readonly style="border-width:0; background-color:transparent; width: 100%" value=""/>
					    	</td>
				    	</tr>
						<tr align="left">
							<td >
								<input name="lblcIdProcedimiento" id="lblcIdProcedimiento" type="text" style="border-width:0; background-color:transparent; width: 100%" readonly="readonly"/>  
							</td>								
						</tr>
						<tr align="left">
							<td >
								<input name="lblEstadoProcedimiento" id="lblEstadoProcedimiento" type="text"  style="border-width:0; background-color:transparent; width: 100% " />
							</td>								
						</tr>
						<tr align="left">
							<td >
							<input name="lblcIdConsolidado" id="lblcIdConsolidado" type="text" style="border-width:0; background-color:transparent; width: 100%" readonly="readonly" />
							</td>								
						</tr>
						<tr align="left">
							<td >
							<input name="lbltipoProceso" id="lbltipoProceso" type="text"  style="border-width:0; background-color:transparent; width: 100%" readonly="readonly" />
							</td>								
						</tr>
						<tr align="left">
							<td >
							<input name="lblcCategoria" id="lblcCategoria" type="text"  style="border-width:0; background-color:transparent; width: 100%" readonly="readonly" />
							</td>								
						</tr>
						<tr align="left">
							<td >
								<div id="tblMinimoMaximo" style="width:100%;"></div>
							</td>
						</tr>
	 			</table>
	 			</div>
			</fieldset>
			<br />
			<fieldset>
				<legend>Relaci&oacute;n de Asistentes a la Junta de Aclaraciones</legend>
				<div align="left">
					<table align="left" >
						<tr>
							<td>
							Nombre:<input type="text" id="nombreRepresentante" name="nombreRepresentante" value="" />
							</td>
						</tr>
						<tr>
							<td>
							Apellido Paterno:<input type="text" id="apellidoPatRepresentante" name="apellidoPatRepresentante" value="" />
							</td>
						</tr>
						<tr>
							<td>
								Apellido Materno:<input type="text" id="apellidoMatRepresentante" name="apellidoMatRepresentante" value="" />
							</td>
						</tr>
						<tr>
							<td>
							Sexo:<select id="nIdSexo" name="nIdSexo"></select>
							</td>
						</tr>
						<tr>
							<td>
							Empresa Representa:<input type="text" class="AyudaSyC obligatorio desahabilitado" maxlength="40" size="45px" name="catalogoProveedores" id=catalogoProveedores readonly />
							</td>
						</tr>
						
						<tr>
							<td>
								<input type="button" id="brnAgregar" name="brnAgregar" value="Agregar" onclick="agregaAsistente()" class="btnInterfaceBG ui-button ui-corner-all"/>
							</td>
						</tr>
					</table>
				</div>
				<div>
					<table id="tblAsistentesJuntaAclaraciones" class="display" >
						<thead >
							<tr>
								<th align="center">Nombre</th>
								<th align="center">Apellido Paterno</th>
								<th align="center">Apellido Materno</th>
								<th align="center">Sexo</th>
								<th align="center">RFC</th>
								<th align="center">Razon Social</th>
								<th align="center">Borrar</th>
							</tr>										
						</thead>
					</table>
				</div>
			</fieldset>
		</div>
		<input type="hidden" id="cIdConsolidado" name="cIdConsolidado" value=""/>
		<input type="hidden" id="nIdTipoAsistente" name="nIdTipoAsistente" value="2"/>
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
		<input type="hidden" id="cAccion" name="cAccion" value=""/>
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin()%>"/>
		<input type="hidden" id="nIdCategoria" name="nIdCategoria" value=""/>
		<input type="hidden" id="rfcEliminar" name="rfcEliminar" value=""/>
	</form>
  </body>
</html>
