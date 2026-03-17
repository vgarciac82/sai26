<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@page import="com.syc.gestion.core.NegativaPestana"%>
<%	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	Map rol = usuario.getRoles();
	String roles="";
	
	if (usuario == null){
		response.sendRedirect("../index.jsp");
		return;
	}
	else{
		Iterator ite = rol.entrySet().iterator();
		
		while(ite.hasNext()){
			Map.Entry r = (Map.Entry)ite.next();
			roles += r.getKey().toString()+",";
		}
		if(roles.length()>0)
			roles = roles.substring(0,roles.length()-1);
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Cancela Precompromiso/Apartado</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="Procedimiento">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle">
				@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
				@import "../css/demo_table_jui.css";
				@import "../css/demo_page.css";
		</style>
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTablesSAICYSPA.js"></script>
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
			var tblRequisiciones;
			var tblLineas;
			var tblPrecompromisos;
			var tblEP;
				
			$(document).ready(function() {
				var roles = "<%=roles%>";
				
				if(roles.toString().indexOf("ADMIN_RECMAT") >= 0)				
					querySelectPost("UnidadBusca", "cIdUnidadEjecutora", {async: false});
				else
					queryFormPost("tCatalogoUnidadEjecutoraTxtRead", { async:false });
				$("#cIdUnidadEjecutora").val($("#cUnidadEjecutoraUsuario").val());
				$("#dTblLineas").css("display","none");
				$("#dTblPrecompromisos").css("display","none");
				$("#dTblEP").css("display","none");
				$("#mensajeProcesando").css("display","none");
				$("#mensajeProcesando2").css("display","none");
				queryFormPost("readEjercicioFiscalActivo", { async:false });
				
				//Inicializa tablas
				loadTableRequisiones();
				
				$("#btnBuscar").button().click(function(){
					if(validaDocumento() == 1){
						if($("#rbtApartado").attr("checked") == "checked"){
							consultaRequisicion($("#documentoCancelar").val()+","+$("#folioDocumentoCancelar").val()+",CONSULTA,APARTADO,"+getUR()+","+getTipoCancelacion()+",,"+$("#cEjercicio").val());
							$("#tblLineas").dataTable().fnClearTable();
						}
						else if( $("#rbtPrecompromiso").attr("checked") == "checked"){
							consultaPrecompromisos($("#documentoCancelar").val()+","+$("#folioDocumentoCancelar").val()+",CONSULTA,PRECOMPROMISO,"+getUR()+",,,"+$("#cEjercicio").val());
							$("#tblEP").dataTable().fnClearTable();
						}
					}
				});
				
				$("#btnCancelar").button().click(function(){
					var aData = null;
					
					if(validaDocumento() == 1){
						if($("#rbtApartado").attr("checked") == "checked"){
							aData = getRequisicionSeleccionada();
							
							//Valida si esta seleccionada alguna requisicion
							if(aData == "" || aData == null || aData.lengt == 0)
								alert("Debe seleccionar una Requisici\xF3n de la tabla.");
							else{
								if(confirm("\xBFEst\xE1s seguro de cancelar el documento?"))
									cancelar(aData[1]+","+(aData[0].split("-")[2])+",CANCELAR,APARTADO,"+getUR()+","+getTipoCancelacion()+",ALL,"+$("#cEjercicio").val(),"mensajeProcesando","APARTADO");
							}
						}
						else if( $("#rbtPrecompromiso").attr("checked") == "checked"){
							aData = getPrecompromisoSeleccionado();
							
							if(aData == "" || aData == null || aData.lengt == 0)
								alert("Debe seleccionar el documento que desea cancelar.");
							else{
								if(confirm("\xBFEst\xE1s seguro de cancelar el documento?"))
									cancelar(aData[1]+","+(aData[0].split("-")[2])+",CANCELAR,PRECOMPROMISO,"+getUR()+","+getTipoCancelacion()+",,"+$("#cEjercicio").val(),"mensajeProcesando","PRECOMPROMISO");
							}
						}
					}
				});
				
				$("#btnLiberarTodo").button().click(function(){					
					if( $("#rbtPrecompromiso").attr("checked") == "checked"){
						if(confirm("\xBFEst\xE1s seguro de cancelar TODOS los PRECOMPROMISOS?")){
							var cc = <%=usuario.getPropiedad("cCentroContable")%>;
							getNextSequenceVal({seqName: "CO-" + cc, async: false, callback: setSequenceVal});
							cancelar(",,LIBERAR,PRECOMPROMISO,*,,,"+$("#cEjercicio").val()+","+$("#caNoPreCompromiso").val(),"mensajeProcesando","");
						}
					}
					else if($("#rbtApartado").attr("checked") == "checked"){
						if(confirm("\xBFEst\xE1s seguro de cancelar TODOS los APARTADOS?")){
							cancelar(",,LIBERAR,APARTADO,*,,,"+$("#cEjercicio").val(),"mensajeProcesando","");
						}
					}
				});
				
				$("#tblRequisiciones tr").live("dblclick", function() {
					fnClearSelectedTable(tblRequisiciones);
					$(this).addClass("row_selected");
					
					if($("#rbtApartado").attr("checked") == "checked"){
						if($("#rbtParciales").attr("checked") == "checked"){
							consultaLineas(getRequisicionSeleccionada()[1]+","+$("#folioDocumentoCancelar").val()+",CONSULTA,LINEAS,"+getUR()+","+getTipoCancelacion()+",,"+$("#cEjercicio").val());
						}
					}
				});
				
				$("#tblPrecompromisos tr").live("dblclick", function() {
					fnClearSelectedTable(tblPrecompromisos);
					$(this).addClass("row_selected");
					
					if($("#rbtPrecompromiso").attr("checked") == "checked"){
						consultaEP_Precom(getPrecompromisoSeleccionado()[1]+","+$("#folioDocumentoCancelar").val()+",CONSULTA,EP,"+getUR()+",,,"+$("#cEjercicio").val()+","+getPrecompromisoSeleccionado()[0]);
					}
				});
				
				$("#rbtCompletos").live("click", function() {
					$("#tblLineas").dataTable().fnClearTable();
					$("#tblRequisiciones").dataTable().fnClearTable();
					$("#dTblLineas").css("display","none");
					$("#btnCancelar").removeAttr("disabled");
					$("#btnLiberarTodo").removeAttr("disabled");
				});
				
				$("#rbtParciales").live("click", function() {
					$("#dTblLineas").css("display","");
					$("#btnCancelar").attr("disabled",true);
					$("#btnLiberarTodo").attr("disabled",true);
					$("#tblLineas").dataTable().fnClearTable();
					$("#tblRequisiciones").dataTable().fnClearTable();
					loadTableLineas();
				});
				
				$("#rbtPrecompromiso").live("click", function() {
					$("#tblLineas").dataTable().fnClearTable();
					$("#tblRequisiciones").dataTable().fnClearTable();
					$("#trTipoCancelacion").css("display","none");
					$("#dTblLineas").css("display","none");
					$("#dTblRequisiciones").css("display","none");
					$("#dTblPrecompromisos").css("display","");
					$("#dTblEP").css("display","");
					$("#btnCancelar").removeAttr("disabled");
					$("#btnLiberarTodo").removeAttr("disabled");
					loadTablePrecompromisos();
					loadTableEP_Precom();
				});
				
				$("#rbtApartado").live("click", function() {
					$("#tblLineas").dataTable().fnClearTable();
					$("#tblRequisiciones").dataTable().fnClearTable();
					$("#trTipoCancelacion").css("display","");
					$("#dTblRequisiciones").css("display","");
					$("#dTblPrecompromisos").css("display","none");
					$("#dTblEP").css("display","none");
					
					if($("#rbtParciales").attr("checked") == "checked"){
						$("#dTblLineas").css("display","");
						loadTableLineas();
						$("#btnCancelar").attr("disabled",true);
						$("#btnLiberarTodo").attr("disabled",true);
					}
					else
						$("#dTblLineas").css("display","none");
				});
				
				$("#btnCancelarTodo").button().click(function(){
					var aData = getRequisicionSeleccionada();
					if(getRequisicionSeleccionada() == "")
						alert("Debe seleccionar una requisici\xF3n.");
					else
						if(confirm("\xBFEst\xE1s seguro de cancelar el apartado de todas las l\xEDneas de la requisici\xF3n "+getRequisicionSeleccionada()[1]+"?"))
							cancelar(aData[1]+","+(aData[0].split("-")[2])+",CANCELAR,APARTADO,"+getUR()+","+getTipoCancelacion()+","+getLineasAll()+","+$("#cEjercicio").val(),"mensajeProcesando2","LINEAS");
				});
				
				$("#btnCancelarSeleccionadas").button().click(function(){
					var aData = getRequisicionSeleccionada();
					if(getLineasSeleccionadas().length > 0){
						if(confirm("\xBFEst\xE1s seguro de cancelar el apartado de las l\xEDneas seleccionadas?"))
							cancelar(aData[1]+","+(aData[0].split("-")[2])+",CANCELAR,LINEAS,"+getUR()+","+getTipoCancelacion()+","+getLineasSeleccionadas()+","+$("#cEjercicio").val(),"mensajeProcesando2","LINEAS");
					}
					else{
						alert("No ha seleccionado ninguna l\xEDnea de la requisici\xF3n.");
						return;
					}
				});
			});
			
			function validaDocumento(){
				if($("#rbtApartado").attr("checked") != "checked" && 
				$("#rbtPrecompromiso").attr("checked") != "checked"){
					alert("Debe seleccionar el tipo de documento que requiere cancelar.");
					return 0;
				}
				return 1;
			}
			
			/*
			* accion = CONSULTA O CANCELACION
			* tipoDocumento = APARTADO O PRECOMPROMISO
			*/
			function consultaRequisicion(params){
				$("#tblRequisiciones").dataTable().fnClearTable();
				$("#btnBuscar").attr("disabled",true);
				$("#mensajeProcesando").css("display","");
				$("#mensajeProcesando").html("Buscando espere...");
				
				if($("#rbtParciales").attr("checked") == "checked"){
					$("#btnCancelarSeleccionadas").attr("disabled",true);
					$("#btnCancelarTodo").attr("disabled",true);
				}
				else if($("#rbtCompletos").attr("checked") == "checked"){
					$("#btnCancelar").attr("disabled",true);
					$("#btnLiberarTodo").attr("disabled",true);
				}
				
				$.getJSON("../../servlet/ApartadoPrecomCancelarServlet?"+new Date().getTime(),{Param: params, ajax: false, async:false}, 
					function(data)
					{
						if(data.length == 0){
							alert("No se encontr\xF3 ningun registro.");
						}
						else{
							arrayCompleto=new Array();
							for(var i=0; i<data.length; i++){
								arrayCompleto [i]=[
									rtrim(ltrim(data[i].Col0)),
									rtrim(ltrim(data[i].Col1)),
									rtrim(ltrim(data[i].Col2)),
									rtrim(ltrim(data[i].Col3)),
									rtrim(ltrim(data[i].Col4)),
									rtrim(ltrim(data[i].Col5)),
									rtrim(ltrim(data[i].Col6))
								];
							}
							$('#tblRequisiciones').dataTable().fnAddData(arrayCompleto);
						}
						$("#btnBuscar").removeAttr("disabled");
						$("#mensajeProcesando").css("display","none");
						$("#mensajeProcesando").html("Procesando Cancelaci&oacute;n espere...");
				
						if($("#rbtParciales").attr("checked") == "checked"){
							$("#btnCancelarTodo").removeAttr("disabled");
							$("#btnCancelarSeleccionadas").removeAttr("disabled");
						}
						else if($("#rbtCompletos").attr("checked") == "checked"){
							$("#btnCancelar").removeAttr("disabled");
							$("#btnLiberarTodo").removeAttr("disabled");
						}
					}
				);
			}
			
			function consultaLineas(params){
				$("#tblLineas").dataTable().fnClearTable();
				$.getJSON("../../servlet/ApartadoPrecomCancelarServlet?"+new Date().getTime(),{Param: params, ajax: false, async:false}, 
					function(data)
					{
						if(data.length == 0){
							alert("No se encontr\xF3 ningun registro.");
						}
						else{
							arrayCompleto=new Array();
							for(var i=0; i<data.length; i++){
								arrayCompleto[i]=[
									"<input type='checkbox' id='chkLinea"+data[i].Col0+"' name='chkLinea' value='"+rtrim(ltrim(data[i].Col0))+"'>",
									rtrim(ltrim(data[i].Col0)),
									rtrim(ltrim(data[i].Col1)),
									rtrim(ltrim(data[i].Col2)),
									rtrim(ltrim(data[i].Col3))
								];
							}
							$('#tblLineas').dataTable().fnAddData(arrayCompleto);
						}
					}
				);
			}
			
			function consultaPrecompromisos(params){
				$("#tblPrecompromisos").dataTable().fnClearTable();
				$("#btnBuscar").attr("disabled",true);
				$("#btnCancelar").attr("disabled",true);
				$("#btnLiberarTodo").attr("disabled",true);
				$("#mensajeProcesando").css("display","");
				$("#mensajeProcesando").html("Buscando espere...");
				
				$.getJSON("../../servlet/ApartadoPrecomCancelarServlet?"+new Date().getTime(),{Param: params, ajax: false}, 
					function(data)
					{
						if(data.length == 0){
							alert("No se encontr\xF3 ningun registro.");
						}
						else{
							arrayCompleto=new Array();
							for(var i=0; i<data.length; i++){
								arrayCompleto[i]=[
									rtrim(ltrim(data[i].Col0)),
									rtrim(ltrim(data[i].Col1)),
									rtrim(ltrim(data[i].Col2)),
									rtrim(ltrim(data[i].Col4))
								];
							}
							$('#tblPrecompromisos').dataTable().fnAddData(arrayCompleto);
						}
						$("#btnBuscar").removeAttr("disabled");
						$("#btnCancelar").removeAttr("disabled");
						$("#btnLiberarTodo").removeAttr("disabled");
						$("#mensajeProcesando").css("display","none");
						$("#mensajeProcesando").html("Procesando Cancelaci&oacute;n espere...");
					}
				);
			}
			
			function consultaEP_Precom(params){
				var total = 0;
				
				$("#tblEP").dataTable().fnClearTable();
				$.getJSON("../../servlet/ApartadoPrecomCancelarServlet?"+new Date().getTime(),{Param: params, ajax: false}, 
					function(data)
					{
						if(data.length == 0){
							alert("No se encontr\xF3 ningun registro.");
						}
						else{
							arrayCompleto=new Array();
							for(var i=0; i<data.length; i++){
								total = 0;
								total = parseFloat(data[i].Col1)+parseFloat(data[i].Col2)+parseFloat(data[i].Col3)+
										parseFloat(data[i].Col4)+parseFloat(data[i].Col5)+parseFloat(data[i].Col6)+
										parseFloat(data[i].Col7)+parseFloat(data[i].Col8)+parseFloat(data[i].Col9)+
										parseFloat(data[i].Col10)+parseFloat(data[i].Col11)+parseFloat(data[i].Col12);
								
								if(total != 0){
									arrayCompleto [i]=[
										"<div>"+rtrim(ltrim(data[i].Col0))+"</div>",
										"<input type='text' id='Enero"+i+"' name='Enero"+i+"' value='"+rtrim(ltrim(data[i].Col1))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Febrero"+i+"' name='Febrero"+i+"' value='"+rtrim(ltrim(data[i].Col2))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Marzo"+i+"' name='Marzo"+i+"' value='"+rtrim(ltrim(data[i].Col3))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Abril"+i+"' name='Abril"+i+"' value='"+rtrim(ltrim(data[i].Col4))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Mayo"+i+"' name='Mayo"+i+"' value='"+rtrim(ltrim(data[i].Col5))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Junio"+i+"' name='Junio"+i+"' value='"+rtrim(ltrim(data[i].Col6))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Julio"+i+"' name='Julio"+i+"' value='"+rtrim(ltrim(data[i].Col7))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Agosto"+i+"' name='Agosto"+i+"' value='"+rtrim(ltrim(data[i].Col8))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Septiembre"+i+"' name='Septiembre"+i+"' value='"+rtrim(ltrim(data[i].Col9))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Octubre"+i+"' name='Octubre"+i+"' value='"+rtrim(ltrim(data[i].Col10))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Noviembre"+i+"' name='Noviembre"+i+"' value='"+rtrim(ltrim(data[i].Col11))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Diciembre"+i+"' name='Diciembre"+i+"' value='"+rtrim(ltrim(data[i].Col12))+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>",
										"<input type='text' id='Total"+i+"' name='Total"+i+"' value='"+total+"' readonly style='width:100px; border-width:0; background-color:transparent; text-align:center;'/>"
									];
									
									$("#Enero"+i).formatCurrency();
									$("#Febrero"+i).formatCurrency();
									$("#Marzo"+i).formatCurrency();
									$("#Abril"+i).formatCurrency();
									$("#Mayo"+i).formatCurrency();
									$("#Junio"+i).formatCurrency();
									$("#Julio"+i).formatCurrency();
									$("#Agosto"+i).formatCurrency();
									$("#Septiembre"+i).formatCurrency();
									$("#Octubre"+i).formatCurrency();
									$("#Noviembre"+i).formatCurrency();
									$("#Diciembre"+i).formatCurrency();
									$("#Total"+i).formatCurrency();
								}
							}
							$('#tblEP').dataTable().fnAddData(arrayCompleto);
						}
					}
				);
			}
			
			function cancelar(params,divMostrarProgreso,tipo){
				$("#btnCancelar").attr("disabled",true);
				$("#btnLiberarTodo").attr("disabled",true);
				$("#btnBuscar").attr("disabled",true);
				$("#btnCancelarTodo").attr("disabled",true);
				$("#btnCancelarSeleccionadas").attr("disabled",true);
				$("#"+divMostrarProgreso).css("display","");
				var mensaje = "";
					
				$.getJSON("../../servlet/ApartadoPrecomCancelarServlet?"+new Date().getTime(),{Param: params, ajax: false, aysnc: false}, 
					function(data)
					{
						for(var i=0; i<data.length; i++){
							if(data[i].mensaje != undefined && data[i].mensaje != "" && data[i].mensaje != null){
								mensaje = data[i].mensaje;
								alert(data[i].mensaje.toString().replace("OK:",""));
								break;
							}
						}
						$("#btnBuscar").removeAttr("disabled");
						$("#"+divMostrarProgreso).css("display","none");
						
						if(tipo == "APARTADO"){
							$("#btnCancelar").removeAttr("disabled");
							$("#btnLiberarTodo").removeAttr("disabled");
							
							if($("#rbtCompletos").attr("checked") == "checked"){
								if(mensaje.indexOf("OK:") >= 0 )
									tblRequisiciones.fnDeleteRow(getFilaSeleccionadaIndex("tblRequisiciones"));
							}
						}
						else if(tipo == "LINEAS"){
							$("#btnCancelarTodo").removeAttr("disabled");
							$("#btnCancelarSeleccionadas").removeAttr("disabled");
							var lineas = params.split(",")[6].split("|");
							
							if(mensaje.indexOf("OK:") >= 0 ){
								for(var i = 0; i < lineas.length; i++)
									tblLineas.fnDeleteRow(getFilaSeleccionadaLineaIndex(lineas[i]));								
								if(tblLineas.dataTable().fnGetNodes().length == 0)
									tblRequisiciones.fnDeleteRow(getFilaSeleccionadaIndex("tblRequisiciones"));
							}
						}
						else if(tipo == "PRECOMPROMISO"){
							$("#btnCancelar").removeAttr("disabled");
							$("#btnLiberarTodo").removeAttr("disabled");
							
							if(mensaje.indexOf("OK:") >= 0 ){
								tblPrecompromisos.fnDeleteRow(getFilaSeleccionadaIndex("tblPrecompromisos"));
								$("#tblEP").dataTable().fnClearTable();
							}
						}
						else if(tipo == ""){
							$("#btnCancelar").removeAttr("disabled");
							$("#btnLiberarTodo").removeAttr("disabled");
							$("#tblRequisiciones").dataTable().fnClearTable();
							$("#tblLineas").dataTable().fnClearTable();
							$("#tblPrecompromisos").dataTable().fnClearTable();
							$("#tblEP").dataTable().fnClearTable();
						}
					}
				);
			}
			
			function trim(cad){
				return cad.replace(/^\s+|\s+$/g, '');
			}
			
			function ltrim(cad){
				return cad.replace(/^\s+/,'');
			}
			
			function rtrim(cad){
				return cad.replace(/\s+$/,'');
			}
			
			/* Get the rows which are currently selected */
			function fnClearSelectedTable( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass("row_selected") )
						$(aTrs[i]).removeClass("row_selected"); 
				}
			}
			
			function getUR(){
				var ur = "*";
				var roles = "<%=roles%>"; 
				
				if($("#cIdUnidadEjecutora").val() != ""){
					if(roles.toString().indexOf("ADMIN_RECMAT") >= 0)
						ur = $("#cIdUnidadEjecutora").val().split("-")[0];
					else
						ur = $("#txtUnidadEjecutora").val().split("-")[0];
				}
				return ur;
			}
			
			function getTipoCancelacion(){
				var tipoCancelacion = "";
				
				if($("#rbtParciales").attr("checked") == "checked")
					tipoCancelacion = "PARCIALES";
				else if($("#rbtCompletos").attr("checked") == "checked")
					tipoCancelacion = "COMPLETO";
				return tipoCancelacion;
			}
			
			function getLineasSeleccionadas(){
				var lineasSeleccionadasStr = "";
				var lineasSeleccionadas = $('input[name="chkLinea"]:checked');
							
				for(var i = 0; i < lineasSeleccionadas.length; i++)
					lineasSeleccionadasStr += lineasSeleccionadas[i].value+"|";
							
				if(lineasSeleccionadasStr.length > 0)
					lineasSeleccionadasStr = lineasSeleccionadasStr.substring(0,lineasSeleccionadasStr.length-1);
				
				return lineasSeleccionadasStr;
			}
			
			function getLineasAll(){
				var aTrs = tblLineas.dataTable().fnGetNodes();
				var aData;
				var lineas = "";
				
				for(var i = 0; i < aTrs.length; i++){
					aData = tblLineas.fnGetData(aTrs[i]);
					lineas += aData[1]+"|";
				}
				
				if(lineas.length > 0)
					lineas = lineas.substring(0,lineas.length-1);
					
				return lineas;
			}
			
			function getPrecompromisoSeleccionado(){
				var aTrs = tblPrecompromisos.dataTable().fnGetNodes();
				var aData = "";
				
				for(var i=0; i<aTrs.length; i++){
					if($(aTrs[i]).hasClass("row_selected"))
						aData =  tblPrecompromisos.fnGetData(aTrs[i]);
				}
				
				return aData;
			}
			
			function getRequisicionSeleccionada(){
				var aTrs = tblRequisiciones.dataTable().fnGetNodes();
				var aData = "";
				
				for(var i=0; i<aTrs.length; i++){
					if($(aTrs[i]).hasClass("row_selected"))
						aData =  tblRequisiciones.fnGetData(aTrs[i]);
				}
				
				return aData;
			}
			
			function getFilaSeleccionadaIndex(tabla){
				var aTrs = $("#"+tabla).dataTable().fnGetNodes();
				var index = 0;
				
				for(var i=0; i<aTrs.length; i++){
					if($(aTrs[i]).hasClass("row_selected")){
						index = i;
						break;
					}
				}
				
				return index;
			}
			
			function getFilaSeleccionadaLineaIndex(linea){
				var aTrs = $("#tblLineas").dataTable().fnGetNodes();
				var index = 0;
				var aData;				
				
				for(var i=0; i<aTrs.length; i++){
					aData = tblLineas.fnGetData(aTrs[i]);
					if(parseInt(aData[1],10) == parseInt(linea,10)){
						index = i;
						break;
					}
				}
				
				return index;
			}
			
			function loadTableRequisiones(){
				tblRequisiciones =$("#tblRequisiciones").dataTable({        			
					sScrollX: "150%",
					//sScrollXInner: "110%",
					bScrollCollapse: true,
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
					},
					bJQueryUI: true
        		});
			}
			
			function loadTableLineas(){
				tblLineas =$("#tblLineas").dataTable({        			
					sScrollX: "110%",
					//sScrollXInner: "110%",
					bScrollCollapse: true,
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
					},
					bJQueryUI: true
        		});
			}
			
			function loadTablePrecompromisos(){
				tblPrecompromisos = $("#tblPrecompromisos").dataTable({        			
					//sScrollY : "100%",
					sScrollX: "120%",
					bAutoWith: true,
					//sScrollXInner: "110%",
					bScrollCollapse: true,
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
					},
					bJQueryUI: true
        		});
			}
			
			function loadTableEP_Precom(){
				tblEP = $("#tblEP").dataTable({        			
					//sScrollY : "100%",
					sScrollX: "110%",
					bAutoWith: true,
					//sScrollXInner: "110%",
					bScrollCollapse: true,
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
					},
					bJQueryUI: true
        		});
			}
			
			function setSequenceVal(seqValue) {
				seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
				$("#caNoPreCompromiso").val($("#cCentroContable").val() + "CO" + $("#cEjercicio").val() + seqValue);
			}
			
		</script>
	</head>
	<body id="dt_example" >
		<form>
			<input type="hidden" id="nFolioApartadoCancelar" name="nFolioApartadoCancelar" value="0"/>
			<input type="hidden" id="nFolioPrecompromisoCancelar" name="nFolioPrecompromisoCancelar" value="0" />
			<input type="hidden" id="folioDocumentoCancelar" name="folioDocumentoCancelar"/>
			<input type="hidden" name="cUnidadEjecutoraUsuario" id="cUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
			<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" value="<%= roles%>"/>
			<input type="hidden" name="cUnidadEjecutora" id="cUnidadEjecutora" value="<%= usuario.getU_UR()%>"/>
			<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=usuario.getPropiedad("CCENTROCONTABLE").getValor() %>"/>
			<input type="hidden" name="caNoPreCompromiso" id="caNoPreCompromiso"/>
			<input type="hidden" name="cEjercicio" id="cEjercicio"/>
		</form>	
		<div id="container" class="container">
    		<h1>Cancelar Apartado/Pre-compromiso<label id="lbOperacion" style="font-size: 8pt;"></label></h1>
			<div class="tabs" id="tabs" name="tabs">
				<fieldset>
					<div align="center">
						<table border="0" class="display" align="center" width="750px">
							<tr>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr>
								<td align="left">Unidad Ejecutora:</td>
								<td align="left">
									<script type="text/javascript" charset="utf-8">
										var rolUsr = "<%=roles%>";
										
						    			if (rolUsr.toString().indexOf("ADMIN_RECMAT") >= 0){
						    				document.write("<select name='cIdUnidadEjecutora' id='cIdUnidadEjecutora' style='width: 550px'></select>");
						    				
						    			}
						    			else
						    				document.write("<input type='text' name='txtUnidadEjecutora' id='txtUnidadEjecutora' style='width: 550px' disabled='disabled'/><input type='hidden' id='cIdUnidadEjecutora' name='cIdUnidadEjecutora' />");
						    		</script>
								</td>
							</tr>
							<tr>
								<td align="left">Documento:</td>
								<td align="left">
									<input type="text" id="documentoCancelar" size="60" name="documentoCancelar"/>
								</td>
							</tr>
							<tr>
								<td align="left">
									Tipo Documento:
								</td>
								<td align="left">
									<input type="radio" id="rbtApartado" name="tipoDocumento" value="1" checked/>&nbsp;Apartado
									&nbsp;&nbsp;
									<input type="radio" id="rbtPrecompromiso" name="tipoDocumento" value="2"/>&nbsp;Pre-compromiso
								</td>
							</tr>
							<tr id="trTipoCancelacion">
								<td align="left">Tipo Cancelaci&oacute;n:</td>
								<td align="left">
									<input type="radio" id="rbtParciales" name="tipoCancelacion" value="1"/>&nbsp;Parciales
									&nbsp;&nbsp;
									<input type="radio" id="rbtCompletos" name="tipoCancelacion" value="2" checked/>&nbsp;Completos
								</td>
							</tr>
							<tr>
								<td colspan="3" align="center">
									<table>
										<tr>
											<td colspan="3">
												<button id="btnBuscar" name="btnBuscar">Buscar</button>
												&nbsp;
												<button id="btnCancelar" name="btnCancelar">Liberar Seleccionado</button>
												&nbsp;
												<button id="btnLiberarTodo" name="btnLiberarTodo" style="display: none;">Liberar Todo</button>
											</td>
										</tr>
										<tr>
											<td colspan="3">
												<div id="mensajeProcesando" style="width: 300px; border: 0px solid;color:#FF0000;">Procesando Cancelaci&oacute;n espere...</div>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
				       	<div id="dTblRequisiciones" style="width: 740px;">
				       		<table id="tblRequisiciones" class="display" width="740px">
								<thead>
									<tr style="width: 740px">
										<th>Folio<br/>Apartado</th>
							            <th>Requisición</th>
							            <th>Descripci&oacute;n</th>
							            <th>Total L&iacute;neas<br/>Solicitud</th>
							            <th>Total L&iacute;neas<br/>Activas</th>
							            <th>Total L&iacute;neas<br/>Apartado</th>
							            <th>Total L&iacute;neas<br/>Precomprometidas</th>
						        	</tr>
						     	</thead>
							</table>
				       	</div>				       	
				       	<div id="dTblLineas" style="width: 740px;">
				       		<br/>
				       		<table width="740px">
				       			<tr>
				       				<td align="center">
				       					<table>
				       						<tr>
				       							<td>
				       								<button id="btnCancelarTodo" name="btnCancelarTodo">Liberar Todas</button>
													&nbsp;
													<button id="btnCancelarSeleccionadas" name="btnCancelarSeleccionadas">Liberar Seleccionadas</button>
				       							</td>
				       						</tr>
				       						<tr>
							       				<td>
							       					<div id="mensajeProcesando2" style="width: 300px; border: 0px solid;color:#FF0000;">Procesando Cancelaci&oacute;n espere...</div>
							       				</td>
							       			</tr>	
				       					</table>
									</td>
				       			</tr>
				       		</table>
				       		<br/>
				       		<table id="tblLineas" class="display" width="740px">
								<thead>
									<tr style="width: 740px">
										<th>&nbsp;</th>
							            <th># L&iacute;nea</th>
							            <th>CUCOP</th>
							            <th>Descripci&oacute;n</th>
							            <th>Monto Apartado</th>
						        	</tr>
						     	</thead>
							</table>
				       	</div>
				       	<div id="dTblPrecompromisos" style="width: 740px;">
				       		<table id="tblPrecompromisos" class="display" width="740px">
								<thead>
									<tr style="width: 740px">
										<th>Folio<br/>Precompromiso</th>
							            <th>Documento</th>
							            <th>Tipo</th>
							            <th>Total<br/>Precomprometido</th>
						        	</tr>
						     	</thead>
							</table>
				       	</div>
				       	<br/>
				       	<br/>
				       	<div id="dTblEP" style="width: 740px;">
				       		<br/>
				       		<table id="tblEP" class="display" width="740px">
								<thead>
									<tr style="width: 740px">
							            <th>EP</th>
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
							            <th>Total</th>
						        	</tr>
						     	</thead>
							</table>
				       	</div>
					</div>
				</fieldset>
			</div>
    	</div>			
	</body>
</html>