<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@page import="com.syc.gestion.core.NegativaPestana"%>
<%	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	Map rol = null;
	String roles="";
	
	if (usuario == null){
		response.sendRedirect("../index.jsp");
		return;
	}else{
		rol = usuario.getRoles();
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
		<title>Cancela Apartado</title>
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
				//if(roles.toString().indexOf("ADMIN_RECMAT") >= 0)				
				querySelectPost("UnidadBusca", "cIdUnidadEjecutora", {async: false});
				//else
				//	queryFormPost("tCatalogoUnidadEjecutoraTxtRead", { async:false });
				$("#cIdUnidadEjecutora").val($("#cUnidadEjecutoraUsuario").val());
				$("#dTblLineas").css("display","none");
				$("#dTblPrecompromisos").css("display","none");
				$("#dTblEP").css("display","none");
				$("#mensajeProcesando").css("display","none");
				$("#mensajeProcesando2").css("display","none");
				queryFormPost("readEjercicioFiscalActivo", { async:false });
				
				//Inicializa tablas
				loadTableRequisiones();
				querySelectPost("mCatalogoPeriodoRead", "mesInicio", {async: false});
				querySelectPost("mCatalogoPeriodoRead", "mesFin", {async: false});
								
				$("#btnBuscar").button().click(function(){
					if(validaDocumento() == 1){
						if(parseInt($("#mesInicio").val(),10)==0 || parseInt($("#mesFin").val(),10)==0 ){
							 alert("Debe seleccionar un rango de mes de Inicio y Mes Fin para poder realizar la consulta");
							 return;
						}
						if($("#rbtApartado").attr("checked") == "checked"){
							consultaRequisicion($("#documentoCancelar").val()+","+$("#folioDocumentoCancelar").val()+",CONSULTA,CONSULTACANCELAAPARTADO,"+getUR()+","+getTipoCancelacion()+",,"+$("#cEjercicio").val()+","+$("#mesInicio").val()+","+$("#mesFin").val());
							//$("#tblLineas").dataTable().fnClearTable();
						} else if( $("#rbtPrecompromiso").attr("checked") == "checked"){
								consultaPrecompromisos($("#documentoCancelar").val()+","+$("#folioDocumentoCancelar").val()+",CONSULTA,CONSULTACANCELADOPRECOMPROMISO,"+getUR()+",,,"+$("#cEjercicio").val()+","+$("#mesInicio").val()+","+$("#mesFin").val());
								//$("#tblEP").dataTable().fnClearTable();
						} 
					}
				});
				$("#btnCancelar").button().click(function(){
					var aData = null;
					if(parseInt(validaDocumento(),10) == 1){
						 if(parseInt($("#mesInicio").val(),10)==0 || parseInt($("#mesFin").val(),10)==0 ){
						 alert("Debe seleccionar un rango de mes de Inicio y Mes Fin para poder realizar la consulta");
						 return;
						}else{
							if($("#rbtApartado").attr("checked") == "checked"){
								var aTrs = tblRequisiciones.dataTable().fnGetNodes();
								var aData = "";
								var folios=" ";
								var folios = [];
								var els = document.getElementsByName('folioApartado_');
								for (var i=0;i<els.length;i++){
								  if ( els[i].checked ) {
								    folios.push(els[i].value);
								  }
								}
								if(folios.length==0){
									alert("Debe elegir los documentos a cancelar");
									return;
								}else{
									if(confirm("\xBFEst\xE1s seguro de cancelar los documentos?"))
										cancelar("folio,folio,CANCELAR,CONSULTACANCELAAPARTADO,"+getUR()+","+getTipoCancelacion()+",ALL,"+$("#cEjercicio").val()+","+folios,"mensajeProcesando","APARTADO");
								}
							 }
							 //esta activado el radiobutton del precompromiso
							 if($("#rbtPrecompromiso").attr("checked") == "checked"){
								var aTrs = tblPrecompromisos.dataTable().fnGetNodes();
								var aData = "";
								var folios=" ";
								var folios = [];
								var els = document.getElementsByName('folioPrecompromiso_');
								for (var i=0;i<els.length;i++){
								  if ( els[i].checked ) {
								    folios.push(els[i].value);
								  }
								}
								if(folios.length==0){
									alert("Debe elegir los documentos a cancelar");
									return;
								}else{
									if(confirm("\xBFEst\xE1s seguro de cancelar los documentos?"))
										cancelar("folio,folio,CANCELAR,CONSULTACANCELADOPRECOMPROMISO,"+getUR()+","+getTipoCancelacion()+",ALL,"+$("#cEjercicio").val()+","+folios,"mensajeProcesando","PRECOMPROMISO");
								}
							 }
						}
					}
				});
				$("#rbtCompletos").live("click", function() {
					$("#tblLineas").dataTable().fnClearTable();
					$("#tblRequisiciones").dataTable().fnClearTable();
					$("#dTblLineas").css("display","none");
					$("#btnCancelar").removeAttr("disabled");
					$("#btnLiberarTodo").removeAttr("disabled");
				});
				$("#rbtApartado").live("click", function() {
					//$("#tblLineas").dataTable().fnClearTable();
					$("#tblRequisiciones").dataTable().fnClearTable();
					//$("#trTipoCancelacion").css("display","");
					$("#dTblRequisiciones").css("display","");
					$("#dTblPrecompromisos").css("display","none");
					//$("#dTblEP").css("display","none");
				});
		        $("#rbtPrecompromiso").live("click", function() {
					//$("#tblLineas").dataTable().fnClearTable();
					$("#tblRequisiciones").dataTable().fnClearTable();
					//$("#trTipoCancelacion").css("display","none");
					//$("#dTblLineas").css("display","none");
					$("#dTblRequisiciones").css("display","none");
					$("#dTblPrecompromisos").css("display","");
					//$("#dTblEP").css("display","");
					$("#btnCancelar").removeAttr("disabled");
					//$("#btnLiberarTodo").removeAttr("disabled");
					loadTablePrecompromisos();
					//loadTableEP_Precom();
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
			function consultaPrecompromisos(params){
				$("#tblPrecompromisos").dataTable().fnClearTable();
				$("#btnBuscar").attr("disabled",true);
				$("#btnCancelar").attr("disabled",true);
				//$("#btnLiberarTodo").attr("disabled",true);
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
							var arreglo = new Array();
							
							for(var i=0; i<data.length; i++){
								arr = [rtrim(ltrim(data[i].Col0)),rtrim(ltrim(data[i].Col3)),rtrim(ltrim(data[i].Col2)),rtrim(ltrim(data[i].Col6)),rtrim(ltrim(data[i].Col4))];
								arreglo.push(arr);
							}
							$('#tblPrecompromisos').dataTable().fnAddData(arreglo);
						}
						$("#btnBuscar").removeAttr("disabled");
						$("#btnCancelar").removeAttr("disabled");
						//$("#btnLiberarTodo").removeAttr("disabled");
						$("#mensajeProcesando").css("display","none");
						$("#mensajeProcesando").html("Procesando Cancelaci&oacute;n espere...");
					}
				);
			}
			function consultaRequisicion(params){
				$("#tblRequisiciones").dataTable().fnClearTable();
				$("#btnBuscar").attr("disabled",true);
				$("#mensajeProcesando").css("display","");
				$("#mensajeProcesando").html("Buscando espere...");
				$("#btnCancelar").attr("disabled",true);
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
									rtrim(ltrim(data[i].Col3)),
									rtrim(ltrim(data[i].Col2)),
									rtrim(ltrim(data[i].Col6)),
									rtrim(ltrim(data[i].Col4))
								];
							}
							$('#tblRequisiciones').dataTable().fnAddData(arrayCompleto);
						}
						$("#btnBuscar").removeAttr("disabled");
						$("#mensajeProcesando").css("display","none");
						$("#mensajeProcesando").html("Procesando Cancelaci&oacute;n espere...");
						$("#btnCancelar").removeAttr("disabled");
						$("#btnLiberarTodo").removeAttr("disabled");
					}
				);
			}
			function cancelar(params,divMostrarProgreso,tipo){
				$("#btnCancelar").attr("disabled",true);
				$("#btnLiberarTodo").attr("disabled",true);
				$("#btnBuscar").attr("disabled",true);
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
							$("#btnBuscar").removeAttr("disabled");
							consultaRequisicion($("#documentoCancelar").val()+","+$("#folioDocumentoCancelar").val()+",CONSULTA,CONSULTACANCELAAPARTADO,"+getUR()+","+getTipoCancelacion()+",,"+$("#cEjercicio").val()+","+$("#mesInicio").val()+","+$("#mesFin").val());
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
				var folios=" ";
				
				for(var i=0; i<aTrs.length; i++){
					aData =  tblRequisiciones.fnGetData(aTrs[i]);
					num=aData[4];
					if(document.getElementById("folioApartado_"+num+"").checked){
						folios=folios+aData[4]+",";
					}
				}
				return folios;
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
    		<h1>Cancelar Apartado/Precompromiso<label id="lbOperacion" style="font-size: 8pt;"></label></h1>
			<div class="tabs" id="tabs" >
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
							<tr style="display:none">
								<td align="left">Documento:</td>
								<td align="left" style="display:none">
									<input type="text" id="documentoCancelar" size="60" name="documentoCancelar" value=""/>
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
							<!-- <tr id="trTipoCancelacion">
								<td align="left">Tipo Cancelaci&oacute;n:</td>
								<td align="left">
									<input type="radio" id="rbtCompletos" name="tipoCancelacion" value="2" checked/>&nbsp;Completos
								</td>
							</tr> -->
							<tr id="trMesCancelacion">
								<td align="left">Mes Inicio</td>
								<td align="left">
									<select name='mesInicio' id='mesInicio' style='width: 100px' ></select>
								     &nbsp;&nbsp;&nbsp;&nbsp;Mes Fin 
									<select name='mesFin' id='mesFin' style='width: 100px' ></select>
								</td>
							</tr>
							
							<tr>
								<td colspan="3" align="center">
									<table>
										<tr>
											<td colspan="3">
												<input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" class="btnInterfaceBG ui-button ui-corner-all"/>
												&nbsp;
												<input type="button" id="btnCancelar" name="btnCancelar" value="Liberar Seleccionado" class="btnInterfaceBG ui-button ui-corner-all"/>
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
										<th>Seleccion</th>
										<th>Folio<br/>Apartado</th>
							            <th>Documento</th>
							            <th>Descripci&oacute;n</th>
							            <th></th>
							       </tr>
						     	</thead>
							</table>
				       	</div>				       	
				      
				    	<div id="dTblPrecompromisos" style="width: 740px;">
				       		<table id="tblPrecompromisos" class="display" width="740px">
								<thead>
									<tr style="width: 740px">
									   <th>Seleccion</th>
										<th>Folio<br/>Precompromiso</th>
							            <th>Documento</th>
							             <th>Descripci&oacute;n</th>
							             <th></th>
							            
						        	</tr>
						     	</thead>
							</table>
				       	</div>
				       	<br/>
				       	<br/>
				    
					</div>
				</fieldset>
			</div>
    	</div>			
	</body>
</html>