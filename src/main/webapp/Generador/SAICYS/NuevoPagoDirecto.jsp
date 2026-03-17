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
    String cCentroContable = "";
    
    if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
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
	<meta http-equiv="description" content="This is my page">
	
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
		$(document).ready(function() {
			var oTableCucops;
			var oTablevFact;
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
				Map botones=nb.getBotones(roles,"PagosDirectos","NuevoPagoDirecto");
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
				var res = validaDatos(true);
				
				if(res){
					formateaValores();
					/////////////////////////generar caso////////////////////////////////////
					$.ajax({url: "../../servlet/PagoDirectoServlet?TIPO_DOCTO="+$("#TIPO_DOCTO").val()+"&operacion=0&cEjercicio="+$("#cEjercicio").val() , type: 'get' , async: false, dataType: 'json', success: 
						function(j){
							var folio =  "";
							var folioCaso = "";
							
							//Valida que haya generado el caso correctamente
							if(j[0].resp == "true"){
								folio =  j[0].folio;
								folioCaso = j[0].folioCaso.split("-");
								$("#idCaso").val(folio);
								$("#folioCasoApartado").val(j[0].folioCaso);
								$("#folio").val(folioCaso[2]);
								$("#id_caso").val($("#folio").val());
								$("#Imp_Bruto").val(quitaFmt($("#Imp_Bruto").val()));
								$("#por_IvaHidden").val(quitaFmt($("#por_Iva").val()));
								$("#Imp_Neto").val(quitaFmt($("#Imp_Neto").val()));
								$("#nIdEstado").val(1);
								$("#TIVAPAGD1").val()
												
								queryFormPost("pagoDirectoMaterialesCreate",{async:false});
								alert("Los datos se guardaron correctamente.");
								window.location = "PagoDirecto.jsp?tab=2&cEjercicio="+$("#cEjercicio").val()+"&cIdDocumento="+$("#cIdFolio").val()+
										"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdEstado=1";
							}
							else
								alert("Ocurri\xF3 un error al iniciar el proceso.");
						}
					});
					/* 
					$("#Imp_Bruto").val(quitaFmt($("#Imp_Bruto").val()));
					$("#por_Iva").val(quitaFmt($("#por_Iva").val()));
					$("#Imp_Neto").val(quitaFmt($("#Imp_Neto").val()));
					$("#nIdEstado").val(1);
					
					queryFormPost("pagoDirectoMaterialesCreate",{async:false}); */
					// guardan las facturas
					/* var aTrs = $('#grdValidaFacturas').dataTable().fnGetNodes();
					for ( var i=aTrs.length-1 ; i>=0; i--){ 	 	
	            		var nTr = $('#grdValidaFacturas').dataTable().fnGetData(i);	
	            		$("#cFacturaAux").val(nTr[0]+nTr[1]);
						$("#mImporteBruto").val(nTr[2]);
						queryFormPost("mInserttPagoFactura",{async:false});
	            	} */
	            	
				//	alert("Los datos se guardaron correctamente.");
					/* window.location = "PagoDirecto.jsp?tab=2&cEjercicio="+$("#cEjercicio").val()+"&cIdDocumento="+$("#cIdFolio").val()+
										"&cIdUnidadEjecutora="+$("#cIdUnidadEjecutora").val()+"&nIdEstado=1"; */
				}
				else
					return -1;
					
				agregaFecha();					
			});
			$( "#dialog-validaFact" ).dialog({
				autoOpen: false,
				height: 480,
				width: 4720,
				modal: true,
				buttons: {
						"Aceptar": function(){
							bClicBtn = true;
							oTablevFact = $('#grdValidaFacturas').dataTable();
							var aData = oTablevFact.fnGetData();
							for(var i=0; i<aData.length; i++) {
								var cSerie = aData[ i ][ 0 ];
								var cFactura = aData[ i ][ 0 ] + aData[ i ][ 1 ];
								var cImporte = aData[ i ][ 2 ];
								$("#cValidaFactura").val( cFactura );
								$("#cExiste").val( "0" );
								$("#cIdRFC").val($("#cIDRFC").val());
								queryFormPost("ValidaFactPagosRead", { async: false});
								if( Number( $("#cExiste").val() ) > 0){
									alert("La Factura " + cSerie + cFactura + " con Importe " + cImporte + " ya existe");
									bClicBtn = false;
									return;
								}
							}
							$("#Imp_Bruto").val( quitaFmt( $("#mTotalFacturaV").val() ) );
							$(".subtotall").change();
							$( this ).dialog( "close" );							
						},
						"Cancelar": function(){
							bClicBtn = true;
							$('#grdValidaFacturas').dataTable().fnClearTable();
							$("#mTotalFacturaV").val( "0.00" );
							$("#Imp_Bruto").val( "0.00" );
							$(".subtotall").change();
							$("#DESTINO_GASTO").val( "" );
							document.getElementById('montosFacturas').style.display = 'none';
							$( this ).dialog( "close" );
							
						}
					}//,
				/* beforeClose: function( event, ui ) {
					return bClicBtn;			
				}, */							
				/* close: function() {	
					parent.document.getElementById("pb_save").disabled=false;
				},
				open: function(){
					parent.document.getElementById("pb_save").disabled=true;
				} */
			});
			
		$('#grdValidaFacturas').dataTable({
			"iDisplayLength": 100,
			sScrollY: "150px",
			sScrollX: "150px",
			"bPaginate": false,
      			"bLengthChange": false,
      			"bFilter": false,
      			"bSort": false,
      			"bInfo": false,
      			"bAutoWidth": false,
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			aoColumns: [
				{ sName: "Serie" },
				{ sName: "Factura" },
				{ sName: "Importe" } ]

		});
		
		$("#pbAgregar").button().click(function() {
		oTablevFact = $('#grdValidaFacturas').dataTable();
			if ( $("#cFactura").val() == "" || Number( $("#cFactura").val() ) <= 0 ){
				alert("Falta Capturar el Número de Factura");
				return;
			}

			if ( $("#mImporteFact").val() == "" ||  Number( $("#mImporteFact").val() ) <= 0 ){
				alert("Falta Capturar el Importe de la Factura");
				return;
			}

			var aData = oTablevFact.fnGetData();
			for(var i=0; i<aData.length; i++) {
				var cFactura = aData[ i ][ 0 ] + aData[ i ][ 1 ];
				if( $("#cSerie").val() + $("#cFactura").val() == cFactura){
					alert("La Factura ya está capturada");
					$("#cSerie").val( "" );
					$("#cFactura").val( "" );
					$("#mImporteFact").val( "" );
					return;
				}
			}

			var mImporte = quitaFmt( $("#mImporteFact").val() );
			$("#mImporteFact").formatCurrency();
			$('#grdValidaFacturas').dataTable().fnAddData( [ $("#cSerie").val(), $("#cFactura").val(), $("#mImporteFact").val()] );
			mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) + Number( mImporte );
			$("#mTotalFacturaV").val( mImporte );
			$("#mTotalFacturaV").formatCurrency();
			$("#cSerie").val("");
			$("#cFactura").val("");
			$("#mImporteFact").val("");
		});			
		
		$(".subtotall").change(function(){
		
		
		 						
			$('.subtotall').each(function(){
				quitaFmtObj( this ) ;
		     });
    

			// calcular el monto neto
			var montoIva=Number($("#Imp_Bruto").val())*(1*$("#por_Iva").val());
			$("#Imp_Ejercer").val(montoIva+Number($("#Imp_Bruto").val()));
			$("#Imp_Iva").val(montoIva);
			
			var importetotal=Number($("#Imp_Bruto").val())+Number($("#Imp_Iva").val());
			$("#Imp_Neto").val(importetotal);
			
			$("#Imp_Neto").formatCurrency();
			$("#Imp_Bruto").formatCurrency();
			$("#Imp_Ejercer").formatCurrency();
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Retenciones").formatCurrency();

    
		});
		
		
		
		
		$("#grdValidaFacturas tbody").dblclick(function(event) {
			$(oTablevFact.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
				var aPos = oTablevFact.fnGetPosition( this.nTr );
				// Get the data array for this row
				var aData = oTablevFact.fnGetData( aPos[0] );
			});
			$(event.target.parentNode).addClass('row_selected');
   			var aPos = oTablevFact.fnGetPosition( event.target.parentNode );
   			if (aPos != null){
    			var aData = oTablevFact.fnGetData( aPos );
    			var mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) - Number( quitaFmt( aData[ 2 ] ) );
				$("#mTotalFacturaV").val( mImporte );
				$("#mTotalFacturaV").formatCurrency();
				oTablevFact.fnDeleteRow( aPos);
   			}
		});

		$("#grdValidaFacturas tbody").click(function(event) {
				$(oTablevFact.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
				var aPos = oTablevFact.fnGetPosition( this.nTr );
				// Get the data array for this row
				var aData = oTablevFact.fnGetData( aPos[0] );
			});
			$(event.target.parentNode).addClass('row_selected');
		});
	
	});
///// termina carga de documento//////
		function quitaFmtObj( elObjeto ) {
		  	var val =0;
			val = elObjeto.value;
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
		function init(){
			$("#cCentroContable").val( "<%=cCentroContable%>" );
			$("#fRecepcion").val( "<%=today%>" );
			
			querySelectPost("catalogoTipoPagoDirectoRead", "cIdTipoOperacion", {async: false });
			//querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {async: false });
			querySelectPost("CatalogoObraDGastoReadRELG", "DESTINO_GASTO", {async: true });
			cambiaTipoDestino();
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			$("#TIPO_CONCEPTO").val($("#tConcepto").val());
			querySelectPost("catalogoTipoMovimientoRead","tmovimiento", {async: false });
			queryFormPost("EjercicioFiscalActvRead",{async:false});
			//querySelectPost("CAT_TIPO_IVAporCienRead", "por_Iva", {async: false });
			queryFormPost("maximoPagoDirectoRead",{async:false});
			queryFormPost("consecutivoPagoDirectoRead",{async:false});
			$("#cIdFolio").val("PDIR-"+$("#cIdUnidadEjecutora").val()+"-"+(parseInt($("#consecutivoPagoDirecto").val(),10)+1));
			
			querySelectPost("CAT_TIPO_IVAporCienRead", "por_Iva", {async: false });
			$("#por_Iva").val("0.1600");
			if($("#cEjercicio").val() != ""){
				$("#cAnioFactEP").append("<option value='"+$("#cEjercicio").val()+"'>"+$("#cEjercicio").val()+"</option>");
				$("#cAnioFactEP").append("<option value='"+($("#cEjercicio").val()-1)+"'>"+($("#cEjercicio").val()-1)+"</option>");
			}
			$("#altaAlmacenariaTitulo").css("display","none");
			$("#altaAlmacenariaCampos").css("display","none");
		
		  //revisa que tipo de iva tiene asignado al inicio de la carga de la pagina	
		  var tipoIva=$("#por_Iva option:selected").text();
		 $("#DESTIVAPAGD").val(tipoIva)
		//BUSACA EL TIPO DE IVA
		 queryFormPost("tipoIvaPagoDirectoMateriales", { async: false});
		 $("#TIVAPAGD1").val($("#TIVAPAGD").val())
			
			
		}
		
		function cambiaTipoDestino(){
			$("input[id='ID_DESTINO_GASTO']").val($('#DESTINO_GASTO option:selected').val());
			querySelectPost("CatalogoObraTConceptoRead","tConcepto", {async: false });
			cambiaConcepto();
			document.getElementById('montosFacturas').style.display = 'block';			
			if($("#DESTINO_GASTO").val() == "CD"){				
					$("#altaAlmacenariaTitulo").css("display","none");
					$("#altaAlmacenariaCampos").css("display","none");
					$("#altaAlmacen").val("");
					$("#nFacturaEP").val("");
				//	document.getElementById('montosFacturas').style.display = 'block';
			//		$( "#dialog-validaFact" ).dialog( "open" );	
			}
			 else if($("#DESTINO_GASTO").val() == "AL"){
				$("#altaAlmacenariaTitulo").css("display","");
				$("#altaAlmacenariaCampos").css("display","");
				document.getElementById('montosFacturas').style.display = 'block';
			}
		}
		
		function cambiaConcepto(){
			$("#TIPO_CONCEPTO").val($("#tConcepto").val());
			querySelectPost("catalogoTipoMovimientoRead","tmovimiento", {async: false });
		}
		
		/*function cambiaMontoNeto(){
			var mImporteBruto = quitaFmt($("#Imp_Neto").val()) / (1 + Number( $("#por_Iva").val() ) );
			$("#Imp_Bruto").val(mImporteBruto);
			$("#Imp_Bruto").formatCurrency();
			$("#Imp_Iva").val(quitaFmt($("#Imp_Neto").val()) - quitaFmt($("#Imp_Bruto").val()));
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Neto").formatCurrency();
		}*/
		
		/*function cambiaMontoBrutoIva(){
			var mImporteNeto = quitaFmt($("#Imp_Bruto").val()) * (1 + Number( $("#por_Iva").val() ) );
			$("#Imp_Neto").val(parseFloat(mImporteNeto));
			$("#Imp_Neto").formatCurrency();
			$("#Imp_Iva").val(quitaFmt($("#Imp_Neto").val()) - quitaFmt($("#Imp_Bruto").val()));
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Bruto").formatCurrency();
		}*/
		
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
			if($("#cIdFolio").val() == ""){
				alert("El folio no puede estar vac\xEDo, favor de introducirlo.");
				return false;
			}
			
			if(validaExisteDocumento){
				queryFormPost("existePagoDirecto",{async:false});
				
				if($("#existePagoDirecto").val() >= 1){
					alert("Ya existe un Pago Directo con ese folio, favor de cambiarlo.");
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
			
			if(quitaFmt($("#Imp_Bruto").val()) == 0){
				alert("Debe introducir el importe bruto.");
				return false;
			}
			
			/* if(quitaFmt($("#Imp_Neto").val()) == 0){
				alert("Debe introducir el importe bruto.");
				return false;
			} */
			
			
			return true;	
		}
		
		function formateaValores(){
			if($("#DESTINO_GASTO").val() == "AL"){
				$("#cAlmacenTmp").val($("#ALM").val());
				$("#cAltaAlmacenariaTmp").val($("#altaAlmacen").val());
				$("#cAnioTmp").val($("#cAnioFactEP").val());
				$("#cFacturaTmp").val($("#nFacturaEP").val());
			}
			else if($("#DESTINO_GASTO").val() == "CD"){
				$("#cAlmacenTmp").val("0");
				$("#cAltaAlmacenariaTmp").val("0");
				$("#cAnioTmp").val($("#cEjercicio").val());
				$("#cFacturaTmp").val("");
			}
				
			//$("#cImporteBrutoTmp").val(quitaFmt($("#Imp_Bruto").val()));
			//$("#cIvaTmp").val(parseInt($("#por_Iva").val()*100));
			//$("#cImporteNetoTmp").val(quitaFmt($("#Imp_Neto").val()));
			$("#fRecepcionTmp").val($("#fRecepcion").val());
			$("#cIDRFCTmp").val($("#cIDRFC").val());
		}
		
		function Sinfrmt( fld )	{
			   var valcol = fld.value ;
			   valcol = valcol.replace("$", "");
			   valcol = valcol.replace(",", "");
			   $("#" + fld.id).val( valcol );
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
		function cambiaIva()
		{
		 var tipoIva=$("#por_Iva option:selected").text();
		 $("#DESTIVAPAGD").val(tipoIva)
		//BUSACA EL TIPO DE IVA
		 queryFormPost("tipoIvaPagoDirectoMateriales", { async: false});
		 $("#TIVAPAGD1").val($("#TIVAPAGD").val())
		  //quita formato 
	    	$("#Imp_Neto").val(quitaFmt($("#Imp_Neto").val()))
	  		$("#Imp_Bruto").val(quitaFmt($("#Imp_Bruto").val()))
	   		$("#Imp_Ejercer").val(quitaFmt($("#Imp_Ejercer").val()))
	    	$("#Imp_Iva").val(quitaFmt($("#Imp_Iva").val()))
	     	$("#Imp_Retenciones").val(quitaFmt($("#Imp_Retenciones").val()))
	  
		 // calcular el monto neto
			var montoIva=Number($("#Imp_Bruto").val())*(1*$("#por_Iva").val());
			$("#Imp_Ejercer").val(montoIva+Number($("#Imp_Bruto").val()));
			$("#Imp_Iva").val(montoIva);
			
			var importetotal=Number($("#Imp_Bruto").val())+Number($("#Imp_Iva").val());
			$("#Imp_Neto").val(importetotal);
			
			$("#Imp_Neto").formatCurrency();
			$("#Imp_Bruto").formatCurrency();
			$("#Imp_Ejercer").formatCurrency();
			$("#Imp_Iva").formatCurrency();
			$("#Imp_Retenciones").formatCurrency();
		 
		}
		
		
		
		
		
	</script>
</head> 
<body >
<form>
	<table width="100%" align="left">
		<tr>
			<td>
				<fieldset>
					<legend>Datos Generales</legend>
					<table align="left" cellpadding="2" width="100%">
						<!--<tr align="left">
							<td>
								&nbsp;
							</td>
						</tr>-->
						<tr align="left">
							<td>
								No. Folio:&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="cIdFolio" id="cIdFolio" />
							</td>
						</tr>
						<!-- <tr>
							<td>
								Folio:&nbsp;&nbsp;&nbsp;&nbsp;<input readonly type="text" id="id_caso" name="id_caso">
							</td>
						</tr> -->
						<tr align="left">
							<td>
								Tipo de Pago Directo: &nbsp;&nbsp;&nbsp;&nbsp;<select id="cIdTipoOperacion" name="cIdTipoOperacion" style="width: 20em;">
													  </select>
							</td>
						</tr>
						<tr align="left">
							<td>
								R.F.C.: &nbsp;&nbsp;&nbsp;&nbsp;<input disabled type="text" maxlength="15" size="15" name="cIDRFC" id="cIDRFC" class="AyudaSyC obligatorio" /> 
								&nbsp;&nbsp;<input disabled readonly type="text" maxlength="100"
									size="70" name="cnombre" ID="cnombre" />
							</td>
						</tr>
						<tr align="left">
							<td>
								<table>
									<tr>
										<td style="width:110px;">
											Fecha Recepci&oacute;n:
										</td>
										<td>
											<input name="fRecepcion" type="text" id="fRecepcion" size="10" disabled>
										</td>
										<td style="width:85px;">
											Tipo Destino:
										</td>
										<td style="width:170px;">
											<select id="DESTINO_GASTO" name="DESTINO_GASTO" onChange="cambiaTipoDestino();"></select>
										</td>
										<td style="width:100px;">
											Fuentes de Financiamiento:
										</td>
										<td>
											<select id="TFONDO" name="TFONDO">
												<option value="FF">Fondos Fiscales
												</option>
											</select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr align="left">
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td align="left">
											Concepto:
										</td>
									</tr>
									<tr>
										<td align="left">
											<textarea name="Concepto" rows="4" style="width:730px;" ID="Concepto" onkeypress="valFmt(this,15)"></textarea>
										</td>
									</tr>	
								</table>
							</td>
						</tr>
						<!--<tr align="left">
							<td>
								&nbsp;
							</td>
						</tr>-->
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td align="left">
				<fieldset>
					<legend>Movimiento</legend>
					<table>
						<!--<tr>
							<td>
								&nbsp;
							</td>
						</tr>-->
						<tr align="left">
							<td>Tipo de Concepto</td>
							<td colspan="2">Tipo De Movimiento</td>
							<td>&nbsp;</td>
						</tr>
						<tr align="left">
							<td>
								<select id="tConcepto" name="tConcepto" style="width: 10em;" onChange="cambiaConcepto()">
								</select>
							</td>
							<td colspan="2">
								<select id="tmovimiento" name="tmovimiento" style="width: 20em;">
								</select>
							</td>
							<td>&nbsp;</td>
						</tr>
						<tr align="left" id="altaAlmacenariaTitulo">
							<td>Almacén</td>
						<!-- 	<td>Alta almacenaria</td> -->
							<td>Año</td>
							<!-- <td>Factura</td> -->
						</tr>
						<tr align="left" id="altaAlmacenariaCampos">
							<td style="width:250px;">
								<select id="ALM" name="ALM"></select>
							</td> 
							<!-- <td>
								<input type="text" id="altaAlmacen" name="altaAlmacen"  style="text-align: right;" onKeyPress="valFmt(this,9)" value="0" size="12" maxlength="12">
							</td> -->
							<td style="width:80px;"><select id="cAnioFactEP" name="cAnioFactEP"></select></td>
							<!-- <td><input type="text" id="nFacturaEP" name="nFacturaEP" style="text-transform:uppercase" size="12" maxlength="12"></td> -->
						</tr>
						<!--<tr>
							<td>
								&nbsp;
							</td>
						</tr>-->
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
								<input style="text-align: right;" name="por_IvaOld" type="hidden" class="subtotall" id="por_IvaOld"  value="0.16" size="15"
									maxlength="15" />
								<select  id="por_Iva" name="por_Iva" style="width: 10em;"  onChange="cambiaIva()" ></select>
							</td>
						</tr>
						 <tr>
							<td colspan="2" align="right" nowrap> Importe Retenciones: </td>
							<td nowrap>
								<input style="text-align: right;" name="Imp_Retenciones" type="text" disabled class="subtotall" id="Imp_Retenciones" onkeypress="valFmt(this,9)" value="0" size="15"
									maxlength="15" />
							</td>
							<td nowrap> &nbsp;&nbsp; </td>
							<td nowrap>&nbsp;  </td>
						</tr>
					<!--	<tr> 
							<td align="right">&nbsp; </td>
							<td align="right" nowrap> Monto penalizaciones: </td>
							<td>
								<input style="text-align: right;" onKeyPress="valFmt(this,9)" onfocus="Sinfrmt(this)" name="mImportePenalizacion" type="text" class="subtotall" id="mImportePenalizacion" value="0" size="15"
									maxlength="15">
							</td>
							<td>&nbsp; </td>
							<td>&nbsp; </td>
						</tr> -->
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
		<tr>
			<td>  &nbsp; </td>
		</tr>
		<tr>
			<td align="left">
				<input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" class="btnInterfaceBG"/>
			</td>
		</tr>
	</table>
	<input type="hidden" id="TO_TIPO_DOCTO"	name="TO_TIPO_DOCTO" value="DIRECTO"/>
	<input type="hidden" id="cDocumento" name="cDocumento" value="PAGODIRECTO"/>
	<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/>
	<input type="hidden" id="cCentroContable" name="cCentroContable" />
	<input type="hidden" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" />
	<input type="hidden" id="cEjercicio" name="cEjercicio" />
	<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" />
	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=usuario.getU_UR() %>" />
	<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
	<input type="hidden" id="existePagoDirecto" name="existePagoDirecto" />
	<input type="hidden" id="cAlmacenTmp" name="cAlmacenTmp" />
	<input type="hidden" id="cAltaAlmacenariaTmp" name="cAltaAlmacenariaTmp" />
	<input type="hidden" id="cAnioTmp" name="cAnioTmp" />
	<input type="hidden" id="cFacturaTmp" name="cFacturaTmp" />
	<!--<input type="hidden" id="cImporteBrutoTmp" name="cImporteBrutoTmp" />-->
	<!--<input type="hidden" id="cIvaTmp" name="cIvaTmp" />-->
	<!--<input type="hidden" id="cImporteNetoTmp" name="cImporteNetoTmp" />-->
	<input type="hidden" id="consecutivoPagoDirecto" name="consecutivoPagoDirecto" />
	<input type="hidden" id="fRecepcionTmp" name="fRecepcionTmp" />
	<input type="hidden" id="cIDRFCTmp" name="cIDRFCTmp" />
	
	<input type="hidden" id="cValidaFactura" name="cValidaFactura" />
	<input type="hidden" id="cIdRFC" name="cIdRFC" />
	<input type="hidden" id="idCaso" name="idCaso" />
	<input type="hidden" id="folio" name="folio" />
	<input type="hidden" id="folioCasoApartado" name="folioCasoApartado" />
	<input type="hidden" id="TIPO_DOCTO"	name="TIPO_DOCTO" value="PAGODIRECTO"/>
	
	<input type="hidden" id="TIVAPAGD" name="TIVAPAGD" />
	<input type="hidden" id="DESTIVAPAGD" name="DESTIVAPAGD" />
	<input type="hidden" id="TIVAPAGD1" name="TIVAPAGD1" />
	<input type="hidden" id="por_IvaHidden" name="por_IvaHidden" />
	

	
	
	

	
	<input type="hidden" id="ID_TIPO_OPER" name="ID_TIPO_OPER" />
	
	<input type="hidden" id="cFacturaAux" name="cFacturaAux" />
	<input type="hidden" id="mImporteBruto"	name="mImporteBruto" />
	<input type="hidden" id="nIdEstado"	name="nIdEstado" />
		 
	<div id="dialog-validaFact" title="Validación de Facturas">
		<fieldset>
			<table>
				<tr>
					<td>Fecha</td>
					<td>Serie</td>
					<td>Factura</td>
					<td>Alta almacenaria</td>
					<td>Importe Bruto</td>
					<td>Razon Social</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td>
						<input type="text" name="fechaFactura" id="fechaFactura"/>
					</td>
					<td>
						<input type="text" style="text-transform:uppercase; text-align: center;" onkeyup="$(this).val( $(this).val().toUpperCase() )"  name="cSerie" id="cSerie" value="" size="4" maxlength="4" />
					</td>
					<td>
						<input type="text" style="text-align: right;" onkeypress="valFmt(this,9)"  name="cFactura" id="cFactura" value="" size="6" maxlength="6" />
					</td>
					<td>
						<input type="text" style="text-align: right;" onkeypress="valFmt(this,9)"  name="altaAlmacenaria" id="altaAlmacenaria" value="" size="6" maxlength="6" />
					</td>
					<td>
						<input type="text" style="text-align: right;" onblur="$(this).formatCurrency();" onkeypress="valFmt(this,9)" name="mImporteFact" id="mImporteFact" value="" size="15" maxlength="15" />
					</td>
					<td>
						<input type="text" style="text-transform:uppercase; text-align: center;" onkeyup="$(this).val( $(this).val().toUpperCase() )"  name="razonSocial" id="razonSocial" value="" size="4" maxlength="4" />
					</td>
					<td>
						<input type="button" style="text-align: center;" id="pbAgregar" class="btnInterfaceBG" value="Agregar"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<table   class="display" id="grdValidaFacturas">
				<thead>
					<tr>
						<th width="30px">Serie</th>
						<th width="40px">Factura</th>
						<th width="120px">Importe Bruto</th>
					</tr>
				</thead>
			</table>
			Total de las facturas:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="15" maxlength="15" readonly/>
		</fieldset>
	</div>
</form>   
</body>
</html>
