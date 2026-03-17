<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*"%>
<%
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Calendar c2= Calendar.getInstance();
	c2.add(Calendar.DATE, 20);
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String vigencia = sdf.format(c2.getTime());
	int mesActual = c1.getTime().getMonth() + 1;//Porque empieza en 0: Enero
	String cUR = "";
	String cRamo = "";
	boolean bAplicadoCont = false;
	Usuario usuarioTab = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String name_user = usuarioTab.getLogin();
	String idRol = "0";
	Map rol = usuarioTab.getRoles();
	//if (usuarioTab == null) {
	//	response.sendRedirect("../../index.jsp");
	//	return;
	//}		
	String cEjercicio = "";
	String cIdTipoPedido = "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null) {
		cEjercicio = (String) session
				.getAttribute(GestionInterface.ATT_PedidoEjercicio);
		cIdTipoPedido = (String) session
				.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
		cIdUnidadEjecutora = (String) session
				.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
		nIdConsecutivo = (String) session
				.getAttribute(GestionInterface.ATT_PedidoConsecutivo);
	}
	if (usuarioTab.getPropiedades() != null
			&& usuarioTab.getPropiedades().containsKey(
					"CCENTROCONTABLE"))
		cCentroContable = usuarioTab.getPropiedad("CCENTROCONTABLE")
				.getValor();
	cUR = usuarioTab.getU_UR();
	cRamo = usuarioTab.getU_Ramo();
	
	 
	/*else 
		response.sendRedirect("Pedidos.jsp?tab=0");*/
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Ampliaciones</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!-- <meta http-equiv="refresh" content="5;url=../Generador/SAICYS/Pedidos.jsp">-->

		<style type="text/css" title="currentStyle">
@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";

@import "../css/demo_table_jui.css";

@import "../css/demo_page.css";
</style>
		<style>
//
estilos del dialogo
			div#dialog-form fieldset {
	padding: 0;
	border: 0;
	margin-top: 25px;
}

div#users-contain {
	width: 350px;
	margin: 20px 0;
}

div#users-contain table {
	margin: 1em 0;
	border-collapse: collapse;
	width: 100%;
}

div#users-contain table td,div#users-contain table th {
	border: 1px solid #eee;
	padding: .6em 10px;
	text-align: left;
}

.ui-dialog .ui-state-error {
	padding: .3em;
}

.validateTips {
	border: 1px solid transparent;
	padding: 0.3em;
}
</style>
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTablaAmpliaciones;
		var oTablaAmpliacionesDetalle;
		var vfolio;
		var vcaNoCompromiso;
		$(document).ready(function() {	
				<%String roles = "";
			//botones
			NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic(
					"jdbc/gestion");
			int imgAnular = 0;
			int imgAprobar = 0;
			int imgDevolver = 0;
			int imgPdf = 0;
			Iterator it1 = rol.entrySet().iterator();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry) it1.next();
				roles += r.getKey().toString() + ",";
			}
			if (roles.length() > 0) {
				roles = roles.substring(0, roles.length() - 1);
			}
			Map botones = nb.getBotones(roles, "Pedidos", "ampliacionesPedido");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry) btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%String img = (String) b.getValue();
				if ("imgAprobarPresupuestoPed".equals(img)) {
					imgAprobar = 1;
				}
				if ("imgDevolverPresupuestoPed".equals(img)) {
					imgDevolver = 1;
				}
			}%> 
			
			$("#dtblDetalleAmpliacionesMaster").css("display","none");
			$("#ddt_grabaprecompD").css("display","none");
			$("#ddt_grabaprecompE").css("display","none");
			$("#ddt_urcc").css("display","none");
			setReadOnly();
			headerQuery();
			initDataTable();
			$("#cTipoDocumento").val("PEDIDO");
			queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
			queryFormPost("fnMontoSubtotalRead", {async: false});
			queryFormPost("fnMontoIVARead", {async: false});
			queryFormPost("fnMontoImpuesto1Read", {async: false});
			queryFormPost("fnMontoImpuesto2Read", {async: false});
			queryFormPost("fnMontoImpuesto3Read", {async: false});
			$("#mesDisponible").val(parseInt($("#mesDisponible").val(),10)+parseInt($("#GP_VALOR").val(),10));
			
			if($("#lblImporteImpuesto1").val() == ""){
				$("#divImporteImpuesto1").css("display","none");
			}
			if($("#lblImporteImpuesto2").val() == ""){
				$("#divImporteImpuesto2").css("display","none");
			}
			if($("#lblImporteImpuesto3").val() == ""){
				$("#divImporteImpuesto3").css("display","none");
			}
			queryFormPost("fnMontoTotalPedido", {async: false});
			$("#mImporteTotal").val($("#lblTotal").val().toString().replace("Total: ",""));
			vcontrato=$("#cIdPedido").val()+"-AMP-"+$("#nIdAmpliacion").val();
			//vfolio=$("#nFolioPreCompromiso").val();
	
			$("#mComprometido").formatCurrency();
			$("#mImporteTotal").formatCurrency();
			$("#difPrecompromiso").formatCurrency();

			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
			if(nIdEstado == 1)
				$("#preCompromisoPedido").css("display", "none");
			else
				$("#preCompromisoPedido").css("display", "block");
			
			ocultarBotones();
			muestraLineas();
		});
		$('#tblAmpliaciones tr').live('dblclick', function() {
			limpiaDatosAmpliacion();
			$("#dtblDetalleAmpliacionesMaster").css("display","");
			$(oTablaAmpliaciones.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});						
			$(this).addClass('row_selected');
			
			var aTrs = $('#tblAmpliaciones').dataTable().fnGetNodes();           
			for ( var i=aTrs.length ; i>=0; i-- ){         
				if ($(aTrs[i]).hasClass('row_selected')){        
					var nTr = $('#tblAmpliaciones').dataTable().fnGetData(aTrs[i]);
					$("#nIdAmpliacion").val(nTr[0]);
					$("#cEstadoAmpliacion").val(nTr[2]);
					muestraBotones();
					$("#cIdOficioPrecompromiso").val(nTr[1]);
					$("#cIdContrato").val($("#cIdPedido").val()+"-AMP-"+nTr[0]);
					$("#nCantidadAmpliacion").val(nTr[3]);
					$("#mMontoAmpliacion").val(nTr[4]);
					$("#cIdUsuarioCreacionAmp").val(nTr[5]);
					$("#nFolioPrecomAmp").val(nTr[7]);
					$("#FolioPrecomAmp").val(nTr[8]);
					$("#cuentaDisponible").val(nTr[9]);
					$("#cDocumentoDefinitivo").val($("#cIdContrato").val());
					$("#cDocumentoDefinitivoAmpliacion").val($("#cPedidoDefinitivo").val()+"-AMP-"+nTr[0]);
					activacheck();
					vcontrato=$("#cIdContrato").val();
					if(nTr[2].toString().toUpperCase() != "CAPTURADO"){
						$("#btnAgregarAmpliacionDetalle").attr("disabled","true");
						$("#checkDispRadicado").attr("disabled","true");
						if(nTr[2].toString().toUpperCase() == "EN SAI SIN PRESUPUESTO" || nTr[2].toString().toUpperCase() == "RECHAZADO")
							$("#btnEditarAmpliacionDetalle").removeAttr("disabled");
						else
							$("#btnEditarAmpliacionDetalle").attr("disabled","true");
					}					
					else{
						$("#btnAgregarAmpliacionDetalle").removeAttr("disabled");
						$("#btnEditarAmpliacionDetalle").attr("disabled","true");
						$("#checkDispRadicado").removeAttr("disabled");
					}				
				}
			}

           //queryFormPost("mFolioPrecompromisoReadAmpliacionContrato", {async: false});
		//	vfolio=$("#nFolioPreCompromiso").val();
			$("#tblDetalleAmpliaciones").css("display", "");
			if($("#cEstadoAmpliacion").val() == "EN SAI SIN PRESUPUESTO" || $("#cEstadoAmpliacion").val() == "RECHAZADO" ){
				recargaTablaAmpliacionDetalle();
				cargaSuficiencias();
				$("#dtblSuficienciaDetalleAmpliaciones").css("display","");
			}
			else if($("#cEstadoAmpliacion").val() == "EN SAI PRESUPUESTADO"){
				recargaTablaAmpliacionDetallePrecompromiso();
				//recargaTablaAmpliacionDetalle();
				$("#dtblSuficienciaDetalleAmpliaciones").css("display","none");
			}
			else if($("#cEstadoAmpliacion").val() == "APROBADO"){
				$("#dtblSuficienciaDetalleAmpliaciones").css("display","none");
				recargaTablaAmpliacionDetallePrecompromiso();
				habilitaMeses($('#tblDetalleAmpliaciones').dataTable());
			}
			else{
				recargaTablaAmpliacionDetalle();
				$("#dtblSuficienciaDetalleAmpliaciones").css("display","none");
			}
			loadURCC();
			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
			if(nIdEstado == 1)
				$("#preCompromisoContrato").css("display", "none");
			else
				$("#preCompromisoContrato").css("display", "block");
		});
		
		function recargaTablaAmpliacionDetalle(){
			oTablaAmpliacionesDetalle=$('#tblDetalleAmpliaciones').dataTable({         
				bAutoWidth : true,
				bScrollCollapse: true,
				bDestroy: true,		
				iDisplayLength: 25,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mPedidoAmpliacionesDetalle('"+$("#cIdPedido").val()+"',"+$("#nIdAmpliacion").val()+")",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cClaveInterna" },
					{ sName: "cClaveEP" },
					{ sName: "compromiso01",bSortable: false },
					{ sName: "compromiso02",bSortable: false },
					{ sName: "compromiso03",bSortable: false },
					{ sName: "compromiso04",bSortable: false },
					{ sName: "compromiso05",bSortable: false },
					{ sName: "compromiso06",bSortable: false },
					{ sName: "compromiso07",bSortable: false },
					{ sName: "compromiso08",bSortable: false },
					{ sName: "compromiso09",bSortable: false },
					{ sName: "compromiso10",bSortable: false },
					{ sName: "compromiso11",bSortable: false },
					{ sName: "compromiso12",bSortable: false },
					{ sName: "cEliminar" },
					{ sName: "nIdConsecutivoAmpliacionDetalle", bVisible:false },
					{ sName: "centroContable", bVisible:false }
				]
			});
			deshabilitaMeses($('#tblDetalleAmpliaciones').dataTable());
		}
		function recargaTablaAmpliacionDetallePrecompromiso(){			
			oTablaAmpliacionesDetalle=$('#tblDetalleAmpliaciones').dataTable({         
				bAutoWidth : true,
				bScrollCollapse: true,
				bDestroy: true,		
				iDisplayLength: 25,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaPreCompromisoTodos( '" + $("#cPedidoDefinitivo").val() + "'," + $("#nIdAmpliacion").val() + " )",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ClaveInterna" },
					{ sName: "ClaveSIAFF" },
					{ sName: "compromiso01",bSortable: false },
					{ sName: "compromiso02",bSortable: false },
					{ sName: "compromiso03",bSortable: false },
					{ sName: "compromiso04",bSortable: false },
					{ sName: "compromiso05",bSortable: false },
					{ sName: "compromiso06",bSortable: false },
					{ sName: "compromiso07",bSortable: false },
					{ sName: "compromiso08",bSortable: false },
					{ sName: "compromiso09",bSortable: false },
					{ sName: "compromiso10",bSortable: false },
					{ sName: "compromiso11",bSortable: false },
					{ sName: "compromiso12",bSortable: false },
					{ sName: "EP",bVisible:false },
					{ sName: "cIdContrato", bVisible:false },
					{ sName: "tmp", bVisible:false }
				]
			});
			deshabilitaMeses($('#tblDetalleAmpliaciones').dataTable());
		}
		function initDataTable(){
			$("#cEstadoAmpliacion").val("");
			$("#nIdAmpliacion").val("");
			oTablaAmpliaciones=$('#tblAmpliaciones').dataTable({         
				bAutoWidth : true,
				bScrollCollapse: true,
				bDestroy: true,		
				iDisplayLength: 25,
				sScrollX: "100%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mPedidoAmpliaciones('"+$("#cIdPedido").val()+"','"+$("#cIdUnidadEjecutoraSolicitud").val()+"')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				//aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "nIdAmpliacion", bVisible:false },
					{ sName: "cIdOficioPrecompromiso" },
					{ sName: "cEstado" },
					{ sName: "cantidadAmpliacion" },
					{ sName: "montoAmpliacion" },
					{ sName: "cIdUsuarioCreacion" },
					{ sName: "cIdUsuarioCancelacion" },
					{ sName: "ConsecutivoPRECOMP" , bVisible:false},
					{ sName: "C_FOLIO_PRE" , bVisible:false},
					{ sName: "cNumCuentaDisp" , bVisible:false}
					//{ sName: "Guarda" }
				]
			});
			
			oTableUrcc=$('#dt_urcc').dataTable( {
					bPaginate: false,
	       			bLengthChange: false,
	       			bFilter: false,
	       			bInfo: false,
					bAutoWidth: false,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					//sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mUrccContratoModificado(" + campos + ")",
					aoColumns: [
						{ sName: "centroContable"},
						{ sName: "ur" }
					]
			}) ;
			$('#dt_urcc').attr('visible', false);
		}
		
		function loadURCC(){
			var campos = "'" + $("#cIdPedido").val() + "'," + $("#nIdAmpliacion").val();
			oTableUrcc=$('#dt_urcc').dataTable( {
					bPaginate: false,
	       			bLengthChange: false,
	       			bFilter: false,
	       			bInfo: false,
					bAutoWidth: false,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mUrccPedidoAmpliacion(" + campos + ")",
					aoColumns: [
						{ sName: "centroContable"},
						{ sName: "ur" }
					]
			}) ;
		}
		
		function setReadOnly(){
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;
		}
		function headerQuery(){
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("cg_roleRead", {async: false});
			queryFormPost("mMontoMaximoPedido", {async: false});
			queryFormPost("mMontoTotalPedido", {async: false});
			queryFormPost("TipoPolizaRead", {async: false});
		}
		function eliminaAmpliacionDetalle(nIdAmpliacionDetalle){
			if(confirm("\xBFEst\xE1s seguro de eliminar el detalle de la ampliaci\xF3n?.")){
				$("#nIdAmpliacionDetalle").val(nIdAmpliacionDetalle);
				queryFormPost("deleteAmpliacionDetallePedido", {async: false});
				//initDataTable();
				recargaTablaAmpliacionDetalle();
				alert("El detalle de ampliaci\xF3n se elimino correctamente.");
			}
		}
		function devuelveContratoDiverso(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#cEstadoAmpliacion").val().toString() == "EN SAI SIN PRESUPUESTO"){
					var proc=""+$("#cEjercicio").val()
							+","+$("#cPedidoDefinitivo").val()+"-AMP-"+$("#nIdAmpliacion").val()
							+",pedido"
							+","+$("#cIdPedido").val();
					ocultarBotones();
					$.getJSON("../../servlet/AmpliacionesServlet?"+new Date().getTime()+"&operacion=1",{Tabla: "", Param: proc, MaxReg: "", ajax: 'true'}, function(j){
				 		for(var i = 0; i < j.length; i++)
		                	 var col=j[i].Col1
		                switch(col){
						case "0":  
							alert("Se ha devuelto el presupuesto de la ampliaci\xF3n correctamente.");
							initDataTable();
							$("#dtblDetalleAmpliacionesMaster").css("display","none");
						break;
						case "1":  
							alert("Error al eliminar las EP del contrato.");
						break;
						case "2":  
							alert("Error al eliminar el contrato diverso convenio.");
						break;
						case "3":  
							alert("Error al eliminar las folios del Documento.");
						break;
						case "4":  
							alert("Error al eliminar las EP de tabla temporal.");
						break;
						case "5":  
							alert("Error al actalizar el estatus de la ampliaci\xF3n.");
						break;
						case "-1":  
							alert("NO SE PUEDE DEVOLVER EL PRESUPUESTO INTENTELO NUEVAMENETE");
						break;
	                 	}
					});					
				}
				else{
					alert("No se puede devolver el presupuesto de la ampliaci\xF3n, su estatus no lo permite.");
				}
			}
			else{
				alert("Debe seleccionar una ampliaci\xF3n.")
			}
		}
		function devuelvePrecompromiso(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#cEstadoAmpliacion").val().toString() == "EN SAI PRESUPUESTADO" || $("#cEstadoAmpliacion").val().toString() == "APROBADO"){
					queryFormPost("usuarioVentanillaReadPedido", {async: false});
					if($("#operacion").val()==2 && $("#responsable").val()!="VENTANILLA DE PAGOS")
					{
						alert("No se puede eliminar el precompromiso, el pedido lo esta revisando el usuario de ventanilla "+ ' '+$("#responsable").val());
						return;
					}
					if($("#cEstadoAmpliacion").val().toString() != "APROBADO")
					{
						var res=window.confirm("¿Está seguro que quiere eliminar el pre-compromiso? el pedido se encuentra en VENTANILLA DE PAGOS y esta pendiente su aprobación,esta acción no se puede deshacer");
						//Elimina el Precompromiso
						if(res){
							ocultarBotones();
							var datos = '&tipoPago=' + $("#nTipoPago").val() + '&cEjercicio=' + $("#cEjercicio").val() 
								+ '&cIdDocumento=' + $("#cIdPedido").val() + '&consAmp=' + $("#nIdAmpliacion").val() + '&tipo=Pedido' 
								+ "&cIdDocumentoDefinitivo=" + $("#cPedidoDefinitivo").val();
							$.ajax({url: '../../servlet/AmpliacionesServlet' , type:'post' , async: false,data:'operacion=4' + datos, dataType: 'json', success: eliminaPrecompromiso});
						}else
							return;					
					}else
					{
						alert("El pedido no se puede devolver porque ya ha sido APROBADO y esta COMPROMETIDO");
					}
				}
			}
			else{
				alert("Debe seleccionar una ampliaci\xF3n.")
			}
		}
	
	
		function eliminaPrecompromiso(j){
		  	var mensaje=j[0].Contable1;
			alert(mensaje);
			if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE")<0){
					//NO CANCELO CONTABLEMENTE SE DEJA EN SAIPRESUPUESTADO
					$("#nIdEstadoAmpliacion").val(3);
					queryFormPost("mUpdateEstadoAmpliacionPedido", {async: false });
					document.getElementById("imgDevolverAmpliacion").disabled = false;
					return;
					}
				
				if ($("#nTipoPago").val() == '0') {
					queryFormPost("mAmpliacionPedidoPrecompromisoDelete", {async: false });
				}
				else {
					queryFormPost('mDocumentoFolioPrecompromisoDeletePed', {async: false });
				}
				
				//Regresa el estado a en SAI sin Precomprometer
				$("#nIdEstadoAmpliacion").val(2);
				
				document.getElementById("imgAprobarpreCompromisoPed").disabled = false;
				document.getElementById("imgDevolverAmpliacion").disabled = true;
				
				queryFormPost("mUpdateEstadoAmpliacionPedido", {async: false });
				queryFormPost("mImportesContratoConvenioUpdatePed", {async: false });
				//alert(mensaje);
				initDataTable();
				$("#dtblDetalleAmpliacionesMaster").css("display","none");
				queryFormPost("mMontoTotalPedido", {async: false});
		
		}
		
		
		function apruebaAmpliacion(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#lblEstado").val().indexOf("APROBADO") >= 0){
					if($("#cEstadoAmpliacion").val().toUpperCase() == "CAPTURADO"){
						if(confirm("\xBFEst\xE1s seguro de aprobar la ampliaci\xF3n?")){
							ocultarBotones();
							var proc=""+$("#cEjercicio").val()
								+","+$("#cIdPedido").val()
								+",pedido"
								+","+$("#nIdAmpliacion").val();

							//Llama a AmpliacionesServlet para llamar el stored procedure que aprueba el Pedido
							 $.getJSON("../../servlet/AmpliacionesServlet?"+new Date().getTime()+"&operacion=0",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
										for(var i = 0; i < j.length; i++)
											 var col=j[i].Col1
										switch(col){
											case "0": 
												$("#nIdEstadoAmpliacion").val(2);
												queryFormPost("mUpdateEstadoAmpliacionPedido", {async: false});
												alert("La ampliaci\xF3n se aprobo correctamente.");
												initDataTable();
												$("#dtblDetalleAmpliacionesMaster").css("display","none");
												//queryFormPost("mMontoTotalPedido", {async: false});
											break;
											case "1":  
												alert("Error al guardar las EP en tabla temporal.");
											break;
											case "2":  
												alert("Error al guardar el contrato diverso convenio.");
											break;
											case "3":  
												alert("Error al guardar las EP del contrato diverso..");
											break;
											case "-1":  
											alert("NO SE HA PODIDO APROBAR EL PRESUPUESTO INTENTELO NUEVAMENTE");
											break;
										}
								});
						}
					}
				}
				else{
					alert("Para aprobar una ampliaci\xF3n debe estar aprobado el pedido.");
				}
			}
			else{
				alert("Debe seleccionar una ampliaci\xF3n.")
			}
		}
		
		function formateaMonto(obj){
			obj.value=obj.value.replace("$","").replace(",","");
		}
		
		function nuevaAmpliacion(){
			var cadenaLineaConsCant="";
			var token="";
			ocultarBotones();
			if($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()==''){
				//Para bienes
				var aTrs = $('#tblLineasBienes').dataTable().fnGetNodes();
				var cantDisp=0;
				for ( var i=0 ; i<aTrs.length; i++ )     
				{
					var nTr = $('#tblLineasBienes').dataTable().fnGetData(aTrs[i]);
					cantDisp=nTr[6];
					var cantCapturado=$("#nCantidadIncremento_"+nTr[2]).val();
					if(parseInt(cantCapturado,10)<=parseInt(cantDisp,10) && parseInt(cantCapturado,10)>0){
						cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[2]+'-'+cantCapturado;
						token=",";
					}else{
						if(parseFloat(cantCapturado)>0){
							alert("En la linea "+nTr[2]+" sobrepasaste la cantidad disponible.");
							return;
						}	
					}
				}
				if(cadenaLineaConsCant==''){
					alert("No hay nada que agregar");
					return;
				}
			}else{//Para Servicios
				var aTrs = $('#tblLineasServ').dataTable().fnGetNodes();
				var montoDisp=0;
				for ( var i=0 ; i<aTrs.length; i++ )     
				{  
					var nTr = $('#tblLineasServ').dataTable().fnGetData(aTrs[i]);
					montoDisp=nTr[6];
					montoDisp = montoDisp.replace("$", "");
					montoDisp = montoDisp.replace(/,/g, "");
					var montoCapturado=$("#mMontoNetoIncremento_"+nTr[2]).val();
					montoCapturado=montoCapturado.replace("$", "");
					montoCapturado = montoCapturado.replace(/,/g, "");
					montoCapturado=parseFloat(montoCapturado);
					montoCapturado = montoCapturado.toFixed(2);
					if(parseFloat(montoCapturado)<=parseFloat(montoDisp) && parseFloat(montoCapturado)>0.0){
						cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[2]+'-'+montoCapturado;
						token=",";
					}else{
						if(parseFloat(montoCapturado)>0){
							alert("En la linea "+nTr[2]+" sobrepasaste el monto disponible.");
							return;
						}	
					}
				}
				if(cadenaLineaConsCant==''){
					alert("No hay nada que agregar");
					return;
				}
			}
			$("#cadenaLineaCantidad").val(cadenaLineaConsCant);
			queryFormPost("insertAmpPedido", {async: false});
			initDataTable();
			muestraLineas();
			$("#dtblDetalleAmpliacionesMaster").css("display","none");			
			
		}
		
		function eliminaAmpliacion(){
			var roles="<%=roles%>";
			if($("#nIdAmpliacion").val() != ""){
				if(roles.indexOf("ADMIN_RECMAT") >= 0){
					if($("#cEstadoAmpliacion").val().toUpperCase() == "CAPTURADO"){
						ocultarBotones();
						$("#nIdEstadoAmpliacion").val(6);
						queryFormPost("mAnulaAmpliacionPedido", {async: false,
							callback: function(){
								$("#cAccion").val("ANULA_AMPLIACIÓN");
								$("#cIdDocumento").val($("#cPedidoDefinitivo").val()+'-AMP-'+$("#nIdAmpliacion").val());
								queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
							}
						});
						initDataTable();
						muestraLineas();
						$("#dtblDetalleAmpliacionesMaster").css("display","none");
					}
					else{
						alert("La ampliaci\xF3n no se puede eliminar, su estado no lo permite.");
					}
				}
				else{
					alert("El usuario no puede realizar esta accion.");
				}
			}
			else{
				alert("Debe seleccionar una ampliaci\xF3n.")
			}
		}
		
		
		function buscaClaveEP() {
			queryFormPost("getcIdUsuarioCreacionPedidoV", {async: false,
				callback : function() 
					{
						pp = window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' 
								+ $('#cIdUsuarioCreacion').val() + '&cIdDocumento=' + $('#cIdPedido').val()
								+ '&cuentaDisponible=' + $('#cuentaDisponible').val()
								, 'MultiReporteGridSacel', 'status=1,scrollbars=yes, width=900px, height=780px, left=100px');
					}
			});
			
		}
		
		function limpiaDatosAmpliacion(){
			$("#ep").val("");
		}
		
		function validarGuardaAmpliacionDetalle(){
			if($("#nIdAmpliacion").val() == ""){
				alert("Debe seleccionar una ampliaci\xF3n.");
				return;
			}
			if($("#ep").val() == ""){
				alert("Debe seleccionar una EP.");
				return;
			}
			var aTrs = $('#tblDetalleAmpliaciones').dataTable().fnGetNodes();
			
			if(aTrs.length > 0){        
				for ( var i=aTrs.length ; i>=0; i-- ){
					var nTr = $('#tblDetalleAmpliaciones').dataTable().fnGetData(aTrs[i]);
					if(nTr[1]+"."+nTr[0] == $("#ep").val()){
						alert("La EP ya esta agregada, favor de seleccionar otra.");
						return;
					}					
				}
			}
			queryFormPost("insertAmpliacionDetallePedido", {async: false});
			recargaTablaAmpliacionDetalle();
			$("#ep").val("");
		}
		
		function editCeldasDetalleAmpliaciones(){
	       	var nRow = editPrecompromiso( $('#tblDetalleAmpliaciones').dataTable() ) ; 
	   	}
	   	
		function editPrecompromiso( oTableLocal ) {
			//funcion para agregar los inputs a la tabla
			var i,j,descMes, nomMes,nColumna;
			var aData;
			var aTrs = oTableLocal.fnGetNodes();
			for (i=1 ; i<=aTrs.length ; i++ ){
				mes=parseInt($("#mesDisponible").val(),10);
				if(mes==0)
					mes=mes+1;
			    aData = oTableLocal.fnGetData(i-1);
			    nColumna=aData.length-mes-1;//dos columnas al final ocultas
			    
			    for(j=2; j<nColumna-1; j++){
			    	nomMes='mes'+mes;
			    	descMes='mes'+mes+'-'+i;
			   		$("#tblDetalleAmpliaciones").children().children()[i].children[j].innerHTML = '<input style="width: 100%" type="text" id="' + descMes + '" name="'+nomMes+'" onchange="valSufic(this)"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[mes+1]+'" onKeyPress="return(onlyNumbers(event))">';
			    	mes++;	
			    }
		    }
		}
		
		function deshabilitaMeses(oTableLocal){
			var i=$("#mesDisponible").val();
			i=parseInt(i,10);
			for(i; i>1;i--){
				oTableLocal.fnSetColumnVis(i,false);
			}
		}
		
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '-0123456789.';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true 
		}
		
		function valSufic( fld ){
			var valor = $("#" + fld.id).val();
			//validacion para que solo se permitan números
			valor=quitaFmt(valor);
			if(isNaN(valor)){
				alert("Ingrese solo valores numéricos");
				$("#" + fld.id).val( "0" );
				return false;
			}
			if(valor<0){
				alert("No se pueden ingresar valores negativos");
		    	$("#" + fld.id).val( "0" );
		    	return false;
			}
			//obtiene la suficiencia de la EP y el valor del precompromiso ingresado para comparar
			var oTableLocal = $("#tblSuficienciaDetalleAmpliaciones").dataTable();
			var vimptot = $("#mImporteTotal").val();
			var vcompr  = $("#mComprometido").val();
			vimptot = quitaFmt( vimptot );
			vcompr = quitaFmt( vcompr );
			vcompT = parseFloat(vimptot)-parseFloat(vcompr);
			var nren = parseInt(fld.id.substring(fld.id.lastIndexOf("-") + 1),10) -1;
			var ncol = parseInt( fld.id.substring(5, 3),10 ) + 1 ;
			var aData = oTableLocal.fnGetData( nren );
			if (valor == "") {
				valor = '0';
				$("#" + fld.id).val( "0" );
			}
			var valsuf = parseFloat( aData[ ncol ] );
			if (parseFloat(valor) > valsuf) {
		    	alert("NO hay Suficicencia Mensual en la Clave Presupuestal");
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompr=parseFloat(vcompr);
			valor=parseFloat(valor);
			vimptot=parseFloat(vimptot);
			if ((vcompr.toFixed(2)+valor.toFixed(2))>  vimptot) {
		    	alert("El Compromiso Actual Excede al Saldo Compromiso"); 
		    	$("#" + fld.id).val( "0" );
		    	return false;
		    }
			vcompT = vimptot-valor-vcompr;
			//Redondea a 2 decimales para evitar conflictos con la notación científica para valores muy pequeños [xE-y]
			vcompT=vcompT.toFixed(2);
			$("#difPrecompromiso").val(vcompT);
			$("#difPrecompromiso").formatCurrency();
		}
		
		function Sinfrmt( fld )	{
		   	var valcol = fld.value ;
		   	var vcompr = $("#mComprometido").val();
		   	vcompr = quitaFmt( vcompr );
		   	valcol = quitaFmt( valcol );
			$("#" + fld.id).val( valcol );
		   	fld.select();
			$("#mComprometido").val( parseFloat( vcompr ) - parseFloat( valcol ) );
			$("#mComprometido").formatCurrency();
		}
		
		function cambiafrmt( fld )	{
		   	var vcompr = $("#mComprometido").val();
		   	var vfld = $("#" + fld.id).val()
		   	if (vfld == "")
		   		vfld = '0';
			vcompr = quitaFmt( vcompr );
			vfld=quitaFmt(vfld);
		   	$("#mComprometido").val( parseFloat(vcompr) + parseFloat( vfld ) );
			$("#" + fld.id).formatCurrency();
			$("#mComprometido").formatCurrency();
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
		
		function cargaSuficiencias(){
			var qw=" nIdConsecutivoAmpliacion="+$("#nIdAmpliacion").val()+ " and cIdEntidadContable = '" + $("#cIdEntidadContable1").val() 
			+ "' and cIdPedido='"+$("#cIdPedido").val()+"' and nCuentaP='"+$("#cuentaDisponible").val()+"'";
			var oTable=$('#tblSuficienciaDetalleAmpliaciones').dataTable( {
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
				}},
	   			bAutoWidth: true,
				sScrollX: 100,
				sScrollY: 100,
		        bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				//Query de Financiero, también se utiliza para Compromiso
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_disponibleContratoAmpEP('" + $("#cIdPedido").val() 
				+ "','"+$("#cuentaDisponible").val()+"','"+$("#cIdEntidadContable1").val()+"',"+$("#nIdAmpliacion").val()+")",
				aaSorting: [[ 0, "asc" ],[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ClaveInterna",bSortable: false },
					{ sName: "ClaveSIAFF" ,bSortable: false},
					//{ sName: "ClaveInterna",bSortable: false },
					{ sName: "MontoEnero" ,bSortable: false},
					{ sName: "MontoFebrero" ,bSortable: false},
					{ sName: "MontoMarzo" ,bSortable: false},
					{ sName: "MontoAbril",bSortable: false },
					{ sName: "MontoMayo" ,bSortable: false},
					{ sName: "MontoJunio",bSortable: false },
					{ sName: "MontoJulio" ,bSortable: false},
					{ sName: "MontoAgosto" ,bSortable: false},
					{ sName: "MontoSeptiembre" ,bSortable: false},
					{ sName: "MontoOctubre",bSortable: false },
					{ sName: "MontoNoviembre" ,bSortable: false},
					{ sName: "MontoDiciembre" ,bSortable: false},
					{ sName: "MontoAnual",bSortable: false },
					{ sName: "cIdPedido",	bSearchable: false,	bSortable: false, bVisible: false } ],
				fnInitComplete: function(oSettings, json) {
				}
	           } );
			deshabilitaMeses($('#tblSuficienciaDetalleAmpliaciones').dataTable());
		}
		
		function guardarPrecompromisoGeneral(){
			if($("#nIdAmpliacion").val() != ""){
				if($("#cEstadoAmpliacion").val() == "EN SAI SIN PRESUPUESTO"){
					
					if (!validaPrecompromisoDes()){
						return;
					}
					ocultarBotones();
			
					if ($("#nTipoPago").val() == '1') {
						var aTrs = oTableUrcc.fnGetNodes();
						for (var i = 0; i < aTrs.length; i++) {
							line = oTableUrcc.fnGetData(aTrs[i])
							var cc = line[0];
							var ur = line[1];		
							//guardarPrecompromisoDes(cc, ur);
							var mensaje=guardarPrecompromisoDes(cc, ur);
							if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE")<0){
								$("#nIdEstadoAmpliacion").val(2);
								vEstado=2;
								queryFormPost("mUpdateEstadoAmpliacionPedido", {async: false });
								queryFormPost('tPreCompromisoAmpPedDeleteD', {async: false });
								queryFormPost('tPreCompromisoAmpPedDeleteE', {async: false });
								 //habilitamos el boton para que solo se le de click una vez. si hubo un error
								document.getElementById("imgAprobarpreCompromisoPed").disabled = false;
								return;
							}
						
						}
						
						$("#nIdEstadoAmpliacion").val(3);
						queryFormPost("mUpdateEstadoAmpliacionPedido", {async: false});
					}
					else{
						var mensaje=guardarPrecompromiso();
						if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO APLICADO CONTABLEMENTE")<0){
							$("#nIdEstadoAmpliacion").val(2);
							vEstado=2;
							queryFormPost("mUpdateEstadoAmpliacionPedido", {async: false });
							queryFormPost('tPreCompromisoAmpPedDeleteD', {async: false });
							queryFormPost('tPreCompromisoAmpPedDeleteE', {async: false });
							 //habilitamos el boton para que solo se le de click una vez. si hubo un error
							document.getElementById("imgAprobarpreCompromisoPed").disabled = false;
					
							return;
						}
					}
					
					
					//Actualiza el monto y la descripcion del contrato diverso si la aplicacion contable fue correcta
					$("#cMontoContratoDiverso").val();
					$("#cIdOficioPrecompromisomod").val(($("#cIdTipoPedido").val())+'-'+($("#cIdUnidadEjecutora").val())+'-'+($("#nIdConsecutivo").val())+'-'+($("#nIdAmpliacion").val()));
					//queryFormPost("obtieneIVAContratoAmpliacion", {async: false});
					queryFormPost("mUpdateImportesPedidoDiversoDesConIvaDesglosado", {async: false});
					//queryFormPost("mUpdateImportesContratoDiversoDesCon", {async: false});
					document.getElementById("imgAprobarpreCompromisoPed").disabled = true;
					document.getElementById("imgDevolverAmpliacion").disabled = false;
			
					initDataTable();
					$("#dtblDetalleAmpliacionesMaster").css("display","none");	
					alert("Ampliacion Precomprometida con folio(s) " + $("#foliosCasoPreCompromiso").val());
					$("#foliosCasoPreCompromiso").val('');
				}else{
					alert("No se puede precomprometer la ampliaci\xF3n por que su estado no lo permite.");
				}
			}
			else{
				alert("Debe seleccionar una ampliaci\xF3n.");
			}
		}
		
		function guardarPrecompromisoDes(centroContable, ur){
			var montoTotal = 0;
			var mensajeAp="";
			//Genera un nuevo folio en caso de que no lo tenga
			$.ajax({url: '../../servlet/AmpliacionesServlet' , type:'post' , async: false,data:'operacion=2&ur='+ur, dataType: 'json', success: guardaFolio});
			
			//Guarda en una tabla auxiliar el encabezado del compromiso
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;
			$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
			$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
			$("#fCarga").val( "<%=today%>" ); 
			$("#fAplicacion").val("<%=today%>" ); 
			//$("#cIdContrato").val(vcontrato);
			$("#cIdContrato").val($("#cPedidoDefinitivo").val()+"-AMP-"+$("#nIdAmpliacion").val());
			$("#cTipoContrato").val( "DI" );
			$("#cCentroContable").val(centroContable); 
			$("#cRamo").val( "<%=cRamo%>" );
			$("#cUnidadResponsable").val(ur);
			$("#nEnviadoSICOP").val("0");
			$("#nMes").val( nMes ); 
			$("#fVigencia").val( "<%=vigencia%>" );
			
			getNextSequenceVal({seqName: "CO-" + centroContable, async: false, callback: setSequenceVal});
			$("#caNoPreCompromiso").val( vcaNoCompromiso );
			
			//Genera Encabezado y detalle
			queryFormPost('tPreCompromisoECreate', {async: false });
			
			//obtiene el número de filas de la tabla de precompromiso
			var vdocren = 1;
			var nRows = $("#tblDetalleAmpliaciones tr").length -1;
			var oTable = $('#tblDetalleAmpliaciones').dataTable();
			var oTablD = $('#dt_grabaprecompD').dataTable();
			var aTrs = oTable.fnGetNodes();
			//Obtine las columnas de las que va a precomprometer
			var j,nColums=12-parseInt($("#mesDisponible").val(),10);
			oTablD.fnClearTable();
			var evento="PRECOM";
			if($("#cuentaDisponible").val()=='82109'){
				evento="R_PRECOM";
			}
			for ( var i=0 ; i< nRows ; i++ ) {
				var aData = oTable.fnGetData( i );
				var vCentroContable = aData[16];
				
				if (vCentroContable == centroContable) {
					//valida la ur de la ep
					if(ur == aData[0].substring(0,3)){

						var jqInputs = $('input', aTrs[i] );
						if (jqInputs.length > 0) {
							for ( j=0 ; j <= nColums ; j++ ) {
								var vep = aData[ 1 ] + "." + $.trim(aData[ 0 ]) ;
								var clv=$.trim(aData[1]);
								var ff=clv.substring(39, 40); 
								if(ff=='4' || $("#cuentaDisponible").val()=="82106"){
									evento="PRECOM";
								}else{
									evento="R_PRECOM";
								}
								var vimporteP = jqInputs[ j ].value ;
								vimporteP = quitaFmt(vimporteP);
								if (parseFloat(vimporteP) != 0 ) {
									var vimporteN = vimporteP * -1 ; 
									vMes = parseInt($("#mesDisponible").val(),10)+j;
									
									montoTotal = montoTotal+parseFloat(vimporteP);
									$('#dt_grabaprecompD').dataTable().fnAddData( [
											'<td><input type="text" id="nDocRenglon" name="nDocRenglon" value="' + vdocren + '"></td>',
											'<td><input type="text" id="EP" name="EP" value="' + vep + '"></td>',
											'<td><input type="text" id="cEvento" name="cEvento" value="'+evento+'"></td>',
											'<td><input type="text" id="mImporte" name="mImporte" value="' + vimporteP + '"></td>',
											'<td><input type="text" id="mImporteNegativo" name="mImporteNegativo" value="' + vimporteN + '"></td>',
											'<td><input type="text" id="nFolioPreCompromisoD" name="nFolioPreCompromisoD" value="' + $("#nFolioPreCompromiso").val()+ '"></td>',
											'<td><input type="text" id="cMes" name="nMesD" value="' + vMes + '"></td>',
											'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + centroContable + '"></td>'
										] );
									queryFormPost("tPreCompromisoDCreate", {async: false });
									oTablD.fnClearTable();
									vdocren++;
								}
							}
						}
					}
				}
			}
			
			//Actualiza el monto y la descripcion del contrato diverso
			$("#cMontoContratoDiverso").val(montoTotal);
			//queryFormPost("mUpdateImportesContratoDiversoDesPed", {async: false});
				
			//Actualiza el encabezado con la descripcion de la poliza incluyendo id definitivo de la ampliación
			queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
			queryFormPost("mDocumentoFolioPrecompromisPedCreate", {async: false });
			
			$('#dt_grabaprecompD').attr('visible', false);
			
			//aplicacion contable
		//	$.ajax({url: '../../servlet/AmpliacionesServlet' , type:'post' , async: false,data:'operacion=3', dataType: 'json'});
				
			//aplicacion contable
			$.ajax({url: '../../servlet/AmpliacionesServlet?nFolioPrecompromiso='+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#cEjercicio").val()+"&cIdContrato="+$("#cIdContrato").val(), type:'post' , async: false,data:'operacion=3', dataType: 'json' , success: 
			function(j){
				mensajeAp=j[0].Contable1;
				alert(mensajeAp);
			}
			
			 });
			queryFormPost("mMontoTotalPedido", {async: false});
			if ($("#foliosCasoPreCompromiso").val() != '') $("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
			$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
			return mensajeAp;
	    }
	    
	
		function guardarPrecompromiso(){
			var montoTotal = 0;
			var mensajeAp="";
			//Genera un nuevo folio en caso de que no lo tenga
			$.ajax({url: '../../servlet/AmpliacionesServlet' , type:'post' , async: false,data:'operacion=2', dataType: 'json', success: guardaFolio});

			//Guarda en una tabla auxiliar el encabezado del compromiso
			var nMes = "<%=today%>";
			var nMes = nMes.substring(5, 7 ) ;
			$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
			$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
			$("#fCarga").val( "<%=today%>" ); 
			$("#fAplicacion").val("<%=today%>" ); 
			$("#cIdContrato").val($("#cPedidoDefinitivo").val()+"-AMP-"+$("#nIdAmpliacion").val());
			$("#cTipoContrato").val( "DI" );
			$("#cCentroContable").val( "<%=cCentroContable%>");
			$("#cRamo").val( "<%=cRamo%>" );
			$("#cUnidadResponsable").val( "<%=cUR%>" );
			$("#nEnviadoSICOP").val("0");
			$("#nMes").val( nMes ); 
			$("#fVigencia").val( "<%=vigencia%>" );

			getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
			$("#caNoPreCompromiso").val( vcaNoCompromiso );
			
			//Genera Encabezado y detalle
			queryFormPost('tPreCompromisoECreate', {async: false });
			
			//Genera en una tabla auxiliar el detalle del precompromiso
			var vdocren = 1 ; //Numero del renglon
			//obtiene el número de filas de la tabla de precompromiso
			var nRows = $("#tblDetalleAmpliaciones tr").length -1 ;
			var oTable = $('#tblDetalleAmpliaciones').dataTable();
			var oTablD = $('#dt_grabaprecompD').dataTable();
			var aTrs = oTable.fnGetNodes();
			//Obtine las columnas de las que va a precomprometer
			var j,nColums=12-parseInt($("#mesDisponible").val(),10);
			oTablD.fnClearTable();
			var evento="PRECOM";
			if($("#cuentaDisponible").val()=='82109'){
				evento="R_PRECOM";
			}
			for ( var i=0 ; i<nRows ; i++ ) {
				var aData = oTable.fnGetData( i );
				var jqInputs = $('input', aTrs[i] );
				if (jqInputs.length > 0) {
					for ( j=0 ; j <= nColums ; j++ ) {
						var vep = aData[ 1 ] + "." + $.trim(aData[ 0 ]) ;
						var clv=$.trim(aData[1]);
						var ff=clv.substring(39, 40); 
						if(ff=='4' || $("#cuentaDisponible").val()=="82106"){
							evento="PRECOM";
						}else{
							evento="R_PRECOM";
						}
						var vimporteP = jqInputs[ j ].value ;
						vimporteP = quitaFmt(vimporteP);
						if (parseFloat(vimporteP) != 0 ) {
							var vimporteN = vimporteP * -1 ; 
							vMes = parseInt($("#mesDisponible").val(),10)+j;
							montoTotal = montoTotal+parseFloat(vimporteP);
							$('#dt_grabaprecompD').dataTable().fnAddData( [
									'<td><input type="text" id="nDocRenglon" name="nDocRenglon" value="' + vdocren + '"></td>',
									'<td><input type="text" id="EP" name="EP" value="' + vep + '"></td>',
									'<td><input type="text" id="cEvento" name="cEvento" value="'+evento+'"></td>',
									'<td><input type="text" id="mImporte" name="mImporte" value="' + vimporteP + '"></td>',
									'<td><input type="text" id="mImporteNegativo" name="mImporteNegativo" value="' + vimporteN + '"></td>',
									'<td><input type="text" id="nFolioPreCompromisoD" name="nFolioPreCompromisoD" value="' + $("#nFolioPreCompromiso").val()+ '"></td>',
									'<td><input type="text" id="cMes" name="nMesD" value="' + vMes + '"></td>',
									'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + "<%=cCentroContable%>" + '"></td>'
								] );
							vdocren++ ;
							queryFormPost("tPreCompromisoDCreate", {async: false });
							oTablD.fnClearTable();
						}
					}
				}
			}

			//Actualiza el monto y la descripcion del contrato diverso
			$("#cMontoContratoDiverso").val(montoTotal);
			$("#cDescripcionContratoDiverso").val("AMPLIACION DEL PEDIDO "+$("#cIdPedido").val()+" NUM. DE OFICIO: "+$("#cIdOficioPrecompromiso").val());
			//queryFormPost("mUpdateImportesContratoDiversoDesPed", {async: false});	
			//Actualiza el encabezado con la descripcion de la poliza incluyendo id definitivo de la ampliación
			queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
			queryFormPost("mAmpliacionPrecompromisoUpdatePed", {async: false });
			$('#dt_grabaprecompD').attr('visible', false);
			
			//aplicacion contable
		//	$.ajax({url: '../../servlet/AmpliacionesServlet' , type:'post' , async: false,data:'operacion=3', dataType: 'json'});
			
			
				
			//aplicacion contable
			$.ajax({url: '../../servlet/AmpliacionesServlet?nFolioPrecompromiso='+$("#nFolioPreCompromisoE").val()+"&cEjercicio="+$("#cEjercicio").val()+"&cIdContrato="+$("#cIdContrato").val() , 
				type:'post' , async: false,data:'operacion=3', dataType: 'json' , success: 
				function(j){
					mensajeAp=j[0].Contable1;
					alert(mensajeAp);
				}
						
			 });
			queryFormPost("mMontoTotalPedido", {async: false});
			$("#foliosCasoPreCompromiso").val($("#folioCasoPreCompromiso").val());
			return mensajeAp;
			
	    }
	    
	    
	    function validaPrecompromisoDes(){
	    	var montoTotal = 0;
	    	
	    	//Genera en una tabla auxiliar el detalle del precompromiso
			var vdocren = 1 ; //Numero del renglon
			//obtiene el número de filas de la tabla de precompromiso
			var nRows = $("#tblDetalleAmpliaciones tr").length -1 ;
			var oTable = $('#tblDetalleAmpliaciones').dataTable();
			var aTrs = oTable.fnGetNodes();
			//Obtine las columnas de las que va a precomprometer
			var j,nColums=12-parseInt($("#mesDisponible").val());
			
			for ( var i=0 ; i<nRows ; i++ ) {
				var aData = oTable.fnGetData( i );
				var jqInputs = $('input', aTrs[i] );
				if (jqInputs.length > 0) {
					for ( j=0 ; j <= nColums ; j++ ) {
						var vep = aData[ 1 ] + "." + $.trim(aData[ 0 ]) ;
						var vimporteP = jqInputs[ j ].value ;
						vimporteP = quitaFmt(vimporteP);
						if (parseFloat(vimporteP) != 0 ) {
							var vimporteN = vimporteP * -1 ; 
							vMes = parseInt($("#mesDisponible").val(),10)+j;
							montoTotal = montoTotal+parseFloat(vimporteP);
							vdocren++ ;
						}
					}
				}
			}
			
			montoTotal = montoTotal.toFixed(2);
			var montoAmpliacion=$("#mMontoAmpliacion").val().replace("$","").replace(",","");
			montoAmpliacion=quitaFmt( montoAmpliacion );
			montoAmpliacion=parseFloat(montoAmpliacion).toFixed(2);
			if(parseFloat(montoTotal)< parseFloat(montoAmpliacion) ){
				alert("El monto Calendarizado es menor al monto de la ampliación. Los Monto deben de ser iguales.");
				return false;
			}
			if(parseFloat(montoTotal)> parseFloat(montoAmpliacion) ){
				alert("El monto Calendarizado es mayor al monto de la ampliación. Los Monto deben de ser iguales.");
				return false;
			}
			var montoActual=quitaFmtExpresion($("#lblMontoActualPedido").val());
			montoActual=parseFloat(montoActual).toFixed(2);
			var montoMaximo=quitaFmtExpresion($("#lblMontoMaximo").val());
			montoMaximo=parseFloat(montoMaximo).toFixed(2);
			montoTotalMontoActual=parseFloat(montoTotal)+parseFloat(montoActual);
			montoTotalMontoActual=parseFloat(montoTotalMontoActual).toFixed(2);
			
			if( parseFloat(montoTotalMontoActual) > parseFloat(montoMaximo) ){
				alert("El monto total del contrato mas la ampliaci\xF3n supera el m\xE1ximo.");
				return false;
			}
			//En caso que no haya ningun valor en el calendario
			if (vdocren == 1) {
				alert("No se ha Calendarizado ningun Compromiso");
				return false;
			}
			return true;
	    }
	    
		function guardaFolio(j){
			var folioPre=-1;
			var folioCaso=-1;
	    	folioPre=j[0].Folio1;
	    	folioCaso=j[0].Folio2;
	        if(folioPre==-1){
	      		alert("Ha ocurrido un error al crear el caso, contacte a su soporte");
	      		return -1;
	        }else{
	     	   $("#nFolioPreCompromiso").val(folioPre);
	     	   $("#folioCasoPreCompromiso").val(folioCaso);
	     	}
		}
		function setSequenceVal(seqValue) {
			seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
			vcaNoCompromiso = $("#cCentroContable").val() + "CO" + $("#cEjercicio").val() + seqValue;
			
		}
		
		function GuardaIVA(value){
		
			 if(!window.confirm("¿Está seguro que quiere modificar el IVA"))
		 	 return;
		    $("#cIdOficioPrecompromisomod").val(($("#cIdTipoPedido").val())+'-'+($("#cIdUnidadEjecutora").val())+'-'+($("#nIdConsecutivo").val())+'-'+value);
			$("#nIVAmod").val(parseInt($("#nIVA_"+value+"").val(),10));
			queryFormPost("updateIVAPedidoAmpliacion", {async: false});
			alert("Se modifico el IVA ");
		}
		
		
		function quitaFmtExpresion( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val
	}	
		
		function habilitaMeses(oTableLocal){
			var i=1;
			i=parseInt(i,10);
			for(i; i<12;i++){
			oTableLocal.fnSetColumnVis(i,true);
			}
		}
		function ocultarBotones(){
			$("#imgAprobarAmpliacion").hide();
			$("#inpAprobarAmpliacion").hide();
			
			$("#imgDevolverAmpliacionPres").hide();
			$("#inpDevolverAmpliacionPres").hide();
			
			$("#imgAprobarpreCompromisoPed").hide();
			$("#inpAprobarpreCompromisoPed").hide();
			
			$("#imgAprobarCompromisoContAmp").hide();
			$("#inpAprobarCompromisoContAmp").hide();
			
			$("#imgDevolverAmpliacion").hide();
			$("#inpDevolverAmpliacion").hide();
			
			$("#imgEliminarAmpliacion").hide();
			$("#inpEliminarAmpliacion").hide();
		}
		function muestraBotones(){
			ocultarBotones();
			if($("#cEstadoAmpliacion").val()=='CAPTURADO'){
				$("#imgAprobarAmpliacion").show();
				$("#inpAprobarAmpliacion").show();
				$("#imgEliminarAmpliacion").show();
				$("#inpEliminarAmpliacion").show();
			}
			if($("#cEstadoAmpliacion").val()=='EN SAI SIN PRESUPUESTO'){
				$("#imgDevolverAmpliacionPres").show();
				$("#inpDevolverAmpliacionPres").show();
				$("#imgAprobarpreCompromisoPed").show();
				$("#inpAprobarpreCompromisoPed").show();
			}
			if($("#cEstadoAmpliacion").val()=='EN SAI PRESUPUESTADO'){
				$("#imgAprobarCompromisoContAmp").show();
				$("#inpAprobarCompromisoContAmp").show();
				$("#imgDevolverAmpliacion").show();
				$("#inpDevolverAmpliacion").show();
			}
			
		}
		function muestraLineas(){
			queryFormPost("esCucopDeGasolina",  {async : false, 
				callback : function() 
				{
					//Bienes
					if($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()==''){
						$("#divTblLineasBienes").css("display","block");
						$("#divTblLineasServ").css("display","none");
						muestraLineasBienes();
					}else{
						$("#divTblLineasBienes").css("display","none");
						$("#divTblLineasServ").css("display","block");
						muestraLineasServ();
					}
				}
			});
			
		}
		function muestraLineasBienes(){
			var roles="<%=roles%>";
			var qw="1=1 and cIdProcedimiento='"+$("#cIdProcedimiento").val()+"' and cIdRFC='"+$("#cIdRFC").val()+"'" ;
			//if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("JEFES") >= 0) ){
				qw=qw+" and cIdUnidadEjecutoraSolicitud='"+$("#cIdUnidadEjecutoraSolicitud").val()+"'" ;
			//}
			
			oTableLineasBienes = $("#tblLineasBienes").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mPartidaAdjPedCont&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 2, "asc" ]],
				aoColumns: [
					{sName: "cIdProcedimiento",bVisible: false,bSearchable: false},
					{sName: "cIdRFC",bVisible: false,bSearchable: false},
					{sName: "nIdLineaConsolidado"},
					{sName: "descripcion"},
					{sName: "ncantidadMinima"},
					{sName: "ncantidadLineaMax"},
					{sName: "nCantidadDisponible"},
					{sName: "nCantidadAmpliacion"},
					{sName: "cIdConsolidado",bVisible: false,bSearchable: false},
					{sName: "cIdUnidadEjecutoraSolicitud"},
					{sName: "cIdSolicitud",bVisible: false,bSearchable: false}
					
				]
			});
		}
		function muestraLineasServ(){
			var roles="<%=roles%>";
			var qw="1=1 and cIdProcedimiento='"+$("#cIdProcedimiento").val()+"' and cIdRFC='"+$("#cIdRFC").val()+"'" ;
			//if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("JEFES") >= 0) ){
				qw=qw+" and cIdUnidadEjecutoraSolicitud='"+$("#cIdUnidadEjecutoraSolicitud").val()+"'" ;
			//}
			
			oTableLineasServ = $("#tblLineasServ").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mPartidaAdjPedCont&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 2, "asc" ]],
				aoColumns: [
					{sName: "cIdProcedimiento",bVisible: false,bSearchable: false},
					{sName: "cIdRFC",bVisible: false,bSearchable: false},
					{sName: "nIdLineaConsolidado"},
					{sName: "descripcion"},
					{sName: "mMontoNetoMinimoLinea"},
					{sName: "mMontoNetoMaximoLinea"},
					{sName: "mMontoNetoDisponible"},
					{sName: "mMontoNetoAmpliacion"},
					{sName: "cIdConsolidado",bVisible: false,bSearchable: false},
					{sName: "cIdUnidadEjecutoraSolicitud"},
					{sName: "cIdSolicitud",bVisible: false,bSearchable: false}
					
				]
			});
		}
		function muestraDispRadicado(){
			if (!confirm('Si desea activar o desactivar el check se perderan las ep´s agregadas, ¿desea continuar?')) {
				activacheck();
				return;
			}
			queryFormPost("deleteEPAmpliacionContrato",{async:false});
			$("#cuentaDisponible").val('82106');	
			if($('#checkDispRadicado').is(':checked')){
				$("#cuentaDisponible").val('82109');
			}
			recargaTablaAmpliacionDetalle();
			queryFormPost("updateCTAContratoAmpliacion",{async:false});

		}
		function activacheck(){
			$("#checkDispRadicado").attr("checked",false);
			if($("#cuentaDisponible").val()=='82109'){
				$("#checkDispRadicado").attr("checked",true);
			}
		}
		function guardarcomprimisoGeneral(){
			var roles="<%=roles%>";
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0) && !(roles.toString().indexOf("JEFES") >= 0) 
					&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val()) && !($("#U_LOGIN").val()==$("#cIdUsuarioCreacionAmp").val())){
				alert("No tienes Permisos");
				return;
			}else{
				ocultarBotones();
				var proc=""+$("#cEjercicio").val()
					+","+$("#cPedidoDefinitivo").val()
					+","+$("#cIdRFC").val()+"";
				
				$.getJSON("../../servlet/PedidoServlet?operacion=10",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
		                switch(j[0].Col1){
						case "0":
							onSubmit();
						break;
						case "1":  
							alert("Este proveedor no está dado de alta en los beneficiarios");
							$("#cMotivoDevolucion").val("Este proveedor no está dado de alta en los beneficiarios");
						break;
						case "2":  
							alert("Las fechas no respetan el orden");
							$("#cMotivoDevolucion").val("Las fechas no respetan el orden fechaDocumento<fechaFormalización<fechaInicio<=fechaFin");
						break;
						case "3":  
							alert("Los montos no coinciden con la suma total");
							$("#cMotivoDevolucion").val("Los montos no coinciden con la suma total (montoBruto+motoIVA=montoTotal)");
						break;
						
						case "-1":  
							alert("No se pudo realizar la validacion intentelo nuevamente por favor");
							$("#cMotivoDevolucion").val("No se pudo realizar la validacion intentelo nuevamente por favor");
						break;
						
	                 	}
				});
			}
		}
		function onSubmit(){
		  	var mensaje="";
		  //  se valida que existan anticipos y amortizaciones
			try{
				//llama a la aplicacion contable
				$("#tipoOperacion").val('ACTUALIZA');
				mensaje=fnTerminaAplicacionCon();
				if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE")<0)
				{
					//Regresa el Contrato
					$("#nIdEstado").val(3);vEstado=3;
					$("#tipoOperacion").val('RECHAZA');
					$("#cIdContratoH").val($("#cDocumentoDefinitivoAmpliacion").val());
					
					queryFormPost("pa_actualizaEstadoVentanillaPedidoContratoCreate", {async: false });
					queryFormPost('tCompromisoDDelete,tCompromisoEDelete', {async: false });
					return;			
				}
				//Bitácora
				$("#cAccion").val("APRUEBA_PRECOMPROMISO_AMPLIACIÓN");
				$("#cIdDocumento").val($("#cDocumentoDefinitivoAmpliacion").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				alert("Se ha creado el Compromiso con folio "+ $("#FOLIO").val());
				//Refresca la pagina
				location.reload();	
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				//return false;
			}
	  	}
	  	function fnTerminaAplicacionCon(){
	  	//aplicacion contable
	  		var mensajeAp="";
	  		var arrayCompromiso=new Array();
	  		var oper=7;
	  		if ($("#nTipoPago").val() == '1') {
	  			oper=14;
	  		}
	  		$.ajax({url: '../../servlet/PedidoServlet?nFolioPreCompromiso='+$("#nFolioPrecomAmp").val()+"&cEjercicio="+$("#cEjercicio").val()
	  		+"&nFolioPreCompromisoPRCP="+$("#FolioPrecomAmp").val(), 
	  		type:'post' , async: false,data:'operacion='+oper+'&contratoDefinitivo='+$("#cDocumentoDefinitivoAmpliacion").val()+'&origen='+$("#origen").val()
	  		+'&tipoOperacion='+$("#tipoOperacion").val()+'&cuentaDispRadicado='+$("#cuentaDisponible").val()
	  		, dataType: 'json', success:
	  		//$.ajax({url: '../../servlet/PedidoServlet?nFolioCompromiso='+$("#nFolioCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=7', dataType: 'json', success: 
			function(j){
					mensajeAp=j[0].Contable1;
					 $("#FOLIO").val(j[0].FolioCompromiso);
					 arrayCompromiso=j[0].FolioCompromiso.split('-');
					 $("#nFolioCompromiso").val(arrayCompromiso[2]);
					alert(mensajeAp);
				}
			});
			return mensajeAp;
		}
		function onlyNumbers2(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			var strCheck = '0123456789';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true; 
		}
	</script>
	</head>
	<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0"
		topmargin="0">
		<form action="#" name="formPedido">
			<div id="container" class="container" style="text-align: left">
				<fieldset style="width: 750px">
					<legend>
						Datos del Pedido
					</legend>
					<table align="left" cellpadding="2" width="100%">
						<tr>
							<td align="right" colspan="2">
								<img id="imgSalir" src="../imagenes/cancel_round.png"
									style="cursor: pointer"
									onclick="window.location = 'Pedidos.jsp?tab=0';" />
								&nbsp;Salir
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" id="lblUnidadEjecutora"
									name="lblUnidadEjecutora" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblProcedimiento"
									id="lblProcedimiento" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblDefinitivo"
									id="lblDefinitivo" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblPedido"
									id="lblPedido" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblProveedor"
									id="lblProveedor" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblEstado"
									id="lblEstado" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblSubtotal"
									id="lblSubtotal" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr id="divImporteIVA">
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblImporteIVA"
									id="lblImporteIVA" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr id="divImporteImpuesto1">
							<td align="left" colspan="2">
								<input type="text" style="width: 600px"
									name="lblImporteImpuesto1" id="lblImporteImpuesto1" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr id="divImporteImpuesto2">
							<td align="left" colspan="2">
								<input type="text" style="width: 600px"
									name="lblImporteImpuesto2" id="lblImporteImpuesto2" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr id="divImporteImpuesto3">
							<td align="left" colspan="2">
								<input type="text" style="width: 600px"
									name="lblImporteImpuesto3" id="lblImporteImpuesto3" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left" colspan="2">
								<input type="text" style="width: 600px" name="lblTotal"
									id="lblTotal" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset style="width: 750px">
					<legend>
						Ampliaciones
					</legend>
					<table>
						<tr>
							<td align="left">
								Monto M&aacute;ximo:
								<input type="text" style="width: 500px" name="lblMontoMaximo"
									id="lblMontoMaximo" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
						<tr>
							<td align="left">
								Monto Actual del Pedido:
								<input type="text" style="width: 500px"
									name="lblMontoActualPedido" id="lblMontoActualPedido" readonly  value="0.00" style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					</table>
					<div id="divTblLineasServ">
						<table id="tblLineasServ" class="display">
							<thead>
								<tr>
									<th style="display: none;"></th>
									<th style="display: none;"></th>
									<th align="center">Linea<br />Consolidado</th>
									<th align="center">Descripci&oacute;n</th>
									<th align="center">Monto <br /> NetoMinimo</th>
									<th align="center">Monto <br />NetoMaximo</th>
									<th align="center">Monto <br />Por ampliar</th>
									<th align="center">Monto <br /> Ampliación</th>
									<th style="display: none;">cIdConsolidado</th>
									<th align="center">UERequi</th>
									<th style="display: none;">cIdSolicitud</th>
	
								</tr>
							</thead>
						</table>
					</div>
					<div id="divTblLineasBienes" style="display: none;">
						<table id="tblLineasBienes" class="display">
							<thead>
								<tr>
									<th style="display: none;"></th>
									<th style="display: none;"></th>
									<th align="center">Linea<br />Consolidado</th>
									<th align="center">Descripci&oacute;n</th>
									<th align="center">Cantidad <br /> Minima</th>
									<th align="center">Cantidad <br />Maxima</th>
									<th align="center">Cantidad <br />Por Ampliar</th>
									<th align="center">Cantidad <br /> Ampliación</th>
									<th style="display: none;">cIdConsolidado</th>
									<th align="center">UERequi</th>
									<th style="display: none;">cIdSolicitud</th>
	
								</tr>
							</thead>
						</table>
					</div>
					<br />
					<table>
						<tr>
							<td align="right" style="width: 750px;">
								<img id="imgNuevaAmpliacion" src="../imagenes/save.png"
									style="cursor: pointer; width: 15px; height: 15px;"
									onclick="nuevaAmpliacion();" />
								<input id="inpimgNuevaAmpliacion" name="inpimgNuevaAmpliacion"
									size="5" title="Agregar Nueva Ampliación" value="Agregar"
									readonly style="border-width:0; background-color:transparent" />
							
								<img id="imgAprobarAmpliacion"
									src="../imagenes/accept_green.png" style="cursor: pointer"
									onclick="apruebaAmpliacion();" />
									<input id="inpAprobarAmpliacion" name="inpAprobarAmpliacion" size="5" title="Aprovar la Ampliación" value="Aprobar" readonly style="border-width:0; background-color:transparent" /> 
								
								<img id="imgDevolverAmpliacionPres"
									src="../imagenes/arrow_left_blue_round.png"
									style="cursor: pointer" onclick="devuelveContratoDiverso();" />
								<input id="inpDevolverAmpliacionPres" name="inpDevolverAmpliacionPres"
									size="6" title="Devolver la Ampliación" value="Devolver" readonly
									style="border-width:0; background-color:transparent" />
								
								<img id="imgAprobarpreCompromisoPed"
									src="../imagenes/accept_green.png" style="cursor: pointer"
									onclick="guardarPrecompromisoGeneral();" />
								<input	id="inpAprobarpreCompromisoPed" name="inpAprobarpreCompromisoPed"
									size="14" title="Pre-Compromete el recurso de la Ampliación"
									value="Pre-Comprometer" readonly
									style="border-width:0; background-color:transparent" />
									
								<img id="imgAprobarCompromisoContAmp" title="Autorizar pre-compromiso"
									src="../imagenes/accept_green.png"
									style="cursor: pointer;width: 15px; height: 15px;"
									onclick="guardarcomprimisoGeneral();" /> <input
									id="inpAprobarCompromisoContAmp" name="inpAprobarCompromisoCont"
									size="6" title="Autorizar pre-compromiso" value="Autorizar"
									readonly style="border-width:0; background-color:transparent" />
							
								<img id="imgDevolverAmpliacion"
									src="../imagenes/arrow_left_blue_round.png"
									style="cursor: pointer" onclick="devuelvePrecompromiso();" />
								<input id="inpDevolverAmpliacion" name="inpDevolverAmpliacion" size="21"
									title="Devuelve el Pre-Compromiso de la Ampliación"
									value="Devolver-Precompromiso" readonly
									style="border-width:0; background-color:transparent" /> 
							
								<img id="imgEliminarAmpliacion" src="../imagenes/cancel.png"
									style="cursor: pointer; width: 13px; height: 13px;"
									onclick="eliminaAmpliacion();" />
								<input id="inpEliminarAmpliacion" name="inpEliminarAmpliacion" size="5"
									title="Anula la Ampliación" value="Anular" readonly
									style="border-width:0; background-color:transparent" />
							
							</td>
						</tr>
						
					</table>
					<br>
					<div id="dtblAmpliaciones">
						<table id="tblAmpliaciones" border="2" class="display" width="800"
							height="50">
							<thead>
							<tr>
								<th>&nbsp;</th>
								<th>Oficio</th>
								<th>Estado</th>
								<th>Cantidad_Ampl</th>
								<th>Monto_Ampl</th>
								<th>Usuario_Creador</th>
								<th>Usuario_Cancela</th>

								<th style="display: none;"></th>
								<th style="display: none;"></th>
								<th style="display: none;"></th>
							</tr>
						</thead>
						</table>
					</div>
				</fieldset>
				<fieldset style="width: 750px" id="dtblDetalleAmpliacionesMaster">
					<legend>
						Detalle Ampliaciones
					</legend>
					<div id="dtblDetalleAmpliaciones">
						<br />
						<table>
							<tr>
	   						<td align="left"  colspan="2">
	   						<label>Disponible Radicado:</label> <input type="checkbox" name="checkDispRadicado" id="checkDispRadicado" onclick="muestraDispRadicado()" />
	   						</td>
	   					</tr>
							<tr>
								<td>
									Clave:
								</td>
								<td>
									<input type="text" name="ep" id="ep" size="60" />
									<input type="button" name="nIdClaveEP" id="nIdClaveEP" size="5"
										value="..." onclick="buscaClaveEP();" onblur="">
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<input type="button" name="btnAgregarAmpliacionDetalle"
										id="btnAgregarAmpliacionDetalle" size="5" value="Agregar"
										onClick="validarGuardaAmpliacionDetalle();">
									<input type="button" name="btnEditarAmpliacionDetalle"
										id="btnEditarAmpliacionDetalle" size="5" value="Editar"
										onClick="editCeldasDetalleAmpliaciones();">
								</td>
							</tr>
						</table>
						<br />
						<div>
							<table id="tblDetalleAmpliaciones" border="2" class="display"
								width="800" height="50">
								<thead>
									<tr>
										<th>
											#
										</th>
										<th>
											EP
										</th>
										<th>
											Enero
										</th>
										<th>
											Febrero
										</th>
										<th>
											Marzo
										</th>
										<th>
											Abril
										</th>
										<th>
											Mayo
										</th>
										<th>
											Junio
										</th>
										<th>
											Julio
										</th>
										<th>
											Agosto
										</th>
										<th>
											Septiembre
										</th>
										<th>
											Octubre
										</th>
										<th>
											Noviembre
										</th>
										<th>
											Diciembre
										</th>
										<th>
											&nbsp;
										</th>
										<th>
											&nbsp;
										</th>
										<th>
											&nbsp;
										</th>
									</tr>
								</thead>
							</table>
						</div>
						<br>
						<div id="dtblSuficienciaDetalleAmpliaciones">
							<table id="tblSuficienciaDetalleAmpliaciones" border="2"
								class="display" width="800" height="50">
								<thead>
									<tr>
										<th>
											&nbsp;
										</th>
										<th>
											Estructura Program&aacute;tica
										</th>
										<!--<th>Clave Interna</th>-->
										<th>
											Enero
										</th>
										<th>
											Febrero
										</th>
										<th>
											Marzo
										</th>
										<th>
											Abril
										</th>
										<th>
											Mayo
										</th>
										<th>
											Junio
										</th>
										<th>
											Julio
										</th>
										<th>
											Agosto
										</th>
										<th>
											Septiembre
										</th>
										<th>
											Octubre
										</th>
										<th>
											Noviembre
										</th>
										<th>
											Diciembre
										</th>
										<th>
											Anual
										</th>
										<th>
											Contrato
										</th>
									</tr>
								</thead>
							</table>
						</div>
						<br>
						<div id="ddt_grabaprecompE">
							<table id="dt_grabaprecompE">
								<thead>
									<tr align="center">
										<th>
											nFolioPreCompromisoE
										</th>
										<th>
											aEjercicioFiscal
										</th>
										<th>
											fCarga
										</th>
										<th>
											fAplicacion
										</th>
										<th>
											cIdContrato
										</th>
										<th>
											cTipoContrato
										</th>
										<th>
											cCentroContable
										</th>
										<th>
											cRamo
										</th>
										<th>
											cUnidadResponsable
										</th>
										<th>
											caNoPreCompromiso
										</th>
										<th>
											nEnviadoSICOP
										</th>
										<th>
											nMes
										</th>
										<th>
											fVigencia
										</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="text" id="nFolioPreCompromisoE"
												name="nFolioPreCompromisoE">
										</td>
										<td>
											<input type="text" id="aEjercicioFiscal"
												name="aEjercicioFiscal">
										</td>
										<td>
											<input type="text" id="fCarga" name="fCarga">
										</td>
										<td>
											<input type="text" id="fAplicacion" name="fAplicacion">
										</td>
										<td>
											<input type="text" id="cIdContrato" name="cIdContrato">
										</td>
										<td>
											<input type="text" id="cTipoContrato" name="cTipoContrato">
										</td>
										<td>
											<input type="text" id="cCentroContable"
												name="cCentroContable">
										</td>
										<td>
											<input type="text" id="cRamo" name="cRamo" value="16">
										</td>
										<td>
											<input type="text" id="cUnidadResponsable"
												name="cUnidadResponsable">
										</td>
										<td>
											<input type="text" id="caNoPreCompromiso"
												name="caNoPreCompromiso">
										</td>
										<td>
											<input type="text" id="nEnviadoSICOP" name="nEnviadoSICOP">
										</td>
										<td>
											<input type="text" id="nMes" name="nMes">
										</td>
										<td>
											<input type="text" id="fVigencia" name="fVigencia">
										</td>
									</tr>
								</tbody>
							</table>
						</div>
						<br>
						<div id="ddt_grabaprecompD">
							<table id="dt_grabaprecompD">
								<thead>
									<tr align="center">
										<th>
											nDocRenglon
										</th>
										<th>
											EP
										</th>
										<th>
											cEvento
										</th>
										<th>
											mImporte
										</th>
										<th>
											mImporteNegativo
										</th>
										<th>
											nFolioPreCompromisoD
										</th>
										<th>
											cMes
										</th>
										<th>
											cCentroContable
										</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>


				</fieldset>

				<div id="ddt_urcc" style="width: 300px; height: 50px;">
					<table id="dt_urcc" width="50px">
						<thead style="width: 50px">
							<tr align="center" style="width: 50px">
								<th>
									CentroContable
								</th>
								<th>
									UnidadEjecutora
								</th>
							</tr>
						</thead>
					</table>
				</div>
				
				<!-- Hiddens de sesion -->
				<!-- El IVA del Contrato -->
				<input type="hidden" id="nIVA" name="nIVA" value=""/>
				<!-- El IVA modificado para mContratoAmpliacion -->
				<input type="hidden" id="nIVAmod" name="nIVAmod" value=""/>
				<!-- El IVA modificado para mContratoAmpliacion -->
				<input type="hidden" name="cIdOficioPrecompromisomod" id="cIdOficioPrecompromisomod" />
				<!-- Hiddens de sesion -->
				<input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido"
					value="<%=cIdTipoPedido%>" />
				<input type="hidden" name="cIdUnidadEjecutora"
					id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
				<input type="hidden" name="cEjercicio" id="cEjercicio"
					value="<%=cEjercicio%>" />
				<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo"
					value="<%=nIdConsecutivo%>" />
				<input type="hidden" name="cIdPedido" id="cIdPedido" />
				<input type="hidden" name="cIdRFC" id="cIdRFC" />
				<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
				<input type="hidden" id="Partida" name="Partida" value="2" />
				<input type="hidden" id="Partida2" name="Partida2" value="3" />
				<input type="hidden" id="Partida3" name="Partida3" value="5" />
				<input type="hidden" id="Partida1" name="Partida1" value="1" />
				<!--  Auxiliares para consulta -->
				<input type="hidden" name="U_LOGIN" id="U_LOGIN"
					value="<%=usuarioTab.getLogin()%>" />
				<input type="hidden" name="R_NOMBRE" id="R_NOMBRE">
				<input type="hidden" name="nIdEstadoPed" id="nIdEstadoPed" />
				<input type="hidden" name="nTipoPago" id="nTipoPago" />
				<input type="hidden" name="nIdAmpliacion" id="nIdAmpliacion" />
				<input type="hidden" name="nIdAmpliacionDetalle"
					id="nIdAmpliacionDetalle" />
				<input type="hidden" name="cEstadoAmpliacion" id="cEstadoAmpliacion" />
				<input type="hidden" name="cMontoTem" id="cMontoTem" />
				<input type="hidden" name="nIdAmpliacionDetalleTem"
					id="nIdAmpliacionDetalleTem" />
				<input type="hidden" name="nIdEstadoAmpliacion"
					id="nIdEstadoAmpliacion" />
				<input type="hidden" name="mesDisponible" id="mesDisponible"
					value="1" />
				<input type="hidden" name="mImporteTotal" id="mImporteTotal"
					value="0">
				<input type="hidden" name="mComprometido" id="mComprometido"
					value="0" />
				<input type="hidden" name="difPrecompromiso" id="difPrecompromiso"
					value="0" />
				<input type="hidden" name="GP_NOMBRE" id="GP_NOMBRE"
					value="desfase_mes_activo_precompromiso" />
				<input type="hidden" name="GP_VALOR" id="GP_VALOR" />
				<input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" />
				<input type="hidden" id="nFolioPreCompromiso"
					name="nFolioPreCompromiso" value="-1">
				<input type="hidden" id="cMontoContratoDiverso"
					name="cMontoContratoDiverso">
				<input type="hidden" id="cDescripcionContratoDiverso"
					name="cDescripcionContratoDiverso">
				<input type="hidden" id="cIdOficioPrecompromiso"
					name="cIdOficioPrecompromiso">
				<input type="hidden" name="responsable" id="responsable" />
				<input type="hidden" name="operacion" id="operacion" />
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
				<input type="hidden" id="cDocumento" name="cDocumento"
					value="PRECOMPROMISO">
				<input type="hidden" name="folioCasoPreCompromiso"
					id="folioCasoPreCompromiso" />
				<input type="hidden" name="foliosCasoPreCompromiso"
					id="foliosCasoPreCompromiso" />
				<input type="hidden" name="cTipoDocumento" id="cTipoDocumento" />
				<input type="hidden" id="cIdEntidadContable1" name="cIdEntidadContable1" value="<%=cCentroContable%>">

				<!--  Auxiliares para actualizar la tabla de mDocumentoFolio -->
				<input type="hidden" name="cDocumentoDefinitivo"
					id="cDocumentoDefinitivo" />
				<input type="hidden" name="cIdUsuarioCreacion"
					id="cIdUsuarioCreacion" />
				
				
				<input type="hidden" name="unidadEjecutoraOriginal"
					id="unidadEjecutoraOriginal" />	
					
					<input type="hidden" name="entidadContableOriginal"
					id="entidadContableOriginal" />	
					
				<input type="hidden" name="cAccion" id="cAccion" value="" />
				<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>" />
				<input type="hidden" name="nIdEstado" id="nIdEstado" />
				<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />	
				<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento"
				value="" /> <input type="hidden" name="esCucopGasolina"
				id="esCucopGasolina" value="" /> <input type="hidden"
				name="cadenaLineaConsCant" id="cadenaLineaConsCant" value="" /> <input
				type="hidden" name="cadenaLineaCantidad" id="cadenaLineaCantidad"
				value="" /> <input type="hidden" name="cIdUnidadEjecutoraSolicitud"
				id="cIdUnidadEjecutoraSolicitud" value="<%=cUR%>" />
				<input type="hidden" name="nIdConsecutivoProcedimiento"
				id="nIdConsecutivoProcedimiento" value="" />
				<input type="hidden" id="mMontoAmpliacion" name="mMontoAmpliacion" value="" />
				<input type="hidden" name="nCantidadAmpliacion" id="nCantidadAmpliacion" value="" />
				<input type="hidden" name="cIdUsuarioCreacionAmp" id="cIdUsuarioCreacionAmp" value="" />
				<input type="hidden" id="nFolioPrecomAmp" name="nFolioPrecomAmp" value="" /> 
				<input type="hidden" id="FolioPrecomAmp" name="FolioPrecomAmp" value="" />
				<input type="hidden" name="cDocumentoDefinitivoAmpliacion" id="cDocumentoDefinitivoAmpliacion" />
				<input type="hidden" id="origen" name="origen" value="PEDIDO AMPLIACION" />
				<input type="hidden" name="tipoOperacion" id="tipoOperacion" />
				<input type="hidden" name="FOLIO" id="FOLIO" />
				<input type="hidden" name="nFolioCompromiso" id="nFolioCompromiso" />
				<input type="hidden" name="cIdDocumento" id="cIdDocumento" value="" />
			</div>
		</form>
	</body>
</html>
