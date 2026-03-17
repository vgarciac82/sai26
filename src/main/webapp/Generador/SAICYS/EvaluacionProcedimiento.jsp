<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
	   return;
	}
	String roles = "";
	Map rol = usuario.getRoles();
	String cEjercicio = "";
	String cIdTipoProcedimiento = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";

	if (session.getAttribute(GestionInterface.ATT_ProEjercicio) != null) {
		cEjercicio = (String) session
				.getAttribute(GestionInterface.ATT_ProEjercicio);
		cIdUnidadEjecutora = (String) session
				.getAttribute(GestionInterface.ATT_ProUnidadEjecutora);
		cIdTipoProcedimiento = (String) session
				.getAttribute(GestionInterface.ATT_ProTipoProcedimiento);
		nIdConsecutivo = (String) session
				.getAttribute(GestionInterface.ATT_ProConsecutivo);
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
			$("#tbs").val(7);
			showAndHideTabs();
		<%NegativaPestana NegPestana = new NegativaPestana();
			NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic(
					"jdbc/gestion");
			Iterator it1 = rol.entrySet().iterator();
			Role role = new Role();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry) it1.next();
				roles += r.getKey().toString() + ",";
			}
			if (roles.length() > 0) {
				roles = roles.substring(0, roles.length() - 1);
			}

			int imgAdjudicarE = 0;
			int imgDesiertoE = 0;
			int imgDevolverE = 0;
			Map botonesEvaluacion = nb.getBotones(roles, "Procedimiento",
					"EvaluacionProcedimiento");
			Iterator btnEvaluacion = botonesEvaluacion.entrySet().iterator();
			while (btnEvaluacion.hasNext()) {
				Map.Entry bEvaluacion = (Map.Entry) btnEvaluacion.next();%>
				$("#<%=bEvaluacion.getValue()%>").attr("disabled", true);<%String img = (String) bEvaluacion.getValue();
				if ("imgAdjudicarEvaluacionProc".equals(img)) {
					imgAdjudicarE = 1;
				}
				if ("imgDesiertoEvaluacionProc".equals(img)) {
					imgDesiertoE = 1;
				}
				if ("imgDevolverEvaluacionProc".equals(img)) {
					imgDevolverE = 1;
				}
			}%>	
		muestraInformacion();
		muestraProveedoresEvaluacion();
		tablaProveedorPartida();
		mostrarCatalogo(); 
		firmantesTblComparativa();
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuarioCreacion").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		$("#cIdUsuario").val('<%=usuario.getLogin()%>');
		$('#tblProveedoresEvaluacion tr').live('dblclick', function() { 
				if($("#nIdEstadoProcedimiento").val().toString().indexOf("CAPTURADO") >= 0){
					$("#chk_pedidoAbiertoCompleto").removeAttr("disabled");
				}
				else{
					$("#chk_pedidoAbiertoCompleto").attr("disabled","true");
				}	
				$(ProveedoresEvaluacion.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
           	    var aTrs = $('#tblProveedoresEvaluacion').dataTable().fnGetNodes();
           		for (var i=aTrs.length;i>=0; i-- ){       
					if ( $(aTrs[i]).hasClass('row_selected') ){  
						var nTr = $('#tblProveedoresEvaluacion').dataTable().fnGetData(aTrs[i]);
						var RFC=nTr[5];
						var razonSocial=nTr[6];
						$("#lblPartidaProveedor").val(RFC+" -  "+razonSocial);
						$("#cidRFCOculto").val(RFC);	
						queryFormPost("sp_existePartidaProcedimiento", {async: false });
						queryFormPost("cContratoAbiertoRead", {async: false });
						if($("#IContratoAbiertoProveedor").val()=="TRUE"){
			            	document.getElementById("chk_pedidoAbiertoCompleto").checked=true;
			        	}
		           	 	else{
		            		document.getElementById("chk_pedidoAbiertoCompleto").checked=false;
			        	}
					}
				}
         	tablaProveedorPartida();
        });
		
		habilitarPestanas();
		
		///////////AGREGAR FIRMANTES//
		$('#tblCatalogoFirmantes tr').live('dblclick', function() {        
			if(validaModificarProcedimiento()){
				$(oTableCatalogo.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');   
				var anSelected = fnGetSelected( oTableCatalogo );						
				var aData = oTableCatalogo.fnGetData(anSelected[0]);					
				$("#nIdFirmante").val(aData[1]);
				queryFormPost("pa_mAgregaFirmanteComparativaCreate", {async: false});
				oTableFirmantes.fnClearTable(oTableFirmantes);
				firmantesTblComparativa();	
			}
		});	
		
		///////////QUITAR FIRMANTES//////////////
		$('#tblFirmantesTblComparativa tr').live('dblclick', function(){
			if(validaModificarProcedimiento()){
				//Click handler para quitar un firmante, al momento de dar de baja un firmante se ejecuta un trigger para renumerar
					$(oTableFirmantes.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableFirmantes );
					var aData = oTableFirmantes.fnGetData(anSelected[0]);
					$("#nNumeroFirmante").val(aData[0]);		
					$("#nIdFirmante").val(aData[1]);	
					queryFormPost("firmanteComparativaDelete", {async: false});	
					queryFormPost("procedimientoComparativaFirmantesUpdate", {async:false});
					oTableFirmantes.fnClearTable(oTableFirmantes);	
					firmantesTblComparativa();
			}			
		});
	});
	
	function tablaProveedorPartida(){
		var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"','"+$("#cidRFCOculto").val()+"'";//,'"+$("#estadoPartida").val()+"'";
		var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";  
		//query += " AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"' AND nIdLineaConsolidado='"+$("#lineaConsolidado").val()+ "'" ;
		query += " AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'";
		$('#tblPartidasProveedorEvaluacion2').dataTable().fnClearTable();
	    $('#tblPartidasProveedorEvaluacion').dataTable().fnClearTable(); 
		
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			//alert("abierto");
	 		$("#div_tblPartidasProveedorEvaluacion").css("display","none");
			$("#div_tblPartidasProveedorEvaluacion2").css("display","");
		    $("#tblPartidasProveedorEvaluacion2").dataTable({
					 sScrollX: "100%",
					 //sScrollXInner: "500%",
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
				
				 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasCompletoBD(" + func + ")&qw="+ query ,
				 "fnServerData": function ( sSource, aoData, fnCallback ) {
                     $.ajax( {
                         "dataType": 'json', 
                         "type": "POST", 
                         "url": sSource, 
                         "data": aoData, 
                         "success": fnCallback
                     } );
                 },
				 aaSorting: [[ 16, "asc" ]] ,
				 aoColumns: [
					{ sName: "ganadorAbt" },
					{ sName: "ganadorSugerido",bVisible: false},										
					{ sName: "ganadorSugOrden", bVisible: false},
					{ sName: "nIdLineaConsolidadoAbt"  ,bSortable:false},
					{ sName: "cDescripcionAbt",bSortable:false},
					{ sName: "cDescripcionAbtAdicional",bSortable:false},
					{ sName: "nCantidad1",bSortable:false },
					{ sName: "cUnidad",bSortable:false },					
					{ sName: "tipoCambioAbt",bSortable:false },
					{ sName: "mMontoMinimoF",bSortable:false },
					{ sName: "mMontoMaximoF",bSortable:false },
					{ sName: "mMontoMaximoBruto1",bSortable:false},
					{ sName: "mMontoMaximo1",bSortable:false },
					{ sName: "evaluacionTecnicaAbt" }, 
					{ sName: "observacionesAbt" },
					{ sName: "botonNoCotiza" },
					{ sName: "boton" },					
					{ sName: "identificador", bVisible: false}
				] 
        	});
		}
		else{
			$("#div_tblPartidasProveedorEvaluacion2").css("display","none");
			$("#div_tblPartidasProveedorEvaluacion").css("display","");
		    $("#tblPartidasProveedorEvaluacion").dataTable({
				sScrollX: "100%",
						 sScrollXInner: "500%",
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
					
					 sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasCompletoBD(" + func + ")&qw="+ query ,
					"fnServerData": function ( sSource, aoData, fnCallback ) {
	                     $.ajax( {
	                         "dataType": 'json', 
	                         "type": "POST", 
	                         "url": sSource, 
	                         "data": aoData, 
	                         "success": fnCallback
	                     } );
	                 },
					 aaSorting: [[ 15, "asc" ]] ,
					 aoColumns: [
						{ sName: "ganador" },
						{ sName: "ganadorSugerido",bVisible: false},										
						{ sName: "ganadorSugOrden", bVisible: false},
						{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
						{ sName: "cDescripcion1",bSortable:false},
						{ sName: "cDescripcionAdicional1",bSortable:false},
						{ sName: "nCantidad1",bSortable:false },
						{ sName: "cUnidad",bSortable:false },					
						{ sName: "mMontoMaximoUnitario1",bSortable:false },
						{ sName: "tipoCambio",bSortable:false },
						{ sName: "mMontoMaximoBruto1",bSortable:false},
						{ sName: "mMontoMaximo1",bSortable:false },
						{ sName: "evaluacionTecnica" }, 
						{ sName: "observaciones" },
						{ sName: "botonNoCotiza" },
						{ sName: "boton" },					
						{ sName: "identificador", bVisible: false}
			
					] 
	        	});
		}
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

	function validaBotones(boton){
		var imgAdjudicarE='<%=imgAdjudicarE%>';
		var imgDesiertoE='<%=imgDesiertoE%>';
		var imgDevolverE='<%=imgDevolverE%>';
		switch (boton){
		case "imgAdjudicarEvaluacionProc":
			if (imgAdjudicarE==0){
				adjudicarProcedimiento();
			}else{
				alert("No tiene permiso para esta accion");
			}
		break;
		case "imgDesiertoEvaluacionProc":
			if (imgDesiertoE==0){
				desiertoProcedimiento();
			}else{
				alert("No tiene permiso para esta accion");
			}
		break;
		case "imgDevolverEvaluacionProc":
			if (imgDevolverE==0){
				devolverProcedimiento();
			}else{
				alert("No tiene permiso para esta accion");
			}
		break;
		}/// FIN SWITCH
	} 
		
	function adjudicarProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" || $("#nIdEstadoProcedimiento").val()=="DESIERTO" )
				return;
		queryFormPost("mSaldosDelete", {async:false});	
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
			
			/* var proc=""+$("#cEjercicio").val()+" ,"+$("#cIdTipoProcedimiento").val()+","+$("#cIdUnidadEjecutora").val()+","+$("#nIdConsecutivo").val()+","+$("#tipoProceso").val()+"";
			$.getJSON("../../servlet/ProcedimientoServlet?cmd=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
		       for(var i = 0; i < j.length; i++){
	             var col=j[i].Col1;
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
			}); */
			$.ajax({url: '../../servlet/ProcedimientoServlet?cEjercicio='+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
			 type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
				function(j){
					mensajeAp=j[0].Contable1;
					alert(mensajeAp);
					window.location = "Procedimiento-copia.jsp?tab=7";
					$("#cAccion").val("ADJUDICA_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});	
				}
			});
		}
  	}  
  	
   function desiertoProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" ||$("#nIdEstadoProcedimiento").val()=="DESIERTO" )
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
	 
  	/* function devolverProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="CAPTURADO "){
				alert("No se puede devolver el procedimiento, porque su estado no lo permite");
				return;
			}
			var proc=""+$("#cEjercicio").val()+" ,"+$("#cIdTipoProcedimiento").val()+","+$("#cIdUnidadEjecutora").val()+","+$("#nIdConsecutivo").val()+"";
			$.getJSON("../../servlet/ProcedimientoServlet?cmd=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
			for(var i = 0; i < j.length; i++){
	           var col=j[i].Col1;
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
	} */
	function devolverProcedimiento(){
		if(validaModificarProcedimiento()){
			if($("#nIdEstadoProcedimiento").val()=="CAPTURADO" ||  $("#nIdEstadoProcedimiento").val()=="DESIERTO"){
				alert("No se puede devolver el procedimiento, porque su estado no lo permite");
				return;
			}
		   $.ajax({url: '../../servlet/ProcedimientoServlet?', type:'post' , async: false, 
           data:"operacion=3&cEjercicio="+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
            dataType: 'json', success: 
            function(j){
            		mensajeAp=j[0].Contable1;
					alert(mensajeAp);
					window.location = "Procedimiento-copia.jsp?tab=7";	
					$("#cAccion").val("DEVOLVER_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			}
	   		}); 
		}
	}
	
	function validaModificarProcedimiento(){
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRoleProcedimiento').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
	
	function actualizaDatosProcedimientoDesierto(){
		$("#nIdEstadoProcedimiento").val("DESIERTO");
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
    }
  
  	function actualizaDatosProcedimientoAdjudicado(){
		$("#nIdEstadoProcedimiento").val("ADJUDICADO");
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
		var imagen=$("#imagenEstadoProcedimiento").val();	   
   }
   function actualizaDatosProcedimientoDevuelto(){
		$("#nIdEstadoProcedimiento").val("CAPTURADO");
	    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
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
					document.getElementById('trBotones').style.display = 'none';
					document.getElementById('trSalir').style.display = 'block';
				}else{
					$("#presupuestoProcedimiento" ).css("display", "block");
					$("#precompromisoProcedimiento" ).css("display", "block");	
					document.getElementById('trBotones').style.display = 'block';
					document.getElementById('trSalir').style.display = 'none';
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
						document.getElementById('trBotones').style.display = 'block';
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
					
					document.getElementById('trBotones').style.display = 'none';
					document.getElementById('trSalir').style.display = 'block';
				}else{
					document.getElementById('trBotones').style.display = 'block';
					document.getElementById('trSalir').style.display = 'none';
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
		if(esRegularizacion()){
				$("#presupuestoProcedimiento" ).css("display", "none");
				$("#precompromisoProcedimiento" ).css("display", "none");
		}
	}
	
	function validaContratoPedidoAbiertoCompleto(check){
		if(check.checked){
			$("#IContratoAbiertoProveedor").val("TRUE");
		}else{
			$("#IContratoAbiertoProveedor").val("FALSE");
		}
		queryFormPost("mUpdateContratoPedidoAbierto",{async:false});
		tablaProveedorPartida();
	}
	
	function SeleccionaTodosEvaluacion(){
		var chk=1;
		var aTrs;
		var nTr;
		var n;

		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
			nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[0]);
			n=nTr[16];
			if(document.getElementById("evaluacionTecnicaAbt_"+n+"").checked){
			  chk=0;
		    }
		}else{
			aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
			nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[0]);
			n=nTr[15];
			if(document.getElementById("evaluacionTecnica_"+n+"").checked){
			  chk=0;
		    }
		}
		for ( var i=0 ; i<aTrs.length; i++ ) {
			var nTr;
			var n;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[i]);
				n=nTr[16];
				if(chk==1){
					document.getElementById("evaluacionTecnicaAbt_"+n+"").checked=true;
					$("#cumpleTecnicaAbt_"+n+"").val(1);
				}else{
					document.getElementById("evaluacionTecnicaAbt_"+n+"").checked=false;
					$("#cumpleTecnicaAbt_"+n+"").val(0);
				}
			}else{
				nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[i]);
				n=nTr[15];
				if(chk==1){
					document.getElementById("evaluacionTecnica_"+n+"").checked=true;
					$("#cumpleTecnica_"+n+"").val(1);
				}else{
					document.getElementById("evaluacionTecnica_"+n+"").checked=false;
					$("#cumpleTecnica_"+n+"").val(0);
				}
			}
		}
	}
	
	function CopiarObservaciones(){
		var aTrs;
		var obs;
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
		    obs= $("#observacionesAbt_0").val();
		}else{
			aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
			obs= $("#observaciones_0").val();
		}
	
		for ( var i=1 ; i<aTrs.length; i++ ) {
			var nTr;
			var n;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[i]);
				n=nTr[16];
				$("#observacionesAbt_"+n+"").val(obs);
			}else{
				nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[i]);
				n=nTr[15];
				$("#observaciones_"+n+"").val(obs);
			}
			
		}
	}
	
	/* function SeleccionaTodosEvaluacion(){
		var chk=1;
		var aTrs;
		var nTr;
		var n;

		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
			nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[0]);
			n=nTr[16];
			if(document.getElementById("evaluacionTecnicaAbt_"+n+"").checked){
			  chk=0;
		    }
		}
		else{
			aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
			nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[0]);
			n=nTr[15];
			if(document.getElementById("evaluacionTecnica_"+n+"").checked){
			  chk=0;
		    }
		}
		for ( var i=0 ; i<aTrs.length; i++ ) {
			var nTr;
			var n;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[0]);
				n=nTr[16];
				if(chk==1){
					document.getElementById("evaluacionTecnicaAbt_"+n+"").checked=true;
					$("#cumpleTecnicaAbt_"+n+"").val(1);
				}else{
					document.getElementById("evaluacionTecnicaAbt_"+n+"").checked=false;
					$("#cumpleTecnicaAbt_"+n+"").val(0);
				}
			}else{
				nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[0]);
				n=nTr[15];
				if(chk==1){
					document.getElementById("evaluacionTecnica_"+n+"").checked=true;
					$("#cumpleTecnica_"+n+"").val(1);
				}else{
					document.getElementById("evaluacionTecnica_"+n+"").checked=false;
					$("#cumpleTecnica_"+n+"").val(0);
				}
			}
		}
	} */
	
	function guardaProcedimientoCompleto(){
		if(validaModificarProcedimiento()){
			var aTrs;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
			}
			else{
				aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
				
				}
		
    	for ( var i=0 ; i<aTrs.length; i++ ){
    		var aTrs;
		    var nTr;	
		    var n;
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				 aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
				 nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[i]);
				 n=nTr[17];
			}else{
				aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
				nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[i]);
				n=nTr[16];
			}
			
		    $("#numGanador").val("");
		    calcularMonto(n);
		    if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
		    	aTrs = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetNodes();
		    	nTr = $('#tblPartidasProveedorEvaluacion2').dataTable().fnGetData(aTrs[i]);
		   		n=nTr[17];
			var montoMaxUnit=$("#montoMaxUnit").val(quitaFmt($("#mMontoMinimoC_"+n+"").val()));
			var montoMaxBrut=$("#montoMaxBrut").val(quitaFmt($("#mMontoMaximoC_"+n+"").val()));
	              if(parseFloat(montoMaxUnit) >= parseFloat(montoMaxBrut)){
	                	alert("El monto m\xEDnimo debe ser menor que el monto m\xE1ximo.");
						return;       
	               }
			}else{
				aTrs = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetNodes();
		    	nTr = $('#tblPartidasProveedorEvaluacion').dataTable().fnGetData(aTrs[i]);
		    	n=nTr[16];
		    	
		    	$("#montoMaxUnit").val(quitaFmt($("#mMontoMaximoUnitario_"+n+"").val()));
				$("#montoMaxBrut").val(quitaFmt($("#mMontoMaximoUnitario_"+n+"").val()));
			}
		  
            var montoMaxUnit=quitaFmt($("#montoMaxUnit").val());
			var gan;
		   
		    if (document.getElementById("chk_pedidoAbiertoCompleto").checked){
		    	if(document.getElementById("ganadorAbt_"+n+"").checked){
				  gan=1;
				}else{
				  gan=0;
			    }
		    	$("#lineaConsolidado").val($("#nIdLineaConsolidadoAbt_"+n+"").val());
		    	$("#descripcionPartida").val($("#cDescripcionAbt_"+n+"").val());
		    	$("#descripcionPartidaAdicional").val($("#cDescripcionAbtAdicional_"+n+"").val());
		    	$("#evaluacionTec").val($("#cumpleTecnicaAbt_"+n+"").val());
		    	$("#Observaciones").val($("#observacionesAbt_"+n+"").val());
		    }else{
		    	if(document.getElementById("ganador_"+n+"").checked){
				  gan=1;
				}else{
				  gan=0;
			    }
		    	$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+n+"").val());
		    	$("#descripcionPartida").val($("#cDescripcion_"+n+"").val());
		    	$("#descripcionPartidaAdicional").val($("#cDescripcionAdicional_"+n+"").val());
		    	$("#evaluacionTec").val($("#cumpleTecnica_"+n+"").val());
		    	$("#Observaciones").val($("#observaciones_"+n+"").val());
		    }

			if(gan==1){
				if ($("#evaluacionTec").val()==1){ ///cumple evaluacion tecnica
					queryFormPost("mProcedimientoContarGanador", {async : false});
					if ($("#numGanador").val()==0){
					    if (parseFloat(montoMaxUnit)==0.00){
					    	//	alert("El proveedor "+$("#lblPartidaProveedor").val()+" no puede cotizar la partida No "+$("#nIdLineaConsolidado_"+n+"").val()+" en cero y ser ganador");
					    	alert("El proveedor "+$("#lblPartidaProveedor").val()+" no puede cotizar la partida No "+$("#lineaConsolidado").val()+" en cero y ser ganador");
					    		return;
					    	}else{
						    	$("#ganador").val(1);
					    		queryFormPost("spAgregaLineaPartidaCompleto", {async : false});
					    	}
						}else{
				    		if (confirm("La partida No. "+$("#lineaConsolidado").val()+" ya cuenta con un proveedor ganador.¿Desea Cambiarlo?")) {
				    			$("#ganador").val(1);
					    		queryFormPost("procedimientoGanadorSeleccionadoUpdate", {async : false});
						    	 queryFormPost("mProcedimientoPartidaBorraProv", {async : false});
						    	// inserta en mprocedimientoAdjudicacionPartidas
						    	queryFormPost("spAgregaLineaPartidaCompleto", {async : false});
							}
					    }
					}else{
						alert("El proveedor "+$("#lblPartidaProveedor").val()+" no puede ser ganador de la partida No "+$("#lineaConsolidado").val()+" ya que no cumple evaluacion tecnica");
				   		return;
					}
    
				}else{
					$("#ganador").val(0);
				    queryFormPost("mProcedimientoPartidaDesierta", {async : false});
				}
				$("#ganadorSug").val(0);
				//alert("valor  de ganador para "+n + $("#ganador").val());
				queryFormPost("procedimientoPartidasEvaluacionUpdate", {async : false});/////actualiza la tabla
			}

    		tablaProveedorPartida();
    		habilitarPestanas();
		}
	}
	
	function firmantesTblComparativa(){
		var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
		 oTableFirmantes = $("#tblFirmantesTblComparativa").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mFirmantesComparativa&qw=" + query,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nNumeroFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" },
					{ sName: "cTipoFirmante" }
				]
        	});
	}
	function mostrarCatalogo () {
		var qw = "cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'"; 
		oTableCatalogo = $("#tblCatalogoFirmantes").dataTable({
			bAutoWidth : true,
			bDestroy: true,
			sScrollX: "100%",
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catalogoFirmantesPedido&qw=" + qw,
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aaSorting: [[ 1, "asc" ]] ,
			aoColumns: [
				{ sName: "cIdUnidadEjecutora" },
				{ sName: "nIdFirmante" },
				{ sName: "cNombre" },
				{ sName: "cPuesto" }
			]
       	});
	}
	function fnGetSelected( oTableLocal ){
		var aReturn = new Array();
		var aTrs = oTableLocal.fnGetNodes();
		for ( var i=0 ; i<aTrs.length ; i++ ){
			if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
			}
		}
		return aReturn;
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
	
	function declararDesierta(linea,valor){
		if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO")
		   	 return;
		$("#lineaConsolidado").val(linea);
		if (valor==0){ //// partida desierta
			/////// actualizar a 0 el ganador
			$("#estadoPartida").val(0);
			queryFormPost("procedimientoPartidaDesiertaUpdate", {async:false});
			// eliminar de la tabla de madjudicacion partidas al proveedor ganador en caso de que exista
			queryFormPost("mProcedimientoPartidaDesierta", {async:false});
			queryFormPost("consolidadoLineasNoProveedor", {async:false});
		}else{// partida activada
			$("#estadoPartida").val(1);
			queryFormPost("consolidadoLineaSiProveedor", {async:false});
			
		}
		tablaProveedorPartida();
	}
	
	function borraDatos1(indice){
		var br=$("#mMontoMaximoUnitario_"+indice+"").val();
		br=br.replace("$","");
		br=br.replace(",","");
		$("#mMontoMaximoUnitario_"+indice+"").val(br);	
	}
	
	function validaNegativos(indice){
	   var maxUnitario= quitaFmt($("#mMontoMaximoUnitario_"+indice+"").val());
	   	if(parseFloat(maxUnitario) < 0){
			//alert("No puede ingresar valores menores o iguales a cero");
			return "1";
		}else{
		  return "2";
		}
	}
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789.';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
		 return false; 
		 
		return true;
	}
	
	function cambiaTipoMonedaEvaluacion(idCambio){
		 if (document.getElementById("chk_pedidoAbiertoCompleto").checked){
			 if($("#cboCambioCotizacionAbt_"+idCambio+"").val()!=1){
			   $("#valorTipoCambioAbt_"+idCambio+"").css("visibility","visible");		
		       if($("#cboCambioCotizacionAbt_"+idCambio+"").val()==3){
			     $("#valorTipoCambioAbt_"+idCambio+"").val($("#tipoCambio").val());
		      }else{
			     var doc=document.getElementById("valorTipoCambioAbt_"+idCambio+"");
			     doc.value="";
		      }
	        }else{
		      $("#valorTipoCambioAbt_"+idCambio+"").css("visibility","hidden");
	          $("#valorTipoCambioAbt_"+idCambio+"").val(1);
	        }
		 }else{
			 if($("#cboCambioCotizacion_"+idCambio+"").val()!=1){
		        $("#valorTipoCambio_"+idCambio+"").css("visibility","visible");
		        if($("#cboCambioCotizacion_"+idCambio+"").val()==3){
			        $("#valorTipoCambio_"+idCambio+"").val($("#tipoCambio").val());
		        }else{
			       var doc=document.getElementById("valorTipoCambio_"+idCambio+"");
			       doc.value="";
		        }
	        }else{
				$("#valorTipoCambio_"+idCambio+"").css("visibility","hidden");
				$("#valorTipoCambio_"+idCambio+"").val(1);
	        }
		 }
  }
  
  	function cambiaEvaluacion(idPosicion){
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			var costo=$("#mMontoMinimoC_"+idPosicion+"").val();
		}else{
			var costo=$("#mMontoMaximoUnitario_"+idPosicion+"").val();
		}
		costo=costo.replace("$","");
		if (costo==0 || costo==0.00 ){
			alert("El precio no puede ser igual a $0.00");
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				document.getElementById("evaluacionTecnicaAbt_"+idPosicion+"").checked=false;
			}else{
				document.getElementById("evaluacionTecnica_"+idPosicion+"").checked=false;
			}
			
		}else{
			if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
				if(document.getElementById("evaluacionTecnicaAbt_"+idPosicion+"").checked){
					$("#cumpleTecnicaAbt_"+idPosicion+"").val(1);
				}else{
					$("#cumpleTecnicaAbt_"+idPosicion+"").val(0);
				}
			}else{
				if(document.getElementById("evaluacionTecnica_"+idPosicion+"").checked){
					$("#cumpleTecnica_"+idPosicion+"").val(1);
				}else{
					$("#cumpleTecnica_"+idPosicion+"").val(0);
				}
			}
			
		}
	}
	
	function calcularMonto(idCambio){
		if(validaModificarProcedimiento()){
		 	 if (document.getElementById("chk_pedidoAbiertoCompleto").checked){
				//var valida=validaNegativos($("#mMontoMinimoC_"+idCambio+"").val());
				var valida=validaNegativos(idCambio);
				var ind=parseInt(idCambio,10);
			 	if(valida=="1"){
					 alert("No puede ingresar valores menores o iguales a cero en la partida " + (ind+1));
				 	return ;
				}     
			    if($("#cboCambioCotizacionAbt_"+idCambio+"").val()!=1){
					if($("#valorTipoCambioAbt_"+idCambio+"").val()==""){
						alert("Ingrese el valor del tipo de cambio");
						return;
					}
					$("#valorTipoCambioAbt_"+idCambio+"").val();
				}else{
					$("#valorTipoCambioAbt_"+idCambio+"").val(1);
				}
				$("#IContratoAbiertoProveedor").val('TRUE');
				$("#tipoCambioEvaluacion").val($("#cboCambioCotizacionAbt_"+idCambio+"").val());
				$("#cantidadCambioEvaluacion").val($("#valorTipoCambioAbt_"+idCambio+"").val()); 	
    			$("#lineaConsolidado").val($("#nIdLineaConsolidadoAbt_"+idCambio+"").val()); 
			 }else{
			    //var valida=validaNegativos($("#mMontoMaximoUnitario_"+idCambio+"").val());
			 	var valida=validaNegativos(idCambio);
			 	var ind=parseInt(idCambio,10);
			 	if(valida=="1"){
					 alert("No puede ingresar valores menores o iguales a cero en la partida " + (ind+1));
				 	return ;
				}     
			    if($("#cboCambioCotizacion_"+idCambio+"").val()!=1){
					if($("#valorTipoCambio_"+idCambio+"").val()==""){
						alert("Ingrese el valor del tipo de cambio");
						return;
					}
					$("#valorTipoCambio_"+idCambio+"").val();
				}else{
					$("#valorTipoCambio_"+idCambio+"").val(1);
				}
				$("#IContratoAbiertoProveedor").val('FALSE');
				$("#tipoCambioEvaluacion").val($("#cboCambioCotizacion_"+idCambio+"").val());
				$("#cantidadCambioEvaluacion").val($("#valorTipoCambio_"+idCambio+"").val()); 	
    			$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+idCambio+"").val());
				 
			 }
			
    		/// actualiza tipo de cambio y monto en tabla mprocedimientoCompleto
    		queryFormPost("procedimientoCompletoAdjudicacionUpdate",{async : false});
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
	function generaTablaComparativa(){
		///// ordenar proveedores
		$("#flag").val(1); 
		queryFormPost("sp_ordenaProveedor", {async:false});
		$("#flag").val(0);
		queryFormPost("sp_ordenaProveedor", {async:false});
		var numFirmantes = oTableFirmantes.dataTable().fnGetNodes().length;
		if (numFirmantes==2){
			var cEjercicio=$("#cEjercicio").val(); 
		var cIdTipoProcedimiento=$("#cIdTipoProcedimiento").val(); 
		var cIdUnidadEjecutora=$("#cIdUnidadEjecutora").val(); 
		var nIdConsecutivo=$("#nIdConsecutivo").val(); 
		param="cEjercicio=" + cEjercicio
			+ "&cIdTipoProcedimiento=" + cIdTipoProcedimiento
			+ "&cIdUnidadEjecutora=" + cIdUnidadEjecutora
			+ "&nIdConsecutivo=" + nIdConsecutivo
		+"&tipoReporte="+$('[name="reporte"]:checked').val();
		//alert(param);
		 window.open('tablaComparativaResultado.jsp?' + param, 'tablaComparativaResultado', 'status=1, width=900px, height=500px,scrollbars=yes,resizable=yes');
		}else{
			alert("Eliga los firmantes correspondientes");
		}
		
	}
	function NoAdjudicaProveedor(i){
		if(document.getElementById("chk_pedidoAbiertoCompleto").checked){
			if (document.getElementById("ganadorAbt_"+i).checked){
				if (confirm("La partida No. "+$("#nIdLineaConsolidadoAbt_"+i+"").val()+" ya cuenta con un proveedor ganador.¿Desea eliminarlo?")){
					$("#mMontoMinimoC_"+i+"").val(0.00);
				}else{
					return;
				}
			}else{
				$("#mMontoMinimoC_"+i+"").val(0.00);
			
			}
		$("#montoMaxUnit").val($("#mMontoMinimoC_"+i+"").val());
		$("#montoMaxBrut").val($("#mMontoMaximoC_"+i+"").val());
		$("#descripcionPartida").val($("#cDescripcionAbt_"+i+"").val());
		$("#descripcionPartidaAdicional").val($("#cDescripcionAbtAdicional_"+i+"").val());
		$("#evaluacionTec").val(0);
		$("#Observaciones").val($("#observacionesAbt_"+i+"").val());
		$("#ganadorSug").val(0);
		$("#ganador").val(0);
		$("#lineaConsolidado").val($("#nIdLineaConsolidadoAbt_"+i+"").val());
		}else{
			if (document.getElementById("ganador_"+i).checked){
			if (confirm("La partida No. "+$("#nIdLineaConsolidado_"+i+"").val()+" ya cuenta con un proveedor ganador.¿Desea eliminarlo?")){
				$("#mMontoMaximoUnitario_"+i+"").val(0.00);
			}else{
				return;
			}
		}else{
			$("#mMontoMaximoUnitario_"+i+"").val(0.00);
			
		}
		$("#montoMaxUnit").val($("#mMontoMaximoUnitario_"+i+"").val());
		$("#montoMaxBrut").val($("#mMontoMaximoUnitario_"+i+"").val());
		$("#descripcionPartida").val($("#cDescripcion_"+i+"").val());
		$("#descripcionPartidaAdicional").val($("#cDescripcionAdicional_"+i+"").val());
		$("#evaluacionTec").val(0);
		$("#Observaciones").val($("#observaciones_"+i+"").val());
		$("#ganadorSug").val(0);
		$("#ganador").val(0);
		$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+i+"").val());
		}
		
		queryFormPost("procedimientoPartidasEvaluacionUpdate", {async : false});
		tablaProveedorPartida();
	}
	function borraDatos4(obj){
		var br=obj.value;
		br=br.replace("$","");
		br=br.replace(",","");
		obj.value=br;	
	}
	
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});		
		if(parseFloat(quitaFmt($("#lblApartado").val()))<parseInt(quitaFmt($("#MontoConIVA").val()),10)){			
			return true;
		}else{
			return false;
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
	</script>
</head>
<body id="dt_example">
	<form>
		<fieldset>
			<legend>Informaci&oacute;n del Procedimiento</legend>
			<table align="left" width="100%">
				<tr id="trBotones" style='display:none'>
					<td align="right" colspan="2"><img
						id="imgAdjudicarEvaluacionProc" src="../imagenes/accept_green.png"
						style="cursor: pointer"
						onclick="validaBotones('imgAdjudicarEvaluacionProc');" />Adjudicar
						<img id="imgDesiertoEvaluacionProc"
						src="../imagenes/cancel_round.png" style="cursor: pointer"
						onclick="validaBotones('imgDesiertoEvaluacionProc');" />Desierto
						<img id="imgDevolverEvaluacionProc"
						src="../imagenes/arrow_left_blue_round.png"
						style="cursor: pointer;"
						onclick="validaBotones('imgDevolverEvaluacionProc');" />Devolver <img
						id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"
						onclick="salir();" />Salir</td>
				</tr>
				<tr id="trSalir" style='display:none'>
					<td align="right" colspan="2"><img id="imgSalir"
						src="../imagenes/cancel.png" style="cursor: pointer"
						onclick="salir();" />Salir</td>
				</tr>

				<!-- <tr>
						<td align="right" colspan="2"  >
							<img id="imgAdjudicarEvaluacionProc" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="validaBotones('imgAdjudicarEvaluacionProc');"/>Adjudicar
							<img id="imgDesiertoEvaluacionProc" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="validaBotones('imgDesiertoEvaluacionProc');"/>Desierto
							<img id="imgDevolverEvaluacionProc" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer;" onclick="validaBotones('imgDevolverEvaluacionProc');"/>Devolver
							<img id="imgSalir" src="../imagenes/cancel.png" style="cursor: pointer"  onclick="salir();"/>Salir
						</td>
				    </tr> -->
				<tr align="left">
					<td colspan="2">[[<input name="cIdProcedimiento"
						id="cIdProcedimiento" type="text" size="10"
						style="border: 0px solid black;" readonly="readonly" />]]&nbsp;<input
						name="desProcedimientoCaratula" id="desProcedimientoCaratula"
						type="text" maxlength="150"
						style="border: 0px solid black; width: 40em;" readonly="readonly"></input>
					</td>
				</tr>
				<tr align="left">
					<td colspan="2"><input name="lblcIdUnidadEjecutora"
						id="lblcIdUnidadEjecutora" type="text" readonly="readonly"
						style="border: 0px solid black; width: 30em;"></input></td>
				</tr>
				<tr align="left">
					<td colspan="2">Consolidado: [[<input name="cIdConsolidado"
						id="cIdConsolidado" type="text" size="10"
						style="border: 0px solid black;" readonly="readonly"></input>]]</td>
				</tr>
				<tr align="left">
					<td colspan="2">Tipo de Proceso: [[<input
						name="lbltipoProceso" id="lbltipoProceso" type="text" size="18"
						style="border: 0px solid black;" readonly="readonly"></input>]]</td>
				</tr>
				<tr align="left">
					<td colspan="2"><input name="nIdEstadoProcedimiento"
						id="nIdEstadoProcedimiento" type="text"
						style="border: 0px solid black; width: 45em;"></input></td>
				</tr>
				<tr align="left">
					<td colspan="2">Monto Precomprometido: [[<input
						name="lblApartado" id="lblApartado" type="text" size="10"
						style="border: 0px solid black;" readonly="readonly"></input>]]</td>
				</tr>
				<tr>
					<td><br />
					</td>
				</tr>
			</table>
		</fieldset>
		<!--  tabla que muestra los proveedores de la tabla mprocedimientoadjudicacion   -->
		<fieldset>
			<table id="tblProveedoresEvaluacion" border="2" class="display"
				width="800" height="50">
				<thead>
					<tr>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th width="400px">RFC</th>
						<th width="400px">Raz&oacute;n Social</th>
						<th></th>
					</tr>
				</thead>
			</table>

		</fieldset>
		<br />
		<br />
		<fieldset>
			<!--  tabla que muestra la relacion entre la partida y los proveedores seleccionados -->
			<legend>Partida-Proveedor</legend>
			<br />
			<div style="width:100%;text-align:left;">
				<input name="lblPartidaProveedor" id="lblPartidaProveedor"
					type="text" size="80" style="border: 0px solid black;"
					readonly="readonly" /> <br /> <input
					name="chk_pedidoAbiertoCompleto" id="chk_pedidoAbiertoCompleto"
					type="checkbox"
					onClick="validaContratoPedidoAbiertoCompleto(this);" />Pedido o
				Contrato Abierto
			</div>
			<table>
				<tr>
					<td colspan="2"><input name="lblEstadoPartida"
						id="lblEstadoPartida" type="text" size="38"
						style="border: 0px solid black;" readonly="readonly"></input></td>
				</tr>
				<tr>
					<td><button name="btnSeleccionaTodos" id="btnSeleccionaTodos"
							onclick="SeleccionaTodosEvaluacion();">Todos Cumplen
							E.T.</button>
					</td>
					<td>&nbsp;</td>
					<td><button name="btnCopiaObservaciones"
							id="btnCopiaObservaciones" onclick="CopiarObservaciones();">Copiar
							Observación</button>
				</tr>
			</table>
			<div id="div_tblPartidasProveedorEvaluacion">
				<table id="tblPartidasProveedorEvaluacion" class="display"
					border="1" width="800" height="50">
					<thead>
						<tr>
							<th>Ganador</th>
							<th style="display: none;">Ganador Sugerido</th>
							<th style="display: none;"></th>
							<th>L&iacute;nea Consolidado</th>
							<th>Descripci&oacute;n</th>
							<th>Descripci&oacute;n Adicional</th>
							<th>Cantidad</th>
							<th>Unidad de Medida</th>
							<th>Precio Unitario</th>
							<th>Tipo Cambio</th>
							<th>Monto Bruto</th>
							<th>Monto Neto</th>
							<th>Cumple evaluaci&oacute;n T&eacute;cnica</th>
							<th>Observaciones</th>
							<th>No cotiza</th>
							<th>Elimina</th>
							<th></th>
						</tr>
					</thead>
				</table>
			</div>
			<div id="div_tblPartidasProveedorEvaluacion2">
				<table id="tblPartidasProveedorEvaluacion2" class="display"
					border="1" width="800" height="50">
					<thead>
						<tr>
							<th>Ganador</th>
							<th style="display: none;">Ganador Sugerido</th>
							<th style="display: none;"></th>
							<th>L&iacute;nea Consolidado</th>
							<th>Descripci&oacute;n</th>
							<th>Descripci&oacute;n Adicional</th>
							<th>Cantidad</th>
							<th>Unidad de Medida</th>
							<th>Tipo Cambio</th>
							<th>M&iacute;nimo</th>
							<th>M&aacute;ximo</th>
							<th>Monto Bruto</th>
							<th>Monto Neto</th>
							<th>Cumple evaluaci&oacute;n T&eacute;cnica</th>
							<th>Observaciones</th>
							<th>No cotiza</th>
							<th>Elimina</th>
							<th></th>
						</tr>
					</thead>
				</table>
			</div>
			<table>
				<tr>
					<td><button name="btnGuardaProcedimientoCompleto"
							id="btnGuardaProcedimientoCompleto"
							onclick="guardaProcedimientoCompleto();">Guardar Tabla
							Comparativa</button>
					</td>
				</tr>

			</table>
		</fieldset>

		<br />
		<br />

		<fieldset>
			<legend>Firmantes</legend>
			<table width="97%" align="left">
				<tr>
					<td>
						<table align="center" id="tblFirmantesTblComparativa" width="100%"
							class="display">
							<thead>
								<tr>
									<th>#</th>
									<th>NOMBRE</th>
									<th>PUESTO</th>
									<th>CONDICI&Oacute;N</th>
								</tr>
							</thead>
						</table></td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<legend>Cat&aacute;logo de Firmantes</legend>
			<table width="97%" align="left">
				<tr>
					<td>
						<table align="center" id="tblCatalogoFirmantes" width="100%"
							class="display">
							<thead>
								<tr>
									<th>UNIDAD EJECUTORA</th>
									<th>#</th>
									<th>NOMBRE</th>
									<th>PUESTO</th>
								</tr>
							</thead>
						</table></td>
				</tr>
			</table>
		</fieldset>
		<table>
			<tr>
				<td><input type="radio" name="reporte" value="tablaComparativa"
					checked>Reporte tabla Comparativa</td>
				<td><input type="radio" name="reporte"
					value="partidasComparativa">Reporte Partidas Comparativa</td>
			</tr>
		</table>
		<button name="btngeneraTablaComparativa"
			id="btngeneraTablaComparativa" onclick="generaTablaComparativa();">Reporte</button>

		<input id="cEjercicio" name="cEjercicio" type="hidden" size="4"
			value="<%=cEjercicio%>"> <input id="cIdTipoProcedimiento"
			name="cIdTipoProcedimiento" type="hidden" size="4"
			value="<%=cIdTipoProcedimiento%>"> <input
			id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" type="hidden"
			size="3" value="<%=cIdUnidadEjecutora%>"> <input
			id="nIdConsecutivo" name="nIdConsecutivo" type="hidden" size="4"
			value="<%=nIdConsecutivo%>"> <input id="cidRFCOculto"
			name="cidRFCOculto" value="" type="hidden" size="10"> <input
			id="nIdFirmante" name="nIdFirmante" value="" type="hidden" size="10">
		<input id="nNumeroFirmante" name="nNumeroFirmante" value=""
			type="hidden" size="10"> <input id="lineaConsolidado"
			name="lineaConsolidado" value="" type="hidden" size="10"> <input
			id="estadoPartida" name="estadoPartida" value="1" type="hidden"
			size="10"> <input id="estadoPartida" name="estadoPartida"
			value="1" type="hidden" size="10"> <input id="estadoPartida"
			name="estadoPartida" value="1" type="hidden" size="10"> <input
			id="tipoCambioEvaluacion" name="tipoCambioEvaluacion" value=""
			type="hidden" size="10"> <input id="cantidadCambioEvaluacion"
			name="cantidadCambioEvaluacion" value="" type="hidden" size="10">
		<input id="montoMaxUnit" name="montoMaxUnit" value="" type="hidden"
			size="10"> <input id="montoMaxBrut" name="montoMaxBrut"
			value="" type="hidden" size="10"> <input
			id="descripcionPartida" name="descripcionPartida" type="hidden"
			size="10"> <input id="evaluacionTec" name="evaluacionTec"
			value="" type="hidden" size="10"> <input id="Observaciones"
			name="Observaciones" value="" type="hidden" size="10"> <input
			id="ganadorSug" name="ganadorSug" value="" type="hidden" size="10">
		<input id="ganador" name="ganador" value="" type="hidden" size="10">
		<input id="flag" name="flag" value="" type="hidden" size="10">


		<input id="usuarioLogin" name="usuarioLogin" value="" type="hidden"
			size="10"> <input id="ivaProcedimiento"
			name="ivaProcedimiento" value="" type="hidden" size="10"> <input
			id="nIdTipoCambioProveedor" name="nIdTipoCambioProveedor" value=""
			type="hidden" size="10"> <input id="mTipoCambioProveedor"
			name="mTipoCambioProveedor" value="" type="hidden" size="10">
		<input id="nPorcentajeIVAProveedor" name="nPorcentajeIVAProveedor"
			value="" type="hidden" size="10"> <input
			id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento"
			value="" type="hidden" size="10"> <input
			id="cIdEntidadContable" name="cIdEntidadContable" value=""
			type="hidden" size="10"> <input id="cIdUsuarioCreacion"
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
		<input id="tieneProveedor" name="tieneProveedor" type="hidden"
			size="10"> <input id="cPartidaDesierta"
			name="cPartidaDesierta" type="hidden" size="10"> <input
			id="cProcedimientoCumple" name="cProcedimientoCumple" type="hidden"
			size="10"> <input id="tipoProceso" name="tipoProceso"
			type="hidden" size="10"> <input id="cboCategoria"
			name="cboCategoria" type="hidden" size="10"> <input
			id="descripcionCaratula" name="descripcionCaratula" type="hidden"
			size="10"> <input id="EstadoCaptura" name="EstadoCaptura"
			type="hidden" size="10"> <input id="numero_externoCaratula"
			name="numero_externoCaratula" type="hidden" size="10"> <input
			id="Activo" name="Activo" type="hidden" size="10"> <input
			id="numGanador" name="numGanador" type="hidden" size="10">

		<!-- habilita pestanas -->
		<input id="existePrecompromiso" name="existePrecompromiso"
			type="hidden" size="10"> <input id="nIdEstado"
			name="nIdEstado" type="hidden" size="10"> <input
			id="documentoAplicado" name="documentoAplicado" type="hidden"
			size="10"> <input id="partidasAdjudicadas"
			name="partidasAdjudicadas" type="hidden" size="10"> <input
			id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
		<input id="cEventoFlujo" name="cEventoFlujo" type="hidden" size="10">

		<!-- bitacora -->
		<input id="cIdDocumento" name="cIdDocumento" type="hidden" size="10">
		<input id="cAccion" name="cAccion" type="hidden" size="10"> 
		<input id="cIdUsuario" name="cIdUsuario" type="hidden" size="10">
		<input type="hidden" name="isPlurianual" id="isPlurianual" />
		<input type="hidden" name="descripcionPartidaAdicional" id="descripcionPartidaAdicional" />
	</form>
</body>
</html>