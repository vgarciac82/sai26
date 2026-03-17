<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
	/*
	 * VGC20150416 Se cambia para el contrato descentralizado. Se agrega combo que se hace visible en caso de ser un contrato == "PE" descentralizado y que el usuario tenga rol de ADMIN_RECMAT o ADMIN
	 */
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	boolean bAplicadoCont = false;

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	String select = c.getTipoCaso().getGavetaAsociada() + "_G"
			+ c.getIdGabinete();//se usa por separado abajo
	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null
			&& request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	boolean esAdmin = (usuario.getRole("ADMIN") != null)
			|| (usuario.getRole("ADMIN_RECMAT") != null);
	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();

	/*VGC20151228 Se parametriza el habilitar radicado*/
	ConfiguraAplicativoBusinessLogic configApp = new ConfiguraAplicativoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	boolean muestraCBRadicado = "S".equalsIgnoreCase(configApp
			.getSystemSetting("MUESTRA_RADICADO_CMP"));
	boolean esSAIAlterno = "true".equals(configApp.getSystemSetting("SAI_AMBIENTAL"))
			|| "true".equals(configApp.getSystemSetting("SAI_FONDEN"));
	
	String opName = c.getCasoOperacion(0).getOperacion().getNombre();
%>
<!DOCTYPE HTML>
<html>
<head>
<title>Genera Compromiso</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";
@import "css/demo_table_jui.css";
@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
@import "css/bootstrap.min.css";
</style>

<%-- script type="text/javascript" src="js/jquery-1.0.4.pack.js"></script--%>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<!-- VGC20150416 Libreria JS para el manejo de Descentralizados -->
<script type="text/javascript" src="js/Compromiso.js"></script>


<script type="text/javascript" charset="utf-8">
var bCarga = false;
var bClicBtn = false;
var breturnVal = false;
var esAdmin = <%=esAdmin%>;
var urOriginal = '<%=cUR%>';
var ccOriginal = '<%=cCentroContable%>';
var tipoDisponible = "";
var muestraCBRadicado = <%=muestraCBRadicado%>;
var esSAIAlterno = <%=esSAIAlterno%>;
var opName = "<%=opName%>";


$(document).ready(function() {
	
	if( esAdmin ){
		querySelectPost("catURCompromisoRead", "cUnidadCompromiso", {async:false} );
	}else{
		querySelectPost({queryName: 'catURVistasCompromisoRead', targetObjectId: 'cUnidadCompromiso', async: false, callback:function(){$("#cUnidadCompromiso".change());}});
	}
	
	$("#chk_radicado").change(function(){
		var vcontrato = $("#cIdContratoCompromiso").val();
		var esDescentralizado = ("1" == $("#nesdescentralizado").val());
			
		if ($("#chk_radicado").attr("checked") ){
			tipoDisponible = " AND cuentaDisp = '82109' ";
			$("#eventoPrecompromiso").val("R_CMP003");
		}else{
			tipoDisponible = " AND cuentaDisp = '82106' ";
			$("#eventoPrecompromiso").val("CMP003");
			
		}

		creaDT(vcontrato,esDescentralizado,tipoDisponible);
	});
	
	//VGC2016019 Parametriza si se muestra o no el checkbox de radicado
	if( !muestraCBRadicado ){
		$("#divRadicado").hide();
		$("#chk_radicado").attr("checked", false);
		$("#chk_radicado").change();
	}
		
			creaDlgFirmantes();
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 400,
				width: 800,
				modal: true,
				beforeClose: function( event, ui ) {
					return bClicBtn;			
				}
			});
				
			$( "#dialog-Procesando" ).dialog({
				autoOpen: false,
				height: 400,
				width: 400,
				modal: true,
				open: function() {
					var tipo = "aplicarMotor";
					var caNoContrarrecibo = $("#nFolioCompromiso").val();
					var campo = $("#campo").val();
					var tablaEnc = $("#tablaEnc").val();
					var campoCondicion = $("#campoCondicion").val(); 
					var tablaDet = $("#tablaDet").val(); 
					var tipoAplicar = $("#tipoAplicar").val();
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					
					$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo,campo:campo,tablaEnc:tablaEnc,campoCondicion:campoCondicion,tablaDet:tablaDet,tipoAplicar:tipoAplicar},
							success:function(data){
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
										if ($("#chk_radicado").prop("checked")){
											queryFormPost("updateEsRadicadoComp", {async: false });
											queryFormPost("contratoDiversoRadicadoUpdate", {async: false });
										}
										alert( "Documento Aplicado Correctamente: "+ $("#caNoCompromiso").val() ) ;
										parent.document.getElementById("pb_send").disabled = false;
										parent.document.getElementById("pb_send").click();		

										breturnVal = true;
									}else{
										breturnVal = false;
										alert( "El Documento No Se Aplico: "+$("#caNoCompromiso").val() + " - " + data.estatus ) ;		
									}
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesando" ).dialog( "close" );
							}
					});
				},
				close: function() {										
				}				
			});
	
			$("#nFolioCompromiso").val( '<%=request.getParameter("folio")%>' );
			
		    $("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
			$("#divImprimirOficio").hide();
			
			var oTable = $('#dt_compromiso').dataTable();
			/* Add a click handler to the rows - this could be used as a callback */
			$("#dt_compromiso tbody").click(function(event) {
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});
				$(event.target.parentNode).addClass('row_selected');
			});
			

			$('#dt_grabacompD').dataTable({
					"iDisplayLength": 20,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false } );

			$('#dt_grabacompD').attr('visible', true);
			
			$('#dt_grabaprecompD').dataTable({
					"iDisplayLength": 20,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false } );

			$('#dt_grabaprecompD').attr('visible', true);

			
			$('#dt_comprometido').dataTable(
			{
   			    "bPaginate": false,
       			"bLengthChange": true,
       			"bFilter": false,
       			"bSort": false,
       			"bInfo": false,
       			"bAutoWidth": true, 
				"bScrollX": "100%",         
				"bScrollY": "100%",         
				"bJQueryUI": true,    
				"bRetrive" : true,
				"bDestroy" : true,
				bScrollCollapse : false  
				
			} );

			querySelectPost("tEjercicioRead", "cEjercicio", {async: false });
			queryFormPost("tCompromisoEncabRead", {async: false });
			
			if($("#cDocumentoHaplicado").val() == "S"){
				if(<%=id_oper == 1%>){
					parent.document.getElementById("pb_cancel").style.visibility='hidden';
					parent.document.getElementById("pb_cancel").disabled=true;
				}
			}
			
			queryFormPost("TipoPolizaRead",{async: false });
			setTimeout("cargaContrato()",5);

			$("#cIdContratoCompromiso").change(function () {
			
				$("#tienePagoPendiente").val("0");
				$("#pagoCXP").val("");
				
				var tipoCompromiso = $("#cIdContratoCompromiso").val().substring(0,2);
				var contratoDescentralizado = $("#nesdescentralizado").val();
				
				$("#divTablas").show();
				
				if( 0 == parseInt( contratoDescentralizado, 10 ) )
					queryFormPost("tienePagosPendientesContrato_Read", {async: false });
				else
					$("#tienePagoPendiente").val("0");
				
				if( $("#tienePagoPendiente").val() == "1" && !( "PE" == tipoCompromiso ) ){
					
					queryFormPost("pagosPendientesContrato_Read", {async: false });
					alert("No puede continuar. Este contrato tiene Pagos Pendientes de procesar en SICOP: " + $("#pagoCXP").val());
					return;
					
				}else{				
					
					var vcontrato = $(this).val();
					var esDescentralizado = ( "1" == $("#nesdescentralizado").val()  && "1" == $("#iEsAbierto").val() ); 
					$("#cIDRFC").val('');			
					
					if( esDescentralizado && esAdmin ){
						$("#divURComp").css("display","block");
						$("#cUnidadCompromiso").val(urOriginal);
						$("#cCentroContable").val( ccOriginal  );
						queryFormPost("tContratoCompAbiertoRead", {async: false });
					}else if( esDescentralizado && !esAdmin ){
						$("#divURComp").css("display","block");
						//$("#cUnidadCompromiso").val(urOriginal);
						//$("#cCentroContable").val( ccOriginal  )
						queryFormPost("tContratoCompAbiertoRead", {async: false });
						cambiaCC();
						
					}else {
						$("#divURComp").css("display","none");
						if( "consulta_compromiso" == opName){
							queryFormPost("tContratoConsultaRead", {async: false });
						}else{
							queryFormPost("tContratoCompRead", {async: false });
						}
					}
					
					
					if(<%=id_oper == 2%>){
						var tipoContrato = $("#cTCont").val();
						
						if(tipoContrato == "FEDERALIZADO"){
						
							//Leer la Unidad Responsable del Folio del Compromiso.
							queryFormPost("unidadResponsableCompromisoEnc_Read",{async: false });
							$("#divImprimirOficio").show();
							querySelectPost("catTipoSuplenciaRead", "tipoSuplencia",{async: false });
							querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo",{async: false });
							$("#nFolioPago").val($("#nFolioCompromiso").val());		
												
						}else{
							$("#divImprimirOficio").hide();
						}						
					}
					
					if ($("#cIDRFC").val() == '') {
					
						if( "consulta_compromiso" == opName){
						
							queryFormPost("tContratoConsultaRead", {async: false });
							
						}else{
						
							queryFormPost("tContratoRead", {async: false });
						
							if ($("#cIDRFC").val() == '') {
								$("input").val("");
								alert("No existe Contrato: " + vcontrato );
							}
							
						}
					}
					
					if(muestraCBRadicado){
						var tipoContrato = $("#cTCont").val();
					
						if(tipoContrato == "FEDERALIZADO"){
							queryFormPost("validaPartidaContratoFED_Read", {async: false });
							var partidaFed = $("#partidaFed").val();
							var partidaFedCombinado = $("#partidaFedCombinado").val();
							
							if(partidaFed == "1" && partidaFedCombinado == "0"){
								alert("No puede continuar ya que el Contrato es FEDERALIZADO y tiene partidas diferentes a 43301 ");
								return;
							}else if(partidaFed == "0" && partidaFedCombinado == "0"){
								activaDesactivaRadicado(false);
							}						
						}else{
							activaDesactivaRadicado(true);
						}
					}
					
					queryFormPost("tContratoAnticipoRead", {async: false });
					queryFormPost("tContComprFolioRead", {async: false });
					
					if( esDescentralizado ){
						queryFormPost({
								queryName:"tSaldoActualCompromisoAbierto", 
								async: false,
								callback:function(){
									if( $("#mComprometido").val() == "" )
										$("#mComprometido").val("0.00");
								}
							  });
					}else{
						queryFormPost({
								queryName:"tSaldoActualCompromiso", 
								async: false,
								callback:function(){
									if( $("#mComprometido").val() == "" )
										$("#mComprometido").val("0.00");
								}
							  }); //URVP.04122014 Se manda llamar el CRUD para llenar el compo del remanente del contrato de la linea comentada de arriba
					}	
					
					// El remanente del contrato. Es el total del contrato menos los pagos realizados. No es igual al remanente del compromiso en el sentido de que puede no haberse comprometido el 100 por ciento del contrato			
					queryFormPost("tRemanenteContrato", {async:false}); 
					
					// El maximo monto que se puede incrementar el contrato.
					queryFormPost("readMaximoIncrementoCompromiso", {async:false}); 
					
					$("#mComprometer").formatCurrency();
					$("#mImporteTotal").formatCurrency();
					$("#mComprometido").formatCurrency();
					$("#remanenteContrato").formatCurrency();
					$("#maximoIncremento").formatCurrency();
					
					creaDT(vcontrato,esDescentralizado,tipoDisponible);
				}
			});

    $('#edit').click( function () {
        var nRow = fnGetSelected( oTable ) ; 
    } );
    
    $("#chk_radicado").change();	
	
});

	function onLoadPlantilla(){
		if ( $("#FECHA_CARGA").val().split("-")[0] != $("#cEjercicio").val() ) {
			$("#FECHA_CARGA").val( $("#cEjercicio").val() + "-12-31" );
		}
	
		if(<%=id_oper == 1%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';			
		}
	
	}
	
	function onSubmit(id_oper){
		var p = window.parent;
		var valida_campos = true;
		if ($("#cDocumentoHaplicado").val() == "S") {
			alert("Documento ya fue aplicado y se avanzar� a modo de CONSULTA");
		}else {
		try{
			$("#chk_radicado").attr('disabled', true);

			p.gestion.setFolio( $("#FOLIO").val() );
			p.gestion.setOperador( $("#OPERADOR").val() );
			p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
			p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
			p.gestion.setConceptoMov("Aplicaci�n de Compromisos");
			p.gestion.setMoneda("MXP");
			p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			p.gestion.setAplicadoCont("false");
			
			if(<%=c.getIdGabinete()%>!=-1){
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			}
			
			var nretval = cmdGuardar();
			if (nretval == -1) {
				return false;
			}
			
			
			$("#campo").val( "nFolioPrecomFinanciero" );
			$("#tablaEnc").val( "tPrecomFinancieroEncabezado" );
			$("#campoCondicion").val( "nFolioPrecomFinanciero" ); 
			$("#tablaDet").val( "tPrecomFinancieroDetalle" ); 
			$("#tipoAplicar").val( "PRECOMFINANCIERO"  );
		
			
			if ( procesar() ){
				parent.document.getElementById("pb_save").disabled = false;												
			}

			parent.document.getElementById("pb_save").disabled=true;
			parent.document.getElementById("pb_cancel").disabled=true;			
		
		} catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
	}
	return valida_campos;
	}

	function cmdGuardar() {
		
		var vdocren = 1 ;
		var nRows = $("#dt_compromiso tr").length -1 ;
		var oTable = $('#dt_compromiso').dataTable();
		var oTablD = $('#dt_grabacompD').dataTable();
		
		/*Tabla para guardar el precompromiso*/
		var oTab2D = $('#dt_grabaprecompD').dataTable();
		
		var aTrs = oTable.fnGetNodes();
		var vrowAf = "";
		var vnumcomp = $("#numcomp").val();
		
		if( $("#cCentroContable").val() == "" ){
			if( $("#cIdEntidadContable").val() == "" ){
				$("#cCentroContable").val(ccOriginal);
				$("#cIdEntidadContable").val(ccOriginal);
			}else{
				$("#cCentroContable").val( $("#cIdEntidadContable").val());
			}
		}
		
		oTablD.fnClearTable();
		
		for ( var i=0 ; i<nRows ; i++ ) {
			var aData = oTable.fnGetData( i );
			var jqInputs = $('input', aTrs[i] );
			
			if (jqInputs.length > 0) {
			
				for ( var j=0 ; j<12 ; j++ ) {
					var vep = aData[ 0 ] + "." + $.trim(aData[ 1 ]) ;
					var vimporteP = jqInputs[ j ].value ;
					vimporteP = quitaFmt(vimporteP);
					
					if (parseFloat(vimporteP) != 0 ) {
						var vimporteN = vimporteP * -1 ; 
						vMes = (j + 1) ;
						
						if (vimporteP > 0 ){
							$("#eventoCompromiso").val("CMP002"); 
						}else{
							$("#eventoCompromiso").val("CMP004");
						}
						
						$('#dt_grabacompD').dataTable().fnAddData( [
								'<td><input type="text" id="nDocRenglon" 	name="nDocRenglon" value="' + vdocren + '"></td>',
								'<td><input type="text" id="EP" 			name="EP" value="' + vep + '"></td>',
								'<td><input type="text" id="cEvento" 		name="cEvento" value="' + $("#eventoCompromiso").val() + '"></td>',
								'<td><input type="text" id="mImporte" 		name="mImporte" value="' + vimporteP + '"></td>',
								'<td><input type="text" id="mImporteNegativo" name="mImporteNegativo" value="' + vimporteN + '"></td>',
								'<td><input type="text" id="nFolioCompromisoD" name="nFolioCompromisoD" value="' + '<%=request.getParameter("folio")%>' + '"></td>',
								'<td><input type="text" id="cMes" 			name="nMesD" value="' + vMes + '"></td>',
								'<td><input type="text" id="cCentroContable" name="cCentroContable" value="' + $("#cCentroContable").val() + '"></td>'
							] );
							
						vdocren++ ;
					}
				}
			}
		}
		
		oTab2D.fnClearTable();
		for ( var i=0 ; i<nRows ; i++ ) {
			var aData = oTable.fnGetData( i );
			var jqInputs = $('input', aTrs[i] );
			if (jqInputs.length > 0) {
				for ( var j=0 ; j<12 ; j++ ) {
					var vep = aData[ 0 ] + "." + $.trim(aData[ 1 ]) ;
					var vimporteP = jqInputs[ j ].value ;
					vimporteP = quitaFmt(vimporteP);
					if (parseFloat(vimporteP) != 0 ) {
						var vimporteN = vimporteP * -1 ; 
						
						if (vimporteP > 0 ){
							$("#eventoPrecompromiso").val("CMP005"); 
						} else{
							$("#eventoPrecompromiso").val("CMP003");
						}
						
						vMes = (j + 1) ;
						
						$('#dt_grabaprecompD').dataTable().fnAddData( [
								'<td><input type="text" id="nDocRenglonPrecom" 			name="nDocRenglonPrecom" value="' + vdocren + '"></td>',
								'<td><input type="text" id="EPPrecom" 					name="EPPrecom" value="' + vep + '"></td>',
								'<td><input type="text" id="cEventoPrecom" 				name="cEventoPrecom" value="' + $("#eventoPrecompromiso").val() + '"></td>',
								'<td><input type="text" id="mImportePrecom" 			name="mImportePrecom" value="' + vimporteP + '"></td>',
								'<td><input type="text" id="mImporteNegativoPrecom" 	name="mImporteNegativoPrecom" value="' + vimporteN + '"></td>',
								'<td><input type="text" id="nFolioCompromisoDPrecom"	name="nFolioCompromisoDPrecom" value="' + '<%=request.getParameter("folio")%>' + '"></td>',
								'<td><input type="text" id="cMesPrecom"					name="nMesDPrecom" value="' + vMes + '"></td>',
								'<td><input type="text" id="cCentroContablePrecom" 		name="cCentroContablePrecom" value="' + $("#cCentroContable").val() + '"></td>'
							] );
						vdocren++ ;
					}
				}
			}
		}
		
		if ($("#mTotalAnticipo").val() == 0 && $("#nPorcAsignacion").val() > 0 ) {
			var nPorcAntic = quitaFmt($("#nPorcAsignacion").val()) * .01 ;
			var mTotal  = quitaFmt($("#mComprometer").val()) * nPorcAntic;
			var nPorcIVA   = parseFloat( $("#nPorcIVAAplicable").val() ) ;
			var mImporte = mTotal / (1 + nPorcIVA);
			var mIVA = mTotal - mImporte ;
			$("#mImporteAnticipo").val( mImporte );
			$("#mImporteAnticipoIVA").val( mIVA );
			$("#mTotalAnticipo").val(mTotal);
		}
			
		if (vdocren == 1) {
			alert("No se ha Calendarizado ningun Compromiso");
			return -1;
		}  

		getNextSequenceVal({seqName: "CO-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});

		var vtcont = $("#cTCont").val();
		var nMes = $("#FECHA_CARGA").val();
		var nMes = nMes.substring(5, 7 ) ;

		$("#nFolioCompromiso").val( '<%=request.getParameter("folio")%>' );
		$("#aEjercicioFiscal").val( $("#cEjercicio").val() );
		$("#fCarga").val( $("#FECHA_CARGA").val() ); 
		$("#fAplicacion").val( $("#FECHA_CARGA").val() ); 
		$("#cIdContrato").val( $("#cIdContratoCompromiso").val() );
		$("#cTipoContrato").val( vtcont.substr(0, 2) );
		$("#cCentroContable").val(  $("#cIdEntidadContable").val()  ); 
		$("#cRamo").val( "<%=cRamo%>" );

		if( $("#cUnidadResponsable").val() == "" )
			$("#cUnidadResponsable").val( "<%=cUR%>" );
			
		$("#nEnviadoSICOP").val("0");
		$("#nMes").val( nMes ); 

			if (bCarga) {
	
				queryFormPost("tCompromisoDDelete,tCompromisoDCreate", {async: false });
				
			}
			else {
				//Se genera el comprometido y el precompFinanciero			
				queryFormPost("tCompromisoECreate,tCompromisoDCreate,tPrecompromisoFinECreate,tPrecompromisoFinDCreate", {async: false });
			
				if(contrato !="CD") {
					$("#nEnviadoSICOP").val("2");
				}
				
				queryFormPost("tCompromisoEncabezadoUpdate", {async: false });
				switch ($("#cTCont").val()){
					case "OBRA":
						queryFormPost("tContratoAnticipoObrUpdate", {async: false });
						break;
					case "DIVERSO":
						queryFormPost("tContratoAnticipoDivUpdate", {async: false });
						break;
					case "FEDERALIZADO":
						queryFormPost("tContratoAnticipoFEDUpdate", {async: false });
						break;
				}
			}
			
			vrowAf = $("#rowsAffected").val();
			
			return 0;
        }


	function setSequenceVal(seqValue) {
		seqValue = 100000 + parseInt(seqValue,10);
		
		seqValue = "<%=cCentroContable%>" + "CO" + $("#cEjercicio").val() + seqValue;
		$("#caNoCompromiso").val( seqValue );
	}

</script>


</head>

<body id="dt_example">
	<form>

		<div id="container">
			<h1>Compromiso</h1>
			<table>
				<tr>
					<td>
						<div id="divImprimirOficio">
							<img src="imagenes/Imprimir.png" width="25" height="21" onClick="abrirDlgFirmantes();"> Oficio
						</div>
					</td>
				</tr>
			</table>
			<table id="clvcont">
				<tr><td>
					<select style="visibility: hidden" name="cEjercicio"	id="cEjercicio" class="form-select">
							<option value="0">cero</option>
					</select> <!-- VGC20150416 Se agrega bandera para saber si es un contrato descentralizado -->
						
						<input type="hidden" name="esSICOP" id="esSICOP" value=""/>
						<input type="hidden" name="esConvenio" id="esConvenio" value=""/>
						<input type="hidden" name="montoMaxCompromisoD" id="montoMaxCompromisoD" value=""/>	
						<input type="hidden" name="nesdescentralizado"	id="nesdescentralizado" value="" onchange="cambiaContratoDescentralizado()" />
						<input type="hidden" name="iEsAbierto" id="iEsAbierto" value="" onchange="cambiaContratoDescentralizado()" /> 
						<input type="hidden" name="cIdUsuarioResponsable" id="cIdUsuarioResponsable" value="Usuario"/> 
						<input type="hidden" name="usuario" id="usuario" value="<%=usuario.getLogin()%>" /> 
						<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /> 
						<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /> 
						<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" /> 
						<input type="hidden" id="rowsAffected" name="rowsAffected" value="0"/> 
						<input type="hidden" id="numcomp" name="numcomp" value="0"/> 
						<input type="hidden" id="nPorcAsignacion" name="nPorcAsignacion" value="0"/> 
						<input type="hidden" id="mImporteAnticipo" name="mImporteAnticipo" value="0"/> 
						<input type="hidden" id="mImporteAnticipoIVA" name="mImporteAnticipoIVA" value="0"/>
						<input type="hidden" id="mTotalAnticipo" name="mTotalAnticipo" value="0"/> 
						<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
						<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value=""/>
						<input type="hidden" id="cTipoPolizaPR" name="cTipoPolizaPR" value="PR"/> 
						<input type="hidden" id="cIdEntidadContable" name="cIdEntidadContable" value="<%=cCentroContable%>"/> 
						<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza" value="REGISTRO DEL CONTRATO FOLIO"/> 
						<input type="hidden" id="EPValida" name="EPValida" value=""/> 
						<input type="hidden" id="MesValida" name="MesValida" value=""/> 
						<input type="hidden" id="mImporteEPMes" name="mImporteEPMes" value="0"/>
						<input type="hidden" id="nPorcIVAAplicable"	name="nPorcIVAAplicable" value="0"/> 
						<input type="hidden" id="campo" name="campo" value=""/> 
						<input type="hidden" id="tablaEnc" name="tablaEnc" value=""/> 
						<input type="hidden" id="campoCondicion" name="campoCondicion" value=""/> 
						<input type="hidden" id="tablaDet" name="tablaDet" value=""/> 
						<input type="hidden" id="tipoAplicar" name="tipoAplicar" value=""/>
						<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado" value=""/> 
						<input type="hidden" id="eventoCompromiso" name="eventoCompromiso" value="CMP004"/>
						<input type="hidden" id="eventoPrecompromiso" name="eventoPrecompromiso" value="CMP003"> 
						<!-- Se agrega hidden para validar si tiene comprometido radicado -->
						<input type="hidden" name="tieneCOMPRadicado"	id="tieneCOMPRadicado" value="" /> 
						<!-- Se agrega hidden para validar si el contrato es federalizado y la partida es '43301' -->
						<input type="hidden" name="partidaFed" id="partidaFed" value="" />
						<input type="hidden" name="partidaFedCombinado"	id="partidaFedCombinado" value="" /> 
						<input type="hidden" name="mComprometidoFiscal" id="mComprometidoFiscal" value="" /> 
						<!-- Se agrega hidden para validar si tiene pagos pendientes de enviar a sicop -->
						<input type="hidden" name="tienePagoPendiente" id="tienePagoPendiente" value="0" /> 
						<input type="hidden" name="pagoCXP" id="pagoCXP" value="" /> 
						<input type="hidden" name="compromisoNegativo" id="compromisoNegativo" value="0" /> 
						<input type="hidden" name="compromisoPositivo" id="compromisoPositivo" value="0" /> 
						<!-- Se agrega hidden para validar si es un PEDIDO � SON EPS DE INGRESOS PROPIOS -->
						<input type="hidden" name="aplicaPreComp" id="aplicaPreComp"value="" />
						<input type="hidden" name="nFolioPago" id="nFolioPago" value=""/>
						<input type="hidden" id="firmanteExiste" name="firmanteExiste"/>
                        <input type="hidden" id="cNombreVo" name="cNombreVo" size=40 />
                        <input type="hidden" id="cPaternoVo" name="cPaternoVo"/>
                        <input type="hidden" id="cMaternoVo" name="cMaternoVo"/>
                        <input type="hidden" id="cPuestoVo" name="cPuestoVo" />
                        <input type="hidden" id="cNombreA" name="cNombreA" />
                        <input type="hidden" id="cPaternoA" name="cPaternoA"/>
                        <input type="hidden" id="cMaternoA" name="cMaternoA"/>
                        <input type="hidden" id="cPuestoA" name="cPuestoA"/>
                        <input type="hidden" id="cNombreE" name="cNombreE"/>
                        <input type="hidden" id="cPaternoE" name="cPaternoE"/>
                        <input type="hidden" id="cMaternoE" name="cMaternoE"/>
                        <input type="hidden" id="cPuestoE" name="cPuestoE"/>
                        <input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
                        <input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
                        <input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
                        <input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
                        <input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
                        <input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
                        <input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>
                        <input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value=""/>
                        
                        <!-- hidden para la captura de oficio delegatorio VoBo-->
                        <input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
                        <input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
                        <input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
                        <input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
                        <input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
                        <input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
                        <input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>
                        <input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value=""/>
						</td>

				</tr>
			</table>
			<div class="row">
				
				<div class="col-3">
					Clave de Contrato:
					<div class="input-group">
					<input type="text" class="AyudaSyC obligatorio form-control desahabilitado" maxlength="40" size="40" name="cIdContrato" id="cIdContratoCompromiso" readonly/>
					</div>
				</div>
				<div class="col-3">
					Tipo Contrato: 
					<input readonly type="text" name="cTCont" id="cTCont" class="form-control desahabilitado"/>
				</div>
				<div class="col-4">
					Tipo de Documento:
					<input name="cTipoContratoDiverso" id="cTipoContratoDiverso" class="form-control desahabilitado" readonly />
				</div>
			</div>
			<div class="row">
				<div class="col-3">
					R.F.C. :
					<input readonly type="text" maxlength="15" size="20" name="cIDRFC" id="cIDRFC" class="form-control desahabilitado" />
				</div>
				<div class="col-3">
					Nombre :
					<input readonly type="text"	maxlength="100" size="80" name="cnombre" ID="cnombre"	class="form-control desahabilitado" />
				</div>
				<div class="col-4">
					Unidad Administrativa:
					<input name="d_descripcion" id="d_descripcion" size="80" class="form-control desahabilitado" readonly/>
				</div>
			</div>
			<div class="row">
				<div class="col-6">
					Tipo de Adjudicaci&oacute;n
					<input readonly name="cTipoAdjudicacion" id="cTipoAdjudicacion" class="form-control desahabilitado"/>
				</div>
			</div>
			<div class="row">
				<div class="col-2">
					Monto Contrato:
					<input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mImporteTotal" id="mImporteTotal" class="form-control desahabilitado"/>
				</div>
				<div class="col-2">
					Compromiso Actual:
					<input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mComprometer" id="mComprometer" value="0" class="form-control desahabilitado"/>
				</div>
				<div class="col-2">
					Saldo Compromiso:
					<input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mComprometido" id="mComprometido" class="form-control desahabilitado"/>
				</div>
				<div class="col-2">
					Remanente Contrato:
					<input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="remanenteContrato" id="remanenteContrato" class="form-control desahabilitado"/>
				</div>
				<div class="col-2">
					Maximo Incremento:
					<input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="maximoIncremento" id="maximoIncremento" class="form-control desahabilitado"/>
				</div>
			</div>
			<table>
				<tr>
					<td>
						<div id="divRadicado">
							<label id="lbl_radicado">Presupuesto Radicado</label> 
								<input type="checkbox" id="chk_radicado" name="chk_radicado" value="S" checked="checked" disabled="disabled">
						</div>
					</td>
				</tr>
			</table>
			<div id="divTablas" style="display: none;">
				<div id="demo1">
				<br>
					<h5>Captura de Compromiso</h5>
					<!-- VGC20150416 Se agrega para filtrar las EPS segun la UR -->
					<table>
						<tr align="left" id="trNegativo" style="display: none;">
							<td><font color="red">Se pueden hacer compromisos Negativos y Positivos</font></td>
						</tr>
						<tr align="left" id="trPositivo" style="display: none;">
							<td><font color="red">Solo se pueden realizar compromisos Positivos</font></td>
						</tr>
						<tr align="left" id="trDeshabilitado" style="display: none;">
							<td><font color="red">No tienes Permisos para hacer compromisos</font></td>
						</tr>
					</table>
					<div style="display: none" id="divURComp">
						Unidad Ejecutora del Compromiso<br> 
						<select id="cUnidadCompromiso" name="cUnidadCompromiso" onchange="cambiaCC()"></select>
					</div>
					<br> <a href="javascript:void(0)" id="edit">editar</a>
					<table id="dt_compromiso" class="display">
						<thead>
							<tr>
								<th>Estructura Program&aacute;tica</th>
								<th>Clave <br>Interna</th>
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
						<tbody>
						</tbody>
					</table>
				</div>
				<br>
				<div id="ComprometidoDiv">
					<h5>Presupuesto Comprometido</h5>
					<table id="dt_comprometido" class="display">
						<thead>
							<tr>
								<th>Estructura Program&aacute;tica</th>
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
								<th>Anual</th>
								<th>Contrato</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<br>
				<div id="demo2">
					<h5>Presupuesto Disponible</h5>
					<table id="dt_suficiencia" class="display">
						<thead>
							<tr>
								<th>Estructura Program&aacute;tica</th>
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
								<th>Anual</th>
								<th>Contrato</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>

				<div id="demo3">
					<table id="dt_grabacompE" style="visibility: hidden">
						<thead>
							<tr>
								<th>nFolioCompromiso</th>
								<th>aEjercicioFiscal</th>
								<th>fCarga</th>
								<th>fAplicacion</th>
								<th>cIdContrato</th>
								<th>cTipoContrato</th>
								<th>cCentroContable</th>
								<th>cRamo</th>
								<th>cUnidadResponsable</th>
								<th>caNoCompromiso</th>
								<th>nEnviadoSICOP</th>
								<th>nMes</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" id="nFolioCompromiso" name="nFolioCompromiso"/></td>
								<td><input type="text" id="aEjercicioFiscal" name="aEjercicioFiscal"/></td>
								<td><input type="text" id="fCarga" name="fCarga"/></td>
								<td><input type="text" id="fAplicacion" name="fAplicacion"/></td>
								<td><input type="text" id="cIdContrato" name="cIdContrato"/></td>
								<td><input type="text" id="cTipoContrato" name="cTipoContrato"/></td>
								<td><input type="text" id="cCentroContable"	name="cCentroContable"/></td>
								<td><input type="text" id="cRamo" name="cRamo" value="16"/></td>
								<td><input type="text" id="cUnidadResponsable" name="cUnidadResponsable"/></td>
								<td><input type="text" id="caNoCompromiso" name="caNoCompromiso"/></td>
								<td><input type="text" id="nEnviadoSICOP" name="nEnviadoSICOP"/></td>
								<td><input type="text" id="nMes" name="nMes"/></td>
							</tr>
						</tbody>
					</table>
					<table id="dt_grabacompD" style="visibility: hidden">
						<thead>
							<tr align="center">
								<th>nDocRenglon</th>
								<th>EP</th>
								<th>cEvento</th>
								<th>mImporte</th>
								<th>mImporteNegativo</th>
								<th>nFolioCompromiso</th>
								<th>cMes</th>
								<th>cCentroContable</th>
							</tr>
						</thead>
						<tbody>
							
						</tbody>
					</table>
					<table id="dt_grabaprecompD" style="visibility: hidden">
						<thead>
							<tr align="center">
								<th>nDocRenglon</th>
								<th>EP</th>
								<th>cEvento</th>
								<th>mImporte</th>
								<th>mImporteNegativo</th>
								<th>nFolioCompromiso</th>
								<th>cMes</th>
								<th>cCentroContable</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>



			</div>
		</div>
		<div id="dialog-Procesando" title="Procesando">
			<div id="divEsperaProcesando" style="visibility: hidden"
				align="center">
				Espere por favor.... <img border="0" src="../imagenes/espera.gif"
					height="30">
			</div>
		</div>

		<div id="dialog-form" title="Aplicaci�n Presupuestal/Contable">
			<div id="divEspera" align="center">
				Espere por favor.... <img border="0" src="../imagenes/espera.gif"
					height="30">
			</div>
			<div id="divAplica">
				<iframe id="ifAplica" src="about:blank"></iframe>
			</div>
		</div>	
		
	</form>	
</body>
</html>
