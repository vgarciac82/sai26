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
	Map rol =usuarioTab.getRoles();
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
    <title>Car&aacute;tula Contrato Modificado</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">	
	var roles="",porcentajeTotalAumentado=0;
	var actualiza=false;
		$(document).ready(function() {
			calculosTotales();
			<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role+= r.getKey().toString()+",";
				}
				if(role.length()>0){
					role = role.substring(0,role.length()-1);
				}
				Map botones=nb.getBotones(role,"ContratoModificatorio","caratulaContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
				
			%>
			roles="<%=role%>";
			PartidasModificadasConvRed="";
			tabb=2;
			showAndHideTabs();
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
		    $("#isConvEjercicioAnt").val("<%=isConvEjercicioAnt %>");
			setFieldsInit();
			if(parseInt($("#tipoMod").val())<=1){
				$("#fielsetPartidasMods").show();
				mostrarPartidasMods();
			}
			
			if(parseInt($("#tipoMod").val())==4){
				mostrarPartidasUE();
			}
			if(parseInt($("#tipoMod").val())!=1){
				$("#compromisoContratoMod").hide();
			}
			mostrarPartidasModificadas();
			
			$('#tblContratoPartidasMods').on('dblclick','tr', function() {
				if($("#tipoMod").val() == 0){
					if(porcentajeTotalAumentado>=20){
						swal("Se ha llegado al máximo aumento disponible",{icon:"info",button: "Cerrar"});
						return;
					}else{
						$(ContratoPartidasMods.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});						
						$(this).addClass('row_selected');
						
						if ($("#tipoMod").val() == 0 && parseInt($("#nIdEstado").val(),10) == 1) {
							var anSelected = fnGetSelected( ContratoPartidasMods );
							var aData = ContratoPartidasMods.fnGetData(anSelected[0]);
							
							var cabm = $.trim(aData[1]);
							//var precio = aData[4].replace('$', '').replace(',', '');
							var precio=quitaFmt(aData[5]);
							var porcLin=parseFloat(aData[8]);
							if(porcLin<20)					
								mostrarReqsMods(cabm, precio);
							else
								swal("La línea seleccionada ya no se puede aumentar al contrato, debido a que llego a su máximo aumento.",{icon:"info",button: "Cerrar"});
						}
					}
				}else{
					if(porcentajeTotalAumentado>=10){
						swal("Se ha llegado al máximo decremento disponible",{icon:"info",button: "Cerrar"});
						return;
					}else{
						$(ContratoPartidasMods.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});						
						$(this).addClass('row_selected');
						
						if ($("#tipoMod").val() == 1 && parseInt($("#nIdEstado").val(),10) == 1) {
							var anSelected = fnGetSelected( ContratoPartidasMods );
							var aData = ContratoPartidasMods.fnGetData(anSelected[0]);
							addPartida(aData[0]);
						}
					}
				}
					
			});
			
			$('#tblReqsMods').on('dblclick','tr', function() {
				$(ReqsMods.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
			});
			
			$('#tblPartidasMods').on('dblclick','tr', function() {
				$(PartidasModificadas.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
			});
			
			$("#tblReqsMods").dataTable({
				//sScrollY: "200px",
				sScrollX: "100%",
				//sScrollXInner: "100%",
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy: true,
				bAutoWidth: true,
				bRetrive : true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay registros",
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
				}
			});
			$("#cContratoDefinitivoConv").val($("#cContratoDefinitivo").val()+"#M"+$("#cConsecutivoMod").val());
			queryFormPost("cargaHipervConv", { async : false});
			queryFormPost("ExisteHipervConv", { async : false});
			if($("#existeRegistro").val()!=0){
				actualiza=true;
			}
		});
		
		function calculosTotales(){
			queryFormPost("mMontoTotalSP", {async: false});
			queryFormPost("mMontoTotalRegistradoSP", {async: false});
			var mContratoMN=0,mContratoMontoNeto=0;
			mContratoMontoNeto= parseFloat($("#mContratoMontoNeto").val());
			if($("#mContratoMN").val()==""){
				$("#mContratoMN").val(0);
			}
			mContratoMN= parseFloat($("#mContratoMN").val());
			porcentajeTotalAumentado=(parseFloat(((mContratoMN*100)/mContratoMontoNeto))).toFixed(2);
			$("#lblTotalContratoOriginal").val("Total Contrato Original: $"+formateaMoneda(mContratoMontoNeto));
			$("#lblTotalPorcentajeMod").val("Porcentaje Total Modificado: "+porcentajeTotalAumentado+"%");
			$("#lblTotalContratoModificado").val("Total Modificado: $"+formateaMoneda(mContratoMN));			
		}
		
		function ocultar(){
			$("#trPartidasPresupCont").hide();
			$("#trPartidasPresupContNuevas").hide();
			document.getElementById("trfini").style.display="none";
			document.getElementById("trffin").style.display="none";
			document.getElementById("trfentrega").style.display="none";
			$("#bajaDiv").css("visibility", "hidden");
			$("#trpartidas").hide();
			$("#trpartidasue").hide();
			$("#trrequiDispoMod").hide();
			$("#trPartidasMod").hide();
			$("#trpartidasUE").hide();
			$("#agregaPartPresupContMod").hide();
			
		}
		function setInitQueys(){
			queryFormPost("mContratoHeaderRead", {async: false});
			if(parseInt($("#lContratoAbierto").val())==1){
				queryFormPost("mContratoModificadoTotalesMax", {async: false});
			}else{
				queryFormPost("mContratoModificadoTotales", {async: false});
			}
			
			//obtiene datos del procedimiento original para cargar fechas
			queryFormPost("mContratoModificatorioProcedimiento", {async: false});
			queryFormPost("mContratoFechaFormalizacionModificado", {async: false});
			if($("#cIdTipoProcedimiento").val()=="PC" || $("#cIdTipoProcedimiento").val()=="PT"){
				document.getElementById("trfini").style.display="none";
				document.getElementById("trffin").style.display="none";
				document.getElementById("trfentrega").style.display="block";
				queryFormPost("mContratoFechaEntregaModificado", {async: false});
			
			}else{
				document.getElementById("trfini").style.display="block";
				document.getElementById("trffin").style.display="block";
				document.getElementById("trfentrega").style.display="none";
				queryFormPost("mContratoFechaInicioModificado", {async: false});
				queryFormPost("mContratoFechaFinModificado", {async: false});
			}
		}
		function setInitQueysContPlu(){
			queryFormPost("mContratoHeaderReadPLU", {async: false});
			queryFormPost("mContratoPluModificadoTotales", {async: false});
			//Obtener Fechas
			queryFormPost("mContratoPluFechas", {async: false});
			if($("#cIdTipoContrato").val()=="CC"){
				document.getElementById("trfini").style.display="none";
				document.getElementById("trffin").style.display="none";
				document.getElementById("trfentrega").style.display="block";
			}else{
				document.getElementById("trfini").style.display="block";
				document.getElementById("trffin").style.display="block";
				document.getElementById("trfentrega").style.display="none";
			}
		}
		function setInitQueysContEjerAnt(){
			queryFormPost("mContratoHeaderReadConvAnt", {async: false});
			queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
			queryFormPost("mContratoConvAntFechas", {async: false});
			
			if($("#cIdTipoContrato").val()=="CC"){
				document.getElementById("trfini").style.display="none";
				document.getElementById("trffin").style.display="none";
				document.getElementById("trfentrega").style.display="block";
			}else{
				document.getElementById("trfini").style.display="block";
				document.getElementById("trffin").style.display="block";
				document.getElementById("trfentrega").style.display="none";
			}
		}
		function mostrarPartidasMods(){
			var campos = "'" + $("#cEjercicio").val() + "','" + $("#cContrato").val() + "','" + $("#cContratoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
			var query="fn_mContratoModificadoPartidas(" + campos + ")";
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
				campos = "'" + $("#cContratoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
				query="fn_mContratoPluModificadoPartidas(" + campos + ")";
				
			}
			if(1==$("#isConvEjercicioAnt").val()){
				campos = "'" + $("#cContratoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
				query="fn_mContratoModEjercAntPartidas(" + campos + ")";
			}
			ContratoPartidasMods = $("#tblContratoPartidasMods").dataTable({
				//sScrollY: "200px",
				sScrollX: "100%",
				//sScrollXInner: "200%",
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth: true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay registros",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaConsolidado" },
					{ sName: "cIdCABM" },
					{ sName: "Descripcion" },
					{ sName: "DescripcionAdicional" },
					{ sName: "Cantidad" },
					{ sName: "PrecioUnitario" },
					{ sName: "MontoBruto" },
					{ sName: "MontoNeto" },
					{ sName: "PorcentajeMod" },
					{ sName: "cIdConsolidado", bVisible: false },
					{ sName: "MontoOriginal", bVisible: false },
					{ sName: "MontoMod", bVisible: false },
					{ sName: "nIVA", bVisible: false },
					{ sName: "cIdTipoConsolidado", bVisible: false },
					{ sName: "nIdConsecutivoConsolidado", bVisible: false }
				]
			});	
		}
		function mostrarPartidasUE(){
			var campos = "'" +$("#cContratoDefinitivo").val() + "',"+$("#cConsecutivoMod").val();
			var query="fn_mContratoModificadoPartidasUE(" + campos + ")";
			if(1==$("#isConvEjercicioAnt").val()){
				//codificar
			}
			ContratoPartidasUE = $("#tblContratoPartidasUE").dataTable({
				sScrollX: "100%",
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth: true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay registros",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaConsolidado" },
					{ sName: "cIdCABM" },
					{ sName: "cDescripcion" },
					{ sName: "cDescripcionAdicional" },
					{ sName: "cIdUnidadEjecutoraSolicitud" },
					{ sName: "UE_Nueva" },
					{ sName: "cIdContratoDefinitivo",bVisible: false  },
					
					{ sName: "cEjercicio",bVisible: false  },
					{ sName: "cIdContrato",bVisible: false  },
					{ sName: "cIdTipoConsolidado",bVisible: false  },
					{ sName: "nIdConsecutivoConsolidado",bVisible: false  }
				]
			});	
		}
		
		
		
		
		function formateaMoneda(importe){
			var importeSeparado = importe.toString().split("\.");
			var importeParte1 = importeSeparado[0];
			var cont=0;
			var tem="";
			
			for(var i=importeParte1.length; i>0; i--){
				if(cont == 3){
					tem = ","+tem;
					cont=0;
				}
				tem = importeParte1.substring(i-1,i)+tem;
				cont++;
			}
			if(importe.toString().indexOf("\.")>0){
				for(var i=importeSeparado[1].length; i<2; i++){
					importeSeparado[1]+="0";
				}
				return tem+"."+importeSeparado[1];
			}
			else{
				return tem+".00";
			}
		}
		
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		
		function fnGetSelected( oTableLocal ) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ ) {
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		
		function devolverPartidaModificada(){
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			var anSelected = fnGetSelected( PartidasModificadas );
			if (anSelected.length > 0) { 
				var aData = PartidasModificadas.fnGetData(anSelected[0]);
				var monto = aData[13];
				var totalModAct=parseFloat($("#totalMod").val()) - parseFloat(monto);
				totalModAct=totalModAct.toFixed(2);
				$("#totalModAct").val(totalModAct);
				var totalNuevoAct=parseFloat($("#totalNuevo").val()) - parseFloat(monto);
				totalNuevoAct=totalNuevoAct.toFixed(2);
				$("#totalNuevoAct").val(totalNuevoAct);
				
				$("#cIdTipoConsolidado").val(aData[10]);
			    $("#cIdUEConsolidado").val(aData[11]);
			    $("#cIdConsecutivoConsolidado").val(aData[12]);
			    $("#cIdLineaConsolidado").val(aData[0]);
				
				$("#cIdSolicitud").val(aData[1]);
				$("#cIdLineaSolicitud").val(aData[2]);
				$("#cEstadoLineaSolicitud").val('D');
				
				queryFormPost("contratoModificadoPartidaDelete", {async: false});
				queryFormPost("actualizaEstadoRequisicionContratoModificado", {async: false});
				queryFormPost("actualizaTotalesContratoModificado", {async: false});
				
				if(1==$("#isConvEjercicioAnt").val()){
					queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
				}else{
					if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
						queryFormPost("mContratoPluModificadoTotales", {async: false});
					}else{
						if(parseInt($("#lContratoAbierto").val())==1){
							queryFormPost("mContratoModificadoTotalesMax", {async: false});
						}else{
							queryFormPost("mContratoModificadoTotales", {async: false});
						}
					}
				}
				
				
				mostrarPartidasMods();
				mostrarPartidasUE();
				mostrarReqsMods('---', 0);
				mostrarPartidasModificadas();
				swal("Partida eliminada del modificatorio.",{icon:"info",button: "Cerrar"});
			}
			else {
				swal("No ha seleccionado una partida modificada.",{icon:"info",button: "Cerrar"});
			}
		}
		
		function agregarPartidaModificada(){
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			if(1==$("#isConvEjercicioAnt").val()){
					if(cIdContratoDef.indexOf("PLU")>=0){
						agregarParticasContPlu();
					}else{
						agregarPartidas();
					}
					
			}else if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
				agregarParticasContPlu();
			}else{
				agregarPartidas();
			}
		}
		function agregarParticasContPlu(){
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			var idCont = cIdContratoDef.split("-");
			$("#cIdTipoConsolidado").val('CS');
			if(idCont[1]=='CC'){
				$("#cIdTipoConsolidado").val('CC');	
			}
		    $("#cIdUEConsolidado").val(idCont[2]);
		    $("#cIdConsecutivoConsolidado").val(1);
		    var anSelected = fnGetSelected( ContratoPartidasMods );
			if (anSelected.length > 0) {
				var aDataPar = ContratoPartidasMods.fnGetData(anSelected[0]);
				$("#cIdLineaConsolidado").val(aDataPar[0]);
				$("#cDescripcion").val(aDataPar[2]);
				anSelected = fnGetSelected( ReqsMods );
				if (anSelected.length > 0) { //Modificacion con requisición, afecta cantidad y descripción
					var montoOri = parseFloat(aDataPar[10]).toFixed(2);
					var montoMod = parseFloat(aDataPar[11]).toFixed(2);
					var iva = parseFloat(aDataPar[12]).toFixed(2);
					
					if(parseInt($("#bEsXTotalPlu").val(),10)==1 ){//si es por el monto total plurianual
						montoOri=parseFloat($("#mContratoMontoNeto").val()).toFixed(2);
					}
					var montoDisp = parseFloat(parseFloat(montoOri) * 0.2) - parseFloat(montoMod);
					montoDisp=parseFloat(montoDisp).toFixed(2);
					if (montoDisp > 0) {
						var aDataReq = ReqsMods.fnGetData(anSelected[0]);
						//var montoReq = aDataReq[4].replace('$','').replace(',',''); 
						
						var montoReq=quitaFmt(aDataReq[7]);
						montoReq = $.trim(montoReq); 
						var precioUnitario=quitaFmt(aDataReq[5]);
						precioUnitario=$.trim(precioUnitario);
						$("#mPrecioUnitario").val(precioUnitario);
						$("#mMontoNeto").val(aDataReq[7]);
						if (parseFloat(montoReq) <= parseFloat(montoDisp)) {
							$("#mMonto").val(montoReq);
						}
						else {
							$("#mMonto").val(montoDisp);
							$("#mPrecioUnitario").val(montoDisp/((1+(iva*0.01))*(aDataReq[4])) );
							$("#mMontoNeto").val(montoDisp);
						}
						
						$("#nCantidad").val(aDataReq[4]);
						var monto = parseFloat($("#mMonto").val());
						
						//monto = monto * (1 + (parseInt(iva,10) / 100.0));
						
						$("#totalModAct").val(parseFloat($("#totalMod").val()) + parseFloat(monto));
						$("#totalNuevoAct").val(parseFloat($("#totalNuevo").val()) + parseFloat(monto));
						
						var totalModAct= $("#totalModAct").val();
						totalModAct=parseFloat(totalModAct).toFixed(2);
						var totalNuevoAct= $("#totalNuevoAct").val();
						totalNuevoAct=parseFloat(totalNuevoAct).toFixed(2);
						var precioU=$("#mPrecioUnitario").val();
						precioU=parseFloat(precioU).toFixed(2);
						$("#mPrecioUnitario").val(precioU);
						
						$("#cIdSolicitud").val(aDataReq[0]);
						$("#cIdLineaSolicitud").val(aDataReq[1]);
						$("#cEstadoLineaSolicitud").val('A');
						
						queryFormPost("actualizaEstadoRequisicionContratoModificado", {async: false});
						queryFormPost("mContratoModificadoPartidaCreate", {async: false});
						queryFormPost("actualizaTotalesContratoModificado", {async: false});
						if(1==$("#isConvEjercicioAnt").val()){
							queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
						}else{
							queryFormPost("mContratoPluModificadoTotales", {async: false});
							
						}
						mostrarPartidasMods();
						mostrarReqsMods('---', 0);
						mostrarPartidasModificadas();
					}
					else {
						swal("La partida seleccionada ha llegado al 20% modificado.",{icon:"info",button: "Cerrar"});
					}
				}else{
					swal("No ha seleccionado una partida de modificación.",{icon:"info",button: "Cerrar"});
				}
			}else{
				swal("No ha seleccionado una partida del original.",{icon:"info",button: "Cerrar"});
			}
		    
			
		}
		function agregarPartidas(){
			var anSelected = fnGetSelected( ContratoPartidasMods );
			if (anSelected.length > 0) {
				var aDataPar = ContratoPartidasMods.fnGetData(anSelected[0]);
								
				var idCons = aDataPar[9].split("-");
				$("#cIdTipoConsolidado").val(idCons[0]);
			    $("#cIdUEConsolidado").val(idCons[1]);
			    $("#cIdConsecutivoConsolidado").val(idCons[2]);
				$("#cIdLineaConsolidado").val(aDataPar[0]);
				$("#cDescripcion").val(aDataPar[2]);
				
				$("#estadoLinea").val('');
			    queryFormPost("mContratoModificadoReduccion", {async: false});
			    
			    if ($("#estadoLinea").val() == 'REDUCCION') { 
			    	swal("No es posible aumentar esta partida ya que se ha reducido anteriormente.",{icon:"info",button: "Cerrar"});
			    	return;
			    }
				
				anSelected = fnGetSelected( ReqsMods );
				if (anSelected.length > 0) { //Modificacion con requisición, afecta cantidad y descripción
					var montoOri = parseFloat(aDataPar[10]).toFixed(2);
					var montoMod = parseFloat(aDataPar[11]).toFixed(2);
					var iva = parseFloat(aDataPar[12]).toFixed(2);
					var montoDisp = parseFloat(parseFloat(montoOri) * 0.2) - parseFloat(montoMod);
					montoDisp=parseFloat(montoDisp).toFixed(2);
					
					if (montoDisp > 0) {
						var aDataReq = ReqsMods.fnGetData(anSelected[0]);
						//var montoReq = aDataReq[4].replace('$','').replace(',',''); 
						
						var montoReq=quitaFmt(aDataReq[7]);
						montoReq = $.trim(montoReq); 
						var precioUnitario=quitaFmt(aDataReq[5]);
						precioUnitario=$.trim(precioUnitario);
						$("#mPrecioUnitario").val(precioUnitario);
						$("#mMontoNeto").val(aDataReq[7]);
						if (parseFloat(montoReq) <= parseFloat(montoDisp)) {
							$("#mMonto").val(montoReq);
						}
						else {
							$("#mMonto").val(montoDisp);
							$("#mPrecioUnitario").val(montoDisp/((1+(iva*0.01))*(aDataReq[4])) );
							$("#mMontoNeto").val(montoDisp);
						}
						
						$("#nCantidad").val(aDataReq[4]);
						var monto = parseFloat($("#mMonto").val());
						
						//monto = monto * (1 + (parseInt(iva,10) / 100.0));
						
						$("#totalModAct").val(parseFloat($("#totalMod").val()) + parseFloat(monto));
						$("#totalNuevoAct").val(parseFloat($("#totalNuevo").val()) + parseFloat(monto));
						
						var totalModAct= $("#totalModAct").val();
						totalModAct=parseFloat(totalModAct).toFixed(2);
						var totalNuevoAct= $("#totalNuevoAct").val();
						totalNuevoAct=parseFloat(totalNuevoAct).toFixed(2);
						var precioU=$("#mPrecioUnitario").val();
						precioU=parseFloat(precioU).toFixed(2);
						$("#mPrecioUnitario").val(precioU);
						
						$("#cIdSolicitud").val(aDataReq[0]);
						$("#cIdLineaSolicitud").val(aDataReq[1]);
						$("#cEstadoLineaSolicitud").val('A');
						
						
						queryFormPost("actualizaEstadoRequisicionContratoModificado", {async: false});
						queryFormPost("mContratoModificadoPartidaCreate", {async: false});
						queryFormPost("actualizaTotalesContratoModificado", {async: false});
						if(1==$("#isConvEjercicioAnt").val()){
							queryFormPost("mContratoModificadoAnteriorTotales", {async: false});
						}else{
							if(parseInt($("#lContratoAbierto").val())==1){
								queryFormPost("mContratoModificadoTotalesMax", {async: false});
							}else{
								queryFormPost("mContratoModificadoTotales", {async: false});
							}
						}
						mostrarPartidasMods();
						mostrarReqsMods('---', 0);
						mostrarPartidasModificadas();
					}
					else {
						swal("La partida seleccionada ha llegado al 20% modificado.",{icon:"info",button: "Cerrar"});
					}
				}
				else { //Modificacion sin requisición, solo descripción
					swal("No ha seleccionado una partida de modificación.",{icon:"info",button: "Cerrar"});
				}
			}
			else {
				swal("No ha seleccionado una partida del original.",{icon:"info",button: "Cerrar"});
			}
		}
		function agregarPartidaModificadaBaja(){
			var anSelected = fnGetSelected( ContratoPartidasMods );
			if (anSelected.length > 0) {
				var monto = parseFloat($("#montoBaja").val());
				if (isNaN(monto)) {
					swal("Debe especificar el monto.",{icon:"info",button: "Cerrar"});
					return;
				}
				
				if (monto <= 0) {
					swal("El monto no es válido.",{icon:"info",button: "Cerrar"});
					return;
				}
				
				var aDataPar = ContratoPartidasMods.fnGetData(anSelected[0]);
				
				var montoOri = parseFloat(aDataPar[10]);
				var montoMod = parseFloat(aDataPar[11]);
				var montoDisp = parseFloat(montoOri * 0.1) + montoMod;
				if (montoDisp == 0) {
					swal("No es posible disminuir esta partida.",{icon:"info",button: "Cerrar"});
					return;
				}
				
				if (monto > montoDisp){
					swal("No es posible disminuir esta partida en la cantidad especificada.",{icon:"info",button: "Cerrar"});
					return;
				}
				
				var idCons = aDataPar[9].split("-");
				$("#cIdTipoConsolidado").val(idCons[0]);
			    $("#cIdUEConsolidado").val(idCons[1]);
			    $("#cIdConsecutivoConsolidado").val(idCons[2]);
				$("#cIdLineaConsolidado").val(aDataPar[0]);
				$("#cDescripcion").val(aDataPar[2]);
				
				$("#estadoLinea").val('');
			    queryFormPost("mContratoModificadoAmpliacion", {async: false});
			    if ($("#estadoLinea").val() == 'AMPLIACION') {
			    	swal("No es posible reducir esta línea ya que se ha ampliado anteriormente.",{icon:"info",button: "Cerrar"});
			    	return;
			    }
			    
				monto = monto * -1;
				$("#mMonto").val(monto);
				$("#totalModAct").val(parseFloat($("#totalMod").val()) + parseFloat(monto));
				$("#totalNuevoAct").val(parseFloat($("#totalNuevo").val()) + parseFloat(monto));
				
				var totalModAct= $("#totalModAct").val();
				totalModAct=totalModAct.toFixed(2);
				var totalNuevoAct= $("#totalNuevoAct").val();
				totalNuevoAct=totalNuevoAct.toFixed(2);
				
				$("#nCantidad").val(1);
				$("#cIdSolicitud").val(null);
				$("#cIdLineaSolicitud").val(null);
				
				queryFormPost("mContratoModificadoPartidaCreate", {async: false});
				queryFormPost("actualizaTotalesContratoModificado", {async: false});
				if(parseInt($("#lContratoAbierto").val())==1){
					queryFormPost("mContratoModificadoTotalesMax", {async: false});
				}else{
					queryFormPost("mContratoModificadoTotales", {async: false});
				}
			
				mostrarPartidasMods();
				mostrarReqsMods('---', 0);
				mostrarPartidasModificadas();
				
				$("#montoBaja").val('');
				swal("Partida de Baja agregada al modificatorio.",{icon:"info",button: "Cerrar"});
			}
			else {
				swal("No ha seleccionado una partida del original.",{icon:"info",button: "Cerrar"});
			}
		}
		
		function guardarDescripcionesModificadas(){
			var aTrs = $('#tblPartidasMods').dataTable().fnGetNodes();
			if(aTrs.length==0){
				swal("No hay datos en la tabla.",{icon:"info",button: "Cerrar"});
				return;
			}
			for ( var i = 0 ; i < aTrs.length; i++){ 	 	
				var nTr = $('#tblPartidasMods').dataTable().fnGetData(i);
				if(nTr[9]>20){
					swal("La Modificación no puede sobre pasar el 20% por línea.",{icon:"info",button: "Cerrar"});
					return;
				}
				$("#cIdTipoConsolidado").val(nTr[10]);
			    $("#cIdUEConsolidado").val(nTr[11]);
			    $("#cIdConsecutivoConsolidado").val(nTr[12]);
				$("#cIdLineaConsolidado").val(nTr[0]);
				
				$("#cDescripcion").val($("#parModDesc" + nTr[0]).val());
				
				queryFormPost("actualizaDescripcionPartidaContratoModificado", {async : false});
			}
			swal("Descripciones guardadas.",{icon:"info",button: "Cerrar"});
		}
		
		function borraModificatorio(){
			if ($("#nIdEstado").val() == '1') {
				swal({
					title: "",
					text: "¿Está seguro de eliminar el modificatorio?",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
						}else{
							queryFormPost("contratoModificadoDelete", {async : false});
							window.location = 'ContratoModificatorio.jsp?tab=1';
						}
				});
			}
			else {
				swal("No es posible eliminar el modificatorio.",{icon:"info",button: "Cerrar"});
			}
		}
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(/,/g, '');
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		function mostrarPartidasPresupCont(){
			var campos="'"+$("#cContratoDefinitivo").val()+"'";
			var query="fn_mPartidasContratos(" + campos + ")";
			
			$("#tblPartidasPresupCont").dataTable({
				sScrollX: "100%",
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth: true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay registros",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					
					{ sName: "cant" },
					{ sName: "cIdSubPartida" },
					{ sName: "cSubPartida" },
					{ sName: "cIdCapitulo" }
				]
			});	
		}
		function desactivaBackspace(event){
			if(window.event && window.event.keyCode == 8){
		     	window.event.keyCode = 505;
	    	}
		    if(window.event && window.event.keyCode == 505){
	    	 	return false;
	    	}
	    	return true;
		}
		function agregarPartidaPresup(){
			if($("#cIdSubPartida").val()==""){
				swal("Selecciona una partida.",{icon:"info",button: "Cerrar"});
				return;
			}
			//validar si ya está agregada la partida
			if(validaPartPresup($("#cIdSubPartida").val())){
				swal("Esta partida ya está agregada.",{icon:"info",button: "Cerrar"});
				return;
			}
			queryFormPost("mAgregaPartidaPresupContMod", {async : false, 
				callback: function(){
					$("#cIdSubPartida").val('');
					$("#partidaContMod").val('');
					partidasPresupAgregadas();
				}	
			});
			
		}
		function validaPartPresup(partida){
			var aTrs = $('#tblPartidasPresupContNuevas').dataTable().fnGetNodes();
			var resp=false;
			for ( var i = 0 ; i < aTrs.length; i++){ 	 	
				var nTr = $('#tblPartidasPresupContNuevas').dataTable().fnGetData(i);
				if(nTr[2]==partida){
					resp= true;
					break;
				}
			}
			return resp;
		}
		function eliminaPartPresup(id){
			$("#nIdPartidaPresupContMod").val(id);
			queryFormPost("mDeletePartidaPresupContMod", {async : false, 
				callback: function(){
					partidasPresupAgregadas();
				}	
			});
		}
		function partidasPresupAgregadas(){
			var campos="'"+$("#cContratoDefinitivo").val()+"',"+$("#cConsecutivoMod").val();
			var query="fn_mPartidasPresupContMod(" + campos + ")";
			
			$("#tblPartidasPresupContNuevas").dataTable({
				sScrollX: "100%",
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth: true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay registros",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdPartidaPresupContMod", bVisible: false},					
					{ sName: "countReg" },
					{ sName: "cIdSubPartida" },
					{ sName: "cSubPartida" },
					{ sName: "btnEliminar" }
				]
			});	
		}
		function actualizarDatos(){
			if(actualiza){
				queryFormPost("updatemDocumentacionHipervinculoConv", {async : false,
				callback : function() 
					{
						//Guarda en la Bitácora
						guardaBitacora("DocumetacionHipervinculosConvenio");
						initQuery();
					}
				});
			}else{
				queryFormPost("insertamDocumentacionContratoConv", {async : false,
				callback : function() 
					{
						//Guarda en la Bitácora
						guardaBitacora("DocumetacionHipervinculosConvenio");
						initQuery();
					}
				});
			}
		}
		function guardaBitacora(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cContratoDefinitivoConv").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function guardarUEModificada(){
			var arregloDatos=joinChain(ContratoPartidasUE);
			if(arregloDatos!=null){
				llamadaAjax(11, arregloDatos)
			}
		}
		function llamadaAjax(oper,arregloDatos){
			$.ajax({url: "../../servlet/ContratoModificadoServlet" , type:'post' , async: false
			,data:'operacion='+oper+'&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()+'&nConsecutivoMod='+$("#cConsecutivoMod").val()+'&arregloDatos='+arregloDatos
			, dataType: 'json', success: 
				function(j){
					var mensaje=j[0].MENSAJE;
					swal(mensaje,{icon:"info",button: "Cerrar"});
				}
			});
		}
		function joinChain(oTable){
			var arregloTmp=new Array();
			var arrayFila=new Object();
			var aTrs = oTable.dataTable().fnGetNodes();
			var nTr;
			var resp=false;
			for ( var i=0 ; i<aTrs.length; i++ ){
				nTr =  oTable.dataTable().fnGetData(aTrs[i]);
				if(nTr[4]==$("#cComboUE_"+nTr[0]).val()){
					swal("Favor de que la unidad ejecutora nueva sea diferente a la original en la linea "+nTr[0],{icon:"info",button: "Cerrar"});
					resp=true;
				}
				arrayFila=[nTr[7],nTr[8],nTr[4],nTr[9],nTr[10],nTr[0],nTr[2],$("#cComboUE_"+nTr[0]).val(),"|"];
				arregloTmp.push(arrayFila);
			}
			if(resp){
				arregloTmp=null;
			}
			return arregloTmp;
		}
		function addPartida(idLineaConsolidado){
			$.ajax({url: "../../servlet/ConvenioModificatorioServlet" , type:'post' , async: false
				,data:'operacion=0&pestana=1&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
				+'&cIdContrato='+$("#cContrato").val()
				+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
				+'&nTipoMod='+$("#tipoMod").val()
				+'&nIdEstado='+$("#nIdEstado").val()
				+'&nIdlineaConsolidado='+idLineaConsolidado
				, dataType: 'json', success: 
					function(j){
						var mensaje=j[0].MSG;
						mostrarPartidasMods();
						mostrarPartidasModificadas_ConvReduccion();
						swal(mensaje,{icon:"info",button: "Cerrar"});
					}
				});
		}
		function eliminaPartidaConvRed(idLineaCons,operacion){
		
			$.ajax({url: "../../servlet/ConvenioModificatorioServlet" , type:'post' , async: false
				,data:'operacion='+operacion+'&pestana=1&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
				+'&cIdContrato='+$("#cContrato").val()
				+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
				+'&nTipoMod='+$("#tipoMod").val()
				+'&nIdEstado='+$("#nIdEstado").val()
				+'&nIdlineaConsolidado='+idLineaCons
				+'&lContratoAbierto='+$("#lContratoAbierto").val()
				+'&isConvEjercicioAnt='+$("#isConvEjercicioAnt").val()
				+'&bEsXTotalPlu='+$("#bEsXTotalPlu").val()
				, dataType: 'json', success: 
					function(j){
						var mensaje=j[0].MSG;
						vaciarJsonAInputs(j[0].datosGuardados);
						mostrarPartidasMods();
						mostrarPartidasModificadas_ConvReduccion();
						swal(mensaje,{icon:"info",button: "Cerrar"});
					}
				});
		}
		function guardaPartidasConvRed(){
			var arregloDatos=joinChain_ConvRed(PartidasModificadasConvRed);
			if(arregloDatos!=null){
				$.ajax({url: "../../servlet/ConvenioModificatorioServlet" , type:'post' , async: false
					,data:'operacion=3&pestana=1&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
					+'&cIdContrato='+$("#cContrato").val()
					+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
					+'&nTipoMod='+$("#tipoMod").val()
					+'&nIdEstado='+$("#nIdEstado").val()
					+'&lContratoAbierto='+$("#lContratoAbierto").val()
					+'&isConvEjercicioAnt='+$("#isConvEjercicioAnt").val()
					+'&bEsXTotalPlu='+$("#bEsXTotalPlu").val()
					+'&arregloDatos='+arregloDatos
					
					, dataType: 'json', success: 
						function(j){
							var mensaje=j[0].MSG;
							vaciarJsonAInputs(j[0].datosGuardados);
							mostrarPartidasMods();
							mostrarPartidasModificadas_ConvReduccion();
							swal(mensaje,{icon:"info",button: "Cerrar"});
						}
				});
			}
		}
		function joinChain_ConvRed(oTable){
			var arregloTmp=new Array();
			var arrayFila=new Object();
			var aTrs = oTable.dataTable().fnGetNodes();
			var nTr;
			var montoReduccion=0;
			var pu=0;
			var resp=false;
			for ( var i=0 ; i<aTrs.length; i++ ){
				nTr =  oTable.dataTable().fnGetData(aTrs[i]);
				if(""==$("#mMontoNetoReduccion_"+nTr[0]).val()){
					swal("Favor de capturar un monto en la linea "+nTr[0],{icon:"info",button: "Cerrar"});
					resp=true;
					break;
				}
				pu=unFrmt2(nTr[6]);
				arrayFila=[nTr[0],nTr[16],nTr[2],nTr[5],pu,unFrmt2($("#mMontoNetoReduccion_"+nTr[0]).val()),nTr[15],"|"];
				arregloTmp.push(arrayFila);
			}
			if(resp){
				arregloTmp=null;
			}
			return arregloTmp;
		}
		function deleteAllPartidas(){
			swal({
				title: "",
				text: "¿Está seguro de eliminar todas las partidas?",
				icon: "info",
				buttons: {
					confirm : "Aceptar",
					cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						return;
					}else{
						eliminaPartidaConvRed(-1,4);
					}
			});
		}
		function agregarPartidasRed(){
			swal({
				title: "",
				text: "¿Está seguro de agregar todas las partidas?",
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
							,data:'operacion=1&pestana=1&cIdContratoDefinitivo='+$("#cContratoDefinitivo").val()
							+'&cIdContrato='+$("#cContrato").val()
							+'&nConsecutivoMod='+$("#cConsecutivoMod").val()
							+'&nTipoMod='+$("#tipoMod").val()
							+'&nIdEstado='+$("#nIdEstado").val()
							+'&lContratoAbierto='+$("#lContratoAbierto").val()
							+'&isConvEjercicioAnt='+$("#isConvEjercicioAnt").val()
							+'&bEsXTotalPlu='+$("#bEsXTotalPlu").val()
							, dataType: 'json', success: 
								function(j){
									var mensaje=j[0].MSG;
									vaciarJsonAInputs(j[0].datosGuardados);
									mostrarPartidasMods();
									mostrarPartidasModificadas_ConvReduccion();
									swal(mensaje,{icon:"info",button: "Cerrar"});
								}
						});
					}
			});
		}
	</script>
  </head>
  <body>
  	<form id="formCaratula">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Car&aacute;tula del Contrato Modificado</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceCancelar ui-button ui-corner-all float-right" 	id="imgEliminar" name="imgEliminar" 	value="Eliminar"	onclick="borraModificatorio();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoModificatorio.jsp?tab=1';" />
						</div>
						
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblAutorizaSICOP" id="lblAutorizaSICOP"  readonly style="color:blue"/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProcedimiento" id="lblProcedimiento" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblContrato" id="lblContrato" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoModificado" id="lblTotalContratoModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalPorcentajeMod" id="lblTotalPorcentajeMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalAnterior" id="lblTotalAnterior" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalModificado" id="lblTotalModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblPorcentajeMod" id="lblPorcentajeMod" readonly />
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Fechas del Contrato Original</legend>
			<div class="form-group">
				<div class="row" id="trffor">
					<div class="input-group">
						<div class="col-2">
							<label for="fechaFormalizacion">Fecha de Formalizaci&oacute;n: </label>
						</div>
						<div class="col-2">
							<input type="text" class="form-control" readonly name="fechaFormalizacion" id="fechaFormalizacion"  />
						</div>
					</div>
				</div>
				<div class="row" id="trfini" style="display: none;">
					<div class="input-group">
						<div class="col-2">
							<label for="fechaInicio">Fecha de Inicio: </label>
						</div>
						<div class="col-2">
							<input type="text" class="form-control" readonly name="fechaInicio" id="fechaInicio"  />
						</div>
					</div>
				</div>
				<div class="row" id="trffin" style="display: none;">
					<div class="input-group">
						<div class="col-2">
							<label for="fechfechaFinaInicio">Fecha de Fin: </label>
						</div>
						<div class="col-2">
							<input type="text" class="form-control" readonly name="fechaFin" id="fechaFin"  />
						</div>
					</div>
				</div>
				<div class="row" id="trfentrega" style="display: none;">
					<div class="input-group">
						<div class="col-2">
							<label for="fechaEntrega">Fecha de Entrega: </label>
						</div>
						<div class="col-2">
							<input type="text" class="form-control" readonly name="fechaEntrega" id="fechaEntrega"  />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Hipervinculos </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cHipDocConv">Documento del Convenio: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="URL Documento del Convenio" 
							aria-label=" URL Documento del Convenio" aria-describedby="basic-addon1"  name="cHipDocConv" id="cHipDocConv"  />
						</div>
						<div class="col-2">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="guardarhiper" name="guardarhiper" 	value="Guardar"	onclick="actualizarDatos();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="fielsetPartidasMods" style="display: none;">
			<legend class="w-auto px-2"> PARTIDAS ORIGINALES Y MODIFICACIONES ANTERIORES </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblContratoPartidasMods" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%;">
								<thead >
									<tr>
										<th width="10%" >LINEA CONS</th>
										<th width="10%" >CUCOP</th>
										<th width="20%" >DESCRIPCION</th>
										<th width="10%" >DESCRIPCION ADICIONAL</th>
										<th width="10%" >CANTIDAD</th>
										<th width="10%" >PRECIO UNITARIO</th>
										<th width="10%" >M. BRUTO</th>
										<th width="10%" >M. NETO</th>
										<th width="10%" >% MOD</th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" id="trpartidas">
				<div class="row">
					<div class="input-group" id="bajaDiv">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="agregarPartidaContMod" name="agregarPartidaContMod" 	value="AGREGAR TODO"	onclick="agregarPartidasRed();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="trrequiDispoMod">
			<legend class="w-auto px-2"> REQUISICIONES DISPONIBLES PARA MODIFICACION </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblReqsMods" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th width="10%" >SOLICITUD</th>
										<th width="10%" >LINEA</th>
										<th width="20%" >DESCRIPCION</th>
										<th width="10%" >DESCRIPCION ADICIONAL</th>
										<th width="10%" >CANTIDAD</th>
										<th width="10%" >PRECIO UNITARIO</th>
										<th width="15%" >MONTO BRUTO</th>
										<th width="15%" >MONTO NETO</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-inline">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-start" 	id="agrBtnContratoMod" name="agrBtnContratoMod" 	value="AGREGAR"	onclick="agregarPartidaModificada();" />
						</div>
						<div class="col-inline">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="devBtnContratoMod" name="devBtnContratoMod" 	value="DEVOLVER"	onclick="devolverPartidaModificada();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="trPartidasMod">
			<legend class="w-auto px-2"> PARTIDAS MODIFICADAS </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblPartidasMods" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th width="5%" >LINEA C</th>
										<th width="5%" >SOLICITUD</th>
										<th width="5%" >LINEA S</th>
										<th width="10%" >CUCOP</th>
										<th width="25%" >DESCRIPCION</th>
										<th width="7%" >CANTIDAD</th>
										<th width="8%" >PRECIO U</th>
										<th width="10%" >MONTO BRUTO</th>
										<th width="10%" >MONTO NETO</th>
										<th width="10%" >% MOD</th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="grdBtnContratoMod" name="grdBtnContratoMod" 	value="GUARDAR"	onclick="guardarDescripcionesModificadas();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="trPartidasMod_ConvReduccion">
			<legend class="w-auto px-2"> PARTIDAS MODIFICADAS CONVENIO DE REDUCCI&Oacute;N; </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblPartidasMod_ConvReduccion" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th width="5%" >LINEA C</th>
										<th width="5%" >MONTO CON IVA<br/>ORIGINAL</th>
										<th width="5%" >IVA</th>
										<th width="10%" >CUCOP</th>
										<th width="25%" >DESCRIPCION</th>
										<th width="7%" >CANTIDAD</th>
										<th width="8%" >PRECIO U</th>
										<th width="10%" >MONTO BRUTO</th>
										<th width="10%" >MONTO CON IVA<br/> A REDUCIR</th>
										<th width="10%" >% MOD</th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-inline">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-start" 	id="grdBtnContratoModDeleteAll" name="grdBtnContratoModDeleteAll" 	
							value="Elimina Todo"	onclick="deleteAllPartidas();" />
						</div>
						<div class="col-inline">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="grdBtnContratoModRead" name="grdBtnContratoModRead" 	
							value="GUARDAR"	onclick="guardaPartidasConvRed();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="trPartidasPresupCont">
			<legend class="w-auto px-2"> Partidas del Contrato Original </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblPartidasPresupCont" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th width="5%" >Num</th>
										<th width="10%" >Partida<br/> Presupuestal</th>
										<th >Descripción Partida</th>
										<th width="5%" >Capitulo</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="trPartidasPresupContNuevas">
			<legend class="w-auto px-2"> Agregar Nuevas Partidas </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-6">
							<input type="hidden" id="cIdSubPartida" name="cIdSubPartida" value="" />
							<input type="text" class="AyudaSyC obligatorio desahabilitado"  name="partidaContMod" id=partidaContMod readonly onkeydown="return(desactivaBackspace(event))" style="width: 350px;"/>
						</div>
						<div class="col-2">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="agregaPartPresupContMod" name="agregaPartPresupContMod" 	
							value="Agregar"	onclick="agregarPartidaPresup();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblPartidasPresupContNuevas" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th ></th>
										<th width="5%">Num</th>
										<th width="10%">Partida<br /> Presupuestal</th>
										<th >Descripción Partida</th>
										<th width="5%"></th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3" id="trpartidasUE">
			<legend class="w-auto px-2"> Cambiar de Unidad Ejecutora las partidas de contrato </legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblContratoPartidasUE" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th >LINEA</th>
										<th >CUCOP</th>
										<th >DESCRIPCION</th>
										<th >DESCRIPCION ADICIONAL</th>
										<th >UNIDAD EJECUTORA ORIGINAL</th>
										<th >UNIDAD EJECUTORA NUEVA</th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
										<th></th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="grdBtnContratoModUE" name="grdBtnContratoModUE" 	value="GUARDAR"	onclick="guardarUEModificada();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		
	    <!-- Hidden's -->
	    <!-- Sesion  -->
	   
	    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
	    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
	    <input type="hidden" name="tipoMod" id="tipoMod" />
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >
	    
	    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
 			<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
 			<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
	    
	    <input type="hidden" name="totalAnterior" id="totalAnterior" />
	    <input type="hidden" name="totalMod" id="totalMod" />
	    <input type="hidden" name="totalNuevo" id="totalNuevo" />
	    
	    <!-- Resultado de consultas -->
	    <input type="hidden" name="nIdEstado" id="nIdEstado" />
	    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />	    
	    <!--  Hidden valores auxiliares -->
	    <input type="hidden" name="totalModAct" id="totalModAct" />
	    <input type="hidden" name="totalNuevoAct" id="totalNuevoAct" />
	    
	    <input type="hidden" name="cIdTipoConsolidado" id="cIdTipoConsolidado" />
	    <input type="hidden" name="cIdUEConsolidado" id="cIdUEConsolidado" />
	    <input type="hidden" name="cIdConsecutivoConsolidado" id="cIdConsecutivoConsolidado" />
	    <input type="hidden" name="cIdLineaConsolidado" id="cIdLineaConsolidado" />
	    
	    <input type="hidden" name="cIdSolicitud" id="cIdSolicitud" />
	    <input type="hidden" name="cIdLineaSolicitud" id="cIdLineaSolicitud" />
	    <input type="hidden" name="cEstadoLineaSolicitud" id="cEstadoLineaSolicitud" />
	    
	    <input type="hidden" name="cDescripcion" id="cDescripcion" />
	    <input type="hidden" name="nCantidad" id="nCantidad" />
	    <input type="hidden" name="mMonto" id="mMonto" />
	    
	    <input type="hidden" name="estadoLinea" id="estadoLinea" />
	    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
	    <input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
	    <input type="hidden" name="mMontoNeto" id="mMontoNeto" value="0"/>
	    <input type="hidden" name="mPrecioUnitario" id="mPrecioUnitario" value="0"/>
		<input type="hidden" name="lContratoAbierto" id="lContratoAbierto" value="0" />
		<input type="hidden" name="bEsXTotalPlu" id="bEsXTotalPlu" value="0" />
		<input type="hidden" name="mMontoTotalPlurianual" id="mMontoTotalPlurianual" value="0" />
		<input type="hidden" name="nIdPartidaPresupContMod" id="nIdPartidaPresupContMod" value="0" />
		<input type="hidden" name="isConvEjercicioAnt" id="isConvEjercicioAnt" value="0" />
		
		<input type="hidden" name="mContratoMontoNeto" id="mContratoMontoNeto" >   	
		<input type="hidden" name="mContratoMN" id="mContratoMN" >
		<input type="hidden" name="existeRegistro" id="existeRegistro" value="0"/>
		<input type="hidden" name="cContratoDefinitivoConv" id="cContratoDefinitivoConv"/>
		<input name="cIdDocumento" id="cIdDocumento" type="hidden">
		<input type="hidden" id="cAccion" name="cAccion" value="1"/>
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%= usuarioTab.getLogin() %>"/> 
		<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%= cCentroContable %>"/>  	    
	    
	    
	</form>
  </body>
</html>
