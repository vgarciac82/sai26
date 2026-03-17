<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	//Calendar c2 = Calendar.getInstance();
	//c2.add(Calendar.DATE, 20);
	//String vigencia = sdf.format(c2.getTime());
	String today = sdf.format(c1.getTime());
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	
	
	
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
	var oTable;
	var zWhere;
	var mImporte;
	var importeTotalDocumento;
	var importeNetoFactura;

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
			Map botones=nb.getBotones(roles,"PagosDirectos","FacturasPagoDirecto");
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
			
		$("#pbAgregar").button().click(function() {
			if(validaDatosFactura()){
				$("#cIdFactura").val($("#cFactura").val()+'-'+$("#cSerie").val());
				
				if(validaInsertar()){
	  				var importeTotalFacturas=parseFloat(quitaFmt($("#mTotalFacturaV").val())) + parseFloat(quitaFmt($("#mImporteTotalNeto").val()));
					if(validaMontos(importeTotalDocumento,importeTotalFacturas)){
						oTablevFact = $('#grdFacturas').dataTable();
						var aData = oTablevFact.fnGetData();
						for(var i=0; i<aData.length; i++) {
							var cFactura = aData[ i ][ 7 ];
							if( $("#cIdFactura").val()== cFactura){
								alert("La Factura ya está capturada");
								$("#cSerie").val( "" );
								$("#cFactura").val( "" );
								$("#mImporteTotalBruto").val( "" );
								$("#mImporteTotalNeto").val( "" );
								$("#fechaFactura").val( "" );
								return;
							}
						}
						$("#mImptBrutoTotal").val($("#mImporteTotalBruto").val());
						//insertamos en la tabla tPagoFactura
						queryFormPost("tPagoFacturaInsert", {async:false});
						formato();
						cargaFacturas();
						$("#mSubTotalAcumulado").formatCurrency();
						if($("#ID_DESTINO_GASTO").val() == "AL"){
							cargaFacturasAlmacen();
						}else{
							cargaConsumoDirecto();
						}
						//si se ha alcanzado el total de factura se deshabilita el boton
						if(importeTotalDocumento==importeTotalFacturas){
							$("#pbAgregar").attr('disabled', true);
						}
						limpiaCampos();
					}else{
						alert("La suma de sus facturas sobrepasa el monto total neto del documento");
					}
	           	}else{
					alert("La factura que desea ingresar ya se encuentra registrada en el documento "+$("#cIdDocumentoExiste").val());
					$("#idDocumentoPago").val("");
				}
			}
		});	
		
		$("#btnGuardaFacturas").button().click(function (){
			var importeTotalFacturasTable=0.0;
			var aTrs = $("#grdFacturas").dataTable().fnGetNodes();
			for ( var i=0 ; i < aTrs.length ; i++ ){
				aData =  $("#grdFacturas").dataTable().fnGetData(aTrs[i]);
				$("#folioPago").val(aData[0]);
				$("#cIdFactura").val(aData[7]);
				$("#importeFacturaHidden").val($("#mImporteNeto_"+aData[0]+"_"+aData[8]+"").val());
				$("#fechaHidden").val($("#fechaFactura_"+aData[0]+"_"+aData[8]+"").val());
				var fecha=$("#fechaHidden").val().split("/");
				if(fecha[2]!=$("#cAnioTmp").val()){
					alert("El año de la factura "+$("#cIdFactura").val()+"no debera ser distinta al año que se capturo al crear el documento");
					$("#fechaFactura_"+aData[0]+"_"+aData[8]+"").val(aData[10]);
					continue; 
				}
				
				queryFormPost("mImporteSubtotal", {async:false});
				var importeFactura=parseFloat(quitaFmt($("#importeFacturaHidden").val()));
				var importeSubtotalFac=parseFloat(quitaFmt($("#mSubTotalFactura").val()));
				if(!validaMontos(importeFactura, importeSubtotalFac) ){
					alert("El monto de la factura "+$("#cIdFactura").val() +" es diferente a la suma de su detalle");
					$("#mImporteNeto_"+aData[0]+"_"+aData[8]+"").val(aData[9]);
					$("#mImporteNeto_"+aData[0]+"_"+aData[8]+"").formatCurrency();
					continue;	
				}
			
			}
			
			var importeBd=0.0;
			var folios="'";
			for ( var i=0 ; i < aTrs.length ; i++ ){
				aData =  $("#grdFacturas").dataTable().fnGetData(aTrs[i]);
				$("#importeFacturaHidden").val($("#mImporteNeto_"+aData[0]+"_"+aData[8]+"").val());
				importeTotalFacturasTable=importeTotalFacturasTable + parseFloat(quitaFmt($("#importeFacturaHidden").val()));
				
				folios=folios+aData[7]+"','";
				/* if(!validaMontos(importeTotalDocumento, importeTotalFacturas) ){
					alert("La suma de los montos de las facturas sobrepasa el monto total bruto del documento");
					return -1;	
				} */
			}
			
			folios=folios.substring(0, folios.length-2);
			//////////////////////////////////////////////
			var where = "where nFolioPago="+$("#folioPago").val()+" and cIdFactura not in ("+folios+")";
			var szWhere = "";
			var szTabla = "MONTOMODIFICADO";
							
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
          		var disponible = 0.0;
           		
				for (var i = 0; i < j.length; i++) {
					importeBd=j[i].Col0;
				} 
			
				var granTotal=parseFloat(importeTotalFacturasTable)+parseFloat(importeBd);
				granTotal=granTotal.toFixed(2);
			   	if (granTotal >importeTotalDocumento){
					alert("La suma es incorrecta");
						return;
			   	}else{
			   	
			   		for ( var i=0 ; i < aTrs.length ; i++ ){
						aData =  $("#grdFacturas").dataTable().fnGetData(aTrs[i]);
						$("#folioPago").val(aData[0]);
						$("#cIdFactura").val(aData[7]);
						$("#folioHidden").val($("#cfactura_"+aData[0]+"_"+aData[8]+"").val());
						$("#serieHidden").val($("#cSerie_"+aData[0]+"_"+aData[8]+"").val());
						$("#cIdFacturaHidden").val($("#folioHidden").val()+'-'+$("#serieHidden").val());
						$("#fechaHidden").val($("#fechaFactura_"+aData[0]+"_"+aData[8]+"").val());
						$("#importeFacturaHidden").val($("#mImporteBruto_"+aData[0]+"_"+aData[8]+"").val());
						$("#importeFacturaHiddenNeto").val($("#mImporteNeto_"+aData[0]+"_"+aData[8]+"").val());
					    queryFormPost("updateFacturaEncabezado", {async:false});
					}
					$("#detalleFacturaDiv").css("display","none");
					//cargaFacturas();
			   	}
			   	$("#mSubTotalAcumulado").formatCurrency();
			   	cargaFacturas();
			});
			////////////////////////////////////////////////
		});
		
		$("#btnGuardarDetalle").button().click(function (){
			var importeTotalFactura=parseFloat(quitaFmt($("#mImporteFactura").val()));
			var importeTotalFacturasTable=0.0;
			if($("#ID_DESTINO_GASTO").val() == "CD"){	
				var aTrs = $("#grdFacturasConsumo").dataTable().fnGetNodes();
						
				///////////////////////////////////////////////////////////////////////////////////
				
				var importeBd=0.0;
				var consecutivos="";
				for ( var i=0 ; i < aTrs.length ; i++ ){
					aData =  $("#grdFacturasConsumo").dataTable().fnGetData(aTrs[i]);
					$("#importeHiddenNeto").val($("#mImporteNetoDetalle_"+aData[0]+"_"+aData[6]+"").val());
					importeTotalFacturasTable=importeTotalFacturasTable + parseFloat(quitaFmt($("#importeHiddenNeto").val()));
					
					consecutivos=consecutivos+aData[6]+","; 
				}
			
				
				consecutivos=consecutivos.substring(0, consecutivos.length-1);
				//////////////////////////////////////////////
				var where = "where nFolioPago="+$("#folioPago").val()+" and cIdFactura='"+$("#cIdFactura").val()+"' and nIdConsecutivo not in ("+consecutivos+")";
				var szWhere = "";
				var szTabla = "MONTOMODIFICADODETALLE";
								
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
	          		var disponible = 0.0;
	           		
					for (var i = 0; i < j.length; i++) {
						importeBd=j[i].Col0;
					}
					var granTotal=parseFloat(importeTotalFacturasTable)+parseFloat(importeBd);
					
					granTotal=granTotal.toFixed(2);
			 	//	if(granTotal<importeTotalFactura || umbral > .03){
				   	if (granTotal >importeTotalFactura){
						alert("La suma del detalle es incorrecta");
							return;
				   	}else{
				   		var aTrs1 = $("#grdFacturasConsumo").dataTable().fnGetNodes();
				   		var aData1;
				   		
				   		for ( var x=0 ; x < aTrs1.length ; x++ ){
							aData1 =  $("#grdFacturasConsumo").dataTable().fnGetData(aTrs1[x]);
							$("#folioPago").val(aData1[0]);
							$("#consecutivoFactura").val(aData1[6]);
							$("#importeHidden").val(quitaFmt($("#mImporteBrutoDetalle_"+aData1[0]+"_"+aData1[6]+"").val()));
							$("#importeHiddenNeto").val(quitaFmt($("#mImporteNetoDetalle_"+aData1[0]+"_"+aData1[6]+"").val()));
							$("#alta").val('0|0|0');
							
							queryFormPost("updatePagoFactura", {async:false});	
						}
					/* 	queryFormPost("obtenSubTotalAcumulado", {async:false});
						$("#mSubTotalAcumulado").formatCurrency(); */
						validaPestana();
						cargaFacturas();
						cargaConsumoDirecto();
				   	}
				   //	
				});
					
				//////////////////////////////////////////////////////////////////////////////////
				
			
			}else if($("#ID_DESTINO_GASTO").val() == "AL"){
				var aTrs = $("#grdValidaFacturas").dataTable().fnGetNodes();
	
				///////////////////////////////////////////////////////////////////////////////////
				
				var importeBd=0.0;
				var consecutivos="";
				for ( var i=0 ; i < aTrs.length ; i++ ){
			
					aData =  $("#grdValidaFacturas").dataTable().fnGetData(aTrs[i]);
					$("#importeHiddenNeto").val($("#mImporteNetoDetalle_"+aData[0]+"_"+aData[8]+"").val());
					importeTotalFacturasTable=importeTotalFacturasTable + parseFloat(quitaFmt($("#importeHiddenNeto").val()));					
					consecutivos=consecutivos+aData[8]+","; 
				}
				
				consecutivos=consecutivos.substring(0, consecutivos.length-1);
				//////////////////////////////////////////////
				var where = "where nFolioPago="+$("#folioPago").val()+" and cIdFactura='"+$("#cIdFactura").val()+"' and nIdConsecutivo not in ("+consecutivos+")";
				var szWhere = "";
				var szTabla = "MONTOMODIFICADODETALLE";
		
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
	          		var disponible = 0.0;
	           		
					for (var i = 0; i < j.length; i++) {
						importeBd=j[i].Col0;
					}
					var granTotal=parseFloat(importeTotalFacturasTable)+parseFloat(importeBd);
					granTotal=granTotal.toFixed(2);
				   	if (granTotal >importeTotalFactura){
						alert("La suma del detalle es incorrecta");
							return;
				   	}else{
				   		var aTrs1 = $("#grdValidaFacturas").dataTable().fnGetNodes();
				   		var aData1;
				   		for ( var x=0 ; x < aTrs1.length ; x++ ){
							aData1 =  $("#grdValidaFacturas").dataTable().fnGetData(aTrs1[x]);
							$("#folioPago").val(aData1[0]);
							$("#consecutivoFactura").val(aData1[8]);
							$("#importeHidden").val(quitaFmt($("#mImporteBrutoDetalle_"+aData1[0]+"_"+aData1[8]+"").val()));
							$("#importeHiddenNeto").val(quitaFmt($("#mImporteNetoDetalle_"+aData1[0]+"_"+aData1[8]+"").val()));
							queryFormPost("updatePagoFactura", {async:false});	
						}
						queryFormPost("obtenSubTotalAcumulado", {async:false});
						$("#mSubTotalAcumulado").formatCurrency();
						validaPestana();
						cargaFacturasAlmacen();
				   	}
				});
					
				//////////////////////////////////////////////////////////////////////////////////
				
		    }
		    	
		});	
			
		$("#btnAgregaDetalle").button().click(function (){
			if(validaDatosDetalle()){
				if($("#ID_DESTINO_GASTO").val() == "CD"){
				
					oTablevFact = $('#grdFacturasConsumo').dataTable();
					var aData = oTablevFact.fnGetData();
					for(var i=0; i<aData.length; i++) {
						var claveEp = $("#claveEp_"+aData[ i ][0]+"_"+aData[ i ][6]+"").val();
						var partida = $("#pagoDirectoPartida_"+aData[ i ][0]+"_"+aData[ i ][6]+"").val().split('-');
						if( $("#claveEp").val()== claveEp && $("#cIdTipoPartida").val()==partida[0]){
							alert("El detalle ya ha sido capturado");
							$("#mImporteFactNeto").val( "" );
							$("#mImporteFactBruto").val( "" );
							return;
						}
						
					}
				}else if($("#ID_DESTINO_GASTO").val() == "AL"){
					oTablevFact = $('#grdValidaFacturas').dataTable();
					var aData = oTablevFact.fnGetData();
					for(var i=0; i<aData.length; i++) {
					
						var claveEp = $("#claveEp_"+aData[ i ][0]+"_"+aData[ i ][8]+"").val();
						var partida = $("#pagoDirectoPartida_"+aData[ i ][0]+"_"+aData[ i ][8]+"").val().split('-');
						var alta=$("#altaAlmacenaria_"+aData[ i ][0]+"_"+aData[ i ][8]+"").val();
						//var idAlmacen=$("#nIdAlmacen_"+aData[ i ][0]+"_"+aData[ i ][8]+"").val();
						var idAlmacen=aData[ i ][9]; 
										
						if( $("#claveEp").val()== claveEp && $("#cIdTipoPartida").val()==partida[0] && $("#altaAlmacenaria").val()==alta && $("#cAlmacenTmp").val()==idAlmacen){
							alert("El detalle ya ha sido capturado");
							$("#mImporteFactNeto").val( "" );
							$("#mImporteFactBruto").val( "" );
							$("#altaAlmacenaria").val("");
							return;
						}
					
					}
				
				}
				
				var mImporte=0.0;
				mImporte = parseFloat(quitaFmt( $("#mImporteFactNeto").val()));
				var mImporteSubtotal;
				mImporteSubtotal=parseFloat(mImporte)+parseFloat(quitaFmt( $("#mSubTotalFactura").val()));
						
				if(mImporteSubtotal>parseFloat(quitaFmt($("#mImporteFactura").val()))){
					alert("El suma de su detalle sobrepasa el monto total de su factura");
					return;
				}
				$("#mImporteFactNeto").formatCurrency();
			
				//suficiencia por Clave
				var mes =["MontoEnero","MontoFebrero","MontoMarzo","MontoAbril","MontoMayo","MontoJunio","MontoJulio","MontoAgosto","MontoSeptiembre","MontoOctubre","MontoNoviembre","MontoDiciembre"];
				var mesActual = parseInt($("#mes").val(),10)-1;
				var campos="";
				for(var i=0; i<=mesActual; i++){
					campos=campos+mes[i];
					campos=campos+"+";	
				}
				campos=campos.substring(0, campos.length-1);
				
				queryFormPost("totalEnClave", {async:false});
				
				var totalEp=parseFloat(quitaFmt($("#totalEp").val()));
				
				//mImporte=(parseFloat(mImporte)+parseFloat(totalEp))*(1+parseFloat($("#nPorcentajeIVA").val()));
				mImporte=(parseFloat(mImporte)+parseFloat(totalEp));
				
				var where = " where " + " cSubCuenta='" + $("#claveEp").val() + "'";
				var szWhere = "";
				var szTabla = "VDISPONIBLEPAGODIRECTOEP";
							
				$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,Campos:campos,ajax: 'false'}, function(j){
	          		var disponible = 0.0;
	           		
					for (var i = 0; i < j.length; i++) {
						disponible = parseFloat(disponible) + parseFloat(j[i].Col0);
					} 
				   	if (disponible < mImporte){
						alert("La clave que ha seleccionado no cuenta con el monto disponible necesario para cubrir el monto de su factura.");
							return;
				   	}else{
				   		insertaDetalleFactura();	
				   	}
				});
			
			} 
				
		});	
		
		$('#grdFacturas tr').live('click', function() {
			$("#detalleFacturaDiv").css("display","");
			if ($(this).hasClass('row_selected') ) {            
				$(this).removeClass('row_selected'); 
				var aTrs = $('#grdFacturas').dataTable().fnGetNodes();
				$("#detalleFacturaDiv").css("display","none");
			}else{
			    var aTrs = $('#grdFacturas').dataTable().fnGetNodes();
		        for(var i=aTrs.length;i>=0; i-- ){       
					$(aTrs[i]).removeClass('row_selected'); 
					$(this).addClass('row_selected'); 
			    }
			}
			for ( var i=aTrs.length ; i>=0; i-- ){
				if ($(aTrs[i]).hasClass('row_selected')){   
					aData =  $("#grdFacturas").dataTable().fnGetData(aTrs[i]);
					$(this).addClass('row_selected'); 
					$("#folioPago").val(aData[0]);
					$("#cIdFactura").val(aData[7]);
					$("#cFacturaAlta").val(aData[7]);
					$("#mImporteFactura").val($("#mImporteNeto_"+aData[0]+"_"+aData[8]+"").val());
					$("#mImporteFactura").css("color","red");
					
					queryFormPost("obtenSubTotalAcumulado", {async:false});
					$("#mSubTotalAcumulado").formatCurrency();
					if($("#ID_DESTINO_GASTO").val() == "AL"){
						queryFormPost("obtieneAlmacen", {async:false});
						cargaFacturasAlmacen();
						limpiaDetalle();
					}else{
						cargaConsumoDirecto();
						limpiaDetalle();
					}
				}
			}
		});	
		
	
						
	});
		
	
	function validaDatosFactura(){   
		var fechaFactura=$('#fechaFactura');
		var cFactura = $('#cFactura');
		var mImporteTotal = $('#mImporteTotalNeto');
		allFields = $( [] ).add(fechaFactura).add(cFactura).add(mImporteTotal),
		tips = $( ".validateTips" );
		var bValid = true;
	    tips.text("");
	    allFields.removeClass( "ui-state-error" );
		bValid = bValid&& checkRequerido(fechaFactura, "Fecha de expedición de la factura");
		bValid = bValid&& checkRequerido(cFactura, "Número de folio de la factura");
		bValid = bValid&& checkRequerido(mImporteTotal, "Importe total bruto de  la factura");
		
		
		var fecha=$('#fechaFactura').val().split("/");
		var anio=fecha[2];
		if(anio!=$("#cAnioTmp").val()){
			if($("#ID_DESTINO_GASTO").val() == "AL"){
				alert("La factura no debera ser distinta al año que se capturo al crear el documento");
			}else{
				alert("El año de la factura no debera ser distinta al año fiscal en curso");
			}
			
			bValid=false;
		}
		
		
		
		return bValid;
	}
		
	function validaDatosDetalle(){   
		var mImporteFactNeto=$('#mImporteFactNeto');
		var cIdTipoPartida=$('#cIdTipoPartida');
		var claveEp=$('#claveEp'); 
		 if($("#ID_DESTINO_GASTO").val() == "AL"){
			var altaAlmacenaria = $('#altaAlmacenaria');
		//	var ALM = $('#ALM');			
		} 
		
		if($("#ID_DESTINO_GASTO").val() == "AL"){
			allFields = $( [] ).add(mImporteFactNeto).add(cIdTipoPartida).add(claveEp).add(altaAlmacenaria),//.add(ALM)
			tips = $( ".validateTips" );
		}else{ 
			allFields = $( [] ).add(mImporteFactNeto).add(cIdTipoPartida).add(claveEp),
			tips = $( ".validateTips" );
		}
		
		var bValid = true;
	    tips.text("");
	    if($("#ID_DESTINO_GASTO").val() == "AL"){
			bValid = bValid&& checkRequerido(altaAlmacenaria, "Alta almacenaria");
			//bValid = bValid&& checkRequerido(ALM, "Almacén");
		} 
		bValid = bValid&& checkRequerido(mImporteFactNeto, "Importe neto de  la factura");
		bValid = bValid&& checkRequerido(cIdTipoPartida, "Número de partida");
		bValid = bValid&& checkRequerido(claveEp, "Clave presupuestal");	
		return bValid;
	}
		
	function insertaDetalleFactura(){
		
		if($("#ID_DESTINO_GASTO").val() == "CD"){
			$("#altaAlmacenaria").val('0');
			$("#cAnioTmp").val('0');
			$("#cFacturaAlta").val('0');
			$("#almInsert").val('0');
		}else{
			$("#almInsert").val($("#cAlmacenTmp").val());
		}
						
		var importeTotalFactura=$("#mImporteFactura").val();
		
		$("#alta").val($("#altaAlmacenaria").val()+'|'+$("#cAnioTmp").val()+'|'+$("#cFacturaAlta").val());
		$("#mImptBruto").val(quitaFmt($("#mImporteFactBruto").val()));
		queryFormPost("insertPagoDirectoDetalle", {async:false});
		if($("#ID_DESTINO_GASTO").val() == "AL"){
			queryFormPost("obtenSubTotalAcumulado", {async:false});
			$("#mSubTotalAcumulado").formatCurrency();
			validaPestana(); 
			cargaFacturasAlmacen();
			limpiaDetalle();
			
		}else{
			queryFormPost("obtenSubTotalAcumulado", {async:false});
			$("#mSubTotalAcumulado").formatCurrency();
			validaPestana(); 
			cargaConsumoDirecto();
			limpiaDetalle();
			
		}
	}
	function init(){
	
		$("#cIdFolio").val("<%=cIdDocumento%>");
		$("#cCentroContable").val("<%=cCentroContable%>");
		$("#cIdUnidadEjecutora").val("<%=cIdUnidadEjecutora%>");
		queryFormPost("mPagoDirectoRead",{async:false});
		if($("#DESTINO_GASTO").val() == "AL"){
			queryFormPost("obtieneAlmacen", {async:false});
		}
			
		queryFormPost("mPagoDirectoFacturas",{async:false});
		queryFormPost("mPagoDirectoInput",{async:false});
		/* querySelectPost("CAT_TIPO_IVAporCienRead", "por_Iva", {async: false }); */
		queryFormPost("mPagoDirectoMontos",{async:false});
		
		agregaFecha();
		
		buscaPartida();
		cargaFacturas();
		if($("#ID_DESTINO_GASTO").val() == "CD"){		
					
			$("#altaAlmacenariaTr").css("display","none");
			$("#idAlmacenTr").css("display","none");
			
			$("#altaAlmacenaria").val('0');
			$("#cAlmacenTmp").val('0');
			$("#cFacturaAlta").val('0');
			$("#tipoAlmacen").css("display","none");
			$("#tipoConsumoDirecto").css("display","block");
			cargaConsumoDirecto();
		}else if($("#ID_DESTINO_GASTO").val() == "AL"){
			
		//	querySelectPost("CatalogoAlmacenRead","ALM", {async:false});
			$("#cFacturaAlta").val();
			$("#altaAlmacenariaTr").css("display","");
			$("#tipoAlmacen").css("display","block");
			$("#tipoConsumoDirecto").css("display","none");
			cargaFacturasAlmacen();
		}
		
		$("#lblBrutoFactura").val($("#lblImporteNetoFactura").val());
		var importetotal=Number($("#Imp_Bruto").val())+Number($("#Imp_Iva").val());
		$("#Imp_Neto").val(importetotal);
		formato();
		//importeTotalDocumento=parseFloat(quitaFmt($("#lblBrutoFactura").val()));
		/* importeTotalDocumento=parseFloat(quitaFmt($("#lblImporteNetoFactura").val()));
		
		importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
		var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));

		Imp_Neto=Imp_Neto.toFixed(2);
		importeNetoFactura=importeNetoFactura.toFixed(2);
		umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
		
		
		if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
			$("#apartadoPagoDirecto").css("display", "block");
		}else{
			$("#apartadoPagoDirecto").css("display", "none");				
		} 	 */
		
		if ($("#nIdEstado").val()==1){
			$(".habilitaCampos").attr('disabled', false);
		}else{
			$(".habilitaCampos").attr('disabled', true);
		}		
		
		if(importeTotalDocumento==parseFloat(quitaFmt($("#mTotalFacturaV").val()))){
			$("#pbAgregar").attr('disabled', true);
		}
		
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
		//	$("#apartadoPagoDirecto").css("display", "block");
			$("#retencionesPagoDirecto").css("display", "block");
			$("#partidasPagoDirecto").css("display", "block");	
			$("#pagosPagoDirecto").css("display", "block");				
			if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
				$("#apartadoPagoDirecto").css("display", "block");
			}else{
				$("#apartadoPagoDirecto").css("display", "none");				
			} 
		}else{
			$("#apartadoPagoDirecto").css("display", "none");	
			$("#partidasPagoDirecto").css("display", "none");	
			$("#retencionesPagoDirecto").css("display", "none");
			$("#pagosPagoDirecto").css("display", "none");			
		}
		$("#mSubTotalAcumulado").formatCurrency();
	}
	function cargaFacturasAlmacen(){
		/* var optionsTem = "", options = "";
		var where ="where cTCONC='"+$("#ID_TIPO_CONCEPTO").val()+"' AND ID_DESTINO_GASTO='"+$("#ID_DESTINO_GASTO").val()+"'";
		$.getJSON("../../catalogos/SelectJson.jsp", {Tabla: "PAGODIRECTOPARTIDA", MaxReg: where, ajax: false},
			function(data){
				for(var i=0; i < data.length; i++)
					options+="<option value='"+data[i].Col1+"' #"+data[i].Col1+"##>"+data[i].Col0+"</option>";
		}); */
		
		zWhere=$("#nFolioPagoDirecto").val()+",'"+$("#cIdUnidadEjecutora").val()+"','"+$("#cCentroContable").val()+"','"+$("#cIdFactura").val()+"'";
		$("#grdValidaFacturas").dataTable({
			sScrollX: "120%",
			//sScrollXInner: "440%",
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
			bServerSide: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mDetalleFacturaAlmacen("+zWhere+")",
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aaSorting: [[ 1, "asc" ]] ,
			aoColumns: [{ sName: "nFolioPago", bVisible:false },
						{ sName: "altaAlmacenaria" },
						{ sName: "almacen" },
						{ sName: "mImporteBruto" },
						{ sName: "mImporteNeto" },
						{ sName: "partida"},
						{ sName: "claveEp" },
						{sName:"eliminaFactura"},
						{sName:"numFactura",bVisible:false},
						{sName:"idAlmacen",bVisible:false}		]
				/* ,fnRowCallback: function( nRow, aData, iDisplayIndex ){
				optionsTem = options;
				optionsTem = optionsTem.replace("#"+aData[4]+"##","selected");
				optionsTem = optionsTem.replace(optionsTem.substring(optionsTem.indexOf("#"),optionsTem.indexOf("##")+2),"");
				var numFactura=aData[7];
				var folio=aData[0];
				$("td:eq(3)", nRow).html("<select id='pagoDirectoPartida_"+folio+"_"+numFactura+"' style='width:25em;' onChange='editaPartida(this,"+folio+","+numFactura+")'>"+optionsTem+"</select>");
				$("#pagoDirectoPartida_"+folio+"_"+numFactura+"").val(aData[4]);
				return nRow;
			}   */ 
       	});
       	queryFormPost("mImporteSubtotal", {async:false});
		$("#mSubTotalFactura").formatCurrency();
				
	}
		
		
	function cargaConsumoDirecto(){
		/* var optionsTem = "", options = "";
		var where ="where cTCONC='"+$("#ID_TIPO_CONCEPTO").val()+"' AND ID_DESTINO_GASTO='"+$("#ID_DESTINO_GASTO").val()+"'";
		$.getJSON("../../catalogos/SelectJson.jsp", {Tabla: "PAGODIRECTOPARTIDA", MaxReg: where, ajax: false},
			function(data){
				for(var i=0; i < data.length; i++)
					options+="<option value='"+data[i].Col1+"' #"+data[i].Col1+"##>"+data[i].Col0+"</option>";
		}); */
		
		zWhere=$("#nFolioPagoDirecto").val()+",'"+$("#cIdUnidadEjecutora").val()+"','"+$("#cCentroContable").val()+"','"+$("#cIdFactura").val()+"'";
		$("#folioPago").val($("#nFolioPagoDirecto").val());
		$("#grdFacturasConsumo").dataTable({
			sScrollX: "120%",
			//sScrollXInner: "440%",
			bScrollCollapse: true,
			bDestroy: true,
			 iDisplayLength: 2,
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
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mDetalleFacturaConsumo("+zWhere+")",
			bProcessing: true,
			sPaginationType: "full_numbers",
			bJQueryUI: true,
			aaSorting: [[ 1, "asc" ]] ,
			aoColumns: [{ sName: "nFolioPago", bVisible:false },
						{ sName: "mImporteBruto" },
						{ sName: "mImporteNeto" },
						{ sName: "partida"},
						{ sName: "claveEp" },{sName:"eliminaFactura"},
						{sName:"numFactura",bVisible:false}	]
			/* ,fnRowCallback: function( nRow, aData, iDisplayIndex ){
				optionsTem = options;
				optionsTem = optionsTem.replace("#"+aData[2]+"##","selected");
				optionsTem = optionsTem.replace(optionsTem.substring(optionsTem.indexOf("#"),optionsTem.indexOf("##")+2),"");
				var numFactura=aData[5];
				var folio=aData[0];
				$("td:eq(1)", nRow).html("<select id='pagoDirectoPartida_"+folio+"_"+numFactura+"' style='width:25em;' onChange='editaPartida(this,"+folio+","+numFactura+")'>"+optionsTem+"</select>");
				$("#pagoDirectoPartida_"+folio+"_"+numFactura+"").val(aData[2]);
				return nRow;
			}  */  
       	});
       	
       	queryFormPost("mImporteSubtotal", {async:false});
		$("#mSubTotalFactura").formatCurrency();			
	}
		function editaPartida(partida,folio,factura){
			$("#cIdTipoPartidaEdit").val(partida.value);
			querySelectPost("mCatalogoClaveEp", "claveEp_"+folio+"_"+factura, {async: false }); 
		}
		
		function eliminaFactura(folioPago, consecutivoFactura){
			$("#folioPago").val(folioPago);
			$("#cIdFactura").val($("#cfactura_"+folioPago+"_"+consecutivoFactura+"").val()+'-'+$("#cSerie_"+folioPago+"_"+consecutivoFactura+"").val());
			//verifica si existe detalle para esa factura
			queryFormPost("existeDetalleFactura", {async:false});
			if($("#existeDetalle").val()!=0){
				var res=confirm("La factura que desea eliminar cuenta con un detalle de claves, ¿Está seguro que desea eliminarla?. Esta acción no podra revertirse");
				if(res){
					//elimina detalle y factura
					queryFormPost("eliminaDetalleFactura", {async:false});
					queryFormPost("eliminaFacturaPagoDirecto", {async:false});
					$("#pbAgregar").attr('disabled', false);
					validaPestana();
				}else{
					return;
				}
			}else{
				queryFormPost("eliminaFacturaPagoDirecto", {async:false});
				$("#pbAgregar").attr('disabled', false);
			}
			cargaFacturas();
			if($("#ID_DESTINO_GASTO").val() == "AL"){
				cargaFacturasAlmacen();
			}else{
				cargaConsumoDirecto();
			} 
			
			
		} 
		
		
		function eliminaDetalleFactura (folioPago, nIdConsecutivo){
			$("#folioPago").val(folioPago);
			$("#nIdConsecutivo").val(nIdConsecutivo);
			
			queryFormPost("eliminaFacturaDetallePagoDirecto", {async:false});
			queryFormPost("updateConsecutivo", {async:false});
			 if($("#ID_DESTINO_GASTO").val() == "AL"){
				cargaFacturasAlmacen();
			}else{
				cargaConsumoDirecto();
			} 
			validaPestana();
		}
		//PAGOaPARTADO
		
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

	/* 	function quitaFormato(){
			$('.subtotall').each(function(){
				quitaFmtObj( this ) ;
		     });
		} */
		
		/* function quitaFmtObj( elObjeto ) {
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
		} */
		
		/* function cambiafrmt( fld )
		{
		    $("#" + fld.id).formatCurrency();
		}
		 */
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
		function buscaClave(){
			$("#cIdTipoPartidaEdit").val($("#cIdTipoPartida").val());
			querySelectPost("mCatalogoClaveEp", "claveEp", {async: false }); 
		}
		function buscaPartida(){
			querySelectPost("mPartidaPagoDirecto", "cIdTipoPartida", {async: false });
			buscaClave();
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
			alert(t);
	   }

	   function formato(){ 
	   		$("#lblImporteBruto").val($("#Imp_Bruto").val());
			$("#lblIVA").val($("#Imp_Iva").val());
			$("#lblImporteNeto").val($("#Imp_Neto").val());
			$("#lblImporteBruto").formatCurrency();
			$("#lblIVA").formatCurrency();
			$("#lblImporteNeto").formatCurrency();
			$("#lblImporteFactura").formatCurrency();
			$("#lblImporteIvaFactura").formatCurrency();
			$("#lblImporteNetoFactura").formatCurrency();
			/* $("#por_Iva").val($("#nPorcentajeIVA").val()); */
			$("#mTotalFacturaV").formatCurrency();	
			$("#Imp_Ejercer").formatCurrency();
			$("#lblBrutoFactura").formatCurrency();
			$("#lblBrutoFactura").css("color","red");
			
	   }
	   
	   function limpiaCampos(){
	   		
	   		$("#fechaFactura").val("");
	   		$("#cSerie").val("");
	   		$("#cFactura").val("");
	   		$("#mImporteTotalNeto").val("");
	   		$("#mImporteTotalBruto").val("");
	   		
	   }
	  
	  function limpiaDetalle(){	  	
	  	 $("#eliminaDetalleFactura").val("");
	  	 $("#mImporteFactNeto").val("");
	  	 $("#mImporteFactBruto").val("");
	  	 $("#altaAlmacenaria").val("");
	  	 querySelectPost("mPartidaPagoDirecto", "cIdTipoPartida", {async: false });
		 //querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
	  	 buscaClave();
	  	
	  }
	  
	  function validaInsertar(){
	  	queryFormPost("existeFactura",{async:false});
	  	if($("#idDocumentoPago").val()==1){ //existe en OTRA FACTURA
	  		return false;
	  	}else{
	  		return true;
	  	}
	  }
	  
	  
	  function validaMontos(importeTotalDocumento,importeTotalFacturas){
		if(Number(importeTotalDocumento)>=Number(importeTotalFacturas) ){
	  		return true;
	  	}else{
	  		return false;
	  	}
	  }
	  
	  
	  function cargaFacturas(){
	  	zWhere=$("#nFolioPagoDirecto").val()+",'"+$("#cIdUnidadEjecutora").val()+"','"+$("#cCentroContable").val()+"'";
	  	$("#grdFacturas").dataTable({
				sScrollX: "110%",
				//sScrollXInner: "100%",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mPagoDirectoFactura("+zWhere+")",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [{ sName: "nFolioPago", bVisible:false },
							{ sName: "fechaFactura" },
							{ sName: "cfactura" },
							{ sName: "cSerie" },
							{ sName: "mImporteTotalBruto" },
							{ sName: "mImporteTotalNeto" },
							{ sName: "eliminaFactura"},
							{ sName: "cIdFactura", bVisible:false},
							{ sName: "number", bVisible:false},
							{ sName: "montoAnt", bVisible:false},
							{ sName: "fechaAnt", bVisible:false}
							]
				
        	});
        	queryFormPost("mTotalFacturas", {async:false});	
        	$("#mTotalFacturaV").formatCurrency();
	  }
	  
	  function generaReporte() {
		window.open("../../servlet/SeguridadCatalogosMateriales?" + "catalogo=REPORTE"
			+ "&accion=run&rn=pagoDirectoReporte.jasper&cIdDocumento=" + $("#cIdFolio").val(),
			"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
	  }
	  
	  function cambiaMontoBruto(){
	  	var iva=quitaFmt($("#nPorcentajeIVA").val());	
		var mImporteNeto = quitaFmt($("#mImporteTotalNeto").val());
		iva=parseFloat(iva)+1;
		var mImporteBruto = mImporteNeto/(iva);
		$("#mImporteTotalBruto").val(parseFloat(mImporteBruto));
		$("#mImporteTotalBruto").formatCurrency();
	  }
	  
	  function cambiaMontoBrutoDetalle(){
	  	var iva=quitaFmt($("#nPorcentajeIVA").val());	
		var mImporteNeto = quitaFmt($("#mImporteFactNeto").val());
		iva=parseFloat(iva)+1;
		var mImporteBruto = mImporteNeto/(iva);
		$("#mImporteFactBruto").val(parseFloat(mImporteBruto));
		$("#mImporteFactBruto").formatCurrency();
	  }
	  
	  function cambiaMontoBrutoEdicion(nfolioPago, consecutivo ){
	  	var iva=quitaFmt($("#nPorcentajeIVA").val());	
		var mImporteNeto = quitaFmt($("#mImporteNeto_"+nfolioPago+"_"+consecutivo+"").val());
		iva=parseFloat(iva)+1;
		var mImporteBruto = mImporteNeto/(iva);
		$("#mImporteBruto_"+nfolioPago+"_"+consecutivo+"").val(parseFloat(mImporteBruto));
		$("#mImporteBruto_"+nfolioPago+"_"+consecutivo+"").formatCurrency();
	  }
	  
	  function cambiaMontoBrutoDetalleEdicion(nfolioPago, consecutivo){
	  	var iva=quitaFmt($("#nPorcentajeIVA").val());	
		var mImporteNeto = quitaFmt($("#mImporteNetoDetalle_"+nfolioPago+"_"+consecutivo+"").val());
		iva=parseFloat(iva)+1;
		var mImporteBruto = mImporteNeto/(iva);
		$("#mImporteBrutoDetalle_"+nfolioPago+"_"+consecutivo+"").val(parseFloat(mImporteBruto));
		$("#mImporteBrutoDetalle_"+nfolioPago+"_"+consecutivo+"").formatCurrency();
	  }
		
	</script>
</head> 
<body>
<form>
	<table width="100%" align="left" border='0'>
		<tr>
			<td style="width: 740px">
				<fieldset>
					<legend>Facturas Pago Directo</legend>
					<table align="left" border='0' cellpadding="2" width="90%"  > <!-- style="width:650px" -->
						<tr  align="left">
							<td align="right">
								<img id="imgReporte" src="../../imagenes/icono_PDF.jpg" style="cursor: pointer" onclick="generaReporte();" />&nbsp;Reporte
							</td>
						</tr>
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
								Importe IVA en facturas:<input type="text" id="lblImporteIvaFactura" name="lblImporteIvaFactura"  style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr> 
						<tr align="left">
							<td class="consumoDirecto">
								Importe Neto en facturas:<input type="text" id="lblImporteNetoFactura" name="lblImporteNetoFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
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
					</table>
				</fieldset>
			</td>
		</tr>
		<tr> 
			<td style="width: 740px"> 
				<fieldset> 
					<legend>Datos de las facturas</legend>
					<table width="90%" align="left" border='0'>
						<tr>
							<td align="left" colspan="1">Importe Total Neto del documento</td>
							<td align="left" colspan="3">
								<input type="text" id="lblBrutoFactura" name="lblBrutoFactura" style="border-width:0; background-color:transparent;width: 90px;" readonly/>
							</td>
						</tr>
						<tr>
							<td colspan="1" align="left">Fecha de la factura</td>
							<td colspan="3" align="left">
								<input maxlength="10"  type="text" class="habilitaCampos"  name="fechaFactura" id="fechaFactura"/><font color="red">*</font>
							</td>
						</tr>
						<tr>
							<td align="left">Serie</td>
							<td align="left">
								<input type="text" class="habilitaCampos" style="text-transform:uppercase; text-align: center;" onkeyup="$(this).val( $(this).val().toUpperCase() )"  name="cSerie" id="cSerie" value="" size="10" maxlength="10" />
							</td>
							<td align="left">Folio</td>
							<td align="left">
								<input type="text" class="habilitaCampos" style="text-align: right;" onkeypress="valFmt(this,9)"  name="cFactura" id="cFactura" value="" size="10" maxlength="10" /><font color="red">*</font>
							</td>
						</tr>
						<tr>
							<td align="left" colspan="1">Importe Total Importe Bruto mas Iva de la factura</td>
							<td align="left" colspan="3">
								<input type="text" class="habilitaCampos" style="text-align: left;" onblur="$(this).formatCurrency();" onkeypress="valFmt(this,9)" name="mImporteTotalNeto" id="mImporteTotalNeto" onchange="cambiaMontoBruto();" value="" size="10" maxlength="10" /><font color="red">*</font>
							</td>
						</tr>
						<tr>
							<td align="left" colspan="1">Importe Total Bruto de la factura</td>
							<td align="left" colspan="3">
								<input type="text" style="text-align: left;" onblur="$(this).formatCurrency();" disabled name="mImporteTotalBruto" id="mImporteTotalBruto" value="" size="10" maxlength="10" />
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<input type="button" class="habilitaCampos, btnInterfaceBG" style="text-align: left;" id="pbAgregar" value="Agregar"/>
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<table   class="display" width="500px" id="grdFacturas">
									<thead>
										<tr>
											<th></th>
											<th>Fecha</th>
											<th>Factura</th>
											<th>Serie</th>
											<th>Importe Total Bruto</th>
											<th>Importe Total Neto</th>
											<th>Eliminar</th>
											<th></th>
											<th></th>
											<th></th>
											<th></th>
										</tr>
									</thead>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="4" align="center">
							 	Total neto de las facturas:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="15" maxlength="15" readonly/>
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<input type="button" class="habilitaCampos, btnInterfaceBG" style="text-align: left;" id="btnGuardaFacturas" value="Guardar cambios" />
							</td>
						</tr>
					</table>	
				</fieldset>
				
				<fieldset id="detalleFacturaDiv" style="display:none"> 
					<legend>Detalle de facturas</legend>
					<table width="90%" align="left" border='0'>
						<tr>
							<td >Importe Neto de la factura</td>
							<td align="left" >
								<input type="text"style="text-align: left;border-width:0; background-color:transparent;width: 100px" readonly  name="mImporteFactura" id="mImporteFactura"  />
							</td>
						</tr>
						<tr>
							<td >Importe Bruto mas Iva</td>
							<td align="left" >
								<input type="text" class="habilitaCampos" style="text-align: left;" onblur="$(this).formatCurrency();" onchange="cambiaMontoBrutoDetalle();" onkeypress="valFmt(this,9)" name="mImporteFactNeto" id="mImporteFactNeto" value="" size="10" maxlength="10" /><font color="red">*</font>
							</td>
						</tr>
						<tr>
							<td >Importe Bruto</td>
							<td align="left" >
								<input type="text" style="text-align: left;" onblur="$(this).formatCurrency();" disabled name="mImporteFactBruto" id="mImporteFactBruto" value="" size="10" maxlength="10" />
							</td>
						</tr>						
						<tr> 
							<td >Partida</td>
							<td align="left" > 
								<select id="cIdTipoPartida" class="habilitaCampos" name="cIdTipoPartida" style="width: 40em;" onchange="buscaClave()"></select><font color="red">*</font>
							</td>
						</tr>
						<tr>
							<td >Clave Presupuestal</td>
							<td align="left">
								<select id="claveEp" class="habilitaCampos" name="claveEp" style="width: 40em;"></select><font color="red">*</font>
							</td>
						</tr>
						 <tr id="altaAlmacenariaTr">
							<td>Alta almacenaria</td>
							<td align="left">
								<input type="text" class="habilitaCampos" style="text-align: left;" onkeypress="valFmt(this,9)"  name="altaAlmacenaria" id="altaAlmacenaria" value="" size="10"/><font color="red">*</font>
							</td>
						</tr> 
						<tr id="idAlmacenTr" >
							<td>Almacen</td>
							<td align="left">
								<!--  <select id="ALM" name="ALM" class="habilitaCampos"></select> -->
								<input type="text" id="ALM" name="ALM" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
								<input type="hidden" id="cAlmacenTmp" name="cAlmacenTmp"/>
								
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input type="button" class="habilitaCampos" style="text-align: left;" id="btnAgregaDetalle" value="Agregar"/>
							</td>
						</tr>
					</table>
					<div id="tipoAlmacen" style="display:none">
						<table   class="display" width="740px" id="grdValidaFacturas">
							<thead>
								<tr>
									<th></th>
									<th>Alta almacenaria</th>
									<th>Almacén</th>
									<th>Importe Bruto</th>
									<th>Importe Neto</th>
									<th>Partida</th>
									<th>Clave Presupuestal</th>
									<th>Eliminar</th>
									<th></th>
									<th></th>    
								</tr>
							</thead>
						</table>
					</div>
					
					
					<div id="tipoConsumoDirecto"  style="display:none">
						<table   class="display" width="740px" id="grdFacturasConsumo">
							<thead>
								<tr>
									<th></th>
									<th>Importe Bruto</th>
									<th>Importe Neto</th>
									<th>Partida</th>
									<th>Clave Presupuestal</th>
									<th>Eliminar</th>
									<th></th>  
								</tr>
							</thead>
							
						</table>
						
					</div>
					Total neto del detalle:<input type="text" style="text-align: right;" name="mSubTotalFactura" id="mSubTotalFactura" value="0.00"  readonly/>&nbsp;
					Total acumulado neto del detalle:<input type="text" style="text-align: right;" name="mSubTotalAcumulado" id="mSubTotalAcumulado" value="0.00"  readonly/>
					
					<table>
						<tr>
							<td colspan="2" style="width: 740px">
								<input type="button" style="text-align: left;" class="habilitaCampos" id="btnGuardarDetalle" value="Guardar cambios"/>
							</td>
						</tr>
					</table>
					
				</fieldset>
			</td>
		</tr>
		
		<tr>

		</tr>
		
	</table>

	<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=cEjercicio %>"/>
	<input type="hidden" id="mes" name="mes" value="<%=mesActual %>"/>
	<input type="hidden" id="cIdFolio" name="cIdFolio"/>
	<input type="hidden" id="nFolioPagoDirecto" name="nFolioPagoDirecto"/>
	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
	<input type="hidden" id="cCentroContable" name="cCentroContable"/>
	 
	<input type="hidden" id="cIdTipoPartidaEdit" name="cIdTipoPartidaEdit"/>
	<input type="hidden" id="nPorcentajeIVA" name="nPorcentajeIVA"/>  
	
	<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/>  
	<input type="hidden" id="ID_TIPO_CONCEPTO" name="ID_TIPO_CONCEPTO"/>  
	
	<input type="hidden" id="cIdFactura" name="cIdFactura"/>  
	<input type="hidden" id="alta" name="alta"/>  
	<input type="hidden" id="cAnioTmp" name="cAnioTmp"/>  
	  
	<input type="hidden" id="almInsert" name="almInsert"/>
	<input type="hidden" id="consecutivoFactura" name="consecutivoFactura"/>
	<input type="hidden" id="folioPago" name="folioPago"/> 
	
	
	<!-- Auxiliares de edicion en encabezado -->
	<input type="hidden" id="fechaHidden" name="fechaHidden"/>
	<input type="hidden" id="folioHidden" name="folioHidden"/> 
	<input type="hidden" id="serieHidden" name="serieHidden"/> 
	<input type="hidden" id="importeFacturaHidden" name="importeFacturaHidden"/>
	<input type="hidden" id="importeFacturaHiddenNeto" name="importeFacturaHiddenNeto"/>
	 
	<input type="hidden" id="cIdFacturaHidden" name="cIdFacturaHidden"/> 
	
	<input type="hidden" id="existeDetalle" name="existeDetalle"/> 
	
	
	<!-- Auxiliares de edicion en detalle -->
	 
	<input type="hidden" id="altaHidden" name="altaHidden"/> 
	<input type="hidden" id="almacenHidden" name="almacenHidden"/> 
	<input type="hidden" id="facturaHidden" name="facturaHidden"/> 
	<input type="hidden" id="importeHidden" name="importeHidden"/>
	<input type="hidden" id="importeHiddenNeto" name="importeHiddenNeto"/> 
	
	<input type="hidden" id="partidaHidden" name="partidaHidden"/> 
	<input type="hidden" id="claveHidden" name="claveHidden"/> 
	<input type="hidden" id="nIdConsecutivo" name="nIdConsecutivo"/>
	<input type="hidden" id="folios" name="folios"/>
	<input type="hidden" id="sumaFoliosHidden" name="sumaFoliosHidden"/>
	
	
	<input type="hidden" id="cIdDocumentoExiste" name="cIdDocumentoExiste"/>
	<input type="hidden" id="idDocumentoPago" name="idDocumentoPago"/>  
	
	<input type="hidden" id="totalEp" name="totalEp"/>
	
	<input type="hidden" id="nPorcentajeIVA" name="nPorcentajeIVA"/>
	
	<input type="hidden" id="Imp_Bruto" name="Imp_Bruto"/>
	<input type="hidden" id="Imp_Iva" name="Imp_Iva"/>
	
	<input type="hidden" id="Imp_Neto" name="Imp_Neto"/>
	
	<input type="hidden" id="cFacturaAlta" name="cFacturaAlta"/>
	<input type="hidden" id="mImptBruto" name="mImptBruto"/>
	<input type="hidden" id="mImptBrutoTotal" name="mImptBrutoTotal"/>
	<input type="hidden" id="nIdEstado" name="nIdEstado"/>        
	
	
</form>    
</body>
</html>

