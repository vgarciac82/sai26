<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
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
			$("#tbs").val(5);
			showAndHideTabs();
		<%
				
			NegativaPestana NegPestana=new NegativaPestana();
			//NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			//botones
			
			NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
			///// botones/////
			int imgAdjudicarP=0;
			int imgDesiertoP=0;
			int imgDevolverP=0;
			Iterator it1 = rol.entrySet().iterator();
			Role role = new Role();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				roles += r.getKey().toString()+",";
			}
			if(roles.length()>0){
				roles = roles.substring(0,roles.length()-1);
			}
			Map botones=nb.getBotones(roles,"Procedimiento","PreguntasProcedimiento");
			Iterator btn = botones.entrySet().iterator();
		%>	
		
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		
		muestraInformacion();
		habilitarPestanas();
		queryInnerDivPost("llenaFechasProcedimiento",{async:false});
		leeProveedoresPreguntas();
		muestraPreguntas();
		$('#tblProvedoresPreguntas tr').live('dblclick', function() { 
			$(ProvedoresPreguntas.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});						
			$(this).addClass('row_selected');
			var aTrs = $('#tblProvedoresPreguntas').dataTable().fnGetNodes();           
			for ( var i=aTrs.length ; i>=0; i-- ){         
				if ($(aTrs[i]).hasClass('row_selected')){        
					var nTr = $('#tblProvedoresPreguntas').dataTable().fnGetData(aTrs[i]);
					$("#IdRFCPreguntas").val(nTr[5]);
					$("#razonSocialPreguntas").val(nTr[6]);
					$("#lblProveedorPreguntas").val($("#IdRFCPreguntas").val()+"-"+$("#razonSocialPreguntas").val());
				    $("#numPreguntas").val("");
					muestraPreguntas();
				}     
			}	
		});
		
	});
	
	function muestraPreguntas(){
		var consulta= "cIdProcedimiento = '"+$("#cIdProcedimiento").val()+"' AND cIdRFC='"+$("#IdRFCPreguntas").val()+"'";
		$('#tblProvedoresPreguntasRespuestas').dataTable({         
						 sScrollX: "100%",
						 sScrollXInner: "97%",
						 bScrollCollapse: true,
						 bDestroy: true,
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
						 bProcessing: true,
						 sPaginationType: "full_numbers",
						 bJQueryUI: true,
						 bAutoWidth: false,
						 bServerSide: true,
					     sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtienePreguntasProveedores&qw="+consulta,
						 aaSorting: [[ 1, "asc" ]] ,
						 aoColumns: [
						{ sName: "numPreg",bVisible:false},
						{ sName: "cIdProcedimiento",bVisible: false},
						{ sName: "cIdRFC", bVisible: false},
						{ sName: "consecutivoPreg" },
						{ sName: "pregunta"},
						{ sName: "respuesta"},
						{ sName: "boton"}
						
						]
					});
	}
	  
  	function guardaPreguntas(){
//		$("#cIdProcedimientoPreguntas").val($("#cIdProcedimientoProveedor").val());
	  /// rfc:IdRFCPreguntas
		var aTrs = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetNodes();								
	    for ( var i=aTrs.length-1 ; i>=0; i-- ){ 
		    var nTr = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetData(aTrs[i]);										
			var valor = nTr[0];
			$("#descPregunta").val($("#cdescPregunta_"+valor).val()); //pregunta
			$("#descRespuesta").val($("#cdescRespuesta_"+valor).val()); // respuesta  
			$("#numConsecPreguntas").val(valor);
			queryFormPost("procedimientoPreguntasUpdate", {async : false});
		}
	    alert("Registro insertado");	
	}
	
	function borrarTodasPreguntas(){
		 var aTrs = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetNodes();	
		 $("#numPreguntasEliminar").val(aTrs.length); 
		 for ( var i=aTrs.length-1 ; i>=0; i-- ){         									
			nTr = $('#tblProvedoresPreguntasRespuestas').dataTable().fnGetData(aTrs[i]);										
			var valor = nTr[0];
			$("#consecPreguntaDelete").val(valor);
			queryFormPost("mProcedimientoPreguntaDelete", {async : false});
		 }
		 $("#numSiguientePreguntaProveedor").val($("#consecPreguntaDelete").val());		 	
	     queryFormPost("procedimientoPreguntasUpdateConsecBorra", {async : false});
	     muestraPreguntas();
	}
	function leeProveedoresPreguntas(){
		/// tabla de preguntas a proveedores
		var consulta= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";   
		//$("#tblProvedoresPreguntas").dataTable({
			ProvedoresPreguntas=$("#tblProvedoresPreguntas").dataTable({
					    sScrollX: "100%",
						 sScrollXInner: "97%",
						 bScrollCollapse: true,
						 bDestroy: true,
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
						 bProcessing: true,
						 sPaginationType: "full_numbers",
						 bJQueryUI: true,
						 bAutoWidth: false,
						 bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneProveedoresPreguntas&qw="+consulta,
					aaSorting: [[ 1, "asc" ]],			
					aoColumns: [
						{ sName: "cEjercicio", bVisible: false },
						{ sName: "cIdTipoProcedimiento", bVisible: false},
						{ sName: "cIdUnidadEjecutora", bVisible: false},
						{ sName: "nIdConsecutivo", bVisible: false  },
						{ sName: "cIdProcedimiento", bVisible: false },
						{ sName: "cIdRFC"  },
						{ sName: "cRazonSocial" },
						{ sName: "boton" }
					]
					
			});
	}
	
	function muestraInformacion (){
	   queryFormPost("llenaCaratulaProcedimiento",{async:false});
	   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
	   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	   queryFormPost("esActivoProcedimiento",{async:false});
	   queryFormPost("mValidaPrecompromiso",{async:false});
	   
	   var cadena_campos=$("#cIdConsolidado").val().split('-');
	   $("#TipoConsolidado").val(cadena_campos[0]);
	   $("#ConsecutivoConsolidado").val(cadena_campos[2]);
		
	  /*  if(esRegularizacion()){
			document.getElementById('trBotones').style.display = 'block';
			document.getElementById('trSalir').style.display = 'none';
	   }else{
			document.getElementById('trBotones').style.display = 'none';
			document.getElementById('trSalir').style.display = 'block';
	   } */
	   
	   if($("#nIdEstado").val()==1){
		 queryFormPost("apartadoConsolidado",{async:false});
	   }else{
		 if ($("#documentoAplicado").val()==0){
		 	 queryFormPost("sumPrecomProcedimiento",{async:false});
		 }else{
		 	 queryFormPost("apartadoConsolidado",{async:false});
		 }				
	   }
				
	}

	
	function agregaPreguntas(){
		queryFormPost("mProcedimientoSigPregunta",{async:false});
		queryFormPost("mProcedimientoSigPreguntaProveedor",{async:false});
		/// insertar en la tabla d preguntas
		if($("#numSiguientePreguntaProveedor").val()==0){
			var numSigPregunta=$("#numSiguientePregunta").val();
			var numPreg=$("#numPreguntas").val();
			//$("#cIdProcedimiento").val($("#cIdProcedimientoProveedor").val());
			var cont=parseInt(numPreg,10)+parseInt(numSigPregunta,10);
			for (var x=parseInt(numSigPregunta,10)+1; x<parseInt(cont,10)+1;x++){
				/// rfc:IdRFCPreguntas
				$("#numConsecPreguntas").val(x);
				queryFormPost("procedimientoPreguntasInsert",{async:false});
			}
		}else{
			var numSigPregunta=$("#numSiguientePreguntaProveedor").val();
			var numPreg=$("#numPreguntas").val();
		//	$("#cIdProcedimientoPreguntas").val($("#cIdProcedimientoProveedor").val());
			var cont=parseInt(numPreg,10)+parseInt(numSigPregunta,10);
			queryFormPost("procedimientoPreguntasUpdateConsec",{async:false});
			for (var x=parseInt(numSigPregunta,10)+1; x<parseInt(cont,10)+1;x++){
	           $("#numConsecPreguntas").val(x);
			   queryFormPost("procedimientoPreguntasInsert",{async:false});
			}
		}
		muestraPreguntas();
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
	
	function habilitarPestanas(){
		$("#EvaluacionProcedimiento" ).css("display", "block");
		$("#PreguntasProcedimiento" ).css("display", "block");
		$("#ArchivosProcedimiento" ).css("display", "block");
		$("#CotizacionProcedimiento" ).css("display", "none");
		queryFormPost("mValidaPrecompromiso",{async:false});	
		queryFormPost("validaPartidasAdjudicadas",{async:false});
		if ($("#nIdEstado").val()==1){
			if($("#documentoAplicado").val()==0){ // no trae precompromiso de consolidado
				if($("#partidasAdjudicadas").val()=="0"){ 
					$("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none");	
				}else{
					$("#presupuestoProcedimiento" ).css("display", "block");
					$("#precompromisoProcedimiento" ).css("display", "block");	
				}
			}else{ // existe precompromiso en consolidado
				//ya trae precompromiso, si no cubre el monto del consolidado mostrar las pestañas de precom y presupuesto, para que sea solvetad
				if($("#partidasAdjudicadas").val()=="0"){
					$("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none");
					//document.getElementById('trBotones').style.display = 'block';
					//document.getElementById('trSalir').style.display = 'none';	
				}else{
					if(cubreMonto()){
						$("#presupuestoProcedimiento" ).css("display", "block");
						$("#precompromisoProcedimiento" ).css("display", "block");
					//	document.getElementById('trBotones').style.display = 'block';
					//	document.getElementById('trSalir').style.display = 'none';	
					}else{
						$("#presupuestoProcedimiento" ).css("display", "none");
						$("#precompromisoProcedimiento" ).css("display", "none");
					//	document.getElementById('trBotones').style.display = 'block';
					//	document.getElementById('trSalir').style.display = 'none';	
					}
				
				}
			}
		}else{
			if($("#nIdEstado").val()==2){
				queryFormPost("validaFlujoProcedimiento", {async:false});
				if($("#cEventoFlujo").val()=="DISP_PRECOMMAT" || $("#cEventoFlujo").val()=="PRECOM_MAT"){
					 $("#presupuestoProcedimiento" ).css("display", "block");
					 $("#precompromisoProcedimiento" ).css("display", "block");
					//$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
					
			//		document.getElementById('trBotones').style.display = 'none';
				//	document.getElementById('trSalir').style.display = 'block';
				}else{
				//	document.getElementById('trBotones').style.display = 'block';
				//	document.getElementById('trSalir').style.display = 'none';
					$("#presupuestoProcedimiento" ).css("display", "none");
					$("#precompromisoProcedimiento" ).css("display", "none");
				}
			}else{
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");
			}
		
		}
		if($("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PS" ){
			$("#RequisitosProcedimiento" ).css("display", "block");
		}else{
			$("#RequisitosProcedimiento" ).css("display", "none");
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
	
	function generaRptAclaracion(){
		var cIdProc =$("#cIdProcedimiento").val();
		var cIdCons =$("#cIdConsolidado").val();
		var servletPath = "../../servlet/SeguridadCatalogosMateriales?" + "rn=rptJuntaAclaracion.jasper" +"&formato=dsdoc" + "&cIdProcedimiento=" + cIdProc + "&cIdConsolidado=" + cIdCons;
		window.open(servletPath, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
	}
	
	function eliminaPregunta(consecPregunta){
		if(validaModificarProcedimiento()){
			$("#numPreguntas").val("");
		//	$("#cIdProcedimiento").val($("#cIdProcedimientoProveedor").val());
			$("#consecPreguntaDelete").val(consecPregunta);
		    // elimina pregunta seleccionada
			queryFormPost("mProcedimientoPreguntaDelete",{async:false});
		    // actualizar el consecutivo de las preguntas
		    queryFormPost("mProcedimientoPreguntaConsecutivoUpdate",{async:false});
			//agregaPreguntas();
			muestraPreguntas();
		}
	}
	
	function  eliminaProveedorRFC(indice){ //preguntas
	 	if(validaModificarProcedimiento()){
	 		if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO")
		   	 return;
			$("#cidRFCOculto1").val(indice);
			$("#IdRFCPreguntas").val(indice);
			queryFormPost("deleteProveedorProcedimiento", {async:false}); // borra de mprocedimientoAdjudicacion
		    //se refresca tabla de proveedores
		    $("#btnBuscarProveedorProcedimiento").click();
		    muestraProveedoresEvaluacion();
		    leeProveedoresPreguntas();
			// elimina preguntas
			queryFormPost("numeroPreguntasProveedor", {async : false});
			queryFormPost("mProcedimientoSigPreguntaProveedor", {async : false});			
			queryFormPost("mProcedimientoProveedorPreguntaDelete", {async : false});		
			
			queryFormPost("procedimientoPreguntasUpdateConsecBorra", {async : false});
			queryFormPost("mProcedimientoProveedorCotizacionesDelete", {async : false}); // borra de procedimiento completo
			queryFormPost("mProcedimientoDocumentosProveedorDelete", {async : false});
			queryFormPost("mProcedimientoPreguntasProveedorDelete", {async : false});
			muestraProveedoresEvaluacion();
		    leeProveedoresPreguntas();
		//	tablaProveedorPartida();
			muestraPreguntas();
			//agregaPreguntas();
	    }
   }
   
   function muestraProveedoresEvaluacion(){
		/// tabla de preguntas a proveedores
		var consulta= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"' AND cEstadoProveedor=1";   
			ProveedoresEvaluacion=$("#tblProveedoresEvaluacion").dataTable({
				     sScrollX: "100%",
					 sScrollXInner: "97%",
					 bScrollCollapse: true,
					 bDestroy: true,
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
					 bProcessing: true,
					 sPaginationType: "full_numbers",
					 bJQueryUI: true,
					 bAutoWidth: false,
					 bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_obtieneProveedoresPreguntas&qw="+consulta,
				aaSorting: [[ 1, "asc" ]],			
				aoColumns: [
					{ sName: "cEjercicio", bVisible: false },
					{ sName: "cIdTipoProcedimiento", bVisible: false  },
					{ sName: "cIdUnidadEjecutora", bVisible: false},
					{ sName: "nIdConsecutivo", bVisible: false  },
					{ sName: "cIdProcedimiento", bVisible: false },
					{ sName: "cIdRFC"  },
					{ sName: "cRazonSocial" },
					{ sName: "boton",bVisible: false   }
				]
					
			});
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
		if((parseFloat(quitaFmt($("#lblApartado").val()))) < (parseFloat(quitaFmt($("#MontoConIVA").val())))){	 		
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
<body id="dt_example">
	<form>
		<fieldset>
			<legend>Informaci&oacute;n del Procedimiento</legend>
			<table align="left" width="100%">
				 <!-- <tr id="trBotones" style='display:none'>						
					<td align="right" colspan="2"  >
						<img id="adjudicaProcedimiento" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="adjudicarProcedimiento();"/>Adjudicar
						<img id="desiertoProcedimiento" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
						<img id="devolverProcedimiento" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="devolverProcedimiento();"/>Devolver
						<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
					</td>
				</tr>
				<tr id="trSalir" style='display:none'>
					<td align="right" colspan="2"  >
						<img id="desiertoProcedimiento" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
						<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
					</td>
				</tr> -->
				<tr align="left">
					<td colspan="2">[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size="10"style="border: 0px solid black;" readonly="readonly" />]]&nbsp;<input
						name="desProcedimientoCaratula" id="desProcedimientoCaratula"
						type="text" maxlength="150"
						style="border: 0px solid black; width: 40em;" readonly="readonly"></input>
					</td>
				</tr>
				<tr align="left">
						<td colspan="2"><input name="lblcIdUnidadEjecutora"	id="lblcIdUnidadEjecutora" type="text" readonly="readonly" style="border: 0px solid black; width: 30em;"></input></td>
				</tr>
				<tr align="left"> <td colspan="2">Consolidado: [[<input name="cIdConsolidado" id="cIdConsolidado" type="text" size="10"	style="border: 0px solid black;" readonly="readonly"></input>]]</td>
				</tr>
				<tr align="left"> <td colspan="2">Tipo de Proceso: [[<input	name="lbltipoProceso" id="lbltipoProceso" type="text" size="18" style="border: 0px solid black;" readonly="readonly"></input>]]</td>
				</tr>
				<tr align="left">
					<td colspan="2"><img id="imgEstado" /><input name="nIdEstadoProcedimiento" id="nIdEstadoProcedimiento" type="text" style="border: 0px solid black; width: 45em;"></input>
					</td>
				</tr>
				<tr align="left">
					<td colspan="2">
							Monto Precomprometido: [[<input name="lblApartado" id="lblApartado" type="text" size ="10" style="border: 0px solid black;" readonly="readonly"></input>]]
					</td>								
				</tr>	
			</table>
		</fieldset>
		<br />
		<fieldset>
			<legend>Proveedores</legend>
			<table id="tblProvedoresPreguntas" class="display" width="1000px"
				border="2">
				<thead>
					<tr>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th width="400px">RFC</th>
						<th width="400px">Raz&oacute;n Social</th>
						<th>Boton</th>
					</tr>
				</thead>
			</table>
		</fieldset>
		<br /> <br />
		<table>
			<tr>
				<td>No de Preguntas &nbsp;<input type="text"
					name="numPreguntas" id="numPreguntas" />&nbsp;&nbsp;
					<input type="button" name="btnAgregaPreguntas" id="btnAgregaPreguntas" value="Agregar" onclick="agregaPreguntas();"/></td>
			</tr>
		</table>
		<fieldset>
			<legend>Preguntas</legend>
			<input name="lblProveedorPreguntas" id="lblProveedorPreguntas"
				type="text" size="80" style="border: 0px solid black;"
				readonly="readonly"></input>
			<table id="tblProvedoresPreguntasRespuestas" class="display"
				width="1000px" border="2">
				<thead>
					<tr>
						<th></th>
						<th></th>
						<th></th>
						<th width="30px">No. de pregunta</th>
						<th width="400px">Pregunta</th>
						<th width="400px">Respuesta</th>
						<th width="400px">Elimina</th>
					</tr>
				</thead>
			</table>
			<table>
				<tr>
					<td><input type="button" name="btnBorrarTodasPreguntas"	id="btnBorrarTodasPreguntas" onclick="borrarTodasPreguntas();" value="Preguntas"/>
					</td>
					<td><input type="button" name="btnGuardaPreguntas" id="btnGuardaPreguntas" onclick="guardaPreguntas();" value="Guardar Preguntas"/>
					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td><input type="button" name="btngeneraRptAclaracion" id="btngeneraRptAclaracion" onclick="generaRptAclaracion();" value="Reporte"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<br /> <input id="cEjercicio" name="cEjercicio" type="hidden" size="4"
			value="<%= cEjercicio %>"> <input id="cIdTipoProcedimiento"
			name="cIdTipoProcedimiento" type="hidden" size="4"
			value="<%= cIdTipoProcedimiento %>"> <input
			id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden"
			size="3" value="<%= cIdUnidadEjecutora %>"> <input
			id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4"
			value="<%= nIdConsecutivo %>">
			
			 <!-- Preguntas -->
	    	<input id="razonSocialPreguntas" name="razonSocialPreguntas" value=""  type="hidden" size="10">
	    	<input id="IdRFCPreguntas" name="IdRFCPreguntas" value=""  type="hidden" size="10">
	    	<input id="numConsecPreguntas" name="numConsecPreguntas" value=""  type="hidden" size="10">
	    	<input id="descRespuesta" name="descRespuesta" value=""  type="hidden" size="10">
	    	<input id="descPregunta" name="descPregunta" value=""  type="hidden" size="10">
	    	<input id="numSiguientePregunta" name="numSiguientePregunta" value=""  type="hidden" size="10">
	    	<input id="consecPreguntaDelete" name="consecPreguntaDelete" value=""  type="hidden" size="10">
	    	<input id="numSiguientePreguntaProveedor" name="numSiguientePreguntaProveedor" value=""  type="hidden" size="10">
	    	<input id="numPreguntasEliminar" name="numPreguntasEliminar" value=""  type="hidden" size="10">
	    	<input id="cidRFCOculto" name="cidRFCOculto" value=""  type="hidden" size="10">
			
			<input id="usuarioLogin" name="usuarioLogin" value="" type="hidden" size="10"> 
			<input
			id="ivaProcedimiento" name="ivaProcedimiento" value="" type="hidden"
			size="10"> <input id="nIdTipoCambioProveedor"
			name="nIdTipoCambioProveedor" value="" type="hidden" size="10">
		<input id="mTipoCambioProveedor" name="mTipoCambioProveedor" value="" type="hidden" size="10">
		<input id="nPorcentajeIVAProveedor" name="nPorcentajeIVAProveedor" value="" type="hidden" size="10">
		<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" value="" type="hidden" size="10"> 
		<input id="cIdEntidadContable" name="cIdEntidadContable" value="" type="hidden" size="10"> 
		<input id="cIdUsuarioCreacion"
			name="cIdUsuarioCreacion" value="" type="hidden" size="10"> <input
			id="IdRFCProveedor" name="IdRFCProveedor" value="" type="hidden"
			size="10"> <input id="IContratoAbiertoProveedor"
			name="IContratoAbiertoProveedor" value="" type="hidden" size="10">
		<input id="cIdConsolidado" name="cIdConsolidado" value=""
			type="hidden" size="10"> <input id="TipoConsolidado"
			name="TipoConsolidado" value="" type="hidden" size="10"> <input
			id="ConsecutivoConsolidado" name="ConsecutivoConsolidado" value=""
			type="hidden" size="10"> <input id="cCategoriaDescripcion"
			name="cCategoriaDescripcion" value="" type="hidden" size="10">
		<input id="tieneProveedor" name="tieneProveedor" type="hidden" size="10">
		<input id="cPartidaDesierta" name="cPartidaDesierta" type="hidden" size="10"> 
		<input id="cProcedimientoCumple" name="cProcedimientoCumple" type="hidden"
			size="10"> <input id="tipoProceso" name="tipoProceso"
			type="hidden" size="10"> <input id="cboCategoria"
			name="cboCategoria" type="hidden" size="10"> <input
			id="descripcionCaratula" name="descripcionCaratula" type="hidden"
			size="10"> <input id="EstadoCaptura" name="EstadoCaptura"
			type="hidden" size="10"> 
		<input id="numero_externoCaratula" name="numero_externoCaratula" type="hidden" size="10"> 
		<input id="Activo" name="Activo" type="hidden" size="10">
		<!-- habilita pestanas -->
		<input id="existePrecompromiso" name="existePrecompromiso" type="hidden" size="10">
		<input id="nIdEstado" name="nIdEstado" type="hidden" size="10">
		<input id="documentoAplicado" name="documentoAplicado" type="hidden" size="10"> 				
		<input id="partidasAdjudicadas" name="partidasAdjudicadas" type="hidden" size="10"> 
		<input id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
		<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">

		<!-- bitacora -->
		<input id="cIdDocumento" name="cIdDocumento" type="hidden" size="10">
		<input id="cAccion" name="cAccion" type="hidden" size="10">
		
	</form>
</body>
</html>