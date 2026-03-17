<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();
	String role="";
	Map rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String cIdPedido = "";
	String cIdPedidoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoPedido= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo) != null) {
		cIdPedidoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo);
		cIdPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioEjercicio);
		
		cIdTipoPedido = cIdPedido.split("-")[0];
		cIdUnidadEjecutora = cIdPedido.split("-")[1];
		nIdConsecutivo = cIdPedido.split("-")[2];		
	}else 
		response.sendRedirect("PedidoModificatorio.jsp?tab=1");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Car&aacute;tula Pedido Modificado</title>
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
	<script type="text/javascript" charset="utf-8">	
		$(document).ready(function() {
			<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map botones=nb.getBotones(role,"PedidoModificatorio","caratulaPedidoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
				
			%>
			
			setFieldsInit();
			mostrarPartidasMods();
			mostrarPartidasModificadas();
			
			$('#tblPedidoPartidasMods tr').live('dblclick', function() {
				$(PedidoPartidasMods.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
					
				if ($("#tipoMod").val() == 0 && parseInt($("#nIdEstado").val(),10) == 1) {
					var anSelected = fnGetSelected( PedidoPartidasMods );
					var aData = PedidoPartidasMods.fnGetData(anSelected[0]);
					
					var cabm = $.trim(aData[1]);
					
					var precio =quitaFmt(aData[4]);
					
					//var precio = aData[4].replace('$', '').replace(',', '');
					
					mostrarReqsMods(cabm, precio);
				}
			});
			
			$('#tblReqsMods tr').live('dblclick', function() {
				$(ReqsMods.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
			});
			
			$('#tblPartidasMods tr').live('dblclick', function() {
				$(PartidasModificadas.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
			});
			
			$("#tblReqsMods").dataTable({
				sScrollY: "200px",
				sScrollX: "100%",
				sScrollXInner: "100%",
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
			
		});
		
		function setFieldsInit(){
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly = true;
			document.getElementById("lblProcedimiento").style.readonly = true;
			document.getElementById("lblDefinitivo").style.readonly = true;
			document.getElementById("lblPedido").style.readonly = true;
			document.getElementById("lblProveedor").style.readonly = true;
			document.getElementById("lblEstadoMod").style.readonly = true;
			document.getElementById("lblTotalAnterior").style.readonly = true;
			document.getElementById("lblTotalModificado").style.readonly = true;
			document.getElementById("lblTotal").style.readonly = true;
						
			//Carga de cabecera
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("mPedidoModificadoTotales", {async: false});
			if ($("#tipoMod").val() == 0){
				$("#bajaDiv").css("visibility", "hidden");
				document.getElementById("reduccionPedidoMod").disabled = true;
			}
			else {
				//$("#ampliTr").css("visibility", "hidden");
				document.getElementById("tblReqsMods").disabled = true;
				document.getElementById("agrBtnPedidoMod").disabled = true;
				document.getElementById("presupuestoPedidoMod").disabled = true;
				document.getElementById("precompromisoPedidoMod").disabled = true;
				document.getElementById("pagosPedidoMod").disabled = true;
			}
			
			if(parseInt($("#nIdEstado").val(),10) == 1){
				document.getElementById("precompromisoPedidoMod").disabled = true;
			}
			
			if(parseInt($("#nIdEstado").val(),10) > 1){
				document.getElementById("tblReqsMods").disabled = true;
				document.getElementById("agrBtnPedidoMod").disabled = true;
				document.getElementById("bajaDiv").disabled = true;
				document.getElementById("devBtnPedidoMod").disabled = true;
				document.getElementById("grdBtnPedidoMod").disabled = true;
			}


			queryFormPost("mPedidoModicadoChecaRolUsuario", {async: false});
			if ($("#usuarioLoginRole").val() != 'ADMIN_RECMAT') { 
				$("#usuarioCreacionOriginal").val('');
				queryFormPost("mPedidoModicadoUsuarioCreacionOriginalRead",{async: false });
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					document.getElementById("tblReqsMods").disabled = true;
					document.getElementById("agrBtnPedidoMod").disabled = true;
					document.getElementById("bajaDiv").disabled = true;
					document.getElementById("devBtnPedidoMod").disabled = true;
					document.getElementById("grdBtnPedidoMod").disabled = true;
				} 
			}
		}
		
		function mostrarPartidasMods(){
			var campos = "'" + $("#cEjercicio").val() + "','" + $("#cPedido").val() + "','" + $("#cPedidoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
			
			PedidoPartidasMods = $("#tblPedidoPartidasMods").dataTable({
				sScrollY: "200px",
				sScrollX: "100%",
				sScrollXInner: "200%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mPedidoModificadoPartidas(" + campos + ")",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdLineaConsolidado" },
					{ sName: "cIdCABM" },
					{ sName: "Descripcion" },
					{ sName: "Cantidad" },
					{ sName: "PrecioUnitario" },
					{ sName: "MontoBruto" },
					{ sName: "MontoNeto" },
					{ sName: "cIdConsolidado", bVisible: false },
					{ sName: "CantidadOriginal", bVisible: false },
					{ sName: "CantidadMod", bVisible: false },
					{ sName: "PrecioUnitarioNeto", bVisible: false }
				]
			});	
		}
		
		function mostrarPartidasModificadas(){
			var campos = "'" + $("#cPedidoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
			
			PartidasModificadas = $("#tblPartidasMods").dataTable({
				sScrollY: "200px",
				sScrollX: "100%",
				sScrollXInner: "200%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mPedidoModificadoPartidasModificadas(" + campos + ")",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					
					{ sName: "nIdLineaConsolidado" },
					{ sName: "cIdSolicitud" },
					{ sName: "cIdLineaSolicitud" },
					{ sName: "cIdCABM" },
					{ sName: "cDescripcion" },
					{ sName: "nCantidad" },
					{ sName: "PrecioUnitario" },
					{ sName: "MontoBruto" },
					{ sName: "MontoNeto" },
					{ sName: "cIdTipoConsolidado", bVisible: false },
					{ sName: "cIdUnidadEjecutora", bVisible: false },
					{ sName: "cIdConsecutivoConsolidado", bVisible: false }
				]
			});	
		}
		
		function mostrarReqsMods(idcabm, precio){
			var campos = "'" + idcabm + "'," + precio;
			
			ReqsMods = $("#tblReqsMods").dataTable({
				sScrollY: "200px",
				sScrollX: "100%",
				sScrollXInner: "200%",
				bScrollCollapse: true,
				bDestroy: true,
				bRetrive : true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mPedidoModificadoPartidaRequisiciones (" + campos + ")",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdSolicitud" },
					{ sName: "nIdLineaSolicitud" },
					{ sName: "cDescripcion" },
					{ sName: "nCantidad" },
					{ sName: "mPrecioUnitario" }
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
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		
		function devolverPartidaModificada(){
			var anSelected = fnGetSelected( PartidasModificadas );
			if (anSelected.length > 0) { 
				var aData = PartidasModificadas.fnGetData(anSelected[0]);
				
				var monto =quitaFmt(aData[8]);
				
				//var monto = aData[8];
				//monto = monto.replace('$', '').replace(',', '');
				monto = $.trim(monto);
				
				$("#totalModAct").val(parseFloat($("#totalMod").val()) - parseFloat(monto));
				$("#totalNuevoAct").val(parseFloat($("#totalNuevo").val()) - parseFloat(monto));
				
				$("#cIdTipoConsolidado").val(aData[9]);
			    $("#cIdUEConsolidado").val(aData[10]);
			    $("#cIdConsecutivoConsolidado").val(aData[11]);
			    $("#cIdLineaConsolidado").val(aData[0]);
				
				$("#cIdSolicitud").val(aData[1]);
				$("#cIdLineaSolicitud").val(aData[2]);
				$("#cEstadoLineaSolicitud").val('D');
				
				queryFormPost("pedidoModificadoPartidaDelete", {async: false});
				queryFormPost("actualizaEstadoRequisicionPedidoModificado", {async: false});		
				queryFormPost("actualizaTotalesPedidoModificado", {async: false});
				queryFormPost("mPedidoModificadoTotales", {async: false});
				
				
				mostrarPartidasMods();
				mostrarReqsMods('---', 0);
				mostrarPartidasModificadas();
				
				alert("Partida eliminada del modificatorio");
			}
			else {
				alert("No ha seleccionado una partida modificada");
			}
		}
		
		function agregarPartidaModificada(){
			var anSelected = fnGetSelected( PedidoPartidasMods );
			if (anSelected.length > 0) {
				var aDataPar = PedidoPartidasMods.fnGetData(anSelected[0]);
				
				var idCons = aDataPar[7].split("-");
				$("#cIdTipoConsolidado").val(idCons[0]);
			    $("#cIdUEConsolidado").val(idCons[1]);
			    $("#cIdConsecutivoConsolidado").val(idCons[2]);
				$("#cIdLineaConsolidado").val(aDataPar[0]);
				$("#cDescripcion").val(aDataPar[2]);
				
				$("#estadoLinea").val('');
			    queryFormPost("mPedidoModificadoReduccion", {async: false});
			    if ($("#estadoLinea").val() == 'REDUCCION') {
			    	alert('No es posible aumentar esta partida ya que se ha reducido anteriormente');
			    	return;
			    }
				
				anSelected = fnGetSelected( ReqsMods );
				if (anSelected.length > 0) { //Modificacion con requisición, afecta cantidad y descripción
					var cantOri = aDataPar[8];
					var cantMod = aDataPar[9];
					var cantDisp = parseInt((parseInt(cantOri,10) * 0.2),10) - parseInt(cantMod,10);
					if (cantDisp > 0) {
						var aDataReq = ReqsMods.fnGetData(anSelected[0]);
												
						var precioReq = aDataReq[4];
						var cantReq = aDataReq[3]; 
						
						if (parseInt(cantReq,10) <= cantDisp) {
							$("#nCantidad").val(cantReq);
						}
						else {
							$("#nCantidad").val(cantDisp);
						}
						
						var cantidad = $("#nCantidad").val();
						
						var precio = quitaFmt(aDataPar[10]);
						
						//var precio = aDataPar[10];
						//precio = precio.replace('$', '').replace(',', '');
						
						
						precio = $.trim(precio);
						var monto = parseInt(cantidad,10) * parseFloat(precio);
						
						$("#totalModAct").val(parseFloat($("#totalMod").val()) + parseFloat(monto));
						$("#totalNuevoAct").val(parseFloat($("#totalNuevo").val()) + parseFloat(monto));
						
						$("#cIdSolicitud").val(aDataReq[0]);
						$("#cIdLineaSolicitud").val(aDataReq[1]);
						$("#cEstadoLineaSolicitud").val('A');
						
						queryFormPost("actualizaEstadoRequisicionPedidoModificado", {async: false});
						queryFormPost("mPedidoModificadoPartidaCreate", {async: false});
						queryFormPost("actualizaTotalesPedidoModificado", {async: false});
						queryFormPost("mPedidoModificadoTotales", {async: false});
						
						mostrarPartidasMods();
						mostrarReqsMods('---', 0);
						mostrarPartidasModificadas();
					}
					else {
						alert('La partida seleccionada ha llegado al 20% modificado');
					}
				}
				else { //Modificacion sin requisición, solo descripción
					$("#nCantidad").val(0);
					$("#cIdSolicitud").val(null);
					$("#cIdLineaSolicitud").val(null);
					
					queryFormPost("mPedidoModificadoPartidaCreate", {async: false});
				
					mostrarPartidasMods();
					mostrarReqsMods('---', 0);
					mostrarPartidasModificadas();
					
					alert("Partida agregada al modificatorio");
				}
			}
			else {
				alert("No ha seleccionado una partida del original");
			}
		}
		
		function agregarPartidaModificadaBaja(){
			var anSelected = fnGetSelected( PedidoPartidasMods );
			if (anSelected.length > 0) {
				var cantidad = parseInt($("#cantBaja").val(),10);
				if (isNaN(cantidad)) {
					alert("Debe especificar la cantidad");
					return;
				}
				
				if (cantidad <= 0) {
					alert("La cantidad no es válida");
					return;
				}
				
				var aDataPar = PedidoPartidasMods.fnGetData(anSelected[0]);
				
				var cantOri = parseInt(aDataPar[8],10);
				var cantMod = parseInt(aDataPar[9],10);
				var cantDisp = parseInt((cantOri * 0.1),10) + cantMod;
				if (cantDisp == 0) {
					alert("No es posible disminuir esta partida");
					return;
				}
				
				if (cantidad > cantDisp){
					alert("No es posible disminuir esta partida en la cantidad especificada");
					return;
				}
				
				var idCons = aDataPar[7].split("-");
				$("#cIdTipoConsolidado").val(idCons[0]);
			    $("#cIdUEConsolidado").val(idCons[1]);
			    $("#cIdConsecutivoConsolidado").val(idCons[2]);
				$("#cIdLineaConsolidado").val(aDataPar[0]);
				$("#cDescripcion").val(aDataPar[2]);
				
				$("#estadoLinea").val('');
			    queryFormPost("mPedidoModificadoAmpliacion", {async: false});
			    if ($("#estadoLinea").val() == 'AMPLIACION') {
			    	alert('No es posible reducir esta linea ya que se ha ampliado anteriormente');
			    	return;
			    }
				
				cantidad = cantidad * -1;
				//var precio = aDataPar[10];
				//precio = precio.replace('$', '').replace(',', '');
				var precio = quitaFmt(aDataPar[10]);
				
				precio = $.trim(precio);
				var monto = parseInt(cantidad,10) * parseFloat(precio);
				$("#totalModAct").val(parseFloat($("#totalMod").val()) + parseFloat(monto));
				$("#totalNuevoAct").val(parseFloat($("#totalNuevo").val()) + parseFloat(monto));
				
				$("#nCantidad").val(cantidad);
				$("#cIdSolicitud").val(null);
				$("#cIdLineaSolicitud").val(null);
				
				queryFormPost("mPedidoModificadoPartidaCreate", {async: false});
				queryFormPost("actualizaTotalesPedidoModificado", {async: false});
				queryFormPost("mPedidoModificadoTotales", {async: false});
			
				mostrarPartidasMods();
				mostrarReqsMods('---', 0);
				mostrarPartidasModificadas();
				
				$("#cantBaja").val('');
				
				alert("Partida de Baja agregada al modificatorio");
			}
			else {
				alert("No ha seleccionado una partida del original");
			}
		}
		
		function guardarDescripcionesModificadas(){
			var aTrs = $('#tblPartidasMods').dataTable().fnGetNodes();
			for ( var i = 0 ; i < aTrs.length; i++){ 	 	
				var nTr = $('#tblPartidasMods').dataTable().fnGetData(i);
				
				$("#cIdTipoConsolidado").val(nTr[9]);
			    $("#cIdUEConsolidado").val(nTr[10]);
			    $("#cIdConsecutivoConsolidado").val(nTr[11]);
				$("#cIdLineaConsolidado").val(nTr[0]);
				
				$("#cDescripcion").val($("#parModDesc" + nTr[0]).val());
				
				queryFormPost("actualizaDescripcionPartidaPedidoModificado", {async : false});
			}
			
			alert("Descripciones guardadas.");
		}
		
		function exportPDF(){
			var cPedidoDefinitivo = $("#cPedidoDefinitivo").val();
			var nConsecutivoMod = $("#cConsecutivoMod").val();
			var servletPath = "../../servlet/SeguridadCatalogosMateriales?" + "directorio=temporal" + "&rn=PedidoModificado.jasper" +"&formato=dspdf&cPedidoDefinitivo=" + cPedidoDefinitivo + "&nConsecutivoMod=" + nConsecutivoMod;
			window.open(servletPath, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
		
		function borraModificatorio(){
			if ($.trim($("#lblEstadoMod").val()) == 'CAPTURADO') {
				if (confirm("¿Está seguro de eliminar el modificatorio?")) {
					queryFormPost("pedidoModificadoDelete", {async : false});
					window.location = 'PedidoModificatorio.jsp?tab=1';
				}
			}
			else {
				alert("No es posible eliminar el modificatorio");
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
		
		
		
	</script>
  </head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form > 
		<div id="container" class="container" >
			<table width="94%" align="left">
				<tr>
					<td width="750px" >
						<fieldset>&nbsp; 
							<legend>Car&aacute;tula del Pedido Modificado</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
							    			<img id="imgExportar" src="../../imagenes/icono_PDF.jpg" style="cursor: pointer" onclick="exportPDF();" />&nbsp;Exportar
							    			<img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="borraModificatorio();" />&nbsp;Eliminar
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="window.location = 'PedidoModificatorio.jsp?tab=1';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblProcedimiento" name="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" id="lblDefinitivo" name="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblPedido" id="lblPedido" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 300px" name="lblEstadoMod" id="lblEstadoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTipoMod" id="lblTipoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalAnterior" id="lblTotalAnterior" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalModificado" id="lblTotalModificado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>				    	
							    </table>
						</fieldset>
					</td>
				</tr>
			
				<tr>
					<td style="width: 740px;" >
						PARTIDAS ORIGINAL Y MODIFICACIONES ANTERIORES
						<table align="left" id="tblPedidoPartidasMods" width="740px" class="display">
							<thead>
								<tr>
									<th width="10%" >LINEA CONS</th>
									<th width="10%" >CUCOP</th>
									<th width="40%" >DESCRIPCION</th>
									<th width="10%" >CANTIDAD</th>
									<th width="10%" >PRECIO UNITARIO</th>
									<th width="10%" >M. BRUTO</th>
									<th width="10%" >M. NETO</th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
								</tr>
							</thead> 
						</table>
						<div id="bajaDiv" >
							<br/>
							<button id="agregarPartidaModificadaPed" onclick="agregarPartidaModificadaBaja();" >AGREGAR BAJA</button> &nbsp&nbsp&nbsp Cantidad: <input type="text" id="cantBaja" name="cantBaja" style="width: 50px;" /> 
							<br/>
							<br/>
						</div>
					</td>
				</tr>
				<tr id="ampliTr" >
					<td style="width: 740px;" >
						REQUISICIONES DISPONIBLES PARA MODIFICACION
						<table align="left" id="tblReqsMods" width="740px" class="display">
							<thead>
								<tr>
									<th width="15%" >SOLICITUD</th>
									<th width="15%" >LINEA</th>
									<th width="40%" >DESCRIPCION</th>
									<th width="15%" >CANTIDAD</th>
									<th width="15%" >PRECIO UNITARIO</th>
								</tr>
							</thead> 
						</table>
						<br/>
						<button id="agrBtnPedidoMod"  onclick="agregarPartidaModificada();" >AGREGAR</button> <button id="devBtnPedidoMod" onclick="devolverPartidaModificada();" >DEVOLVER</button>
						<br/>
						<br/>
					</td>
				</tr>
				<tr>
					<td style="width: 740px;" >
						PARTIDAS MODIFICADAS
						<table align="left" id="tblPartidasMods" width="740px" class="display">
							<thead>
								<tr>
									<th width="5%" >LINEA C</th>
									<th width="10%" >SOLICITUD</th>
									<th width="5%" >LINEA S</th>
									<th width="10%" >CUCOP</th>
									<th width="30%" >DESCRIPCION</th>
									<th width="7%" >CANTIDAD</th>
									<th width="8%" >PRECIO U</th>
									<th width="10%" >MONTO BRUTO</th>
									<th width="10%" >MONTO NETO</th>
									<th></th>
									<th></th>
									<th></th>
								</tr>
							</thead> 
						</table>
						<br/>
						<div style="text-align: center;" >
							<button id="grdBtnPedidoMod" onclick="guardarDescripcionesModificadas();" >GUARDAR</button>
						</div>
					</td>
				</tr>
			</table>
		    <!-- Hidden's -->
		    <!-- Sesion  -->
		    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		    <input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="cPedido" id="cPedido" value="<%=cIdPedido%>" />
		    <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" value="<%=cIdPedidoDefinitivo%>" />
		    <input type="hidden" name="pedidoDefinitivo" id="pedidoDefinitivo" value="<%=cIdPedidoDefinitivo%>" />
		    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
		    <input type="hidden" name="tipoMod" id="tipoMod" />
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >
		    
		    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  			<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=usuarioTab.getLogin()%>" />
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
		    
		    <input type="hidden" name="estadoLinea" id="estadoLinea" />
		    
	    </div>
	</form>
  </body>
</html>
