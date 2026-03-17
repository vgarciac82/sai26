<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab.getLogin();
	String role="";
	Map<?,?> rol =usuarioTab.getRoles();
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String isConvEjercicioAnt="";
	String cCentroContable = usuarioTab.getPropiedad("CCENTROCONTABLE")
				.getValor();
	if (session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
		isConvEjercicioAnt = (String)session.getAttribute(GestionInterface.ATT_ContratoIsModificatorioEjercicioAnt);
		
		cIdTipoContrato = cIdContrato.split("-")[0];
		cIdUnidadEjecutora = cIdContrato.split("-")[1];
		nIdConsecutivo = cIdContrato.split("-")[2];		
	}else 
		response.sendRedirect("ContratoModificatorio.jsp?tab=1");
%>
<!DOCTYPE html>
<html>
  <head>
  	<title>Compromiso Contrato Modificado</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0"> 
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		$(document).ready(function() {
			tabb=5;
			showAndHideTabs();
			query();
			initTables();
			cargaCompromiso();
		});//Fin del document ready
		function query(){
			$.ajax({url: "../../servlet/ConvenioModificatorioServlet" , type:'post' , async: false
				,data:'operacion=0&pestana=4&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
				+'&cIdContrato='+$("#cContrato").val()
				+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
				, dataType: 'json', success: 
					function(j){
						vaciarJsonAInputs(j[0].datosGuardados);
						showButtons();
						showHidePestanas();
					}
				});
		}
		function initTables(){
			$('#dt_compromiso').dataTable(
				{
	  			    "bPaginate": false,
	      			"bLengthChange": false,
	      			"bFilter": false,
	      			"bSort": false,
	      			"bInfo": false,
	      			"bAutoWidth": false, 
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );
		}
		function cargaCompromiso(){
			var funcion="fn_mCompromisoCapturaConvRed";
			var param="'"+encodeURIComponent($("#cContratoDefinitivo").val())+"',"+$("#cConsecutivoMod").val();
			var oTable=$('#dt_compromiso').dataTable( {
				bScrollCollapse: false,
	    		bInfo: false,
	    		sScrollX: '100%',
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				bPaginate: false,
	   			bLengthChange: false,
	   			bFilter: false,
	   			bInfo: false,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+funcion+"("+param+")",
				aoColumns: [
					{ sName: "ClaveSIAFF" ,bSortable: false},
					{ sName: "ClaveInterna",bSortable: false },
					{ sName: "MontoEnero" ,bSortable: false},
					{ sName: "captMontoEnero" ,bSortable: false},
					{ sName: "MontoFebrero" ,bSortable: false},
					{ sName: "captMontoFebrero" ,bSortable: false},
					{ sName: "MontoMarzo" ,bSortable: false},
					{ sName: "captMontoMarzo" ,bSortable: false},
					{ sName: "MontoAbril",bSortable: false },
					{ sName: "captMontoAbril" ,bSortable: false},
					{ sName: "MontoMayo" ,bSortable: false},
					{ sName: "captMontoMayo" ,bSortable: false},
					{ sName: "MontoJunio",bSortable: false },
					{ sName: "captMontoJunio" ,bSortable: false},
					{ sName: "MontoJulio" ,bSortable: false},
					{ sName: "captMontoJulio" ,bSortable: false},
					{ sName: "MontoAgosto" ,bSortable: false},
					{ sName: "captMontoAgosto" ,bSortable: false},
					{ sName: "MontoSeptiembre" ,bSortable: false},
					{ sName: "captMontoSeptiembre" ,bSortable: false},
					{ sName: "MontoOctubre",bSortable: false },
					{ sName: "captMontoOctubre" ,bSortable: false},
					{ sName: "MontoNoviembre" ,bSortable: false},
					{ sName: "captMontoNoviembre" ,bSortable: false},
					{ sName: "MontoDiciembre" ,bSortable: false},
					{ sName: "captMontoDiciembre" ,bSortable: false},
					{ sName: "MontoAnual",bSortable: false },
					{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false } ]
            });
		}
		function sumaCaptura(id,compromiso){
			var suma=0;
			if($("#"+id).val()==""){
				$("#"+id).val(0.0);
			}
			if(parseFloat($("#"+id).val())>parseFloat(compromiso)){
				swal("No es posible capturar está reducción.\nEl monto capturado de "+$("#"+id).val()+" es mayor a "+compromiso+" el cuál es el monto comprometido que hay en el mes",{icon:"info",button: "Cerrar"});
				$("#"+id).val(0.0);
			}
			suma=totalCapturado();
			$("#mPreComprometer").val(Math.round(suma * 100) / 100);
			$("#difPrecompromiso").val(Math.round(((suma*-1)+parseFloat(unFrmt2($("#mImporteTotalReduccion").val())))*100)/100);
		}
		function totalCapturado(){
			var aTrs = $('#dt_compromiso').dataTable().fnGetNodes();
			var nTr;
			var jqInputs;
			var vimporteP=0;
			var suma=0;
			var nombre="";
			for ( var i=0 ; i<aTrs.length; i++ ){
				nTr =  $('#dt_compromiso').dataTable().fnGetData(aTrs[i]);
				jqInputs = $('input',aTrs[i] );
				for ( j=0 ; j < jqInputs.length ; j++ ) {
					vimporteP = jqInputs[j].value ;
					nombre=jqInputs[j].name;
					vimporteP = unFrmt2(vimporteP);
					suma=parseFloat(suma)+((parseFloat(vimporteP))*-1);
				}
			}
			return suma;
		}
		function aprobarReduccion(){
			var arregloDatos =tablaEpMontos();
			if($("#difPrecompromiso").val()!=0.0){
				swal("No es posible aprobar el convenio porque no se ha capturado todo el calendario del compromiso a reducir o se capturo de mas.",{icon:"info",button: "Cerrar"});
				return;
			}
			arregloDatos=tablaEpMontos();
			$.ajax({url: "../../servlet/ConvenioModificatorioServlet" , type:'post' , async: false
				,data:'operacion=1&pestana=4&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
				+'&cIdContrato='+$("#cContrato").val()
				+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
				+'&arregloDatos='+arregloDatos
				, dataType: 'json', success: 
					function(j){
						swal(j[0].MSG,{icon:"info",button: "Cerrar"});
						vaciarJsonAInputs(j[0].datosGuardados);
						cargaCompromiso();
						showButtons();
					}
			});
		}
		function tablaEpMontos(){
			var mesesCap="";
			var row="";
			var token="";
			var token2="";
			var table="";
			var aTrs = $('#dt_compromiso').dataTable().fnGetNodes();
			var vimporteP;
			var nTr;
			var jqInputs;
			var isCaptura=false;
			for ( var i=0 ; i<aTrs.length; i++ ) {
				nTr =  $('#dt_compromiso').dataTable().fnGetData(aTrs[i]);
				jqInputs = $('input',aTrs[i] );
				for ( j=0 ; j < jqInputs.length ; j++ ) {
					if(jqInputs[j].value!=0){
						mesesCap=mesesCap+token+'{"nameMes":"'+jqInputs[j].name+'","value":'+jqInputs[j].value+'}'
						token=",";
						isCaptura=true;
					}
				}
				token="";
				if(isCaptura){
					row='{"claveSIAFF":"'+nTr[0]+'","claveInterna":"'+nTr[1]+'","datosMesesCap":['+mesesCap+']}'
					table=table+token2+row
					token2=",";
				}
				mesesCap="";
				isCaptura=false;
			}
			return '['+table+']';
		}
		function devuelveAprobacion(){
			swal({
				title: "",
				text: "¿Está seguro de devolver el proceso?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$.ajax({url: "../../servlet/ConvenioModificatorioServlet" , type:'post' , async: false
						,data:'operacion=2&pestana=4&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
						+'&cIdContrato='+$("#cContrato").val()
						+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
						, dataType: 'json', success: 
							function(j){
								swal(j[0].MSG,{icon:"info",button: "Cerrar"});
								vaciarJsonAInputs(j[0].datosGuardados);
								cargaCompromiso();
								showButtons();
							}
					});
				}
			});
					
		}
		function autorizar(){
			swal({
				title: "",
				text: "¿Está seguro de autorizar el convenio de reducci\u00f3n?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
				},
			}).then((continuar) => {
				if (!continuar) {
					return;
				}else{
					$.ajax({url: "../../servlet/ConvenioModificatorioServlet" , type:'post' , async: false
						,data:'operacion=3&pestana=4&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
						+'&cIdContrato='+$("#cContrato").val()
						+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
						, dataType: 'json', success: 
							function(j){
								swal(j[0].MSG,{icon:"info",button: "Cerrar"});
								vaciarJsonAInputs(j[0].datosGuardados);
								showButtons();
							}
					});
				}
			});
			
		}
		function showButtons(){
			
			if($("#nIdEstado").val()==2){
				$("#imgAprobarRedCompromisoContMod").show();
				$("#imgAutorizarsContMod").hide();
				$("#imgDevolverAprobacionContMod").hide();
			}else if($("#nIdEstado").val()==3){
				$("#imgDevolverAprobacionContMod").show();
				$("#imgAutorizarsContMod").show();
				$("#imgAprobarRedCompromisoContMod").hide();
			}else{
				$("#imgAprobarRedCompromisoContMod").hide();
				$("#imgDevolverAprobacionContMod").hide();
				$("#imgAutorizarsContMod").hide();
			}
		}
	</script>
  </head>
  <body>
  	<form id="formCompromiso">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Compromiso del Contrato Modificado</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarRedCompromisoContMod" name="imgAprobarRedCompromisoContMod" 	value="Aprobar"	onclick="aprobarReduccion();" title="Aprobar convenio de reducción"/>
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverAprobacionContMod" name="imgDevolverAprobacionContMod" 	value="Devolver"	onclick="devuelveAprobacion();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAutorizarsContMod" name="imgAutorizarsContMod" 	value="Autorizar"	onclick="autorizar();" title="Autoriza convenio de reducción"/>
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoModificatorio.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblAutorizaSICOP" id="lblAutorizaSICOP"  readonly style="color:blue"/>
					</div>
				</div>
				
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalAnterior" id="lblTotalAnterior" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalModificado" id="lblTotalModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblPorcentajeMod" id="lblPorcentajeMod" readonly />
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Captura compromiso de reducci&oacute;n</legend>
			<div class="form-group" >
				<div class="row">
					<div class="input-group">
						<div class="col">
							<label for="mImporteTotal">Monto a Reducir:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="20"  aria-describedby="basic-addon1"  id="mImporteTotalReduccion" name="mImporteTotalReduccion" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Monto Capturado:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="mPreComprometer" name="mPreComprometer" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Saldo a Capturar:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="difPrecompromiso" name="difPrecompromiso" value="0">
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						<input type="text" class="form-control transpInput" name="etiquetadeCapturaMonto" id="etiquetadeCapturaMonto" readonly
						value="Favor de capturar los montos de la reducción en la siguiente tabla, capturar el monto en positivo y el sistema lo pone en negativo" />
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="dt_compromiso" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th>Estructura Program&aacute;tica</th>
										<th>Clave Interna</th>
										<th>Compromiso<br/>Enero</th>
										<th>Enero</th>
										<th>Compromiso<br/>Febrero</th>
										<th>Febrero</th>
										<th>Compromiso<br/>Marzo</th>
										<th>Marzo</th>
										<th>Compromiso<br/>Abril</th>
										<th>Abril</th>
										<th>Compromiso<br/>Mayo</th>
										<th>Mayo</th>
										<th>Compromiso<br/>Junio</th>
										<th>Junio</th>
										<th>Compromiso<br/>Julio</th>
										<th>Julio</th>
										<th>Compromiso<br/>Agosto</th>
										<th>Agosto</th>
										<th>Compromiso<br/>Septiembre</th>
										<th>Septiembre</th>
										<th>Compromiso<br/>Octubre</th>
										<th>Octubre</th>
										<th>Compromiso<br/>Noviembre</th>
										<th>Noviembre</th>
										<th>Compromiso<br/>Diciembre</th>
										<th>Diciembre</th>
										<th>Compromiso<br/>Anual</th>
										<th>Contrato</th>
									</tr>										
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
	    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
	    <input type="hidden" name="nIdEstado" id="nIdEstado" />
	    <input type="hidden" name="tipoMod" id="tipoMod" />
	</form>
  </body>
</html>