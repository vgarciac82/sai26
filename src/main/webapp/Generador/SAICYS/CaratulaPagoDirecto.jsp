<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map<String, Role> rol =usuario.getRoles();
    String cIdDocumento = "";
    String cCentroContable = "";
    String cEjercicio = "";
    String cIdUnidadEjecutora = "";
    
    if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	if (session.getAttribute(GestionInterface.ATT_PagoDirectoFolio) != null) {
		cIdDocumento = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoFolio);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoEjercicio);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoUE);
	}else 
		response.sendRedirect("PagoDirecto.jsp?tab=1");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title></title>
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
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker-es.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/validaciones.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
	var tmpMonto;
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
				Map botones=nb.getBotones(roles,"PagosDirectos","CaratulaPagoDirecto");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
			$("input.AyudaSyC").subIniciaDlg();
			$("#Imp_Bruto").formatCurrency();
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Neto").formatCurrency();
			
			$("#btnGuardar").button().click(function(){
				//Realiza las validaciones para guardar
				var res = validaDatos(false);
				
				if(res){
				
			  //revisamos si cambio el tipo destino esto provocara que todas las facturas en el detalle sean borradas.
		    queryFormPost("verificaTipoDestinoPAGD", {async:false});
		    if($("#tipoDestinoActual").val()!= $("#DESTINO_GASTO").val()){
		       if (!window.confirm("El tipo destino ha cambiado esto provocara que todos los detalles de las facturas sean borrados en caso de que existan, Desea Continuar?")){
		       location.reload();
		       return;
		       }
				
				else{
				//borramos todos los detalles de las facturas en caso de que existan
				queryFormPost("borraDetalleFacturasPAGD", {async:false});
				}
				
				
		    }
				
					formateaValores();
					
					queryFormPost("pagoDirectoMaerialesUpdate",{async:false});
					
					if(parseInt($("#nFolioPagoDirecto").val(),10) > 0){
						queryFormPost("pagoDirectoMUpdate",{async:false});
						queryFormPost("pagoDirectoDocumentacionComprobatoriaMUpdate",{async:false});
					}
					
					
					if($("#DESTINO_GASTO").val()=="AL"){
						//SE CHECA SI EXISTEN REGISTROS DADOS DE ALTA EN EL DETALLE
						queryFormPost("existeFacturasDetallePAGD", {async:false});
						if(parseInt($("#exixteDetalleFacturas").val(),10) > 0){
						//existen registros se tiene que actualizar el almacen si es que fuera cambiado
						queryFormPost("actualizaAlmacenFacturasDetallePAGD", {async:false});
						
						}
					
				}
				
					alert("Los datos se guardaron correctamente.");
					init();
					window.location = 'PagoDirecto.jsp?tab=3&cEjercicio='+$("#cEjercicio").val()+'&cIdDocumento='+$("#cIdFolio").val()+'&nIdEstado=1&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val();
					
				}
				else
					return -1;
			});				
		});
		
		function init(){
			$("#cIdFolio").val("<%=cIdDocumento%>");
			$("#cCentroContable").val("<%=cCentroContable%>");
			$("#cIdUnidadEjecutora").val("<%=cIdUnidadEjecutora%>");
			queryFormPost("mPagoDirectoRead",{async:false});
			if($("#ID_DESTINO_GASTO").val() == "AL"){
				queryFormPost("obtieneAlmacen", {async:false});
			}
			
			queryFormPost("mPagoDirectoMontos",{async:false});
			querySelectPost("catalogoTipoPagoDirectoRead", "cIdTipoOperacion", {async: false });
			$("#cIdTipoOperacion").val($("#ID_TIPO_OPER").val());
			//querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {async: false });
			querySelectPost("CatalogoObraDGastoReadRELG", "DESTINO_GASTO", {async: true });
			$("#DESTINO_GASTO").val($("#ID_DESTINO_GASTO").val());
			cambiaTipoDestino();
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			$("#ALM").val($("#cAlmacenTmp").val());
			$("#TIPO_CONCEPTO").val($("#tConcepto").val());
			cambiaConcepto();			
			querySelectPost("catalogoTipoMovimientoRead","tmovimiento", {async: false });
			$("#tmovimiento").val($("#ID_TIPO_MOVIMIENTO").val());
			//querySelectPost("CAT_TIPO_IVAporCienRead", "por_Iva", {async: false });
			queryFormPost("maximoPagoDirectoRead",{async:false});
			queryFormPost("mPagoDirectoFacturas",{async:false});
			querySelectPost("CAT_TIPO_IVAporCienRead", "por_IvaDoc", {async: false });
								
			$("#por_IvaDoc").val($("#nPorcentajeIVADoc").val());
			validaHabilitaCampos();
			
			if($("#lblMotivoRechazo").val() != "" && $("#nIdEstado").val() == 5){
				$("#trMovitoRechazo").css("display","");
				$("#divMotivoRechazo").html($("#lblMotivoRechazo").val());
			}
			else
				$("#trMovitoRechazo").css("display","none");
			
			if($("#cEjercicio").val() != ""){
				if($("#cAnioTmp").val() == $("#cEjercicio").val()){
					$("#cAnioFactEP").append("<option value='"+$("#cEjercicio").val()+"' selected>"+$("#cEjercicio").val()+"</option>");
					$("#cAnioFactEP").append("<option value='"+($("#cEjercicio").val()-1)+"'>"+($("#cEjercicio").val()-1)+"</option>");
				}
				else{
					$("#cAnioFactEP").append("<option value='"+$("#cEjercicio").val()+"'>"+$("#cEjercicio").val()+"</option>");
					$("#cAnioFactEP").append("<option value='"+($("#cEjercicio").val()-1)+"' selected>"+($("#cEjercicio").val()-1)+"</option>");
				}
			}
			$("#lblImporteBruto").val($("#Imp_Bruto").val());
			$("#lblIVA").val($("#Imp_Iva").val());
			$("#lblImporteNeto").val($("#Imp_Neto").val());
			
			var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
			var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));

			Imp_Neto=Imp_Neto.toFixed(2);
			importeNetoFactura=importeNetoFactura.toFixed(2);
			
			umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
			if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
				$("#apartadoPagoDirecto").css("display", "block");
			}  else{
				
				$("#apartadoPagoDirecto").css("display", "none");				
			} 
			
			
			$("#lblImporteBruto").formatCurrency();
			$("#lblIVA").formatCurrency();
			$("#lblImporteNeto").formatCurrency();
			$("#lblImporteFactura").formatCurrency();
			$("#lblImporteIvaFactura").formatCurrency();
			$("#lblImporteNetoFactura").formatCurrency();
			   
			$("#Imp_BrutoDoc").formatCurrency();
			$("#Imp_NetoDoc").formatCurrency();
			$("#Imp_IvaDoc").formatCurrency();

			if($("#nIdEstado").val()==5){
				//actualizar el usuario
				queryFormPost("updateOperadorCaso", {async:false});
			}
			
			/*
			queryFormPost("obtieneNumeroDetalle", {async:false});
			if($("#numeroDetalle").val()==0){
				$(".habilitaCampos").attr('disabled', false);
			}else{
				$(".habilitaCampos").attr('disabled', true);
			}
			*/
			queryFormPost("mTotalFacturas", {async:false});
			tmpMonto=$("#Imp_BrutoDoc").val();
			validaPestana();
			
		 
			 
		}
		
		function validaPestana(){
			importeTotalDocumento=parseFloat(quitaFmt($("#lblImporteNetoFactura").val()));
			
			importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
			var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));
	
			Imp_Neto=Imp_Neto.toFixed(2);
			importeNetoFactura=importeNetoFactura.toFixed(2);
			umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
	
			$("#folioPago").val($("#nFolioPagoDirecto").val());
			queryFormPost("obtenSubTotalAcumulado", {async:false});
			
			var mSubTotalAcumulado=Number(quitaFmt($("#mSubTotalAcumulado").val()));
			mSubTotalAcumulado=mSubTotalAcumulado.toFixed(2);
			umbral1 = Math.abs(parseFloat(quitaFmt($("#mSubTotalAcumulado").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
			if(Number(mSubTotalAcumulado)==Number(importeNetoFactura) || umbral1 < .03 ){
				queryFormPost("longitudRfc", {async:false});
				if($("#rfcLength").val()==13 &&  parseFloat(quitaFmt($("#lblImporteNetoFactura").val()))>2000  && $("#numRetencion").val()==0  ){
					$("#trRetenciones").css("display","");
					$("#divRetenciones").html($("#lblRetenciones").val());
					$("#apartadoPagoDirecto").css("display", "none");
					$("#partidasPagoDirecto").css("display", "none");
					$("#pagosPagoDirecto").css("display", "none");
					$("#retencionesPagoDirecto").css("display", "block");
				}else{
					$("#trRetenciones").css("display","none");
					$("#retencionesPagoDirecto").css("display", "block");
					$("#partidasPagoDirecto").css("display", "block");
					$("#pagosPagoDirecto").css("display", "block");		
					if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
						$("#apartadoPagoDirecto").css("display", "block");
						$("#pagosPagoDirecto").css("display", "block");		
					}else{
						$("#apartadoPagoDirecto").css("display", "none");	
						$("#pagosPagoDirecto").css("display", "none");					
					}
				} 
			}else{
				$("#apartadoPagoDirecto").css("display", "none");	
				$("#partidasPagoDirecto").css("display", "none");	
				$("#retencionesPagoDirecto").css("display", "none");
				$("#pagosPagoDirecto").css("display", "none");		
			}
			$("#mSubTotalAcumulado").formatCurrency();
		}
		function cambiaTipoDestino(){
			$("input[id='ID_DESTINO_GASTO']").val($('#DESTINO_GASTO option:selected').val());
			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
			$("#tConcepto").val($("#ID_TIPO_CONCEPTO").val());
			cambiaConcepto();
			
			if($("#DESTINO_GASTO").val() == "CD"){
				$("#altaAlmacenariaTitulo").css("display","none");
				$("#altaAlmacenariaCampos").css("display","none");
				$("#altaAlmacen").val("");
				$("#nFacturaEP").val("");
			}
			else if($("#DESTINO_GASTO").val() == "AL"){
				$("#altaAlmacenariaTitulo").css("display","");
				$("#altaAlmacenariaCampos").css("display","");
			}
		}
		
		function cambiaConcepto(){
			$("#TIPO_CONCEPTO").val($("#tConcepto").val());
			querySelectPost("catalogoTipoMovimientoRead","tmovimiento", {async: false });
		}
		
		function cambiaMontoNeto(){
			var mImporteBruto = quitaFmt($("#Imp_Neto").val()) / (1 + Number( $("#por_Iva").val() ) );
			$("#Imp_Bruto").val(mImporteBruto);
			$("#Imp_Bruto").formatCurrency();
			$("#Imp_Iva").val(quitaFmt($("#Imp_Neto").val()) - quitaFmt($("#Imp_Bruto").val()));
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Neto").formatCurrency();
		}
		
		function cambiaMontoBrutoIva(){
		
		 var tipoIva=$("#por_IvaDoc option:selected").text();
		 $("#DESTIVAPAGD").val(tipoIva)
		//BUSACA EL TIPO DE IVA
		 queryFormPost("tipoIvaPagoDirectoMateriales", { async: false});
		 $("#TIVAPAGD1").val($("#TIVAPAGD").val())
		
			queryFormPost("obtieneNumeroDetalle", {async:false});			
			var mImporteNeto = quitaFmt($("#Imp_BrutoDoc").val()) * (1 + Number( $("#por_IvaDoc").val() ) );
			$("#Imp_NetoDoc").val(parseFloat(mImporteNeto));
			$("#Imp_IvaDoc").val(quitaFmt($("#Imp_NetoDoc").val()) - quitaFmt($("#Imp_BrutoDoc").val()));
			if($("#numeroDetalle").val()!=0){
				if(parseFloat(quitaFmt($("#mTotalFacturaV").val()))>parseFloat(quitaFmt($("#Imp_NetoDoc").val()))){
					$("#Imp_BrutoDoc").val(tmpMonto);
					mImporteNeto = quitaFmt($("#Imp_BrutoDoc").val()) * (1 + Number( $("#por_IvaDoc").val() ) );
					$("#Imp_NetoDoc").val(parseFloat(mImporteNeto));
					$("#Imp_IvaDoc").val(quitaFmt($("#Imp_NetoDoc").val()) - quitaFmt($("#Imp_BrutoDoc").val()));
					alert("El monto bruto que desea capturar no cubre el monto bruto de las facturas capturadas");					
				} 
			}
			$("#Imp_NetoDoc").formatCurrency();
			$("#Imp_IvaDoc").formatCurrency();
			$("#Imp_BrutoDoc").formatCurrency();
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
		
		function validaDatos(validaExisteDocumento){
		
		//valida si es un pago directo que no lleva validacion del maximo	
		var vTipoOperacion = $("#cIdTipoOperacion").val();
		 if ("67".indexOf(vTipoOperacion) == -1){
	    		if(parseFloat(quitaFmt($("#mImporteMaximo").val())) < parseFloat(quitaFmt($("#Imp_Neto").val())) &&  parseInt($("#cIdTipoOperacion").val(),10) != 8){
				alert("El importe neto sobre pasa el monto m\xE1ximo.");
				return false;
			}
		
		}	
		
			if($("#cIDRFC").val() == ""){
				alert("Debe seleccionar un proveedor.");
				return false;
			}
			
			if($("#Concepto").val() == ""){
				alert("Debe introducir el concepto.");
				return false;
			}
			
			if($("#Concepto").val().length < 10){
				alert("Debe introducir una descripci\xF3n mas amplia en el Concepto, mayor a 10 caracteres.");
				return false;
			}
			
			/*if(quitaFmt($("#Imp_Bruto").val()) == 0){
				alert("Debe introducir el importe bruto.");
				return false;
			}
			
			if(quitaFmt($("#Imp_Neto").val()) == 0){
				alert("Debe introducir el importe bruto.");
				return false;
			}*/
			
			return true;	
		}
		
		function formateaValores(){
			
			$("#DESTINO_GASTO_EDIT").val($("#DESTINO_GASTO").val());
			$("#cIdTipoOperacion_edit").val($("#cIdTipoOperacion").val());
			if($("#DESTINO_GASTO").val() == "AL"){
				$("#cAlmacenTmp").val($("#ALM").val());
				$("#cAltaAlmacenariaTmp").val($("#altaAlmacen").val());
				$("#cAnioTmp").val($("#cAnioFactEP").val());
				$("#cFacturaTmp").val($("#nFacturaEP").val());
			}
			else if($("#DESTINO_GASTO").val() == "CD"){
				$("#cAlmacenTmp").val("");
				$("#cAltaAlmacenariaTmp").val("");
				$("#cAnioTmp").val($("#cEjercicio").val());
				$("#cFacturaTmp").val("");
			}
			   
			$("#cImporteBrutoTmp").val($("#Imp_BrutoDoc").val());
			$("#cIvaTmp").val($("#por_IvaDoc").val());
			$("#cImporteNetoTmp").val($("#Imp_NetoDoc").val());
			$("#cIDRFCTmp").val($("#cIDRFC").val());
			 var tipoIva=$("#por_IvaDoc option:selected").text();
		     $("#DESTIVAPAGD").val(tipoIva)
		     //BUSACA EL TIPO DE IVA
		     queryFormPost("tipoIvaPagoDirectoMateriales", { async: false});
		     $("#TIVAPAGD1").val($("#TIVAPAGD").val())
		   
			
		}
		
		function validaHabilitaCampos(){
			if($("#nIdEstado").val() <= 2 || $("#nIdEstado").val() == 5){
				var roles = "<%=roles%>";
				
				if(roles.indexOf("ADMIN_RECMAT") < 0 && $("#cIdUsuario").val() != $("#cIdUsuarioCreacion").val()){
					$("#DESTINO_GASTO").attr("disabled","true");
					$("#Concepto").attr("disabled","true");
					$("#Imp_Bruto").attr("disabled","true");
					$("#por_Iva").attr("disabled","true");
					$("#Imp_Neto").attr("disabled","true");
					$("#cIdTipoOperacion").attr("disabled","true");
					$("#tConcepto").attr("disabled","true");
					$("#tmovimiento").attr("disabled","true");
					$("#ALM").attr("disabled","true");
					$("#altaAlmacen").attr("disabled","true");
					$("#cAnioFactEP").attr("disabled","true");
					$("#nFacturaEP").attr("disabled","true");
					$("#btnGuardar").attr("disabled","true");
				}
			}
			else{
				//$("#DESTINO_GASTO").attr("disabled","true");
				$("#Concepto").attr("disabled","true");
				$("#Imp_Bruto").attr("disabled","true");
				$("#por_Iva").attr("disabled","true");
				$("#Imp_Neto").attr("disabled","true");
				//$("#cIdTipoOperacion").attr("disabled","true");
				$("#tConcepto").attr("disabled","true");
				$("#tmovimiento").attr("disabled","true");
				$("#ALM").attr("disabled","true");
				$("#altaAlmacen").attr("disabled","true");
				$("#cAnioFactEP").attr("disabled","true");
				$("#nFacturaEP").attr("disabled","true");
				$("#btnGuardar").attr("disabled","true");
			}
		}
		
		
		
		function copiarPagoDirecto(){
		//checa que sea el usuario creador el que puede realizar la reactivacion de la relacion de gastos
		queryFormPost("verificaUsuarioCreacionPAGD", {async:false});
		if($("#usuarioCreacionPAGD").val()== $("#cIdUsuario").val()){
		//se revisa que la relacion de gastos este aprobada y cancelada
		queryFormPost("verificaRelacionGastosCanceladaFinancieroPAGD", {async:false});
			if( $("#documentoAplicadoOld").val()=="C" && parseInt($("#estadoOld").val(),10)==4){
			
			if (!window.confirm("Esta seguro de realizar la reactivacion del Pago Directo este proceso no podra ser cancelado , Desea Continuar?")){
		       location.reload();
		       return;
		       }
			
		//se tiene que crear un nuevo folio de gestion
		 
		  	//if($("#nFolioPagoDirectoNew").val() == " ")
		  	$.ajax({url: '../../servlet/PagoDirectoServlet?TIPO_DOCTO='+$("#TIPO_DOCTO").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data: 'operacion=0', dataType: 'json', success: guardaFolio});		
					//En caso de cualquier error al obtener el folio, sale de la función
					if($("#nFolioPagoDirectoNew").val() == 0 || $("#nFolioPagoDirectoNew").val() == ""){
					alert("No se pudo generar el Folio del Caso");
					return -1;
					}
		        
		          //actualiza el folio en las tablas correspondientes
		  		$.ajax({url: '../../servlet/PagoDirectoServlet?cIdDocumento='+$("#cIdFolio").val()+"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nFolioPagoDirectoNew="+$("#nFolioPagoDirectoNew").val()+"&nFolioPagoDirectoOld="+$("#nFolioPagoDirecto").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoPagoDirectoNew="+$("#folioCasoPagoDirectoNew").val(), type:'post' , async: false,data:'operacion=3', dataType: 'json', success: 
				function(j){
				res=j[0].Contable1;
				if(res!="-1"){
				alert("Se Realizo con Exito la Copia del Pago Directo");
				//se constuye link donde va a ir la copia
				//Bitácora
				$("#cAccion").val("REALIZA_COPIA_PAGO_DIRECTO");
				$("#cIdDocumento").val($("#cIdFolio").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				//queryFormPost("fn_ConstruyeLinkPAGDCopia",{async : false});
				link='PagoDirecto.jsp?tab=3&cEjercicio='+$("#cEjercicio").val()+'&cIdDocumento='+$("#cIdFolio").val()+'&nIdEstado=1&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val();
				window.location=link;
				}else{
					alert("No se realizo con exito la copia del Pago Directo intentelo nuevamente");
					return;
					}
				}	
			});    
		
			
			}else{
			alert("Para que pueda Copiarse el Pago Directo debe estar Cancelado por el Modulo Financiero y debe de estar Aprobado en el modulo de Materiales.");
			return;
			 
			}
		
		}else{
		alert("Solo el usuario creador del Pago Directo puede realizar la Reactivacion,el usuario creador es: "+$("#nombreUsuarioPAGD").val()+"");
		return;
		}	    
		  
		
		
		}
		
		
	 function guardaFolio(j){
		var folioPre=-1;
		var folioCaso=-1;
		folioRel=j[0].folioCasoConsecutivo;
    	folioCaso=j[0].folioCaso;
    	 if(folioRel==-1){
      		alert("Ha ocurrido un error al crear el caso, contacte a su soporte");
      		return -1;
        }else{
     	   $("#nFolioPagoDirectoNew").val(folioRel);
     	   $("#folioCasoPagoDirectoNew").val(folioCaso);
     	}
	}	
		
		
	</script>
</head> 
<body>
<form>
	<table width="100%" align="left">
		<tr>
			<td>
				<fieldset>
					<legend>Caratula Pago Directo</legend>
					<table align="left" cellpadding="2" width="100%">
						<tr align="left">
							<td>
								<input type="text" id="lblUnidadEjecutora" name="lblUnidadEjecutora" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								No. Folio:&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="lblFolio" id="lblFolio" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="text" id="lblEstado" name="lblEstado" style="border-width:0; background-color:transparent;width: 400px" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Fecha Recepci&oacute;n:<input type="text" id="lblFechaRecepcion" name="lblFechaRecepcion" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe Bruto en facturas:<input type="text" id="lblImporteFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe IVA en facturas:<input type="text" id="lblImporteIvaFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr> 
						<tr align="left">
							<td>
								Importe Neto en facturas:<input type="text" id="lblImporteNetoFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblImporteBruto" name="lblImporteBruto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblIVA" name="lblIVA" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblImporteNeto" name="lblImporteNeto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr id="trMovitoRechazo" style="color:#EC1B0F;">
							<td align="left">
								<table>
									<tr>
										<td>
											Motivo Rechazo:
										</td>
										<td>
											<div id="divMotivoRechazo"></div>
											<input type="hidden" id="lblMotivoRechazo" name="lblMotivoRechazo" readonly/>	
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		
    	<tr>
    		<td align="right" colspan="2">
    		     <img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="copiarPagoDirecto();"/>&nbsp;Reacivar Pago Directo
				
    		</td>
    	</tr>

		<tr>
			<td>
				<fieldset>
					<legend>Actualizar Datos</legend>
					<table>
						<tr align="left">
							<td colspan="2">
								R.F.C.: &nbsp;&nbsp;&nbsp;&nbsp;<input disabled type="text" maxlength="15" size="15" name="cIDRFC" id="cIDRFC" class="AyudaSyC obligatorio" />
								&nbsp;&nbsp;<input disabled readonly type="text" maxlength="100"
									style="width:500px;" name="cnombre" ID="cnombre" />
							</td>
						</tr>
						<tr>
							<td align="left">
								<table>
									<tr align="left">
										<td style="width:180px">
											Tipo de Pago Directo:
										</td>
										<td>
											<select id="cIdTipoOperacion" name="cIdTipoOperacion" class='habilitaCampos'  style="width:545px;">
											</select>
										</td>
									</tr>
									<tr>
										<td align="left" style="width:180px">
											Tipo Destino:
										</td>
										<td align="left">
											<select id="DESTINO_GASTO" name="DESTINO_GASTO" class='habilitaCampos' onChange="cambiaTipoDestino();" style="width:545px;"></select>
										</td>
									</tr>
									<tr>
										<td align="left" style="width:180px">
											Fuentes de Financiamiento:
										</td>
										<td align="left">
											<select id="TFONDO" name="TFONDO" style="width:545px;" disabled>
												<option value="FF">Fondos Fiscales
												</option>
											</select>
										</td>
									</tr>
									
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="left">
								<table>
									<tr>
										<td>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td align="left">
														Concepto:
													</td>
												</tr>
												<tr>
													<td align="left">
														<textarea name="Concepto" rows="4" style="width:720px;" ID="Concepto" onkeypress="valFmt(this,15)"></textarea>
													</td>
												</tr>	
											</table>
										</td>
									</tr>
									<tr>
										<td>
											<table>
												<tr>
													<td align="right" nowrap> 
														Importe Bruto:
													</td>
													<td nowrap>
														<input type="text" id="Imp_BrutoDoc" name="Imp_BrutoDoc" onkeypress="valFmt(this,9)" onChange="cambiaMontoBrutoIva();" value="0" size="15" maxlength="15" style="text-align: right;" />
													</td>
													<td align="right" nowrap>
														% IVA:
													</td>
													<td nowrap>
														<input type="hidden" id="nPorcentajeIVADoc" name="nPorcentajeIVADoc" size="15" maxlength="15" style="text-align: right;"/>
														<select id="por_IvaDoc" name="por_IvaDoc" onChange="cambiaMontoBrutoIva();" style="width: 10em;">
														
														</select>
									 
														
													</td>
												</tr>
												<tr>
													<td align="right" nowrap>
														Importe IVA:
													</td>
													<td nowrap>
														<input type="text" id="Imp_IvaDoc" name="Imp_IvaDoc" onkeypress="valFmt(this,9)" value="0" disabled size="15" maxlength="15" style="text-align: right;"/>
													</td>
													<td align="right">
														Importe Neto:
													</td>
													<td>
														<input type="text" id="Imp_NetoDoc" name="Imp_NetoDoc" onKeyPress="valFmt(this,9)" disabled value="0" size="15" maxlength="15" style="text-align: right;"/>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="left">
								<table>
									<tr>
										<td>
											&nbsp;
										</td>
									</tr>
									<tr align="left">
										<td>Tipo de Concepto</td>
										<td colspan="2">Tipo De Movimiento</td>
										<td>&nbsp;</td>
									</tr>
									<tr align="left">
										<td>
											<select id="tConcepto" name="tConcepto" style="width: 10em;" onChange="cambiaConcepto();"></select>
										</td>
										<td colspan="2">
											<select id="tmovimiento" name="tmovimiento" style="width: 20em;"></select>
										</td>
										<td>&nbsp;</td>
									</tr>
									<tr align="left" id="altaAlmacenariaTitulo">
										 <td>Almacén</td>
										<!--<td>Alta almacenaria</td> -->
										<td>Año</td>
										<!-- <td>Factura</td> -->
									</tr>
									<tr align="left" id="altaAlmacenariaCampos">
										<td style="width:250px;">
											<select id="ALM" name="ALM" class='habilitaCampos'></select>
										</td>
									<!-- 	<td>
											<input type="text" id="altaalmacen" name="altaalmacen"  style="text-align: right;" onkeypress="valfmt(this,9)" value="0" size="12" maxlength="12">
										</td> -->
										<td style="width:80px;">
											<select id="cAnioFactEP" name="cAnioFactEP" class='habilitaCampos'></select>
										</td>
										<td>
											<input type="hidden" id="nFacturaEP" name="nFacturaEP" >
										</td>
									</tr>
									<!--<tr>
										<td>
											&nbsp;
										</td>
									</tr>-->
								</table>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<!--<tr>
			<td>
				&nbsp;
			</td>
		</tr>-->
		<tr>
			<td align="left">
				<input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" class="btnInterfaceBG"/>
			</td>
		</tr>
	</table>
	<input type="hidden" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO" value="DIRECTO"/>
	<input type="hidden" id="cDocumento" name="cDocumento" value="PAGODIRECTO"/>
	<input type="hidden" id="ID_TIPO_OPER" name="ID_TIPO_OPER"/>
	<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/>
	<input type="hidden" id="cCentroContable" name="cCentroContable" />
	<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" />
	<input type="hidden" id="ID_TIPO_CONCEPTO" name="ID_TIPO_CONCEPTO" />
	<input type="hidden" id="ID_TIPO_MOVIMIENTO" name="ID_TIPO_MOVIMIENTO" />
	<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=cEjercicio %>"/>
	<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" />
	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
	<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
	<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion"/>
	<input type="hidden" id="cIdFolio" name="cIdFolio" />
	<input type="hidden" id="cAlmacenTmp" name="cAlmacenTmp" />
	<input type="hidden" id="cAltaAlmacenariaTmp" name="cAltaAlmacenariaTmp" />
	<input type="hidden" id="cAnioTmp" name="cAnioTmp" />
	<input type="hidden" id="cFacturaTmp" name="cFacturaTmp" />	
	<input type="hidden" id="cIDRFCTmp" name="cIDRFCTmp" />
	<input type="hidden" id="nIdEstado" name="nIdEstado" />
	<input type="hidden" id="Imp_Bruto" name="Imp_Bruto" />
	<input type="hidden" id="Imp_Iva" name="Imp_Iva" />
	<input type="hidden" id="Imp_Neto" name="Imp_Neto" />
	<input type="hidden" id="cImporteBrutoTmp" name="cImporteBrutoTmp" />
	<input type="hidden" id="cIvaTmp" name="cIvaTmp" />
	<input type="hidden" id="cImporteNetoTmp" name="cImporteNetoTmp" />
	<input type="hidden" id="nFolioPagoDirecto" name="nFolioPagoDirecto" />
	<input type="hidden" id="caNoContrarrecibo" name="caNoContrarrecibo" />
	<input type="hidden" id="numeroDetalle" name="numeroDetalle" />
	
	<input type="hidden" id="folioPago" name="folioPago" />
	<input type="hidden" id="mSubTotalAcumulado" name="mSubTotalAcumulado" />
	
	 
	
	<input type="hidden" id="numRetencion" name="numRetencion"/>
	<input type="hidden" id="rfcLength" name="rfcLength"/> 
	
	<input type="hidden" name="mTotalFacturaV" id="mTotalFacturaV"/>
	
	<input type="hidden" name="DESTINO_GASTO_EDIT" id="DESTINO_GASTO_EDIT"/>
	<input type="hidden" name="cIdTipoOperacion_edit" id="cIdTipoOperacion_edit"/>
	<input type="hidden" name="DESTIVAPAGD" id="DESTIVAPAGD"/>
	<input type="hidden" name="TIVAPAGD" id="TIVAPAGD"/>
	<input type="hidden" name="TIVAPAGD1" id="TIVAPAGD1"/>
	<input type="hidden" name="tipoIvaCaratula" id="tipoIvaCaratula"/>
	
	
	<!-- bitacora -->
		<input type="hidden" id="cAccion" name="cAccion" />
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" />
		
		<!-- copia pago directo -->
		<input type="hidden" id="documentoAplicadoOld" name="documentoAplicadoOld" />
		<input type="hidden" id="estadoOld" name="estadoOld" />
		<input type="hidden" id="DESTINO_GASTOLINK" name="DESTINO_GASTOLINK" />
		<input type="hidden" id="nIdEstadoLINK" name="nIdEstadoLINK" />
		<input type="hidden" id="tConceptoLINK" name="tConceptoLINK" />
		<input type="hidden" id="usuarioCreacionPAGD" name="usuarioCreacionPAGD" />
		<input type="hidden" id="nombreUsuarioPAGD" name="nombreUsuarioPAGD" />
	    <input type="hidden" id="nFolioPagoDirectoNew" name="nFolioPagoDirectoNew"/>
	    <input type="hidden" id="folioCasoPagoDirectoNew" name="folioCasoPagoDirectoNew"/>
	    <input type="hidden" id="TIPO_DOCTO"	name="TIPO_DOCTO" value="PAGODIRECTO"/>
	    <input type="hidden" id="tipoDestinoActual" name="tipoDestinoActual" />
		<input type="hidden" id="exixteDetalleFacturas" name="exixteDetalleFacturas" />
		
	
	
	
	
	

	   	
</form>    
</body>
</html>
