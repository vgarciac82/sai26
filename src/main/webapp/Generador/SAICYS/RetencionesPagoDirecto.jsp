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
    Map rol =usuario.getRoles();
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
	var yaSeLleno = 0;
	var suma = 0;
	var mImporteIVA = 0;
	var subTotal_1 = 0.0;
	var subTotal_2 = 0.0;
	var mImporteNeto = 0.0;
	var mImporteBruto = 0.0;

	var mImporteRetencion2 = 0.0;


	var m23IVA = 0.0;
	var mISRHonorarios = 0.0;
	var m5Millar = 0.0;
	var mFletes = 0.0;
	var mISRArrenda = 0.0;
	var mCedular = 0.0;
	var oTable;
	var szWhere;

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
				Map botones=nb.getBotones(roles,"PagosDirectos","RetencionesPagoDirecto");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
			$("#Imp_Bruto").formatCurrency();
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Neto").formatCurrency();
			
			 szWhere=" nFolioPagoDirecto="+$("#nFolioPagoDirecto").val();
			cargaRetenciones();
			$("#cIdTipoRetencion").change(function(){
				if ( $( this ).val() == 2){
					$("#divMilla2").show();
				}else{
					$("#divMilla2").hide();
				}
			});
			
			/* $("#grdRetencion tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
 					$(this.nTr).removeClass('row_selected');
 					var aPos = oTable.fnGetPosition( this.nTr );
  					// Get the data array for this row
  					var aData = oTable.fnGetData( aPos[0] );
				});
				$(event.target.parentNode).addClass('row_selected');
			});
 			 */
 			$("#grdRetencion tbody").dblclick(function(event) {
 				if ($("#nIdEstado").val()==1){
 					quitaFormato();
 					if ($("#btAgregaMov").attr('disabled') == null){
						$(oTable.fnSettings().aoData).each(function (){
 							$(this.nTr).removeClass('row_selected');
 							var aPos = oTable.fnGetPosition( this.nTr );
  							// Get the data array for this row
  							var aData = oTable.fnGetData( aPos[0] );
						});
						$(event.target.parentNode).addClass('row_selected');
		   				var aPos = oTable.fnGetPosition( event.target.parentNode );
						var temp=0;
		   				var aData = oTable.fnGetData( aPos );
						temp=aData[0];
						
						$("#id_caso").val($("#nFolioPagoDirecto").val( ) );
		   				$("#cveRetencion").val( temp);
						if( aData[0] == 2 ){
							$("#divMilla2").hide();
						}
						queryFormPost("tpagodirectoretencionDelete", {async: false });
						cargaRetenciones();

						$(".subtotall").attr('disabled', true);
						/* queryFormPost("longitudRfc", {async:false});
						if($("#rfcLength").val()==13 &&  parseFloat(quitaFmt($("#lblImporteNetoFactura").val()))>2000  && $("#numRetencion").val()==0 ){
							$("#trRetenciones").css("display","");
							$("#divRetenciones").html($("#lblRetenciones").val());
							$("#apartadoPagoDirecto").css("display", "none");
							$("#partidasPagoDirecto").css("display", "none");
						}else{
							$("#trRetenciones").css("display","none");
							if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
								$("#apartadoPagoDirecto").css("display", "block");
							}else{
								$("#apartadoPagoDirecto").css("display", "none");				
							}
						//	$("#apartadoPagoDirecto").css("display", "block");
							$("#partidasPagoDirecto").css("display", "block");
						} */
						validaPestana();
					}
 				}else{
 					return;
 				}
			
			
			ponFormato();

			});	
		});
		
		function init(){
			$("#cIdFolio").val("<%=cIdDocumento%>");
			$("#cCentroContable").val("<%=cCentroContable%>");
			$("#cIdUnidadEjecutora").val("<%=cIdUnidadEjecutora%>");
			queryFormPost("mPagoDirectoRead",{async:false});
			queryFormPost("mPagoDirectoMontosHidden",{async:false});
			
			queryFormPost("mPagoDirectoFacturas",{async:false});
			queryFormPost("mPagoDirectoInput",{async:false});
			
			//catalogo de retenciones
			querySelectPost("CatalogoTipoRetencionReadMateriales", "cIdTipoRetencion", {async: false });
			querySelectPost("CAT_TIPO_IVAporCienRead", "por_Iva", {async: false });
			
			$("#divMilla2").hide();			
			validaHabilitaCampos();
			
			if ($("#ID_DESTINO_GASTO").val()=='CD'){
				$(".consumoDirecto").css("display","block");
			}else{
				$(".consumoDirecto").css("display","none");
			}	
			
			$("#lblImporteBruto").val($("#Imp_BrutoHidden").val());
			$("#lblIVA").val($("#Imp_IvaHidden").val());
			$("#lblImporteNeto").val($("#Imp_NetoHidden").val());
			$("#lblImporteBruto").formatCurrency();
			$("#lblIVA").formatCurrency();
			$("#lblImporteNeto").formatCurrency();
			$("#lblImporteFactura").formatCurrency();
			$("#lblImporteIvaFactura").formatCurrency();
			$("#lblImporteNetoFactura").formatCurrency();
			$("#por_Iva").val($("#nPorcentajeIVA").val());
			
			//input
			$("#Imp_Ejercer").formatCurrency();
			
			$(".subtotall").attr('disabled', true);
			oTable = $('#grdRetencion').dataTable();
			
		
			if ($("#nIdEstado").val()==1){
				$(".paso03").attr('disabled', false);
			}else{
				$(".paso03").attr('disabled', true);
			}
			
			//pasar a una funcion //////////////7
			/* var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
			var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));

			Imp_Neto=Imp_Neto.toFixed(2);
			importeNetoFactura=importeNetoFactura.toFixed(2);
			umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));

			 
			///////////////////////////////////////
			queryFormPost("longitudRfc", {async:false});
			if($("#rfcLength").val()==13 &&  parseFloat(quitaFmt($("#lblImporteNetoFactura").val()))>2000  && $("#numRetencion").val()==0  ){
				$("#trRetenciones").css("display","");
				$("#divRetenciones").html($("#lblRetenciones").val());
				$("#apartadoPagoDirecto").css("display", "none");
				$("#partidasPagoDirecto").css("display", "none");
			}else{
				if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
					$("#apartadoPagoDirecto").css("display", "block");
				}else{
					$("#apartadoPagoDirecto").css("display", "none");				
				}
				//$("#apartadoPagoDirecto").css("display", "block");
				$("#partidasPagoDirecto").css("display", "block");
			} */
			validaPestana();
			
		}
		
		
		function validaPestana(){
			var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
			var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));

			Imp_Neto=Imp_Neto.toFixed(2);
			importeNetoFactura=importeNetoFactura.toFixed(2);
			umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));

			 
			///////////////////////////////////////
			queryFormPost("longitudRfc", {async:false});
			if($("#rfcLength").val()==13 &&  parseFloat(quitaFmt($("#lblImporteNetoFactura").val()))>2000  && $("#numRetencion").val()==0  ){
			
				$("#trRetenciones").css("display","");
				$("#divRetenciones").html($("#lblRetenciones").val());
				$("#apartadoPagoDirecto").css("display", "none");
				$("#partidasPagoDirecto").css("display", "none");
			}else{
				if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
					$("#apartadoPagoDirecto").css("display", "block");
				}else{
					$("#apartadoPagoDirecto").css("display", "none");				
				}
				//$("#apartadoPagoDirecto").css("display", "block");
				$("#partidasPagoDirecto").css("display", "block");
				$("#trRetenciones").css("display","none");
			}
		
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
			var mImporteNeto = quitaFmt($("#Imp_Bruto").val()) * (1 + Number( $("#por_Iva").val() ) );
			$("#Imp_Neto").val(parseFloat(mImporteNeto));
			$("#Imp_Neto").formatCurrency();
			$("#Imp_Iva").val(quitaFmt($("#Imp_Neto").val()) - quitaFmt($("#Imp_Bruto").val()));
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Bruto").formatCurrency();
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
		
		function validaHabilitaCampos(){
			if($("#nIdEstado").val() <= 2 || $("#nIdEstado").val() == 5){
				var roles = "<%=roles%>";
				
				if(roles.indexOf("ADMIN_RECMAT") < 0 && $("#cIdUsuario").val() != $("#cIdUsuarioCreacion").val()){
					
				}
			}
			else{
				
			}
		}
		
		function habilitaMillar2(elPar){
			if (elPar == 0){
				$("#aux").val(elPar);//CAMARA
			}else{
				$("#aux").val(elPar);//INSITUTO
			}
		}
		
		function fnAgregarRet() {
			if($("#cIdTipoRetencion").val()==2){
				if($("#aux").val()==''){
					alert("Debe elegir un tipo: Camara o Instituo");
					return -1;
				}
			}
			
			quitaFormato();
			var table = document.getElementById('grdRetencion');

		    var rowCount = table.rows.length;
		    var yaExiste = 0;
			var nu2=0;
			
		    try{
				for(var i=0; i<rowCount; i++) {
			        var row = table.rows[i];
			        var chkbox = '';
					try
					{
					  var chkbox = row.cells[0].childNodes[0];
					}
					catch(e) {
			        	null;
			        }
					if(null != chkbox ) {
						if(chkbox.toString() == $("#cIdTipoRetencion").val()){
			        		yaExiste=1;
						}	
			        }	
			    }
		    }
		    catch(e) {
		       alert(e);
		    }
		    
			if (yaExiste == 1){
				alert("Ya se ingresó una retención con esa clave");
				return;
			}

			queryFormPost("CatalogoTipoRetencionObtenDetRead", {async: false });
			queryFormPost("tPagoDirectoRetencionMateriales", {async: false });
			
			if($("#cIdTipoRetencion").val()==2){
				// ACTUALIZAR EL DATO EN LA TABLA PADRE
				queryFormPost("updatePAgoDirectoCNIC", {async: false });
			}
			validaPestana();
           	cargaRetenciones();

			//Actulizo en base de datos las neuvas retenciones
			$("#elcontra").val($("#caNoContrarrecibo").val());
		   	$("#elrfc").val($("#cIDRFC").val());
			$(".paso01").attr('disabled', false);
			$(".subtotall").attr('disabled', true);
			ponFormato();
								
			if ( $("#cIdTipoRetencion").val() == 2){
				$("#divMilla2").show();
			}else{
				$("#divMilla2").hide();
			}
			
			/* queryformpost("longitudrfc", {async:false});
			if($("#rfclength").val()==13 &&  parsefloat(quitafmt($("#lblimportenetofactura").val()))>2000 && $("#numretencion").val()==0 ){
				$("#trretenciones").css("display","");alert();
				$("#divretenciones").html($("#lblretenciones").val());
				$("#apartadopagodirecto").css("display", "none");
				$("#partidaspagodirecto").css("display", "none");
			}else{
				if(number(imp_neto)==number(importenetofactura) || umbral < .03 ){
					$("#apartadopagodirecto").css("display", "block");
				}else{
					$("#apartadopagodirecto").css("display", "none");				
				}
				$("#trretenciones").css("display","none");
				//$("#apartadopagodirecto").css("display", "block");
				$("#partidaspagodirecto").css("display", "block");
			} */
		
		}	
		
		function calculaIVA(calcRet, elMonto){
			
			quitaFormato();
			var ayuda=0;
			var ayuda2=0;
			var diferencia=0;
			elMonto = quitaFmt(elMonto);
			suma = 0;
			mImporteIVA=0;
			subTotal_1 = 0.0;
			subTotal_2 = 0.0;
			mImporteNeto = 0.0;
			mImporteBruto = 0.0;
			mImporteSancion = 0.0;
			mImporteDevolucion = 0.0;
			mAmortizacionAnticipo = 0.0;
			mImporteRetencion2 = 0.0;
			mImportePenalizacion = 0.0;
			m2Millar = 0.0;
			m23IVA = 0.0;
			mISRHonorarios = 0.0;
			m5Millar = 0.0;
			mFletes = 0.0;
			mISRArrenda = 0.0;
			mCedular = 0.0;

		/*	if (esNeto == 1){
				// Si se considera que es el neto
				//		elProrrateo = Number(elMonto) /Number($("#Imp_Neto").val());
				// Si se considera que es el por ejercer
				elProrrateo = Number(elMonto) / Number($("#Imp_Ejercer").val());
				mImporteBruto = elMonto / (1 + Number( $("#por_Iva").val() ) ) ; 
				//Number( $("#Imp_Bruto").val() ) * Number(elProrrateo);
			}else{*/
				if( Number($("#Imp_Bruto").val()) == 0 ){
					elProrrateo = Number(elMonto);
				}else{
					elProrrateo = Number(elMonto) / Number($("#Imp_Bruto").val());
				}
				mImporteBruto = Number( $("#Imp_Bruto").val() ); //* Number(elProrrateo);			
//			}
			mImporteBruto = mImporteBruto.toFixed(2);
			suma = Number( mImporteBruto );

			subTotal_1 = suma;
		
			/*if (esNeto == 1){
				mImporteIVA = elMonto - Number( mImporteBruto ); 
				//Number( elMonto ) - Number( mImporteBruto );
			}else{*/
				mImporteIVA = elMonto * Number( $("#por_Iva").val() ); 
				//* Number( subTotal_2 );
		//	}

			mImporteIVA = Number( mImporteIVA.toFixed(2) );
			suma += Number( mImporteIVA );
			if (calcRet==1){
				try {
					
				//	var table = document.getElementById('grdRetencion');
					var aTrs = $('#grdRetencion').dataTable().fnGetNodes();	
		       	//	var rowCount = table.rows.length;
		       		var elMonto2 = 0.0;
		       	//	alert(aTrs.length);
					for(var i=0; i<aTrs.length; i++) {
		        		//var row = table.rows[i];
		        		var nTr = $('#grdRetencion').dataTable().fnGetData(aTrs[i]);
		            	var elPorcentaje = '';
		            	var elTipo = '';
						var descTipo = '';
						var gua=0;
						var gua2=0;
						try{
							/* var elTipo = row.cells[0].childNodes[0];
							var descTipo = row.cells[1].childNodes[0];
							var elPorcentaje = row.cells[2].childNodes[0]; */
							 elTipo = nTr[0];
							 descTipo = nTr[1];
							 elPorcentaje = nTr[2];
							
						}
						catch(e) {
		        				alert("error");
		        		}
		        	//	alert("elPorcentaje "+elPorcentaje);
						if(null != elPorcentaje ) {
		     		    	if('' != elPorcentaje.toString() ) {
		     		       		$("#tipoRet").val( elTipo.toString() );
								gua2 = parseFloat( elPorcentaje.toString() ) * parseFloat( subTotal_1 );
								gua2 = gua2.toFixed(2);
		       		     		elMonto2 = Number( gua2 );
								
		       		     		if (parseInt($("#tipoRet").val(), 10) == 0) {
									gua2 = (Number( mImporteIVA ) * 2) /3 ;
		       		     			elMonto2 = gua2.toFixed(2);
						   			mImporteRetencion2 = Number( mImporteRetencion2 ) + Number( elMonto2 );
						   		//	alert(mImporteRetencion2);
							   		m23IVA = Number( elMonto2 );
								}
								if (parseInt($("#tipoRet").val(), 10) == 2) {
								
							   		mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							   		m2Millar = elMonto2.toFixed(2);
								 }
								if (parseInt($("#tipoRet").val(), 10) == 3) {
							   		mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							   		m5Millar = elMonto2.toFixed(2);
								 }
								if (parseInt($("#tipoRet").val(), 10) == 4) {
							   		mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							   		mISRHonorarios = elMonto2.toFixed(2);
								 }
								if (parseInt($("#tipoRet").val(), 10) == 5) {
							   		mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							   		mFletes = elMonto2.toFixed(2);
								 }
								if (parseInt($("#tipoRet").val(), 10) == 6) {
							   		mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							   		mISRArrenda = elMonto2.toFixed(2);
								 }
								if (parseInt($("#tipoRet").val(), 10) == 7) {
								   elMonto2 = parseFloat( elPorcentaje.toString() ) * parseFloat( mImportePenalizacion );
								   elMonto2 = elMonto2.toFixed(2);
							   		mImporteRetencion2 = mImporteRetencion2 + Number( elMonto2 ) ;
							   		mImportePenalizacion = elMonto2;
								 }
								 if (parseInt($("#tipoRet").val(), 10) == 9) {
							   		mImporteRetencion2 = mImporteRetencion2 + elMonto2;
							   		mCedular = elMonto2.toFixed(2);
								 } 
		        			}else{
		        				alert("segundo else");
		        			}
		        		}else{
		        			alert("no hay nada");
		        		}
			        }
				}
			    catch(e) {
			     	alert(e);
			    }
			}else{
				mImporteRetencion2 = $("#mImporteRetencion2").val() * elProrrateo;
				mImporteRetencion2 = mImporteRetencion2.toFixed(2);
			}
			suma -= Number( mImporteRetencion2 );
			
			suma -= Number( mImportePenalizacion );
			mImporteNeto = suma;
			
			$("#Imp_Iva").val(Math.round(mImporteIVA*100)/100);
			$("#Imp_Retenciones").val(Math.round(mImporteRetencion2*100)/100);
			$("#Imp_Ejercer").val(Math.round((mImporteNeto + mImporteRetencion2)*100)/100);
			$("#Imp_Neto").val(Math.round(mImporteNeto*100)/100);
			ponFormato();
		}
		
		function actVariablestmp(){
			$("#mBruto").val(parseFloat(mImporteBruto));
			$("#mSancion").val(parseFloat(mImporteSancion));
			$("#mDevolucion").val( parseFloat(mImporteDevolucion));
			$("#mAmortizacionAnticipo").val( mAmortizacionAnticipo);
			$("#mIVA").val(parseFloat(mImporteIVA));
			$("#mRetencion").val(mImporteRetencion2);
			$("#mPenalizacion").val( mImportePenalizacion);
			$("#mNeto").val( mImporteNeto);
			$("#m2Millar").val(m2Millar);
			$("#m23IVA").val(parseFloat(m23IVA));
			$("#mISRHonorarios").val(mISRHonorarios);
			$("#m5Millar").val(m5Millar);
			$("#mFletes").val( mFletes);
			$("#mISRArrenda").val( mISRArrenda);
			$("#mCedular").val(mCedular);
			$("#laamortizacion").val( mAmortizacionAnticipo);
			$("#mImporteMasIva").val(parseFloat(mImporteBruto) + parseFloat(mImporteIVA));
		
		}
		
		function quitaFormato(){
			$('.subtotall').each(function(){
				quitaFmtObj( this ) ;
		     });
		/* 	$('.tmpTotall').each(function(){
				quitaFmtObj( this ) ;
		     });
			$('.paso055').each(function(){
				quitaFmtObj( this ) ;
		     }); */
		}
		
		function quitaFmtObj( elObjeto ) {
			var val =0;
			val = elObjeto.value;
			//alert(val);
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
		   	elObjeto.value=val;
		}
		
		function ponFormato(){
			$('.subtotall').each(function(){
				cambiafrmt( this ) ;
		     });
		}
		function cambiafrmt( fld )
		{
		    $("#" + fld.id).formatCurrency();
		}
		
		function cargaRetenciones(){
			 $("#grdRetencion").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		sScrollY : "100",
				sScrollX: "300",
				sScrollXInner: "700",
				
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRetencionesPagoDirecto&qw="+szWhere,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [{ sName: "codigo" },{ sName: "retencion" },{ sName: "porcentaje" }],
				fnInitComplete: function(oSettings, json) {
						calculaIVA(1,$("#Imp_Bruto").val());
					}
			} );
		}
	</script>
</head> 
<body>
<form>
	<table width="100%" align="left" border='0'>
		<tr>
			<td>
				<fieldset>
					<legend>Retenciones Pago Directo</legend>
					<table align="left" border='0' cellpadding="2" width="100%"  > <!-- style="width:650px" -->
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
							<td  class="consumoDirecto">
								Importe Bruto en facturas:<input type="text" id="lblImporteFactura" name="lblImporteFactura"  style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td class="consumoDirecto">
								Importe IVA en facturas:<input type="text" id="lblImporteIvaFactura" name="lblImporteFactura"  style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr> 
						<tr align="left">
							<td class="consumoDirecto">
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
						<tr id="trRetenciones" style="color:#EC1B0F;">
							<td align="left">
								<table>
									<tr>
										<td>
											<div id="divRetenciones"></div>
											<input type="hidden" id="lblRetenciones" name="lblRetenciones" value="Para poder continuar con el proceso debera agregar las retenciones correspondientes" />	
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
			<td>
				<fieldset>
					<legend>Retenciones</legend>
					
						<table align=left border='0' width="100%" > 
							<tr>
								<td>
									<select name="cIdTipoRetencion" id="cIdTipoRetencion" >
										<option selected value="">	SIN RETENCION	</option>
									</select>
									<input type="hidden" id="cTipoRetencion" name="cTipoRetencion" />
									<input type="hidden" id="nPorcRetencion" name="nPorcRetencion" />
								</td>
								<td>
									<input type="button" id="btAgregaMov" class="paso03" name="btAgregaMov" onClick="fnAgregarRet();" value="Agregar" class="btnInterfaceBG"/>
								</td>
								<td>&nbsp;
									<div id="divMilla2">
										<label>	Camara :
											<input name="grpMilla2" type="radio" id="grpMilla2" onclick="habilitaMillar2(0)" value="Camara">
										</label>
										<label> Instituto :
											<input type="radio" id="grpMilla2" name="grpMilla2" onclick="habilitaMillar2(1)" value="Instituto">
										</label>
										&nbsp;&nbsp;&nbsp;&nbsp;
									</div>
								</td>
						</tr>
						<tr>
							<td colspan="3">
								<!-- <table  id="grdRetencion" width="100%" class="display"> style="width:650px"    -->
								<table   class="display" id="grdRetencion">
									<thead>
										<tr>
											<th nowrap>Código</th>		 
											<th>Retención</th>
											<th>Porcentaje</th>
										</tr>
									</thead>
									 <!-- <tbody></tbody>
									<tfoot></tfoot> -->
								</table>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td id="montosFacturas">
				<fieldset>
					<legend>Montos</legend>
					<table>
						<tr>
							<td colspan="2" align="right" nowrap>Importe Bruto:	</td>
							<td nowrap>
								<input style="text-align: right;" name="Imp_Bruto" type="text" class="subtotall" id="Imp_Bruto" onkeypress="valFmt(this,9)" value="0" size="15" maxlength="15" />
							</td>
							<td nowrap>&nbsp;&nbsp;Importe por Ejercer:</td>
							<td width="177">
								<input style="text-align: right;" name="Imp_Ejercer" type="text" class="subtotall" id="Imp_Ejercer" onkeypress="valFmt(this,9)" value="0" size="15" maxlength="15" />
							</td>
						</tr>
						<tr>
							<td colspan="2" align="right" nowrap>Importe IVA:</td>
							<td nowrap>
								<input style="text-align: right;" name="Imp_Iva" type="text" class="subtotall" id="Imp_Iva" onkeypress="valFmt(this,9)" value="0" size="15" maxlength="15" />
							</td>
							<td align="right" nowrap> % IVA: 	</td>
	
							<td nowrap>
								<input style="text-align: right;" name="por_IvaOld" type="hidden" class="subtotall" id="por_IvaOld" onKeyPress="valFmt(this,9)" value="0.16" size="15"
									maxlength="15" />
								<select class="subtotall" id="por_Iva" name="por_Iva" style="width: 10em;"></select>
							</td>
						</tr>
						 <tr>
							<td colspan="2" align="right" nowrap> Importe Retenciones: </td>
							<td nowrap>
								<input style="text-align: right;" name="Imp_Retenciones" type="text" class="subtotall" id="Imp_Retenciones" onkeypress="valFmt(this,9)" value="0" size="15"
									maxlength="15" />
							</td>
							<td nowrap> &nbsp;&nbsp; </td>
							<td nowrap>&nbsp;  </td>
						</tr>
						<tr>
							<td colspan="2" align="left"></td>
	
							<td align="left"> Importe Neto: </td>
							<td>
								<input style="text-align: right;" name="Imp_Neto" type="text" class="subtotall" id="Imp_Neto" onKeyPress="valFmt(this,9)" value="0" size="15" maxlength="15" />
							</td>
						</tr>
				
					</table>
				</fieldset>
			</td>
		</tr>
		<!-- <tr>
			<td align="left">
				<input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" />
			</td>
		</tr> -->
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

	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
	<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
	<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion"/>
	<input type="hidden" id="cIdFolio" name="cIdFolio" />

	<input type="hidden" id="nIdEstado" name="nIdEstado" />
	<input type="hidden" id="Imp_BrutoHidden" name="Imp_BrutoHidden" />
	<input type="hidden" id="Imp_IvaHidden" name="Imp_IvaHidden" />
	<input type="hidden" id="Imp_NetoHidden" name="Imp_NetoHidden" />
	<input type="hidden" id="cImporteBrutoTmp" name="cImporteBrutoTmp" />
	<input type="hidden" id="cIvaTmp" name="cIvaTmp" />
	<input type="hidden" id="cImporteNetoTmp" name="cImporteNetoTmp" />
	<input type="hidden" id="nFolioPagoDirecto" name="nFolioPagoDirecto" />
	
	<input type="hidden" id="aux" name="aux" />
	<input type="hidden" id="mImporteSancion" name="mImporteSancion" />
	
	<input type="hidden" id="nPorcentajeIVA" name="nPorcentajeIVA" />
	<input type="hidden" id="tipoRet" name="tipoRet" />
	<input type="hidden" id="cveRetencion" name="cveRetencion" />
	<input type="hidden" id="id_caso" name="id_caso" />
	
	<input type="hidden" id="rfcLength" name="rfcLength" />
	<input type="hidden" id="numRetencion" name="numRetencion" />
	
	 
</form>    
</body>
</html>
