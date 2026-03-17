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
  
    
    <title>Podibles Contratantes</title>
    
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
				$("#tbs").val(13);
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
			posiblesContratantes();
			querySelectPost("tCatalogoSexo", "nSexo", {async: false});
		});//Fin document ready
		function ocultaPestanas(){
			$("#EvaluacionProcedimiento" ).css("display", "none");
			$("#PreguntasProcedimiento" ).css("display", "none");
			$("#ArchivosProcedimiento" ).css("display", "none");
			$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			$("#presupuestoProcedimiento" ).css("display", "none");
			$("#precompromisoProcedimiento" ).css("display", "none");
		}
		function posiblesContratantes(){
			var qw="cEjercicio='"+$('#cEjercicio').val()+"' and cIdTipoProcedimiento='"+$('#cIdTipoProcedimiento').val()+"' and cIdUnidadEjecutora='"
					+$('#cIdUnidadEjecutora').val()+"' and nIdConsecutivoProced="+$('#nIdConsecutivo').val()+" and nIdTipoAsistente="+$('#nIdTipoAsistente').val() ;
			oTable = $("#tblPosiblesContratantes").dataTable({
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
					{sName: "rfc"},
					{sName: "nombre"},
					{sName: "apellidoPat"},
					{sName: "apellidoMat"},
					{sName: "cSexo"},
					{sName: "razonSocial"},
					{sName: "borrar"}
				]
			});
		}
		function borrar(rfc){
			$("#rfcEliminar").val(rfc);
			swal({
				title: "\xBFEst\xE1s seguro de borrar el proveedor?",
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
						callback : function(){
							posiblesContratantes();
							swal("Proveedor Borrado.",{icon:"info",button: "Cerrar"});
							//Guarda en la Bitácora
							guardaBitacora("ELIMINA_CONTRATANTE_"+rfc);
						}
					});
				}
			});
		}
		function agregaProveedor(){
			if(parseInt($("#nIdCategoria").val(),10)<10){
				if($('#checkprovExisteEnSAI').is(':checked')){
					if(validaCampos()){
						queryFormPost("insertPosiblesContratantes",  {async : false, 
							callback : function() 
							{
								posiblesContratantes();
								//Guarda en la Bitácora
								guardaBitacora("Guara_Posibles_Contratantes");
								$("#catalogoProveedores").val('');
							}
						});
					}
				}else{
					$("#catalogoProveedores").val($("#cIdRFC1").val()+"-"+$("#cIdRFC2").val()+"-"+$("#cIdRFC3").val());
					if(validaCamposCapturaManual()){
						queryFormPost("insertPosiblesContratantesNoExistentesEnSAI",  {async : false, 
							callback : function() 
							{
								posiblesContratantes();
								//Guarda en la Bitácora
								guardaBitacora("Guara_Posibles_Contratantes");
								clearInputs();
							}
						});
					}
				}
			}else{
				swal("Aplica solo para Licitación e Invitación a cuando menos 3.",{icon:"info",button: "Cerrar"});
			}
		}
		function clearInputs(){
			$("#catalogoProveedores").val('');
			$("#cIdRFC1").val('');
			$("#cIdRFC2").val('');
			$("#cIdRFC3").val('');
			$("#cNombre").val('');
			$("#cApellidoPaterno").val('');
			$("#cApellidoMaterno").val('');
			$("#cRazonSocial").val('');
		}
		function guardaBitacora(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function validaCampos(){
			var resp=true;
			if($("#catalogoProveedores").val()==""){
				resp=false;
				swal("Favor de seleccionar un proveedor.",{icon:"info",button: "Cerrar"});
			}
			return resp;
		}
		function provExisteEnSAI(){
			if($('#checkprovExisteEnSAI').is(':checked')){
				$("#trCatProveedor").show();
				$("#tblCapturaManual").hide();
			}else{
				$("#trCatProveedor").hide();
				$("#tblCapturaManual").show();
				tipoPersonaChange();
			}
		}
		function validaCamposCapturaManual() {
			var tipo = $("#cIdTipoPersonaRFC").val();
			if($("#catalogoProveedores").val()==""){
				swal("Favor de capturar el RFC.",{icon:"info",button: "Cerrar"});
				return false;
			}
			if (tipo == 2) {//FISICA
				if($("#cNombre").val()==""){
					swal("Favor de capturar el nombre.",{icon:"warning",button: "Cerrar"});
					return false;
				}
				if($("#cApellidoPaterno").val()==""){
					swal("Favor de capturar el apellido paterno.",{icon:"warning",button: "Cerrar"});
					return false;
				}
				if($("#cApellidoMaterno").val()==""){
					swal("Favor de capturar el apellido materno.",{icon:"warning",button: "Cerrar"});
					return false;
				}
			}else{
				if($("#cRazonSocial").val()==""){
					swal("Favor de capturar la razón social.",{icon:"warning",button: "Cerrar"});
					return false;
				}
			}
			return true;
		}
		function tipoPersonaChange() {
			var tipo = $("#cIdTipoPersonaRFC").val();
			if (tipo == 2) {//FISICA
				document.getElementById('cIdRFC1').maxLength = 4;
				$("#cRazonSocial").val("");
				$("#cNombre").val("");
				$("#cApellidoPaterno").val("");
				$("#cApellidoMaterno").val("");
				
				$("#trRazonSocial").hide();
				$("#trNombre").show();
				$("#trApellidoPaterno").show();
				$("#trApellidoMaterno").show();
				
			}else{//MORAL
				$("#cIdRFC1").val($("#cIdRFC1").val().substring(0, 3));
				document.getElementById('cIdRFC1').maxLength = 3;
				$("#cRazonSocial").val("");
				$("#cNombre").val("");
				$("#cApellidoPaterno").val("");
				$("#cApellidoMaterno").val("");
				
				$("#trRazonSocial").show();
				$("#trNombre").hide();
				$("#trApellidoPaterno").hide();
				$("#trApellidoMaterno").hide();
					
			}
				
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
								<input name="lblcIdProcedimiento" id="lblcIdProcedimiento" type="text" style="border-width:0; background-color:transparent;width: 100%" readonly="readonly"/>  
							</td>								
						</tr>
						<tr align="left">
							<td >
								<input name="lblEstadoProcedimiento" id="lblEstadoProcedimiento" type="text"  style="border-width:0; background-color:transparent;width: 100% " />
							</td>								
						</tr>
						<tr align="left">
							<td >
							<input name="lblcIdConsolidado" id="lblcIdConsolidado" type="text" style="border-width:0; background-color:transparent;width: 100%" readonly="readonly" />
							</td>								
						</tr>
						<tr align="left">
							<td >
							<input name="lbltipoProceso" id="lbltipoProceso" type="text"  style="border-width:0; background-color:transparent;width: 100%" readonly="readonly" />
							</td>								
						</tr>
						<tr align="left">
							<td >
							<input name="lblcCategoria" id="lblcCategoria" type="text"  style="border-width:0; background-color:transparent;width: 100%" readonly="readonly" />
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
				<legend>Posibles Contratantes</legend>
				<div>
					<table style="width: 100% " align="left">
						<tr>
							<td>
								Proveedor existe en SAI: 
								<input type="checkbox" id="checkprovExisteEnSAI" name="checkprovExisteEnSAI" onclick="provExisteEnSAI()" checked="checked"/>
							</td>
						</tr>
						<tr id="trCatProveedor">
							<td align="left">Proveedores:
								<input type="text" class="AyudaSyC obligatorio desahabilitado" maxlength="40" size="50" name="catalogoProveedores" id=catalogoProveedores readonly />
							</td>
						</tr>
						<tr id="trCapturaManual" >
							<table align="left" style="display: none;" id="tblCapturaManual">
								<tr>
									<td style="width: 120px; ">Tipo Persona: </td>
									<td style="width: 500px; ">
										<select id="cIdTipoPersonaRFC" name="cIdTipoPersonaRFC" onChange="tipoPersonaChange()" >
											<option id="1" value="1" selected>MORAL</option>
											<option id="2" value="2">FISICA</option>
										</select>
									</td>
								</tr>
								<tr>
									<td style="width: 120px; ">Sexo: </td>
									<td style="width: 500px; ">
										<select id="nSexo" name="nSexo" >
										</select>
									</td>
								</tr>
								<tr>
									<td style="width: 120px; ">RFC: </td>
									<td style="width: 500px; ">
										<input type="text" name="cIdRFC1" id="cIdRFC1" maxLength="3" style="width: 4em;" onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);">-
										<input type="text" name="cIdRFC2" id="cIdRFC2" style="width: 4em;" maxlength="6" onKeyPress="Change(this,event);return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);" />-
										<input type="text" name="cIdRFC3" id="cIdRFC3" style="width: 4em;" maxlength="3" onKeyPress="return(onlyNumbersAndLetters(event,this));" onblur="ChangeCase(this);" />	
									</td>
								</tr>
								<tr id="trRazonSocial">
									<td style="width: 120px; ">Raz&oacute;n Social: </td>
									<td style="width: 500px; ">
										<input type="text" name="cRazonSocial" id="cRazonSocial" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
								</tr>
								<tr id="trNombre">
									<td style="width: 120px; ">Nombre: </td>
									<td style="width: 500px; ">
										<input type="text" name="cNombre" id="cNombre" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
								</tr>
								<tr id="trApellidoPaterno">
									<td style="width: 120px; ">Apellido Paterno: </td>
									<td style="width: 500px; ">
										<input type="text" name="cApellidoPaterno" id="cApellidoPaterno" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
								</tr>
								<tr id="trApellidoMaterno">
									<td style="width: 120px; ">Apellido Materno: </td>
									<td style="width: 500px; ">
										<input type="text" name="cApellidoMaterno" id="cApellidoMaterno" style="width: 30em;" maxlength="300" onblur="ChangeCase(this);" />
								</tr>
							</table>
						</tr>
						<tr >
							<td >
								<input type="button" id="agregar" name="agregar" value="Agregar" onclick="agregaProveedor()" class="btnInterfaceBG ui-button ui-corner-all"/>
							</td>
						</tr>
					</table>
				</div>
				<div>
					<table id="tblPosiblesContratantes" class="display" >
						<thead >
							<tr>
								<th align="center">RFC</th>
								<th align="center">Nombre</th>
								<th align="center">Apellido Paterno</th>
								<th align="center">Apellido Materno</th>
								<th align="center">Sexo</th>
								<th align="center">Razon Social</th>
								<th align="center">Borrar</th>
							</tr>										
						</thead>
					</table>
				</div>
			</fieldset>
		</div>
			
		<input type="hidden" id="cIdConsolidado" name="cIdConsolidado" value=""/>
		<input type="hidden" id="nIdTipoAsistente" name="nIdTipoAsistente" value="1"/>
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
		<input type="hidden" id="cAccion" name="cAccion" value=""/>
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin()%>"/>
		<input type="hidden" id="nIdCategoria" name="nIdCategoria" value=""/>
		<input type="hidden" id="rfcEliminar" name="rfcEliminar" value=""/>
		
	</form>
  </body>
</html>
