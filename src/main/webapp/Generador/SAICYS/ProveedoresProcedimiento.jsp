<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
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
		<title>Procedimiento</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Consolidado">
		
		<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$("#tbs").val(3);
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
			Map botones=nb.getBotones(roles,"Procedimiento","CotizacionProcedimiento");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
				String img=(String) b.getValue();
				if ("imgAdjudicarProveedoresProc".equals(img)){     
					imgAdjudicarP=1; 
				}
				if ("imgDesiertoProveedoresProc".equals(img)){
					imgDesiertoP=1;
				}
				if ("imgDevolverProveedoresProc".equals(img)){
					imgDevolverP=1;
				}
				
			}
		%>	
		
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuario").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		
		muestraInformacion();
		habilitarPestanas();
		mostrarTablaProveedores();
		queryInnerDivPost("llenaFechasProcedimiento",{async:false});
		$( "#btnBuscarProveedorProcedimiento" ).button().click(function() {
			mostrarTablaProveedores();
		});
		
		/////EVENTO CLICK EN EL RENGLON DE LA TABLA DE PROVEEDORES DISPONIBLES
		$('#tblProveedoresDisponibles tr').live('dblclick', function() {
		if(parseInt($("#nIdEstado").val(),10)>1){
			swal("El estado del procedimiento no permite agregar mas proveedores.",{icon:"info",button: "Cerrar"});
		}else{
 
			var cadena_campos=$("#cIdConsolidado").val().split('-');
			$("#TipoConsolidado").val(cadena_campos[0]);
			$("#ConsecutivoConsolidado").val(cadena_campos[2]);
			
			
			if(validaModificarProcedimiento()){
		    	if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else  
				{         
					$(this).addClass('row_selected');
					var aTrs = $('#tblProveedoresDisponibles').dataTable().fnGetNodes();
					for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							var nTr = $('#tblProveedoresDisponibles').dataTable().fnGetData(aTrs[i]);   
							//Obtengo el campo de la tabla
							$("#IdRFCProveedor").val(nTr[0]); 

							if( !esProveedorAdquisiciones( nTr[0] ) ){
		
								swal("El proveedor seleccionado no es un proveedor de adquisiciones. Debe actualizarlo para que pueda ser adjudicado."
								,{icon:"info",button: "Cerrar"});

								return false;
							}

							//Asigno Valor a los campos hidden que necesito 
							$("#nIdTipoCambioProveedor").val('01');
							$("#mTipoCambioProveedor").val('1');
							//$("#IContratoAbiertoProveedor").val('FALSE');
							$("#nPorcentajeIVAProveedor").val($("#ivaProcedimiento").val());
							//Mando llamar el procedimiento que me inserta los campos en la tabla mProcedimientoAdjudicacion
							queryFormPost("sp_AdjudicacionCreate",{async : false});
							oTableMP.dataTable().fnDeleteRow( i );
							guardaBitacora("AGREGA_PROVEEDOR",$("#cIdProcedimiento").val());
							swal("Registro insertado.",{icon:"info",button: "Cerrar"});
						//	agregaTablaProveedoresCotizaciones();
							$("#btnBuscarProveedorProcedimiento").click();
							
						}
					}
				}
			}	
		}
			
	    });
		
	});
	
	function muestraInformacion (){
		   queryFormPost("llenaCaratulaProcedimiento",{async:false});
		   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
		   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		   queryFormPost("esActivoProcedimiento",{async:false});
		 //  queryFormPost("apartadoConsolidado",{async:false});
		   queryFormPost("mValidaPrecompromiso",{async:false});
		   if($("#nIdEstado").val()==1){
				 queryFormPost("apartadoConsolidado",{async:false});
			}else{
				  if ($("#documentoAplicado").val()==0){
				 	 queryFormPost("sumPrecomProcedimiento",{async:false});
				 }else{
				 	 queryFormPost("apartadoConsolidado",{async:false});
				 }
			}
		  
		   var cadena_campos=$("#cIdConsolidado").val().split('-');
			$("#TipoConsolidado").val(cadena_campos[0]);
			$("#ConsecutivoConsolidado").val(cadena_campos[2]);
			if(esRegularizacion()){
				document.getElementById('trBotones').style.display = 'none';
				document.getElementById('trSalir').style.display = 'none';
			}else{
				document.getElementById('trBotones').style.display = 'none';
				document.getElementById('trSalir').style.display = 'block';
			}
			queryFormPost("mSolicitudDescripcionUnidad", { async:false });	
	}

	var oTableMP;		
	function mostrarTablaProveedores(){
		    $("#tblProveedoresDisponibles").css("display", "");  			
			///////////////////////CLICK EN EL TR DE tblSolicitudDispPreSel////////////////////////////////////////
			//row_selected  ----Color gris al pasar el click	
			//gradeA   -----Color verdeson
			var etiqueta="PROVEEDORES DISPONIBLES";
			var consulta="qw= 1=1 ";
			if(parseInt($("#cboCategoria").val(),10)==11){
				consulta+=" and nIdPyme=6 ";
				var etiqueta="ORGANISMOS PÚBLICOS";
			}else{
				consulta+=" and nIdPyme!=6 ";
			}

			if (($.trim($("#rfcProveedor").val()))!="")
			{
				consulta += " AND cIdRFC LIKE '%25" + $.trim($("#rfcProveedor").val())+"%25'";
				consulta=consulta.replace("\&","%26");
 				
			}
			if(($.trim($("#rSocialProveedor").val()))!="" )
			{
				consulta += " AND cRazonSocial LIKE '%25" + $.trim($("#rSocialProveedor").val())+"%25'";
				consulta=consulta.replace("\&","%26");
			}
			var queryNot= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
			if($("#cIdTipoProcedimiento").val()!='PT'){
				consulta+= " AND  CIDRFC NOT IN (select CIDRFC from v_obtieneProveedoresCotizacion where "+queryNot+")";	
			}
			
		
		   	//Mostramos la tabla de procedimientos
			oTableMP=$('#tblProveedoresDisponibles').dataTable({
			"bProcessing": true,
			"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
			"bFilter" : false,
			"bDestroy" : true,
			"bJQueryUI": true,
			"bAutoWidth" : false,
			"iDisplayLength": 5,
			"sPaginationType": "full_numbers",
			//"sScrollY": 250,
					"oLanguage": {
					sProcessing: "Procesando...",
					//sLengthMenu: "Mostrar _MENU_ registros",
					sLengthMenu: "<h2><b>"+etiqueta+"</b></h2><h5>Click para agregar el proveedor</h5>",
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
				"bServerSide": true,
				"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCatalogoProveedores&"+consulta,
				"fnServerData": function ( sSource, aoData, fnCallback ) {
                     $.ajax( {
                         "dataType": 'json', 
                         "type": "POST", 
                         "url": sSource, 
                         "data": aoData, 
                         "success": fnCallback
                     } );
                 },
				"aaSorting": [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial"   },
					{ sName: "alta_rapida"   }
				]
			});
		}
		
		function validaBotones(boton){
		var imgAdjudicarP='<%=imgAdjudicarP%>';
		var imgDesiertoP='<%=imgDesiertoP%>';
		var imgDevolverP='<%=imgDevolverP%>';
			switch (boton){
			case "imgAdjudicarProveedoresProc":
				if (imgAdjudicarP==0){
					adjudicarProcedimiento();
				}else{
					swal("No tiene permiso para esta acción.",{icon:"info",button: "Cerrar"});
				}
			break;
			case "imgDesiertoProveedoresProc":
				if (imgDesiertoP==0){
					desiertoProcedimiento();
				}else{
					swal("No tiene permiso para esta acción.",{icon:"info",button: "Cerrar"});
				}
			break;
			case "imgDevolverProveedoresProc": 
				if (imgDevolverP==0){
					devolverProcedimiento();
				}else{
					swal("No tiene permiso para esta acción.",{icon:"info",button: "Cerrar"});
				}
			break;			
			}/// FIN SWITCH
		} 
		
		function adjudicarProcedimiento(){
		if(validaModificarProcedimiento()){
			if(parseInt($("#nIdEstado").val(),10)>1)
				return;	
		queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
		if($("#cProcedimientoCumple").val() == false){
			swal("El procedimiento no cumple los requisitos.",{icon:"info",button: "Cerrar"});
			return;
		}

		queryFormPost("checaProveedoresAsignados",{async:false});
		if($("#tieneProveedor").val()=="0"){
			swal("El procedimiento no tiene ningun proveedor asociado",{icon:"info",button: "Cerrar"});
			return;
		}
		//valida el procedimiento de invitacion a tres
		queryFormPost("fn_mVerificaAplicaPartidaDesiertaRead",{async:false});
		if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("tres personas") >= 0 && ($("#tipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#tipoProceso").val(),10) == 0)){			
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
	
			if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("adjudicaci") >= 0 && $("#cCategoriaDescripcion").val().toLowerCase().indexOf("n directa") >= 0 && ($("#tipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#tipoProceso").val(),10) == 0)){
				if($("#tieneProveedor").val() < 3){
					if(confirm("El procedimiento solo tiene "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?")){
					    $("#EstadoCaptura").val(3);
						queryFormPost("sp_ProcedimientoDesierto",{async:false});
						actualizaDatosProcedimientoDesierto();
						return;
					}
				}
			}
			$.ajax({url: '../../servlet/ProcedimientoServlet?cEjercicio='+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
			 type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
				function(j){
					mensajeAp=j[0].Contable1;
					swal(mensajeAp,{icon:"info",button: "Cerrar"});
					window.location = "Procedimiento-copia.jsp?tab=3";
					$("#cAccion").val("ADJUDICA_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});	
				}
			});
		}
  	}  
  	
   function desiertoProcedimiento(){
		if(validaModificarProcedimiento()){
			if(parseInt($("#nIdEstado").val(),10)>1 )
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
			if(parseInt($("#nIdEstado").val(),10)!=2){				
				swal("No se puede devolver el procedimiento, porque su estatus no lo permite.",{icon:"info",button: "Cerrar"});
				return;
			}
		   $.ajax({url: '../../servlet/ProcedimientoServlet?', type:'post' , async: false, 
           data:"operacion=3&cEjercicio="+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
            dataType: 'json', success: 
            function(j){
            		mensajeAp=j[0].Contable1;
					swal(mensajeAp,{icon:"info",button: "Cerrar"});
					window.location = "Procedimiento-copia.jsp?tab=3";	
					$("#cAccion").val("DEVOLVER_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			}
	   		});
		}
	}
	
	
	function validaModificarProcedimiento(){
		queryFormPost("mUsuarioMismaUE", {async: false   });
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0) ||
			$('#usuarioRoleProcedimiento').val().toString().indexOf('ANALISTA') >= 0 || $('#usuarioRoleProcedimiento').val().toString().indexOf('JEFE') >= 0
			|| parseInt($("#usuariosMismaUE").val(),10)==1){
			return true;
		}
		else{
			swal("El usuario no tiene permiso para realizar esta acci\xF3n.",{icon:"info",button: "Cerrar"});
			return false;
		}
	}
	
	function actualizaDatosProcedimientoDesierto(){
		$("#nIdEstadoProcedimiento").val("DESIERTO");

		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	    //bitacora
		$("#cAccion").val("DESIERTO_PROCEDIMIENTO");
		$("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
    }
  
  	function actualizaDatosProcedimientoAdjudicado(){
		$("#nIdEstadoProcedimiento").val("ADJUDICADO");
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		var imagen=$("#imagenEstadoProcedimiento").val();
	    //bitacora
		$("#cAccion").val("ADJUDICA_PROCEDIMIENTO");
		$("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
   }
   function actualizaDatosProcedimientoDevuelto(){
		$("#nIdEstadoProcedimiento").val("CAPTURADO");
	    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	    //bitacora
		$("#cAccion").val("DEVUELVE_PROCEDIMIENTO");
		$("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
   
   }

	function habilitarPestanas(){
		if($("#tipoProceso").val()==1){ //proceso corto
			$("#EvaluacionProcedimiento" ).css("display", "none");
			$("#PreguntasProcedimiento" ).css("display", "none");
			$("#ArchivosProcedimiento" ).css("display", "none");
			$("#CotizacionProcedimiento" ).css("display", "block");
			if(esRegularizacion()){// no trae precompromiso
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");	
			}else{ // trae precompromiso
				queryFormPost("validaPartidasAdjudicadas",{async:false});
				if ($("#nIdEstado").val()==1){ 
					if($("#documentoAplicado").val()==0){ // precompromiso cancelado
						$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
							// si aun no hay partidas en adjudicacionPartidas, no se muestran las pestañas
						
						if($("#partidasAdjudicadas").val()=="0"){ 
							$("#presupuestoProcedimiento" ).css("display", "none");
							$("#precompromisoProcedimiento" ).css("display", "none");	
						}else{
							$("#presupuestoProcedimiento").css("display", "block");
							$("#precompromisoProcedimiento").css("display", "block");	
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
							/* if ($("#usuarioRoleProcedimiento").val().toString().indexOf("ADMIN_RECMAT")>=0){
								if($("#cIdUsuarioCreacion").val()==$("#usuarioLogin").val()){
									$("#presupuestoProcedimiento").css("display", "block");
									$("#precompromisoProcedimiento").css("display", "block");
								}else{
									$("#presupuestoProcedimiento").css("display", "none");
									$("#precompromisoProcedimiento").css("display", "none");
								}
							}else{
								$("#presupuestoProcedimiento").css("display", "block");
								$("#precompromisoProcedimiento").css("display", "block");
							} */	
							$("#presupuestoProcedimiento" ).css("display", "block");
							$("#precompromisoProcedimiento" ).css("display", "block"); 
							$("#AmpliacionVigenciaProcedimiento" ).css("display", "block");
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
		}else{// proceso completo
			$("#EvaluacionProcedimiento" ).css("display", "block");
			$("#PreguntasProcedimiento" ).css("display", "block");
			$("#ArchivosProcedimiento" ).css("display", "block");
			
			$("#CotizacionProcedimiento" ).css("display", "none");
			
			if(esRegularizacion()){// no trae precompromiso
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");	
			}else{
				if ($("#nIdEstado").val()==1){
					if($("#documentoAplicado").val()==0){ // precompromiso cancelado 
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
		if($("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PS" ){
			$("#RequisitosProcedimiento" ).css("display", "block");
		}else{
			$("#RequisitosProcedimiento" ).css("display", "none");
		}
		//valida que exista un precompromiso en procedimiento,muestra la pestaña de ampliacion de vigencias
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
		//if(parseInt(quitaFmt($("#lblApartado").val()))<quitaFmt($("#MontoConIVA").val())){
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
	function guardaBitacora(accion,documento){
		//Bitácora
		$("#cAccion").val(accion);
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}

	function esProveedorAdquisiciones( rfc ){
		
		$("#rfcValidar").val(rfc);
		var esProveedorAdquisiciones = false;
		var queryName = "esProveedorAdquisiciones";
		queryFormPost({
			queryName:queryName,
			async:false,
			callback:function(){
				esProveedorAdquisiciones = "true" === $("#proveedorAdquisiciones").val();
			}
		})
		
		return esProveedorAdquisiciones;
	}
	</script>
	</head>
	<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Informaci&oacute;n del Procedimiento</legend>
					<table align="left" width="100%">
				         <tr id="trBotones" style='display:none'>						
							<td align="right" colspan="2"  >
							<img id="imgAdjudicarProveedoresProc" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="adjudicarProcedimiento();"/>Adjudicar
							<img id="imgDesiertoProveedoresProc" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="desiertoProcedimiento();" />Desierto
							<img id="imgDevolverProveedoresProc" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="devolverProcedimiento();"/>Devolver
							<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
							</td>
						</tr>
						<tr id="trSalir" style='display:none' align="right">
							<td align="right" colspan="2"  >
								<input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 	id="imgPlayStop" 	name="imgPlayStop" 	value="Desierto"	onclick="desiertoProcedimiento();"/>&nbsp;&nbsp;
                            	<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	 onclick="salir();" />&nbsp;&nbsp;
							</td>
						</tr>
						<tr>
					    	<td align="left" colspan="2">
					    		<input type="text" style="width: 25px;border-width:0; background-color:transparent;" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly  value="<%=unidadUsuarioLogeado%>"/>
					    		<input type="text" style="width: 690px;border-width:0; background-color:transparent;" id="lblDescUsuario" name="lblDescUsuario" readonly />
					    	</td>
				    	</tr>
						<tr align="left">
							<td colspan="2">
								[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size ="10" style="border-width:0; background-color:transparent" readonly="readonly"/>]]&nbsp;
								<input name="desProcedimiento" id="desProcedimiento" type="text" maxlength="150" style="border-width:0; background-color:transparent; width: 40em;" readonly="readonly"></input>  
							</td>								
						</tr>
					   <tr align="left">
							<td colspan="2">
								<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" readonly="readonly" style="border-width:0; background-color:transparent; width: 30em;"></input>  
							</td>								
						</tr>
						<tr align="left">
							<td colspan="2">
								<input name="nIdEstadoProcedimiento" id="nIdEstadoProcedimiento" type="text"  style="border-width:0; background-color:transparent; width: 45em;"></input>  
							</td>								
						</tr>
						<tr align="left">
							<td colspan="2">
							Consolidado: <input name="cIdConsolidado" id="cIdConsolidado" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"></input> 
							</td>								
						</tr>
						<tr align="left">
							<td colspan="2">
							Tipo de Proceso: <input name="lbltipoProceso" id="lbltipoProceso" type="text" size ="18" style="border-width:0; background-color:transparent;" readonly="readonly"></input>  
							</td>								
						</tr>
						<tr align="left">
							<td colspan="2">
									Monto Precomprometido: <input name="lblApartado" id="lblApartado" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"></input>
							</td>								
						</tr>
						<tr align="left">
							<td>
				 				<div id="tablaFechasProcedimiento" style="width:100%;">
									<!-- Carga las fechas -->
								</div>
							</td>
						</tr>
					</table>
				</fieldset>
					<br/>
				
				<table>
					<tr>
						<td colspan="2"><h4>Solo puede seleccionar Proveedores con documentacion completa autorizada. Los proveedores con alta rapida se excluyen</h4></td>
					</tr>
					<tr><td align="right">RFC:</td>
						<td><input type="text" id="rfcProveedor" name="rfcProveedor"style="width: 30em;" /></td>
					</tr>
					<tr><td align="right">Raz&oacute;n Social:</td>
						<td><input type="text" id="rSocialProveedor" name="rSocialProveedor"style="width: 30em;" /></td>
					</tr>
					<tr><td width="33%"></td>
						<td width="33%" align="center">
							<input type="button" name="btnBuscarProveedorProcedimiento" id="btnBuscarProveedorProcedimiento" value="BUSCAR" class="btnInterfaceBG ui-button ui-corner-all"/>
						</td>
						<td width="33%" align="right">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3" >
							<table id="tblProveedoresDisponibles" class="display">
						       <thead>
						          <tr>
						             <th>RFC</th>
						             <th>Raz&oacute;n Social</th>
									 <th>Alta Rápida</th>
						          </tr>
						        </thead>
						     </table>
						</td>						
					</tr>
				</table>
				
				
				<input id="proveedorAdquisiciones" name="proveedorAdquisiciones" value=""  type="hidden">
				<input id="rfcValidar" name="rfcValidar" value=""  type="hidden">
				<input id="usuarioLogin" name="usuarioLogin" value=""  type="hidden" size="10">
				<input id="ivaProcedimiento" name="ivaProcedimiento" value=""  type="hidden" size="10">
				<input id="nIdTipoCambioProveedor" name="nIdTipoCambioProveedor" value=""  type="hidden" size="10">
				<input id="mTipoCambioProveedor" name="mTipoCambioProveedor" value=""  type="hidden" size="10">
				<input id="nPorcentajeIVAProveedor" name="nPorcentajeIVAProveedor" value=""  type="hidden" size="10">		
				<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" value=""  type="hidden" size="10">
				<input id="cIdEntidadContable" name="cIdEntidadContable" value=""  type="hidden" size="10">	
				<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" value=""  type="hidden" size="10">		
				<input id="IdRFCProveedor" name="IdRFCProveedor" value=""  type="hidden" size="10">
				<input id="IContratoAbiertoProveedor" name="IContratoAbiertoProveedor" value=""  type="hidden" size="10">
				<input id="cIdConsolidado" name="cIdConsolidado" value=""  type="hidden" size="10">				
				<input id="TipoConsolidado" name="TipoConsolidado" value=""  type="hidden" size="10">
				<input id="ConsecutivoConsolidado" name="ConsecutivoConsolidado" value=""  type="hidden" size="10">
				<input id="cCategoriaDescripcion" name="cCategoriaDescripcion" value=""  type="hidden" size="10">
				<input id="tieneProveedor" name="tieneProveedor" type="hidden" size="10">
				<input id="cPartidaDesierta" name="cPartidaDesierta" type="hidden" size="10">
				<input id="cProcedimientoCumple" name="cProcedimientoCumple" type="hidden" size="10">
				<input id="tipoProceso" name="tipoProceso" type="hidden" size="10">
				<input id="cboCategoria" name="cboCategoria" type="hidden" size="10">
				<input id="descripcionCaratula" name="descripcionCaratula" type="hidden" size="10">
				<input id="EstadoCaptura" name="EstadoCaptura" type="hidden" size="10">
				<input id="numero_externoCaratula" name="numero_externoCaratula" type="hidden" size="10">
				<input id="Activo" name="Activo" type="hidden" size="10">
				
				<!-- habilitar pestanas -->
				<input id="documentoAplicado" name="documentoAplicado" type="hidden" size="10">
				<input id="nIdEstado" name="nIdEstado" type="hidden" size="10">
				<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">
 				<input id="existePrecompromiso" name="existePrecompromiso" type="hidden" size="10">
 				<input id="partidasAdjudicadas" name="partidasAdjudicadas" type="hidden" size="10">
 				<input id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
 				<!-- bitacora -->
				<input id="cAccion" name="cAccion" type="hidden" size="10">
				<input id="cIdDocumento" name="cIdDocumento" type="hidden" size="10">
				<input id="cIdUsuario" name="cIdUsuario" type="hidden" size="10">
				<input type="hidden" name="isPlurianual" id="isPlurianual" />
				<input type="hidden" name="montoTotalPluri" id="montoTotalPluri" value="0.00"/>
				<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
				<input type="hidden" name="nServicio_A_Bienes" id="nServicio_A_Bienes" value="0"/>
				<input type="hidden" name="cNumProcedimientoCNET" id="cNumProcedimientoCNET" value=""/>
				
		</form>				
	</body>
</html>