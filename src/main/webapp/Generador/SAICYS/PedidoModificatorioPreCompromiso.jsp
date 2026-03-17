<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	Calendar c2= Calendar.getInstance();
	c2.add(Calendar.DATE, 8);
	String vigencia=sdf.format(c2.getTime());
	String today= sdf.format(c1.getTime());
	int mesActual = c1.getTime().getMonth()+1;//Porque empieza en 0: Enero
	String cCentroContable="";
	String cUR = "";
	String cRamo = "";
	boolean bAplicadoCont=false;
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuario.getLogin();
	String idRol="0";		
	Map rol =usuario.getRoles();
	if (usuario == null) {
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
		response.sendRedirect("PedidosModificatorio.jsp?tab=0");
	//Valida Centro de Costos
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
        cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Genera Pre-Compromiso</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<%-- script type="text/javascript" src="js/jquery-1.0.4.pack.js"></script--%>
	<script type="text/javascript" src="../js/jquery.jeditable-1.6.2.js"></script>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.editable-1.3.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/FixedColumns.js"></script>
<script type="text/javascript" charset="utf-8">
	var vcontrato;
	var vfolio;
	var vEstado;
	var vVigente=1;  //temporal se deshabilita la vigencia de 8 dias
	var vcaNoCompromiso;
	var oTableUrcc;
	$(document).ready(function() {
			<%
			    String roles="";
				//botones
				NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int editar=0;
				int imgAprobar=0;
				int imgDevolver=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"PedidoModificatorio","precompromisoPedidoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarpreCompromisoPedMod".equals(img)){  
						%>$("#<%=img%>").attr("disabled", false);<%
						imgAprobar=1;
					}
					if ("imgDevolverpreCompromisoPedMod".equals(img)){
						%>$("#<%=img%>").attr("disabled", false);<%
						imgDevolver=1; 
					}
					if ("trEditar".equals(img)){
						editar=1; 
					}
				}
			%>
	
	    var nEditing = null;
		initTables();
		setReadOnly();
		//Esconde el letrero de apartado encontrado
		$("#apartadoNotice").hide();
		//Carga los datos de inicio
		initQueries();
		//Guarda en variables globales el contrato, folio, estado y si es vigente
		vcontrato = $("#cPedidoDefinitivo").val();
		vfolio = $("#nFolioPreCompromiso").val();
		vEstado = parseInt($("#nIdEstado").val(),10);
		
		setInitConditions();
		//Carga la tabla que tiene el monto de cada EP
		$("#mComprometido").formatCurrency();
		$("#mImporteTotal").formatCurrency();
		$("#difPrecompromiso").formatCurrency();
		var offset = parseInt($("#mesDisponible").val(),10) + parseInt($("#GP_VALOR").val(),10);
		if(offset > 12 || offset < 0){
			alert("El desfase del calendario está mal configurado, favor de avisar al administrador");
			window.location ="PedidosModificado.jsp?tab=0";
		}else
			$("#mesDisponible").val(offset);
			//Solo se puede precomprometer para el mes siguiente al actual
	});
	
	function setInitConditions(){
		if(vEstado > 2 || vVigente == 0){
			//Se deshabilita el boton de aprobar en caso que ya exista un precompromiso
			document.getElementById("imgAprobarpreCompromisoPedMod").disabled = true;
		}
		if(vEstado != 3){
			//En caso el estado del pedido sea aprobado, falta por confirmar que hacer en los otros casos
			document.getElementById("imgDevolverpreCompromisoPedMod").disabled = true;
		}
		if(vVigente == 0){
			alert("No es posible crear más precompromisos porque todavía exiten precompromisos del área caducos");
		}

		queryFormPost("mPedidoModicadoChecaRolUsuario", {async: false});
		queryFormPost("mPedidoModicadoUsuarioCreacionOriginalRead",{async: false });
	}
	
	function initQueries(){
		//Lee los datos de la cabecera
		queryFormPost("mPedidoHeaderRead", {async: false});
		queryFormPost("mPedidoModificadoTotales", {async: false});
		$("#mImporteTotal").val($("#totalMod").val());
		
		//Para ver si tiene apartados en nApartadosUsados
		queryFormPost("fn_mApartadoPedidoModificadoCuenta", {async: false} );
		
		//Esconde el letrero de apartado encontrado
		$("#apartadoNotice").hide();
		//Si existe al menos un apartado, consulta el monto total - apartado disponible y lo pone en monto real
		if ($("#nApartadosUsados").val() > 0){
			queryFormPost("fn_mApartadoPedidoModificadoTotal", {async: false} );
			
			//Si el valor del apartado disponible es > 0
			if ($("#mApartadoReal").val() > 0){
				$("#mApartadoReal").formatCurrency();
				$("#aptdReal").html($("#mApartadoReal").val());
				$("#apartadoNotice").show();
				//Actualiza el 'Monto Contrato:'
				$("#mImporteTotal").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mApartadoReal").val()));
				if (parseInt($("#mImporteTotal").val(),10) < 0) {
					$("#mImporteTotal").val(0);
				}
			}
		}
		
		//Actualiza la diferencia entre el importe(montoMod - montoApartado) y el comprometido(siempre es 0)
		$("#difPrecompromiso").val(quitaFmt($("#mImporteTotal").val()) - quitaFmt($("#mComprometido").val()));
		
		//Ejecuta una función en la base de datos para saber si la unidad ejecutora no tiene Precompromisos pendientes por entregar documentacion
		//queryFormPost("fn_vigenciaValida", {async: false});
		queryFormPost("TipoPolizaRead", {async: false});
		//Lee la configuración de mes
		queryFormPost("CG_GRUPO_PROPIEDADESRead", {async: false});
		if ($("#tipoMod").val() != 1){
			document.getElementById("reduccionPedidoMod").disabled = true;
		}
		
	}
	
	function actualizaCaratula(){
		queryFormPost("mPedidoHeaderRead", {async: true});
		queryFormPost("mPedidoModificadoTotales", {async: false});
	}
	
	function setReadOnly(){
		document.getElementById("lblUnidadEjecutora").style.readonly = true;
		document.getElementById("lblProcedimiento").style.readonly = true;
		document.getElementById("lblDefinitivo").style.readonly = true;
		document.getElementById("lblPedido").style.readonly = true;
		document.getElementById("lblProveedor").style.readonly = true;
		document.getElementById("lblEstadoMod").style.readonly = true;
		document.getElementById("lblTotalAnterior").style.readonly = true;
		document.getElementById("lblTotalModificado").style.readonly = true;
		document.getElementById("lblTotal").style.readonly = true;		
	}
	
	function initTables(){
		$('#dt_grabaprecompD').dataTable({
		"iDisplayLength": 20,
     	"bPaginate": false,
     	"bFilter": false,
     	"bSort": false,
     	"bInfo": false } );
		$('#dt_grabaprecompD').attr('visible', false);
		
		loadURCC();
		$('#dt_urcc').attr('visible', false);
	}
	
	function loadURCC(){
		var campos = "'" + $("#cPedidoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mUrccPedidoModificado(" + campos + ")",
				aoColumns: [
					{ sName: "centroContable"},
					{ sName: "ur" }
				]
		}) ;
	}
	
	function guardarPrecomprimisoGeneral(){
		var  imgAprobar='<%=imgAprobar%>';
		if(imgAprobar==0){
			//Condiciones para guardar un precompromiso
			//Se tiene que precomprometer por el monto total
			if($("#mImporteTotal").val() != $("#mComprometido").val()){
				alert("El importe total es mayor al monto pre-comprometido");
				return -1;
			}

			if ($("#usuarioLoginRole").val() != 'ADMIN_RECMAT') { 
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					alert("No tiene permisos para realizar esta accion");
					return;
				}
			}
			
			if ($("#nTipoPago").val() == '1') {
				var aTrs = oTableUrcc.fnGetNodes();
				for (var i = 0; i < aTrs.length; i++) {
					line = oTableUrcc.fnGetData(aTrs[i])
					var cc = line[0];
					var ur = line[1];
					guardarPrecompromisoDes(cc, ur);
				}
				
				$("#nIdEstado").val(3);
				vEstado=3;
				queryFormPost("mPedidoModificadoPrecompromisoDesUpdate", {async: false });
			}
			else{
				guardarPrecompromiso();
				$("#nIdEstado").val(3);
				vEstado=3;
				queryFormPost("mPedidoModificadoPrecompromisoUpdate", {async: false });
			}
			
			document.getElementById("imgAprobarpreCompromisoPedMod").disabled = true;
			document.getElementById("imgDevolverpreCompromisoPedMod").disabled = false;
			actualizaCaratula();
			alert("Pedido Modificatorio Precomprometido con folio(s) " + $("#foliosCasoPreCompromiso").val());
			$("#foliosCasoPreCompromiso").val('');
			
		}else{
			alert("No tiene permisos para realizar esta acción");
		}
	}
	
	function guardarPrecompromisoDes(entidadContable, ur){		
		//Genera un nuevo folio en caso de que no lo tenga
		$.ajax({url: '../../servlet/PedidoModificadoServlet' , type:'post' , async: false,data:'operacion=2&ur='+ur, dataType: 'json', success: guardaFolio});
		
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val()==0)
			return -1;
		
		//Guarda en una tabla auxiliar el encabezado del compromiso
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 ) ;
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato + '#M' + $("#cConsecutivoMod").val());
		$("#cTipoContrato").val( "DI" );
		$("#cCentroContable").val(entidadContable); 
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val(ur);
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 
		$("#fVigencia").val( "<%=vigencia%>" );
		
		getNextSequenceVal({seqName: "CO-" + entidadContable, async: false, callback: setSequenceVal});
		$("#caNoPreCompromiso").val(vcaNoCompromiso);
		
		//Genera Encabezado y detalle
		queryFormPost('tPreCompromisoECreate', {async: false });
		
		//Si exste un apartado, se agregan a Detalle las operaciones pertinentes
		if ($("#nApartadosUsados").val() > 0 && quitaFmt($("#mApartadoReal").val()) > 0){
			queryFormPost("pa_mAptdPrcpDetalleMod", {async: false });
		}
		
		//Actualiza la tabla de pedido con los folio y el nuevo estado
		queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
		queryFormPost("mDocumentoFolioPrecompromisoCreate", {async: false });
		
		$.ajax({url: '../../servlet/PedidoModificadoServlet' , type:'post' , async: false,data:'operacion=3', dataType: 'json', success: imprimeRes});
		
		if ($("#foliosCasoPreCompromiso").val() != '') $("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
    }
	
	function guardarPrecompromiso(){		
		//Genera un nuevo folio en caso de que no lo tenga
		if($("#nFolioPreCompromiso").val()==""){
			$.ajax({url: '../../servlet/PedidoModificadoServlet' , type:'post' , async: false,data:'operacion=2', dataType: 'json', success: guardaFolio});
		}
		
		//En caso de cualquier error al obtener el folio, sale de la función
		if($("#nFolioPreCompromiso").val()==0)
			return -1;
		
		//Guarda en una tabla auxiliar el encabezado del compromiso
		
		var nMes = "<%=today%>";
		var nMes = nMes.substring(5, 7 ) ;
		$("#nFolioPreCompromisoE").val( $("#nFolioPreCompromiso").val());
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( "<%=today%>" ); 
		$("#fAplicacion").val("<%=today%>" ); 
		$("#cIdContrato").val(vcontrato + '#M' + $("#cConsecutivoMod").val());
		$("#cTipoContrato").val( "DI" );
		$("#cCentroContable").val( "<%=cCentroContable%>"); 
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 
		$("#fVigencia").val( "<%=vigencia%>" );
		
		getNextSequenceVal({seqName: "CO-" + "<%=cCentroContable%>", async: false, callback: setSequenceVal});
		$("#caNoPreCompromiso").val(vcaNoCompromiso);
		
		//Genera Encabezado y detalle
		queryFormPost('tPreCompromisoECreate', {async: false });
		
		//Si exste un apartado, se agregan a Detalle las operaciones pertinentes
		if ($("#nApartadosUsados").val() > 0 && quitaFmt($("#mApartadoReal").val()) > 0){
			queryFormPost("pa_mAptdPrcpDetalleMod", {async: false });
		}
		
		//Actualiza la tabla de pedido con los folio y el nuevo estado
		queryFormPost("tPreCompromisoEncabezadoUpdate", {async: false });
		
		$.ajax({url: '../../servlet/PedidoModificadoServlet' , type:'post' , async: false,data:'operacion=3', dataType: 'json', success: imprimeRes});
		
		if ($("#foliosCasoPreCompromiso").val() != '') $("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + ', ');
		$("#foliosCasoPreCompromiso").val($("#foliosCasoPreCompromiso").val() + $("#folioCasoPreCompromiso").val());
    }
    
	function devuelvePrecompromiso(){
		var imgDevolver='<%=imgDevolver%>';
		if(imgDevolver==0){

			if ($("#usuarioLoginRole").val() != 'ADMIN_RECMAT') { 
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					alert("No tiene permisos para realizar esta accion");
					return;
				}
			}
			
			////Vuelve a leer el estado del pedido para evitar conflictos con ventanilla y saber si el pedido fue aprobado
			queryFormPost("mPedidoModificadoTotales", {async: false});
			if($("#nIdEstado").val()==("2")){
				alert("El modificatorio ha sido rechazado por el usuario de ventanilla");
				//se tiene que actualizar co_responsable en la tabla cg_caso_operacion para que ya no aparezca en el inbox
				queryFormPost("actualizaCoResponsableRead", {async: false});
				//actualizaCaratula();
				window.location = "PedidoModificatorio.jsp?tab=" + 5;
				return;
			}
			//primero checamos si el pedido ya esta siendo trabajado por el usuario de ventanilla
			queryFormPost("mPedidoModicadoUsuarioVentanillaRead", {async: false});
			if($("#operacion").val() == 2 && $("#responsable").val() != "VENTANILLA_PRECOMPROMISO") {
				alert("No se puede eliminar el precompromiso, el pedido lo esta revisando el usuario de ventanilla "+ ' '+$("#responsable").val());
				return;
			}		 
					
			if($("#nIdEstado").val() != ("4")){
				//Elimina el Precompromiso
				if(window.confirm("¿Está seguro que quiere eliminar el pre-compromiso, el modificatorio se encuentra en VENTANILLA DE PAGOS y esta pendiente su aprobación, esta acción no se puede deshacer?")){
					$.ajax({url: '../../servlet/PedidoModificadoServlet' , type:'post' , async: false,data:'operacion=4&tipoPago=' + $("#nTipoPago").val(), dataType: 'json', success: eliminaPrecompromiso});
				}
				else {
					return;
				}	
			}
			else{
				actualizaCaratula();
				alert("El modificatorio no se puede devolver porque ya ha sido APROBADO y esta COMPROMETIDO");
			}
		}else{
			alert("No tiene permisos para realizar esta acción, contacte a su administrador");
		}
		
	}
	function eliminaPrecompromiso(j){
		var mensaje=j[0].Contable1;
		if(mensaje != ""){
			if ($("#nTipoPago").val() == '0') {
				queryFormPost('tPreCompromisoDDelete', {async: false });
				queryFormPost('tPreCompromisoEDelete', {async: false });
			}
			else {
				queryFormPost('tPreCompromisoDDesDelete', {async: false });
				queryFormPost('tPreCompromisoEDesDelete', {async: false });
				queryFormPost('mDocumentoFolioPrecompromisoDelete', {async: false });
			}
			//Regresa el estado a en SAI sin Precomprometer
			$("#nIdEstado").val(2);
			vEstado = 2;
			queryFormPost("mPedidoModificadoPrecompromisoDevuelveUpdate", {async: false });
			document.getElementById("imgAprobarpreCompromisoPedMod").disabled = false;
			document.getElementById("imgDevolverpreCompromisoPedMod").disabled = true;
			actualizaCaratula();
			alert(mensaje);
			window.location = "PedidoModificatorio.jsp?tab=" + 5;
		}
	}
	
	function imprimeRes(j){
		if ($("#nTipoPago").val() == '0') {
			var res=j[0].Contable1;
			alert(res);
		}
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
	
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode
		var strCheck = '-0123456789.';
		var key = String.fromCharCode( keyPressed );
		if (strCheck.indexOf( key ) == -1)
			return false; // Valida que sea numero y punto decimal
		return true 
	}
	
	function setSequenceVal(seqValue) {
		seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
		vcaNoCompromiso = $("#cCentroContable").val() + "CO" + $("#cEjercicio").val() + seqValue;
	}
	
</script>
</head>
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
<form>
	<div id="container" class="container">
		<table align="left" width="750px">
			<tr>
				<td>
					<fieldset style="width:750px" align="left">
						<legend>Pre-Compromiso del Pedido</legend>
						<table align="left" cellpadding="2" width="100%">
							<tr>
					    		<td align="right" colspan="2">
					    			<img id="imgAprobarpreCompromisoPedMod" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="guardarPrecomprimisoGeneral();" />&nbsp;Pre-Comprometer
					    			<img id="imgDevolverpreCompromisoPedMod" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devuelvePrecompromiso();"/>&nbsp;Devolver
									<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="window.location = 'Pedidos.jsp?tab=0';"/>&nbsp;Salir
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
				<td align="left">
					<table>
						<tr>
							<td align="left">Monto Contrato: <input style="text-align:right;" readonly type="text" maxlength="20" size="12" name="mImporteTotal" id="mImporteTotal" value="0" disabled="disabled"></td>
							<td align="left">Compromiso Actual: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mPreComprometer" id="mComprometido" value="0" disabled="disabled"></td>
							<td align="left">Saldo Compromiso: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mPreComprometido" id="difPrecompromiso" value="0" disabled="disabled"></td>
						</tr>
						<tr id="apartadoNotice">
							<td colspan="3"><span style="color:red;">Monto cubierto por el apartado: <span id="aptdReal"></span>.</span></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table id="dt_grabaprecompE" style="visibility: hidden">
						<thead>
							<tr align="center">
								<th>nFolioPreCompromisoE</th>
								<th>aEjercicioFiscal</th>
								<th>fCarga</th>
								<th>fAplicacion</th>
								<th>cIdContrato</th>
								<th>cTipoContrato</th>
								<th>cCentroContable</th>
								<th>cRamo</th>
								<th>cUnidadResponsable</th>
								<th>caNoPreCompromiso</th>
								<th>nEnviadoSICOP</th>
								<th>nMes</th>
								<th>fVigencia</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" id="nFolioPreCompromisoE" name="nFolioPreCompromisoE"></td>
								<td><input type="text" id="aEjercicioFiscal" name="aEjercicioFiscal"></td>
								<td><input type="text" id="fCarga" name="fCarga"></td>
								<td><input type="text" id="fAplicacion" name="fAplicacion"></td>
								<td><input type="text" id="cIdContrato" name="cIdContrato"></td>
								<td><input type="text" id="cTipoContrato" name="cTipoContrato"></td>
								<td><input type="text" id="cCentroContable" name="cCentroContable"></td>
								<td><input type="text" id="cRamo" name="cRamo" value="16"></td>
								<td><input type="text" id="cUnidadResponsable" name="cUnidadResponsable"></td>
								<td><input type="text" id="caNoPreCompromiso" name="caNoPreCompromiso"></td>
								<td><input type="text" id="nEnviadoSICOP" name="nEnviadoSICOP"></td>
								<td><input type="text" id="nMes" name="nMes"></td>
								<td><input type="text" id="fVigencia" name="fVigencia"></td>
							</tr>
						</tbody>
					</table>
					
					<div style="width:0px; height: 0px;" >
					<table id="dt_urcc" style="visibility: hidden; width: 0px;" width="0px" >
						<thead style="width:0px" >
							<tr align="center" style="width:0px" >
								<th >CentroContable</th>
								<th >UnidadEjecutira</th>
							</tr>
						</thead>
					</table>
					</div>
				</td>
			</tr>
		</table>
				
				<!-- Hidden Section -->
				<!-- Valores de caratula -->
				<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    	<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    	<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		    	<input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
		    	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin() %>"/>
		    	<input type="hidden" name="cPedido" id="cPedido" value="<%=cIdPedido%>" />
		    	<input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" value="<%=cIdPedidoDefinitivo%>" />
		    	<input type="hidden" name="pedidoDefinitivo" id="pedidoDefinitivo" value="<%=cIdPedidoDefinitivo%>" />
		    	<input type="hidden" name="cDocumentoDefinitivo" id="cDocumentoDefinitivo" value="<%=cIdPedidoDefinitivo + "#M" + cIdConsecutivoMod%>" />
		    	<input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
			    <input type="hidden" name="mesDisponible" id="mesDisponible" value="<%=mesActual%>"/>
			    <input type="hidden" name="tipoMod" id="tipoMod" />
		        <input type="hidden" name="totalAnterior" id="totalAnterior" />
			    <input type="hidden" name="totalMod" id="totalMod" />
			    <input type="hidden" name="totalNuevo" id="totalNuevo" />
			    <input type="hidden" id="cDocumento" name="cDocumento" value="PRECOMPROMISO">
			    <input type="hidden" name="nIdEstado" id="nIdEstado" />
			    <input type="hidden" name="nTipoPago" id="nTipoPago" />
			    
			    <!-- Para unir apartado con precompromiso -->
			    <input type="hidden" name="nApartadosUsados" id="nApartadosUsados" />
			    <input type="hidden" name="mApartadoReal" id="mApartadoReal" />
			    
			    <!-- Valores de Precompromiso -->
			    <input type="hidden" name="folioCasoPreCompromiso" id="folioCasoPreCompromiso" />
			    <input type="hidden" name="foliosCasoPreCompromiso" id="foliosCasoPreCompromiso" />
				<input type="hidden" name="cIdUsuarioResponsable" id="cIdUsuarioResponsable" value="Usuario">
				
				<input type="hidden" id="mImporteAnticipo" name="mImporteAnticipo" value="0">
				<input type="hidden" id="mImporteAnticipoIVA" name="mImporteAnticipoIVA" value="0">
				<input type="hidden" id="mTotalAnticipo" name="mTotalAnticipo" value="0">	
				<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
				<input type="hidden" id="nFolioPreCompromiso" name="nFolioPreCompromiso" value="-1">
				<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza" value="REGISTRO DEL PRECOMPROMISO FOLIO">
				
				<!-- Valores de Caso -->
				<input type="hidden" name="ID_CASO" id="ID_CASO" />
				<input type="hidden" name="ID_TC" id="ID_TC" />
				<input type="hidden" name="ID_CASO_OPER" id="ID_CASO_OPER" />
				
				<!--  Valores de Configuración -->
				<input type="hidden" name="GP_NOMBRE" id="GP_NOMBRE" value="desfase_mes_activo_precompromiso"/>
				<input type="hidden" name="GP_VALOR" id="GP_VALOR" />
				<input type="hidden" name="responsable" id="responsable" />
				<input type="hidden" name="operacion" id="operacion" />
				<input type="hidden" name="tipoCaso" id="tipoCaso" value="14" />
				
				<input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  				<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
  				<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
		</div>
		
	</form>
</body>
</html>