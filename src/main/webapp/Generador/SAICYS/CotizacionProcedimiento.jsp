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
		<meta http-equiv="description" content="CotizacionProcedimiento">
		
		<script type="text/javascript" charset="utf-8">
		var roles;
		
		$(document).ready(function() {
			$("#tbs").val(4);
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
			Map botones=nb.getBotones(roles,"Procedimiento","CotizacionProcedimiento");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
				String img=(String) b.getValue();
				if ("imgAdjudicarProc".equals(img)){    
						imgAdjudicarC=1; 
				}
				if ("imgDesiertoProc".equals(img)){
					imgDesiertoC=1;
				}
				if ("imgDevolverProc".equals(img)){
					imgDevolverC=1;
				}
			
			}
		%>	
		$("#usuarioLogin").val('<%=usuario.getLogin()%>');
		$("#cIdEntidadContable").val('<%=usuario.getPropiedad("CCENTROCONTABLE").getValor()%>');
		$("#cIdUsuario").val('<%=usuario.getLogin()%>');
		$("#usuarioRoleProcedimiento").val('<%=roles%>');
		roles='<%=roles%>';
		muestraInformacion();
		habilitarPestanas();
		agregaTablaProveedoresCotizaciones();
		queryInnerDivPost("llenaFechasProcedimiento",{async:false});
		querySelectPost("llenaTipoCambioCotizaciones","cboCambioCotizacion",{async:false});
		
		$("#formProveedores").css("visibility","hidden");
		$("#procedimientoMateriales").css("visibility","hidden");
		//$("#arrendamientos").css("visibility","hidden");
		$("#arrendamientos").css("display","none");
		$("#trDuplicidad").hide();
		$("#procedimientoArrendamiento").css("visibility","hidden");
		
		$("#tblProvCotizacion").css("visibility","hidden");
		$("#tblPartidasCotizacion").css("visibility","hidden");
		
		var cadena_campos=$("#cIdConsolidado").val().split('-');
			$("#TipoConsolidado").val(cadena_campos[0]);
			$("#ConsecutivoConsolidado").val(cadena_campos[2]);
		///boton que borra todos los proveedores en cotizaciones
		$( "#borraTodoProveedoresCotizacion").button().click(function() {
			if(validaModificarProcedimiento()){
				if(parseInt($("#nIdEstado").val(),10)>1)
	       	 		return;	
				swal({
					title: "¿Est\u00e1 seguro que desea eliminar todos los proveedores? Una vez confirmado, no podr\u00e1 deshacer los cambios?",
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
						for (var i = 0; i < nEditaRegistrosProveedor; i++) {
							$("#cidRFCOculto").val($("#nIdRFCProveedor_" + i).val());
							$("#nIdconsecutivoAdj").val($("#nIdconsecutivoAdj_"+i+"").val());
							$("#nIdProcedimiento").val($("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val());
			                queryFormPost("deleteProveedorProcedimiento", {async:false});
			                queryFormPost("deleteDocumentosProveedor",{async:false});
						}
						agregaTablaProveedoresCotizaciones();
						$("#btnBuscarProveedorProcedimiento").click();
						$("#tblProvCotizacion").css("visibility","hidden");
					    $("#tblPartidasCotizacion").css("visibility","hidden");
					    $("#formProveedores").css("visibility","hidden");
					    $("#btnGuardarCotizacionProc").css("visibility","hidden");
					    $("#btnPartidaCotizacionProc").css("visibility","hidden");
					    guardaBitacora("ELIMINA_PROVEEDORES",$("#cIdProcedimiento").val());
					}
				});
		  }
	   });
		
		$("#tblProvedoresCotizaciones").dataTable({
				    bPaginate: false,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
        			sScrollY : "100%",
					sScrollX: "100%",
					sScrollXInner: "100%",
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
				}
				
		       });
		       
		oTable3= $("#tblPartidas").dataTable({
				    bPaginate: true,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
        			sScrollY : "100%",
					sScrollX: "200%",
					sScrollXInner: "100%",
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
				}			
        	});					       
		       
		//cuando se le da click a la tabla de proovedores 
		$('#tblProvedoresCotizaciones tr').live('dblclick', function() {
			if($("#nIdEstadoProcedimiento").val().toString().indexOf("CAPTURADO") >= 0){
				$("#chk_pedidoAbierto").removeAttr("disabled");
			}else{
				$("#chk_pedidoAbierto").attr("disabled","true");
			}
			if($("#cIdTipoProcedimiento").val()== "PT"){
				$("#trNumProcedimientoCNET").css("visibility", "visible");
		       
			}else{
				 $("#trNumProcedimientoCNET").css("visibility", "hidden");
			}
			document.getElementById("chk_pedidoAbierto").checked=false;
			$("#btnGuardarCotizacionProc").css("display","");
			$("#btnPartidaCotizacionProc").css("display","");
			$("#tblProvCotizacion").css("display","");
			if(validaModificarProcedimiento()){
				if ($(this).hasClass('row_selected') ) {            
					$(this).removeClass('row_selected'); 
			   	}else{
				  	var aTrs = $('#tblProvedoresCotizaciones').dataTable().fnGetNodes();
				  	if(aTrs.length==0){
				  		swal("No hay proveedores agregados.",{icon:"info",button: "Cerrar"});
				  		return;
				  	}
			      	for(var i=aTrs.length;i>=0; i-- ){       
						$(aTrs[i]).removeClass('row_selected'); 
						$(this).addClass('row_selected'); 
					}
				}    
				//mostramos formulario de proovedores en la seccion de cotizaciones
				//$("#trDuplicidad").css("visibility","visible");
				$("#trDuplicidad").show();
				$("#desProvedorCotizacion").css("visibility","visible");
				$("#tblProvCotizacion").css("visibility","visible");
				//$("#tablaProveedores").css("visibility","visible");
		      	$("#formProveedores").css("visibility","visible");
			  	//revisamos si es un tipo de procedimiento de arrendamiento
			  	if(($("#cIdTipoProcedimiento").val()== "PA") || ($("#cIdTipoProcedimiento").val()== "PL")){
					// inicialmente se deshabilitan los texbox delrubro de rmv de arrendamiento y mantenimiento
					document.getElementById("chk_rvArrendamiento").checked=false;
					document.getElementById("chk_rvMantenimiento").checked=false;
				
					//se habilitan los texbox del rubro de mantenimiento
			        $("#rvMantenimiento").css("visibility", "hidden");
			        $("#semanasMantenimiento").css("visibility", "hidden");		        
					//se habilitan los texbox del rubro de Arrendamiento
			        $("#rvArrendamiento").css("visibility", "hidden");
			        $("#semanasArrendamiento").css("visibility", "hidden");
					//se checa si existen datos en el la seccion de mantenimiento y arrendamiento
					queryFormPost("ConsultaCotizacionArrendamientos",{async:false});
					queryFormPost("ConsultaCotizacionMantenimiento",{async:false});
					if($("#rvArrendamiento").val()!="" || $("#semanasArrendamiento").val()!=""){
						document.getElementById("chk_rvArrendamiento").checked=true;
						//se habilitan los texbox del rubro de mantenimiento
			        	$("#rvMantenimiento").css("visibility", "visible");
			        	$("#semanasMantenimiento").css("visibility", "visible");		
					}
					if($("#rvMantenimiento").val()!="" || $("#semanasMantenimiento").val()!=""){
						document.getElementById("chk_rvMantenimiento").checked=true;
						//se habilitan los texbox del rubro de Arrendamiento
				        $("#rvArrendamiento").css("visibility", "visible");
				        $("#semanasArrendamiento").css("visibility", "visible");
					}
					//$("#arrendamientos").css("visibility","visible");
					$("#arrendamientos").css("display","block");
					$("#procedimientoArrendamiento").css("visibility","visible");
					//ocultamos la seccion de botones de proovedores y mostramos la seccion de arrendamientos
					$("#procedimientoMateriales").css("visibility","hidden");
				}else{
					$("#arrendamientos").css("display","none");
					//$("#arrendamientos").css("visibility","hidden");
					$("#procedimientoArrendamiento").css("visibility","hidden");
					//division de botones de proovedores
					$("#procedimientoMateriales").css("visibility","visible");
				}
				if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO"){
	            	$("#btnGuardarCotizacionProc").css("visibility","hidden");
					$("#btnPartidaCotizacionProc").css("visibility","hidden");
				}
           		var aTrs = $('#tblProvedoresCotizaciones').dataTable().fnGetNodes();
				for ( var i=aTrs.length ; i>=0; i-- ){
					if ($(aTrs[i]).hasClass('row_selected')){   
						$(this).addClass('row_selected'); 
						$("#cidRFCOculto").val($("#nIdRFCProveedor_"+i+"").val());
						$("#nIdconsecutivoAdj").val($("#nIdconsecutivoAdj_"+i+"").val());
						//llenamos rfc y razon social
						queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
						//llena montos bruto y neto
						queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
						queryFormPost("llenaMontoNetoCotizaciones",{async:false});
		            	// llena iva y tipo de cambio
		            	queryFormPost("llenaIvaCotizaciones",{async:false}); 
		            	queryFormPost("tipodeCambioProcedimiento",{async:false,
		            		callback : function() {
		            			muestraObserv();
							}
		            	});
		            	queryFormPost("cContratoAbiertoRead",{async:false});
						
						activaPedidoContServAbienes();
		            	if($("#IContratoAbierto").val()=="TRUE"){
			            	document.getElementById("chk_pedidoAbierto").checked=true;
			            	if($("#lComprometeMaximo").val()==1){
			            		$("#trComprometeMaximo").show();
			            		document.getElementById("chk_comprometeMaximo").checked=true;
			            	}
						}else{
							$("#trComprometeMaximo").hide();
							document.getElementById("chk_pedidoAbierto").checked=false;
							document.getElementById("chk_comprometeMaximo").checked=false;
				        }
	                    if($("#idTipoCambio").val()!="01"){
	                        $("#cboCambioCotizacion").val($("#idTipoCambio").val());
	                        $("#valorTipoCambio").css("visibility","visible");
	                      	$("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
						}else{
	                        $("#cboCambioCotizacion").val($("#idTipoCambio").val());
	                        $("#valorTipoCambio").css("visibility","hidden");
	                        $("#valorTipoCambio").val($("#valorTipoCambio1").val());
						}
	                    $("#valorTipoCambio").formatCurrency();
					    $("#montoBrutoCotizacion").formatCurrency();
						$("#montoNetoCotizacion").formatCurrency();
						if($("#isPlurianual").val()==1){
							$("#trMontoTotalPluri").css("display","block");
							queryFormPost("obtieneMontoPluri", {async: false });
							$("#montoTotalPluri").formatCurrency();
						}            
						var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
						var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
						query += "  and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
						
						//se borra la tabla antes de realizar la consulta
						showTables(query,func);          
					}  
				}
			}
		});
		$('#tblPartidas tr').live('dblclick', function() {
			if(validaModificarProcedimiento()){
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else{
					$(this).addClass('row_selected'); 
			   		/// se inserta cucop seleccionado
	            	var aTrs = $('#tblPartidas').dataTable().fnGetNodes();
			    	for ( var i=aTrs.length ; i>=0; i-- ){       
						if ( $(aTrs[i]).hasClass('row_selected') ){ 
							aData = oTable3.fnGetData(aTrs[i]);
							try{
		                        $("#lineaConsolidado").val(aData[0]);
		                        queryFormPost("obtieneCidProcedimiento", {async: false });
		                        queryFormPost("promedioProcedimientoPartidas", {async: false });
		                        $("#montoMinimo").val($("#totalPromedioProcedimiento").val());
								$("#montoMaximo").val($("#totalPromedioProcedimiento").val());
		                        $("#descripcionPartida").val(aData[2]);
		                        $("#descripcionPartidaAdicional").val(aData[3]);
		                        $("#nCantidad").val(aData[4]);
		                        $("#nCantidadMax").val(aData[4]);
		                        $("#mMontoNetoLinea").val(aData[6]);
		                        $("#mMontoNetoLineaMax").val(aData[6]);
		                        $("#nPorcIVA").val(aData[5]);
							  	queryFormPost("spAgregaLineaPartida", {async: false });
							    oTable3.dataTable().fnDeleteRow( i );
								queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
								//llena montos bruto y neto
								queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
								queryFormPost("llenaMontoNetoCotizaciones",{async:false});
					            // llena iva y tipo de cambio
					            //queryFormPost("llenaIvaCotizaciones",{async:false}); 
						        queryFormPost("tipodeCambioProcedimiento",{async:false});
			                    if($("#idTipoCambio").val()!="01"){
			                       $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			                       $("#valorTipoCambio").css("visibility","visible");
			                       $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
			                    }else{
			                       $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			                       $("#valorTipoCambio").css("visibility","hidden");
			                       $("#valorTipoCambio").val($("#valorTipoCambio1").val());
			                    }
			                  	$("#valorTipoCambio").formatCurrency();
							  	$("#montoBrutoCotizacion").formatCurrency();
							  	$("#montoNetoCotizacion").formatCurrency();
						            
								var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
								var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
								query += "   and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
						        //se borra la tabla antes de realizar la consulta
						        $("#IContratoAbierto").val("");
						        queryFormPost("cContratoAbiertoRead",{async:false});	
						        
						        habilitarPestanas();				        
								showTables(query,func);
							    guardaBitacora("SELECCIONA_PARTIDA", $("#cIdProcedimiento").val());
				               //se actualiza tabla de cucops
				         
							}catch(e){
								swal("No se pudo agregar la partida...",{icon:"info",button: "Cerrar"});
							}
						}							    
					} 			
	        	}
     		}
		});
	//botones de guardado y partidas de la seccion de arrendamientos
	
	$( "#btnGuardarCotizacionArrendamientos" ).button().click(function(){
		$("#btnGuardarCotizacionProc").click();
	
	});
	
	$( "#btnPartidaCotizacionArrendamientos" ).button().click(function(){
		$("#btnPartidaCotizacionProc").click();
	});
			
	$("#btnGuardarCotizacionProc").button().click(function(){
		var totalRMFArrendamientos=0;
		var totalRMVArrendamientos=0;
		var totalRMFMantenimiento=0;
		var totalRMVMantenimiento=0;
		var totalInmuebles=0;
		var umbral=0;
		var cadena_campos=$("#cIdConsolidado").val().split('-');
		$("#cIdUEConsolidado").val(cadena_campos[1]);
		if(validaModificarProcedimiento()){
			if(parseFloat( $("#porcentajeIVACotizacion").val()) > 100  || $("#porcentajeIVACotizacion").val()< 0 ){
				swal("El porcentaje de IVA no puede ser mayor a 100 ni puede ser negativo",{icon:"info",button: "Cerrar"});
				return;
			}
			if(parseFloat( $("#valorTipoCambio").val()) < 0 ){
				swal("El tipo de cambio no puede ser negativo",{icon:"info",button: "Cerrar"});
				return;
			}	   
			//checa si son contratos de arrendamiento y solicita que los campos esten llenos
			if(($("#cIdTipoProcedimiento").val()== "PA") || ($("#cIdTipoProcedimiento").val()== "PL")){
				//variables arrendamiento								
				var rmfArrendaniento=$('#rmfArrendaniento');
				var mesesArrendaniento=$('#mesesArrendaniento');
				var rvArrendamiento=$('#rvArrendamiento');
				var semanasArrendamiento=$('#semanasArrendamiento');
				//variables mantenimiento								
				var rmfMantenimiento = $('#rmfMantenimiento');
				var mesesMantenimiento=$('#mesesMantenimiento');
				var rvMantenimiento = $('#rvMantenimiento');
				var semanasMantenimiento=$('#semanasMantenimiento');
				allFields = $( [] ).add(rmfArrendaniento) .add(mesesArrendaniento).add(rvArrendamiento).add(semanasArrendamiento).add(rmfMantenimiento).add(mesesMantenimiento).add(rvMantenimiento).add(semanasMantenimiento),
				tips = $( ".validateTips" );
				var bValid = true;
				tips.text("");
				allFields.removeClass( "ui-state-error" );

				bValid = bValid&& checkRequerido(rmfArrendaniento, "Renta Mensual Fija Arrendamiento");
				bValid = bValid&& checkRequerido(mesesArrendaniento, "Renta Mensual Fija Meses Arrendamiento");
				//se verifica si esta activado el checkbox de la seccion de arrendamiento para validar los campos
				if(document.getElementById("chk_rvArrendamiento").checked){
					bValid = bValid&& checkRequerido(rvArrendamiento, "Renta Variable Arrendamiento");
					bValid = bValid&& checkRequerido(semanasArrendamiento, "Renta Variable Semanas de Arrendamiento");
				}											
				bValid = bValid&& checkRequerido(rmfMantenimiento, "Renta Mensual Fija Mantenimiento");
				bValid = bValid&& checkRequerido(mesesMantenimiento, "Renta Mensual Fija Meses Mantenimiento");
				//se verifica si esta activado el checkbox de la seccion de mantenimiento para validar los campos
				if(document.getElementById("chk_rvMantenimiento").checked){
					bValid = bValid&& checkRequerido(rvMantenimiento, "Renta Variable  Mantenimiento");
					bValid = bValid&& checkRequerido(semanasMantenimiento, "Renta Variable Semanas Mantenimiento");
				} 
		      	//si existe un campo vacio se regresa
				if(!bValid)
					return;
				       				    
			}
			if(	$("#cboCambioCotizacion").val()!=1){
				if($("#valorTipoCambio").val()==""){
					swal("Ingrese el valor del tipo de cambio",{icon:"info",button: "Cerrar"});
					return;
				}
				$("#valorTipoCambio").val();
			}else{
				$("#valorTipoCambio").val(1);
			}		
			$("#porcentajeIVACotizacion").val();
			$("#rfcCotizacion").val();
			//Mando llamar el procedimiento que  inserta los campos en la tabla mProcedimientoAdjudicacion
			queryFormPost("sp_AdjudicacionCreate1",{async : false});
			//refresca valores de lineas
			var aTrs=ObtieneDatosTabla();
			var indice=0;
			var cantReal=0;
			var nlineaCons=0;
			var onlyOne=true;
			
			for(var i=0;i<aTrs.length;i++){
				aData = oTable2.fnGetData(aTrs[i]);
				if($("#isPlurianual").val()==1){
					if(document.getElementById("chk_pedidoAbierto").checked){
						indice=15;
						nlineaCons=aData[indice];
						cantReal=aData[16];
					}else{//
						indice=12;
						nlineaCons=aData[indice];
						cantReal=aData[13];
					}
				}else{
					if(document.getElementById("chk_pedidoAbierto").checked){
						indice=14;
						nlineaCons=aData[indice];
						cantReal=aData[15];
					}else{//
						indice=11;
						nlineaCons=aData[indice];
						cantReal=aData[12];
					}
				}
				asignaValorHidens(nlineaCons,cantReal);
					
				var montoNetoLinea=quitaFmt($("#mMontoNetoLinea").val());
				var montoNetoLineaComp=quitaFmt($("#mMontoNetoLineaMinimo").val());
				var montoNetoLineaMax=quitaFmt($("#mMontoNetoLineaMax").val());
				$("#mMontoNetoLinea").val(montoNetoLinea);
				$("#mMontoNetoLineaMinimo").val(montoNetoLineaComp);
				$("#mMontoNetoLineaMax").val(montoNetoLineaMax);
				$("#nIdLineaCons").val($("#lineaConsolidado").val());
				if($("#nCantidad").val()<=0 //|| $("#nCantidadMax").val()<=0
				){
					swal("No puede ingresar cantidades menores o iguales a cero en la partida " + (parseInt(aData[indice],10)+1),{icon:"info",button: "Cerrar"});
					return ;
				}
				if(parseFloat($("#mMontoNetoLineaMinimo").val())<=0){
					swal("El monto m\u00ednimo por linea no puede ser 0.00",{icon:"info",button: "Cerrar"});
					return ;
				}
				var minimo=quitaFmt($("#montoMinimo").val());
				var maximo=quitaFmt($("#montoMaximo").val());
				if(onlyOne && validaMontoPlurianual(aTrs,indice)){
					onlyOne=false;
					return;
				}
				onlyOne=false;
				if(document.getElementById("chk_pedidoAbierto").checked){
					if(parseFloat(minimo)> 0){
						if(
							(($("#cIdTipoProcedimiento").val()=='PC' || $("#cIdTipoProcedimiento").val()=='PR' ) && $("#esCucopGasolina").val()=='')
								||($("#nServicio_A_Bienes").val()==1) || ($("#cIdTipoProcedimiento").val()=='PT') 
						){
							if(parseInt($("#nCantidad").val(),10) == parseInt($("#nCantidadMax").val(),10)){
                       			swal("La cantidad m\xE1xima debe ser mayor que la cantidad m\xEDnima en la linea "+$("#nIdLineaCons").val(),{icon:"info",button: "Cerrar"});
								return;
							}
                     		if(parseInt($("#nCantidad").val(),10) < parseInt($("#nCantidadMax").val(),10)){
								modificaCantRequi();
		   						queryFormPost("spAgregaLineaPartida",{async:false});
		   						arrendamineto();
							}else{
								swal("La cantidad m\xEDnima debe ser menor que la cantidad m\xE1xima en la linea "+$("#nIdLineaCons").val(),{icon:"info",button: "Cerrar"});
								return;
                    		}
   						}else{
	                 		if(parseFloat(minimo) == parseFloat(maximo)){
								swal("El monto m\xE1ximo debe ser mayor que el monto m\xEDnimo en la linea "+$("#nIdLineaCons").val(),{icon:"info",button: "Cerrar"});
								return;
	                     	}
	                     	if(parseFloat(minimo) < parseFloat(maximo)){
								modificaCantRequi();
	   							queryFormPost("spAgregaLineaPartida",{async:false});
	   							arrendamineto();
							}else{
								swal("El monto m\xEDnimo debe ser menor que el monto m\xE1ximo en la linea "+$("#nIdLineaCons").val(),{icon:"info",button: "Cerrar"});
								return;
							}
						}
					}else{
						swal("El monto m\xEDnimo debe ser mayor a 0 en la linea "+$("#nIdLineaCons").val(),{icon:"info",button: "Cerrar"});
						return;
					}
				}else{
					if(parseFloat(minimo)> 0){
						modificaCantRequi();
						queryFormPost("spAgregaLineaPartida", {async: false }); 
						arrendamineto();
					}else{
						swal("El Precio Unitario debe ser mayor a 0.",{icon:"info",button: "Cerrar"});
						return;
					}
				}
			}//Termina el for	
			//llenamos rfc y razon social
			queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
			//llena montos bruto y neto
			queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
			// llena iva y tipo de cambio
			//queryFormPost("llenaIvaCotizaciones",{async:false});
			queryFormPost("tipodeCambioProcedimiento",{async:false});
			if($("#idTipoCambio").val()!="01"){
				$("#cboCambioCotizacion").val($("#idTipoCambio").val());
				$("#valorTipoCambio").css("visibility","visible");
				$("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
			}else{
				$("#cboCambioCotizacion").val($("#idTipoCambio").val()); 
				$("#valorTipoCambio").css("visibility","hidden");
				$("#valorTipoCambio").val($("#valorTipoCambio1").val());
			}
			$("#valorTipoCambio").formatCurrency();
			$("#montoBrutoCotizacion").formatCurrency();
			$("#montoNetoCotizacion").formatCurrency();
			var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
			var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
			query += "   and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
			//valida si es plurianual
			if($("#isPlurianual").val()==1){
				var montoTotalPluriSinFormato=quitaFmt($("#montoTotalPluri").val());
				var montoNetoSinFormato=quitaFmt($("#montoNetoCotizacion").val());
				if(parseFloat(montoTotalPluriSinFormato)<=parseFloat(montoNetoSinFormato)){
					swal("El monto total Plurianual " +$("#montoTotalPluri").val() +" debe de ser mayor al monto neto "+$("#montoNetoCotizacion").val(),{icon:"info",button: "Cerrar"});
					return;
				}
				queryFormPost("sp_AdjudicacionCreate1",{async : false});
			} 
			//se borra la tabla antes de realizar la consulta
			showTables(query,func);
			habilitarPestanas();
			$("#YaFueGuardado").val(1);
			guardaBitacora("GUARDA_PARTIDAS", $("#cIdProcedimiento").val());
//			swal("Datos Guardados.\nRevisa que tus montos netos sean los correctos en cada linea.\n Puedes modificar centavos en el importe neto.",{icon:"info",button: "Cerrar"});
			validaMontoApartadoMontoMaximo();
		}	
	});
	function validaMontoPlurianual(aTrs,indice){
		var montoTotalPlu=quitaFmt($("#montoTotalPluri").val());
		var montosumaPlu=0;
		var montoCapturaPlu=0;
		var resp=false;
		
		if($("#isPlurianual").val()==1){
			for(var i=0;i<aTrs.length;i++){
				aData = oTable2.fnGetData(aTrs[i]);
				if(document.getElementById("chk_pedidoAbierto").checked){
					montoCapturaPlu=$("#mMontoNetoPluriPartAbi_"+aData[indice]+"").val();
				}else{
					montoCapturaPlu=$("#mMontoNetoPluriPart_"+aData[indice]+"").val();
				}
				if(montoCapturaPlu==""){
					montoCapturaPlu="$0.0";
				}
				montoCapturaPlu=quitaFmt( montoCapturaPlu );
				if(parseFloat(montoCapturaPlu)<=0){
					swal("El monto pluri-anual por linea no puede ser menor o igual a 0.00 en la linea "+$("#nIdLineaConsolidado_"+aData[indice]+"").val(),{icon:"info",button: "Cerrar"});
					return true;
				}
				montosumaPlu=montosumaPlu+parseFloat(montoCapturaPlu);
			}
			montosumaPlu=montosumaPlu.toFixed(2);
			montosumaPlu=parseFloat(montosumaPlu);
			if(montoTotalPlu!=montosumaPlu){
				swal("El monto total plurianual es="+montoTotalPlu +" y la suma pluri-anual por linea es= "+montosumaPlu+"; los montos deben de ser iguales.",{icon:"info",button: "Cerrar"});
				return true;
			}
		}
		
		return false;
	}
	function asignaValorHidens(indice,cantReal){
		$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+indice+"").val());
		$("#descripcionPartida").val($("#cDescripcion_"+indice+"").val());
		
		$("#nCantidadReal").val(cantReal);
		$("#nCantidad").val($("#nCantidad_"+indice+"").val());
		if(document.getElementById("chk_pedidoAbierto").checked){
			$("#nPorcIVA").val($("#nporcIVA_"+indice+"").val());
			$("#montoMinimo").val($("#mMontoMinimo_"+indice+"").val());
			$("#descripcionPartidaAdicional").val($("#cDescripcionAuxDT2_"+indice+"").val());
			$("#mMontoNetoLinea").val($("#mMontoMaximoNetoA_"+indice+"").val());
			$("#mMontoNetoLineaMinimo").val($("#mMontoNetoMinimoOrigA_"+indice+"").val());
			$("#mMontoNetoLineaMax").val($("#mMontoMaximoNetoM_"+indice+"").val());
			if(
					(($("#cIdTipoProcedimiento").val()=='PC' || $("#cIdTipoProcedimiento").val()=='PR') && $("#esCucopGasolina").val()=='')
					||($("#nServicio_A_Bienes").val()==1) 
					|| ($("#cIdTipoProcedimiento").val()=='PT') 
			){
				$("#nCantidadMax").val($("#nCantidadMax_"+indice+"").val());
				$("#montoMaximo").val($("#mMontoMinimo_"+indice+"").val());
			}else{
				$("#nCantidadMax").val($("#nCantidad_"+indice+"").val());
				$("#montoMaximo").val($("#mMontoMaximo_"+indice+"").val());
			}
		}else{
			$("#montoMinimo").val($("#mMontoMaximoUnitario_"+indice+"").val());
			$("#montoMaximo").val($("#mMontoMaximoUnitario_"+indice+"").val());
			$("#descripcionPartidaAdicional").val($("#cDescripcionAuxDT1_"+indice+"").val());
			$("#nCantidadMax").val($("#nCantidad_"+indice+"").val());
			$("#mMontoNetoLinea").val($("#mMontoMaximoNeto_"+indice+"").val());
			$("#mMontoNetoLineaMinimo").val(parseFloat(quitaFmt($("#mMontoMaximoUnitario_"+indice+"").val()))*parseInt($("#nCantidad_"+indice+"").val(),10)*(1+(0.01*parseInt($("#nporcIVA"+indice+"").val(),10))));
			$("#mMontoNetoLineaMax").val($("#mMontoMaximoNeto_"+indice+"").val());
			$("#nPorcIVA").val($("#nporcIVA"+indice+"").val());
		}
		$("#mMontoNetoLineaPlu").val(0);
		if($("#isPlurianual").val()==1){
	    	if(document.getElementById("chk_pedidoAbierto").checked){
				$("#mMontoNetoLineaPlu").val($("#mMontoNetoPluriPartAbi_"+indice+"").val());
			}else{
				$("#mMontoNetoLineaPlu").val($("#mMontoNetoPluriPart_"+indice+"").val());
			}
		}
	}
	function ObtieneDatosTabla(){
		var aTrs;
		$("#IContratoAbiertoProveedor").val('FALSE');
	    if($("#isPlurianual").val()==1){
	    	if(document.getElementById("chk_pedidoAbierto").checked){
	    		$("#IContratoAbiertoProveedor").val('TRUE');
		    	if(
		    			(($("#cIdTipoProcedimiento").val()=='PC' || $("#cIdTipoProcedimiento").val()=='PR' ) && $("#esCucopGasolina").val()=='') 
		    			||($("#nServicio_A_Bienes").val()==1) || ($("#cIdTipoProcedimiento").val()=='PT') 
		    	){
		    		aTrs = $('#tblProovedoresCotizacion2BienesPlu').dataTable().fnGetNodes();
		    	}else{
		    		aTrs = $('#tblProovedoresCotizacion2Plu').dataTable().fnGetNodes();
		    	}
			}
		    else{
		    	aTrs = $('#tblProovedoresCotizacionPlu').dataTable().fnGetNodes();
			}
	    }else{ 
			if(document.getElementById("chk_pedidoAbierto").checked){
				$("#IContratoAbiertoProveedor").val('TRUE');
				if((($("#cIdTipoProcedimiento").val()=='PC' || $("#cIdTipoProcedimiento").val()=='PR') && $("#esCucopGasolina").val()=='')||($("#nServicio_A_Bienes").val()==1) 
						|| ($("#cIdTipoProcedimiento").val()=='PT') ){
		   			aTrs = $('#tblProovedoresCotizacion2Bienes').dataTable().fnGetNodes();
			   	}else{
			   		aTrs = $('#tblProovedoresCotizacion2').dataTable().fnGetNodes();
			   	}
			}else{
		   		aTrs = $('#tblProovedoresCotizacion').dataTable().fnGetNodes();
			}
		}
		return aTrs;
	}
	function arrendamineto(){
		//checa si son contratos de arrendamiento y solicita que los campos esten llenos
		if(($("#cIdTipoProcedimiento").val()== "PA") || ($("#cIdTipoProcedimiento").val()== "PL")){
	   		//se checa monto total de las partidas
   			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
			var totalRMFArrendamientos=(parseFloat(quitaFmt($("#rmfArrendaniento").val()))* parseFloat(quitaFmt($("#mesesArrendaniento").val())));
			if(document.getElementById("chk_rvArrendamiento").checked){
				var totalRMVArrendamientos=(parseFloat( quitaFmt($("#rvArrendamiento").val()))* parseFloat(quitaFmt($("#semanasArrendamiento").val())));
			}
   			var totalRMFMantenimiento=(parseFloat(quitaFmt($("#rmfMantenimiento").val()))* parseFloat(quitaFmt($("#mesesMantenimiento").val())));
   			if(document.getElementById("chk_rvMantenimiento").checked){
				var totalRMVMantenimiento=(parseFloat(quitaFmt($("#rvMantenimiento").val()))* parseFloat(quitaFmt($("#semanasMantenimiento").val())));
			}
			totalInmuebles=	totalRMFArrendamientos+ totalRMVArrendamientos+totalRMFMantenimiento+totalRMVMantenimiento;
			totalInmuebles=totalInmuebles.toFixed(2);
  			umbral = Math.abs(quitaFmt($("#montoNetoCotizacion").val())-totalInmuebles );
			if(umbral > .05){
				swal("El monto Total de las Partidas del Procedimiento no es igual al Monto Total de la seccion de Arrendamientos",{icon:"info",button: "Cerrar"});
				return;
			}
			//inserta o actualiza datos en la tabla de mArrendamientoServicios y mMantenimientoServicios
			queryFormPost("sp_mArrendamientoServicios", {async: false }); 
			queryFormPost("sp_mMantenimientoServicios", {async: false }); 
		}
	}	
	$("#btnPartidaCotizacionProc").button().click(function() {
			if(validaModificarProcedimiento()){
			$("#tblPartidasCotizacion").css("display","");
			var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'";
			var where="	nIdLineaConsolidado NOT IN( SELECT nIdLineaConsolidado FROM mProcedimientoAdjudicacionPartidas WHERE cEjercicio ='"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento ='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora ='"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo = '"+$("#nIdConsecutivo").val()+"' AND cIdTipoConsolidado ='"+$("#TipoConsolidado").val()+"' AND nIdConsecutivoConsolidado = '"+$("#ConsecutivoConsolidado").val()+"')";
			
			oTable3.dataTable().fnClearTable();
		    $("#tblPartidasCotizacion").css("visibility","visible");
		    
			//Mostramos la tabla de proveedores agregados
			$('#tblPartidas').dataTable({    
			"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
			"bFilter" : false,
			"bDestroy" : true,
			"bJQueryUI": true,
			"bAutoWidth" : false,
			sScrollY : "100%",
			sScrollX: "200%",
			sScrollXInner: "100%",
			"sPaginationType": "full_numbers",
			//"sScrollY": 250,
					"oLanguage": {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					//sLengthMenu: "<h2><b>PROVEEDORES AGREGADOS</b></h2><h5>Doble click sobre el proveedor a cotizar</h5>",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidas("+func+")&qw="+where,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaConsolidado" },
					{ sName: "cidCABM"   },
					{ sName: "cDescripcion"   },
					{ sName: "cDescripcionAdicional"   },
					{ sName: "nCantidad"   },
					{ sName: "consolidadoIVA" ,bVisible: false },//,bVisible: false
					{ sName: "mMontoNetoLineaSol"  ,bVisible: false }
				]
			});
			}
		});	
		

		$( "#btnBorraPartidas").button().click(function() {
			if(validaModificarProcedimiento()){
				if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO")
	        		return;	
				swal({
					title: "Est\u00e1 seguro que desea eliminar las l\u00edneas? Una vez confirmado, no podr\u00e1 deshacer los cambios?",
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
						var aTrs=ObtieneDatosTabla();
						for(var i=0;i<aTrs.length;i++){
							aData = oTable2.fnGetData(aTrs[i]);
							if($("#isPlurianual").val()==1){
								if(document.getElementById("chk_pedidoAbierto").checked){
									indice=15;
									nlineaCons=aData[indice];
								}else{//
									indice=12;
									nlineaCons=aData[indice];
								}
							}else{
								if(document.getElementById("chk_pedidoAbierto").checked){
									indice=14;
									nlineaCons=aData[indice];
								}else{//
									indice=11;
									nlineaCons=aData[indice];
								}
							}
							$("#lineaConsolidado").val($("#nIdLineaConsolidado_"+nlineaCons+"").val());
							
					 		queryFormPost("deletePartidasProcedimiento", {async:false});
						}
						queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
						//llena montos bruto y neto
						queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
						queryFormPost("llenaMontoNetoCotizaciones",{async:false});
						// llena iva y tipo de cambio
						//queryFormPost("llenaIvaCotizaciones",{async:false}); 
						queryFormPost("tipodeCambioProcedimiento",{async:false});
		                 if($("#idTipoCambio").val()!="01"){
			                 $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			                 $("#valorTipoCambio").css("visibility","visible");
			                 $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
		                 }else{
		                     $("#cboCambioCotizacion").val($("#idTipoCambio").val()); 
		                     $("#valorTipoCambio").css("visibility","hidden");
		                     $("#valorTipoCambio").val($("#valorTipoCambio1").val());
		                 }
		                $("#valorTipoCambio").formatCurrency();
			               
						$("#montoBrutoCotizacion").formatCurrency();
						$("#montoNetoCotizacion").formatCurrency();
									
						var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
						var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
						 query += "   and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
						             //se borra la tabla antes de realizar la consulta
						showTables(query,func);
						guardaBitacora("BORRA_PARTIDAS", $("#cIdProcedimiento").val());			
		                $("#btnPartidaCotizacionProc").click();
					}
				});
			}
		});	

		$("#btnSeleccionaPartidas").button().click(function(){
			var aTrs = oTable3.dataTable().fnGetNodes();
			for ( var i=0 ; i<aTrs.length; i++ ){
				aData = oTable3.fnGetData(aTrs[i]);
				try{
	              $("#lineaConsolidado").val(aData[0]);
	              queryFormPost("obtieneCidProcedimiento", {async: false });
			   	  queryFormPost("promedioProcedimientoPartidas", {async: false });
	              $("#montoMinimo").val($("#totalPromedioProcedimiento").val());
			      $("#montoMaximo").val($("#totalPromedioProcedimiento").val());
			      $("#descripcionPartida").val(aData[2]);
			      $("#descripcionPartidaAdicional").val(aData[3]);
			      $("#nCantidadMax").val(aData[4]);
			      $("#mMontoNetoLinea").val(aData[6]);
			      $("#mMontoNetoLineaMax").val(aData[6]);
			      $("#nPorcIVA").val(aData[5]);
	               //procedimiento almacenado para agregar cucops al programa anual
			      queryFormPost("spAgregaLineaPartida", {async: false });
			   	  //lenamos rfc y razon social	 
				  queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
				  //llena montos bruto y neto
				  queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
				  queryFormPost("llenaMontoNetoCotizaciones",{async:false});
	              // llena iva y tipo de cambio
		          //queryFormPost("llenaIvaCotizaciones",{async:false}); 
		          queryFormPost("tipodeCambioProcedimiento",{async:false});
	              if($("#idTipoCambio").val()!="01"){
	                  $("#cboCambioCotizacion").val($("#idTipoCambio").val());
	                  $("#valorTipoCambio").css("visibility","visible");
	                  $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
	              }else{
				      $("#cboCambioCotizacion").val($("#idTipoCambio").val());
	                  $("#valorTipoCambio").css("visibility","hidden");
	                  $("#valorTipoCambio").val($("#valorTipoCambio1").val());
	              }
				  $("#valorTipoCambio").formatCurrency();
			      $("#montoBrutoCotizacion").formatCurrency();
				  $("#montoNetoCotizacion").formatCurrency();
				  guardaBitacora("SELECCIONA_PARTIDAS", $("#cIdProcedimiento").val());
				} catch(e){
			 		swal("No se pudo agregar la Partida...",{icon:"info",button: "Cerrar"});
				} 
			}							             
			oTable3.dataTable().fnClearTable(); 
			var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
			var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
			query += "   and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
			 //se borra la tabla antes de realizar la consulta
			habilitarPestanas();
			showTables(query,func);
			
			// $("#tblProovedoresCotizacion").css("visibility","visible");
		    //se actualiza tabla de cucops
		});	
		muestraOcultaBotones();	
	}); // fin de carga de documento
	
	function muestraInformacion (){
	   queryFormPost("llenaCaratulaProcedimiento",{async:false});
	   queryFormPost("llenaUnidadEjecutoraProcedimiento",{async:false});
	   queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	   queryFormPost("esActivoProcedimiento",{async:false});
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
	   queryFormPost("mSolicitudDescripcionUnidad", { async:false });	
	}
	
	function validaBotones(boton){
		switch (boton){
			case "imgAdjudicarProc":
				adjudicarProcedimiento();
			break;
			case "imgDesiertoProc":
				desiertoProcedimiento();
			break;
			case "imgDevolverProc":
				devolverProcedimiento();
			break;			
		}/// FIN SWITCH
	} 
		
	function adjudicarProcedimiento(){
		if(validaModificarProcedimiento()){
			if(parseInt($("#nIdEstado").val(),10)>1){
				swal("No se puede modificar por su estatus.",{icon:"info",button: "Cerrar"});
				return;
			}
				
			if($("#YaFueGuardado").val()==0 ){
				swal("Para adjudicar primero hay que guardar.",{icon:"info",button: "Cerrar"});
				return;
			}
			queryFormPost("mSaldosDelete", {async:false});
			queryFormPost("mProcedimientoCumpleRequisitosRead",{async:false});
			if($("#cProcedimientoCumple").val() == false){
				swal("El procedimiento no cumple los requisitos.",{icon:"info",button: "Cerrar"});
				return;
			}
	
			queryFormPost("checaProveedoresAsignados",{async:false});
			queryFormPost("mPartidasProcedDisponibles",{async:false});
			if($("#tieneProveedor").val()=="0"){
				swal("El procedimiento no tiene ningun proveedor asociado",{icon:"info",button: "Cerrar"});
				return;
			}
			queryFormPost("readProcedimientoCNET", {async:false});
			
			//valida el procedimiento de invitacion a tres
			queryFormPost("fn_mVerificaAplicaPartidaDesiertaRead",{async:false});
			if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("tres personas") >= 0 && ($("#tipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0
			 || parseInt($("#tipoProceso").val(),10) == 0)){			
				if($("#tieneProveedor").val() < 3){
					swal({
						title: "El procedimiento solo tienes "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?",
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
							$("#EstadoCaptura").val(3);
							queryFormPost("sp_ProcedimientoDesierto",{async:false});
							actualizaDatosProcedimientoDesierto();
							return;
						}
					});
				}
				else{ 
					//valida que se hayan cargado mas de 3 cotizaciones				
					if($("#cPartidaDesierta").val() != ""){
						if($("#cPartidaDesierta").val().toUpperCase() == "SI"){
							swal({
								title: "Existen partidas que tienen menos de 3 cotizaciones si contin\xFAa se cambiaran a desiertas autom\xE1ticamente.\n\xBFDesea continuar?",
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
									queryFormPost("sp_mAplicaPartidaDesierta",{async:false});
									return;
								}
							});
						}
					}
				}
			}
	
			if($("#cCategoriaDescripcion").val().toLowerCase().indexOf("adjudicaci") >= 0 && $("#cCategoriaDescripcion").val().toLowerCase().indexOf("n directa") >= 0 && ($("#tipoProceso").val().toUpperCase().indexOf("COMPLETO") >= 0 || parseInt($("#tipoProceso").val(),10) == 0)){
				if($("#tieneProveedor").val() < 3){
					swal({
						title: "El procedimiento solo tiene "+$("#tieneProveedor").val()+" provedor(s) si contin\xFAa se cambiar\xE1 el estatus a desierto autom\xE1ticamente.\n\xBFDesea continuar?",
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
							$("#EstadoCaptura").val(3);
							queryFormPost("sp_ProcedimientoDesierto",{async:false});
							actualizaDatosProcedimientoDesierto();
							return;
						}
					});
				}
			}

			$.ajax({url: '../../servlet/ProcedimientoServlet?cEjercicio='+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
			 type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
				function(j){
					mensajeAp=j[0].Contable1;
					swal(mensajeAp,{icon:"info",button: "Cerrar"});
					swal({
						title: "",
						text: mensajeAp,
						icon: "info",
						buttons: {
							confirm : "Cerrar"
							},
						}).then((continuar) => {
							window.location = "Procedimiento-copia.jsp?tab=4";
							$("#cAccion").val("ADJUDICA_PROCEDIMIENTO");
							$("#cIdDocumento").val($("#cIdProcedimiento").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					});
						
				}
			});
			}
  	}  
  	
   function desiertoProcedimiento(){
		if(validaModificarProcedimiento()){
			if(parseInt($("#nIdEstado").val(),10)>1)
			return;	
			
			swal({
				title: "¿Desea declarar desierto el Procedimiento " + $("#cIdProcedimiento").val(),
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
					$("#EstadoCaptura").val('3'); 
					queryFormPost("sp_ProcedimientoDesierto",{async:false});
					muestraOcultaBotones();
					actualizaDatosProcedimientoDesierto();
				}
			});
		}
	 }		
  
	function devolverProcedimiento(){
		if(validaModificarProcedimiento()){
			if(parseInt($("#nIdEstado").val(),10)!=2){
				swal("No se puede devolver el procedimiento, porque su estatus no lo permite",{icon:"info",button: "Cerrar"});
				return;
			}
		   $.ajax({url: '../../servlet/ProcedimientoServlet?', type:'post' , async: false, 
           data:"operacion=3&cEjercicio="+$("#cEjercicio").val()+"&cIdTipoProcedimiento="+$("#cIdTipoProcedimiento").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdConsecutivo="+$("#nIdConsecutivo").val()+"&tipoProceso="+$("#tipoProceso").val(),
            dataType: 'json', success: 
            function(j){
            		mensajeAp=j[0].Contable1;
					swal(mensajeAp,{icon:"info",button: "Cerrar"});
					$("#cAccion").val("DEVOLVER_PROCEDIMIENTO");
					$("#cIdDocumento").val($("#cIdProcedimiento").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					window.location = "Procedimiento-copia.jsp?tab=4";	
					
			}
	   		});
		}
	}
	
	function validaModificarProcedimiento(){
		queryFormPost("mUsuarioMismaUE", {async: false   });
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( roles.indexOf('ADMIN_RECMAT') >= 0)
		|| ( roles.indexOf('ANALISTA') >= 0)||( roles.indexOf('JEFE') >= 0)||parseInt($("#usuariosMismaUE").val(),10)==1){
			return true;
		}
		else{
			swal("El usuario no tiene permiso para realizar esta acci\xF3n.",{icon:"info",button: "Cerrar"});
			return false;
		}
	}
		
	function actualizaDatosProcedimientoDevuelto(){
		$("#nIdEstadoProcedimiento").val("CAPTURADO");
	    queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
	 
 	}
 	
	
	function agregaTablaProveedoresCotizaciones(){
		var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
	   
		oTableProvedoresCotizaciones=$('#tblProvedoresCotizaciones').dataTable().fnClearTable();
	    
	         szTabla = "PROVEEDORESCOTIZACION";                                                                                         
				$.getJSON("../../catalogos/SelectJson.jsp?"+new Date(),{Tabla: szTabla, Param: query, MaxReg:"" , ajax: 'false'}, 
					function(j)
					{     
					nEditaRegistrosProveedor=j.length;
						arrayCompleto=new Array();
						var l=0;
						for (var i = 0; i < j.length; i++) 
    					{
    					   if(j[i].Col0!=$("#nIdRFCProveedor_"+i+"").val()){
    				       
	    					arrayCompleto [l]=[
							  "<input type='text' id='nIdRFCProveedor_"+i+"' name='nIdRFCProveedor_"+i+"' value='"+ j[i].Col0 +"' readonly style='width:120px; border-width:0; background-color:transparent'/>", 
							  "<input type='text' id='cIdRazonSocialProveedor_"+i+"' name='cIdRazonSocialProveedor_"+i+"' value='" + j[i].Col1 + "' readonly style='width:350px; border-width:0; background-color:transparent'/>",
							  "<input type='button' name='borraProveedor_" + i +"' id='borraProveedor_" + i +"' value='Elimina' class='btnInterfaceBG ui-button ui-corner-all'  onclick='eliminaProveedor(" + i + ");'/>",
							  "<input type='text' id='nIdconsecutivoAdj_"+i+"' name='nIdconsecutivoAdj_"+i+"' value='"+ j[i].Col2 +"' readonly style='width:40px; border-width:0; background-color:transparent'/>"
							]; 
							l++;
							}	     
    					
						}
						$('#tblProvedoresCotizaciones').dataTable().fnAddData(arrayCompleto);	
		         });   
	}
	
	function actualizaDatosProcedimientoAdjudicado(){
		 $("#nIdEstadoProcedimiento").val("ADJUDICADO");
		 muestraInformacion();
				
		//lenamos rfc y razon social
			queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
			//llena montos bruto y neto
			queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
	           // llena iva y tipo de cambio
	       // queryFormPost("llenaIvaCotizaciones",{async:false});
			queryFormPost("tipodeCambioProcedimiento",{async:false});
			if($("#idTipoCambio").val()!="01"){
			    $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			    $("#valorTipoCambio").css("visibility","visible");
			    $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 

			}else{
			    $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			    $("#valorTipoCambio").css("visibility","hidden");
			    $("#valorTipoCambio").val($("#valorTipoCambio1").val());
			 }
			 $("#valorTipoCambio").formatCurrency();
			 $("#montoBrutoCotizacion").formatCurrency();
			 $("#montoNetoCotizacion").formatCurrency();
			 $("#btnGuardarCotizacionProc").css("visibility","hidden");
			 $("#btnPartidaCotizacionProc").css("visibility","hidden");
			
			$("#tblRequisitos").attr("disabled", true);
			$("#btnGuardarRequisitosProcedimiento").attr("disabled", true);
			$("#btnGuardaProcedimientoCompleto").attr("disabled", true);
			
   	}
	
	function isAbierto(query,func){
		$("#dtblProovedoresCotizacion").css("display","none");
		$("#trmMontoNetoMax").css("display","block");
		queryFormPost("esCucopDeGasolinaProc",  {async : false, 
					callback : function() 
					{
						//Bienes
						if( (($("#cIdTipoProcedimiento").val()=='PC' || $("#cIdTipoProcedimiento").val()=='PR' )&& $("#esCucopGasolina").val()=='')
							||($("#nServicio_A_Bienes").val()==1) 
							|| ($("#cIdTipoProcedimiento").val()=='PT')  
						){
					    	$("#dtblProovedoresCotizacion2Bienes").css("display","block");
							//tabla partidas el procedimientos Servicios
							oTable2= $("#tblProovedoresCotizacion2Bienes").dataTable({
								bAutoWidth : true,
								bPaginate:true,
								bDestroy:true,
								bRetrive : true,
								bServerSide:false,
								sScrollY: "200px",
								sScrollX: "200px",
								//sScrollXInner: "200%",				
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
								
								bServerSide: true,
								sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
								bProcessing: true,
							    sPaginationType: "full_numbers",
								bJQueryUI: true,
								aaSorting: [[ 14, "asc" ]] ,
								aoColumns: [
									{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
									{ sName: "cIdCABM1",bSortable:false },					
									{ sName: "cDescripcion1",bSortable:false},
									{ sName: "cDescripcionDataT2",bSortable:false},
									{ sName: "nCantidad1",bSortable:false },
									{ sName: "mMontoMinimo1",bSortable:false },
									{ sName: "ncantidad1Max",bSortable:false },
									{ sName: "mMontoMaximoBruto1",bSortable:false},//Minimo
									{ sName: "mMontoMaximoBruto2Bienes",bSortable:false},//Maximo
									{ sName: "nPorcentajeIVA2",bSortable:false},
									{ sName: "mMontoNetoA",bSortable:false },//minimo
									{ sName: "mMontoNetoMinimoOrigA",bSortable:false },//Monto Compromiso por linea
									{ sName: "mMontoNetoM",bSortable:false },//maximo
									{ sName: "boton1" ,bSortable:false},
									{ sName: "identificador", bVisible: false },
									{ sName: "catidadReal", bVisible: false }
								],
								fnInitComplete: function(oSettings, json) {
									$(".hola1").formatCurrency();
					    			$(".hola2").formatCurrency();
					                $(".hola3").formatCurrency();
								}
							
					       	});
						}else{
					    	$("#dtblProovedoresCotizacion2").css("display","block");
					    	$("#dtblProovedoresCotizacion2Bienes").css("display","none");
							//tabla partidas el procedimientos Servicios
							oTable2= $("#tblProovedoresCotizacion2").dataTable({
								bAutoWidth : true,
								bPaginate:true,
								bDestroy:true,
								bRetrive : true,
								bServerSide:false,
								sScrollY: "200px",
								sScrollX: "200px",
								//sScrollXInner: "200%",				
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
								
								bServerSide: true,
								sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
								bProcessing: true,
							    sPaginationType: "full_numbers",
								bJQueryUI: true,
								aaSorting: [[ 14, "asc" ]] ,
								aoColumns: [
									{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
									{ sName: "cIdCABM1",bSortable:false },					
									{ sName: "cDescripcion1",bSortable:false},
									{ sName: "cDescripcionDataT2",bSortable:false},
									{ sName: "nCantidad1",bSortable:false },
									{ sName: "mMontoMinimo1",bSortable:false },
									{ sName: "mMontoMaximo1",bSortable:false },
									{ sName: "mMontoMaximoBruto1",bSortable:false},//Minimo
									{ sName: "mMontoMaximoBruto2",bSortable:false},//Maximo
									{ sName: "nPorcentajeIVA2",bSortable:false},
									{ sName: "mMontoNetoA",bSortable:false },//minimo
									{ sName: "mMontoNetoMinimoOrigA",bSortable:false },//Monto Compromiso por linea
									{ sName: "mMontoNetoM",bSortable:false },//maximo
									{ sName: "boton1" ,bSortable:false},
									{ sName: "identificador", bVisible: false },
									{ sName: "catidadReal", bVisible: false }
									
								],fnInitComplete: function(oSettings, json) {
								$(".hola1").formatCurrency();
				    			$(".hola2").formatCurrency();
				                $(".hola3").formatCurrency();}
					       	});
						}
					}
		});
	}
	function showTables(query,func){
		hideDivdDataTables();
 		query=encodeURIComponent(query);
 		func=encodeURIComponent(func);
		if($("#isPlurianual").val()==1){
			showPartidasPlu(query,func);
		}else{
			muestraPartidasElegidas(query,func);
		}
	}
	function showPartidasPlu(query,func){
		  if(document.getElementById("chk_pedidoAbierto").checked){
		  	$("#trmMontoNetoMax").css("display","block");
		  	partidasContPluAbi(query,func);
		  }else{
		  	$("#trmMontoNetoMax").css("display","none");
		  	partidasContPlu(query,func);
		  }
	}
	function partidasContPlu(query,func){
		$("#dtblProovedoresCotizacionPlu").css("display","block");
	    oTable2= $("#tblProovedoresCotizacionPlu").dataTable({
			bAutoWidth : true,
			bPaginate:true,
			bDestroy:true,
			bRetrive : true,
			bServerSide:false,
			sScrollY: "200px",
			sScrollX: "200px",
			//sScrollXInner: "200%",				
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
			bServerSide: true,
			"fnServerData": function ( sSource, aoData, fnCallback ) {
					$.ajax( {
								"dataType": 'json', 
								"type": "POST", 
								"url": sSource, 
								"data": aoData, 
								"success": fnCallback
					} );
			},
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
			bProcessing: true,
		    sPaginationType: "full_numbers",
			bJQueryUI: true,
			aaSorting: [[ 12, "asc" ]] ,
			aoColumns: [
				{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
				{ sName: "cIdCABM1",bSortable:false },
				{ sName: "cDescripcion1",bSortable:false},
				{ sName: "cDescripcionDataT1",bSortable:false},
				{ sName: "nCantidad1",bSortable:false },
				{ sName: "mMontoMaximoUnitario1",bSortable:false },
				{ sName: "mMontoMaximoBruto1",bSortable:false},
				{ sName: "nPorcentajeIVA1",bSortable:false},
				{ sName: "mMontoNeto",bSortable:false },
				{ sName: "mMontoNetoMinimoOrig",bSortable:false , bVisible: false},
				{ sName: "mMontoNetoPluriPart",bSortable:false },
				{ sName: "boton1" ,bSortable:false},
				{ sName: "identificador", bVisible: false },
				{ sName: "catidadReal", bVisible: false }
			],fnInitComplete: function(oSettings, json) {
			$(".hola1").formatCurrency();
   			$(".hola2").formatCurrency();
			$(".hola3").formatCurrency();}
		});
	}
	function partidasContPluAbi(query,func){
		queryFormPost("esCucopDeGasolinaProc",  {async : false, 
			callback : function() 
			{
				if(
						(($("#cIdTipoProcedimiento").val()=='PC'|| $("#cIdTipoProcedimiento").val()=='PR')  && $("#esCucopGasolina").val()=='') 
						||($("#nServicio_A_Bienes").val()==1) || ($("#cIdTipoProcedimiento").val()=='PT') 
				){
					partidasContPluAbiBienes(query,func);
				}else{
					partidasContPluAbiServ(query,func);
				}
			}
		});
	}
	function partidasContPluAbiServ(query,func){
		$("#dtblProovedoresCotizacion2Plu").css("display","block");
		//tabla partidas el procedimientos Servicios
		oTable2= $("#tblProovedoresCotizacion2Plu").dataTable({
			bAutoWidth : true,
			bPaginate:true,
			bDestroy:true,
			bRetrive : true,
			bServerSide:false,
			sScrollY: "200px",
			sScrollX: "200px",
			//sScrollXInner: "200%",				
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
								
			bServerSide: true,
			"fnServerData": function ( sSource, aoData, fnCallback ) {
					$.ajax( {
								"dataType": 'json', 
								"type": "POST", 
								"url": sSource, 
								"data": aoData, 
								"success": fnCallback
					} );
				},
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
			bProcessing: true,
		    sPaginationType: "full_numbers",
			bJQueryUI: true,
			aaSorting: [[ 15, "asc" ]] ,
			aoColumns: [
				{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
				{ sName: "cIdCABM1",bSortable:false },					
				{ sName: "cDescripcion1",bSortable:false},
				{ sName: "cDescripcionDataT2",bSortable:false},
				{ sName: "nCantidad1",bSortable:false },
				{ sName: "mMontoMinimo1",bSortable:false },
				{ sName: "mMontoMaximo1",bSortable:false },
				{ sName: "mMontoMaximoBruto1",bSortable:false},//Minimo
				{ sName: "mMontoMaximoBruto2",bSortable:false},//Maximo
				{ sName: "nPorcentajeIVA2",bSortable:false},
				{ sName: "mMontoNetoA",bSortable:false },//minimo
				{ sName: "mMontoNetoMinimoOrigA",bSortable:false },//Monto Compromiso por linea
				{ sName: "mMontoNetoM",bSortable:false },//maximo
				{ sName: "mMontoNetoPluriPartAbi",bSortable:false },//monto pluri
				{ sName: "boton1" ,bSortable:false},
				{ sName: "identificador", bVisible: false },
				{ sName: "catidadReal", bVisible: false }
				
			],
			fnInitComplete: function(oSettings, json) {
				$(".hola1").formatCurrency();
   				$(".hola2").formatCurrency();
               $(".hola3").formatCurrency();
			}
       	});
	}
	function partidasContPluAbiBienes(query,func){
		$("#dtblProovedoresCotizacion2BienesPlu").css("display","block");
			oTable2= $("#tblProovedoresCotizacion2BienesPlu").dataTable({
				bAutoWidth : true,
				bPaginate:true,
				bDestroy:true,
				bRetrive : true,
				bServerSide:false,
				sScrollY: "200px",
				sScrollX: "200px",
				//sScrollXInner: "200%",				
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
				bServerSide: true,
				"fnServerData": function ( sSource, aoData, fnCallback ) {
								$.ajax( {
											"dataType": 'json', 
											"type": "POST", 
											"url": sSource, 
											"data": aoData, 
											"success": fnCallback
								} );
				},
 				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
				bProcessing: true,
			    sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 15, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
					{ sName: "cIdCABM1",bSortable:false },					
					{ sName: "cDescripcion1",bSortable:false},
					{ sName: "cDescripcionDataT2",bSortable:false},
					{ sName: "nCantidad1",bSortable:false },
					{ sName: "mMontoMinimo1",bSortable:false },
					{ sName: "ncantidad1Max",bSortable:false },
					{ sName: "mMontoMaximoBruto1",bSortable:false},//Minimo
					{ sName: "mMontoMaximoBruto2Bienes",bSortable:false},//Maximo
					{ sName: "nPorcentajeIVA2",bSortable:false},
					{ sName: "mMontoNetoA",bSortable:false },//minimo
					{ sName: "mMontoNetoMinimoOrigA",bSortable:false },//Monto Compromiso por linea
					{ sName: "mMontoNetoM",bSortable:false },//maximo
					{ sName: "mMontoNetoPluriPartAbi",bSortable:false },//maximo
					{ sName: "boton1" ,bSortable:false},
					{ sName: "identificador", bVisible: false },
					{ sName: "catidadReal", bVisible: false }	
																		
					
				],
				fnInitComplete: function(oSettings, json) {
					$(".hola1").formatCurrency();
	    			$(".hola2").formatCurrency();
	                $(".hola3").formatCurrency();
				}
			});
	}
	function hideDivdDataTables(){
		$("#dtblProovedoresCotizacion2").css("display","none");
		$("#dtblProovedoresCotizacion2Bienes").css("display","none");
		$("#dtblProovedoresCotizacion").css("display","none");
		$("#dtblProovedoresCotizacionPlu").css("display","none");
		$("#dtblProovedoresCotizacion2Plu").css("display","none");
		$("#dtblProovedoresCotizacion2BienesPlu").css("display","none");
	}
	function muestraPartidasElegidas(query,func){		
	    if(document.getElementById("chk_pedidoAbierto").checked){
	    	isAbierto(query,func);
	    }else{
	    	$("#dtblProovedoresCotizacion").css("display","block");
	    	$("#trmMontoNetoMax").css("display","none");
		       //tabla partidas el procedimientos seccion cotizaciones
	             oTable2= $("#tblProovedoresCotizacion").dataTable({
					bAutoWidth : true,
					bPaginate:true,
					bDestroy:true,
					bRetrive : true,
					bServerSide:false,
					sScrollY: "200px",
					sScrollX: "200px",
					//sScrollXInner: "200%",				
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
					
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProcedimientoPartidasBD (" + func + ")&qw="+ query ,
					bProcessing: true,
				    sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 11, "asc" ]] ,
					aoColumns: [
						{ sName: "nIdLineaConsolidado1"  ,bSortable:false},
						{ sName: "cIdCABM1",bSortable:false },					
						{ sName: "cDescripcion1",bSortable:false},
						{ sName: "cDescripcionDataT1",bSortable:false},
						{ sName: "nCantidad1",bSortable:false },
						{ sName: "mMontoMaximoUnitario1",bSortable:false },
						{ sName: "mMontoMaximoBruto1",bSortable:false},
						{ sName: "nPorcentajeIVA1",bSortable:false},
						{ sName: "mMontoNeto",bSortable:false },
						{ sName: "mMontoNetoMinimoOrig",bSortable:false , bVisible: false},
						{ sName: "boton1" ,bSortable:false},
						{ sName: "identificador", bVisible: false }
						,{ sName: "catidadReal", bVisible: false }													
						
					],fnInitComplete: function(oSettings, json) {
					$(".hola1").formatCurrency();
	    			$(".hola2").formatCurrency();
	                $(".hola3").formatCurrency();}
	       	});
		}
	}
	function cambiaTipoMoneda(){
	  if($("#cboCambioCotizacion").val()!=1){
			$("#valorTipoCambio").css("visibility","visible");
			if($("#cboCambioCotizacion").val()==3){
				$("#valorTipoCambio").val($("#tipoCambio").val());
			}else{
				var doc=document.getElementById("valorTipoCambio");
				doc.value="";
			}
		}else{
	
			$("#valorTipoCambio").css("visibility","hidden");
			$("#valorTipoCambio").val(1);
		}
	}
	
	 function  eliminaProveedor(indice){///proceso recortado
		 if(validaModificarProcedimiento()){
		 	if(parseInt($("#nIdEstado").val(),10)>1)
			    return;
			$("#nIdProcedimiento").val($("#cIdTipoProcedimiento").val()+"-"+$("#cIdUnidadEjecutora").val()+"-"+$("#nIdConsecutivo").val());	    
			$("#cidRFCOculto").val($("#nIdRFCProveedor_" + indice).val());
			$("#nIdconsecutivoAdj").val($("#nIdconsecutivoAdj_" + indice).val());
			$("#cIdRfcProveedorDocumento").val($("#cidRFCOculto").val());  
			//ELIMINAR REGISTROS DE mArrendamientoServicios Y mMantenimientoServicios 
			queryFormPost("deleteMantenimientoServicios", {async:false});
			queryFormPost("deleteArrendamientoServicios",{async:false});
			queryFormPost("deleteProveedorProcedimiento", {async:false});
			queryFormPost("deleteDocumentosProveedor",{async:false});
			habilitarPestanas();
			agregaTablaProveedoresCotizaciones();
			guardaBitacora("ELIMINA_PROVEEDOR",$("#cIdProcedimiento").val());
			// $("#btnBuscarProveedorProcedimiento").click();
			$("#tblProvCotizacion").css("visibility","hidden");
			$("#tblPartidasCotizacion").css("visibility","hidden");
			$("#formProveedores").css("visibility","hidden");
			$("#btnGuardarCotizacionProc").css("visibility","hidden");
			$("#btnPartidaCotizacionProc").css("visibility","hidden");
		}
	}
	function validaContratoPedidoAbierto(check){
		if(validaModificarProcedimiento()){   
			if(check.checked){
				$("#IContratoAbierto").val('TRUE');
				$("#trComprometeMaximo").show();
			}
			else{
				$("#IContratoAbierto").val('FALSE');
				$("#trComprometeMaximo").hide();
				document.getElementById("chk_comprometeMaximo").checked=false;
			}
			$("#porcentajeIVACotizacion").val();
			$("#rfcCotizacion").val();
			//Mando llamar el procedimiento que me inserta los campos en la tabla mProcedimientoAdjudicacion
			queryFormPost("sp_AdjudicacionCreate1",{async : false});	
			 //lenamos rfc y razon social
			queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
			//llena montos bruto y neto
			queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
			// llena iva y tipo de cambio
			//queryFormPost("llenaIvaCotizaciones",{async:false});
			//queryFormPost("tipodeCambioProcedimiento",{async:false});
	        //$("#valorTipoCambio").formatCurrency();
			$("#montoBrutoCotizacion").formatCurrency();
			$("#montoNetoCotizacion").formatCurrency();
			var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
			var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
			query += "  and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
			//se borra la tabla antes de realizar la consulta
			$("#YaFueGuardado").val(0);
			showTables(query,func);
		}
	}
	function eliminaPartida(indiceTabla){
		if(validaModificarProcedimiento()){
	  		if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO"){
	  			return;
	  		}
		 	$("#lineaConsolidado").val($("#nIdLineaConsolidado_" + indiceTabla).val());
		  	queryFormPost("deletePartidasProcedimiento", {async:false});
			queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
			//llena montos bruto y neto
			queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
			queryFormPost("llenaMontoNetoCotizaciones",{async:false});
		    // llena iva y tipo de cambio
		    //queryFormPost("llenaIvaCotizaciones",{async:false}); 
		    queryFormPost("tipodeCambioProcedimiento",{async:false});
			if($("#idTipoCambio").val()!="01"){
		          $("#cboCambioCotizacion").val($("#idTipoCambio").val());
		          $("#valorTipoCambio").css("visibility","visible");
		          $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 
		     }else{
		           $("#cboCambioCotizacion").val($("#idTipoCambio").val());
		           $("#valorTipoCambio").css("visibility","hidden");
		           $("#valorTipoCambio").val($("#valorTipoCambio1").val());
			 }
			 $("#valorTipoCambio").formatCurrency();
		  	 $("#montoBrutoCotizacion").formatCurrency();
		     $("#montoNetoCotizacion").formatCurrency();
		     var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
		     var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
		     query += "  and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
		     //se borra la tabla antes de realizar la consulta
		     habilitarPestanas();
		     showTables(query,func);
		     guardaBitacora("BORRA_PARTIDA", $("#cIdProcedimiento").val());
		     $("#btnPartidaCotizacionProc").click();
		}
 	}
	function borraDatos1(indice){
		var br=$("#mMontoMaximoUnitario_"+indice+"").val();
		br=br.replace("$","");
		br=br.replace(",","");
		$("#mMontoMaximoUnitario_"+indice+"").val(br);	
	}
	function validaNegativos(indice){
	   var maxUnitario= quitaFmt($("#mMontoMaximoUnitario_"+indice+"").val());
	   	if(parseFloat(maxUnitario) <= 0){
			return "1";
		}else{
		  return "2";
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
	function unFrmt (dlt){
		var val=$("#"+dlt.id).val();
		val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	$("#"+dlt.id).val(val);
	}
	function actualizaDatosProcedimientoDesierto(){
		$("#nIdEstadoProcedimiento").val("DESIERTO");
		queryFormPost("llenaImagenEstadoProcedimiento",{async:false});
	    var imagen=$("#imagenEstadoProcedimiento").val();
		//lenamos rfc y razon social
		queryFormPost("llenaRazonsocialRFCCotizaciones",{async:false});
		//llena montos bruto y neto
		queryFormPost("llenaMontoBrutoCotizaciones",{async:false});
		queryFormPost("llenaMontoNetoCotizaciones",{async:false});
        // llena iva y tipo de cambio
        //queryFormPost("llenaIvaCotizaciones",{async:false});
        queryFormPost("tipodeCambioProcedimiento",{async:false});
		if($("#idTipoCambio").val()!="01"){
			 $("#cboCambioCotizacion").val($("#idTipoCambio").val());
			 $("#valorTipoCambio").css("visibility","visible");
			 $("#valorTipoCambio").val($("#valorTipoCambio1").val()); 

		}else{
             $("#cboCambioCotizacion").val($("#idTipoCambio").val());
             $("#valorTipoCambio").css("visibility","hidden");
             $("#valorTipoCambio").val($("#valorTipoCambio1").val());
		}
		$("#valorTipoCambio").formatCurrency();
		$("#montoBrutoCotizacion").formatCurrency();
		$("#montoNetoCotizacion").formatCurrency();
		$("#btnGuardarRequisitosProcedimiento").attr("disabled", true);	
		$("#cAccion").val("DESIERTO_PROCEDIMIENTO");
		$("#cIdDocumento").val($("#cIdProcedimiento").val());
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}
  
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1){
			return false; // Valida que sea numero y punto decimal
		}			
		return true; 
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}
	function onlyNumbersInmuebles(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1){
			return false; // Valida que sea numero y punto decimal
		}
		return true;
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
	}
	function onlyIntInmuebles(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1){
			return false; // Valida que sea numero y punto decimal
		}
		return true;
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
    }
	function validaRVMantenimiento(check){
		//validamos se el usuario tiene permisos de realizar la accion
		if(validaModificarProcedimiento()){
			//se revisa si el checkbox esta activo
			if(check.checked){
				//se habilitan los texbox del rubro de mantenimiento
				$("#rvMantenimiento").css("visibility", "visible");
				$("#semanasMantenimiento").css("visibility", "visible");
			}else{
				//se deshabilitan los texbox del rubro de mantenimiento
			    $("#rvMantenimiento").css("visibility", "hidden");
		        $("#semanasMantenimiento").css("visibility", "hidden");
		        //se limpian campos de texto
		        $("#rvMantenimiento").val("");
		        $("#semanasMantenimiento").val(""); 
			}
		}
	}
	function validaRVArrendamiento(check){
		//validamos se el usuario tiene permisos de realizar la accion
		if(validaModificarProcedimiento()){
			//se revisa si el checkbox esta activo
			if(check.checked){
				$("#rvArrendamiento").css("visibility", "visible");
				$("#semanasArrendamiento").css("visibility", "visible");
			}else{
				//se deshabilitan los texbox del rubro de mantenimiento
			    $("#rvArrendamiento").css("visibility", "hidden");
		        $("#semanasArrendamiento").css("visibility", "hidden");
		        //se limpian campos de texto
		        $("#rvArrendamiento").val("");
		        $("#semanasArrendamiento").val(""); 
			}
		}
	}
	function checkRequerido(o, n) {
		var sTemp = $.trim(o.val());
		o.val(sTemp);
		if (sTemp.length == 0) {
			o.addClass("ui-state-error");
			updateTipsDlg(n + " es un dato requerido.");
			o.focus();
			return false;
		} else {
			return true;
		}
	}
	function updateTipsDlg(t) {
		tips.text(t);
		swal(t,{icon:"info",button: "Cerrar"});
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
				document.getElementById('trBotones').style.display = 'block';
				document.getElementById('trSalir').style.display = 'none';
			}else{ // trae precompromiso
				queryFormPost("validaPartidasAdjudicadas",{async:false});
				if ($("#nIdEstado").val()==1){
					if($("#documentoAplicado").val()==0){ // precompromiso cancelado
						$("#AmpliacionVigenciaProcedimiento" ).css("display", "none");
						// validar que exista un precompromiso, de lo contrario ocultar los botones
		   				document.getElementById('trBotones').style.display = 'none';
						document.getElementById('trSalir').style.display = 'block';
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
							document.getElementById('trBotones').style.display = 'block';
							document.getElementById('trSalir').style.display = 'none';	
						}else{
							if(cubreMonto() && false){//Corregir esta validación Humberto.
								$("#presupuestoProcedimiento" ).css("display", "block");
								$("#precompromisoProcedimiento" ).css("display", "block");
								document.getElementById('trBotones').style.display = 'block';
								document.getElementById('trSalir').style.display = 'none';	
							}else{
								$("#presupuestoProcedimiento" ).css("display", "none");
								$("#precompromisoProcedimiento" ).css("display", "none");
								document.getElementById('trBotones').style.display = 'block';
								document.getElementById('trSalir').style.display = 'none';	
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
			}			
		}
		
		if($("#cIdTipoProcedimiento").val()=="PN" || $("#cIdTipoProcedimiento").val()=="PS" ){
			$("#RequisitosProcedimiento" ).css("display", "block");
		}else{
			$("#RequisitosProcedimiento" ).css("display", "none");
		}
		if($("#cIdTipoProcedimiento").val()=="PS" && (roles.indexOf('ADMIN_RECMAT') >= 0 || roles.indexOf('JEFE') >= 0 || roles.indexOf('ANALISTA') >= 0) ){
			$("#trPedidoContratoBienes" ).css("display", "block");
		}else{
			$("#trPedidoContratoBienes" ).css("display", "none");
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
	 
	function cubreMonto(){
		queryFormPost("fn_mConsolidadoCalculaMontoConIVARead", {async:false});
		if(parseInt(quitaFmt($("#lblApartado").val()),10)<parseInt(quitaFmt($("#MontoConIVA").val()),10)){			
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
	 
	function frmt (dlt){
		$("#"+dlt.id).formatCurrency();
	}
	function borraDatos2(indice){
		var br=$("#mMontoMinimo_"+indice+"").val();
		br=br.replace("$","");
		br=br.replace(",","");
		$("#mMontoMinimo_"+indice+"").val(br);	
	}
	function borraDatos3(indice){
		var br=$("#mMontoMaximo_"+indice+"").val();
		br=br.replace("$","");
		br=br.replace(",","");
		$("#mMontoMaximo_"+indice+"").val(br);	
	}
	function onlyNumbers1(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		var strCheck = '0123456789.';
	
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1){
			// Valida que sea numero y punto decimal
			return false; 
		}
		
		//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		  return true;
	}
	function muestraOcultaBotones(){
		if($("#nIdEstadoProcedimiento").val()=="ADJUDICADO" || $("#nIdEstadoProcedimiento").val()=="DESIERTO"){
			
			$("#imgPlayAdjudicar").hide();
			
			$("#imgPlayDesierto").hide();
		}else{
		
			$("#imgPlayAdjudicar").show();
			
			$("#imgPlayDesierto").show();
		}
	}
	function salir(){
		window.location = "Procedimiento-copia.jsp?tab=1";
	}
	function desactivaBackspace(event){
		if(window.event && window.event.keyCode == 8)
			{
		     	window.event.keyCode = 505;
	    	}
		    if(window.event && window.event.keyCode == 505)
			{
	    	 	return false;
	    	}
	    	return true;
	}
	function modificaCantRequi(){
		if(parseInt($("#nCantidadReal").val(),10) != parseInt($("#nCantidad").val(),10)){
			////Modificar cantidades cEjercicio,cIdUEConsolidado,TipoConsolidado,ConsecutivoConsolidado,lineaConsolidado,nCantidad
			$.ajax({url: '../../servlet/ProcedimientoServlet?cEjercicio='+$("#cEjercicio").val()+"&cIdUEConsolidado="+$("#cIdUEConsolidado").val()
			+"&TipoConsolidado="+$("#TipoConsolidado").val()+"&ConsecutivoConsolidado="+$("#ConsecutivoConsolidado").val()
			+"&lineaConsolidado="+$("#lineaConsolidado").val()+"&nCantidad="+$("#nCantidad").val(),
			 type:'post' , async: false,data:'operacion=9', dataType: 'json', success: 
				function(j){
					mensajeAp=j[0].respuesta;
					cadenaMeses=j[0].cadenaMeses;
					if(parseInt(mensajeAp,10)==1){
					 	swal("No hay disponibilidad en los meses "+cadenaMeses+" del PAAS para modificar la linea "+$("#lineaConsolidado").val(),{icon:"info",button: "Cerrar"});
					 }
					 if(parseInt(mensajeAp,10)==-1){
					 	swal("Error Inesperado. Contacte a soporte técnico.",{icon:"info",button: "Cerrar"});
					 }
					 if(parseInt(mensajeAp,10)==2){
					 	swal("No se pueden guardar la cantidad 0",{icon:"info",button: "Cerrar"});
					 }
				}
			});
		}
	}
	function guardaBitacora(accion,documento){
		//Bit\u00e1cora
		$("#cAccion").val(accion);
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}
	function pedidoContServAbienes(){
		$("#nServicio_A_Bienes").val(0);
		if(document.getElementById("chk_pedidoContratoBienes").checked){
			$("#nServicio_A_Bienes").val(1);
		}
		var func="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoProcedimiento").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#nIdConsecutivo").val()+"'"; 
		var query= "  cEjercicio = '"+$("#cEjercicio").val()+"' AND cIdTipoProcedimiento='"+$("#cIdTipoProcedimiento").val()+"' AND cIdUnidadEjecutora = '"+$("#cIdUnidadEjecutora").val()+"' AND nIdConsecutivo= '"+$("#nIdConsecutivo").val()+"'";
		query += "  and nIdconsecutivoAdj ="+$("#nIdconsecutivoAdj").val()+" AND cIdRFC='"+$("#cidRFCOculto").val()+"'  AND cIdTipoConsolidado= '"+$("#TipoConsolidado").val()+"' AND  nIdConsecutivoConsolidado=  '"+$("#ConsecutivoConsolidado").val()+"'" ;  
		//se borra la tabla antes de realizar la consulta
		$("#YaFueGuardado").val(0);
		showTables(query,func);
	}
	function activaPedidoContServAbienes(){
		document.getElementById("chk_pedidoContratoBienes").checked=false;
		if($("#nServicio_A_Bienes").val()==1){
			document.getElementById("chk_pedidoContratoBienes").checked=true;
		}
	}
	function muestraObserv(){
		var cad=$("#cadenaDuplicidad").val();
		cad=cad.replace(/(?:<b>|<\/b>)/g, '');
		cad=cad.replace(/(?:<br>)/g, '\n');
		cad=cad.replace(/(?:<br \/>)/g, '\n');
		$("#cadenaDuplicidad").val(cad);
	}
	function comprometeMaximo(check){
		$("#lComprometeMaximo").val(0);
		if(check.checked){
			$("#lComprometeMaximo").val(1);	
		}
	}
	function validaMontoApartadoMontoMaximo(){
		var resp=false;
		if(document.getElementById("chk_pedidoAbierto").checked && document.getElementById("chk_comprometeMaximo").checked){
			var montoMaximo=quitaFmt($("#montoNetoCotizacionMax").val());
			var montoApartado=quitaFmt($("#lblApartado").val());
			if(parseFloat(montoMaximo)!=parseFloat(montoApartado)){
				resp=true;
			}
		}
		
		if(resp){
			swal("Datos Guardados.\nRevisa que tus montos netos sean los correctos en cada linea.\n Puedes modificar centavos en el importe neto.\n"+
					"El monto de la requisición es "+montoApartado+" y el monto máximo es "+montoMaximo
					+"\n No podras autorizar esté contrato a menos que se solvente la diferencia de presupuesto.",{icon:"info",button: "Cerrar"});
		}else{
			swal("Datos Guardados.\nRevisa que tus montos netos sean los correctos en cada linea.\n Puedes modificar centavos en el importe neto.",{icon:"info",button: "Cerrar"});	
		}
		
	}
	</script>
	</head>
	<body id="dt_example">
	<form action="">
		<div  id="container" class="container" style="width: 98%;">
			 <fieldset> 
			 <legend>Informaci&oacute;n del Procedimiento</legend>
					<table align="left" width="100%">
			 		<tr id="trBotones" style='display:none' align="right">						
						<td align="right" colspan="2"  >
							<input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgPlayAdjudicar" 	name="imgPlayAdjudicar" 	value="Adjudicar"	onclick="adjudicarProcedimiento();"/>&nbsp;&nbsp;  
							<input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 	id="imgPlayDesierto" 	name="imgPlayDesierto" 	value="Desierto"	onclick="desiertoProcedimiento();"/>&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgPlayDevolver" 	name="imgPlayDevolver" 	value="Devolver" onclick="devolverProcedimiento();"	/>&nbsp;&nbsp;
                            <input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	 onclick="salir();" />&nbsp;&nbsp;
						</td>
					</tr>
					<tr id="trSalir" style='display:none'>
						<td align="right" colspan="2"  >
							<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	 onclick="salir();" />&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
				    	<td align="left" colspan="2">
				    		<input type="text" style="width: 25px;border-width:0; background-color:transparent;" id="lblUnidadUsuario" name="lblUnidadUsuario" readonly  value="<%=unidadUsuarioLogeado%>"/>
				    		<input type="text" style="width: 690px;border-width:0; background-color:transparent" id="lblDescUsuario" name="lblDescUsuario" readonly />
				    	</td>
			    	</tr>
					<tr align="left">
						<td colspan="2">
							[[<input name="cIdProcedimiento" id="cIdProcedimiento" type="text" size ="10" style="border-width:0; background-color:transparent;" readonly="readonly"/>]]&nbsp;
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
				<tr id="trDuplicidad" align="left">
					<td>
						<textarea id="cadenaDuplicidad" name="cadenaDuplicidad" rows="5" cols="95" style="border-width:0; background-color:transparent; color: red" readonly="readonly"></textarea>
					</td>
				</tr>
					<tr><td><br/></td></tr>
			    </table>
			 </fieldset>
			  <br/>
			  <div id="tablaProveedores">
				<table align="center" width="850px" height="20">
				  <tr>
					<td align="left">
						<input type="button" name="borraTodoProveedoresCotizacion" id="borraTodoProveedoresCotizacion" value="BORRA PROVEEDORES" class="btnInterfaceBG ui-button ui-corner-all"/>
					</td>
				  </tr>
				</table>
				<table id="tblProvedoresCotizaciones" class="display" >
					<thead>
					  <tr>
						<th width="300px">RFC</th>
						<th width="400px">Raz&oacute;n Social</th>
						<th width="100px"></th>
						<th ></th>     
					  </tr>
					</thead>
				</table>
			 </div>
			 <div id="formProveedores">
				<table align="center" width="850px" height="20" >
					<tr><td align="left"><input type="text" id="rfcCotizacion" name="rfcCotizacion"style="width: 10em;border-width:0; background-color:transparent;"  readonly="readonly"/>
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2" ><input type="text" id="razonSocialCotizacion" name="razonSocialCotizacion"style="width: 30em; border-width:0; background-color:transparent;"  readonly="readonly"/>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="left"><input id="chk_pedidoAbierto" name="chk_pedidoAbierto" type="checkbox" onClick="validaContratoPedidoAbierto(this);" />&nbsp; Contrato o Pedido Abierto							
						</td>
					</tr>
					<tr id="trComprometeMaximo" style="display: none;">
						<td colspan="2" align="left"><input id="chk_comprometeMaximo" name="chk_comprometeMaximo" type="checkbox" onclick="comprometeMaximo(this)" />&nbsp; Comprometer el Monto M&aacute;ximo						
						</td>
					</tr>
					<tr id="trPedidoContratoBienes">
						<td colspan="2" align="left"><input id="chk_pedidoContratoBienes" name="chk_pedidoContratoBienes" type="checkbox" onclick="pedidoContServAbienes();"/>&nbsp; Convertir Servicios a Bienes						
						</td>
					</tr>
					<tr>
						<td align="left"colspan="2" >Monto Bruto:<input type="text" id="montoBrutoCotizacion" name="montoBrutoCotizacion"style="width: 30em; border-width:0; background-color:transparent;"  readonly="readonly"/>
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2">Monto Neto:<input type="text" id="montoNetoCotizacion" name="montoNetoCotizacion"style="width: 30em; border-width:0; background-color:transparent;"  readonly="readonly"/>
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2">Monto Neto M&iacute;nimo:<input type="text" id="montoNetoCotizacionMinimo" name="montoNetoCotizacionMinimo"style="width: 15em; border-width:0; background-color:transparent;"  readonly="readonly"/>
						</td>
					</tr>
					<tr id="trmMontoNetoMax" style="display: none;">
						<td align="left" colspan="2">Monto Neto M&aacute;ximo:<input type="text" id="montoNetoCotizacionMax" name="montoNetoCotizacion"style="width: 15em; border-width:0; background-color:transparent;"  readonly="readonly"/>
						</td>
					</tr>
					<tr style="display: none;"><td align="center">Porcentaje de IVA:</td>
						<td align="left"><input type="text" id="porcentajeIVACotizacion" name="porcentajeIVACotizacion" style="width: 5em; border-width:0; background-color:transparent;"  readonly="readonly"/>%
						</td>
					</tr>
					<tr>
						<td align="left" colspan="2">Tipo de Cambio:<select id="cboCambioCotizacion" name="cboCambioCotizacion" onchange="cambiaTipoMoneda();"style="width: 15em;">
													<option value="" selected="selected"></option>
													</select>
						<input type="text" id="valorTipoCambio" name="valorTipoCambio" onKeyPress="return(onlyNumbers(event));" style="width: 5em; visibility: hidden; " />	
					    </td>
				    </tr>
				    <tr id="trMontoTotalPluri" style="display: none;">
						<td align="left" colspan="2">Monto Total Pedido/Contrato Plurianual:<input type="text" id="montoTotalPluri" name="montoTotalPluri" onkeypress="return onlyNumbers1(event);" onblur="frmt(this)" value="">
						</td>
					</tr>
					<tr id="trNumProcedimientoCNET" >
						<td align="left" colspan="2">Procedimiento CNET:<input type="text" id="cNumProcedimientoCNET" name="cNumProcedimientoCNET"style="width: 30em;" />
						</td>
					</tr>
					 </table>
			  </div>
			  <div id="procedimientoMateriales" >
			     <table border="0" align="center" width="850px" height="20">
				  	<tr><td width="33%">&nbsp;</td>
						<td  align="center">
							<input type="button" name="btnGuardarCotizacionProc" id="btnGuardarCotizacionProc" value="GUARDAR" class="btnInterfaceBG ui-button ui-corner-all"/>&nbsp;&nbsp;&nbsp;
							<input type="button" name="btnPartidaCotizacionProc" id="btnPartidaCotizacionProc" value="NUEVA PARTIDA" class="btnInterfaceBG ui-button ui-corner-all"/>
						</td>
						<td width="33%">&nbsp;</td>
					</tr>
					
				</table>
			 </div><br/>
			 <div id="arrendamientos">
				<fieldset> 
			 	   <legend>Arrendamiento de Inmuebles</legend>
				   <table border="0" align="center" width="850px" height="20">
				    	<tr><td align="center"><font color="red">Arrendamientos</font></td></tr>
					    <tr><td><br/></td></tr>
					   	<tr>
						    <td align="right">Renta Mensual Fija C/IVA</td>
						    <td colspan="3" align="left"><input type="text" id="rmfArrendaniento" name="rmfArrendaniento" size="30" class="arrendamientos" onKeyPress="return(onlyNumbersInmuebles(event));" /><font color="red">&nbsp;&nbsp;X&nbsp;&nbsp;</font><input type="text" id="mesesArrendaniento" name="mesesArrendaniento" size="15" class="arrendamientos" onKeyPress="return(onlyIntInmuebles(event));"/>&nbsp;Meses<font color="red" >*</font></td>
						</tr>
				    
					    <tr>
					    <td align="right">Renta Variable C/IVA</td>
					    <td colspan="3" align=left><input id="chk_rvArrendamiento" name="chk_rvArrendamiento" type="checkbox" size="4" class="arrendamientos"  onClick="validaRVArrendamiento(this);"   />&nbsp;<input type="text" id="rvArrendamiento" name="rvArrendamiento" size="27" class="arrendamientos" onKeyPress="return(onlyNumbersInmuebles(event));" /><font color="red" >&nbsp;&nbsp;X&nbsp;&nbsp;</font><input type="text" id="semanasArrendamiento" name="semanasArrendamiento" size="14" class="arrendamientos"  onKeyPress="return(onlyIntInmuebles(event));"/>&nbsp;Semanas<font color="red" >*</font></td>
					    </tr>
					    
					    <tr><td><br/></td></tr>
					     <tr>
					    	<td align="center"><font color="red">Mantenimiento</font></td>
					    </tr>
					    
					    <tr><td><br/></td></tr>
					    <tr>
					    	<td align="right">Renta Mensual Fija C/IVA</td>
					    	<td colspan="3" align="left"><input type="text" id="rmfMantenimiento" name="rmfMantenimiento" size="30" class="arrendamientos"  onKeyPress="return(onlyNumbersInmuebles(event));" /><font color="red">&nbsp;&nbsp;X&nbsp;&nbsp;</font><input type="text" id="mesesMantenimiento" name="mesesMantenimiento" size="15" class="arrendamientos" onKeyPress="return(onlyIntInmuebles(event));"/>&nbsp;Meses<font color="red" >*</font></td>
					    </tr>
					    <tr>
						    <td align="right">Renta Variable C/IVA</td>
						    <td colspan="3" align=left><input id="chk_rvMantenimiento" name="chk_rvMantenimiento" type="checkbox" size="4" class="arrendamientos" onClick="validaRVMantenimiento(this);" />&nbsp;<input type="text" id="rvMantenimiento" name="rvMantenimiento" size="27" class="arrendamientos"  onKeyPress="return(onlyNumbersInmuebles(event));"/><font color="red" >&nbsp;&nbsp;X&nbsp;&nbsp;</font><input type="text" id="semanasMantenimiento" name="semanasMantenimiento" size="14" class="arrendamientos" onKeyPress="return(onlyIntInmuebles(event));"/>&nbsp;Semanas<font color="red" >*</font></td>
					    </tr>
				   </table>
				     </fieldset> 
				   </div>
				   <br/>
				   
				   
				   <div id="procedimientoArrendamiento" >
				     <table border="0" align="center" width="850px" height="20">
					  	<tr><td width="33%">&nbsp;</td>
							<td  align="center">
								<input type="button" name="btnGuardarCotizacionArrendamientos" id="btnGuardarCotizacionArrendamientos" value="GUARDAR" class="btnInterfaceBG ui-button ui-corner-all"/>&nbsp;&nbsp;&nbsp;
								<input type="button" name="btnPartidaCotizacionArrendamientos" id="btnPartidaCotizacionArrendamientos" value="NUEVA PARTIDA" class="btnInterfaceBG ui-button ui-corner-all"/>
							</td>
							<td width="33%">&nbsp;</td>
						</tr>
					</table>
				  </div>
			  
			  
			  <br/>
			 
			 <div id="tblPartidasCotizacion">
					<fieldset><legend>Partidas Disponibles</legend>

						<table border="0"  class="display" width="800">
						  <tr><td  align="left" >
						  	<input type="button" name="btnSeleccionaPartidas" id="btnSeleccionaPartidas" value="TODAS LAS PARTIDAS" class="btnInterfaceBG ui-button ui-corner-all"/>
						  </td></tr>
						</table>
						<table id="tblPartidas" border="0" class="display" width="800"  height="50"  >
							<thead >
								<tr>
									<th >Partida</th>
									<th >CUCOP</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n Adicional</th>
									<th >Cantidad</th>
									<th style="display: none;"></th>
									<th style="display: none;"></th>
								</tr>
							</thead>
						</table>
					</fieldset>
				</div><br/>		
			 <div id="tblProvCotizacion">
			 	<fieldset><legend>Partidas Agregadas al Proveedor</legend>
				<table align="center" width="850px" height="20">
					<tr><td  align="left">
						<input type="button" name="btnBorraPartidas" id="btnBorraPartidas" value="BORRAR TODAS LAS PARTIDAS" class="btnInterfaceBG ui-button ui-corner-all"/>
						</td>
					</tr>
					</table>
					<div id="dtblProovedoresCotizacion">
						<table id="tblProovedoresCotizacion" border="0"  class="display" width="800"  height="50"  >
							<thead >
									<tr>
										<th width="70px">Partida</th>
										<th width="60px">CUCOP</th>
										<th width="200px">Descripcion</th>
										<th width="200px">Descrip. Adicional</th>
										<th width="50px">Cantidad</th>
										<th width="150px">Precio Unitario</th>
										<th width="150px">Monto Bruto</th>
										<th width="40px">IVA</th>
										<th width="150px">Monto Neto <br/> a Pre-Comprometer</th>
										<th width="150px"  style="display: none;">Monto Neto <br/> M&iacute;nimo</th>
										<th></th>
										<th style="display: none;"></th>
										<th style="display: none;"></th>
									</tr>
								</thead>
							</table>
						</div>
						<div id="dtblProovedoresCotizacionPlu">
							<table id="tblProovedoresCotizacionPlu" border="0"  class="display" width="800"  height="50"  >
								<thead >
									<tr>
										<th width="70px">Partida</th>
										<th width="60px">CUCOP</th>
										<th width="200px">Descripcion</th>
										<th width="200px">Descrip. Adicional</th>
										<th width="50px">Cantidad</th>
										<th width="150px">Precio Unitario</th>
										<th width="150px">Monto Bruto</th>
										<th width="40px">IVA</th>
										<th width="150px">Monto Neto <br/> a Pre-Comprometer</th>
										<th width="150px"  style="display: none;">Monto Neto <br/> M&iacute;nimo</th>
										<th width="150px">Monto Neto <br/> Pluri-Anual</th>
										<th></th>
										<th style="display: none;"></th>
										<th style="display: none;"></th>
									</tr>
								</thead>
							</table>
						</div>
						<div id="dtblProovedoresCotizacion2">
							<table id="tblProovedoresCotizacion2" border="0"  class="display" width="800"  height="50"  >
								<thead >
									<tr>
										<th width="70px">Partida</th>
										<th width="60px">CUCOP</th>
										<th width="200px">Descripcion</th>
										<th width="200px">Descrip. Adicional</th>
										<th width="50px">Cantidad</th>
										<th width="150px">Precio Unitario</th>
										<th width="150px">M&aacute;ximo</th>
										<th width="150px">Monto Bruto <br/> a Pre-Comprometer</th>
										<th width="150px">Monto Bruto  <br/>M&aacute;ximo</th>
										<th width="40px">IVA</th>
										<th width="150px">Monto Neto <br/> a Pre-Comprometer</th>
										<th width="150px">Monto Neto<br/> M&iacute;nimo</th>
										<th width="150px">Monto Neto<br/> M&aacute;ximo</th>
										<th></th>
										<th style="display: none;"></th>
										<th style="display: none;"></th>
									</tr>
								</thead>
							</table>
						</div>
						<div id="dtblProovedoresCotizacion2Plu">
							<table id="tblProovedoresCotizacion2Plu" border="0"  class="display" width="800"  height="50"  >
								<thead >
									<tr>
										<th width="70px">Partida</th>
										<th width="60px">CUCOP</th>
										<th width="200px">Descripcion</th>
										<th width="200px">Descrip. Adicional</th>
										<th width="50px">Cantidad</th>
										<th width="150px">Precio Unitario</th>
										<th width="150px">M&aacute;ximo</th>
										<th width="150px">Monto Bruto <br/> a Pre-Comprometer</th>
										<th width="150px">Monto Bruto  <br/>M&aacute;ximo</th>
										<th width="40px">IVA</th>
										<th width="150px">Monto Neto <br/> a Pre-Comprometer</th>
										<th width="150px">Monto Neto<br/> M&iacute;nimo</th>
										<th width="150px">Monto Neto<br/> M&aacute;ximo</th>
										<th width="150px">Monto Neto <br/> Pluri-Anual</th>
										<th></th>
										<th style="display: none;"></th>
										<th style="display: none;"></th>
									</tr>
								</thead>
							</table>
						</div>
						<div id="dtblProovedoresCotizacion2Bienes">	
							<table id="tblProovedoresCotizacion2Bienes" border="0"  class="display" width="800"  height="50"  >
								<thead >
									<tr>
										<th width="70px">Partida</th>
										<th width="60px">CUCOP</th>
										<th width="200px">Descripcion</th>
										<th width="200px">Descrip. Adicional</th>
										<th width="50px">Cantidad Minima</th>
										<th width="150px">Precio Unitario</th>
										<th width="50px">Cantidad Maxima</th>
										<th width="150px">Monto Bruto<br/> a Pre-Comprometer</th>
										<th width="150px">Monto Bruto<br/>M&aacute;ximo</th>
										<th width="40px">IVA</th>
										<th width="150px">Monto Neto <br/> a Pre-Comprometer</th>
										<th width="150px">Monto Neto <br/>M&iacute;nimo</th>
										<th width="150px">Monto Neto <br/>M&aacute;ximo</th>
										<th></th>
										<th style="display: none;"></th>
										<th style="display: none;"></th>
									</tr>
								</thead>
							</table>
						</div>
						<div id="dtblProovedoresCotizacion2BienesPlu">	
							<table id="tblProovedoresCotizacion2BienesPlu" border="0"  class="display" width="800"  height="50"  >
								<thead >
									<tr>
										<th width="70px">Partida</th>
										<th width="60px">CUCOP</th>
										<th width="200px">Descripcion</th>
										<th width="200px">Descrip. Adicional</th>
										<th width="50px">Cantidad Minima</th>
										<th width="150px">Precio Unitario</th>
										<th width="50px">Cantidad Maxima</th>
										<th width="150px">Monto Bruto<br/> a Pre-Comprometer</th>
										<th width="150px">Monto Bruto<br/>M&aacute;ximo</th>
										<th width="40px">IVA</th>
										<th width="150px">Monto Neto <br/> a Pre-Comprometer</th>
										<th width="150px">Monto Neto <br/>M&iacute;nimo</th>
										<th width="150px">Monto Neto <br/>M&aacute;ximo</th>
										<th width="150px">Monto Neto <br/> Pluri-Anual</th>
										<th></th>
										<th style="display: none;"></th>
										<th style="display: none;"></th>
									</tr>
								</thead>
							</table>
						</div>
					</fieldset>
				</div>
				
				<input id="usuarioLogin" name="usuarioLogin" type="hidden" size="10">
				<input id="cIdEntidadContable" name="cIdEntidadContable" type="hidden" size="10">
				<input id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" type="hidden" size="10">
				<input id="usuarioRoleProcedimiento" name="usuarioRoleProcedimiento" value=""  type="hidden" size="10">
				
				<input id="ivaProcedimiento" name="ivaProcedimiento" value=""  type="hidden" size="10">
				<input id="idTipoCambio" name="idTipoCambio" type="hidden" size="10">
				<input id="valorTipoCambio1" name="valorTipoCambio1"  type="hidden" size="10">
				<input id="nPorcentajeIVAProveedor" name="nPorcentajeIVAProveedor" value=""  type="hidden" size="10">	
				<input id="cidRFCOculto" name="cidRFCOculto" type="hidden" size="10">
				<input id="nIdProcedimiento" name="nIdProcedimiento" value=""  type="hidden" size="10">
				<input id="tipoProceso" name="tipoProceso" type="hidden" size="10">
						
				
				<input id="ConsecutivoConsolidado" name="ConsecutivoConsolidado" value=""  type="hidden" size="10">
				<input id="TipoConsolidado" name="TipoConsolidado" type="hidden" size="10">
						
				<input id="IContratoAbierto" name="IContratoAbierto" type="hidden" size="10">
				<input id="lineaConsolidado" name="lineaConsolidado" type="hidden" size="10">
				<input id="montoMinimo" name="montoMinimo" type="hidden" size="10">
    			<input id="montoMaximo" name="montoMaximo" type="hidden" size="10">
    			<input id="totalPromedioProcedimiento" name="totalPromedioProcedimiento" type="hidden" size="10">
    			<input id="descripcionPartida" name="descripcionPartida" type="hidden" size="10">
    			<input id="tieneProveedor" name="tieneProveedor" type="hidden" size="10">
				<input id="cPartidaDesierta" name="cPartidaDesierta" type="hidden" size="10">
				<input id="cCategoriaDescripcion" name="cCategoriaDescripcion" type="hidden" size="10">
				<input id="idconsolidado" name="idconsolidado" type="hidden" size="10">
				<input id="EstadoCaptura" name="EstadoCaptura" type="hidden" size="10">
				<input id="cboCategoria" name="cboCategoria" type="hidden" size="10">
				<input id="descripcionCaratula" name="descripcionCaratula" type="hidden" size="10">
				<input id="numero_externoCaratula" name="numero_externoCaratula" type="hidden" size="10">
				<input id="Activo" name="Activo" type="hidden" size="10">
				<input id="nIdEstado" name="nIdEstado" type="hidden" size="10">
				<input id="nIdLineaCons" name="nIdLineaCons" type="hidden" value="">
				
				<input id="documentoAplicado" name="documentoAplicado" type="hidden">
				<input id="cEventoFlujo" name="cEventoFlujo" type="hidden">
				<input id="existePrecompromiso" name="existePrecompromiso" type="hidden" size="10">
				<input id="partidasAdjudicadas" name="partidasAdjudicadas" type="hidden" size="10">
				<input id="MontoConIVA" name="MontoConIVA" type="hidden" size="10">
				<input id="nPorcIVA" name="nPorcIVA" type="hidden" size="10" value="">
				<input id="cIdUEConsolidado" name="cIdUEConsolidado" type="hidden" />
				<input id="resutadoModificaCantRequisiciones" name="resutadoModificaCantRequisiciones" type="hidden" />
				<input id="cadenaMeses" name="cadenaMeses" type="hidden" />
				<input id="spAgregaCantidadesLineaPartida" name="spAgregaCantidadesLineaPartida" type="hidden" />
				
				<!-- bitacora -->
				<input id="cAccion" name="cAccion" type="hidden" size="10">
				<input id="cIdDocumento" name="cIdDocumento" type="hidden" size="10">
				<input id="cIdUsuario" name="cIdUsuario" type="hidden" size="10">
				<input type="hidden" name="isPlurianual" id="isPlurianual" />
				<input type="hidden" name="descripcionPartidaAdicional" id="descripcionPartidaAdicional" />
				<input type="hidden" name="nCantidad" id="nCantidad" />
				<input type="hidden" name="nCantidadReal" id="nCantidadReal" />
				<input type="hidden" name="mMontoNetoLinea" id="mMontoNetoLinea" value="0"/>
				<input type="hidden" name="mMontoNetoLineaMax" id="mMontoNetoLineaMax" value="0"/>
				<input type="hidden" name="esCucopGasolina" id="esCucopGasolina" value=""/>
				<input type="hidden" name="nCantidadMax" id="nCantidadMax" value="0"/>
				<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
				<input type="hidden" name="mMontoNetoLineaMinimo" id="mMontoNetoLineaMinimo" value="0"/>
				<input type="hidden" name="hayPartidasDisp" id="hayPartidasDisp" value="0"/>
				<input type="hidden" name="YaFueGuardado" id="YaFueGuardado" value="0"/>
				<input type="hidden" name="IContratoAbiertoProveedor" id="IContratoAbiertoProveedor" value="FALSE"/>
				<input type="hidden" name="mMontoNetoLineaPlu" id="mMontoNetoLineaPlu" value="0"/>
				<input type="hidden" name="nServicio_A_Bienes" id="nServicio_A_Bienes" value="0"/>
				<input id="cOficio" name="cOficio" type="hidden" value="">
				<input id="nIdconsecutivoAdj" name="nIdconsecutivoAdj" type="hidden" value="-1">
				<input id="lComprometeMaximo" name="lComprometeMaximo" type="hidden" value="0">
				
		</div>	
		</form>
	</body>
</html>