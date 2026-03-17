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
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Procedimiento</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$("#tbs").val(8);
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
			Map botones=nb.getBotones(roles,"Procedimiento","RequisitosProcedimiento");
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
	$("#usuarioLogin").val('<%=usuario.getLogin()%>');
	$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
	$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
	$("#usuarioRoleProcedimiento").val('<%=roles%>');		
	querySelectPost("CategoriaRead", "cboCategoria", {async : false});
	$("#usuarioLogin").val('<%=usuario.getLogin()%>');
	$("#EvaluacionProcedimiento").attr("disabled", true);
	$("#CotizacionProcedimiento").attr("disabled", true);
	muestraInformacionCaratula();
	habilitarPestanas();
	mostrarRequisitos();
	
    
    	$("#btnGuardarRequisitosProcedimiento").button().click(function() {
			var aTrs = $('#tblRequisitos').dataTable().fnGetNodes();	
			for ( var i = 0 ; i < aTrs.length; i++){ 	
				var nTr = $('#tblRequisitos').dataTable().fnGetData(i);	
     			var id = nTr[0];
				$("#cIdRequisito").val(id);
				if ($("#requisitoR" + id).is(':checked')) $("#cRequisitoRequerido").val('checked="checked"');
				else $("#cRequisitoRequerido").val('');
				if ($("#requisitoC" + id).is(':checked')) $("#cRequisitoCumple").val('checked="checked"');
				else $("#cRequisitoCumple").val('');
				$("#cRequisitoObs").val($("#requisitoO" + id).val());
				queryFormPost("mRequisitosProcedimientoUpdate", {async : false});
			}
			$("#cProcedimientoCumple").val(1);
			queryFormPost("mRequisitosProcedimientoCumpleRead", {async : false});
			if ($("#cProcedimientoCumple").val() == 1) {
				$("#cProcedimientoCumple").val(true);
				queryFormPost("mProcedimientoCumpleRequisitosUpdate", {async : false});
				if ($("#tipoProceso").val() == 1) {
					$("#CotizacionProcedimiento").attr("disabled", false);
					alert("Requisitos guardados correctamente. Se cumplen todos los requeridos ahora puede realizar la cotización.");
				}
				else if ($("#tipoProceso").val() == 2) {
					$("#EvaluacionProcedimiento").attr("disabled", false); 
					alert("Requisitos guardados correctamente. Se cumplen todos los requeridos ahora puede realizar la evaluación.");
				}
			}
			else {
				$("#CotizacionProcedimiento").attr("disabled", true);
				$("#EvaluacionProcedimiento").attr("disabled", true);
				$("#cProcedimientoCumple").val(false);
				queryFormPost("mProcedimientoCumpleRequisitosUpdate", {async : false});
				if ($("#tipoProceso").val() == 1) {
					alert("Requisitos guardados correctamente. Es necesario cumplir con todos los requeridos para poder realizar la cotización.");
				}
				else if ($("#tipoProcesor").val() == 2) {
					alert("Requisitos guardados correctamente. Es necesario cumplir con todos los requeridos para poder realizar la evaluación.");
				}
			}
		});
		
	});
	

	function muestraInformacionCaratula (){
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
		   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		   if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO"){
		   		$("#btnGuardarCaratulaProcedimiento" ).css("visibility","hidden");
		   }
		   $("#caratulaAnterior").val($("#cboCategoria").val());
		   if($("#tipoProcedimiento").val()=='PS'){
		   	 $("#esServicio").val(1);
		   }else{
		   	 $("#esServicio").val(0);
		   }
		   queryInnerDivPost("llenaFechasProcedimientoCaratula",{async:false});
		   var cadena_campos=$("#cIdConsolidado").val().split('-');
			$("#TipoConsolidado").val(cadena_campos[0]); 
			$("#ConsecutivoConsolidado").val(cadena_campos[2]);
			
			if($("#nIdEstado").val()==1){
			 queryFormPost("apartadoConsolidado",{async:false});
		   }else{
			 if ($("#documentoAplicado").val()==0){
			 	 queryFormPost("sumPrecomProcedimiento",{async:false});
			 }else{
			 	 queryFormPost("apartadoConsolidado",{async:false});
			 }				
		   }
		   queryFormPost("mValidaPrecompromiso",{async:false});
		   
	}
	
	
	function validaModificarProcedimiento(){ 
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
	
	function adjudicarProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO")
				return;	
		queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
		if($("#cProcedimientoCumple").val() == false){
			alert("El procedimiento no cumple los requisitos.");
			return;
		}

		queryFormPost("checaProveedoresAsignados",{async:false});
		if($("#tieneProveedor").val()=="0"){
			alert("El procedimiento no tiene ningun proveedor asociado");
			return;
		}
		//valida el procedimiento de invitacion a tres
		queryFormPost("fn_mVerificaAplicaPartidaDesiertaRead",{async:false});
		if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("tres personas") >= 0 && ($("#ctipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#ctipoProceso").val(),10) == 0)){			
				if($("#tieneProveedor").val() < 3){
					if(confirm("El procedimiento solo tienes "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?")){
					    $("#EstadoCaptura").val(3);
						queryFormPost("sp_ProcedimientoDesierto",{async:false});
						actualizaDatosProcedimientoDesierto();
						return;
					}
				}else{
					//valida que se hayan cargado mas de 3 cotizaciones				
					if($("#cPartidaDesierta").val() != ""){
						if($("#cPartidaDesierta").val().toUpperCase() == "SI"){
							if(confirm("Existen partidas que tienen menos de 3 cotizaciones si contin\xFAa se cambiaran a desiertas autom\xE1ticamente.\n\xBFDesea continuar?")){
								queryFormPost("sp_mAplicaPartidaDesierta",{async:false});
								return;
							}
						}
					}
				}
			}
	
			if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("adjudicaci") >= 0 && $("#cCategoriaDescripcion").val().toLowerCase().indexOf("n directa") >= 0 && ($("#ctipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#ctipoProceso").val(),10) == 0)){
				if($("#tieneProveedor").val() < 3){
					if(confirm("El procedimiento solo tiene "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?")){
					    $("#EstadoCaptura").val(3);
						queryFormPost("sp_ProcedimientoDesierto",{async:false});
						actualizaDatosProcedimientoDesierto();
						return;
					}
				}
			}
			
			var proc=""+$("#cEjercicio").val()+" ,"+$("#cIdTipoProcedimiento").val()+","+$("#cIdUnidadEjecutora").val()+","+$("#nIdConsecutivo").val()+","+$("#tipoProceso").val()+"";
			$.getJSON("../../servlet/ProcedimientoServlet?cmd=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
		       for(var i = 0; i < j.length; i++){
	             var col=j[i].Col1
	           }
	
	                switch(col){
					case "0":  
					alert("El Procedimiento  se ha Adjudicado Correctamente");
					actualizaDatosProcedimientoAdjudicado();
					break;
					case "1":  
					alert("Hubo un error en la Adjudicacion");
					break;
					case "2":  
					alert("Existen Proveedores sin Partidas Adjudicadas,eliminelos o agregue lineas");
					break;
					case "15":  
					alert("No puede adjudicar el procedimiento completo,le falta asignar ganador y cumple con la evaluacion tecnica ");
					break;
					
				}      
			});
		}
  	}
  	
  	function actualizaDatosProcedimientoDesierto(){
		$("#nIdEstadoProcedimiento").val("DESIERTO");
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	    imagen=imagen.substring(11,56);
	    var ima=document.getElementById("imgEstado");
	    ima.src=imagen;
	    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});	
    }
  
  	function actualizaDatosProcedimientoAdjudicado(){
		$("#nIdEstadoProcedimiento").val("ADJUDICADO");
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		var imagen=$("#imagenEstadoProcedimiento").val();
	    imagen=imagen.substring(11,56);
	    var ima=document.getElementById("imgEstado");
	    ima.src=imagen;
	    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});		
   }
   
   function desiertoProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" ||$("#nIdEstadoProcedimiento").val()=="DESIERTO  " )
			return;	
			
			if (confirm("¿Desea declarar desierto el Procedimiento " + $("#cIdProcedimiento").val())) {
				$("#EstadoCaptura").val('3'); 
				queryFormPost("sp_ProcedimientoDesierto",{async:false});
				actualizaDatosProcedimientoDesierto();
			}else{
				return;
			}
		}
	}
	
	function devolverProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="CAPTURADO "){
				alert("No se puede devolver el procedimiento, porque su estado no lo permite");
				return;
			}
			var proc=""+$("#cEjercicio").val()+" ,"+$("#cIdTipoProcedimiento").val()+","+$("#cIdUnidadEjecutora").val()+","+$("#nIdConsecutivo").val()+"";
			$.getJSON("../../servlet/ProcedimientoServlet?cmd=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
	
				for(var i = 0; i < j.length; i++){
		                 var col=j[i].Col1
		        }
		
		        switch(col){
					case "1":  
					alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS CONTRATOS APROBADOS');
					break;
					case "2":  
					alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS CONTRATOS TIENE UNO O MAS OFICIOS APROBADOS');
					break;
					case "3":  
					alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE TIENE UNO O MAS PEDIDOS APROBADOS');
					break;
					case "4":  
					alert('NO SE PUEDE DEVOLVER EL PROCEDIMIENTO PORQUE LOS PEDIDOS TIENEN UNO O MAS OFICIOS APROBADOS');
					break;
					case "5":  
					alert('ERROR DE BORRADO DE TABLA');
					break;
					case "6":  
					alert("Se devuelve correctamente el procedimiento");
					actualizaDatosProcedimientoDevuelto();
					break;
				}
	   		});
		}
	}
	
	function actualizaDatosProcedimientoDevuelto(){
		$("#nIdEstadoProcedimiento").val("CAPTURADO");
	    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	    imagen=imagen.substring(11,56);
	    var ima=document.getElementById("imgEstado");
	    ima.src=imagen;
	    queryFormPost("llenaEstadoImagenProcedimiento",{async:false});
 	}
 	
 	function mostrarRequisitos() {
		$('#tblRequisitos').dataTable( {    
		"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
		"bFilter" : false,
		"bDestroy" : true,
		"bJQueryUI": true,
		"bAutoWidth" : false,
		"iDisplayLength": 20,
		"sPaginationType": "full_numbers",
		//"sScrollY": 250,
				"oLanguage": {
				sProcessing: "Procesando...",
				sLengthMenu: "<h2><b>Requisitos</b></h2>",
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mRequisitosProcedimiento('" + $("#cIdProcedimiento").val() + "')",
			aaSorting: [[ 0, "asc" ]] ,
			aoColumns: [
				{ sName: "idRequisito" },
				{ sName: "requisito" },
				{ sName: "descripcion" },
				{ sName: "requerido" },
				{ sName: "cumple" },
				{ sName: "obs" }
			]
		});	
	}
	
	function habilitarPestanas(){
		if($("#tipoProceso").val()==1){ //proceso corto
			$("#EvaluacionProcedimiento" ).css("display", "none");
			$("#PreguntasProcedimiento" ).css("display", "none");
			$("#ArchivosProcedimiento" ).css("display", "none");
			$("#CotizacionProcedimiento" ).css("display", "block");
		}else{// proceso completo
			$("#EvaluacionProcedimiento" ).css("display", "block");
			$("#PreguntasProcedimiento" ).css("display", "block");
			$("#ArchivosProcedimiento" ).css("display", "block");			
			$("#CotizacionProcedimiento" ).css("display", "none");			
		}
		if(esRegularizacion()){// no trae precompromiso
			$("#presupuestoProcedimiento" ).css("display", "none");
			$("#precompromisoProcedimiento" ).css("display", "none");				
		}else{
			queryFormPost("validaPartidasAdjudicadas",{async:false});
			if ($("#nIdEstado").val()==1){
				if($("#documentoAplicado").val()==0){ // precompromiso cancelado
					$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
					
					// si aun no hay partidas en adjudicacionPartidas, no se muestran las pestañas
					if($("#partidasAdjudicadas").val()=="0"){ 
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");	
					}else{
						$("#presupuestoProcedimiento" ).css("display", "block");
						$("#precompromisoProcedimiento" ).css("display", "block");	
					}
				}else{
					//ya trae precompromiso, si no cubre el monto del consolidado mostrar las pestañas de precom y presupuesto, para que sea solvetad
					if($("#partidasAdjudicadas").val()=="0"){
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
						
					}else{
						if(cubreMonto()){
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block");
							
						}else{
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");
						
						}
					
					}						
				}
			}else{
				
				if($("#nIdEstado").val()==2){
					queryFormPost("validaFlujoProcedimiento", {async:false});
					if($("#cEventoFlujo").val()=="DISP_PRECOMMAT" || $("#cEventoFlujo").val()=="PRECOM_MAT"){
						 $("#presupuestoProcedimiento" ).css("display", "block");
						 $("#precompromisoProcedimiento" ).css("display", "block");
						
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					}
				}else{
					$("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none");
				}
			
			}
		}
		
		if($("#nIdEstado").val()==2){
			queryFormPost("existePrecompromiso",{async:false});
			if($("#existePrecompromiso").val()==1){
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
			}else{
				$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
			}
		}else{
			$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
		}
	  }
	  
	function esRegularizacion(){
		var regularizacion=false;
		var apartables = [ "PC", "PO", "PS", "PA", "PT" ];
		if ($.inArray($("#cIdTipoProcedimiento").val(), apartables) == -1){ // no traen apartado
			regularizacion=true;
		}else
			regularizacion=false;
		return regularizacion;
	}
	
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});
		if(parseInt(quitaFmt($("#lblApartado").val()),10)<parseInt(quitaFmt($("#MontoConIVA").val()),10)){			
			return true;
		}else{
			return false;
		}
	}	
	
	function quitaFmt( val ) {
	  	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}
	
	</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
			<legend> Informaci&oacute;n del Procedimiento </legend>
			<table border="0" align="left" width="100%">
				<!-- <tr >
					<td align="right" colspan="2"  >
						<img id="imgPlayStop" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="adjudicarProcedimiento();"/>Adjudicar
						<img id="imgPlayStop" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
						<img id="imgPlayStop" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="devolverProcedimiento();"/>Devolver
						<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
					</td>
				</tr> -->
				<tr align="left">
					<td colspan="2">
						[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"/>]]&nbsp;<input name="desProcedimientoCaratula" id="desProcedimientoCaratula" type="text" maxlength="150" style="border: 0px solid black; width: 40em;" readonly="readonly"></input>  
					</td>								
				</tr>
			   <tr align="left">
					<td colspan="2">
						<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input>  
					</td>								
				</tr>
				<tr align="left">
					<td colspan="2">
					Consolidado: [[<input name="cIdConsolidado" id="cIdConsolidado" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"></input>]]  
					</td>								
				</tr>
				<tr align="left">
					<td colspan="2">
					Tipo de Proceso: [[<input name="lbltipoProceso" id="lbltipoProceso" type="text" size ="18" style="border: 0px solid black;" readonly="readonly"></input>]]  
					</td>								
				</tr>
				<tr align="left">
					<td colspan="2">
						<img id="imgEstado"/><input name="nIdEstadoProcedimiento" id="nIdEstadoProcedimiento" type="text"  style="border: 0px solid black; width: 45em;"></input>  
					</td>								
				</tr>								
				<tr align="left">
						<td colspan="2">
								Monto Precomprometido: [[<input name="lblApartado" id="lblApartado" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"></input>]]
						</td>								
					</tr>
			</table>
		</fieldset>
		
		<table id="tblRequisitos" class="display" style="width:755px;" border="2">
			<thead>
				<tr>
					<th width="20px" >#</th>
					<th >Requisito</th>
					<th >Descripci&oacute;n</th>
					<th >Requerido</th>
					<th >Cumple</th>
					<th>Observaciones</th>
				 </tr>
			</thead>
		</table>
		
		<div style="text-align: center; margin-top: 20px;" >
			<button id="btnGuardarRequisitosProcedimiento">GUARDAR</button>
		</div>
		<input id="cEjercicio" name="cEjercicio" type="hidden" size="4" value="<%= cEjercicio %>">
		<input id="cIdTipoProcedimiento" name="cIdTipoProcedimiento" type="hidden" size="4" value="<%= cIdTipoProcedimiento %>">
		<input id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden" size="3" value="<%= cIdUnidadEjecutora %>">
		<input id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4" value="<%= nIdConsecutivo %>">	
		
		<!-- Checklist de Requisitos -->
    	<input type="hidden" id="cProcedimientoCumple" name="cProcedimientoCumple" >
    	<input type="hidden" id="cIdRequisito" name="cIdRequisito" >
    	<input type="hidden" id="cRequisitoRequerido" name="cRequisitoRequerido" >
    	<input type="hidden" id="cRequisitoCumple" name="cRequisitoCumple" >
    	<input type="hidden" id="cRequisitoObs" name="cRequisitoObs" >
    	<input id="Activo" name="Activo" value="false" type="hidden" size="10">
    	<input id="Categoria" name="Categoria" value="" type="hidden" size="10">
		
		<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
		<input id="usuarioLogin" name="usuarioLogin" type="hidden" size="10">
		<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" type="hidden" size="10">
		<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="10">
		<input id="tipoProceso" name="tipoProceso" type="hidden" size="10">
		

		<!-- habilitar pestanas -->
		<input id="documentoAplicado" name="documentoAplicado" type="hidden" size="10">
		<input id="nIdEstado" name="nIdEstado" type="hidden" size="10">
		<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">
		<input id="existePrecompromiso" name="existePrecompromiso" type="hidden" size="10">
		<input id="partidasAdjudicadas" name="partidasAdjudicadas" type="hidden" size="10">
		<input id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>"/>
		</form>				
	</body>
</html>