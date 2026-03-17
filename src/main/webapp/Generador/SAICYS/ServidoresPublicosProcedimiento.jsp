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
  
    
    <title> Servidores P&uacute;blicos que Asisten a la Junta de Aclaraciones</title>
    
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
				$("#tbs").val(15);
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
			ocultaPestanas();
			servidoresPublicos();
		});//Fin document ready
		function ocultaPestanas(){
			$("#EvaluacionProcedimiento" ).css("display", "none");
			$("#PreguntasProcedimiento" ).css("display", "none");
			$("#ArchivosProcedimiento" ).css("display", "none");
			$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			$("#presupuestoProcedimiento" ).css("display", "none");
			$("#precompromisoProcedimiento" ).css("display", "none");
		}
		function servidoresPublicos(){
			var qw="cEjercicio='"+$('#cEjercicio').val()+"' and cIdTipoProcedimiento='"+$('#cIdTipoProcedimiento').val()+"' and cIdUnidadEjecutora='"
					+$('#cIdUnidadEjecutora').val()+"' and nIdConsecutivoProced="+$('#nIdConsecutivo').val()+" and nIdTipoAsistente="+$('#nIdTipoAsistente').val() ;
			oTable = $("#tblServidoresPublicos").dataTable({
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
					{sName: "numEmpleado"},
					{sName: "rfc"},
					{sName: "nombre"},
					{sName: "apellidoPat"},
					{sName: "apellidoMat"},
					{sName: "cSexo"},
					{sName: "numPlaza"},
					{sName: "descripPlaza"},
					{sName: "borrar"}
				]
			});
		}
		function borrar(rfc){
			$("#rfcEliminar").val(rfc);
			swal({
				title: "\xBFEst\xE1s seguro de borrar al servidor público?",
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
							
							servidoresPublicos();
							swal("Servidor Público Borrado.",{icon:"info",button: "Cerrar"});
							//Guarda en la Bitácora
							guardaBitacora("ELIMINA_SERVIDOR_PÚBLICO_"+rfc);
						}
					});
				}
			});
		}
		function agregaServPublico(){
			if( parseInt($("#nIdCategoria").val(),10)<10){
				if(validaCampos()){
					queryFormPost("insertServidorPublico",  {async : false, 
						callback : function() 
						{
							servidoresPublicos();
							//Guarda en la Bitácora
							guardaBitacora("Guara_Servidor_Público");
							$("#ServidoresPublicos").val('');
						}
					});
				}
			}else{
				swal("Aplica solo para licitación e invitación a cuando menos 3.",{icon:"info",button: "Cerrar"});
			}
		}
		function guardaBitacora(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function validaCampos(){
			var resp=true;
			if($("#ServidoresPublicos").val()==""){
				resp=false;
				swal("Favor de seleccionar un servidor público.",{icon:"info",button: "Cerrar"});
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
				<legend>Servidores P&uacute;blicos que Asisten a la Junta de Aclaraciones</legend>
				<div>
					<table style="width: 100% " align="left">
						<tr>
							<td align="left">Servidor Público:
								<input type="text" class="AyudaSyC obligatorio desahabilitado" maxlength="40" size="50" name="ServidoresPublicos" id="ServidoresPublicos" readonly />
							</td>
							<td align="right">
								<input type="button" id="agregar" name="agregar" value="Agregar" onclick="agregaServPublico()" class="btnInterfaceBG ui-button ui-corner-all" />
							</td>
						</tr>
					</table>
				</div>
				<div>
					<table id="tblServidoresPublicos" class="display" >
						<thead >
							<tr>
								<th align="center"># Empleado</th>
								<th align="center">RFC</th>
								<th align="center">Nombre</th>
								<th align="center">Apellido Paterno</th>
								<th align="center">Apellido Materno</th>
								<th align="center">Sexo</th>
								<th align="center">Plaza</th>
								<th align="center">Descripci&oacute;n Plaza</th>
								<th align="center">Borrar</th>
							</tr>										
						</thead>
					</table>
				</div>
			</fieldset>
		</div>
		<input type="hidden" id="cIdConsolidado" name="cIdConsolidado" value=""/>
		<input type="hidden" id="nIdTipoAsistente" name="nIdTipoAsistente" value="3"/>
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
		<input type="hidden" id="cAccion" name="cAccion" value=""/>
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin()%>"/>
		<input type="hidden" id="nIdCategoria" name="nIdCategoria" value=""/>
		<input type="hidden" id="rfcEliminar" name="rfcEliminar" value=""/>
		<input type="hidden" id="numEmpleado" name="numEmpleado" value=""/>
		<input type="hidden" id="rfcEmpleado" name="rfcEmpleado" value=""/>
		<input type="hidden" id="nombreEmpleado" name="nombreEmpleado" value=""/>
		<input type="hidden" id="apellidoPatEmpleado" name="apellidoPatEmpleado" value=""/>
		<input type="hidden" id="apellidoMatEmpleado" name="apellidoMatEmpleado" value=""/>
		<input type="hidden" id="numPlazaEmpleado" name="numPlazaEmpleado" value=""/>
		<input type="hidden" id="puestoEmpleado" name="puestoEmpleado" value=""/>
		<input type="hidden" id="cCurp" name="cCurp" value=""/>
		<input type="hidden" id="nIdSexo" name="nIdSexo" value="1"/>
		
		
	</form>
  </body>
</html>