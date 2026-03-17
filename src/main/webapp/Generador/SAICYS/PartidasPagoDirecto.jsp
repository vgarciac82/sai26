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
		$(document).ready(function() {
			var oTableCucops;
			var oTableDetalle;
			 var umbral;
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
				Map botones=nb.getBotones(roles,"PagosDirectos","PartidasPagoDirecto");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
			var mes = 0;
			
			$("#tblCucopPAA td:nth-child(3)").live("click", function() {
				mes = 1;
			});
			$("#tblCucopPAA td:nth-child(4)").live("click", function() {
				mes = 2;
			});
			$("#tblCucopPAA td:nth-child(5)").live("click", function() {
				mes = 3;
			});
			$("#tblCucopPAA td:nth-child(6)").live("click", function() {
				mes = 4;
			});
			$("#tblCucopPAA td:nth-child(7)").live("click", function() {
				mes = 5;				
			});
			$("#tblCucopPAA td:nth-child(8)").live("click", function() {
				mes = 6;
			});
			$("#tblCucopPAA td:nth-child(9)").live("click", function() {
				mes = 7;
			});
			$("#tblCucopPAA td:nth-child(10)").live("click", function() {
				mes = 8;
			});
			$("#tblCucopPAA td:nth-child(11)").live("click", function() {
				mes = 9;
			});
			$("#tblCucopPAA td:nth-child(12)").live("click", function() {
				mes = 10;
			});
			$("#tblCucopPAA td:nth-child(13)").live("click", function() {
				mes = 11;
			});
			$("#tblCucopPAA td:nth-child(14)").live("click", function() {
				mes = 12;
			});
			$("#tblCucopPAA tr").live("click", function(){
				fnClearSelected( $("#tblCucopPAA").dataTable() );
				
				if(mes >= 1){
					$(this).addClass("row_selected");
					validaAgregaDetalle(mes);
				}
				mes = 0;
			});
			$("#btnGuardar").button().click(function(){
				validaActualizaDetalle();
			});
		});
		
		function init(){
			$("#cIdFolio").val("<%=cIdDocumento%>");
			$("#cCentroContable").val("<%=cCentroContable%>");
			$("#cIdUnidadEjecutora").val("<%=cIdUnidadEjecutora%>");
			
			queryFormPost("mPagoDirectoRead",{async:false});
			queryFormPost("mPagoDirectoMontos",{async:false});
			queryFormPost("maximoPagoDirectoRead",{async:false});
			querySelectPost("mCatalogoCapituloCMBRead","sltCapitulo",{async:false});
			$("#cIdCapitulo").val($("#sltCapitulo").val());
			//querySelectPost("mCatalogoSubPartidaCMBRead","sltSubpartida",{async:false});
			querySelectPost("mCatalogoPartidaPago","sltSubpartida",{async:false});
			
			queryFormPost("mPagoDirectoFacturas",{async:false});
			$("#lblImporteBruto").val($("#Imp_Bruto").val());
			$("#lblIVA").val($("#Imp_Iva").val());
			$("#lblImporteNeto").val($("#Imp_Neto").val());
			
			
			
			initTable_CUCOPS();
			initTable_DetallePDIR();
			validaHabilitaCampos();
			
			if($("#lblMotivoRechazo").val() != "" && $("#nIdEstado").val() == 5){
				$("#trMovitoRechazo").css("display","");
				$("#divMotivoRechazo").html($("#lblMotivoRechazo").val());
			}
			else
				$("#trMovitoRechazo").css("display","none");
			
			
			var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
			var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));

			Imp_Neto=Imp_Neto.toFixed(2);
			importeNetoFactura=importeNetoFactura.toFixed(2);
			
			umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
			
			 if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03){
				$("#lblImporteNetoFactura").css("color","black");
				$("#lblImporteNeto").css("color","black");
				$("#apartadoPagoDirecto").css("display", "block");
			}  else{
				$("#lblImporteNetoFactura").css("color","red");
				$("#lblImporteNeto").css("color","red");
				$("#apartadoPagoDirecto").css("display", "none");				
			} 
			
					
			
			$("#lblImporteNeto").formatCurrency();
			$("#lblImporteBruto").formatCurrency();
			$("#lblIVA").formatCurrency();
			
			$("#lblImporteFactura").formatCurrency();
			$("#lblImporteIvaFactura").formatCurrency();
			$("#lblImporteNetoFactura").formatCurrency();
		}
		
		function initTable_CUCOPS(){
			var qw = " cIdCABM like '%25FONDO%25' or cIdCABM like '%25PROCE%25' or cIdCABM like '%25PGDIR%25'";
			
			oTableCucops = $("#tblCucopPAA").dataTable({
				sScrollX: "440%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_Calendario('" + $("#cEjercicio").val() + "', '" +
					 $("#cIdUnidadEjecutora").val() + "', 'NULL' , '" + 
					 $("#sltSubpartida").val() + "')&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cCABM" },
					{ sName: "enero" },
					{ sName: "febrero" },
					{ sName: "marzo" },
					{ sName: "abril" },
					{ sName: "mayo" },
					{ sName: "junio" },
					{ sName: "julio" },
					{ sName: "agosto" },
					{ sName: "septiembre" },
					{ sName: "octubre" },
					{ sName: "noviembre" },
					{ sName: "diciembre" },
					{ sName: "CABM", bVisible : false }
				]
        	});
		}
		
		function initTable_DetallePDIR(){
			oTableDetalle = $("#tblDetalle").dataTable({
				sScrollX: "120%",
				//sScrollXInner: "440%",
				bScrollCollapse: true,
				bDestroy: true,
				iDisplayLength: 40,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_DetallePagoDirecto('"+$("#cEjercicio").val()+"','"+$("#cIdUnidadEjecutora").val()+"','"+$("#cIdFolio").val()+"')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cDescripcion" },
					{ sName: "cMes" },
					{ sName: "nCantidad" },
					{ sName: "mPrecioUnitario" },
					{ sName: "nPorcentajeIVA" },
					{ sName: "mImporteNeto" },
					{ sName: "cEliminar"},
					{ sName: "nIdMes", bVisible: false},
					{ sName: "cIdSubPartida", bVisible: false}
				]
        	});
		}
		
		function quitaFmt( val ) {
			if ( val.indexOf( "$" ) >= 0 )
		   		val = val.replace("$", "");
		   	while(val.indexOf( "," ) > 0)
		   		val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		function cambiaCapitulo(){
			$("#cIdCapitulo").val($("#sltCapitulo").val());
			//querySelectPost("mCatalogoSubPartidaCMBRead","sltSubpartida",{async:false});
			querySelectPost("mCatalogoPartidaPagoCapitulo","sltSubpartida",{async:false});
			
		}
		
		function cambiaSubpartida(){
			initTable_CUCOPS();
		}
		
		function agregarDetalle(td, mes){
			alert(mes);
		}
		
		function fnGetSelected( oTableLocal ){
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		
		function validaAgregaDetalle(mes){
			var anSelected = fnGetSelected( $("#tblCucopPAA").dataTable() );				
			var aData = $("#tblCucopPAA").dataTable().fnGetData(anSelected[0]);
			
			$("#cIdCABMIns").val(aData[14]);
			$("#nIdPeriodo1").val(mes);
			$("#nIdPeriodo2").val(mes);
			$("#cIdSubPartida").val($("#sltSubpartida").val());
			queryFormPost("obtenMontoYCantidadRead",{async:false});
			
			if (parseInt($("#nCantidadDisponibilidad").val(),10) > 0) {
				if (parseFloat($("#mPrecioUnitario").val()) <= parseFloat($("#mMontoDisponibilidad").val())) {
					queryFormPost("existeDetallePagoDirecto",{async:false});
					if($("#existeDetallePagoDirecto").val() == 0){						
						//if(validaTopeImporte($("#cIdSubPartida").val())){
							queryFormPost("detallePagoDirectoCreate",{async:false});
							 initTable_DetallePDIR();
							initTable_CUCOPS(); 
							queryFormPost("mPagoDirectoMontos",{async:false});
							$("#lblImporteBruto").val($("#Imp_Bruto").val());
							$("#lblIVA").val($("#Imp_Iva").val());
							$("#lblImporteNeto").val($("#Imp_Neto").val());
							$("#lblImporteBruto").formatCurrency();
							$("#lblIVA").formatCurrency();
							$("#lblImporteNeto").formatCurrency();
							
							var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
							var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));
				
							Imp_Neto=Imp_Neto.toFixed(2);
							importeNetoFactura=importeNetoFactura.toFixed(2);
							
							
							umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
							if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03){
						//	 if(Number(Imp_Neto)>Number(importeNetoFactura)){
								$("#lblImporteNetoFactura").css("color","black");
								$("#lblImporteNeto").css("color","black");
								$("#apartadoPagoDirecto").css("display", "block");
								return -1;
							}  else{
								alert("El monto neto de las facturas debe ser el mismo que al monto neto de las partidas");
								$("#apartadoPagoDirecto").css("display", "none");
								$("#lblImporteNetoFactura").css("color","red");
								$("#lblImporteNeto").css("color","red");
							}  
						//}
					}
					else
						alert("Ya existe un detalle con este mes, favor de editarlo.");
				}
				else
					alert("No se cuenta con presupuesto para este CUCOP, edite el programa anual.");
			}
			else
				alert("No hay disponiblidad para este CUCOP, edite el Programa Anual.");
		}
		
		function validaActualizaDetalle(){
			var aTrs = $("#tblDetalle").dataTable().fnGetNodes();
			var aData;
			var ivaTem = 0;
			var montoBruto=0;
			var ivaTem=0;
			var montoTotal=0;
			var cantidad=0;
			var montoUnitario=0;
			var where=" where nFolioPago="+$("#nFolioPagoDirecto").val()+" ";
			var arreglo= new Array();
			var arr = new Array();
			var totalPartida=0;
			var totalLinea=0;
			var partida=0;
			var cantidad=0;
			var precioUnitario=0;
			//Valida que todas las partidas tengan el mismo IVA.
			 for ( var i=0 ; i < aTrs.length ; i++ ){
				
				aData =  $("#tblDetalle").dataTable().fnGetData(aTrs[i]);				
				ivaTem = parseFloat($("#nPorcentajeIVA-"+aData[8]+"-"+aData[0]).val());
			
				if(i==0){
					partida=aData[9];
					cantidad=Number($("#nCantidad-"+aData[8]+"-"+aData[0]).val());
					precioUnitario=Number(quitaFmt( $("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
					totalLinea=precioUnitario*cantidad;
					totalLinea=totalLinea*((ivaTem/100)+1);
					totalPartida=totalPartida+totalLinea;
					if(i+1 == aTrs.length){
						arr = [partida,totalPartida];
						arreglo.push(arr);
					}
				}else{
					if(partida==aData[9]){
						cantidad=Number($("#nCantidad-"+aData[8]+"-"+aData[0]).val());
						precioUnitario=Number(quitaFmt( $("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
						totalLinea=precioUnitario*cantidad;
						totalLinea=totalLinea*((ivaTem/100)+1);
						totalPartida=totalPartida+totalLinea;
						if(i+1 == aTrs.length){
							arr = [partida,totalPartida];
							arreglo.push(arr);
						} 
					}else{
						arr = [partida,totalPartida];
						arreglo.push(arr);
						totalPartida=0;
						totalLinea=0;
						partida=aData[9];
						cantidad=Number($("#nCantidad-"+aData[8]+"-"+aData[0]).val());
						precioUnitario=Number(quitaFmt( $("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
						totalLinea=precioUnitario*cantidad;
						totalLinea=totalLinea*((ivaTem/100)+1);
						totalPartida=totalPartida+totalLinea;
						
						if(i+1 == aTrs.length){
							arr = [partida,totalPartida];
							arreglo.push(arr);
						} 
					}
				}
								
			}
			/*
			//////////////////////////////////////////////
			var szWhere = "";
			var szTabla = "MONTOVALIDAPAA";
			var encontrado=false;
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
          		var disponible = 0.0;
           		
				for (var i = 0; i < j.length; i++) {
					arr = [j[i].Col0,j[i].Col1];
					
					for(var x=0; x<arreglo.length;x++){
				
						if(arreglo[x][0]==arr[0]){
							posicion=x;
							encontrado=true;
						}
					}
					if(encontrado){
						t=Number(arr[1])+Number(arreglo[posicion][1]);
						arreglo[posicion][1]=t;
						encontrado=false;
					}
					else{
						arreglo.push(arr);
						encontrado=false;
					}
				} 	
				var arreglo1= new Array();
				var granTotalPartida=0;
				for(var i=0; i<arreglo.length;i++){	
					if(i==0){
						partida=arreglo[i][0];
						granTotalPartida=arreglo[i][1];
					}else{
						if(partida==arreglo[i][0]){
							granTotalPartida=granTotalPartida+arreglo[i][1];
						}else{
							arr=[partida,granTotalPartida];
							arreglo1.push(arr);
							granTotalPartida=0;
						
							partida=arreglo[i][0];
							granTotalPartida=granTotalPartida+arreglo[i][1];
							if(i+1 == arreglo.length){
								arr = [partida,granTotalPartida];
								arreglo1.push(arr);
							}
						}
					}
			  } */
			//});
			
			
			////////////////////////////////////////////////
			var partidas= new Array();
			var szWhere = "";
			var szTabla = "MONTOVALIDAPAA";
			var diferentes=false;
			var arregloFacturas= new Array();
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: where,ajax: 'false'}, function(j){
          		var disponible = 0.0;
           		
				for (var i = 0; i < j.length; i++) {
					arr = [j[i].Col0,j[i].Col1];
					arregloFacturas.push(arr);
				}
				
				
				for(var x=0;x<arregloFacturas.length;x++){
					for(var y=0;y<arreglo.length;y++){						
						if(arregloFacturas[x][0]==arreglo[y][0]){
							if(arregloFacturas[x][1]>arreglo[y][1]){
								continue;
							}else{
								var aux=Math.abs(arregloFacturas[x][1]-arreglo[y][1]);
								if(aux<.03){
									continue;
								}else{
									diferentes=true;
									partidas.push(arreglo[y][0]);
								}
								
							}
							
						}
					}
				}
				
				if(!diferentes){
					for ( var i=0 ; i < aTrs.length ; i++ ){
						aData =  $("#tblDetalle").dataTable().fnGetData(aTrs[i]);
						$("#cIdCABMIns").val(aData[0]);
						$("#nIdPeriodo1").val(aData[8]);
						$("#nIdPeriodo2").val(aData[8]);
						$("#cIdSubPartida").val(aData[9]);
						$("#nCantidad").val($("#nCantidad-"+aData[8]+"-"+aData[0]).val());
						$("#nPorcentajeIVA").val($("#nPorcentajeIVA-"+aData[8]+"-"+aData[0]).val());
						
						if(parseInt($("#nCantidad").val(),10) > 0){
							$("#mPrecioUnitario").val(quitaFmt($("#mPrecioUnitario-"+aData[8]+"-"+aData[0]).val()));
							if(parseFloat($("#mPrecioUnitario").val()) > 0){
								queryFormPost("updateDetallePagoDirecto", {async:false});
							}else{
								alert("No puede guardar precio unitario en cero");
							}
						}
						else{
							alert("No puede guardar en cantidad 0.");
						}
					}
					initTable_DetallePDIR();
					initTable_CUCOPS();
					
					queryFormPost("mPagoDirectoMontos",{async:false});
					$("#lblImporteBruto").val($("#Imp_Bruto").val());
					$("#lblIVA").val($("#Imp_Iva").val());
					$("#lblImporteNeto").val($("#Imp_Neto").val());					
					montoNeto=Number(quitaFmt($("#Imp_Neto").val()));
					var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
					
					montoNeto=Number(montoNeto.toFixed(2));
					importeNetoFactura=Number(importeNetoFactura.toFixed(2));
					
					umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
					 if(Number(montoNeto)==Number(importeNetoFactura) || umbral < .03){
					 	$("#lblImporteNetoFactura").css("color","black");
						$("#lblImporteNeto").css("color","black");
						$("#apartadoPagoDirecto").css("display", "block");	
					 }else{
					 	alert("El monto neto de las facturas debe ser el mismo que al monto neto de las partidas");
						$("#apartadoPagoDirecto").css("display", "none");
						$("#lblImporteNetoFactura").css("color","red");
						$("#lblImporteNeto").css("color","red");
					 }
					
					$("#lblImporteBruto").formatCurrency();
					$("#lblIVA").formatCurrency();
					$("#lblImporteNeto").formatCurrency();		
				}else{
					alert("Los montos de las partidas: "+partidas+" no corresponden a los montos capturados en la pestaña de facturas");
				}
			});		
			
			
		}
		
		function validaHabilitaCampos(){
			if($("#nIdEstado").val() == 1){
				var roles = "<%=roles%>";
				
				if(roles.indexOf("ADMIN_RECMAT") < 0 && $("#cIdUsuario").val() != $("#cIdUsuarioCreacion").val()){
					$("#sltCapitulo").attr("disabled","true");
					$("#sltSubpartida").attr("disabled","true");
					$("#tblCucopPAA").attr("disabled","true");
					$("#tblDetalle").attr("disabled","true");
					$("#btnGuardar").attr("disabled","true");
				}
			}
			else{
				$("#sltCapitulo").attr("disabled","true");
				$("#sltSubpartida").attr("disabled","true");
				$("#tblCucopPAA").attr("disabled","true");
				$("#tblDetalle").attr("disabled","true");
				$("#btnGuardar").attr("disabled","true");
			}
		}
		
		function Sinfrmt( fld )	{
			var valcol = fld.value ;
		   	var vcompr = $("#mImporteTmp").val();
		   	vcompr = quitaFmt( vcompr );
		   	valcol = quitaFmt( valcol );
			$("#" + fld.id).val( valcol );
		   	fld.select();
			$("#mImporteTmp").val( parseFloat( vcompr ) - parseFloat( valcol ) );
			$("#mImporteTmp").formatCurrency();
		}
		
		function cambiafrmt( fld )	{
		   	var vcompr = $("#mImporteTmp").val();
		   	var vfld = $("#" + fld.id).val();
		   	if (vfld == "")
		   		vfld = '0';
			vcompr = quitaFmt( vcompr );
			vfld=quitaFmt(vfld);
		   	$("#mImporteTmp").val( parseFloat(vcompr) + parseFloat( vfld ) );
			$("#" + fld.id).formatCurrency();
			$("#mImporteTmp").formatCurrency();
		}
		
		function eliminaDetalle(cabm, mes){
			$("#cIdCABMIns").val(cabm);
			$("#nIdPeriodo1").val(mes);
			
			if(confirm("\xBFEst\xE1s seguro de eliminar el detalle?.")){
				queryFormPost("detallePagoDirectoDelete",{async:false});
				initTable_CUCOPS();
				initTable_DetallePDIR();
				queryFormPost("mPagoDirectoMontos",{async:false});
				$("#lblImporteBruto").val($("#Imp_Bruto").val());
				$("#lblIVA").val($("#Imp_Iva").val());
				$("#lblImporteNeto").val($("#Imp_Neto").val());
				$("#lblImporteBruto").formatCurrency();
				$("#lblIVA").formatCurrency();
				$("#lblImporteNeto").formatCurrency();
				
				montoNeto=Number(quitaFmt($("#Imp_Neto").val()));
				var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
				
				montoNeto=Number(montoNeto.toFixed(2));
				importeNetoFactura=Number(importeNetoFactura.toFixed(2));
				if(montoNeto>importeNetoFactura){
					$("#apartadoPagoDirecto").css("display", "none");
					$("#lblImporteNetoFactura").css("color","red");
					$("#lblImporteNeto").css("color","red");
				}else{
					$("#lblImporteNetoFactura").css("color","black");
					$("#lblImporteNeto").css("color","black");
					if(montoNeto==importeNetoFactura){
						$("#apartadoPagoDirecto").css("display", "block");
					}else{
						$("#apartadoPagoDirecto").css("display", "none");
					}	
				}
				
			}
		}
		
		function validaTopeImporte(partida){
			queryFormPost("montoUnidadEjecutoraPartidaPagoDirecto",{async:false});
						
			//Valida Tope por Partida
			Sinfrmt(document.getElementById("mPrecioUnitario"));
			Sinfrmt(document.getElementById("montoUEPartida"));
			Sinfrmt(document.getElementById("mImporteMaximo"));
			
			if((parseFloat($("#mPrecioUnitario").val()) + parseFloat($("#montoUEPartida").val())) > parseFloat($("#mImporteMaximo").val())){
				cambiafrmt(document.getElementById("mImporteMaximo"));
				if(!confirm("El monto total de Pagos Directos para la partida "+partida+" supera el Monto M\xE1ximo ("+$("#mImporteMaximo").val()+"). \xBFDesea Continuar?.")){
					Sinfrmt(document.getElementById("mImporteMaximo"));
					return false;
				}
				else{
					Sinfrmt(document.getElementById("mImporteMaximo"));
					
					//Valida Tope por Proveedor
					Sinfrmt(document.getElementById("montoUEProveedor"));
					queryFormPost("montoUnidadEjecutoraProveedorPagoDirecto",{async:false});
					if((parseFloat($("#mPrecioUnitario").val()) + parseFloat($("#montoUEProveedor").val())) > parseFloat($("#mImporteMaximo").val())){
						cambiafrmt(document.getElementById("mImporteMaximo"));
						if(!confirm("El monto total de Pagos Directos para \xE9ste Proveedor supera el Monto M\xE1ximo ("+$("#mImporteMaximo").val()+"). \xBFDesea Continuar?.")){
							Sinfrmt(document.getElementById("mImporteMaximo"));
							return false;
						}
						Sinfrmt(document.getElementById("mImporteMaximo"));
					}
				}
			}
			
			return true;
		}
		
		function fnClearSelected( oTable ){
			var aTrs = oTable.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ )
				$(aTrs[i]).removeClass("row_selected");
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
			<td>
				<fieldset>
					<legend>Partidas</legend>
					<table align="left" style="width: 740px">
						<tr>
							<td style="width: 740px" align="left">
								<table>
									<tr>
										<td>
											Capitulo:
										</td>
										<td>
											<select id="sltCapitulo" onChange="cambiaCapitulo();" style="width:670px">
											</select>
										</td>
									</tr>
									<tr>
										<td>
											Partida:
										</td>
										<td>
											<select id="sltSubpartida" onChange="cambiaSubpartida();" style="width:670px">
											</select>
										</td>
									</tr>
									<tr>
						    			<td align="left" style="width:670px" colspan="2">
						    				<img id="imgRefresh" src="../../imagenes/icono_refresh.jpg" style="cursor: pointer" onclick="cambiaSubpartida();"/> Actualizar 
						    			</td>
						    		</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td style="width: 740px">
						    	<table id="tblCucopPAA" class="display">
									<thead >
										<tr>
											<th align="center">&nbsp;&nbsp;CUCOP&nbsp;&nbsp;</th>
											<th align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Descripci&oacute;n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
											<th align="center">ENERO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">FEBRERO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">MARZO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">ABRIL<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">MAYO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">JUNIO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">JULIO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">AGOSTO<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">SEPTIEMBRE<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">OCTUBRE<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">NOVIEMBRE<br />Mon. Disp. / Can. Disp.</th>
											<th align="center">DICIEMBRE<br />Mon. Disp. / Can. Disp.</th>
											<th></th>
										</tr>										
									</thead>
								</table>
						    </td>
						</tr>
						<tr>
							<td align="left" style="width: 240px">
								<input type="button" name="btnGuardar" id="btnGuardar" value="Guardar" class="btnInterfaceBG" />
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td style="width: 740px">
			    				<table id="tblDetalle" class='display' width="740px" >
									<thead>
								    	<tr>
									    	<th>CUCOP</th>
									    	<th style="width: 300px">Descripci&oacute;n</th>
									    	<th>Mes</th>
									    	<th>Cantidad</th>
									    	<th>Precio<br/>Unitario</th>
									    	<th>IVA</th>
									    	<th>Importe<br/>Neto</th>
									    	<th>&nbsp;</th>
									    	<th>&nbsp;</th>
									    	<th>&nbsp;</th>
									    </tr>
									</thead>
								</table>
			    			</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
	<input type="hidden" id="cCentroContable" name="cCentroContable" />
	<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=cEjercicio %>"/>
	<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" />
	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
	<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
	<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion"/>
	<input type="hidden" id="cIdFolio" name="cIdFolio" />
	<input type="hidden" id="cIDRFC" name="cIDRFC" />
	<input type="hidden" id="nIdEstado" name="nIdEstado" />
	<input type="hidden" id="cIdCapitulo" name="cIdCapitulo" />
	<input type="hidden" id="Imp_Bruto" name="Imp_Bruto" />
	<input type="hidden" id="Imp_Iva" name="Imp_Iva" />
	<input type="hidden" id="Imp_Neto" name="Imp_Neto" />
	<input type="hidden" id="nCantidadDisponibilidad" name="nCantidadDisponibilidad" />
	<input type="hidden" id="mMontoDisponibilidad" name="mMontoDisponibilidad" />
	<input type="hidden" id="mPrecioUnitario" name="mPrecioUnitario" />
	<input type="hidden" id="cIdCABMIns" name="cIdCABMIns" />
	<input type="hidden" id="nIdPeriodo1" name="nIdPeriodo1" />
	<input type="hidden" id="nIdPeriodo2" name="nIdPeriodo2" />
	<input type="hidden" id="cDescripcion" name="cDescripcion" />
	<input type="hidden" id="cIdSubPartida" name="cIdSubPartida" />
	<input type="hidden" id="mImporteTmp" name="mImporteTmp" />
	<input type="hidden" id="existeDetallePagoDirecto" name="existeDetallePagoDirecto" />
	<input type="hidden" id="montoUEPartida" name="montoUEPartida" />
	<input type="hidden" id="montoUEProveedor" name="montoUEProveedor" />
	<input type="hidden" id="nCantidad" name="nCantidad" />
	<input type="hidden" id="nPorcentajeIVA" name="nPorcentajeIVA" />
	<input type="hidden" id="nFolioPagoDirecto" name="nFolioPagoDirecto" />
</form>    
</body>
</html>
