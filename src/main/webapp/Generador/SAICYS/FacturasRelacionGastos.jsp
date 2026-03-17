<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	 String user_login=usuario.getLogin();
    String roles="";
    Map rol =usuario.getRoles();
    String cEjercicio = "";
	String cIdDocumento = "";
	String nIdEstado = "";
	String cCentroContable="";
	String destino_gasto="";
	String tconcepto="";
		
	if (session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio) != null) {
	  	cEjercicio =(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
		cIdDocumento= (String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
		nIdEstado=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEstado);
		destino_gasto=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosDESTINO_GASTO);
		tconcepto=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosTIPO_CONCEPTO);
		}
	
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	/*
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	*/
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Facturas Relacion de Gastos</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
	
	
		<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
		</style>
	
	
	<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />
	
	
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			

			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
		</style>
	
	
	
	
	
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
	    var importeTotalDocumento;
	    var vEstado;
	    $(document).ready(function() {
		
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Relacion Gastos","FacturasRelacionGastos");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			init();
			activaDesactivaPestanas();
			
			//condiciones iniciales
		   vEstado=parseInt($("#nIdEstadoCaratula").val(),10);
		   setInitConditions();
			$( "#pbAgregar" ).button().click(function() {
				       //se checa que todos los campos esten llenos
			            var fFactura=$('#fechaFactura');
						var cSerie=$('#cSerie');
						var razonSocial=$('#razonSocial');
						var mImporteTotalFact=$('#mImporteTotalFact');
						allFields = $( [] ).add(fFactura) .add(cSerie).add(razonSocial).add(mImporteTotalFact),
						tips = $( ".validateTips" );
						var bValid = true;
					    tips.text("");
					    allFields.removeClass( "ui-state-error" );
					    bValid = bValid&& checkRequerido(fFactura, "Fecha de la Factura");
						bValid = bValid&& checkRequerido(cSerie, "Folio de la Factura");
						bValid = bValid&& checkRequerido(razonSocial, "Razon Social Factura");
					    bValid = bValid&& checkRequerido(mImporteTotalFact, "Importe Neto Total de la Factura");
					    //si existe un campo vacio ser regresa
				   			 if(!bValid)
				    		return;
				    		
						    //valida año de factura que se desea ingresar
						    var yearFactura=$("#fechaFactura").val();
						    var res=yearFactura.split("\/")
						    //valida que el año de la fecha que se le esta ingresando sea el mismo que el que se capturo en nuevo
						     var fechaFacturaSplit=res[2];
						    //obtenemos año de factura capturado en la relacion de gastos
						     queryFormPost("yearFacturaRelacionGastos", {async:false});
						     if(fechaFacturaSplit != $("#cEjercicioFactura").val()){
						      alert("El año de la factura que desea ingresar no es el mismo que se capturo al momento de crear la relación de gastos")
						     return;
						    }
						  
						
					         //valida si la factura esta repetida en la relacion actual
						     var factura= validaFacturaRepetida();
						     if(factura){
						     alert("La factura que desea ingresar ya fue capturada previamente en la actual Relación de Gastos");
							 //se limpia formulario
							 $(".headerForm").val("");
						     return;
						     }
						    
						      var importe=parseFloat(quitaFmt($("#mImporteTotalFact").val()));
							  $("#mImporteFacturaCapturada").val(importe);
						   
							//verificar que el monto ingresado por linea no sobrepase el total que se ingreso en la caratula
							 queryFormPost("verificaTotalFacturasRelacionGastos", {async:false});
														 
							 if(parseFloat(quitaFmt($("#totalFactCapturado").val())) > parseFloat(quitaFmt($("#mImporteNeto").val()))){
							 alert("El monto total de la factura mas los montos capturados en otras facturas es mayor al monto que se capturo cuando se creo la Relacion de Gastos");
							 $("#mImporteTotalFact").val("");
						   	 return;
							 
							 }
						
						//inserta en tabla mRelacionGastosFacturaLineas
						if($("#cSerieFactura").val()!=""){
						$("#cIdFactura").val($("#cSerieFactura").val()+"-"+$("#cSerie").val());
						}else
						$("#cIdFactura").val($("#cSerie").val());
						//obtiene maximo consecutivo
						queryFormPost("maximoFacturasRELGEncabezado", {async:false});
						queryFormPost("insertaDatosTablaFacturasRELGEncabezado", {async:false});
						//actualiza los montos que se cargaron a la tabla
						queryFormPost("checaMontoCapturadoFacturas",{async:false});
						cargaFacturasEncabezado();
						//se limpia formulario para capturar otro registro
						 $(".headerForm").val("");
						
						
					});
					
					
					
					
					$( "#pbGuardarEncabezado" ).button().click(function() {
						var importeTotalFacturasTable=0.0;			
					  	var importeTotalFacturas=0.0;	 
						var aTrs = $("#grdValidaFacturasEncabezado").dataTable().fnGetNodes();
						for ( var i=0 ; i < aTrs.length ; i++ ){
						aData =  $("#grdValidaFacturasEncabezado").dataTable().fnGetData(aTrs[i]);
						$("#folioHidden").val(aData[0]);
				        $("#facturaHiddenEncabezado").val(aData[9]);
				        //$("#importeFacturaHiddenEncabezadoANT").val(aData[8]);
						$("#importeFacturaHiddenEncabezado").val($("#mImporteNeto_"+aData[0]+"_"+aData[7]+"").val());
						queryFormPost("mImporteTotalDetalle", {async:false});
						var importeFactura=parseFloat(quitaFmt($("#importeFacturaHiddenEncabezado").val()));
				        var importetotalFac=parseFloat(quitaFmt($("#mTotalFacturaDetalleEncabezadoUpdate").val()));
				        if(!validaMontos(importeFactura, importetotalFac)){
						alert("El monto de la factura "+$("#facturaHiddenEncabezado").val() +" es diferente a la suma de su detalle");
						$("#mImporteNeto_"+aData[0]+"_"+aData[7]+"").val(aData[8]);
						$("#mImporteNeto_"+aData[0]+"_"+aData[7]+"").formatCurrency();
						continue;	
				        }
				        
				      }  
				      
						var importeBd=0.0;
						var folios="'";
						for ( var i=0 ; i < aTrs.length ; i++ ){
						aData =  $("#grdValidaFacturasEncabezado").dataTable().fnGetData(aTrs[i]);
						$("#importeFacturaHiddenEncabezado").val($("#mImporteNeto_"+aData[0]+"_"+aData[7]+"").val());
						importeTotalFacturasTable=importeTotalFacturasTable + parseFloat(quitaFmt($("#importeFacturaHiddenEncabezado").val()));
						folios=folios+aData[9]+"','";
						}
						//se le quita el ,' que le queda al final a la cadena	
						 folios=folios.substring(0, folios.length-2);
					 	var where = " where nFolioRelacionGastos="+$("#folioHidden").val()+" and cIdFactura not in ("+folios+")";
						var szWhere = "";
						var szTabla = "MONTOMODIFICADORELG";
						$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
			          		var disponible = 0.0;
			           		for (var i = 0; i < j.length; i++) {
								importeBd=j[i].Col0;
							} 
						
							var granTotal=parseFloat(importeTotalFacturasTable)+ parseFloat(importeBd);
							var totalSumado=granTotal.toFixed(2); 
						   	if (totalSumado > importeTotalDocumento){
						   	    alert("La suma de los montos de las facturas sobrepasa el monto total  del documento");
								 cargaFacturasEncabezado(); 
								return -1;	
						   	  
						   	}else{
					   	
						   		for ( var i=0 ; i < aTrs.length ; i++ ){
								aData =  $("#grdValidaFacturasEncabezado").dataTable().fnGetData(aTrs[i]);
								$("#folioHidden").val(aData[0]);
								$("#facturaHiddenEncabezado").val(aData[9]);
								$("#fechaHiddenEncabezado").val($("#fechaFactura_"+aData[0]+"_"+aData[7]+"").val());
								$("#importeHiddenEncabezado").val($("#mImporteNeto_"+aData[0]+"_"+aData[7]+"").val());
								queryFormPost("updateFacturasEncabezadoRELG", {async:false});
								}
								 alert("La actualización se realizo correctamente")
							     cargaFacturasEncabezado(); 
							     queryFormPost("checaMontoCapturadoFacturas",{async:false});
							   	 //muestra montos x factura
								queryFormPost("checaMontoCapturadoFacturaEncabezado",{async: false }); 
								queryFormPost("checaMontoCapturadoFacturaDetalle",{async: false }); 
							}
						
				   
						});
						
						});
						
						
					
					$( "#pbGuardarDetalle" ).button().click(function() {
					    var importeTotalFactura=parseFloat(quitaFmt($("#lblImporteNetoCapturadoDetalle").val()));
						var importeTotalFacturasTable=0.0;
																	
						if($("#DESTINO_GASTO").val() == "CD"){	
							var aTrs = $("#grdValidaFacturasDetalleConsumoDirectoTabla").dataTable().fnGetNodes();
							var importeBd=0.0;
							var consecutivos="";
							for ( var i=0 ; i < aTrs.length ; i++ ){
								aData =  $("#grdValidaFacturasDetalleConsumoDirectoTabla").dataTable().fnGetData(aTrs[i]);
								$("#folioHidden").val(aData[0]);
						        $("#facturaHiddenDetalle").val(aData[8]);
								$("#importeFacturaHiddenDetalle").val($("#mImporteNetoDetalle_"+aData[0]+"_"+aData[7]+"").val());
								importeTotalFacturasTable=importeTotalFacturasTable + parseFloat(quitaFmt($("#importeFacturaHiddenDetalle").val()));
								consecutivos=consecutivos+aData[7]+","; 
								
							}
							consecutivos=consecutivos.substring(0, consecutivos.length-1);
							var where = " where nFolioRelacionGastos="+$("#folioHidden").val()+" and cIdFactura='"+$("#facturaHiddenDetalle").val()+"' and consecutivoFacturaDetalle not in ("+consecutivos+")";
							var szWhere = "";
							var szTabla = "MONTOMODIFICADODETALLERELG";
											
							$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
				          		var disponible = 0.0;
				           		
								for (var i = 0; i < j.length; i++) {
									importeBd=j[i].Col0;
								}
							
								var granTotal=parseFloat(importeTotalFacturasTable)+ parseFloat(importeBd);
								var totalSumado=granTotal.toFixed(2);
								
							 	if (totalSumado > importeTotalFactura){
									alert("La suma de los montos del detalle sobrepasa el monto total neto de su factura");
									    cargaFacturasEncabezado(); 
										cargaFacturasDetalleConsumo();
										return;
							   	}else{
								   		var aTrs1 = $("#grdValidaFacturasDetalleConsumoDirectoTabla").dataTable().fnGetNodes();
								   		var aData1;
								   		for ( var x=0 ; x < aTrs1.length ; x++ ){
											aData1 = $("#grdValidaFacturasDetalleConsumoDirectoTabla").dataTable().fnGetData(aTrs1[x]);
											$("#consecutivoFacturaDetalle").val(aData1[7])
											$("#importeHiddenDetalleNeto").val(quitaFmt($("#mImporteNetoDetalle_"+aData1[0]+"_"+aData1[7]+"").val()));
											
											//valida si la clave tiene suficiencia
											$("#mImporteFactAUXHidden").val(quitaFmt($("#importeHiddenDetalleNeto").val()))
											$("#cIdTipoPartidaEditHidden").val(aData1[4])
											$("#claveEpHidden").val($("#claveEp_"+aData1[0]+"_"+aData1[7]+"").val())
											
										    queryFormPost("verificaSaldosEPFacturasRelacionGastosDetalleGuarda", {async:false});
										     if(parseInt($("#existeSaldo").val(),10)==1){
										    alert("La clave que desea ingresar ya no tiene Saldo Disponible en la partida "+$("#cIdTipoPartidaEditHidden").val()+" y la clave "+ $("#claveEpHidden").val());
										    return;
										    }
										
											
											$("#altaAlmacenariaHiddenDetalle").val('0||');
											$("#almacenHiddenDetalle").val("");
										
										    queryFormPost("updateFacturasDetalleRELG", {async:false});	
										}
										cargaFacturasEncabezado(); 
										cargaFacturasDetalleConsumo();
										queryFormPost("checaMontoCapturadoFacturas",{async:false});
										 //muestra montos x factura
										queryFormPost("checaMontoCapturadoFacturaEncabezado",{async: false }); 
										queryFormPost("checaMontoCapturadoFacturaDetalle",{async: false }); 
							   		}
							   
								});
							
							}else if($("#DESTINO_GASTO").val() == "AL"){
							var aTrs = $("#grdValidaFacturasDetalleTabla").dataTable().fnGetNodes();
							for ( var i=0 ; i < aTrs.length ; i++ ){
									aData =  $("#grdValidaFacturasDetalleTabla").dataTable().fnGetData(aTrs[i]);
									$("#importeFacturaHiddenDetalle").val(quitaFmt($("#mImporteNetoDetalle_"+aData[0]+"_"+aData[9]+"").val()));
									importeTotalFacturasTable=importeTotalFacturasTable + parseFloat(quitaFmt($("#importeFacturaHiddenDetalle").val()));
									if(!validaMontos(importeTotalFactura, importeTotalFacturasTable) ){
										alert("La suma de los montos del detalle sobrepasa el monto total neto de su factura");
										 cargaFacturasEncabezado(); 
										 cargaFacturasDetalle();
										return -1;	
										} 
								}
							
							for ( var i=0 ; i < aTrs.length ; i++ ){
										aData =  $("#grdValidaFacturasDetalleTabla").dataTable().fnGetData(aTrs[i]);
										$("#folioHidden").val(aData[0]);
								        $("#facturaHiddenDetalle").val(aData[10]);
									    $("#consecutivoFacturaDetalle").val(aData[9])
										$("#importeHiddenDetalleNeto").val(quitaFmt($("#mImporteNetoDetalle_"+aData[0]+"_"+aData[9]+"").val()));
										
										//valida si la clave tiene suficiencia
											$("#mImporteFactAUXHidden").val(quitaFmt($("#importeHiddenDetalleNeto").val()))
											$("#cIdTipoPartidaEditHidden").val(aData[6])
											$("#claveEpHidden").val($("#claveEp_"+aData[0]+"_"+aData[9]+"").val())
											
										    queryFormPost("verificaSaldosEPFacturasRelacionGastosDetalleGuarda", {async:false});
										    if(parseInt($("#existeSaldo").val(),10)==1){
										    alert("La clave que desea ingresar ya no tiene Saldo Disponible en la partida "+$("#cIdTipoPartidaEditHidden").val()+" y la clave "+ $("#claveEpHidden").val());
										    return;
										    }
										
										
						         	     queryFormPost("updateFacturasDetalleSinAlmacenRELG", {async:false});	
									
					  			 }
						  			 cargaFacturasEncabezado(); 
									 cargaFacturasDetalle();
									 queryFormPost("checaMontoCapturadoFacturas",{async:false});
									  //muestra montos x factura
									queryFormPost("checaMontoCapturadoFacturaEncabezado",{async: false }); 
									queryFormPost("checaMontoCapturadoFacturaDetalle",{async: false }); 
		    				}
		    				activaDesactivaPestanas();
											
					});
					
					
					
					
					$( "#pbAgregarDetalle" ).button().click(function() {
					  //se checa que todos los campos esten llenos
			            // var almacen=$('#ALM');
			            var almacen=$('#cAlmacen');
			            var altaAlmacen=$('#altaAlmacen');
			            var mImporteFact=$('#mImporteFact');
			            var cIdTipoPartida=$('#cIdTipoPartida');
			            var claveEp=$('#claveEp');
			           
						if($("#DESTINO_GASTO").val() == "AL"){
						allFields = $( [] ).add(almacen) .add(altaAlmacen).add(mImporteFact).add(cIdTipoPartida).add(claveEp),
						tips = $( ".validateTips" );
						var bValid = true;
					    tips.text("");
					    allFields.removeClass( "ui-state-error" );
					    bValid = bValid&& checkRequerido(almacen, "Almacen");
						bValid = bValid&& checkRequerido(altaAlmacen, "Alta Almacenaria");
						bValid = bValid&& checkRequerido(mImporteFact, "Importe Neto del Detalle");  
						bValid = bValid&& checkRequeridoPartida(cIdTipoPartida, "Partida");  
						bValid = bValid&& checkRequerido(claveEp, "Clave presupuestal");
						 //si existe un campo vacio ser regresa
				   			 if(!bValid)
				    			return;
				    						    				
						}else{
						allFields = $( [] ).add(mImporteFact).add(cIdTipoPartida).add(claveEp),
						tips = $( ".validateTips" );
						var bValid = true;
					    tips.text("");
					    allFields.removeClass( "ui-state-error" );
					 	bValid = bValid&& checkRequerido(mImporteFact, "Importe Neto del Detalle");  
						bValid = bValid&& checkRequeridoPartida(cIdTipoPartida, "Partida");  
						bValid = bValid&& checkRequerido(claveEp, "Clave presupuestal");
					    //si existe un campo vacio ser regresa
				   			 if(!bValid)
				    		return;
					    
						}
						
						
						//verifica que los montos ingresados en el detalle no sean mayores al total de la factura
						var montoXFactura=parseFloat(quitaFmt($("#lblImporteNetoCapturadoDetalle").val()))
						var montoXFacturaDetalle=parseFloat(quitaFmt($("#mImporteFact").val()))
						var montoTotalFacturaDetalle=parseFloat(quitaFmt($("#mTotalFacturaV").val()))
						var sumaFacturaDetalle=montoXFacturaDetalle+montoTotalFacturaDetalle
						//redondeamos
						var total=sumaFacturaDetalle.toFixed(2);
						
						if(total > montoXFactura){
						alert("La suma de los Montos de los detalles Capturados es mayor al monto total de la factura");
						$(".detalleForm").val("")
						return;
						
						}
						
						$("#mImporteFactAUX").val(quitaFmt($("#mImporteFact").val()))
						//verifica que la clave que se ingresa tenga suficiencia
					    queryFormPost("verificaSaldosEPFacturasRelacionGastosDetalle", {async:false});
					    if(parseInt($("#existeSaldo").val(),10)==1){
					    alert("La clave que desea ingresar ya no tiene Saldo Disponible");
					    $(".detalleForm").val("")
					    return;
					    }
					    
						    //obtiene maximo consecutivo
						   queryFormPost("maximoFacturasRELG", {async:false});
						   if($("#DESTINO_GASTO").val() == "AL"){
						   
							//verifica que el detalle que se va a ingresar no se repita
							queryFormPost("verificaClavesPrimariasRELGDetalle", {async:false});
							if(parseInt($("#existeRestriccion").val(),10)> 0){
								alert("El registro que desea Ingresar ya se encuentra capturado y no se puede repetir");
								//limpia formulario de detalle
								$(".detalleForm").val("");
								return;
							}
						  
						   
						   $("#cIdAltaAlmacenaria").val($("#altaAlmacen").val()+'|'+$("#cAnioFactEP").val()+'|'+$("#cIdFacturaHeader").val())
						   	 queryFormPost("insertaDatosTablaFacturasRELGDetalle", {async:false});
						   	 cargaFacturasDetalle();
						   }else{
						   	$("#cIdAltaAlmacenaria").val("0||");
						   	$("#ALMAUX").val("");
						   	
							 //verifica que el detalle que se va a ingresar no se repita
							queryFormPost("verificaClavesPrimariasRELGDetalleConsumo", {async:false});
							if(parseInt($("#existeRestriccion").val(),10)> 0){
							alert("El registro que desea Ingresar ya se encuentra capturado y no se puede repetir");
							//limpia formulario de detalle
							$(".detalleForm").val("");
							return;
							}
							
						   	queryFormPost("insertaDatosTablaFacturasRELGDetalleConsumo", {async:false});
						    cargaFacturasDetalleConsumo();
						    }
							queryFormPost("checaMontoCapturadoFacturaDetalle",{async: false }); 
							
						
					});
					
				     cargaFacturasEncabezado();
				     
				     
			
				     
			$( "#dialog" ).dialog({
			autoOpen: false,
			minWidth: 500,
			draggable: false,
			modal: true,
			buttons: { 
				"Cancelar": function() {
				    alert("Si desea generar el reporte es necesario elegir una clabe bancaria");
					$( this ).dialog( "close" );
					return;
				},
				"Aceptar": function() {
				if(parseInt($("#clableInter").val(),10)==0){
				  alert("Si desea generar el reporte es necesario elegir una clabe bancaria");
				  $( this ).dialog( "close" );
					return;
				}
							
			    window.open("../../servlet/SeguridadCatalogosMateriales?" + "catalogo=REPORTE"
				+ "&accion=run&rn=relacionGastosReporteFacturas.jasper&cIdDocumento=" + $("#cIdFolio").val()+"&clabeInterBancaria="+ $("#clableInter").val(),
				"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		        $( this ).dialog( "close" );
		        
				}
			}
		});
		
				     
		
		});
		
	
		function init(){
		    queryFormPost("llenaCaratulaRelacionGastos", {async:false});
			queryFormPost("mContratoPagadoReadRelacionGastos", {async: false});
			queryFormPost("mContratoPorPagarReadRelacionGastos", {async: false});
			queryFormPost("checaMontoTotalLineas",{async:false});
			queryFormPost("checaMontoCapturadoFacturas",{async:false});
			agregaFecha();
			
			//se oculta o muestra el alta almacenaria dependiendo del destino del gasto
			 if($("#DESTINO_GASTO").val() == "AL"){
				$("#altaAlmacenariaRow").css("display","block");
				queryFormPost("obtineAlmacenCapturadoNuevaRELG",{async:false});
				//querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
				
			} else
			    $("#altaAlmacenariaRow").css("display","none");
				//obtenemos cIdFactura de la relacion de gastos actual
				queryFormPost("obtineIDFacturaRelacionGastosActual",{async:false});
				importeTotalDocumento=parseFloat(quitaFmt($("#mImporteNeto").val()));
				
           	//ocultamos divicion detalle solo se mostrara al hacer doble click algun renglon del encabezado 
			$("#datosDetalleFacturas").css("display","none");
			
			
		}
		
		function cargaFacturasDetalleConsumo(){
		var zWhere="'"+$("#nFolioConsecutivoRelacionGastosCaratula").val()+"','"+$("#cIdFacturaHeader").val()+"','"+$("#cCentroContable").val()+"','"+$("#cIdUnidadEjecutora").val()+"'";
		/*
		var where= " WHERE cTCONC='"+$("#TIPO_CONCEPTO").val()+"' AND ID_DESTINO_GASTO='"+$("#DESTINO_GASTO").val()+"'";
		var optionsTem = "", options = "";
			$.getJSON("../../catalogos/SelectJson.jsp", {Tabla: "PAGODIRECTOPARTIDA",MaxReg:where,ajax: false, async: true},
				function(data){
					for(var i=0; i < data.length; i++)
						options+="<option value='"+data[i].Col1+"' #"+data[i].Col1+"##>"+data[i].Col0+"</option>";
			});
	
         */
			 $("#grdValidaFacturasDetalleConsumoDirectoTabla").dataTable({
				//sScrollY : "100%",
			//	sScrollX: "500",
				sScrollX: "110%",
			//	sScrollXInner: "200%",
				bScrollCollapse: true,
        		bInfo: false,
				bAutoWith: false,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mRelacionGastosFacturasDetalleConsumo("+zWhere+")",
				bJQueryUI: true,
				aaSorting: [[ 7, "asc" ]] ,
				aoColumns: [{ sName: "nFolioRelacionGastos", bVisible:false },
						    { sName: "cSerie" },{ sName: "cfactura" },
							//{ sName: "almacen" },{ sName: "altaAlmacenaria" },
							{ sName: "mImporteTotalFacturaDetalle" },
							{ sName: "partida"},{ sName: "claveEp" },
							{sName:"eliminaFactura"},
							{sName:"consecutivoFacturaDetalle",bVisible:false},{sName:"cIdFactura",bVisible:false}]
							
				    /*			
				    fnRowCallback: function( nRow, aData, iDisplayIndex ){
				    var idDocumento=aData[0];
					var consecutivoFactura=aData[7];
					optionsTem = options;
					optionsTem = optionsTem.replace("#"+aData[4]+"##","selected");
					optionsTem = optionsTem.replace(optionsTem.substring(optionsTem.indexOf("#"),optionsTem.indexOf("##")+2),"");
					$("td:eq(3)", nRow).html("<select id='cIdTipoPartida_"+idDocumento+"_"+consecutivoFactura+"' style='width:30em;' onChange='editaPartida(this,"+consecutivoFactura+")'>"+optionsTem+"</select>");
					$("#cIdTipoPartida_"+idDocumento+"_"+consecutivoFactura+"").val(aData[4]);
					return nRow;
				}  
		      */
				 
			} );
			
		}
		
		
		
		function cargaFacturasDetalle(){
		var zWhere="'"+$("#nFolioConsecutivoRelacionGastosCaratula").val()+"','"+$("#cIdFacturaHeader").val()+"','"+$("#cCentroContable").val()+"','"+$("#cIdUnidadEjecutora").val()+"'";
		
		/*
		//var where= " WHERE cTCONC='"+$("#TIPO_CONCEPTO").val()+"' AND ID_DESTINO_GASTO='"+$("#DESTINO_GASTO").val()+"'";
		var optionsTem = "", options = "";
			$.getJSON("../../catalogos/SelectJson.jsp", {Tabla: "PAGODIRECTOPARTIDA",MaxReg:where,ajax: false, async: true},
				function(data){
					for(var i=0; i < data.length; i++)
						options+="<option value='"+data[i].Col1+"' #"+data[i].Col1+"##>"+data[i].Col0+"</option>";
			});
	     */
	     
			 $("#grdValidaFacturasDetalleTabla").dataTable({
				//sScrollY : "100%",
			//	sScrollX: "500",
				sScrollX: "110%",
			//	sScrollXInner: "200%",
				bScrollCollapse: true,
        		bInfo: false,
				bAutoWith: false,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mRelacionGastosFacturasDetalle("+zWhere+")",
				bJQueryUI: true,
				aaSorting: [[ 9, "asc" ]] ,
				aoColumns: [{ sName: "nFolioRelacionGastos", bVisible:false },
						    { sName: "cSerie" },{ sName: "cfactura" },
							{ sName: "almacen" },{ sName: "altaAlmacenaria" },
							{ sName: "mImporteTotalFacturaDetalle" },
							{ sName: "partida"},{ sName: "claveEp" },
							{sName:"eliminaFactura"},
							{sName:"consecutivoFacturaDetalle",bVisible:false},{sName:"cIdFactura",bVisible:false}]
							
					/*		
				    fnRowCallback: function( nRow, aData, iDisplayIndex ){
				    var idDocumento=aData[0];
					var consecutivoFactura=aData[9];
					optionsTem = options;
					optionsTem = optionsTem.replace("#"+aData[6]+"##","selected");
					optionsTem = optionsTem.replace(optionsTem.substring(optionsTem.indexOf("#"),optionsTem.indexOf("##")+2),"");
					$("td:eq(5)", nRow).html("<select id='cIdTipoPartida_"+idDocumento+"_"+consecutivoFactura+"' style='width:30em;' onChange='editaPartida(this,"+consecutivoFactura+")'>"+optionsTem+"</select>");
					$("#cIdTipoPartida_"+idDocumento+"_"+consecutivoFactura+"").val(aData[6]);
					return nRow;
				}  
		      */
				 
			} );
			
		}
		
		
		  $("#grdValidaFacturasEncabezado tr").live("dblclick", function() {
		  
		   //selecciona renglon de la tabla
		   if ($(this).hasClass('row_selected')){
		   $(this).removeClass('row_selected'); 
		   }else   
           $(this).addClass('row_selected'); 
		   //mostram dosivicion detalle solo se mostrara al hacer doble click algun renglon del encabezado 
			$("#datosDetalleFacturas").css("display","block");
			$(".detalleForm").val("")
			
			
			 if($("#DESTINO_GASTO").val() == "AL"){
			 $("#tipoAlmacen").css("display","block");
			 $("#tipoConsumo").css("display","none");
			 var aTrs =oTableEncabezado.dataTable().fnGetNodes();
			for ( var i=aTrs.length ; i>=0; i-- ){         
						if ( $(aTrs[i]).hasClass('row_selected'))         
						{
			             $(this).removeClass('row_selected'); 
						  aData = oTableEncabezado.fnGetData(aTrs[i]);
						 $("#cFacturaHeaderAux").val($("#cfactura_"+aData[0]+"_"+aData[7]+"").val());
						 if( $("#cFacturaHeaderAux").val()=="" || $("#cFacturaHeader").val()=="0"){
						  $("#cIdFacturaHeader").val($("#cSerie_"+aData[0]+"_"+aData[7]+"").val());
						 }else
	                     $("#cIdFacturaHeader").val($("#cfactura_"+aData[0]+"_"+aData[7]+"").val()+'-'+$("#cSerie_"+aData[0]+"_"+aData[7]+"").val());
	                    cargaFacturasDetalle();
	                     buscaPartida();
	                   			
			            }
			 }
			 
			}else{
			         $("#tipoConsumo").css("display","block");
			          $("#tipoAlmacen").css("display","none");
			         var aTrs =oTableEncabezado.dataTable().fnGetNodes();
			          for ( var i=aTrs.length ; i>=0; i-- ){         
						if ( $(aTrs[i]).hasClass('row_selected'))         
						{
			             $(this).removeClass('row_selected'); 
						  aData = oTableEncabezado.fnGetData(aTrs[i]);
						 $("#cFacturaHeaderAux").val($("#cfactura_"+aData[0]+"_"+aData[7]+"").val());
						 if( $("#cFacturaHeaderAux").val()=="" || $("#cFacturaHeader").val()=="0"){
						  $("#cIdFacturaHeader").val($("#cSerie_"+aData[0]+"_"+aData[7]+"").val());
						 }else
	                     $("#cIdFacturaHeader").val($("#cfactura_"+aData[0]+"_"+aData[7]+"").val()+'-'+$("#cSerie_"+aData[0]+"_"+aData[7]+"").val());
	                     cargaFacturasDetalleConsumo();
	                     buscaPartida();
	                   			
			            }
			 }
			
			
			
			} 
			 
			 //muestra montos x factura
			queryFormPost("checaMontoCapturadoFacturaEncabezado",{async: false }); 
			queryFormPost("checaMontoCapturadoFacturaDetalle",{async: false }); 
			
		  });
		
		
		
	
		
		function cargaFacturasEncabezado(){
		var zWhere="'"+$("#nFolioConsecutivoRelacionGastosCaratula").val()+"'";
		oTableEncabezado= $("#grdValidaFacturasEncabezado").dataTable({
				//sScrollY : "100%",
			//	sScrollX: "500",
				sScrollX: "110%",
			//	sScrollXInner: "200%",
				bScrollCollapse: true,
        		bInfo: false,
				bAutoWith: false,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mRelacionGastosFacturasEncabezado("+zWhere+")",
				bJQueryUI: true,
				aaSorting: [[ 10, "asc" ]] ,
				aoColumns: [{ sName: "nFolioRelacionGastos", bVisible:false },{ sName: "fechaFactura" },
						    { sName: "cSerie" },{ sName: "cfactura" },
						    { sName: "cRazonSocial" },
						  	{ sName: "mImporteTotalFactura" },
							 {sName:"eliminaFactura"},{ sName: "consecutivoFacturaEncabezado", bVisible:false },{ sName: "montoActual", bVisible:false },{ sName: "cIdFactura", bVisible:false },{ sName: "consecutivoFacturaEncabezadohidden", bVisible:false }]
						
			                });
			
	        	}
		
		
		
		function cargaConsumoDirecto(){
				
		}
		
		
			
		function buscaClave(){
		$("#cIdTipoPartidaEdit").val($("#cIdTipoPartida").val());
			querySelectPost("mCatalogoClaveEpRelacionGastosFacturas1", "claveEp", {async: false }); 
		}
		function buscaPartida(){
			querySelectPost("mPartidaRelacionGastosFacturas", "cIdTipoPartida", {async: false });
		}
	
		function editaPartida(partida,consecutivo){
		
		 var documento=$("#nFolioConsecutivoRelacionGastosCaratula").val()
		 $("#cIdTipoPartidaEdit").val(partida.value);
		 querySelectPost("mCatalogoClaveEpRelacionGastosFacturas1", "claveEp_"+documento+"_"+consecutivo, {async: false }); 
		}
		
		
		function eliminaFacturaEncabezado(folio,consecutivoFactura){
		      if (!window.confirm("Esta seguro de eliminar la factura Capturada esta acción eliminara sus detalles si es que los tiene?"))
				return;
				
			$("#consecutivoFactura1").val(consecutivoFactura);
			$("#folioAUX").val(folio);
			queryFormPost("eliminaDetalleFacturasRelacionGastos", {async:false});
			queryFormPost("eliminaEncabezadoFacturasRelacionGastos", {async:false});
			queryFormPost("updateConsecutivoRelacionGastosFacturasEncabezado", {async:false});
			cargaFacturasEncabezado();
			queryFormPost("checaMontoCapturadoFacturas",{async:false});
			location.reload();
			
		} 
		
		
		
		function eliminaFacturaDetalle(folio,consecutivoFactura,factura){
		  if (!window.confirm("Esta seguro de eliminar el detalle de la factura "+factura+"?"))
				return;
			$("#consecutivoFactura1Detalle").val(consecutivoFactura);
			$("#folioAUXDetalle").val(folio);
			$("#facturaAUXDetalle").val(factura);
			queryFormPost("eliminaDetalleFacturasRelacionGastos1", {async:false});
			queryFormPost("updateConsecutivoRelacionGastosFacturasDetalle", {async:false});
			 if($("#DESTINO_GASTO").val() == "AL"){
			 cargaFacturasDetalle();
			 }else
			 cargaFacturasDetalleConsumo();
			 
			 //muestra montos x factura
			queryFormPost("checaMontoCapturadoFacturaDetalle",{async: false }); 
			location.reload();
			
		} 
		
		
		
		function onlyNumbersAndLetters(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = "!@#$%^&*()´+=-[]\\';,./{}|\":<>?";
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
			    //fue ingresado un caracter valido
				return true; // Valida que sea numero y punto decimal
		       	//fue ingresado un caracter especial
				return false 
			}
			
		function agregaFecha(){
			 $("#fechaFactura").datepicker({
			 	beforeShowDay: nonWorkingDates,						
				dateFormat: "dd/mm/yy",
				currentText: "Now",
				showOn: 'button',
				altField: "#actualDate",
				buttonImageOnly: true,	
			    buttonImage: '../images/calendar.gif',			    					 
				changeYear: true
				});
		}
		///Desabilita sabados y domingos del datepicker
			 function nonWorkingDates(date){
		        var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
		        //var closedDates = [[7, 29, 2009], [8, 25, 2010]];
		        var closedDays = [[Sunday], [Saturday]];
		        for (var i = 0; i < closedDays.length; i++) {
		            if (day == closedDays[i][0]) {
		                return [false];
		            }
		
		        }
		
	// 	        for (i = 0; i < closedDates.length; i++) {
	// 	            if (date.getMonth() == closedDates[i][0] - 1 &&
	// 	            date.getDate() == closedDates[i][1] &&
	// 	            date.getFullYear() == closedDates[i][2]) {
	// 	                return [false];
	// 	            }
	// 	        }
		
		        return [true];
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
			
		function checkRequeridoPartida	(o, n) {
			var sTemp = $.trim(o.val());
			o.val(sTemp);
			if (sTemp =="0") {
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
			alert(t);
		}
		
		
			function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		
		function validaFacturaRepetida(){
		     var retVal=false
			//inserta en tabla mRelacionGastosFacturaLineas
			
				if($("#cSerieFactura").val()!=""){
				$("#cIdFactura").val($("#cSerieFactura").val()+"-"+$("#cSerie").val());
				}else
				$("#cIdFactura").val($("#cSerie").val());
				  //verificar que la factura que se esta ingresando no este capturada en la relacion de gastos actual
					 queryFormPost("verificaIdFacturasCapturadasRelacionGastos", {async:false});
				  if(parseInt($("#cIdFacturaCheck").val(),10) > 0){
					 retVal= true;
				 	 }
				 	 
					return retVal;		 
		  		 }
		
	function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	var strCheck = '0123456789.';

	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1)
		return false; // Valida que sea numero y punto decimal

	return true 
	//!(keyPressed > 31 && (keyPressed < 48 || keyPressed > 57));
}



    function validaMontos(importeTotalDocumento,importeTotalFacturas){
		if(importeTotalDocumento >= importeTotalFacturas ){
	  		return true;
	  	}else{
	  		return false;
	  	}
	  }


		
   function generaReporte() {
    //revisamos si existen los dos firmantes antes de imprimir el reporte
    //queryFormPost("verificaTotalFacturasRelacionGastosInicio", {async:false});
    
     queryFormPost("verificaTotalFirmantesRelacionGastos", {async:false});
     if(parseInt($("#numFirmantes").val(),10)!= 2){
     alert("No se han capturado los firmantes,debe de capturarlos antes de generar el reporte");
     return;
     }
   
   
    //llenamos combobox de clave interbancaria
    querySelectPost("llenaClabeBancariaRELG", "clableInter", {async: false }); 
     $("#dialog").dialog("open");
   }
   
   
   
   function deshabilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",true);
	$("#partidasRelacionGastos").attr("disabled",true);
	$("#pagosRelacionGastos")	.attr("disabled", true);
	}
		
	function habilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",false);
	$("#partidasRelacionGastos").attr("disabled",false);
	$("#pagosRelacionGastos")	.attr("disabled",false);
	}
			
	 function setInitConditions(){
	    
  		if(vEstado==3 || vEstado==4 || vEstado==6  ){
  		    $('#grdValidaFacturasEncabezado').attr('disabled', true);
  		    $('#grdValidaFacturasDetalleTabla').attr('disabled', true);
  		    $('#grdValidaFacturasDetalleConsumoDirectoTabla').attr('disabled', true);
  		    document.getElementById("pbAgregar").disabled = true;
			document.getElementById("pbGuardarEncabezado").disabled = true;
			document.getElementById("pbAgregarDetalle").disabled = true;
			document.getElementById("pbGuardarDetalle").disabled = true;
	    }else if( vEstado==1 || vEstado==5 ){
	        $('#grdValidaFacturasEncabezado').attr('disabled', false);
  		    $('#grdValidaFacturasDetalleTabla').attr('disabled', false);
  		    $('#grdValidaFacturasDetalleConsumoDirectoTabla').attr('disabled', false);
  		    document.getElementById("pbAgregar").disabled = false;
			document.getElementById("pbGuardarEncabezado").disabled = false;
			document.getElementById("pbAgregarDetalle").disabled = false;
			document.getElementById("pbGuardarDetalle").disabled = false;
		}
				
		}		
		
		
	function activaDesactivaPestanas(){
	      var umbral;
		 //verificar que el monto ingresado por linea no sobrepase el total que se ingreso en la caratula
		 queryFormPost("verificaTotalFacturasRelacionGastosInicio", {async:false});
		 queryFormPost("checaMontoCapturadoFacturaDetalleTotal", {async:false});
		 queryFormPost("verificaMontosTotalesPartidasRELG", {async:false});
		 
		  deshabilitaTabs();
		  if(parseFloat(quitaFmt($("#totalFactCapturadoInicio").val())) == parseFloat(importeTotalDocumento)){
		 //se deshabilita el boton de agregar facturas encabezado
		  $("#pbAgregar").attr("disabled",true);
		  if(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) == parseFloat(importeTotalDocumento)){
		   $("#pbAgregarDetalle").attr("disabled",true);
		    $("#partidasRelacionGastos").attr("disabled",false);
		     umbral = Math.abs(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) - parseFloat($("#totalSumaPartidasRELG").val()));
		       if(umbral > .03){
				$("#apartadoRelacionGastos").attr("disabled",true);
	            $("#pagosRelacionGastos").attr("disabled",true);
				}else
			    habilitaTabs();
		   
	     }  
		 }
	    
	}	
		
	
	</script>
</head> 
<body id="dt_example" >
		<form>
		    		<fieldset style="width:750px">
							<legend>Informaci&oacute;n de la Relaci&oacute;n de Gastos</legend>
							<table border="0" align="center" width="100%">
						<tr  align="left">
							<td align="right">
								<img id="imgReporte" src="../../imagenes/icono_PDF.jpg" style="cursor: pointer" onclick="generaReporte();" />&nbsp;Reporte
							</td>
						</tr>	
							
								
					<tr align="left">
						<td colspan="2">
							<input name="lblcIdDocumento" id="lblcIdDocumento" type="text" style="width: 600px"  style="border: 0px solid black;" readonly="readonly"/>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" style="width: 600px" readonly="readonly" style="border: 0px solid black;"></input>  
						</td>								
					</tr>
					
					<tr align="left">
						<td colspan="2">
							<input name="lblnIdEstado" id="lblnIdEstado" type="text" readonly="readonly" style="border: 0px solid black; width: 45em;"></input>  
						</td>								
					</tr>
					<tr>
							<td align="left">
								Importe Neto Relacion Gastos:
								<input type="text" style="width: 500px" name="lblImporteNeto"
									id="lblImporteNeto" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					<tr>
							<td align="left">
								<!--Importe Neto Partidas:-->
								<input type="hidden" style="width: 500px" name="montoNetoRELG"
									id="montoNetoRELG" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					</table>
				</fieldset>
				 <br/>
				
				  <div id="datosEncabezadoFacturas">
				      <fieldset style="width:750px">
		    			<legend>Datos de las facturas</legend>
					<table width="100%" align="left" border='0'>
				    	<tr>
							<td align="right" >Fecha</td><td align="left"><input maxlength="15"  type="text" name="fechaFactura" id="fechaFactura" class="headerForm"/>
							<font color="red">*</font>
							</td>
						</tr>
						<tr>
							<td align="right" colspan="1">Serie Documento</td>
							<td align="left" >
								<input type="text" style="text-transform:uppercase; text-align: left;" onkeyup="$(this).val($(this).val().toUpperCase())"  name="cSerieFactura" id="cSerieFactura" value="" size="5" maxlength="15" class="headerForm" />
								Folio Documento
								<input type="text" style="text-transform:uppercase; text-align: left;" onkeyup="$(this).val($(this).val().toUpperCase())"  name="cSerie" id="cSerie" value="" size="20" maxlength="55"  class="headerForm"/>
								<font color="red">*</font>
							</td>
						</tr> 
								
						<!-- 													
						<tr id="altaAlmacenariaRow">
							<td align="left"  colspan="2">Almacen
							<select id="ALM" name="ALM" ></select>
							<font color="red">*</font>
							Alta Almacenaria
							<input type="text" id="altaAlmacen" name="altaAlmacen"  style="text-align: right;" onKeyPress="valFmt(this,9)" value="0" size="5" maxlength="12" class="headerForm">
							<font color="red">*</font>
							</td>	
						</tr>
					 -->		
						<tr>
							<td align="right">Razon Social</td>
							<td align="left">
								<textarea name="razonSocial" rows="2" cols="80" id="razonSocial" class="headerForm" style="text-transform:uppercase; text-align: left;" onkeyup="$(this).val($(this).val().toUpperCase())"   ></textarea><font color="red">*</font>
							</td>
						</tr>
						
						
						<tr>
							<td align="right">Importe Total Capturado</td>
							<td align="left">
							    <input type="text" style="text-align: left;"  name="lblImporteNetoCapturado" id="lblImporteNetoCapturado" size="15" maxlength="15" readonly="readonly" style="border: 0px none ; color: red;"  />
																	
							    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Importe Total Factura
								<input type="text" style="text-align: left;" onblur="$(this).formatCurrency();" onKeyPress="return(onlyNumbers(event));" name="mImporteTotalFact" id="mImporteTotalFact" value="" size="15" maxlength="15" class="headerForm" />
								<font color="red">*</font>
							</td>
						</tr>
						
							<tr>
							<td colspan="2" align="center">
								<input type="button" style="text-align: left;" id="pbAgregar" value="Agregar" />
							</td>
						</tr>
					
						</table>
				  </fieldset>
				  </div>
						<br/>
						
					   <div id="datosEncabezadoFacturasTabla">
				      <fieldset style="width:750px">
		    			<legend>Facturas Capturadas</legend>
					  <table   class="display" id="grdValidaFacturasEncabezado"  width="500px" >
						<thead>
							<tr>
								<th></th><!--folio  -->
								<th>Fecha</th>
								<th>Serie Factura</th>
								<th>Folio Factura</th>
								<th>Raz&oacute;n Social</th>
								<th>Importe Factura</th>
								<th></th> <!--elimina  -->
								<th></th> <!--consecutivo  --> 
								<th></th> <!--montoActual -->
								<th></th> <!--cIdFactura --> 
								<th></th> <!--consecutivoHidden -->     
							</tr>
						</thead>
						</table>
					
					  <table>
					  
						  <tr>
							<td colspan="2" align="center">
								<input type="button" style="text-align: left;" id="pbGuardarEncabezado" value="Guarda Cambios"/>
							</td>
						</tr>
						  </table>
										
					  </fieldset>
				  </div>
				 <br/>
										
					  <div id="datosDetalleFacturas">
				      <fieldset style="width:750px">
		    			
					<legend>Detalle de Facturas Capturadas</legend>
					<table width="100%" align="left" border='0'>
					<tr>
					<td align="right">Importe Total Factura</td>
					<td align="left">
					<input type="text" style="text-align: left;"  name="lblImporteNetoCapturadoDetalle" id="lblImporteNetoCapturadoDetalle" size="15" maxlength="15" readonly="readonly" style="border: 0px none ; color: red;"  />
					</tr>
					
					<tr id="altaAlmacenariaRow">
							<td align="left"  colspan="2">Almacen
							<input type="text" id=cAlmacen name="cAlmacen" readonly size="25" maxlength="25" disabled="disabled" />
							<!--  <select id="ALM" name="ALM" ></select>-->
							<font color="red">*</font>
							
							
							Alta Almacenaria
							<input type="text" id="altaAlmacen" name="altaAlmacen"  style="text-align: right;"  value="0" size="5" maxlength="12" class="detalleForm"/>
							<font color="red">*</font>
							</td>	
						</tr>
					   <tr>
							<td align="right">Importe Neto Detalle</td>
							<td align="left">
								<input type="text" style="text-align: left;" onblur="$(this).formatCurrency();" onKeyPress="return(onlyNumbers(event));" name="mImporteFact" id="mImporteFact" value="" size="15" maxlength="15" class="detalleForm" />
								<font color="red">*</font>
							</td>
						</tr>
						
						
						<tr>
							<td align="right">Partida</td>
							<td align="left">
								<select id="cIdTipoPartida" name="cIdTipoPartida" style="width: 35em;" onchange="buscaClave()"></select>
								<font color="red">*</font>
							</td>
						</tr>
						<tr>
							<td align="right">Clave Presupuestal</td>
							<td align="left">
								<select id="claveEp" name="claveEp" style="width: 40em;"></select>
								<font color="red">*</font>
							</td>
						</tr>
						 
						 <tr>
							<td colspan="2" align="center">
								<input type="button" style="text-align: left;" id="pbAgregarDetalle" value="Agregar"/>
							</td>
						</tr>
					
						
						
						</table>
					  </fieldset>
					  <br/>
					  	<fieldset  style="width:750px" >
					  	<legend>Tabla de Detalle de Facturas Capturadas</legend>
					      <div style="visibility: none" id="tipoAlmacen">
					    	<table   class="display" id="grdValidaFacturasDetalleTabla" >
							<thead>
							<tr>
								<th></th><!-- folio -->
								<th>Serie</th>
								<th>Factura</th>
								<th>Almacen</th>
								<th>Alta almacenaria</th>
								<th>Importe Neto Detalle</th>
								<th>Partida</th>
								<th>Clave Presupuestal</th>
								<th>Eliminar</th>
								<th></th> <!-- consecutivo --> 
								<th></th> <!-- cIdFactura --> 
	
							</tr>
						</thead>
					</table>
					 </div>
					 
					  <div style="visibility: none" id="tipoConsumo">
					
							<table   class="display" id="grdValidaFacturasDetalleConsumoDirectoTabla" height="50">
							<thead>
							<tr>
								<th></th><!-- folio -->
								<th>Serie</th>
								<th>Factura</th>
								<th>Importe Neto Detalle</th>
								<th>Partida</th>
								<th>Clave Presupuestal</th>
								<th>Eliminar</th>
								<th></th> <!-- consecutivo --> 
								<th></th> <!-- cIdFactura --> 
	
							</tr>
						</thead>
					</table>
					
					</div>
					Total de la factura en Detalle:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="15" maxlength="15" readonly/>
					
					<table>
					  
						  <tr>
							<td colspan="2" align="center">
								<input type="button" style="text-align: left;" id="pbGuardarDetalle" value="Guardar Cambios" />
							</td>
						</tr>
						  </table>
					
					
					</fieldset>
				  </div>
			
		<input id="cEjercicio" name="cEjercicio" type="hidden"  value="<%=cEjercicio%>" />
		<input id="cIdDocumentoCaratula" name="cIdDocumentoCaratula" type="hidden"  value="<%=cIdDocumento%>" />
		<input id="nIdEstadoCaratula" name="nIdEstadoCaratula" type="hidden" size="4" value="<%=nIdEstado%>" />
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=usuario.getU_UR() %>" />
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
		<input type="hidden" id="montoNetoRELG" name="montoNetoRELG" />
		<input id="cIdFolio" name="cIdFolio" type="hidden"  value="<%=cIdDocumento%>" />
		<input id="DESTINO_GASTO" name="DESTINO_GASTO" type="hidden"  value="<%=destino_gasto%>" />
		<input id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" type="hidden"  value="<%=tconcepto%>" />
		<input id="cCentroContable" name="cCentroContable" type="hidden"  value="<%=cCentroContable%>" />
		<input id="cIdTipoPartidaEdit" name="cIdTipoPartidaEdit" type="hidden"/>
		<input id="existeSaldo" name="existeSaldo" type="hidden"/>
		<input id="mImporteFacturaCapturada" name="mImporteFacturaCapturada" type="hidden"/>
		<input id="cIdFactura" name="cIdFactura" type="hidden"/>
		<input id="cIdAltaAlmacenaria" name="cIdAltaAlmacenaria" type="hidden"/>
		<input id="consecutivoFactura" name="consecutivoFactura" type="hidden"/>
		<input id="consecutivoFactura1" name="consecutivoFactura1" type="hidden"/>
		<input id="nFolioConsecutivoRelacionGastosCaratula" name="nFolioConsecutivoRelacionGastosCaratula" type="hidden"/>
		<input id="mImporteNeto" name="mImporteNeto" type="hidden"/>
		<input id="totalFactCapturado" name="totalFactCapturado" type="hidden"/>
		<input id="totalFactCapturadoInicio" name="totalFactCapturadoInicio" type="hidden"/>
		<input id="cIdFacturaCheck" name="cIdFacturaCheck" type="hidden"/>
		<input id="cAnioFactEP" name="cAnioFactEP" type="hidden"/>
		
		<input id="nFolioConsecutivoRelacionGastosHeader" name="nFolioConsecutivoRelacionGastosHeader" type="hidden"/>
		<input id="cIdFacturaHeader" name="cIdFacturaHeader" type="hidden"/>
		
		<input id="cFacturaHeader" name="cFacturaHeader" type="hidden"/>
		<input id="cFacturaHeaderAux" name="cFacturaHeaderAux" type="hidden"/>
		<input id="consecutivoFacturaEncabezado" name="consecutivoFacturaEncabezado" type="hidden"/>
		<input id="folioAUX" name="folioAUX" type="hidden"/>
		<input id="folioAUXDetalle" name="folioAUXDetalle" type="hidden"/>
		<input id="consecutivoFactura1Detalle" name="consecutivoFactura1Detalle" type="hidden"/>
		<input id="facturaAUXDetalle" name="facturaAUXDetalle" type="hidden"/>
		<input id="existeRestriccion" name="existeRestriccion" type="hidden"/>
		<input id="mImporteFactAUX" name="mImporteFactAUX" type="hidden"/>
		<input id="ALMAUX" name="ALMAUX" type="hidden"/>
		<input id="ALM" name="ALM" type="hidden"/>
		<input id="mTotalFacturaDetalleTotal" name="mTotalFacturaDetalleTotal" type="hidden"/>
		<input id="totalSumaPartidasRELG" name="totalSumaPartidasRELG" type="hidden"/>
		<input id="numFirmantes" name="numFirmantes" type="hidden"/>
		
		
		
		
		<!-- Auxiliares de edicion -->
	<input type="hidden" id="fechaHiddenEncabezado" name="fechaHiddenEncabezado"/> 
	<input type="hidden" id="importeHiddenEncabezado" name="importeHiddenEncabezado"/> 
	<input type="hidden" id="folioHidden" name="folioHidden"/> 
	
	
	
	
	<input type="hidden" id="facturaHiddenEncabezado" name="facturaHiddenEncabezado"/> 
	<input type="hidden" id="importeFacturaHiddenEncabezado" name="importeFacturaHiddenEncabezado"/>  
	<input type="hidden" id="importeFacturaHiddenEncabezadoANT" name="importeFacturaHiddenEncabezadoANT"/> 
	<input type="hidden" id="mTotalFacturaDetalleEncabezadoUpdate" name="mTotalFacturaDetalleEncabezadoUpdate"/>
		
	<input type="hidden" id="facturaHiddenDetalle" name="facturaHiddenDetalle"/> 
	<input type="hidden" id="importeFacturaHiddenDetalle" name="importeFacturaHiddenDetalle"/>  
	<input type="hidden" id="consecutivoFacturaDetalle" name="consecutivoFacturaDetalle"/>  
	<input type="hidden" id="importeHiddenDetalleNeto" name="importeHiddenDetalleNeto"/> 
	<input type="hidden" id="altaAlmacenariaHiddenDetalle" name="altaAlmacenariaHiddenDetalle"/>  
	<input type="hidden" id="almacenHiddenDetalle" name="almacenHiddenDetalle"/> 
	<input type="hidden" id="cEjercicioFactura" name="cEjercicioFactura"/> 
	<input type="hidden" id="cIdTipoPartidaEditHidden" name="cIdTipoPartidaEditHidden"/>
	<input type="hidden" id="claveEpHidden" name="claveEpHidden"/>
	<input type="hidden" id="mImporteFactAUXHidden" name="mImporteFactAUXHidden"/>
	
		</form>	
		
		<div id="dialog" title="Elija Clabe Interbancaria" >
		    <br />Elija la Clabe Interbancaria <br />
		    <form>
		   	<select id="clableInter" name="clableInter" style="width: 25em;"></select>
			</form>
		</div>
					
	</body>
</html>
