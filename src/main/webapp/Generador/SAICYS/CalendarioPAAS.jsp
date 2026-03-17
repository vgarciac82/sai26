<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
	String role="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String editaCIdCABM=request.getParameter("editaCIdCABM");
	String cEjercicio=request.getParameter("cEjercicio");
	String desUnidadResponsable1=request.getParameter("desUnidadResponsable1");
	String editaCIdSubPartida=request.getParameter("editaCIdSubPartida");
	String descrip=request.getParameter("descrip");
	String CVE_UNI=request.getParameter("CVE_UNI");
	Map rol =usuario.getRoles();
	String ueOrig=usuario.getU_UR_Orig() == null? usuario.getU_UR(  ): usuario.getU_UR_Orig();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    
    <title>'CalendarioPAAS'</title>
    <style type="text/css">
		.centerCls {
			text-align: center;
		}
		
		.rightCls {
			text-align: right;
		}
	</style>
		    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
  		$(document).ready(function() {
  			<%
  				int tabla=0;
  				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
  				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role =(String)r.getKey();
					roles+= r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
  			%>
  			$("#tbs").val(1);
  			$("#CalendarioPAAS").show();
  			var roles="<%=roles%>";
  			$("#editaCIdCABM").val("<%=editaCIdCABM%>");
  			$("#cEjercicio").val("<%=cEjercicio%>");
  			$("#desUnidadResponsable1").val("<%=desUnidadResponsable1%>");
  			$("#editaCIdSubPartida").val("<%=editaCIdSubPartida%>");
  			
  			iva = $("#ivaEdita");
			estimado_compras = $("#porcentajeEdita");
			pyme = $("#pymeEdita");
			plurianualidad = $("#plurianualidad");
			plurianualidadv = $("#plurianualidadv");
			precargaPAAS = $("#precargaPAAS");
			allFields = $( [] ).add( iva ).add( estimado_compras ).add( pyme ).add( plurianualidad ).add( precargaPAAS );
  			//cEjercicio,desUnidadResponsable1,editaCIdCABM,editaCIdSubPartida
  			queryFormPost("llenaDetalleCucopEdita",{async:false});
  			queryFormPost("totalCucopEdita",{async:false});
  			$("#totalEdita").formatCurrency();
  			tips = $( ".validateTips" );
  			queryFormPost("existenciasAlmacen",{async:false});
  			querySelectPost("mCatalogoProcedenciaEdita", "tipoProcedimientoEdita", {async : false
  				,callback : function() {
  					$("#tipoProcedimientoEdita").val('N');
				}
  			});
  			querySelectPost("mCatalogoTipoIVA", "ivaEdita", {async : false});
  			querySelectPost("mCatalogoTipoAdj", "tipoAdjudicacion", {async : false});
  			queryFormPost("techoPresupuestalActivado",{async : false});
			queryFormPost("fn_mDataStoreTechoPresupuestalPartidaRead",{async : false});
			queryFormPost("toleranciaPAPartidaRead",{async : false});
			
			datosTabla();
			
  			$("#autoAjustePAAS").button().click(function(){
				$("#usuarioUE").val('<%=ueOrig%>');
			 	//if( ( $('#desUnidadResponsable1').val()== $("#usuarioUE").val() || ($('#desUnidadResponsable1').val()=="E04") && $("#usuarioUE").val()=="E02") || ( roles.indexOf("ADMIN_RECMAT") >= 0 ) ){
			 	if(($('#desUnidadResponsable1').val().trim()== $("#usuarioUE").val()) ||($("#desUnidadResponsable1").val().trim()=="E04" &&$("#usuarioUE").val()=="E02")|| ( roles.indexOf("ADMIN_RECMAT") >=0  || ( roles.indexOf("ANALISTA") >=0)) ){
			 		swal({
			 			title: "Est\u00e1 seguro de autoajustar los montos y cantidades programadas?, est\u00e1 acci\u00f3n no podra revertirse",
			 			text: "Se procedera al borrado de informaci\u00f3n!",
			 			icon: "info",
			 			buttons: {
			 				confirm : "Aceptar",
			 				cancel: "Cancelar"
			 				},
			 			}).then((continuar) => {
			 				if (!continuar) {
			 					return;
			 			}else{
			 				var res="";
							res=new Array();
							//revisar los checkbox que estan habilitados				 			
							var aTrs = $('#tblcucopPeriodo').dataTable().fnGetNodes();
							var token = "";
							for ( var i=0 ; i<aTrs.length; i++ ) {
								if ($("#mantener_"+i+"").is(':checked')){
									res += token + (i+1) ;
									token=",";
								}
							}  
					  		if(res== ""){
					   			res="ajustaTodoCucop";
					  		}					  		
							$("#ajustePAAS").val(res);
							queryFormPost("pa_autoAjustePAAS", { async : false});		
							swal("Se realiz\u00f3 el ajuste correctamente.",{icon:"info",button: "Cerrar"});
							actualizaPeriodoSuficiencia();
						 	//Guarda en la Bitácora
							$("#cAccion").val("AUTO_AJUSTEPAAS_PARCIAL");
							$("#cIdDocumento").val($("#ue_usuarioEdita").val()+"-"+$("#cucopEdita").val());
							queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			 			}
			 		});
				}else{
					swal("Solo un administrador o un usuario de la Unidad Ejecutora pueden realizar el auto ajuste",{icon:"warning",button: "Cerrar"});
					return;
				}
			});
			initTabl();
			$("#pbCopiarEdita").button().click(function(){
				var aTrs = $('#tblcucopPeriodo').dataTable().fnGetNodes();
				var can= $("#ncantidad_0").val();
				var pu=  $("#mpreciounitario_0").val();
				for ( var i=1 ; i<aTrs.length; i++ ) {
					$("#ncantidad_"+i+"").val(can);
					$("#mpreciounitario_"+i+"").val(pu);
				}    
				$('.editacucop').formatCurrency();
				//GUARDA EN LA BITACORA ACCION
				//Guarda en la Bitácora
				$("#cAccion").val("COPIA_CALENDARIOPAAS");
				$("#cIdDocumento").val($("#ue_usuarioEdita").val()+"-"+$("#cucopEdita").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
			});
			$("#pbGuardaEdita").button().click(function(){
	     		var totalVirtual = 0;
	     		var aTrsV = $('#tblcucopPeriodo').dataTable().fnGetNodes();
	           	var puPromedio="0.00";
	           	var color="";
				for(var i=0; i < aTrsV.length; i++){
	          		var nTr = $('#tblcucopPeriodo').dataTable().fnGetData(i);
	          		if($("#ncantidad_" + i + "").val()==''){
	          			$("#ncantidad_" + i + "").val(0);
	          		}
					var cantidad = parseInt($("#ncantidad_" + i + "").val(),10);
					//var precio = $("#mpreciounitario_" + i + "").asNumber({ parseType: 'Float'});
					var precio = parseFloat(quitaFmt($("#mpreciounitario_" + i + "").val()));
					var resultadoPrecio = Math.round(precio*Math.pow(10,2))/Math.pow(10,2);
					
					if(parseInt(cantidad,10)>0 && parseFloat(resultadoPrecio)<=parseFloat(0)){
						swal("El precio no puede ser 0.00 en el mes "+(i+1),{icon:"warning", button: "Cerrar"});
						return;
					}
					var totalLinea =parseFloat(cantidad * resultadoPrecio);
					totalVirtual = totalVirtual + totalLinea;
	           	}
	           	//Obtener el promedio de PU del ejercicio anterior
				var obtenerPU=false;
				if(obtenerPU){
					$.ajax({
						async: false,
						cache: false,
						type: 'GET',
						url: "../../servlet/ObtienePrecioUnitarioPAA",
						data: "Param="+$("#editaCIdCABM").val()+","+$("#desUnidadResponsable1").val()+","+$("#cEjercicio").val(),
						success: function(resp){
							if(resp.indexOf("Error:") >= 0)
								puPromedio = "0.00";
	 	  					else
	 	  						puPromedio = resp;
						}
					});
				
				}
				totalVirtual = parseFloat(totalVirtual * (1 + ($("#ivaEdita").val() / 100)));
				// var difVirtual = totalVirtual - $("#totalEdita").asNumber({ parseType: 'Float'});
				var difVirtual = totalVirtual -  parseFloat(quitaFmt($("#totalEdita").val()));
				if (difVirtual < 0.01 && difVirtual >= 0) {
	            	difVirtual = 0;
				}
				//Comentar y descomentar los siguientes 3 cruds para validar el PAAS vs presupuesto
				queryFormPost("fn_mProgramaAnualMontoPorPartidaRestCUCOPRead",{async : false});
				queryFormPost("fn_mDataStoreTechoPresupuestalPartidaRead",{async : false});
				queryFormPost("toleranciaPAPartidaRead",{async : false});
				if ($("#mTechoPresupuestal").val() == null || $("#mTechoPresupuestal").val() == '') {
					$("#mTechoPresupuestal").val(0);
				}
				var totalPartidaV = parseFloat(totalVirtual)+parseFloat($("#mMontoPartidaRestCUCOP").val());
				var techoConTolerancia = (parseFloat($("#mTechoPresupuestal").val()) * parseFloat((1 + ($("#cTolerancia").val() / 100))));
				
				//Comentar y descomentar el siguiente cruds para validar el PAAS vs presupuesto
				// VALIDA SI ESTA ACTIVADO EL TECHO PRESUPUESTAL
				queryFormPost("techoPresupuestalActivado",{async : false});
				
				if($("#techoActivado").val()=='TRUE'){
					if (totalPartidaV > techoConTolerancia){
	                	swal("No es posible realizar las modificaciones porque sobrepasan el presupuesto de la partida.",{icon:"warning", button: "Cerrar"});
	                	return;
	                }
				}
		        var bValid = true;
				tips.text("");
				//para obtener el importe bruto de un cucop seleccionado
		      	queryFormPost("obtienMontoBrutoCucop", {async: false});
				//allFields.removeClass( "ui-state-error" );
				bValid = bValid && checkRequerido( iva, "Iva" );
				bValid = bValid && checkRequerido( estimado_compras, "Pyme" );
				bValid = bValid && checkRequerido( pyme, "Estimado de compras" );
				if ($('#chk_plurianualidad').is(':checked')) {
					bValid = bValid && checkRequerido( plurianualidad, "Plurianualidad" );
					bValid = bValid && checkRequerido( plurianualidadv, "Plurianualidadv" );
				} 
				if(!bValid){
					return;
				}
			     //se valida si es administrador o usuario de la unidad ejecutora seleccionada
				$("#usuarioUE").val('<%=usuario.getU_UR()%>');
				if(parseInt( $("#pymeEdita").val(),10) > ( parseInt($("#mImporteBruto").val(),10)) ){
	            	swal("El pyme debe ser menor igual que el monto bruto.",{icon:"warning", button: "Cerrar"});
	                return;
				}
	            if(parseInt( $("#porcentajeEdita").val(),10) > ( parseInt($("#mImporteBruto").val(),10)) ){
	            	swal("El valor estimado debe ser menor o igual al monto bruto.",{icon:"warning", button: "Cerrar"});
	                return;
				}
	            if(parseFloat( $("#ivaEdita").val()) > 100 ){
	            	swal("El porcentaje de IVA no puede ser mayor a 100.",{icon:"warning", button: "Cerrar"});
					return;
				}
	     		var aTrs = $('#tblcucopPeriodo').dataTable().fnGetNodes();
	            for(var i=0;i<aTrs.length;i++){
					if(parseInt($("#ncantidad_"+i+"").val(),10) < parseInt($("#ncantidadensolicitudes_"+i+"").val(),10)){
				    	swal("La cantidad no puede ser menor a " + $("#ncantidadensolicitudes_"+i+"").val() + ", en el periodo "+ (i+1),{icon:"warning", button: "Cerrar"});
			            return;
					}
					if(parseInt($("#ncantidadensolicitudes_"+i+"").val(),10)!=0){
			        	var precio=$("#mMontoNetoEnSolicitudes_"+i+"").asNumber({ parseType: 'Float' })/(1 + (parseFloat($("#ivaEdita").val())* 0.01))/$("#ncantidadensolicitudes_"+i+"").asNumber({ parseType: 'int' });
						precio=precio.toFixed(2);
			            var unitario= $("#mpreciounitario_"+i+"").asNumber({ parseType: 'Float'});
			            unitario=unitario.toFixed(2);
						if(parseFloat(unitario) <  parseFloat(precio)) {
				        	swal("El precio unitario no puede ser menor a " + precio + ", en el periodo "+ (i+1),{icon:"warning", button: "Cerrar"});
			                return;
						}
					}
				}
				if(parseFloat( $("#ivaEdita").val()) > 100 ){
	            	swal("El porcentaje de IVA no puede ser mayor a 100",{icon:"warning", button: "Cerrar"});
	                return;
				}
				for(var i=0;i<aTrs.length;i++){
					if(parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) != parseFloat(puPromedio) 
				  		&& parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) > 0  
				  		&& parseInt($("#ncantidad_"+i+"").val(),10) == 0 ){
				  		swal("Debe introducir una cantidad en el mes "+(i+1)+".",{icon:"warning", button: "Cerrar"});
				  		return;
					}
				  	if(parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) == 0  
				  		&& parseInt($("#ncantidad_"+i+"").val(),10) > 0 ){
				  		swal("Debe introducir el precio unitario en el mes "+(i+1)+".",{icon:"warning", button: "Cerrar"});
				  		return;
					}
				}
				for(var i=0;i<aTrs.length;i++){
					var cantidad = parseInt($("#ncantidad_" + i + "").val(),10).toFixed(2)-parseInt($("#ncantidadensolicitudes_" + i + "").val(),10).toFixed(2);
					var precio = parseFloat(quitaFmt($("#mpreciounitario_" + i + "").val())).toFixed(2);
					var resultadoPrecio = parseFloat(Math.round(precio*Math.pow(10,2))/Math.pow(10,2))*(1 + (parseFloat($("#ivaEdita").val())* 0.01)).toFixed(2);
					var totalLinea =parseFloat(cantidad * resultadoPrecio).toFixed(2);
					
					var mes=(i+1);

					if(totalLinea>0){
						$("#cIdPartida").val($("#editaCIdSubPartida").val());
						$("#cUE").val($("#usuarioUE").val());
						$("#cMes").val(mes);
						queryFormPost("readv_SaldosRemanentesPAAAS", {async: false });
						var remanente=parseFloat(parseFloat($("#montoRemanente").val()).toFixed(2))+parseFloat(parseFloat(quitaFmt($("#mmontodisponibilidad_" + i + "").val())).toFixed(2));
						var remanente2;
						remanente2=(remanente-totalLinea).toFixed(2);
					}
					
				}
				for(var i=0;i<aTrs.length;i++){
					$("#editanCantidad").val(parseInt($("#ncantidad_"+i+"").val(),10));
				    $("#editanIdPeriodo").val(i+1);
				    $("#editacIdUsuarioModifica").val('<%=usuario.getLogin()%>');
					if((parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) == parseFloat(puPromedio) 
				    	|| parseFloat(replaceAll(replaceAll(replaceAll($("#mpreciounitario_"+i+"").val(),"$","")," ",""),",","")) == 0)  
				    	&& parseInt($("#ncantidad_"+i+"").val(),10) == 0){
				    		$("#mpreciounitario_"+i+"").val("0.00");
					}
				    $("#editamPrecioUnitario").val($("#mpreciounitario_"+i+"").val());			      	
				    queryFormPost("spGuardaCucopEditado", {async: false });
				}
			    //Se checa si se habilito el checkbox de plurianualidad
			    if ($('#chk_plurianualidad').is(':checked')) {
			    	if($("#plurianualidad").val()>0 ){
						if( parseInt($("#plurianualidadv").val(),10)>(parseInt($("#mImporteBruto").val(),10)) ){
					    	$("#valorplurianualidad").val($("#plurianualidad").val());
						    $("#valorplurianualidadv").val($("#plurianualidadv").val());
						    $("#valortipoAdjudicacion").val($("#tipoAdjudicacion").val());
						    //PARA MODIFICAR IVA Y PORCENTAJE DE PYMES
					      	queryFormPost("spAgregaCucopEdita", {async: false });
						    swal("Datos Guardados.","info",{button: "Cerrar"});
						    iniTablRemanente();
						}else{
			      			swal("El campo Monto a Ejercer debe ser  mayor  que su monto total bruto.",{icon:"warning",button: "Cerrar"});
						}
					}else{
						swal("El campo Ejercicio de plurianualidad debe ser mayor a cero.",{icon:"warning",button: "Cerrar"});
					}
				}else{
			     	$("#valorplurianualidadv").val('0');
					$("#valorplurianualidad").val('0');
			      	$("#valortipoAdjudicacion").val($("#tipoAdjudicacion").val());
					queryFormPost("spAgregaCucopEdita", {async: false });
					swal("Datos Guardados.",{icon:"info",button: "Cerrar"});
					iniTablRemanente();
				}
			    var selectFunc = "'" + $("#cEjercicio").val() + "', '" + $("#desUnidadResponsable1").val() + "', '" + $("#editaCIdCABM").val() + "'";
	          	$('#tblcucopPeriodo').dataTable().fnClearTable(); 
				szTabla = "CUCOPSPERIODOGRID";
				$.getJSON("../../catalogos/SelectJson.jsp?"+new Date().getTime(),{Tabla: szTabla, SelectFunc: selectFunc, MaxReg:"" , ajax: 'false'}, 
				function(j){   
					arrayCompleto=new Array();  
					for (var i = 0; i < j.length; i++) 
	    			{
	    				if(parseFloat(j[i].Col2) > 0){
				    		color="#000000";
				    	}else{
				    		j[i].Col3 = puPromedio;
				    		color="#B4B4B4";
						}
						arrayCompleto [i]=[
							"<input type='text' id='cperiodo' name='cperiodo' value='" + j[i].Col0 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>", 
							"<input type='text' id='cantDisp' name='cantDisp' value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='ncantidad_"+i+"' name='ncantidad' value='" + j[i].Col2 + "'  style='width:150px;'  onKeyPress='return(onlyNumbers2(event));'/>",
							"<input type='text' id='mpreciounitario_"+i+"' name='mpreciounitario' value='" + j[i].Col3 + "' class='editacucop' style='color:"+color+";width:150px;' onfocus='borraDatos("+i+");'  onchange='cambiafrmt(this);' onKeyPress='return(onlyNumbers2(event));' />",
							"<input type='text' id='mimportebruto' name='mimportebruto' value='" + j[i].Col4 + "'  class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='mimporteneto' name='mimporteneto'  value='" + j[i].Col5 + "' class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='ncantidadensolicitudes_"+i+"'   name='ncantidadensolicitudes_"+i+"' value='" + j[i].Col6 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='ncantidaddisponibilidad' name='ncantidaddisponibilidad'  value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
							"<input type='text' id='mMontoNetoEnSolicitudes_"+i+"' class='editacucop'  name='mMontoNetoEnSolicitudes_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
		                    "<input type='text' id='mmontodisponibilidad_"+i+"' class='editacucop' name='mmontodisponibilidad'  value='" + j[i].Col9 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
		                    "<input type='checkbox' id='mantener_"+i+"' class='editacucop' name='mantener_"+i+"'  value='" + j[i].Col10 + "'/>"
						];
					}	
	    			$('#tblcucopPeriodo').dataTable().fnAddData(arrayCompleto);
					$('.editacucop').formatCurrency();	
				});   
				queryFormPost("llenaDetalleCucopEdita",{async:false});
				queryFormPost("totalCucopEdita",{async:false});
				$("#totalEdita").formatCurrency();  
				if($("#desUnidadResponsable1").val() == 0){
			    	queryFormPost("fn_mProgramaAnualMontosMontoRead",{async : false});
			    }else{
			    	queryFormPost("fn_mProgramaAnualMontosPorCapitulomMontoC2Read",{async : false});
				}
				//se pone formato tipo moneda
				$("#mMontoC2").formatCurrency();
				$("#mMontoC3").formatCurrency();
				$("#mMontoC5").formatCurrency();
				$("#mMontoTotal").formatCurrency();	
				//GUARDA EN LA BITACORA ACCION
				//Guarda en la Bitácora
				$("#cAccion").val("GUARDA_CALENDARIOPAAS");
				$("#cIdDocumento").val($("#ue_usuarioEdita").val()+"-"+$("#cucopEdita").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});		
				$("#pbBuscacucop").click(); 
			});//termina el guardar
			/////////CHECK BOX PARA LA PLURIANUALIDAD////////////////
      		$( "#chk_plurianualidad" ).change(function() {
				if ($('#chk_plurianualidad').is(':checked')) {
					$("#plurianualidad").css("visibility","visible");
					$("#plurianualidadv").css("visibility","visible");
					$("#EP").css("visibility","visible");
					$("#ME").css("visibility","visible");
					$("#anos").css("visibility","visible");
					queryFormPost("obtienePluri", {async: false });
	      			$("#plurianualidad").val($("#valorplurianualidad").val());
					$("#plurianualidadv").val($("#valorplurianualidadv").val());
					$('#chk_plurianualidad').val(1);
			    } else {
			        $("#plurianualidad").css("visibility","hidden");
			        $("#plurianualidadv").css("visibility","hidden");
					$("#EP").css("visibility","hidden");
			        $("#ME").css("visibility","hidden");
					$("#anos").css("visibility","hidden");
			        $('#chk_plurianualidad').val(0);
			    }
			});
  		});//Fin del document ready
  		function initTabl(){
        	oTable3=  $("#tblcucopPeriodo").dataTable({
				bAutoWidth : false,
				bPaginate:false,
				bFilter : false,
				sScrollX: "100%",
				bDestroy:true,
				bRetrive:true,
				bLengthChange: false,
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
					sSearch: "Buscar:"
					
				},
				bProcessing: true,
				bJQueryUI: true,
				aaSorting: [[ 1, "desc" ]] ,
				aoColumns: [
					{ sName: "cperiodo",bSortable: false },
					{ sName: "cantDisp",bSortable: false   },
					{ sName: "ncantidad",bSortable: false   },					
					{ sName: "mpreciounitario",bSortable: false	},
					{ sName: "mimportebruto",bSortable: false },
					{ sName: "mimporteneto",bSortable: false },
					{ sName: "ncantidadensolicitudes",bSortable: false },
					{ sName: "ncantidaddisponibilidad", bVisible: false,bSortable: false },
					{ sName: "mMontoNetoEnSolicitudes",bSortable: false },
					{ sName: "mmontodisponibilidad",bSortable: false },
					{ sName: "mMantener",bSortable: false }
				]
        	});
        	iniTablRemanente();
  		}
  		function iniTablRemanente(){
  		oTable4= $("#tblDisponible").dataTable({
  			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bPaginate" : true,
			"bAutoWidth" : true,
			"bScrollCollapse" : true,
			"sScrollX": "100%",
			"sPaginationType" : "full_numbers",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bServerSide": true,
			"fnInitComplete": function() {
				oTable4.fnAdjustColumnSizing();
			},
			"iDisplayLength": 10,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_SaldosRemanentesPAAAS&qw="+" UE= '"+'<%=usuario.getU_UR()%>'+"' AND Partida= '"+$("#editaCIdSubPartida").val()+"'",
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "UE",bSortable: false , sClass : "centerCls" },
					{ sName: "Partida",bSortable: false, sClass : "centerCls"   },
					{ sName: "MontoAnual",bSortable: false , sClass : "centerCls"  },					
					{ sName: "MontoEnero",bSortable: false	, sClass : "centerCls"},
					{ sName: "MontoFebrero",bSortable: false , sClass : "centerCls"},
					{ sName: "MontoMarzo",bSortable: false , sClass : "centerCls"},
					{ sName: "MontoAbril",bSortable: false , sClass : "centerCls"},
					{ sName: "MontoMayo", bSortable: false, sClass : "centerCls"},
					{ sName: "MontoJunio",bSortable: false , sClass : "centerCls"},
					{ sName: "MontoJulio",bSortable: false , sClass : "centerCls"},
					{ sName: "MontoAgosto",bSortable: false, sClass : "centerCls" },
					{ sName: "MontoSeptiembre",bSortable: false , sClass : "centerCls"},
					{ sName: "MontoOctubre",bSortable: false, sClass : "centerCls" },
					{ sName: "MontoNoviembre",bSortable: false, sClass : "centerCls" },
					{ sName: "MontoDiciembre",bSortable: false , sClass : "centerCls"}]
			}); 
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
		function datosTabla(){
			var tabla='<%=tabla%>';
			var meses = 0;
			var puPromedio="0.00";
			if (tabla==0){
				$("#cIdPartida").val("<%=editaCIdSubPartida%>");
				queryFormPost("hayEPS",{async:false});
				if($("#hayEP").val()==0){
					swal("No hay estructura programatica para está partida.\nSolicitar adecuación presupuestal.",{icon:"warning",button: "Cerrar"});
				}else{
					queryFormPost("hayDisponibleEnEPS",{async:false});
					if($("#montoAnualTotalEPS").val()==0){
						swal("No hay monto disponible en tus EPS.\nSolicitar adecuación presupuestal.",{icon:"warning",button: "Cerrar"});
					}
				}
				//Obtener el promedio de PU del ejercicio anterior
				var obtenerPU=false;
				if(obtenerPU){
					$.ajax({
						async: false,
						cache: false,
						type: 'GET',
						url: "../../servlet/ObtienePrecioUnitarioPAA",
						data: "Param="+$("#editaCIdCABM").val()+","+$("#desUnidadResponsable1").val()+","+$("#cEjercicio").val(),
						success: function(resp){
							if(resp.indexOf("Error:") >= 0)
								puPromedio = "0.00";
	 	  					else
	 	  						puPromedio = resp;
						}
					});
				
				}
				
	  			if($("#cantidad_").val()==''){
					$("#cantidad").val('0');
	  				$("#descrip").val("<%=descrip%>");
	  				$("#CVE_UNI").val("<%=CVE_UNI%>");
	  			}
	  			else{
	  				$("#cantidad").val($("#cantidad_").val());
	  				$("#descrip").val($("#descrip_").val());
	  				$("#CVE_UNI").val($("#CVE_UNI_").val() );//
				}
		 	 	//obtiene los valores de plurianualidad 
  				queryFormPost("obtienePluri", {async: false});
  	 				
				//Para mostrar los datos que ya tiene guardados
     			$("#plurianualidad").val($("#valorplurianualidad").val());
				$("#plurianualidadv").val($("#valorplurianualidadv").val());
				$("#tipoAdjudicacion").val($("#valortipoAdjudicacion").val());
				
			 	if(parseInt($("#plurianualidad").val(),10)>0){
					document.getElementById("chk_plurianualidad").checked=true;
					$("#plurianualidad").css("visibility","visible");
					$("#plurianualidadv").css("visibility","visible");
					$("#EP").css("visibility","visible");
					$("#ME").css("visibility","visible");
					$("#anos").css("visibility","visible");
				}
				var selectFunc = "'" + $("#cEjercicio").val() + "', '" + $("#desUnidadResponsable1").val() + "', '" + $("#editaCIdCABM").val() + "'";
				var color="";
				$('#tblcucopPeriodo').dataTable().fnClearTable(); 
				szTabla = "CUCOPSPERIODOGRID";
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, SelectFunc: selectFunc, MaxReg:"" , ajax: 'false'}, 
					function(j){
						arrayCompleto=new Array();
						for (var i = 0; i < j.length; i++){
							if(parseFloat(j[i].Col2) > 0){
			    				color="#000000";
							}else{
			    				j[i].Col3 = puPromedio;
			    				color="#B4B4B4";
							}
				    		arrayCompleto [i]=[
								"<input type='text' id='cperiodo' name='cperiodo' value='" + j[i].Col0 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>", 
								"<input type='text' id='cantDisp' name='cantDisp' value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='ncantidad_"+i+"' name='ncantidad' value='" + j[i].Col2 + "'  style='width:150px;'  onKeyPress='return(onlyNumbers2(event));'/>",
								"<input type='text' id='mpreciounitario_"+i+"' name='mpreciounitario' value='" + j[i].Col3 + "' class='editacucop' style='color:"+color+";width:150px;' onfocus='borraDatos("+i+");' onchange='cambiafrmt(this);' onKeyPress='return(onlyNumbers2(event));' />",
								"<input type='text' id='mimportebruto' name='mimportebruto' value='" + j[i].Col4 + "'  class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='mimporteneto' name='mimporteneto'  value='" + j[i].Col5 + "' class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='ncantidadensolicitudes_"+i+"'   name='ncantidadensolicitudes_"+i+"' value='" + j[i].Col6 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='ncantidaddisponibilidad' name='ncantidaddisponibilidad'  value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
								"<input type='text' id='mMontoNetoEnSolicitudes_"+i+"' class='editacucop'  name='mMontoNetoEnSolicitudes_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
			                    "<input type='text' id='mmontodisponibilidad_"+i+"' class='editacucop' name='mmontodisponibilidad'  value='" + j[i].Col9 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
			                    "<input type='checkbox' id='mantener_"+i+"' class='editacucop' name='mantener_"+i+"'  value='" + j[i].Col10 + "'/>"
							];
						}
						$('#tblcucopPeriodo').dataTable().fnAddData(arrayCompleto);		    					   
						$('.editacucop').formatCurrency();
					});   							
			}
		
		}
		function onlyNumbers2(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '0123456789.';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true 
			//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
		}
		function cambiafrmt(fld){
			var formato=$("#" + fld.id).val();
			formato=parseFloat(formato).toFixed(2);
			$("#" + fld.id).val(formato);			
		}
		function borraDatos(indice){
			var br=$("#mpreciounitario_"+indice+"").val();
			br=br.replace("$","");
			br=br.replace(",","");
			$("#mpreciounitario_"+indice+"").val(br);
		}
		function checkRequerido( o, n) {
			var sTemp = $.trim(o.val());
			o.val(sTemp);
			if ( sTemp.length == 0  ) {
				o.addClass( "ui-state-error" );
				updateTipsDlg(  n + " es un dato requerido." );
				o.focus();
				return false;
			}else {
				return true;
			}
		}
		function updateTipsDlg( t ) {
			tips.text( t );
			swal(t,{icon:"info",button: "Cerrar"});
		}
		function actualizaPeriodoSuficiencia(){
			var puPromedio="0.00";
			var color="";
			queryFormPost("llenaDetalleCucopEdita",{async:false});
			queryFormPost("totalCucopEdita",{async:false});
			$("#totalEdita").formatCurrency();
			var selectFunc = "'" + $("#cEjercicio").val() + "', '" + $("#desUnidadResponsable1").val() + "', '" + $("#editaCIdCABM").val() + "'";
			//Obtener el promedio de PU del ejercicio anterior
			var obtenerPU=false;
			if(obtenerPU){
				$.ajax({
					async: false,
					cache: false,
					type: 'GET',
					url: "../../servlet/ObtienePrecioUnitarioPAA",
					data: "Param="+$("#editaCIdCABM").val()+","+$("#desUnidadResponsable1").val()+","+$("#cEjercicio").val(),
					success: function(resp){
						if(resp.indexOf("Error:") >= 0)
							puPromedio = "0.00";
 	  					else
 	  						puPromedio = resp;
					}
				});
			
				}
			
			$('#tblcucopPeriodo').dataTable().fnClearTable(); 
			szTabla = "CUCOPSPERIODOGRID";                                                                                         
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, SelectFunc: selectFunc, MaxReg:"" , ajax: 'false'}, 
			function(j){   
				arrayCompleto=new Array();  
				for (var i = 0; i < j.length; i++) 
				{
					if(parseFloat(j[i].Col2) > 0){
						color="#000000";
					}
					else{
						j[i].Col3 = puPromedio;
						color="#B4B4B4";
					}
					arrayCompleto [i]=[ 
						"<input type='text' id='cperiodo' name='cperiodo' value='" + j[i].Col0 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>", 
						"<input type='text' id='cantDisp' name='cantDisp' value='" + j[i].Col1 + "' readonly style='width:80px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='ncantidad_"+i+"' name='ncantidad' value='" + j[i].Col2 + "'  style='width:150px;'  onKeyPress='return(onlyNumbers2(event));'/>",
						"<input type='text' id='mpreciounitario_"+i+"' name='mpreciounitario' value='" + j[i].Col3 + "' class='editacucop' style='color:"+color+";width:150px;' onfocus='borraDatos("+i+");' onchange='cambiafrmt(this);' onKeyPress='return(onlyNumbers2(event));' />",
						"<input type='text' id='mimportebruto' name='mimportebruto' value='" + j[i].Col4 + "'  class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='mimporteneto' name='mimporteneto'  value='" + j[i].Col5 + "' class='editacucop' readonly style='width:150px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='ncantidadensolicitudes_"+i+"'   name='ncantidadensolicitudes_"+i+"' value='" + j[i].Col6 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='ncantidaddisponibilidad' name='ncantidaddisponibilidad'  value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
						"<input type='text' id='mMontoNetoEnSolicitudes_"+i+"' class='editacucop'  name='mMontoNetoEnSolicitudes_"+i+"' value='" + j[i].Col8 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
	                    "<input type='text' id='mmontodisponibilidad_"+i+"' class='editacucop' name='mmontodisponibilidad'  value='" + j[i].Col9 + "' readonly style='width:150px; border-width:0; background-color:transparent'/>",
	                    "<input type='checkbox' id='mantener_"+i+"' class='editacucop' name='mantener_"+i+"'  value='" + j[i].Col10 + "'/>"
					]; 
				}	
				$('#tblcucopPeriodo').dataTable().fnAddData(arrayCompleto);
				$('.editacucop').formatCurrency();
			});   
		}
	</script>

  </head>
  
  <body>
    <form action="">
    	<div > 
			<table align="center"> 
				<tr> 
					<td align="right"> 
						UE Consultada: 
					</td> 
					<td align="left"> 
						<input type="text" id="ue_usuarioEdita" name="ue_usuarioEdita" style="border: 0px none ;background:#FEFEFE; width: 30em;" maxlength="120" readonly="readonly" /> 
					</td> 
				</tr> 
				<tr> 
					<td align="right"> 
						Cucop: 
					</td> 
					<td align="left" colspan="2"> 
						<input type="text" id="cucopEdita" name="cucopEdita" readonly="readonly" style="border: 0px none ;background:#FEFEFE; width: 6em; color: red;" /> 
								-- 
						<input type="text" id="cCABMEdita" name="cCABMEdita" readonly="readonly" style="border: 0px none ;background:#FEFEFE; width: 25em; color: red;" /> 
					</td> 
				</tr> 
				<tr> 
					<td align="right"> 
						Partida: 
					</td> 
							<td align="left"> 
							<input type="text" id="partidaEdita" name="partidaEdita" style="border: 0px none ; width: 30em;background:#FEFEFE" readonly="readonly" /> 
						</td> 
					 
					</tr> 
					<tr> 
						<td align="right"> 
							Total: 
						</td> 
						<td align="left"> 
							<input type="text" id="totalEdita" name="totalEdita" readonly="readonly" style="border: 0px none ;background:#FEFEFE; width: 30em;" /> 
						</td> 
					</tr> 
					<tr> 
					   <td align="right"> 
<!-- 									<input type="text" id="ivaEdita" name="ivaEdita" style="width: 4em;" onkeypress="return(onlyNumbers(event));" maxlength="3" />%  -->
							<select id="ivaEdita" name="ivaEdita" style="width: 8em;"> 
							</select> 
						</td> 
						<td align="left" colspan=""> 
							IVA 
						</td> 
						<td> 
							 
							</td> 
							 
						</tr> 
						 
						 
						<tr> 
						   <td align="right"> 
								$<input type="text" id="pymeEdita" name="pymeEdita" maxlength="15"  style="width: 7em;" onkeypress="return(onlyNumbers2(event));" maxlength="4" /> 
							</td> 
							<td align="left"> 
								Cantidad destinado a MIPyME  
							</td> 
							 
						</tr> 
						 
						 
						<tr> 
						   <td align="right"> 
								$<input type="text" id="porcentajeEdita" name="porcentajeEdita" maxlength="15"  style="width: 7em;" onkeypress="return(onlyNumbers2(event));" maxlength="4" /> 
							</td> 
							<td align="left" colspan="2"> 
								estimado de compras no cubiertas por tratados 
							</td> 
						</tr> 
						<tr> 
						<td align="right"> 
								<select id="tipoProcedimientoEdita" name="tipoProcedimientoEdita" style="width: 15em;"> 
								</select> 
							</td> 
							<td align="left"> 
								Procedencia 
							</td> 
						</tr> 
						<tr> 
						<td align="right"> 
							Plurianual: <input type="checkbox" id="chk_plurianualidad" name="chk_plurianualidad" /> 
						</td> 
							<td align="left" colspan="6"> 

							<input name="EP" id="EP"  value="Ejercicio de Plurianualidad"size="23" style="border-width:0; background-color:transparent;visibility: hidden;" onkeypress="return(onlyNumbers(event));"/>
							<input type="text" id="plurianualidad" name="plurianualidad" maxlength="2" style="width: 2em; visibility: hidden;" onkeypress="return(onlyNumbers2(event));" /> 
							<input name="anos" id="anos"  value="A&ntilde;os"size="8" style="border-width:0; background-color:transparent;visibility: hidden;" onkeypress="return(onlyNumbers(event));"/>
							<br><input name="ME" id="ME"  value="Monto a Ejercer $" size="15" style="border-width:0; background-color:transparent; visibility: hidden;" onkeypress="return(onlyNumbers(event));" />
							<input type="text" id="plurianualidadv" name="plurianualidadv" maxlength="15" style="width: 7em; visibility: hidden;" onkeypress="return(onlyNumbers2(event));" />
							</td> 
						</tr> 
						<tr> 
							<td align="right">Tipo de Procedimiento</td> 
							<td align="left" colspan="6"> 
								<select id="tipoAdjudicacion" name="tipoAdjudicacion" style="width: 22em;"> 
								</select> 
							</td>
						</tr> 
						 	<tr> 
							<td align="right">Existencias: </td> 
							<td align="left">
								<input type="text" id="descrip" name="descrip" readonly="readonly" style="border: 0px none ;background:#FEFEFE; width: 15em;" />  
								<input type="text" id="cantidad" name="cantidad" readonly="readonly" style="border: 0px none ;background:#FEFEFE; width: 1em;" />
								<input type="text" id="CVE_UNI" name="CVE_UNI" readonly="readonly" style="border: 0px none ;background:#FEFEFE; width: 5em;" />
							</td> 
						</tr> 
						<tr> 
							<td width="33%"> 
								&nbsp; 
							</td> 
							<td align="left"> 
								<input type="button" id="pbCopiarEdita" name="pbCopiarEdita" disabled value="Copiar" class="btnInterfaceBG"/>
								&nbsp;&nbsp;&nbsp; 
								<input type="button" id="pbGuardaEdita" name="pbGuardaEdita" value="Guardar" class="btnInterfaceBG"/> 
							</td> 
						</tr> 
							 <tr>
							 <td align="right" colspan="5">
							  Auto Ajuste PAAS 
						        </td>   
							 </tr>
						<tr>	
						  <td align="right" colspan="8">
								<input type="button" id="autoAjustePAAS" value="PARCIAL" class="btnInterfaceBG"/> 
						  </td>
						 </tr>
                    </table> 
			<br /> 
			<table id="tblcucopPeriodo" class="display"> 
            <thead> 
                <tr> 
                	<th>Trimestre</th>
                	<th>Cantidad Disponible</th>
                	<th>Cantidad</th>	                     
                    <th>Precio Unitario</th> 
                    <th>Importe Bruto</th> 
                    <th>Importe Neto</th> 
                    <th>Cantidad En solicitudes</th> 
                    <th style="display: none;">Cantidad Disponible</th> 
                    <th>Monto en solicitudes</th>	 
                    <th>Monto Disponible</th>
                    <th>Mantener</th>		                     
              </tr> 
            </thead> 
        </table> 
          <br /> 
         <br /> 
        <table id="tblDisponible" class="display" > 
            <thead> 
                <tr> 
                	<th>UE</th>
                	<th>Partida</th>
                	<th>Monto Anual</th>	                     
                    <th>Enero</th> 
                    <th>Febrero</th> 
                    <th>Marzo</th> 
                    <th>Abril</th> 
                    <th>Mayo</th> 
                    <th>Junio</th>	 
                    <th>Julio</th>
                    <th>Agosto</th>
                    <th>Septiembre</th>	
                    <th>Octubre</th>	
                    <th>Noviembre</th>
                    <th>Diciembre</th>				                     
              </tr> 
            </thead> 
        </table> 
        <br /> 
         <br /> 
        
         
        </div>
        <input type="hidden" id="editaCIdSubPartida" name="editaCIdSubPartida"/>
        <input type="hidden" id="editaCIdCABM" name="editaCIdCABM"/>
        <input type="hidden" id="desUnidadResponsable1" name="desUnidadResponsable1"/>
        <input type="hidden" id="mCatalogoSubPartida" name="mCatalogoSubPartida" value="<%=editaCIdSubPartida%>"/>
        <input type="hidden" id="mesesCat" name="mesesCat" value=""/>
        <input type="hidden" id="cIdPartida" name="cIdPartida" value=""/>
        <input type="hidden" id="cUE" name="cUE" value=""/>
        <input type="hidden" id="cMes" name="cMes" value=""/>
        <input type="hidden" id="montoRemanente" name="montoRemanente" value=""/>
        <input type="hidden" id="mTechoPresupuestal" name="mTechoPresupuestal" value=""/>
        <input type="hidden" id="mMontoPartidaRestCUCOP" name="mMontoPartidaRestCUCOP" value="0"/>
        
    </form>
  </body>
</html>