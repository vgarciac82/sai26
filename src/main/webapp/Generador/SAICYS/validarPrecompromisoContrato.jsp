<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	String cCentroContable="";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
	    cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null) {
		//response.sendRedirect("../index.jsp");
		//return;
	}
	String folio=c.getFolio();
	int nfolio=Integer.parseInt(folio.substring(folio.lastIndexOf('-')+1));
		
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Ventanilla PreCompromiso</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
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
	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" charset="utf-8">
	var vcontrato, vfolio,EP, vAvanzo;
	var oTableCalendario,oTableEP,oTableRetencion;
	var cambio=true;
	var calendarioConsultado=false;
	$(document).ready(function() {
		//Todos los tabs están en un mismo JSP
		var $tabs=$(".tabs").tabs();
		$tabs.tabs('select', 0);
		initQueries();
		
		//Concatena el proveedor
		//$("#cProveedor").val("RFC: "+$("#dRFC").val()+" Razón Social: "+$("#cRazonSocial").val());
		$("#rfcProv").val($("#dRFC").val());
		$("#razonSocial").val($("#cRazonSocial").val());
				
		initTables();
		clickHandlers();
		//variables globales del contrato
		$("#cIdContrato").val($("#cIdContratoH").val());
		vcontrato=$("#cIdContrato").val();
		vfolio=$("#nFolioPreCompromiso").val();
		$(".montos").formatCurrency();
		$.ajaxSetup({cache:false});
	});
	function initQueries(){
		queryFormPost("preCompromisoCaratulaVentanillaRead", {async: false});
		queryFormPost("puestoUsuarioReadVentanillaPRECOM", {async: false});
		//Query heredado de SAI para obtener los tipos de retenciones
		querySelectPost("CatalogoTipoRetencionRead", "cIdTipoRetencionCMB", {async: false });
		queryFormPost("AnticipoPreCompromisosRead", {async: false});
		
		//checa nombre de usuario de materiales para agregar al volante de devolucion
		queryFormPost("nombreUsuarioMaterialesDevolucion", {async: false});
		
		
		//El estado para bloquer desde ventanilal un pedido es 4
		//$("#nIdEstado").val(4);
		//$("#nIdEstado").val(3);
		
		//se ingresa tipo de operacion para usarla en el procedimiento pa_actualizaEstadoVentanillaPedidoContrato ya que este es para pago centralizado y descentralizado
		//$("#tipoOperacion").val('ACTUALIZA');
		//queryFormPost("pa_actualizaEstadoVentanillaPedidoContratoCreate", {async: false});
		
		/*
		queryFormPost("mContratoDiversoCategoriaProcedimientoRead", {async: false});
		if ($("#cCategoriaProcedimiento").val().toLowerCase().indexOf('colaboraci') >= 0){
			$("#fAdjudicacion").val('');
			$("#fContratoIni").val('');
			$("#fContratoFin").val('');
			
			//document.getElementById("tdfd").disabled = true;
			//document.getElementById("trfin").disabled = true;
		}
		else {
			$("#fContratoSol").val('');
			$("#fContratoPro").val('');
			
			document.getElementById("trfsp").disabled = true;
		}
		
		*/
	}
	function initTables(){
		$('#dt_preCompromiso').dataTable(
			{
   			    "bPaginate": false,
       			"bLengthChange": false,
       			"bFilter": false,
       			"bSort": false,
       			"bInfo": false,
				"sScrollX": 100,         
				"sScrollY": 100,         
				"bJQueryUI": true,    
				"bRetrive" : true,
				"bDestroy" : true
		} );
	}
	function initAnticipos(){
		$('#dt_anticipos').dataTable({         
   			    "bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : false,
				"bInfo" : false,
				"bAutoWidth" : false,
				"sScrollY" : 150,
				"sScrollInnerY" : 200,
				"bScrollCollapse": true,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType" : "full_numbers"    
		} );
		oTableRetencion=$("#dt_retencion").dataTable({         
	    	"bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
  			"bInfo": false,
   			"bAutoWidth": true, 
			"sScrollY": 100,
			"sScrollX": 200,
			"bJQueryUI": true,    
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"    
		} );
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
     	   $("#nFolioCompromiso").val(folioPre);
     	   $("#folioCasoCompromiso").val(folioCaso);
     	   $("#FOLIO").val(folioCaso)
     	}
	}
	function imprimeRes(j){  
	    $.unblockUI();
		var res=j[0].Contable1;
		alert(res);
		document.getElementById("validaAutoPrecomp").disabled=true;
		document.getElementById("rechazaPrecomp").disabled=true;
		parent.document.getElementById("pb_save").disabled=true;
		$("#cMotivoDevolucion").val(res);
	}
	function casoAvanzado(j){
		vAvanzo=j[0].Col1;
	}
// INICIO funciones heredadas de Contrato diverso preCompAvanzado
  	function onLoadPlantilla(){
		//parent.document.getElementById("pb_send").style.visibility='hidden';
		//parent.document.getElementById("pb_save").style.visibility='hidden';
		parent.document.getElementById("pb_save").disabled=true;
		parent.document.getElementById("pb_cancel").style.visibility='hidden';
		parent.document.getElementById("pb_leave").disabled=false;
		
				
  	}
  	function onSubmit(id_oper){//validaciones del boton guardar
  	
	  	var mensaje="";
	  //  se valida que existan anticipos y amortizaciones
  	   var aTrs = $('#dt_retencion').dataTable().fnGetNodes();
  	      
  	      if(aTrs.length==0 || $("#nPorcAsignacion").val()=='0' || $("#nPorcAmortizacion").val()=='0' )
  	      {
  	       if (!confirm("No ha Ingresado Anticipos o Amortizaciones , Desea generar el Compromiso?")){
			//click a pestaña de anticipos
				$("#aTab2").click();
					return;
				}
  	   
  	      }
  	  // se deshabilita boton de guardar y se oculta el de cerrar
  	    parent.document.getElementById("pb_save").disabled=true;
        parent.document.getElementById("pb_leave").style.visibility='hidden';
		
  		//Avanza precompromiso
  	
  	/*
  		
  		//para guardar en variable de session el objeto caso del precompromiso
  		$.ajax({url: '../../servlet/PedidoServlet' , type:'post' , async: false,data:'operacion=8', dataType: 'json'});
  		
  		//Obtiene un folio de Compromiso
  		$.ajax({url: '../../servlet/PedidoServlet' , type:'post' , async: false,data:'operacion=6', dataType: 'json', success: guardaFolio});
  		if(  $("#nFolioCompromiso").val()==""){
  		alert("NO SE GENERO FOLIO DE COMPROMISO INTENTELO NUEVAMENTE");
  		return;
  		}
  		$("#tipoOperacion").val('ACTUALIZA');
  		// compromete los diferentes tipos de documentos
  		if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO')
  		queryFormPost("pa_comprometePrecompromisoModificadoCreate", {async: false});
  		else if($("#origen").val()=='PEDIDO AMPLIACION' || $("#origen").val()=='CONTRATO AMPLIACION')
  		queryFormPost("pa_comprometePrecompromisoAmpliacionCreate", {async: false});
  		else
  		queryFormPost("pa_comprometePrecompromisoCreate", {async: false});		
  		*/
  		var p = window.parent;
		try{
			//Guardado de los campos correspondientes a cada variable de caso
			p.gestion.setFolio( $("#FOLIO").val() );
			p.gestion.setOperador( $("#OPERADOR").val() );
			p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
			p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
			p.gestion.setConceptoMov("Aplicación de Compromisos");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
			p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			p.gestion.setAplicadoCont("false");
			//llama a la aplicacion contable
			$("#tipoOperacion").val('ACTUALIZA');
			mensaje=fnTerminaAplicacionCon();
			if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE COMPROMISO APLICADO CONTABLEMENTE")<0)
				{
				//Regresa el Pedido
				$("#nIdEstado").val(3);vEstado=3;
				$("#tipoOperacion").val('RECHAZA');
				queryFormPost("pa_actualizaEstadoVentanillaPedidoContratoCreate", {async: false });
				queryFormPost('tCompromisoDDelete', {async: false });
				queryFormPost('tCompromisoEDelete', {async: false });
				return;			
			  }
			  else{
			  	//Valisa si existe algun precompromiso (sin aplizar) de pedido o contrato que libera saldo a disponible
			  	if($("#origen").val() == "PEDIDO"){
			  		$.ajax({url: "../../servlet/PedidoServlet?cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:"operacion=13&pedidoDefinitivo="+$("#cIdContratoH").val(), dataType: 'json', success:
						function(j){}
					});
			  	}
			  	if($("#origen").val() == "CONTRATO"){
			  		$.ajax({url: "../../servlet/ContratoServlet?cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:"operacion=11&contratoDefinitivo="+$("#cIdContratoH").val(), dataType: 'json', success:
						function(j){}
					});
			  	}
			  }
			
			//Avanza precompromiso
  		
  		/*
  			$.ajax({url: '../../servlet/PedidoServlet' , type:'post' , async: false,data:'operacion=11', dataType: 'json', success: casoAvanzado});
  			if(vAvanzo!="true"){
  			alert("APLICO CONTABLEMENTE PERO NO AVANZO EL CASO AVISAR AL ADMINISTRADOR");
  			return;
  			}
		*/
		
		     
			//Bitácora
			$("#cAccion").val("APRUEBA_PRECOMPROMISO_FINANCIERO");
			$("#cIdDocumento").val($("#cIdContratoH").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			alert("Se ha creado el Compromiso con folio "+ $("#FOLIO").val());
			
			/*
			//se inserta un registro en la tabla de pcontratoanticipos para que pueda reflejarse en la ventana de pagos
			if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO' ||  $("#origen").val()=='CONTRATO AMPLIACION' ||  $("#origen").val()=='PEDIDO AMPLIACION' ){
			queryFormPost("obtieneContratoOriginal", {async: false});
					
			}else{
			$("#cIdContratoOriginal").val($("#cIdContratoH").val())
			}
			
			 queryFormPost("pa_agregaAnticipoAmortizacionPrecompromiso", {async: false});
			*/ 
			 
			 
			 //se llama al servlet que recarga el inbox
			document.getElementById("frmRecargar").submit();
			
				
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				//return false;
			}
			//return true;
  	}

  	function fnTerminaAplicacionCon(){
  	//aplicacion contable
  		var mensajeAp="";
  		
  		$.ajax({url: '../../servlet/PedidoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val()+"&nFolioPreCompromisoPRCP="+$("#nFolioPreCompromisoPRCP").val(), type:'post' , async: false,data:'operacion=7&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&tipoOperacion='+$("#tipoOperacion").val(), dataType: 'json', success:
  		//$.ajax({url: '../../servlet/PedidoServlet?nFolioCompromiso='+$("#nFolioCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=7', dataType: 'json', success: 
		function(j){
				mensajeAp=j[0].Contable1;
				 $("#FOLIO").val(j[0].FolioCompromiso);
				alert(mensajeAp);
			}
		});
		 
		 return mensajeAp;
		
  	  	}
  	  	

	  	function fnRechazaPrecomp(){
	  	cancelaPrecompromiso();
		}
	  	
  	
  	
  	function cancelaPrecompromiso(){
  	      
	queryFormPost("obtieneTipoPago", {async: false});
	queryFormPost("obtieneEstadoActual", {async: false});
	
	if($("#nEstadoActual").val()==4){
  	   alert("No se puede rechazar el precompromiso el contrato ya ha sido aprobado por otra unidad ejecutora ya que el tipo de pago es descentralizado");
  	   return;
  	}else{
  	 	if($("#origen").val() == "PEDIDO"){
  	 		queryFormPost("estatusPedidoVentanillaUpdate", {async: false});
  	 		$("#cDocumentoDefinitivo").val($("#cIdContratoH").val());
  	 		queryFormPost("retrocedeCasoVentanillaPrecompromisoPedido", {async: false });
			queryFormPost("updatePrecompromisoFolioPedidoVentanilla", { async: false });
			queryFormPost("updateDocumentoFolioDefinitivoPedido", {async: false });
			alert("Documento rechazado correctamente.");
			document.getElementById("frmRecargar").submit();
			//$.ajax({url: '../../servlet/PedidoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
		}
        else if($("#origen").val() == "CONTRATO"){
        	queryFormPost("estatusContratoVentanillaUpdate", {async: false});
  	 		$("#cDocumentoDefinitivo").val($("#cIdContratoH").val());
  	 		queryFormPost("retrocedeCasoVentanillaPrecompromisoContrato", {async: false });
  	 		queryFormPost("updatePrecompromisoFolioContratoVentanilla", { async: false });
  	 		queryFormPost("updateDocumentoFolioDefinitivoContrato", {async: false });
  	 		alert("Documento rechazado correctamente.");
			document.getElementById("frmRecargar").submit();
			//$.ajax({url: '../../servlet/ContratoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
		}
		else if($("#origen").val()=='PASIVO')
		$.ajax({url: '../../servlet/PasivoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val() , type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
		else if($("#origen").val()=='PLURIANUALIDAD')
		$.ajax({url: '../../servlet/ContratoPlurianualidadServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
  	    else if($("#origen").val()=='PEDIDO MODIFICADO')
		$.ajax({url: '../../servlet/PedidoModificadoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val() , type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
  	     else if($("#origen").val()=='CONTRATO MODIFICADO')
		$.ajax({url: '../../servlet/ContratoModificadoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val() , type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
		else if($("#origen").val()=='PEDIDO AMPLIACION')
		$.ajax({url: '../../servlet/AmpliacionesServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val() , type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
  	    else if($("#origen").val()=='CONTRATO AMPLIACION')
		$.ajax({url: '../../servlet/AmpliacionesServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val() , type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
  		 else if($("#origen").val()=='PLURIANUALIDADPEDIDO')
		$.ajax({url: '../../servlet/PedidoPlurianualidadServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val() , type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
  	     else if($("#origen").val()=='PASIVOPEDIDO')
		$.ajax({url: '../../servlet/PedidoPasivoServlet?nFolioPreCompromiso='+$("#nFolioPreCompromiso").val()+"&cEjercicio="+$("#cEjercicio").val(), type:'post' , async: false,data:'operacion=9&tipoPago=' + $("#nTipoPago").val()+'&cContableVentanilla='+$("#cCentroContable").val()+'&contratoDefinitivo='+$("#cIdContratoH").val()+'&origen='+$("#origen").val()+'&ur='+$("#ur").val(), dataType: 'json', success: eliminaPrecompromiso});
  	
  			}
  	
  	
  	
  	}
  	
  			function eliminaPrecompromiso(j){
  		  	  var mensaje=j[0].Contable1;
				alert(mensaje);
					if(mensaje.toString().toUpperCase().indexOf("DOCUMENTO DE PRECOMPROMISO CANCELADO CONTABLEMENTE")<0){
							//NO CANCELO CONTABLEMENTE SE DEJA EN SAIPRESUPUESTADO
					$("#nIdEstado").val(3);vEstado=3;
					$("#tipoOperacion").val('RECHAZA');
					queryFormPost("pa_actualizaEstadoVentanillaPedidoContratoCreate", {async: false });
			 		return;
					}
  	
			//Regresa el Pedido
			$("#nIdEstado").val(5);vEstado=5;
			$("#tipoOperacion").val('RECHAZA');
			queryFormPost("pa_rechazaVentanillaPedidoContratoCreate", {async: false });
			queryFormPost("pa_actualizaEstadoVentanillaPedidoContratoCreate", {async: false });
			
			//Bitácora
			$("#cAccion").val("CANCELA_PRECOMPROMISO_FINANCIERO");
			$("#cIdDocumento").val($("#cIdContratoH").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	
			
			 // se deshabilita boton de guardar y se oculta el de cerrar
	  	    parent.document.getElementById("pb_save").disabled=true;
	        parent.document.getElementById("pb_leave").style.visibility='hidden';
			document.getElementById("rechazaPrecomp").disabled=true;
            document.getElementById("validaAutoPrecomp").disabled=true;
			//se llama al servlet que recarga el inbox
			document.getElementById("frmRecargar").submit();
			
		
  	}
  	
	function onlyNumbers(evt)
    {
        var keyPressed = (evt.which) ? evt.which : event.keyCode
        if (keyPressed == 47)
			return false;
        return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
     }
	function habilitaMillar2(elPar){
		$("#CamInst").val(elPar);
	}
	function fn_actret(pcontrol, pctrlret) {				
		var valor = $('#' + pcontrol).val()
		$('#' + pctrlret).val( valor.substring(2) * 100 + "%" ) ;
		if ( $("#cIdTipoRetencionCMB").val() == 2 ) 
			$("#divMilla2").show();
		else 
			$("#divMilla2").hide();
	}
	function fnAgregarRet() {
		if ( $("#cIdTipoRetencionCMB").val() == "2")
			if ($("#CamInst").val() == "") {
				alert("Selecccione Camara o Instituto para continuar");
				return false;
			}
		if ($("#nPorcAsignacion").val()=='0' || $("#nPorcAsignacion").val()=='' || $("#nPorcAsignacion").val()=='0.0' || $("#nPorcAsignacion").val()=='0.00')
			$("#LHaySaldoAnticipo").val(0);
		else
			$("#LHaySaldoAnticipo").val(1);
		var oTableLocal = $('#dt_retencion').dataTable();
		var rowCount = oTableLocal.fnGetNodes().length;
		var idRet=$("#cIdTipoRetencionCMB").val();
		//i: inicia en 2 por que hay dos filas en la tabla al iniciar
		var i=0, noExiste=true;
		var aData;
		//Revisa que no se repita la clave de retención
		while(i<rowCount && noExiste){
			aData = oTableLocal.fnGetData( i );
			if(idRet== aData[ 0 ])	
				noExiste=false;
			else
				i++;
		}
		// En caso de que ya se haya agregado la clave sale de la función
		if(i!=rowCount){
			alert("Esa clave ya se agregó previamente");
			return false;
		}			
		$("#cIdTipoRetencionVentanilla").val(idRet);
		queryFormPost("tContratoDiversoRetencionVentanillaCreate", {async: false });
		loadRetenciones();
	}
	function loadRetenciones(){
	  var vcontrato=encodeURIComponent($("#cIdContratoH").val())
	 	oTableRetencion=$('#dt_retencion').dataTable( {
			bPaginate: false,
  			bFilter: false,
  			bInfo: false,
			sScrollX: 200,
			sScrollY: 100,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,   
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_CONSULTARETENCIONES_DIVERSOS&qw=cIdContrato='" + vcontrato + "' AND cIdTipoRetencion<>1",
			aoColumns: [
				{ sName: "cIdTipoRetencion", bVisible: false},
				{ sName: "cTipoRetencion"},
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }]
              } ) ;
		}
// FIN funciones heredadas de Contrato diverso
	function fnAgregarAntAmort(){
		var anticipo=$("#nPorcAsignacion").val();
    	var amort=$("#nPorcAmortizacion").val();
		if(parseInt(anticipo,10)>0 || parseInt(amort,10)>0){
		
		if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO' ||  $("#origen").val()=='CONTRATO AMPLIACION' ||  $("#origen").val()=='PEDIDO AMPLIACION' ){
			queryFormPost("obtieneContratoOriginal", {async: false});
					
			}else{
			$("#cIdContratoOriginal").val($("#cIdContratoH").val())
			}
				
			queryFormPost("pa_agregaAnticipoAmortizacion", {async: false });
			alert("Amotizacion creada");
		}else{
			alert("Tienes que agegar un anticipo o amortización");
		}
	}
	function clickHandlers(){
	//Carga el calendario de una EP al momento de hacer click sobre la EP
		$('#dt_total tr').live('dblclick', function() { 
				$(oTableEP.fnSettings().aoData).each(function(){
					$(this.nTr).removeClass('row_selected');
				});
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableEP );		
				var aData = oTableEP.fnGetData(anSelected[0]);
				EP=aData[1].substring(0, aData[1].length - 8 );
				cargaContratoPrecomprometido();
			});
	//Carga los datos de la tablas solo cuando se hace click sobre la pestaña
		$("#aTab1").click(function() {
			if(cambio){
				cargaTotalEP();
				cambio=false;
			}
		});
		$("#aTab2").click(function() {
			initAnticipos();
			loadRetenciones();
		});
		//Click para quitar una retencion de la tabla de retenciones
		$('#dt_retencion tr').live('dblclick', function() { 
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableRetencion );		
				var aData = oTableRetencion.fnGetData(anSelected[0]);
				$("#cIdTipoRetencionVentanilla").val(aData[0]);
				queryFormPost("tContratoDiversoRetencionVentanillaDelete", {async: false });
				loadRetenciones();
			});
	}
	function fnGetSelected( oTableLocal )
	{
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
	function cargaTotalEP() {
			var qw = "nFolioPreCompromiso ='" + vfolio +"'";
			oTableEP=$('#dt_total').dataTable( {
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
				bAutoWidth: false,
				sScrollX: 100,
				sScrollY: 100,
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				"bRetrive" : true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_preCompromisoEPTotal&qw=" + qw,
				aoColumns: [
					{ sName: "nFolioPreCompromiso",bSortable: false },
					{ sName: "EP",bSortable: false },
					{ sName: "Total",bSortable: false }]
			} ) ;
	}
	function cargaContratoPrecomprometido() {
		var qw = " cIdContrato = '" + vcontrato+ 				
		"' AND nFolioPreCompromiso ='" + vfolio +
		"' AND ClaveSIAFF ='" + EP +"'"; 
      
        qw=encodeURIComponent(qw) ;
         // compromete los diferentes tipos de documentos
  		if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO' ||  $("#origen").val()=='CONTRATO AMPLIACION' ||  $("#origen").val()=='PEDIDO AMPLIACION' ){
  		oTableCalendario=$('#dt_preCompromiso').dataTable( {
			bPaginate: false,
   			bLengthChange: false,
   			bFilter: false,
   			bInfo: false,
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
		bJQueryUI: true,
		bDestroy : true,
		bServerSide: true,   
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_calendarioPrecompromisoModificadoAmpliacion&qw=" + qw,
		aoColumns: [
			{ sName: "ClaveSIAFF",bSortable: false },
			{ sName: "ClaveInterna",bSortable: false },
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
			{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
       		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }],
   		bAutoWidth: true
        } ) ;
  		
  		
  		
  		
  		
  		}else{
  		
		oTableCalendario=$('#dt_preCompromiso').dataTable( {
			bPaginate: false,
   			bLengthChange: false,
   			bFilter: false,
   			bInfo: false,
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
		bJQueryUI: true,
		bDestroy : true,
		bServerSide: true,   
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_calendarioPrecompromiso&qw=" + qw,
		aoColumns: [
			{ sName: "ClaveSIAFF",bSortable: false },
			{ sName: "ClaveInterna",bSortable: false },
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
			{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
       		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }],
   		bAutoWidth: true
        } ) ;
		/*new FixedColumns(oTableCalendario, {
				iLeftColumns: 2
		});*/
  		
  		}
  		

	}
	
	
	function checkShortcut()
	{				
		//Deshabilita el ESC y BACKSPACE
		if(event.keyCode==27){
			return false;
		}
		if((event.srcElement.tagName.toUpperCase() != 'INPUT' &&
				event.srcElement.tagName.toUpperCase() != 'TEXTAREA')
			&& (event.keyCode==8 || event.keyCode==13)){					
			return false;
		}
	}
	function openPDF(reporte){
	       var vcontrato=encodeURIComponent($("#cIdContratoH").val())
			window.open(
				"../../admin/SeguridadCatalogos?"
					+ "catalogo=REPORTE"
					+ "&accion=run"
					+ "&rn="+reporte
					+ "&cIdContrato=" + vcontrato,
				"popacuse",
				"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	function imprimeVolante(){
		//Funcion que manda imprimir el rptVolante
		queryFormPost("tPreCompromisoVolanteDevolucionCreate", {async: false});
		openPDF("rptVolanteDevolucion.jasper");
		alert ("El volante se está creando");
	}
	function fnValidaAutoPrecomp(){
		var proc=""+$("#cEjercicio").val()
					+","+$("#cIdContratoH").val()
					+","+$("#rfcProv").val()+"";
					
					
					
		if($("#origen").val()=='PEDIDO MODIFICADO' || $("#origen").val()=='CONTRATO MODIFICADO' ||  $("#origen").val()=='CONTRATO AMPLIACION' ||  $("#origen").val()=='PEDIDO AMPLIACION' ){			
		
		 $.getJSON("../../servlet/PedidoServlet?operacion=10",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
		                switch(j[0].Col1){
						case "0":
							alert("Este contrato cumple con las validaciones establecidas");
							//No se puede comprometer hasta que se haya hecho las validaciones
							parent.document.getElementById("pb_save").disabled=false;
						break;
						case "1":  
							alert("Este proveedor no se está dado de alta en los beneficiarios");
							$("#cMotivoDevolucion").val("Este proveedor no se está dado de alta en los beneficiarios");
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
			
		}else{
	
		  $.getJSON("../../servlet/PedidoServlet?operacion=5",{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
		                switch(j[0].Col1){
						case "0":
							alert("Este contrato cumple con las validaciones establecidas");
							//No se puede comprometer hasta que se haya hecho las validaciones
							parent.document.getElementById("pb_save").disabled=false;
						break;
						case "1":  
							alert("Este proveedor no se está dado de alta en los beneficiarios");
							$("#cMotivoDevolucion").val("Este proveedor no se está dado de alta en los beneficiarios");
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
	
	
	function ResponsableSiguiente(id_oper){
   	 if(parseInt(id_oper,10)==2)
  		 	return "CONSULTA_PRECOMPROMISO";
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){
   		if(parseInt(id_oper,10)==2)
  		 	//return "consulta_compromiso";
  		 	return "consulta_precomp";
  	}
	
	function onPostDisplay(){
	
	}
  		
	
	
  	
</script>
</head>
<body  id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" onkeydown="return checkShortcut()">
<form>
	<div id="container" class="container">
			<h1>Ventanilla PreCompromiso</h1>
			<div class="tabs" id="tabs">
				<input type="text" style="width: 600px" id="cIdContratoH" name="cIdContratoH" readonly style="border-width:0; background-color:transparent"/>
				<table style="width: 100%"><tr>
					<td align="right">
						<input type="button"  align="middle"  style="width: 155px" value="Validación Automática"  id="validaAutoPrecomp"	onclick="fnValidaAutoPrecomp();">
						<input type="button"  align="right" style="width: 170px" value="Rechazar Precompromiso"  id="rechazaPrecomp"	onclick="fnRechazaPrecomp();">
					</td>
				</tr></table>
				<ul>
					<li><a id="aTab0" href="#tabs-0" >Informaci&oacute;n General</a></li>
					<li><a id="aTab1" href="#tabs-1" >Financiamiento</a></li>
					<li><a id="aTab2" href="#tabs-2" >Anticipos y Retenciones</a></li>
					<li><a id="aTab3" href="#tabs-3" >Volante de devolución</a></li>
				</ul>
				<div id="tabs-0" align="center">
						<table  align="left" width="90%">
						       <tr>
						    	<td align="left" >Tipo Documento:<input type="text" style="width: 200px" name="origen" id="origen" readonly style=" background-color:transparent"/></td>
						    	</tr>
							<tr><td colspan="2"><table width="100%" height="78" style="width: 709px;" align="left">
								
								
								<tr>
						    		<td align="left" >RFC: <input type="text" style="width: 200px" name="rfcProv" id="rfcProv" readonly style=" background-color:transparent"/></td>
						    	</tr>
						    	<tr>
						    		<td align="left" >Razón Social: <input type="text" style="width: 400px" name="razonSocial" id="razonSocial" readonly style=" background-color:transparent"/></td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="4">Plazo:
						    		<input type="text" style="width: 75px" name="Plazo" id="Plazo" readonly style="background-color:transparent"/>
						    		Unidad Admin: 
						    		<input type="text" style="width: 450px" name="UnidadEjecutora" id="UnidadEjecutora" readonly style="background-color:transparent"/>
						    		 </td>
						    	</tr>
						    	<tr>
						    		<td align="left" colspan="2">Tipo de Fondo: <input type="text" style="width: 200px" name="cTipoFondo" id="cTipoFondo" readonly style="background-color:transparent"/></td>
						    	</tr>
							</table ></td></tr>
							<tr>
								<td align="left">Concepto: <textarea id="cConceptoContrato"  name="cConceptoContrato" rows="5" readonly="readonly" cols="50"></textarea></td>
								<td align="center"><table >
									<tr id="trfsp" >
										<td align="left">Fecha Solicitud:
						    				<input type="text" name="fContratoSol" id="fContratoSol" readonly style="background-color:transparent"/>
						    			</td>
						    			<td align="left">Fecha Propuestas:
						    				<input type="text" name="fContratoPro" id="fContratoPro" readonly style=" background-color:transparent"/>
					    				</td>
				    				</tr>
									<tr>
										<td id="tdfd" align="left">Fecha documento:
						    				<input type="text" name="fAdjudicacion" id="fAdjudicacion" readonly style="background-color:transparent"/>
						    			</td>
						    			<td align="left">Fecha Formalización:
						    				<input type="text"  name="fFirmaContrato" id="fFirmaContrato" readonly style=" background-color:transparent"/>
					    				</td>
				    				</tr>
				    				<tr id="trfif" >
				    					<td align="left">Fecha Inicio:
						    				<input type="text" name="fContratoIni" id="fContratoIni" readonly style="background-color:transparent"/>
						    			</td>
						    			<td align="left">Fecha Fin:
						    				<input type="text"  name="fContratoFin" id="fContratoFin" readonly style=" background-color:transparent"/>
					    				</td>
				    				</tr>
									<tr>
										<td align="left" colspan="2">Tipo Adjudicación:
						    				<input type="text"  style="width: 200px"  name="cTipoAdjudicacion" id="cTipoAdjudicacion" readonly style=" background-color:transparent"/>
						    			</td>
						    		</tr>
									<tr>
										<td align="left">Tipo Moneda:
						    				<input type="text"   style="width: 180px" name="cTipoMoneda" id="cTipoMoneda" readonly style=" background-color:transparent" value="01 PESO MEXICANO"/>
					    				</td>
					    				<td align="left">%IVA:
						    				<input type="text" style="width: 150px"  name="IVA" id="IVA" readonly style="background-color:transparent"/>
					    				</td>
				    				</tr>
									</table></td>
							</tr>
							<tr><td colspan="2"><table>
									<tr>
										<td>Monto Pedido
											<input type="text"   style="width: 180px" class="montos" name="mImporteBruto" id="mImporteBruto" readonly style=" background-color:transparent" />
										</td>
										<td>Importe Bruto
											<input type="text"   style="width: 180px" class="montos" name="mImporteContrato" id="mImporteContrato" readonly style=" background-color:transparent" />
										</td>
										<td>Monto Contrato(Pesos)
											<input type="text"   style="width: 180px"  class="montos" name="mImporteTotal" id="mImporteTotal" readonly style=" background-color:transparent" />
										</td>
									</tr>
									<tr>
										<td></td>
										<td>Monto IVA
											<input type="text"   style="width: 180px"  class="montos"name="mImporteIVA" id="mImporteIVA" readonly style=" background-color:transparent" />
										</td>
										<td>Monto Contrato(ME)
											<input type="text"   style="width: 180px" class="montos" name="mContratoME" id="mContratoME" readonly style=" background-color:transparent" />
										</td>
									</tr>
									<tr>
										<td> Monto Total Original
										
										<input type="text"   style="width: 180px" class="montos" name="mContratoMNOriginal" id="mContratoMNOriginal" readonly style=" background-color:transparent" />
										
										
										</td>
										<td>Monto Total a Pagar
											<input type="text"   style="width: 180px" class="montos" name="mContratoMN" id="mContratoMN" readonly style=" background-color:yellow " />
										</td>
										<td>Responsable RM
											<input type="text"   style="width: 180px" name="U_NOMBRE" id="U_NOMBRE" readonly style=" background-color:transparent" />
										</td>
									</tr>
							</table></td></tr>
						</table>
					</div>
					<div id="tabs-1"  >
					<br/><h2>Detalle de Claves Presupuestarias</h2>
					<table align="left" width="750px"><tr><td style="width: 740px">
						<table id="dt_total" class="display" style="width: 740px">
							<thead>
								<tr>
									<th>Folio</th>
									<th>EP</th>
									<th>Total</th>
								</tr>
							</thead>
						</table>
						<br/><h2>Calendario Detallado de Claves Presupuestales</h2>
						</td></tr>
						<tr><td style="width: 740px">
						<table id="dt_preCompromiso" class="display" style="width: 740px" >
							<thead>
								<tr>
									<th >Estructura Program&aacute;tica</th>
									<th>Clave Interna</th>
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
									<th>Contrato</th>
									<th>EP</th>
								</tr>
							</thead>
						</table>
						</td></tr></table>
				</div>
				<div id="tabs-2">
					<h2>Anticipos</h2>
					<table  id="dt_anticipos" class="display" style="height: 40px">
						<thead>
							<tr align="center">
							<th>% Anticipo</th>
							<th>% Amortizaci&oacute;n</th> 
							</tr> 
						</thead>
						<tbody>
							<tr align="left">
								<td align="center"><input type="text" maxlength="2" size="5" name="nPorcAsignacion" id="nPorcAsignacion" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)"></td>
								<td align="center"><input type="text" maxlength="2" size="5" name="nPorcAmortizacion" id="nPorcAmortizacion" value="0" onKeyPress="return onlyNumbers(event)" style="text-align:right;"></td> 
							</tr> 
						</tbody>
					</table>
					<br/>
					<input align="right" type="button" value="Agregar Anticipos y Amortizaciones"  id="AgregarAntAmort"	onclick="fnAgregarAntAmort();">
					<br/><br/><br/>
					<h2>Retenciones</h2>
					<select	onchange="fn_actret('cIdTipoRetencionCMB', 'nPorcRetencion')" name="cIdTipoRetencionCMB" id="cIdTipoRetencionCMB"><option selected value="1">SIN RETENCION</option></select>
					<input type="button" value="Agregar Retención" name="Add1" id="Agregar"	onclick="fnAgregarRet();">
					<div id="divMilla2">
						<label>
							Camara :
							<input name="grpMilla2" type="radio" id="grpMilla2"
								onclick="habilitaMillar2(0)" value="Camara">
						</label>
						<label>
							Instituto :
							<input type="radio" id="grpMilla2" name="grpMilla2"
								onclick="habilitaMillar2(1)" value="Instituto">
						</label>
					</div>			
					<table id="dt_retencion" class="display">
						<thead>
							<tr align="center">
								<th></th>
								<th width="400px">Clave de retenci&oacute;n</th>
								<th></th> 
							</tr> 
						</thead>
					</table>
				</div>
				<div id="tabs-3" align="center" >
						<table align="left">
							<tr><td align="left">Nombre responsable SAI RM<br/>
								<input type="text"   style="width: 500px"  name="usuarioNombreRM" id="usuarioNombreRM" readonly style=" background-color:transparent" />
							</td></tr>
							<tr><td align="left">Cargo responsable SAI RM<br/>
								<input type="text"   style="width: 500px"  name="PTO_NOMBRE" id="PTO_NOMBRE" readonly style=" background-color:transparent" />
							</td></tr>
							<tr><td align="left">Motivo de devolución<br/>
								 <textarea id="cMotivoDevolucion"  name="cMotivoDevolucion" rows="5" cols="50" >NO ESPECIFICADO</textarea>
							</td></tr>
							<tr><td align="center">
								 <input type="button"  name="btnImprime" id="btnImprime" value="Imprimir" onclick="imprimeVolante();" />
							</td></tr>
							
							
							
							
							
						</table>
				</div>
			</div>
			
			  
			
			
				<!-- Hidden Section -->
			    <!-- Valores de Precompromiso -->
			    <input type="hidden" name="cRazonSocial" id="cRazonSocial" />
			    <input type="hidden" name="cIdContrato" id="cIdContrato" />
			    <input type="hidden" name="cEjercicio" id="cEjercicio" />
			    <input type="hidden" name="nIdEstado" id="nIdEstado" />
		     	<input type="hidden" name="dRFC" id="dRFC" />
				<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>" />
				<input type="hidden" id="nFolioPreCompromiso" name="nFolioPreCompromiso" value="<%=nfolio %>"/>
				
				<input type="hidden" id="nFolioPreCompromisoPRCP" name="nFolioPreCompromisoPRCP" value="<%=folio %>"/>
				
				<!-- Valores de Contrato Diverso -->
				<input name="CamInst" type="hidden" id="CamInst" />
				<input type="hidden" name="LHaySaldoAnticipo" />
				<input name="cIdEntidadContable" type="hidden" id="cIdEntidadContable"/>
				<input type="hidden" name="nPorcRetencion" id="nPorcRetencion"/>
				<input type="hidden" name="cIdTipoRetencionVentanilla" id="cIdTipoRetencionVentanilla"/>
				<!-- Valores de compromiso -->
				<input name="FOLIO" type="hidden" id="FOLIO" />
				<input name="OPERADOR" type="hidden" id="OPERADOR" value="<%=usuario.getNombre()%>" />
				<input name="FECHA_CARGA" type="hidden" id="FECHA_CARGA" value="<%=today %>"/>
				<input name="nFolioCompromiso" type="hidden" id="nFolioCompromiso" value=""/>
				<input name="folioCasoCompromiso" type="hidden" id="folioCasoCompromiso" />
				<!-- Categoria del procedimiento, para mostrar unas fechas u otras -->
				<input type="hidden" name="cCategoriaProcedimiento" id="cCategoriaProcedimiento" />
				<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable%>" />
				<input name="nTipoPago" type="hidden" id="nTipoPago" />
				<input name="nEstadoActual" type="hidden" id="nEstadoActual" />
				<input name="ur" type="hidden" id="ur" value="<%=usuario.getU_UR()%>"/>
				<input name="cIdContratoOriginal" type="hidden" id="cIdContratoOriginal" />
				<input name="tipoOperacion" type="hidden" id="tipoOperacion" value=""/>
				 <input name="cAccion" id="cAccion" type="hidden">
				<input name="cIdDocumento" id="cIdDocumento" type="hidden">
				<input name="cDocumentoDefinitivo" id="cDocumentoDefinitivo" type="hidden"/>
				<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuario.getLogin()%>"/>
								
				
				
		</div>
		
		
										
		
		
</form>
<form id="frmRecargar" action="../../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_USER_LEAVE_CASE%>" method="post" target="content-iframe"></form>
</body>
</html>
