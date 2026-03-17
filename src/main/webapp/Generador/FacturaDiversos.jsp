<%@page import="com.syc.contable.PagosDiversosBussinessLogic"%>
<%@page import="com.syc.ejercido.pagado.core.EgresosManager"%>
<%@page import="com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic"%>
<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";
	String U_LOGIN = "";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int idTipoCaso = c.getIdTC();

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}

	String mensaje = "";
	if (request.getParameter("msg") != null && !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = (Empleado) session.getAttribute(GestionInterface.ATT_EMPLEADO);
	EmpleadoArea ea = e.getArea(  );
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	String select = c.getTipoCaso().getGavetaAsociada() + "_G" + c.getIdGabinete();
	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null
			&& request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	algo = usuario.getLogin();
	U_LOGIN = usuario.getLogin();
	String numeroEmpleado = usuario.getNumeroEmpleado();
	
	/*VGC20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
	boolean esSAIAlterno = "true".equals( cabl.getSystemSetting("SAI_AMBIENTAL") );
	boolean esSAIFonden = "true".equals( cabl.getSystemSetting("SAI_FONDEN") );
	
	CatalogoURFIELBusinessLogic curbl = new CatalogoURFIELBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean permitePagoSinFIEL = curbl.permitePagoSinFiel(cUR);
	
	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;
	
	String cNombreElabora = e.getNombre();
	String cApellidoPaternoElabora  = e.getApellidoPaterno();
	String cApellidoMaternoElabora  = e.getApellidoMaterno();
	String cPuestoElabora = e.getCargo();	
	
	int nFolio = Util.folio( c );
	
	PagosDiversosBussinessLogic pdbl = new PagosDiversosBussinessLogic(GestionInterface.ATT_CONEXION);
	
	boolean autorizaPROFOEM ="S".equalsIgnoreCase(  cabl.getSystemSetting( "ACTIVA_VALIDACION_PROFOEM" ) );
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Documentos Diversos - Recepción de documentos</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<style type="text/css" title="currentStyle">
@import "css/demo_page.css";
@import "css/demo_table_jui.css";
@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>


<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker-es.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<script type="text/javascript" src="js/ImpuestosRetenciones.js"></script>
<script type="text/javascript" src="js/ValidaMesContable.js"></script>
<script type="text/javascript" src="js/FacturaDiverso.js"></script>
<script type="text/javascript" src="js/ActualizaFIEL.js"></script>
<script type="text/javascript" src="js/Firmantes.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

<script type="text/javascript" charset="utf-8">
	
	var cNombreElabora = "<%=cNombreElabora%>";
	var cApellidoPaternoElabora  = "<%=cApellidoPaternoElabora%>";
	var cApellidoMaternoElabora  = "<%=cApellidoMaternoElabora%>";
	var cPuestoElabora = "<%=cPuestoElabora%>";	
	var esConsulta = <%=esConsulta%>;
	var bClicBtn = false;
	var breturnVal = false;
	var bCOMSOC = false;
	var oTablevFact;
	var oTableEps;
	var excluirValidacion = 'MET8908305M9';
	var oTableVuelos;
	var oTableVuelosDet;
	var idOperacion = <%=id_oper%>;
	
	/*VGC20171019 Se guarda en base el prefijo de CxP*/
	var cxpPrefijo = "<%=cxpPrefijo%>";
	var numeroEmpleado = "<%=numeroEmpleado%>";	
	var esSAIAlterno = <%=esSAIAlterno%>;
	var esSAIFonden = <%=esSAIFonden%>;
	
	/* Variable que indica si el pago es con firma (FIEL) */
	var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
	var muestraDivImprimePoliza = false;
	let modalValidaFact;
	let modalOli;
	let modalCorreo;
	let ivaanticipo=0;
	
	 $(document).ready(
		 function() {
		 	
		 	queryFormPost({
				queryName : "tipoAutorizacionRead",
				async:false,
				callback: function(){
					if( $("#TipoAutorizacion").val() == "N" ){
						muestraDivImprimePoliza = true;
					}
				}
		   });
		   
		 	$("#mAmortizacionAnticipo").focus(
		 		function(){
		 			$(this).select();
		 		}
		 	);
		 	
		 	if (parent.document.getElementById("pb_cancel")){
	   			parent.document.getElementById("pb_cancel").style.visibility='hidden';
				parent.document.getElementById("pb_cancel").disabled=true;
			}
		 	
		 	modalValidaFact = new bootstrap.Modal(document.getElementById('dialog-validaFact'), 'data-bs-backdrop');
			modalOpinion = new bootstrap.Modal(document.getElementById('dialog-opinion'), 'data-bs-backdrop');

		 	modalOli= new bootstrap.Modal(document.getElementById('dialog-validaOLI'), 'data-bs-backdrop');
		 	modalCorreo= new bootstrap.Modal(document.getElementById('dialog-actualizaCorreo'), 'data-bs-backdrop');
		 	modalFirmantes= new bootstrap.Modal(document.getElementById('dialog-firmantes'), 'data-bs-backdrop');
		 	
			$("input.AyudaSyC").subIniciaDlg();
			$("input.autoCompletaSyC").subIniciaAutoCompleta();
			$("#mImporteRetencion").val(0);
			$("#Agregar1").button();
			$("#Borrar").button();
			$("#btnLayoutBoletos").button();
			$("#btnCalculaCedular").button();
			$("#agrega2").button();
			$("#nIdClaveEgresos2").button();
			$("#penas").css("display", "none");
			
				
		if( permitePagoSinFIEL ) {
			$("#AutorizaConFielTD").css("display","block");
		}else{
			$("#autorizadoPorFiel ").val("true");
		}
			
    	var aaa = <%=request.getParameter("folio")%>;
		var porretencion=0;
		
		$("#oficioDelegatorioCaptura").css("display","none");
		
		$("#oficioDelegatorioCapturaUpdate").hide();
 		$("#btnLayoutBoletos").hide();
 		$("#btnCalculaCedular").hide();
 		
 		/*VGC27032018 Para que se seleccione el tipo de delegatorio, por suplencia o delego.*/
		querySelectPost("catTipoSuplenciaRead", "tipoSuplencia",{async: false });
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo",{async: false });
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBoUpdate",{async: false });
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaUpdate",{async: false });
		/*FIN VGC27032018 Para que se seleccione el tipo de delegatorio, por suplencia o delego.*/
		
 		$("#verFondenSPAN").hide();	
		
		$( "#dFechaOficio" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});
			
		$( "#dFechaOficioUpdate" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});
		
		$("#oficioDelegatorioVoBo").hide();
		$("#oficioDelegatorioVoBoUpdate").hide();
		$( "#dFechaOficioVoBo" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});
			
		$( "#dFechaOficioVoBoUpdate" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});
		
		$( "#dlgSeleccionaFonden" ).dialog({
			autoOpen: false,
			height: 200,
			width: 320,
			modal: true,
			buttons: {
				"Aceptar": function() {
					if(<%=id_oper%> == 1  ){
						if( $("#proyectoFonden").val() == "" ){
							Swal.fire({ icon: 'warning',
										text: "No ha seleccionado un proyecto de FONDEN" });							
						}else{
							$.blockUI();
							$("#cProyectoFonden").val( $("#proyectoFonden").val() );
							queryFormPost({
								queryName:"proyectoFondenPDUpdate",
								async:false,
								callback:function(){
									$( "#dlgSeleccionaFonden" ).dialog("close");
								}
							});
							$.unblockUI();
						}
					}
					else
						$( "#dlgSeleccionaFonden" ).dialog("close");
					
				},
				"Cerrar":function(){
					if(<%=id_oper%> != 1  ){
						$("#dlgSeleccionaFonden").dialog("close");
					}else{
						if( $("#proyectoFonden").val() == "" ){
							Swal.fire({ icon: 'warning',
										text: "No ha capturado el proyecto de FONDEN. No podra continuar el pago hasta capturarlo." });							
						}else{
							$("#dlgSeleccionaFonden").dialog("close");
						}
					}
				}
			}
		});
		
		$( "#dialog-form" ).dialog({
			autoOpen: false,
			height: 400,
			width: 800,
			modal: true,
			beforeClose: function( event, ui ) {
				return bClicBtn;			
			}
		});

			creaDlgCargaBoletaje();		
			creaDlgProcesar();
 			creaDlgFoliosVuelos();
			creaDTFacturas();
			creaDialogJustificacion();

			$("#pbAgregar").button().click(function() {
					if (Number($("#mImporteFact").val()).toFixed(2)==0){
						Swal.fire("Revisar importe","No se puede agregar factura con importe 0", "info");
						return;
					}
					if ( $("#cFactura").val() == "" || Number( $("#cFactura").val() ) <= 0 ){
						Swal.fire("Capturar","Falta Capturar el Número de Factura","info");
						return;
					}

					if ( $("#mImporteFact").val() == "" ||  Number( $("#mImporteFact").val() ) <= 0 ){
						Swal.fire("Capturar","Falta Capturar el Importe de la Factura","info");
						return;
					}

					var aData = oTablevFact.fnGetData();
					for(var i=0; i<aData.length; i++) {
						var cFactura = aData[ i ][ 0 ] + aData[ i ][ 1 ];
						if( $("#cSerie").val() + $("#cFactura").val() == cFactura){
							Swal.fire("Ya capturada","La Factura ya está capturada","warning");
							$("#cSerie").val( "" );
							$("#cFactura").val( "" );
							$("#mImporteFact").val( "" );
							return;
						}
					}

					var mImporte = quitaFmt( $("#mImporteFact").val() );
					$("#mImporteFact").formatCurrency();
					$('#grdValidaFacturas').dataTable().fnAddData( [ $("#cSerie").val(), $("#cFactura").val(), $("#mImporteFact").val()] );
					mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) + Number( mImporte );
					$("#mTotalFacturaV").val( mImporte );
					$("#mTotalFacturaV").formatCurrency();
					$("#cSerie").val("");
					$("#cFactura").val("");
					$("#mImporteFact").val("");
			});	
			
			$("#grdCompromisos tbody").dblclick(function(event) {
				if(<%=id_oper == 1%>){
					 
  					$(oTableEps.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTableEps.fnGetPosition( this.nTr );
    					var aData = oTableEps.fnGetData( aPos[0] );
 					});
  					$(event.target.parentNode).addClass('row_selected');
     				var aPos = oTableEps.fnGetPosition( event.target.parentNode );
     				var aData = oTableEps.fnGetData( aPos );
					
     				$("#cEPborrar").val(aData[1]);
     				$("#mImporteBorrar").val(aData[2]);
     				$("#renglonBorrar").val(aPos+1);
     				
     				var montoRestar = 0.00;
     				montoRestar = Number($("#mImporteBorrar").val());

     				queryFormPost("tpagodiversodetalleDelete", {async: false });
     				queryFormPost("UpdateRecorridoRenglonPDiverso", {async: false });
					
					$('#grdCompromisos').dataTable().fnClearTable();
					consMovimientos();
				}
 			});

 			$("#grdValidaFacturas tbody").dblclick(function(event) {
 			
					$(oTablevFact.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTablevFact.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTablevFact.fnGetData( aPos[0] );

 					});
 					
  					$(event.target.parentNode).addClass('row_selected');
     				var aPos = oTablevFact.fnGetPosition( event.target.parentNode );
     				if (aPos != null){
	     				var aData = oTablevFact.fnGetData( aPos );
	     				var mImporte = Number( quitaFmt( $("#mTotalFacturaV").val() ) ) - Number( quitaFmt( aData[ 2 ] ) );
						$("#mTotalFacturaV").val( mImporte );
						$("#mTotalFacturaV").formatCurrency();
						oTablevFact.fnDeleteRow( aPos);
     				}
 			});

			$("#grdValidaFacturas tbody").click(function(event) {
					$(oTablevFact.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
					var aPos = oTablevFact.fnGetPosition( this.nTr );
					// Get the data array for this row
					var aData = oTablevFact.fnGetData( aPos[0] );
				});
				$(event.target.parentNode).addClass('row_selected');
			});
			
			
			$("#destGasto").change(function(){
			
				if ( $(this).val() == "AL" ){
					document.getElementById("Imp_Bruto").removeAttribute("readonly",false);
				}else if ( $.trim($(this).val()) != "" ){
					if( $("#cIdRFC").val() == "" ){
						$('#destGasto option[value=" "]').attr('selected','selected');
						Swal.fire("Seleccionar","Favor de seleccionar un Beneficiario","warning");
						return;						
					}else{
						//URVP.19112014 VALIDACIONES PARA SABER SI LLEVA O NO ANTICIPO, SI YA SE REGISTRO EL ANTICIPO EN CASO DE LLEVAR
						if ($(this).val()=="ANDV"){
							if ($("#llevaAnticipo").val()=="0"){
								$('#destGasto option[value=" "]').attr('selected','selected');
								Swal.fire("Este contrato no lleva anticipo", "Favor de seleccionar la opcion correcta.","warning");
								return;
							}
							if ($("#tieneAnticipo").val()!="0"){
							
								queryFormPost("remanentePagoDiversoAnticipoRead", {async: false });
								if($("#tieneRemanente").val() == "0"){
									$('#destGasto option[value=" "]').attr('selected','selected');
									Swal.fire("A este contrato ya se hizo el pago del anticipo", "Favor de seleccionar la opcion correcta.", "warning");
									return;
								}else if($("#tieneRemanente").val() == "1"){
									$("#tieneAnticipo").val("0");
									$(".subtotall").change();									
								}
							}
							$("#nPorcAmortizacion").val(0);
						}else{
							if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0"){
								$('#destGasto option[value=" "]').attr('selected','selected');
								Swal.fire("Este contrato lleva anticipo y no se le ha hecho", "Favor de seleccionar la opcion correcta.","warning");
								return;
							}
							agregaAmortizacion(true);
						}
					
						$("#mImporteBruto").attr("readonly", true);
						modalValidaFact.show();
						
						$("#uploadFacturasDiv").show();
						$("#facturasCapturadasDiv").hide();
						habilitaBtnCargaLayout();
						muestraCalculoCedular();	
					}
					if ( $.trim($(this).val())!="" && $("#cIdRFC").val() != ""){
						if($("#numPaso").val() == "1" )
							$("#EditaFacturas").css('visibility', 'visible');
							$("#EditaOpinion").css('visibility', 'visible');
							
					}else{
						$("#EditaFacturas").css('visibility', 'hidden');
						$("#EditaOpinion").css('visibility', 'hidden');	
					}
				}
			});
			
			$('#Descrip_Concepto').bind('copy paste', function (e) {       
				e.preventDefault();
	    	});

		  $("#OIRAUSU").val( "<%=algo%>" );
          $("#cCentroContable").val( "<%=cCentroContable%>");			
          $("#id_caso").val(aaa);
          $("#cNoFactura").val(aaa);
          $("#tabs").tabs( {
          	"show": function(event, ui) {            
          		var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
          		if ( oTable.length > 0 ) {                
          			oTable.fnAdjustColumnSizing();            
          		}
          	}    
          } );		
          $('#tblDevengado1').dataTable(
				{
				"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
		oTableEps = $('#grdCompromisos').dataTable(
				{
				"bPaginate": false,
				"iDisplayLength": 20,        			
				"bLengthChange": false,
        		"bFilter": false,
        		"bSort": false,
        		"bInfo": false,
        		"bAutoWidth": false,
				"sScrollY": 100,
				"bJQueryUI": true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"

				} );
		$('#grdFacturas').dataTable(
				{
					sScrollY: "150px",
					sScrollX: "700px",
					sScrollXInner: "200%",
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
				
		$('#grdRetClave').dataTable(
				{
				"bPaginate": false,
        			"bLengthChange": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false,
        			"bAutoWidth": false,
					"sScrollY": 100,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"

				} );
 oTable = $('#tretencion').dataTable( );
		$('#tretencion').dataTable({
			"bPaginate": false,
  			"bLengthChange": false,
  			"bFilter": false,
  			"bSort": false,
  			"bInfo": false,
  			"bAutoWidth": false,
			"sScrollY": "80%",
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers"
				} );
	$(function() {
		
		$( "#desde" ).datepicker({
				showOn: "button",
		//		dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
    } );
    
	$(function() {
		$( "#hasta" ).datepicker({
				showOn: "button",
		//		dateFormat: "yy/mm/dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
				} );
	$(function() {
		$( "#fRecepcion" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true 
		});
	});
	
	$(function() {
		$( "#DCD_FECHA_FACTURA" ).datepicker({
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true
		});
	});	

	var suma = 0;
	var sredondea=0;
	var res_iva=0;
	
	cssReadOnly();
	queryFormPost({
			queryName:"readImporteBrutoPago", 
			async:false, 
			callback:function(){
				if( $("#Imp_Bruto").val() == "" )
					$("#Imp_Bruto").val("$0.00");
			} 
		});		
		

	$(".subtotall").change(function(){
		validaPenalizacion();
		calculaIVA();
	});
		
});

	var es_mx = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
		sLoadingRecords : "Cargando...",
		sInfo : "Registros _START_ al _END_ de _TOTAL_",
		sInfoEmpty : "Registro 0 al 0 de 0",
		sInfoFiltered : "(filtered from _MAX_ total entries)",
		sInfoPostFix : "",
		sInfoThousands : ",",
		sSearch : "Filtro:",
		oPaginate : {
			sFirst : "Primero",
			sPrevious : "Ant.",
			sNext : "Sigte.",
			sLast : "&Uacute;ltimo"
		}	
	
	};

function cssReadOnly(){
	$( "[readOnly]" ).each(function(){	
		$(this).addClass("notEditable");	
	});
}
	
	
	function aceptarOli(){
		var vEP = $("#EP").val();
		var vCapitulo = "%" + vEP.substring(31, 36) + "%";
		var vCartera = vEP.substring(44, 55);
		var vUnidEj = vEP.substring(56, 59);
		var vUnidRe = vEP.substring(60, 64);
		var url = '../proimpro/readInfo';
		$.ajax({
			url : url,
			dataType : 'json',
			data : {
				"accion" : "READ_IMPORTEOLI",
				"cartera" : vCartera,
				"capitulo" : vCapitulo,
				"oli": $("#cOLI").val(),
				"ue":vUnidEj
			},
			async : false,
			success : function(RS) {
				var exito = RS.success;
				if(exito=="true"){
					var MontoOlis = RS.data_1.result;
					$("#cartera").val( vCartera );
					$("#capitulo").val( vEP.substring(31, 36) );
					$("#UE").val( vUnidEj );
					$("#mImporteCartera").val("0.00");
					queryFormPost("vAcumulaImporteCarteraEP", {async: false });
					var importeTotal = Number( $("#mImporteCartera").val() ) + Number( $("#montoDev").val() );
					var importeTotal = importeTotal.toFixed(2);
					
					if( Number( importeTotal ) <= Number( MontoOlis ) ){
						cambioMovmientos();	
					}else{
						alert("El Importe Capturado sobregira la Cartera ");
					}
				}else{
					var msg = RS.data_1.result;
					alert("Error al intentar validar OLI:\n"+msg);
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
						+ textStatus + "\n" + errorThrown);
				r = true;
			}
		});
		modalOli.hide();
	}
	
	function cerrarOli() {
		breturnVal = false;
		modalOli.hide();
	}
	
	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
  		if ($("#docAplicado").val() == "S") {
  			Swal.fire("Documento ya fue aplicado", "Se avanzará a modo de CONSULTA", "success");
  		}else {
	  		
			try{
				//validaciones de la forma
				//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
	
				//Guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
				p.gestion.setEjercicioFiscal( $("#aEjercicioFiscal").val() );
				p.gestion.setConceptoMov("Aplicación Pago Obra");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
				p.gestion.setAplicadoCont("false");
	
				var esEFO = validaEFO();
				if( esEFO ){
					Swal.fire({ icon: 'warning',
								text: "No se puede realizar el pago a un EFO. Solicite mas informacion con el administrador" });					
					return false;
				}				
					
				if(  $("#cIdRFC").val().replace(/ /g, '') != excluirValidacion ){
					$("#facturasDiferentes").val("0");
					queryFormPost("rfcDiferentesRead", {async : false});
					if( $("#facturasDiferentes").val() != "0" ){
						Swal.fire({ icon: 'warning',
									text: "El pago contiene facturas para otro proveedor diferente a [" + $("#cIdRFC").val()  + "] por lo que no puede continuar." });						
						return false;
					}
				}
					
				var nretval = cmdGuardar();
				if (nretval == -1) {
					
					return false;
				}
	
	
				if(id_oper==2){
					$("#COMSOCAutoriza").val("");
					queryFormPost("BuscaCOMSOCAutorizaRead", {async : false});
					if ( $("#COMSOCAutoriza").val() == "0"){
						Swal.fire({ icon: 'info',
									text: "Pago en Proceso de Autorización del Área Normativa" });						
						return false;
					}

					if ( $("#COMSOCAutoriza").val() == "-1"){
						Swal.fire({ icon: 'info',
									text: "Pago fue Rechazado por el Área Normativa, <BORRAR>" });						
						return false;
					}		 

						$("#campo").val( "nFolio" + $("#cDocumento").val() );
						$("#tablaEnc").val( "t" + $("#cDocumento").val() + "Encabezado" );
						$("#campoCondicion").val( "caNoContrarrecibo" ); 
						$("#tablaDet").val( "t" + $("#cDocumento").val() + "Detalle" ); 
						$("#tipoAplicar").val( $("#cDocumento").val() );
						
						tipoFirmantes();
	
				}
				
			}
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
		}
		return valida_campos;
  	}

	function procesar(){
		$( "#dialog-Procesando" ).dialog( "open" );
		return breturnVal;
	}
	
	function creaCasoComsoc(tipoDocumento, folioDocumentoPago){
		
		$.ajax({
			url : '../servlet/ComsocAutorizacionServlet',
			dataType : 'json',
			type :"POST",
			data : {
				"h_TipoPago" : tipoDocumento,
				"hFolioPago": folioDocumentoPago
			},
			async : false,
			success : function(json) {
				var exito = json.success;
				if( exito == "true"){
					Swal.fire("Pago en proceso","El Pago está en Proceso de Autorización por parte del Área Normativa","warning");
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				Swal.fire({ icon: 'warning',
							text: "Advertencia: " + xhr.responseText + "\nEstatus: "
							+ textStatus + "\n" + errorThrown });				
				return false;
			}
		});
		
	}


 	function fnAplicaMotor()
 	{
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}

  	function onPostSubmit(id_oper){//validaciones del boton enviar
  		if(<%=id_oper == 1%>){ 
  			Swal.fire({ icon: 'info',
						text: "Cuenta por Pagar: "+$("#caNoContrarrecibo").val() });				  			
  		} //URVP.22092014
  		//valida que el documento de la Justificacion este agregado si se elimino la retencion
  		queryFormPost("retencionEliminada", {async:false});
		queryFormPost("validaDoctoJustificacion", {async: false });
		
		if( $("#lEliminaRetencion6IVA").val() == 1 && $("#doctoCapturado").val() == 0){
			Swal.fire({ icon: 'warning',
						text: "Debe adjuntar la documentacion que Justifique el pago sin retenciones" });			
  			dlgJustificacion();	
  			return false;	
		}
  		
  		return true;
  	}
  	
    function onLoadPlantilla(){
    
    	carga();
    	
    	if(<%=id_oper > 1%>){
    		$("#EditaFacturas").css('visibility', 'hidden');
			$("#EditaOpinion").css('visibility', 'hidden');

    		$("#ctaBancaria").attr('disabled', true);
    		$("#noRecepcion").attr('disabled', true);
    		$("#noPenalty").attr('disabled', true);
    		$("#cObservaciones").attr('disabled', true);
    	}
    	
		if(<%=id_oper == 1%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}
		if(<%=id_oper == 2%>){
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=true;
		}

		if(<%=id_oper == 3%>){
			if( muestraDivImprimePoliza ){
				$("#EditaFirmas").css('visibility', 'visible');
			}
			
			if( parent.document.getElementById("pb_cancel") ){
				parent.document.getElementById("pb_cancel").style.visibility='hidden';
				parent.document.getElementById("pb_cancel").disabled=true;
			}			
		}
  	}
  	function ResponsableSiguiente(id_oper){

  		 if(id_oper==1){
  		 	return "AUTORIZA_" + $("#cDocumento").val();
		 }else if(id_oper==2){
	  		return "CONSULTA_" + $("#cDocumento").val();
		 }
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1){
  		 	return "autoriza_factura";
  		}else if(id_oper==2){
	 		return "consulta_factura";
  		 }
  	}

	function onPostDisplay(id_oper){
		if ($("#docAplicado").val() == "S") {
			parent.document.getElementById("pb_send").disabled = false;
			parent.document.getElementById("pb_send").click();	
		}
			
	}
	
	function onCancel(id_oper){
		queryFormPost(	"TFactDiversoEncabezadoUpdate", {async: false });
		if ($("#docAplicado").val() != "S") {
			queryFormPost("borraFactRelacionPagoBorrado", {async: false });
			$("#estatusRecepcion").val("Emitida");
			queryFormPost("updateEstatusRecepcion",{async: false });
			$("#noRecepcion").attr('disabled', false);
			$("#noPenalty").attr('disabled', true);
		}
		return true;
	}
	
	function fnClickAddRowComp() {

		dlgJustificacion();
			
		queryFormPost("leeImporteTotalDiversoEncabezado",{async: false });
		queryFormPost("leeImporteTotalDiversoDetalle",{async: false });

		var importeDiferencia = 0.00;
		var importeDiferenciaOriginal = 0.00;
		importeDiferencia = Number(Number($("#importeTotalEncabezado").val())-Number($("#importeTotalDetalle").val())).toFixed(2);
		importeDiferenciaOriginal = Number(Number($("#importeTotalEncabezado").val())-Number($("#importeTotalDetalle").val())).toFixed(2);
		if (importeDiferencia<0)
			importeDiferencia*=-1;
		
		if (importeDiferencia <= 0.02){
			if (importeDiferencia > 0){
				$("#importeTotalDetalle").formatCurrency();
				Swal.fire({
					  title: 'Desea continuar?',
					  text: "El Importe Neto se afectara contablemente por "+$("#importeTotalDetalle").val(),
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (!result.isConfirmed) {
						  Swal.fire("Borrar","Favor de Borrar el tramite.", "error");
						  return;
					  }
					})
				//if (!confirm("El Importe Neto se afectara contablemente por "+$("#importeTotalDetalle").val()+", ¿Desea continuar?")){
					//alert("Favor de Borrar el tramite.");
					//return;
				//}
				var aux=0.00;
				aux = Number(Number($("#DCD_NETO").val())-Number(importeDiferenciaOriginal)).toFixed(2);
				$("#DCD_NETO").val(aux);
				aux = Number(Number($("#DCD_RETENCION").val())+Number(importeDiferenciaOriginal)).toFixed(2);
				$("#DCD_RETENCION").val(aux);
				$("#mImporteDiferenciaCentavos").val(importeDiferenciaOriginal);
				queryFormPost("updateEncabezadoNetoRetencionDiverso",{async: false });
			}
		}else{
			Swal.fire("No se puede seguir con el pago","El detalle no corresponde al encabezado.","error");
			return;
		}
	
		$("#DCD_TIPO_OPE").attr('disabled', false);//URVP.17062014 se habilita para poder obtener su informacion y se deshabilita al finalizar de contrareibo()
	
		//Verificar importes de iva
		var importeDifIVA = 0.00;
		importeDifIVA = Number(Number($("#importeTotalIvaEnc").val())-Number($("#importeTotalIvaDet").val())).toFixed(2);
		if(importeDifIVA != 0) {
			$("#importeDifIVA").val(importeDifIVA);
			queryFormPost("UpdateImporteIVA", {async: false });
		}
		
		
		CalculaRetencionesG();		
		$('#grdFacturas').dataTable().fnAddData( [
		$("#DCD_FACTURA").val(),
		$("#DCD_FECHA_FACTURA").val(),
		$("#DCD_TBEN").val(),
		$("#DCD_CBEN").val(),
		$("#DCD_TIPO_OPE").val(),
		$("#DESCRIPCION20").val(),
		$("#DCD_IMP_BRUTO").val(),
		$("#DCD_IVADES").val(),
		$("#DCD_IVA").val(),
		$("#DCD_ISR").val(),
		$("#DCD_MIL5").val(),
		$("#DCD_MIL2").val(),
		$("#DCD_CONTRIBUCION").val(),
		$("#DCD_OTRAS_RET").val(),
		$("#DCD_PENALIZACION").val()
		] );
		
		$("#fRecepcion2").val($("#fRecepcion").val());
		getNextSequenceVal({seqName: "CR-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
		setTimeout("contrareibo()",1000);
		
		parent.doSave();
		
		if (bCOMSOC){
			creaCasoComsoc($("#cDocumento").val(), $("#id_caso").val());	
		}
		$("#numPaso").val("5");
	}
	
function setSequenceVal(seqValue) {
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "1" + seqValue.substr(seqValue.length - 5);
	seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#aEjercicioFiscal").val() + seqValue;
	$("#caNoContrarrecibo").val( seqValue );
}

	function contrareibo(){
		$("#cIDContrato").attr('disabled', false);
		queryFormPost("obtieneIVAparaDocComp", {async: false });
		queryFormPost("SIG_FOLIO_CONTRAODIVERSOSUpdate", {async: false });
		queryFormPost("StatusAnticipoFACTDIVERSOUpdate", {async: false });		
		var aux = $("#DCD_IMP_BRUTO").val();
		$("#DCD_IMP_BRUTO").val(Number($("#DCD_IMP_BRUTO").val())-Number($("#mImporteSancion").val())); //se resta la sancion para mandar a doc comp el importe neto al proveedor
		queryFormPost("tDocumentacionComprobatoriaDetCreate2",{async: false }); //queryFormPost("tDocumentacionComprobatoriaDetCreate",{async: false }); //URVP.18022015
		$("#DCD_IMP_BRUTO").val(aux); //se regresa el valor que traia el campo
		$("#DCD_TIPO_OPE").attr('disabled', true);//URVP.17062014 se muestra "solo lectura" nuevamente
		$("#divImprime").show();
		$("#Agregar1").attr('disabled', true);
		setTimeout("elRetardo()",1000);
		
	}					
	function fnClickAddRowB(A, B,C) {
		$('#tretencion').dataTable().fnAddData( [
				A,
				B,
				C] );
	}
	function fnClickAddRowH(A, B, C,D,E) {
		$('#grdCompromisos').dataTable().fnAddData( [A,B,C,D,E]);	
	}
				

	function Borrar1(){
		if ($("#docAplicado").val() != "S") {
			queryFormPost("borraFactRelacionPagoBorrado", {async: false }); //URVP.08092014 VALIDAR EN CASO DE YA APLICADOS
			queryFormPost("tPagoDiversoBoletajeAvionDelete", {async: false });
			queryFormPost("tTempVuelosEnCapturaDelete", {async: false });
			$("#estatusRecepcion").val("Emitida");
			$("#noRecepcion").attr('disabled', false);
			queryFormPost("updateEstatusRecepcion",{async: false });
			$("#noRecepcion").attr('disabled', true);
		}
		
		if (idOperacion == 2 ) {
			queryFormPost("readPenalty", {async: false });
		} else {
			$("#mPenalizacion2").val($("#noPenalty").val());
		}
		
		if ($("#mPenalizacion2").val() !="-1") {
			queryFormPost("actualizarEstatusPenasBorrar",{async: false });
		}
		
		$.blockUI({message : "Procesando espere..."});
		
		$("#elcontra").val($("#caNoContrarrecibo").val());

		$("#COMSOCAutoriza").val("");
		queryFormPost("BuscaCOMSOCAutorizaRead", {async : false});
		if ( $("#COMSOCAutoriza").val() == "0"){
			Swal.fire("Pago en Proceso de Autorización del Área Normativa", "No es posible Descarta el Documento", "warning");
			return false;
		}

		if ( $("#COMSOCAutoriza").val() == "1"){
			Swal.fire("Pago Autorizado por el Área Normativa", "No es posible Descarta el Documento", "warning");
			return false;
		}
		
		$.unblockUI();
		
		parent.document.getElementById("pb_send").disabled = true;
		parent.document.getElementById("pb_cancel").disabled = false;
		parent.document.getElementById("pb_cancel").click();
		parent.document.getElementById("pb_cancel").disabled = true;
		
	}
	
	
	function cmdRegresar(){
		self.location="../caso/inbox.jsp";
	}	
	
	
	function reten(){
		$("#cIDContrato").val($("#cFolioPAGODIVERSO").val());
		if ($("#cIDContrato").val()!=""){
			
			FiltroRetencionesD();
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC").val());			//quitar
			$("input[id='nIdConcepto']").val($('#TIPO_CONCEPTO option:selected').val());
			$("input[id='tConcepto2']").val($('#TIPO_CONCEPTO option:selected').text());
			$("input[id='noFactura2']").val($("input[id='cNoFactura']").val());
			$("input[id='cIdTipoOperacion2']").val($('#cIdTipoOperacion option:selected').val());
			$("input[id='tipoOper']").val($('#TIPO_OPERACION option:selected').val());
			$("input[id='impTotal']").val($("input[id='mImporteNeto']").val());
			
			$("#impTotal2").val($("#impTotal").val());
			$("#iva").val($("#nPorcIVAAplicable").val());
			$("#poriva").val($("#mImporteIVA").val());
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });		 
			$(".pasoDos").show();
			$("#cIDContrato").attr('disabled', true);
			$(".paso01").attr('disabled', true);
			$(".subtotall").attr('disabled', true);
			$("#guardar").attr("disabled", true);
			$("#numPaso").val('2');
			DOCUMENTACION();
			claves();
			setTimeout("retraso3()",100);				
			setTimeout("retraso5()",100);	
			
			/* CONAFOR, No mostraba toda la informacion de Pestaña de Devengado*/
			$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
			querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
			concepto();
			$("#agrega2").attr('disabled', true);
			$("#TIPO_CONCEPTO").attr('disabled', true);
			$("#ALM").attr('disabled', true);
			
		}else{
			$(".pasoDos").hide();
			$(".pasoTres").hide();
		}
	}	

	function retraso5(){
		if ($("#cllave").val()!=""){
			$(".paso9").attr('disabled', true);
			//valida que no se vea la liga de captura de justificacion si ya ha sido agregada
			queryFormPost("retencionEliminada", {async:false});
			queryFormPost("validaDoctoJustificacion", {async: false });
		
			if( $("#lEliminaRetencion6IVA").val() == 1 && $("#doctoCapturado").val() == 0){
					Swal.fire ("Adjuntar documentación","Debe adjuntar la documentación que Justifique el pago sin retenciones", "warning");
	  				dlgJustificacion();	
	  				return false;	
			}
			$(".pasoTres").show();	
		}
	}		
	
	function retraso3(){
		if ( $("#caNoContrarrecibo").val() !="0"  && (<%=id_oper%> == 2 )  ){
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });
			queryFormPost("tFacturaDiversoConsultaRead", {async: false });
			//calculaIVAConsulta();
			$("#Agregar1").attr('disabled', true);   
			$(".paso03").attr('disabled', true);   
			
			$("#numPaso").val('6');		

			Swal.fire("Guardar","Para finalizar el trámite presiona el botón Guardar.", "success");
			
		}else{
			$(".paso03").attr('disabled', false); 
		}
	}	
	function DOCUMENTACION(){    	
		var szWhere = "";     
		szWhere = " caNoContrarrecibo ='"+  $("#caNoContrarrecibo").val()+"'";
		
		var elMonto = "";     
		var este=0;          
		var szTabla = "DOCUMENTACION_RELACION_GASTO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {              			
				$('#grdFacturas').dataTable().fnAddData( [
				j[i].Col0, j[i].Col1, j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8,j[i].Col9,j[i].Col10,j[i].Col11,j[i].Col12,j[i].Col13,j[i].Col14	
				] );
				$("#DCD_FACTURA").val(j[i].Col0);
				$("#DCD_FECHA_FACTURA").val(j[i].Col1);
				$("#DCD_TBEN").val(j[i].Col2);
				$("#DCD_CBEN").val(j[i].Col3);
				$("#DCD_TIPO_OPE").val(j[i].Col4);
				$("#DESCRIPCION20").val(j[i].Col5);
				$("#DCD_IMP_BRUTO").val(j[i].Col6);
				$("#DCD_IVADES").val(j[i].Col7);
				$("#DCD_IVA").val(j[i].Col8);
				$("#DCD_ISR").val(j[i].Col9);
				$("#DCD_MIL5").val(j[i].Col10);
				$("#DCD_MIL2").val(j[i].Col11);
				$("#DCD_CONTRIBUCION").val(j[i].Col12);
				$("#DCD_OTRAS_RET").val(j[i].Col13);
				$("#DCD_PENALIZACION").val(j[i].Col14);		
				$("#DCD_NETO").val($("#mImporteNeto").val());				
				$("#DCD_SANCION").val($("#mImporteSancion").val());				
				$("#DCD_DEVOL").val($("#mImporteDevolucion").val());	
				$("#numPaso").val("4"); 
				
			}
			// Si Trae Informacion y es num Paso 4 
			if($("#numPaso").val() == "4" && <%=id_oper%> == 1){
			
				$("#numPaso").val("5");
				parent.document.getElementById("pb_send").disabled=false;//para CONAFOR
				parent.document.getElementById("pb_send").style.visibility='visible'; //para CONAFOR
				parent.document.getElementById("pb_save").disabled = true; //para CONAFOR
				parent.doSave();
				$("#divImprime").show(); //para CONAFOR*/
				$("#Agregar1").attr("disabled",true);
				
			}
			  
			
		}) ;  
		
	}
	function  compDocumentacion(){
		$("#DCD_FACTURA").val( $("#cNoFactura").val());
		$("#DCD_FECHA_FACTURA").val($("#fRecepcion").val());
		$("#DESCRIPCION20").val($("#nPorcIVAAplicable").val());
		
		if(  $("#tipoAmortizacion").val() != "1"  )
			$("#DCD_IMP_BRUTO").val($("#mImporteBruto").val());
		else{
			var valImpBrutoDCD = ( parseFloat(  $("#mImporteBruto").val() + $("#mAmortizacionAnticipo").val() ) * 100 ) / 100;
			$("#DCD_IMP_BRUTO").val( valImpBrutoDCD.toFixed(2) );
		}
		$("#DCD_IVA").val($("#mImporteIVA").val());
		$("#DCD_NETO").val($("#mImporteNeto").val());				
		$("#DCD_SANCION").val($("#mImporteSancion").val());				
		$("#DCD_DEVOL").val($("#mImporteDevolucion").val());
		$("#DCD_CONCEPTO").val( $("#Descrip_Concepto").val());
		
		if(  $("#tipoAmortizacion").val() != "1"  )
			$("#DCD_AMORT").val( $("#mAmortizacionAnticipoMasIva").val() );
		else
			$("#DCD_AMORT").val( $("#mAmortizacionAnticipo").val() );
			
		$("#DCD_RETENCION").val($("#mImporteRetencion").val() );
		$("#DCD_CONTRIBUCION").val( $("#DCD_CONTRIBUCION_D").val() ); //urvp.06012014 se cambia por mImporteFlete4
		$("#DCD_PENALIZACION").val( $("#mImportePenalizacion").val() );
				
	}
	function claves(){
		       
		var contador=0;                          
		var szWhere = "";     
		szWhere = " tr.nFolioPAGODIVERSO ='"+  $("#id_caso").val()+"'";
		var elMonto = "";     
		var este=0;      
		var szTabla = "CLAVES_DIVERSO";                                                                                         
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
			var options = '';
			for (var i = 0; i < j.length; i++) {
				$("#montoDev").val(j[i].Col3);
				$("#montoDev").formatCurrency();
				var mImporteADev = $("#montoDev").val();
				$('#grdCompromisos').dataTable().fnAddData( [ j[i].Col0, j[i].Col2, mImporteADev ]);
				$("#montoDev").val("");
				contador = contador + 1;
				$("#cllave").val(contador);
				$("#numPaso").val("3"); // Para CONAFOR
				
			}
			
			if($("#numPaso").val() == "3" && <%=id_oper%> == 1 ){
				
				$("#nIdClaveEgresos2").attr('disabled', true);
				$("#agrega2").attr('disabled', true);
				$("#TIPO_CONCEPTO").attr('disabled', true);
				//$("#TIPO_MOVIMIENTO").attr('disabled', true); URVP.18092014
				$("#ALM").attr('disabled', true);
				DOCUMENTACION();
				$(".pasoTres").show();
			}
			
		});  	
	}			  

	function cambioss(){
		$('#grdRetClave').dataTable().fnClearTable();
		$("#EP").val( $("#ep").val() );
		var vEp = $("#EP").val();
		var vCartera = vEp.substring(44, 55);
		var vCapitulo = vEp.substring(31, 32);
		if(vCartera == "00000000000" || "1234".indexOf(vCapitulo) >= 0 || $("#IEsPasivo").val() == "1"){
			cambioMovmientos();
		}else{
			modalOli.show();
		}			
	}
	
	function cambioMovmientos(){
		$("#montoDev").val(quitaFmt($("#montoDev").val())); //URVP.20102014 Se quita formato para evitar error de conversion en caso de traer signos
        var vOGT = $("#ep").val();
	    vOGT = vOGT.substring(31, 36);
		$("#vOGT").val( vOGT );

		queryFormPost("tMsgPartidaRead", {async: false });
		if( $("#obs").val() != '' ){
			Swal.fire("ATENCIÓN",$("#obs").val(),"warning");
		}

		var validM;
		var validacion=0;				
		validM = Number( $("#saldoCompM").val() ) + Number( $("#montoDev").val() );	
		validM = validM.toFixed(2);
		
		if ( Number( validM ) >  Number( $("#mImpEjercer").val()) ){
			Swal.fire("Verifique!","Ha superado el Importe a Ejercer","warning");
			$("#montoDev").val("");
			$("#ep").val("");
			$("#codSIAFF").val("");
			return;
		}
		
		validM = Number( $("#montoDev").val() );						                           
		
		var campos="";
		var szWhere="";
		var elMonto=12;
		
		$("#cMes").val($("#fRecepcion").val().split("/")[1]);

		campos = $("#cMes").val() + ", '" + $("#cIdContrato").val() + "', '" + $("#cCentroContable").val() + "', 'DI', '" + $("#ep").val() + "'";
		szWhere = "";
        var szTabla = "SALDOS_DISPONIBLE_PAGOOBRA";
        
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto,Campos:campos, Order:" order by 1 desc ", ajax: 'false'}, function(j){
			var acumulado = 0.0;
			for (var i = 0; i < j.length; i++) {            
				acumulado = parseFloat(acumulado) + parseFloat(j[i].Col2);
				$("#TOTALSUBCUENTA").val(acumulado);
			}	
			
		   	acumulado = acumulado.toFixed(2);

		   	if (acumulado < validM){
		   		Swal.fire({ icon: 'warning',
							text: "La cuenta no tiene suficiente saldo comprometido" });				
				return;
			}	
			var resto = parseFloat(validM);
			var aplicar = 0.0;
			for (var i = 0; i < j.length; i++){
				$("#nMes").val( j[i].Col0 );
				
				var SuficMes = parseFloat(j[i].Col2); 
				SuficMes = SuficMes.toFixed(2);
				if (SuficMes > 0){
					if (resto - SuficMes > 0){
						aplicar = SuficMes;
					}else{
						aplicar = resto;
					}
					resto = resto-aplicar;
					if (parseFloat(aplicar) > 0.0) {
						$('#grdRetClave').dataTable().fnAddData([j[i].Col1, j[i].Col0, aplicar]);
					}
					
					var vOGT = $("#ep").val();
					vOGT = vOGT.substring(31, 36);
					$("#vOGT").val( vOGT ); 
					queryFormPost("BuscaPartidaExcepRead", {async : false});
					
					if ($("#TIPO_CONCEPTO").val() != "AL"){
						queryFormPost("BuscaPartidaExcepRead", {async : false});
						$("#altaAlmacen").val("");
						$("#cAnioFactEP").val("");
						$("#nFacturaEP").val("");
							
						if (vOGT == $("#partida").val()){
							querySelectPost("CatalogoAlmacenRead","ALM", {async: false });

						} else{
							$("#altaAlmacen").hide();
							$("#cAnioFactEP").hide(); 
							$("#nFacturaEP").hide();			
							querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
						}
					}
				}
			}
		}) ;  
	}
			
	var TotalNeto=0;
    var	TotalRetenciones=0;
    
    

	function actualizaretencion(){
						elParametro = $("#campoRetencion").val()+ " = "+ $("#campoRetencion").val() +" + " + $("#TotalRetenciones").val()
						+" where  nFolioPAGODIVERSO = "+$("#id_caso").val()+ " AND cEvento !='ANTICIPO_DIV' AND nDocRenglon=1 " ;

		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "ACTUALIZARETENCIONDIVERSOS", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});
	}
	
	
	
	
	function carga(){
		queryFormPost("tEjercicioRead",{async: false });
		$("#cEjercicio").val( $("#aEjercicioFiscal").val() );
		
		$("#cRamo").val( "<%=cRamo%>" );
		$("#cUnidadResponsable").val( "<%=cUR%>" );
		$(".divAutorizar").hide();
		$("#divImprime").hide();
		$("#divImprimePoliza").hide();
		$("#divImprimeAnexo").hide();				
		
		var con=document.getElementById("TO_TIPO_DOCTO").value;
		querySelectPost("CatalogoTipoRfcRead","tipoBenef", {async: false });
		querySelectPost("CatalogoObraDGastoRead","destGasto", {async: false });
		$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		querySelectPost("cat_TipoConceptoPagosDiversosRead","TIPO_CONCEPTO", {async: false });
		
		querySelectPost("CAT_TIPO_OPERACIONRead", "TIPO_OPERACION",{async: false });
		$('#TIPO_OPERACION option[value="1"]').attr('selected','selected'); //URVP.14102014 SE DEJA POR DEFAULT LA OPCION DE PAGO NORMAL VAL=1
		querySelectPost("ImporteTotalDiversoRead","cIDContrato", {async: false });
		queryFormPost("fRecepcion2Read", {async: false });
		queryFormPost("fRecepcionRead", {async: false });
		queryFormPost("TipoPolizaRead", {async: false });		

		if ($("#aEjercicioFiscal").val() >= '2013'){
            $("#fRecepcion").val( "<%=today%>" );
            $("#fecha_Pago").val( "<%=today%>" );

			if ( $("#fRecepcion").val().split("/")[2] != $("#aEjercicioFiscal").val() ) {
            	$("#fRecepcion").val( "31/12/" + $("#aEjercicioFiscal").val() );
            	$("#FECHA_CARGA").val( $("#fRecepcion").val() );
            }

			$("#fRecepcion").attr( "readonly","readonly" );
		}

		ponCerosD();
		
		 if (<%=id_oper%> == 2 || <%=id_oper%> == 3){
			
			 queryFormPost("tFacturaDiversoRead", {async: false });
			 retraso3();
			 querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });
		}
		
		BuscaPoliza();
		 if (<%=id_oper%> ==3 ){
		   $("#divAutorizar").hide();
		   
		   if( muestraDivImprimePoliza ){
		   		$("#divImprime").show();
		   		$("#divImprimePoliza").show();
		   		$("#divImprimeAnexo").show();
		   }	 
	  	   $("#Agregar").attr('disabled', true);
		   $("#Agregar1").attr('disabled', true);
		   				   
		 }	
		
		 if (<%=id_oper%> == 1 ){
		 	setTimeout("informacionDocumento()",100);
		 }
	}
	
	function informacionDocumento(){	
		  /** Informacion que se Perdia al Cambiar de Pestaña Documento a Adjunto **/	  
		 	queryFormPost("tFacturaDiversoRead", {async: false });
		  
		 	if($("#numPaso").val() == "1" && $("#caNoContrarrecibo").val() != ""){ 
			 	$(".pasoDos").hide();
		   		$(".pasoTres").hide();
			 	$(".pasoCuatro").hide();
			 }
			 
			 if($("#numPaso").val()!="1"){
			 	querySelectPost("readCtaBancxPago", "ctaBancaria",{async: false });
			 	$("#ctaBancaria").attr('disabled', true);
			 }
			 
		 
		 // Trae Las Ep Detalle Clave

		 	var contador=0;                          
			var szWhere = "";     
			szWhere = " tr.nFolioPAGODIVERSO ='"+  $("#id_caso").val()+"'";
			var elMonto = "";     
			var este=0;          
			var szTabla = "CLAVES_DIVERSO";                                                                                         
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){                       
				var options = '';
				
				for (var i = 0; i < j.length; i++) {
					$("#montoDev").val(j[i].Col3);
					$("#montoDev").formatCurrency();
					var mImporteADev = $("#montoDev").val();
					$('#grdCompromisos').dataTable().fnAddData( [ j[i].Col0, j[i].Col2, mImporteADev ]);
					$("#montoDev").val("");
					contador = contador + 1;
					$("#cllave").val(contador);
					$("#numPaso").val("3"); // Para CONAFOR
					$("#TIPO_CONCEPTO").val(j[i].Col4); 
					$("#TIPO_MOVIMIENTO").val(j[i].Col5); 
					 
				}
				
				// Si trae Informacion 
				if($("#numPaso").val() == "3"){  
					
					$("#nIdClaveEgresos2").attr('disabled', true);
					$("#agrega2").attr('disabled', true);
					concepto();
					$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
					querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
					//setTimeout("concepto()",1000);
					//concepto();
					$("#TIPO_CONCEPTO").attr('disabled', true);
					//$("#TIPO_MOVIMIENTO").attr('disabled', true); URVP.18092014
					$("#ALM").attr('disabled', true);
					DOCUMENTACION();
					//valida que no se vea la liga de captura de justificacion si ya ha sido agregada
						queryFormPost("retencionEliminada", {async:false});
						queryFormPost("validaDoctoJustificacion", {async: false });	
							if( $("#lEliminaRetencion6IVA").val() == 1 && $("#doctoCapturado").val() > 0 || ("#lEliminaRetencion6IVA").val() == 0 ){
					  				$("#justificacion").hide();
								
							}
					$(".pasoTres").show();
				}
				
				if($("#numPaso").val() == "2"){
					
					concepto();
					
		  		 	$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		  		 	querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
					
					setTimeout("concepto()",1000);
				 	$(".pasoTres").hide();
				 	$("#DCD_TIPO_OPE").attr('disabled', true); // Desabilito Pestaña Detalle Oper
					$("#L03").click(); // Muestro Pestaña Devengado
					parent.document.getElementById("pb_save").disabled = true;
								 
				 }
				
				if($("#numPaso").val() == "3"){
					parent.document.getElementById("pb_save").disabled = true; //para CONAFOR
				}
			
				 if($("#numPaso").val() == "3" || $("#numPaso").val() == "6"){
				 	$("#TFONDO").attr('disabled', true);
				 
				 }
				 
				 if($("#numPaso").val() == "6" && $.trim($("#docAplicado").val()) == ""){
				 	$("#numPaso").val("5");
				 }
					
			$("#cIDContrato").val($("#cFolioPAGODIVERSO").val());
			
			if ($("#cIDContrato").val()!=""){
				
				$("#nPorcAmortizacion").attr('readOnly', true);//URVP
				$("#mAmortizacionAcumulado").attr('readOnly', true);//URVP
				$("#btncIDContrato").attr('disabled', true);//URVP
				$("#destGasto").attr('disabled', true);//URVP
				$("#TIPO_OPERACION").attr('disabled', true);//URVP
				$("#cNoFactura").attr('readOnly', true);//URVP
				$("#mImporteSancion").attr('readOnly', true);//URVP
				$("#mImporteDevolucion").attr('readOnly', true);//URVP
				$("#mAmortizacionAnticipo").attr('readOnly', true);//URVP
				$("#mImportePenalizacion").attr('readOnly', true);//URVP
				$("#saldoAnticipo").attr('readOnly', true);//URVP
				$("#Descrip_Concepto").attr('readOnly', true);//URVP
				$("#mImporteBruto").attr('readOnly', true);//URVP
				cssReadOnly();
				
				$("#nPorcAmortizacion").attr('disabled', true);
				$("#mAmortizacionAcumulado").attr('disabled', true);
				
			$("#cIdContrato").val( $("#cIDContrato").val() );
			szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
			if($("#cCentroContable").val()=='10' && $("#cCentroContable").val()!=$("#cCentroContableContrato").val() && $("#cCentroContable").val()==$("#cCentroContableOrig").val()){
				szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContableContrato").val() + "'";
			}
			if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0") {szWhere = szWhere + " AND 1=2";}
			var elMonto = "100";
			var este=0;
			var retencion=0;
			elMonto = $("#mTotal").val();
			
			var szTabla = "CALC_RETENCIONES_FACT_CONT_DIVERSO";
			
			$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
					var options = '';
					var porretencion=0;
					for (var i = 0; i < j.length; i++) {
							//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
							if (j[i].Col0=="9" && $("#cCentroContable").val()=="28"){
								var porcentCedular = 0.00;
								porcentCedular = Number(Number(j[i].Col2) + 0.005).toFixed(6);
								fnClickAddRowB(j[i].Col0, j[i].Col1, porcentCedular);
							}
							else{
								fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
							}
							porretencion=este*elMonto;
							
							if (j[i].Col0 > 10 && j[i].Col0 < 16 ){ //URVP.04092014 SE CAMBIA LA CONDICION == 0 A >10 YA QUE SE DESGLOZARON EN MAS RETENCIONES Y AS POSTERIORES AL ID 10 SON LAS EQUIVALENTES A LA 0 DEL 2/3 DEL IVA
								$("#DCD_IVADES").val( 1);
								if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){///1 espara cuando hay anticipo
									mImporteFlete23=1;
											
								}
								
							}else if (j[i].Col0 == 2 ){
								$("#DCD_MIL2").val( parseFloat (j[i].Col2));
				
								if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){
										CamInst=parseFloat (j[i].Col2);
										CamInst2=parseFloat (j[i].Col2);
										}
									DCD_MIL2res=parseFloat (j[i].Col2);
							}else if (j[i].Col0 == 3 ){
									$("#DCD_MIL5").val( parseFloat (j[i].Col2));
									if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){	
										$("#mObra5").val( parseFloat (j[i].Col2) );
										$("#mObra52").val( parseFloat (j[i].Col2) );
									}
									
									DCD_MIL5res	= parseFloat (j[i].Col2	);	
					
							}else{
								if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){///1 espara cuando hay anticipo
								
									if (j[i].Col0 == 4 ){
										$("#mISRHonorarios").val( parseFloat (j[i].Col2));
										$("#mISRHonorarios2").val( parseFloat (j[i].Col2));						
									}else if (j[i].Col0 == 5 ){
										$("#mImporteFlete4").val( parseFloat (j[i].Col2));
										$("#mImporteFlete42").val( parseFloat (j[i].Col2));						
									}else if (j[i].Col0 == 6 ){						
											$("#mISRArrenda").val( parseFloat (j[i].Col2));		
											$("#mISRArrenda2").val( parseFloat (j[i].Col2));						
									}else if (j[i].Col0 == 7 ){						
										$("#mTesofe").val( parseFloat (j[i].Col2) );	
										$("#mTesofe2").val( parseFloat (j[i].Col2) );						
									}else if (j[i].Col0 == 9 ){	
										if ($("#cCentroContable").val()=="28"){				
											$("#mRetImpuestoCedular").val(porcentCedular);
											$("#mRetImpuestoCedular2").val(porcentCedular);
										}else{
											$("#mRetImpuestoCedular").val( parseFloat (j[i].Col2));
											$("#mRetImpuestoCedular2").val( parseFloat (j[i].Col2));
										}
									}	
								}
								
								if (j[i].Col0=="9" && $("#cCentroContable").val()=="28"){ //dejar en igual a 28 para que en nayarit sea 1.5%
									$("#porcCedular").val("1");
								}else{
									retencion= parseFloat(retencion) + parseFloat(j[i].Col2);
								}
							}
							$("#sumareten").val(retencion);
							DCD_RETENCIONres=retencion;
					}
			});	
		}
				
					
			});  	
		 
			 compDocumentacion();
			 
			 if($("#numPaso").val() == "5"){
				 
				 $("#L04").click();
				 
			 }
			 
			 
}
	function clearSelect(idSel) {
		for ( var i = 0; i < idSel.length; i++)
			$('#' + idSel[i]).find('option').remove().end().append(
					'<option value="-1"></option>');
	}
	function concepto(){
		$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
		querySelectPost("CatalogoObraTMovimendoRead", "TIPO_MOVIMIENTO",{async: false });
		concepto2();
	}
	function concepto2(){
 		querySelectPost("CatalogoObraTMovimendo2Read",{async: false });
 		
 		if( esSAIFonden && <%=id_oper%> ==3  ){
			$("#verProyectoFondenDiv").css('display', 'show');
			$("#selproyectoFondenDiv").css('display', 'none');
			
		}
		
		if( esSAIFonden  ){
			$("#verFondenSPAN").show();
			querySelectPost(
				{ 	     queryName: 'catFondenRead', 
					targetObjectId: 'proyectoFonden', 
					         async: false,
					      callback: function(){
					         	$("#dlgSeleccionaFonden").dialog("open");
					         }
				}
				
			);
			$("#verProyectoFondenDiv").css('display', 'none');
			$("#selproyectoFondenDiv").css('display', 'show');
		}
				
		if ($("#TIPO_CONCEPTO").val() == "AL"){
			querySelectPost("CatalogoAlmacenRead","ALM", {async: false });
			$("#altaAlmacen").val("-1");
			$("#altaAlmacen").hide(); //show-->hide
			$("#cAnioFactEP").val("-1");
			$("#nFacturaEP").val("-1");
			$("#cAnioFactEP").hide(); //show-->hide
			$("#nFacturaEP").hide(); //show-->hide
		}
		else{
			$("#altaAlmacen").val("0");
			$("#altaAlmacen").hide();
			$("#cAnioFactEP").val("0");
			$("#nFacturaEP").val("0");
			$("#cAnioFactEP").hide(); 
			$("#nFacturaEP").hide();
			querySelectPost("CatalogoAlmacenVacioRead","ALM", {async: false });
		}
		
		
		
	}
	
	/*ERH2015 Para mantener los valores originales tanto del monto de la recepcion como del acumulado de amortizacion.*/
	var importeRecepcionOriginal = 0.0;
	var acumuladoAmortizacionOriginal = 0.0;
	
	var esIvaArrenda=0;
	var mImporteFlete23=0;
	var mImporteFlete232=0;
	var ayuda1=0;
	var ayuda2=0;
	var saldoo=0;
	var acumula=0;
	var comprometido=0;
	var DCD_IVADES=0;
	var DCD_MIL2=0;
	var DCD_MIL5=0;
	var DCD_RETENCION=0;
	var DCD_IVADESres=0;
	var DCD_MIL2res=0;
	var DCD_MIL5res=0;
	var DCD_RETENCIONres=0;
	var mAmortizacionAnticipo=0;
	var mAmortizacionAnticipoRes=0;
	var mAmortizacionAcumulado=0;
	var mAmortizacionAcumuladoRes=0;
	var mImporteSancionAcumulado=0;
	var mImporteDevolucionAcumulado=0;
	var contador=0;
	var mIMDT=0;
	var mCNIC=0;
	var CamInst=0;	
	var mObra5=0;
	var mISRHonorarios=0;
	var mISRArrenda=0;
	var mPenalizacion=0;
	var mRetImpuestoCedular=0;
	var mImporteNetoEP=0;
	var mImporteNetoEPRes=0;
	var mImporteIVAEP=0;
	
	function calculaIVAConsulta(){
		suma = Number($("input[id='mImporteBruto']").val());
		suma -= Number($("input[id='mImporteSancion']").val());
		suma += Number($("input[id='mImporteDevolucion']").val());		
		$("#subTotal_1").val(Math.round(Number(suma) *100 )/100);
		suma -= $("#mAmortizacionAnticipo").val();	
		$("#subTotal_2").val(Math.round(Number(suma) *100 )/100);
		// JGDS Calcula el iva solo si tuvo Sancion, dev o anticipos, sino deja el de la recepcion
		if ($("#subTotal_1").val() != $("#subTotal_2").val())
			$("input[id='mImporteIVA']").val( $("#subTotal_2").val() * Number($("input[id='nPorcIVAAplicable']").val()/100)    );
		suma += Number($("input[id='mImporteIVA']").val());		
		$("#mImpEjercer").val(Math.round(Number(suma) *100 )/100);		
		suma -= Number($("input[id='mImporteRetencion']").val());
		suma -= Number($("input[id='mImportePenalizacion']").val());
		$("#mImporteNeto").val(Math.round(Number(suma) *100 )/100);					
	}
	
	function AnticipoTotal(){
			if ( parseFloat ($("#saldoAnticipo").val()) == parseFloat($("#mAmortizacionAcumulado").val())){
				$("#nPorcAmortizacion").val(0);
				calculaIVA();
			}
	}
	function Amortizacion(){
		 if ( Number ($("#nPorcAmortizacion").val()) < Number( $("#nPorcAmortizacion2").val() )){
			 Swal.fire("Revise!","El porcentaje minimo es: " +$("#nPorcAmortizacion2").val(), "warning" );
			$("#nPorcAmortizacion").val($("#nPorcAmortizacion2").val());
	 	}else{
			calculaIVA();
		}
	}	
	var cantidadsiniva=0;
	var cantidadrespaldo=0;
	var cantidadsinIvaEP=0;
	var cantidadsinIvaEP23=0;
	
	function calculaRetencionesEP ( cantidad ){
		
		var porcentajeIVA = 0.00;
		porcentajeIVA = ( Number($("#mImporteIVA").val()) * 100 / (Number($("#mImpEjercer").val())) ) / 100;
		cantidadsinIvaEP = cantidad * (1-porcentajeIVA);
		cantidadsinIvaEP = cantidadsinIvaEP.toFixed(2);
		
		mCNIC=0;
		mIMDT=0;
		mObra5=0;
		$("#mObra5").val($("#mObra52").val());
		$("#mISRHonorarios").val($("#mISRHonorarios2").val( ) );
		$("#mImporteFlete4").val($("#mImporteFlete42").val( ) );
		$("#mISRArrenda").val($("#mISRArrenda2").val());
		$("#mTesofe").val($("#mTesofe2").val());
		$("#mRetImpuestoCedular").val($("#mRetImpuestoCedular2").val( ) );
		cantidadrespaldo = cantidad;	
   		cantidad = cantidadsinIvaEP;
   		var d_miva = Number(cantidadrespaldo) - Number( cantidadsinIvaEP );
   		d_miva = d_miva.toFixed(2);
		$("#mIVA").val( d_miva );
		
		if ( mImporteFlete23 == 1){
			mImporteFlete232= ($("#mIVA").val()*2)/3;
			
			$("#mImporteFlete23").val( Math.round( mImporteFlete232 *100 )/100 );

		}	
		if ( $("#CamInst").val() == 0 ){
			mCNIC = parseFloat ( cantidad )* parseFloat( CamInst);
			mCNIC = Number( mCNIC.toFixed(2) ) ;
			
			if( ($("#nPorcAmortizacion").val()/100) == 1 )
				mCNIC = mCNIC / ( 1 ); 
			else
				mCNIC = mCNIC / ( 1 - ($("#nPorcAmortizacion").val()/100));
				
			mCNIC = mCNIC.toFixed(2);
			$("#mCNIC").val( mCNIC );
		}else if ( $("#CamInst").val() == 1 ){
			mIMDT = parseFloat ( cantidad )* parseFloat( CamInst);
			mIMDT = Number( mIMDT.toFixed(2) );
			if( ($("#nPorcAmortizacion").val()/100) == 1 )
				mIMDT = mIMDT / ( 1 );
			else
				mIMDT = mIMDT / ( 1 - ($("#nPorcAmortizacion").val()/100));
			mIMDT = mIMDT.toFixed(2);
			$("#mIMDT").val( mIMDT );
		}else{
			$("#mIMDT").val(0);
			$("#mCNIC").val(0);
		}
		
		mObra5 = parseFloat ( cantidad )* parseFloat( $("#mObra5").val());
		mObra5 = Number( mObra5.toFixed(2) ) ;
		
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mObra5 = mObra5 / ( 1 );
		else
			mObra5 = mObra5 / ( 1 - ($("#nPorcAmortizacion").val()/100));
		
		mObra5 = mObra5.toFixed(2);
		$("#mObra5").val( mObra5 );
		
		mISRHonorarios = parseFloat ( cantidad )* parseFloat( $("#mISRHonorarios").val());
		mISRHonorarios = Number( mISRHonorarios.toFixed(2) ) ;
		
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mISRHonorarios = mISRHonorarios / ( 1 ) ;
		else
			mISRHonorarios = mISRHonorarios / ( 1 - ($("#nPorcAmortizacion").val()/100));
			
		mISRHonorarios = mISRHonorarios.toFixed(2);
		$("#mISRHonorarios").val( mISRHonorarios );
		
		mImporteFlete4 = parseFloat ( cantidad )* parseFloat( $("#mImporteFlete4").val());
		mImporteFlete4 = Number( mImporteFlete4.toFixed(2) ) ;
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mImporteFlete4 = mImporteFlete4 / ( 1 );
		else
			mImporteFlete4 = mImporteFlete4 / ( 1 - ($("#nPorcAmortizacion").val()/100));
			
		mImporteFlete4 = mImporteFlete4.toFixed(2);
		$("#mImporteFlete4").val( mImporteFlete4 );
		
		mISRArrenda = parseFloat ( cantidad )* parseFloat( $("#mISRArrenda").val());
		mISRArrenda = Number( mISRArrenda.toFixed(2) ) ;
		
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mISRArrenda = mISRArrenda / ( 1 );
		else
			mISRArrenda = mISRArrenda / ( 1 - ($("#nPorcAmortizacion").val()/100));
			
		mISRArrenda = mISRArrenda.toFixed(2);
		$("#mISRArrenda").val( mISRArrenda );
		
		var mAmortizaEP = ( Number( $("#mAmortizacionAnticipoMasIva").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mAmortizaEP = mAmortizaEP.toFixed(2);
		$("#mAmortizacionAnticipoMasIvaEP").val( mAmortizaEP );
		
		var mPenaliza = ( Number( $("#mImportePenalizacion").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mPenaliza = mPenaliza.toFixed(2);
		$("#mPenalizacion").val( mPenaliza );
		
		mRetImpuestoCedular = parseFloat ( cantidad )* parseFloat( $("#mRetImpuestoCedular").val());
		mRetImpuestoCedular = Number( mRetImpuestoCedular.toFixed(2) ) ;
		
		if( ($("#nPorcAmortizacion").val()/100) == 1 )
			mRetImpuestoCedular = mRetImpuestoCedular/ ( 1 );
		else
			mRetImpuestoCedular = mRetImpuestoCedular/ ( 1 - ($("#nPorcAmortizacion").val()/100));
			
		mRetImpuestoCedular = mRetImpuestoCedular.toFixed(2);
		$("#mRetImpuestoCedular").val( mRetImpuestoCedular );

		mImporteNetoEP = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
		Number($("#mImporteFlete23").val()) + Number($("#mTesofe").val());
		
		mImporteNetoEPRes = parseFloat(cantidadrespaldo) -  parseFloat(mImporteNetoEP);
		mImporteNetoEPRes = mImporteNetoEPRes.toFixed(2);
		$("#mImporteNetoEP").val( mImporteNetoEPRes );
	
	}

function ponCerosD(){
	
	$("#DCD_ISR_D").val( "0" );
	$("#DCD_IVA_D").val( "0" );
	$("#DCD_MIL5_D").val( "0" );
	$("#DCD_MIL2_D").val( "0" );
	$("#DCD_CONTRIBUCION_D").val( "0" );
	$("#DCD_OTRAS_RET_D").val( "0" );
	$("#DCD_PENALIZACION_D").val( "0" );
	$("#DCD_SANCION_D").val( "0" );
	$("#DCD_DEVOL_D").val( "0" );
	$("#DCD_AMORT_D").val( "0" );
	$("#DCD_RETENCION_D").val( "0" );
	$("#mIVA_D").val( "0" );
}
	
function actualSaldosDet( elTipo ){
    var laRetencion = 0;
	
    laRetencion = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
		Number($("#mImporteFlete23").val()) + Number($("#mTesofe").val());
    laRetencion = laRetencion.toFixed(2);
 
	$("#DCD_ISR_D").val(parseFloat($("#DCD_ISR_D").val()) + elTipo * parseFloat($("#mISRHonorarios").val()) + elTipo * Number( $("#mISRArrenda").val() ));
	$("#DCD_IVA_D").val(parseFloat($("#DCD_IVA_D").val()) + elTipo * parseFloat($("#mImporteFlete23").val()));
	$("#DCD_MIL5_D").val(parseFloat($("#DCD_MIL5_D").val()) + elTipo * parseFloat($("#mObra5").val()));
	$("#DCD_MIL2_D").val(parseFloat($("#DCD_MIL2_D").val()) + elTipo * ( Number( $("#mCNIC").val() ) + Number( $("#mIMDT").val() ) ) );
	$("#DCD_CONTRIBUCION_D").val(parseFloat($("#DCD_CONTRIBUCION_D").val()) + elTipo * parseFloat($("#mImporteFlete4").val()));
	$("#DCD_OTRAS_RET_D").val(parseFloat($("#DCD_OTRAS_RET_D").val()) + elTipo * parseFloat($("#mRetImpuestoCedular").val()));
	$("#DCD_PENALIZACION_D").val(parseFloat($("#DCD_PENALIZACION_D").val()) + elTipo * parseFloat($("#mTesofe").val()));
	$("#DCD_SANCION_D").val(parseFloat($("#DCD_SANCION_D").val()) + elTipo * parseFloat($("#mSancion").val()));
	$("#DCD_DEVOL_D").val(parseFloat($("#DCD_DEVOL_D").val()) + elTipo * parseFloat($("#mDevolucion").val()));
	$("#DCD_AMORT_D").val(parseFloat($("#DCD_AMORT_D").val()) + elTipo * parseFloat($("#mAmortizacionAnticipoMasIvaEP").val()));
	$("#DCD_RETENCION_D").val(parseFloat($("#DCD_RETENCION_D").val()) + elTipo * parseFloat(laRetencion));
	$("#mIVA_D").val( Number( $("#mIVA_D").val() ) + elTipo * Number( $("#mIVA").val() ) ) ;
}

function AjustaRetencionesD(){
		
    var laRetencion = 0;
	
	$("#mDevolucion").val( Number( $("#mDevolucion").val() ) + ( Number( $("#DCD_DEVOL").val() ) - Number( $("#DCD_DEVOL_D").val() ) ) );
	
	$("#mTesofe").val( Number( $("#mTesofe").val() ) + ( Number( $("#mImportePenalizacion").val() ) - Number($("#DCD_PENALIZACION_D").val()) ));
	
	if ( $("#CamInst").val() == 0 ){
		$("#mCNIC").val( Number( $("#mCNIC").val() ) + ( Number( $("#DCD_MIL2").val() ) - Number($("#DCD_MIL2_D").val()) ) );
	}else{
		$("#mIMDT").val( Number( $("#mIMDT").val() ) + ( Number( $("#DCD_MIL2").val() ) - Number($("#DCD_MIL2_D").val()) ) );
	}
	
	$("#mImporteFlete23").val( Number( $("#mImporteFlete23").val() ) + ( Number( $("#DCD_IVADES").val() ) - Number($("#DCD_IVA_D").val()) ) );
	$("#mObra5").val( Number( $("#mObra5").val() ) + ( Number( $("#DCD_MIL5").val() ) - Number($("#DCD_MIL5_D").val()) ) );
	$("#mImporteFlete4").val( Number( $("#mImporteFlete4").val() ) + ( Number( $("#DCD_CONTRIBUCION").val() ) - Number($("#DCD_CONTRIBUCION_D").val()) ) );
	
	if ( Number( $("#mISRHonorarios").val() ) > 0)
		$("#mISRHonorarios").val( Number( $("#mISRHonorarios").val() ) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val()) ) );	
	else
		$("#mISRArrenda").val( Number( $("#mISRArrenda").val() ) + ( Number( $("#DCD_ISR").val() ) - Number($("#DCD_ISR_D").val()) ) );
	
	$("#mRetImpuestoCedular").val( Number( $("#mRetImpuestoCedular").val() ) + ( Number( $("#DCD_OTRAS_RET").val() ) - Number($("#DCD_OTRAS_RET_D").val()) ) );
	$("#mSancion").val( Number( $("#mSancion").val() ) + ( Number( $("#DCD_SANCION").val() ) - Number($("#DCD_SANCION_D").val()) ) );

	$("#mAmortizacionAnticipoMasIvaEP").val( Number( $("#mAmortizacionAnticipoMasIvaEP").val() ) + ( Number( $("#DCD_AMORT").val() ) - Number($("#DCD_AMORT_D").val()) ) );
	//if (1==2){
		var mAmortizaEP = ( Number( $("#mAmortizacionAnticipoMasIva").val() ) / Number( $("#mImpEjercer").val() ) ) * cantidadrespaldo;
		mAmortizaEP = mAmortizaEP.toFixed(2);
		$("#mAmortizacionAnticipoMasIvaEP").val( mAmortizaEP );

    laRetencion = Number($("#m23IVA").val()) + Number($("#mCNIC").val())+Number($("#mIMDT").val())  + Number($("#mObra5").val()) + Number ($("#mISRHonorarios").val())+
		Number($("#mImporteFlete4").val())+ Number( $("#mISRArrenda").val() )+Number( $("#mRetImpuestoCedular").val())+
		Number($("#mImporteFlete23").val()) + Number($("#mTesofe").val());
    laRetencion = laRetencion.toFixed(2);
 	
    $("#mIVA").val( Number( $("#mIVA").val() ) + ( Number( $("#mImporteIVA").val() ) - Number( $("#mIVA_D").val() ) ) );
	$("#mImporteNetoEP").val( Number( $("#DCD_IMP_BRUTO").val() ) - Number( laRetencion ));
	
}

	function CalculaRetencionesG(){
		var szWhere = "";
		szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		if($("#cCentroContable").val()=='10' && $("#cCentroContable").val()!=$("#cCentroContableContrato").val() && $("#cCentroContable").val()==$("#cCentroContableOrig").val()){
			szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContableContrato").val() + "'";
		}
		if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0") {szWhere = szWhere + " AND 1=2";} //URVP.25112014 Si lleva anticipo y no se le ha hecho el anticipo se quitan las retenciones ya que el pago a realizar sera el anticipo el cual no lleva retenciones
		var este=0;
		var elMonto = "100";
		var retencion=0;
		var szTabla = "CALC_RETENCIONES_FACT_CONT_DIVERSO";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion=0;
			$("#DCD_ISR").val( 0 );
			for (var i = 0; i < j.length; i++) {
				if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){
					//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
					if (j[i].Col0=="9" && $("#cCentroContable").val()=="28"){
						var porcentCedular = 0.00;
						porcentCedular = Number(Number(j[i].Col2) + 0.005).toFixed(6);
						retencion = parseFloat(porcentCedular)*Number($("#subTotal_1").val());	
					}else{
						retencion = parseFloat(j[i].Col2)*Number($("#subTotal_1").val());
					}
					
					if (j[i].Col0 == 4 ){
						retencion = Number($("#DCD_ISR").val()) + retencion ;
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_ISR").val( retencion ); 						
					}else if (j[i].Col0 == 5 ){
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_CONTRIBUCION").val( retencion ); 
					}else if (j[i].Col0 == 6 ){						
						retencion = Number($("#DCD_ISR").val()) + retencion ;
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_ISR").val( retencion ); 						
					}else if (j[i].Col0 == 9 ){
						retencion = Number(retencion).toFixed( 2 );
						$("#DCD_OTRAS_RET").val( retencion ); 						
					}	
				}
			}
		});	
	}
	

	function FiltroRetencionesD(){
		//se limpia grid debido a que si se selecciona por error un contrato con retencion y se selecciona uno sin retencion, el grid deja lass retenciones del que si tiene
		$('#tretencion').dataTable().fnClearTable(); 
		
		queryFormPost( {
			queryName:"esContratoValesRead",
			async:false,
			callback:function(){
					if( $("#vales_combustible").val()  === "" )
						$("#vales_combustible").val("false");
					
					$('#uploadFacturasFrm').attr('src', "UploadFacturas.jsp?tipo_pago=PAGODIVERSO&contratoVales="+$("#vales_combustible").val() );
			}  
		});
				
		
		cargaCtaBancariaRFC();
		obtieneCentroContableContrato_Orig($("#cIDContrato").val());
		querySelectPost("readRecepcionMateriales", "noRecepcion", {async:false});
		querySelectPost("readPenaltyDeduction", "noPenalty", {async:false});
		
		// reinicia valores
		$("#acumulado").val("0");
		comprometido=0;
		$("#mImporteFlete23").val("0");
		$("#mIMDT").val("0");
		$("#mCNIC").val("0");
		$("#mObra5").val("0");
		$("#mISRHonorarios").val("0");
		$("#mImporteFlete4").val("0");
		$("#mISRArrenda").val("0");
		$("#mAmortizacionAnticipoMasIvaEP").val("0");
		$("#mTesofe").val("0");
		$("#mRetImpuestoCedular").val("0");
		// termina reinicia valores
		
		var szWhere = "";
		 
		$("#cIdContrato").val( $("#cIDContrato").val() );
		szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContable").val() + "'";
		if($("#cCentroContable").val()=='10' && $("#cCentroContable").val()!=$("#cCentroContableContrato").val() && $("#cCentroContable").val()==$("#cCentroContableOrig").val()){
			szWhere = " cidcontrato ='"+ $("#cIDContrato").val()+"' AND cIdEntidadContable = '" +  $("#cCentroContableContrato").val() + "'";
		}
		if ($("#llevaAnticipo").val()=="1" && $("#tieneAnticipo").val()=="0") {szWhere = szWhere + " AND 1=2";}
		var elMonto = "100";
		var este=0;
		var retencion=0;
		elMonto = $("#mTotal").val();
		var szTabla = "CALC_RETENCIONES_FACT_CONT_DIVERSO";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			var options = '';
			var porretencion=0;
			for (var i = 0; i < j.length; i++) {
					//URVP.27112014 Se valida si es impuesto cedular y nayarit, se cambia el porcentaje de la retencion a 1.5%
					if (j[i].Col0=="9" && $("#cCentroContable").val()=="28"){
						var porcentCedular = 0.00;
						porcentCedular = Number(Number(j[i].Col2) + 0.005).toFixed(6);
						fnClickAddRowB(j[i].Col0, j[i].Col1, porcentCedular);
					}
					else{
						fnClickAddRowB(j[i].Col0, j[i].Col1, j[i].Col2);
					}
					porretencion=este*elMonto;
					
					if (j[i].Col0 > 10 && j[i].Col0 < 16 ){ //URVP.04092014 SE CAMBIA LA CONDICION == 0 A >10 YA QUE SE DESGLOZARON EN MAS RETENCIONES Y AS POSTERIORES AL ID 10 SON LAS EQUIVALENTES A LA 0 DEL 2/3 DEL IVA
						$("#DCD_IVADES").val(1);
						if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){///1 espara cuando hay anticipo
							mImporteFlete23=1;//alo
							//$("#m23IVA").val(1);
						if (j[i].Col0==11)
							esIvaArrenda=1;
						}
					}else if (j[i].Col0 == 2 ){
						$("#DCD_MIL2").val( parseFloat (j[i].Col2));
		
						if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){
								CamInst=parseFloat (j[i].Col2);
								CamInst2=parseFloat (j[i].Col2);
								}
							DCD_MIL2res=parseFloat (j[i].Col2);
					}else if (j[i].Col0 == 3 ){
							$("#DCD_MIL5").val( parseFloat (j[i].Col2));
							if ($("#lHaySaldoAnticipo").val() == 0 || $("#mAmortizado").val() != '0.0000' ){	
								$("#mObra5").val( parseFloat (j[i].Col2) );
								$("#mObra52").val( parseFloat (j[i].Col2) );
							}
							
							DCD_MIL5res	= parseFloat (j[i].Col2	);	
			
					}else{
						if ($("#lHaySaldoAnticipo").val() == 0  || $("#mAmortizado").val() != '0.0000'){///1 espara cuando hay anticipo
						
							if (j[i].Col0 == 4 ){
								$("#mISRHonorarios").val( parseFloat (j[i].Col2));
								$("#mISRHonorarios2").val( parseFloat (j[i].Col2));						
							}else if (j[i].Col0 == 5 ){
								$("#mImporteFlete4").val( parseFloat (j[i].Col2));
								$("#mImporteFlete42").val( parseFloat (j[i].Col2));						
							}else if (j[i].Col0 == 6 ){						
								$("#mISRArrenda").val( parseFloat (j[i].Col2));		
								$("#mISRArrenda2").val( parseFloat (j[i].Col2));						
							}else if (j[i].Col0 == 7 ){						
								$("#mTesofe").val( parseFloat (j[i].Col2) );	
								$("#mTesofe2").val( parseFloat (j[i].Col2) );						
							}else if (j[i].Col0 == 9 ){
								if ($("#cCentroContable").val()=="28"){				
									$("#mRetImpuestoCedular").val(porcentCedular);
									$("#mRetImpuestoCedular2").val(porcentCedular);
								}else{
									$("#mRetImpuestoCedular").val( parseFloat (j[i].Col2));
									$("#mRetImpuestoCedular2").val( parseFloat (j[i].Col2));
								}								
							}	
						}
						
						if (j[i].Col0=="9" && $("#cCentroContable").val()=="28"){
							$("#porcCedular").val("1");
							
						}else if (j[i].Col0=="9" && $("#cCentroContable").val()=="21"){
							$("#porcCedular").val("1");
							
						}else{
							retencion= parseFloat(retencion) + parseFloat(j[i].Col2);
						}
					}
					$("#sumareten").val(retencion);
					DCD_RETENCIONres=retencion;
			}
		});	
		if (<%=id_oper%> ==1 ){
			Anticipo();
			
			if ($("#lHaySaldoAnticipo").val() == 1 && $("#mAmortizado").val() != '0.0000'){
				$("#mAmortizacionAcumulado").val(  $("#mAmortizado").val() );
			}else {
				if($("#cCentroContable").val()=='10' && $("#cCentroContable").val()!=$("#cCentroContableContrato").val() && $("#cCentroContable").val()==$("#cCentroContableOrig").val()){
					queryFormPost("tFACTDiversosSEGUNDOPAGOReadCentrales", {async: false });
				}else{
					queryFormPost("tFACTDiversosSEGUNDOPAGORead", {async: false });
				}
			}
			//ERH2015 Se guarda el acumulado de amortizacion original.
			acumuladoAmortizacionOriginal = Number( quitaFmt( $("#mAmortizacionAcumulado").val() ) ) * 100 / 100;
			
			$("#nPorcAmortizacion2").val($("#nPorcAmortizacion").val());
			mAmortizacionAcumuladoRes= $("#mAmortizacionAcumulado").val();
			mImporteSancionAcumulado=$("#mImporteSancionAcumulado").val();
			mImporteDevolucionAcumulado=$("#mImporteDevolucionAcumulado").val();
			comprometido=$("#acumulado").val();
			setTimeout("AnticipoTotal()",1000);
		}
	}
	
	function validaAmortizacion(){
		var impAnticipo = 0.00;
		var impAmortizado = 0.00;
		var impPago = 0.00;		
		impAnticipo = Number($("#saldoAnticipo").val());
		impAmortizado = Number($("#mAmortizacionAcumulado").val());
		impPago = Number($("#mImpEjercer").val());
		
		if (impPago <= (impAnticipo-impAmortizado)){
			Swal.fire("El pago no se puede procesar debido a que es menor o igual que la amortizacion del anticipo", "Favor de capturar el resto de las facturas.", "warning");
			return;
		}else{
			$("#mAmortizacionAnticipo").val((impAnticipo-impAmortizado).toFixed(2));
			$("#mImpEjercer").val(Number($("#mImpEjercer").val())+Number($("#mAmortizacionAnticipo").val()));
			$(".subtotall").change();
		}
	}
	
	
	function Anticipo(){
		$("#cIDContratoObra").val( $("#cIDContrato").val() );
		
		if($("#cCentroContable").val()=='10' && $("#cCentroContable").val()!=$("#cCentroContableContrato").val() && $("#cCentroContable").val()==$("#cCentroContableOrig").val()){
			queryFormPost("tFACTOBRASaldocompromisoReadCentrales", {async: false });
		}else{
			queryFormPost("tFACTOBRASaldocompromisoRead", {async: false });
		}
		
		if ($("#lHaySaldoAnticipo").val() == "1" && $("#mAmortizado").val() == '0.0000'){
			$("#mImporteBruto").attr("readonly",true);
			$("#nPorcAmortizacion").val(0);
			setTimeout("calculaIVA()",400);
		}else{
			$("#mImporteBruto").val(0);
			calculaIVA();			
		}
	}
	function obtieneCentroContableContrato_Orig(cidContrato){
			if(cidContrato.substring(0,3)=='PLU'){
				$("#cUnidadContrato").val(cidContrato.substring(7,10));
			}else{
				$("#cUnidadContrato").val(cidContrato.substring(3,6));
			}
		queryFormPost("ObtieneCentroContableContrato_Original", {async: false,
			callback:function(){
				
			}
		 });
	
	}
			
	
	function elRetardo(){
		$("#nombre").val($("#cobjetocontrato").val());	
		$(".paso01").attr('disabled', false);
		
		if(  $("#tipoAmortizacion").val() != "1"  ){
			elParametro = "'"+$("#caNoContrarrecibo").val()+"', '"+$("#TIPO_OPERACION").val()+"', '0', '0', '"+$("#DCD_IMP_BRUTO").val()+"', '"+$("#DCD_SANCION").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_AMORT").val()+"', '"+$("#DCD_IVA").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','" + $("#cCentroContable").val() + "'," + $("#aEjercicioFiscal").val() + ",'"+$("#nombre").val()+"', '2','0','"+$("#cIdRFC_RelacionGasto2").val()+"',"+$("#mOtrosImpuestos").val();
		}else{
			elParametro = "'"+$("#caNoContrarrecibo").val()+"', '"+$("#TIPO_OPERACION").val()+"', '0', '0', '" + $("#mImporteBruto").val() + "', '"+$("#DCD_SANCION").val()+"', '"+$("#DCD_DEVOL").val()+"', '"+$("#DCD_AMORT").val()+"', '"+$("#DCD_IVA").val()+"','"+$("#DCD_RETENCION").val()+"', '"+$("#DCD_PENALIZACION").val()+"', '"+$("#DCD_NETO").val()+"', '"+$("#DCD_FECHA_FACTURA").val()+"','" + $("#cCentroContable").val() + "'," + $("#aEjercicioFiscal").val() + ",'"+$("#nombre").val()+"', '2','0','"+$("#cIdRFC_RelacionGasto2").val()+"',"+$("#mOtrosImpuestos").val();
		}
		
		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TCONTRARECIBODIVERSOSCREATE", Param: elParametro, MaxReg: "", ajax: 'false'}, function(j){});
		$("#cIDContrato").attr('disabled', true);
		$(".paso01").attr('disabled', true);
		$("#Agregar1").attr('disabled', true);
		
	parent.document.getElementById("pb_send").style.visibility='visible';
	parent.document.getElementById("pb_send").disabled = false;
	}	
	function validar(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) return true;
			patron =/[A-Za-z.\d\s\\. ]/;
			te = String.fromCharCode(tecla);
			return patron.test(te);
	}
	function validar2(e) {
		tecla = (document.all) ? e.keyCode : e.which;
		if (tecla==8) 
			return true;
		patron =/[.\d]/;
		te = String.fromCharCode(tecla);
		return patron.test(te);
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
	function Sinfrmt( fld ){
		var valcol = fld.value ;
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(",", "");
		$("#" + fld.id).val( valcol );
	}
	
	function cambiafrmt( fld ){
	    $("#" + fld.id).formatCurrency();
	}

	function validaSancion(){
		var montoPago = 0.00;
		var montoSancion = 0.00;
		montoSancion = Number($("#mImporteSancion").val());
		montoPago = Number($("#mImporteBruto").val());
		if (montoPago==0){
			Swal.fire("El importe del pago no puede ser 0", "Favor de verificar la recepcion de material o que los importes sean correctos en las Facturas.", "warning");
			return false;
		}
		if (montoSancion>montoPago){
			Swal.fire({ icon: 'warning',
						text: "El monto de la Sanción no puede ser Mayor al Importe Bruto." });					
			$("#mImporteSancion").val(0);
			document.getElementById("mImporteSancion").focus();
			$(".subtotall").change();
			return false;
		}
		return true;
	}

	/**
	 * Verifica si ya existe un documento de Opinión de Cumplimiento (SAT Formato 32D)
	 * capturado en el sistema para el trámite actual.
	 * Esta función realiza una petición AJAX síncrona (bloqueante).
	 * 
	 * @return true si existe el documento, false en caso contrario
	 */
	function verificarOpinionCumplimiento() {
		var hayDocumento = false;
		
		$.ajax({
			url: '../validateOpinion32D',
			type: 'GET',
			async: false,  // Petición síncrona (bloqueante)
			dataType: 'json',
			success: function(response) {
				console.log('Verificación de opinión:', response);
				if (response.success) {
					hayDocumento = response.hasDocument;
				}
			},
			error: function(xhr, status, error) {
				console.error('Error verificando opinión:', status, error);
				// En caso de error, asumimos que no hay documento
				hayDocumento = false;
			}
		});
		
		return hayDocumento;
	}

	function cmdGuardar(){
		if ($("#numPaso").val() == '1'){
			if (!validaSancion()) return;
			if ($("#noRecepcion").val()=="-1"){
				Swal.fire("Seleccione","Favor de seleccionar una recepcion.", "warning");
				return;
			}
			
			// Verificar que exista la Opinión de Cumplimiento (SAT Formato 32D)
			if (!verificarOpinionCumplimiento()) {
				Swal.fire({
					icon: 'warning',
					title: 'Documento Requerido',
					text: 'Debe adjuntar el documento de Opinión de Cumplimiento (SAT Formato 32D) antes de guardar el pago. Por favor, cargue el documento y valídelo.',
					confirmButtonText: 'Entendido'
				});
				return;
			}
			
			queryFormPost("penaltyExist", {async: false});
			if ($("#tienePenas").val()!="0"){
				if ($("#noPenalty").val()=="-1"){
					if (!confirm("¿Este contrato tiene penas y/o deducciones capturadas en el modulo de materiales y no se agregaron al pago, deseas continuar?")){						
						Swal.fire({ icon: 'warning',
									text: "Favor de agregar las Penas/Deducciones." });		
			   			return;
			   		}
				}
			}

			var penalizacion = (  parseFloat( $("#mImportePenalizacion").val() )  ) * 100 / 100;
			var penasRetencion = (parseFloat( $("#mImporteRetencion").val()) + parseFloat( $("#mImportePenalizacion").val()) + (parseFloat ($("#mAjustePenas").val()) *-1)) * 100 / 100;
			var facRet =   parseFloat( $("#mImporteFacturas").val() ) + penasRetencion;
			facRet = Number(facRet).toFixed(2);
			
			/*EHR2015 Validacion de montos.*/
			var importeEjercer = parseFloat(  $("#mImpEjercer").val() ) * 100 / 100;			
			var techoImporteEjercer =  parseFloat( importeEjercer + 0.02 ) * 100 / 100;
			var pisoImporteEjercer  =  parseFloat( importeEjercer - 0.02 ) * 100 / 100;
			var amortizacionAnticipo = parseFloat( $("#mAmortizacionAnticipo").val() ) * 100 / 100;
			var ivaAmortizacion = (amortizacionAnticipo * 0.16) * 100 / 100; ;
			var amortizacionConIVA = (amortizacionAnticipo + ivaAmortizacion).toFixed(2);
			$("#mAmortizacionAnticipoMasIva").val(amortizacionConIVA); 
			$("#mImporteAnticipoIVA").val(amortizacionConIVA);
			
			var techoEjercerMasAmortizaciones = 0.0;
			var pisoEjercerMasAmortizaciones = 0.0;
			
			if (amortizacionConIVA > 0) {
				pisoImporteEjercer =  parseFloat( importeEjercer + Number(amortizacionAnticipo) - 0.02 ) * 100 / 100;
				techoImporteEjercer =  parseFloat( importeEjercer + Number(amortizacionAnticipo) ) * 100 / 100;  
			}
			
			if(  $("#tipoAmortizacion").val() != "1"  ){
				pisoEjercerMasAmortizaciones  =  parseFloat( importeEjercer + Number(amortizacionConIVA) - 0.02 ) * 100 / 100;
				techoEjercerMasAmortizaciones =  parseFloat( importeEjercer + Number(amortizacionConIVA) ) * 100 / 100;   
			}else{
				
				pisoEjercerMasAmortizaciones = pisoImporteEjercer;
				techoEjercerMasAmortizaciones = techoImporteEjercer;
				
				var suma = (  parseFloat(  $("#mImpEjercer").val()  ) * 100 / 100 );	
				
				$("#mImpEjercer").val(suma);
				
			}	
		
			if( ( facRet >= pisoImporteEjercer && facRet <= techoImporteEjercer ) ){
			
				var diferencia = 0.00;
				diferencia =  Math.abs(parseFloat($("#mImporteFacturas").val()) + (parseFloat($("#mAjustePenas").val()) * -1) -parseFloat($("#mImporteNeto").val())).toFixed(2);
				if (diferencia>-0.02 && diferencia<0.02 && diferencia != 0){
					if (!confirm("Tu pago saldra con diferencia de $"+diferencia+" en el Importe Neto, ¿Deseas continuar?")){			   			
			   			Swal.fire({ icon: 'warning',
									text: "Favor de borrar el tramite." });
			   			return;
			   		}
			   	}
			   
			}else{
				if (penalizacion > 0 ) {
					Swal.fire("Revise","El importe en Facturas + Retencion - Penas  no corresponde a la recepción.", "warning");
					return;
				} else {
					Swal.fire("Revise","El importe en Facturas + Retencion no corresponde a la recepción.", "warning");
					return;
				}
			}
			
			if ($("#destGasto").val()=="ANDV"){
				if (Number($("#saldoAnticipo").val()).toFixed(2) != (Number($("#mImpEjercer").val()) + Number($("#totalPagosAnticipo").val())).toFixed(2)){
					Swal.fire("El importe del pago no coincide con el importe correcto del anticipo", "Favor de revisar las facturas.","warning");
					return;
				}
				if ($("#destGasto").val()=="ANDV"){
					$('#tretencion').dataTable().fnClearTable();
					$("#mImporteRetencion").val(0);
					$(".subtotall").change();
				}
			}
			
			if ($("#tipoAmortizacion").val() != "1"){
				if ( Number($("#saldoAnticipo").val()) != ( Number($("#mAmortizacionAcumulado").val() ) + Number($("#mAmortizacionAnticipoMasIva").val() ) ) && $("#destGasto").val()!="ANDV" ){ 
					Swal.fire("No se puede realizar el pago", "Debido a que no cumple el total de la amortizacion del anticipo.", "warning");
					return;
				}
			}
			
			
			if (Number($("#mImpEjercer").val())<0){
				Swal.fire("Verique!","El importe no puede ser Negativo.","warning");
				return;
			}
			var aData = oTablevFact.fnGetData();
			if ( $("#destGasto").val() != "AL" && $("#destGasto").val() != ""){
				if ( aData.length == 0 ){
					Swal.fire("Capturar","Se deben Capturar las facturas","warning");
					return -1;
				}
			}
			
			if ($.trim($("#destGasto").val()) == ""){
				Swal.fire("Capturar","Favor de seleccionar un tipo destino.","warning");
				return;
			}
			
			if ($.trim($("#ctaBancaria option:selected").text())==""){											   
				Swal.fire("No se puede proceder con el pago.", "El beneficiario " + $("#cIdRFC").val() + " no tiene dada de alta alguna cuenta bancaria", "warning");
				return;
			} 
			
			if(!validaCapturaLayoutVuelos()){
				
				return;
			}else{
				$("#btnLayoutBoletos").hide();
			}
			
			agregaAmortizacion(false);
			$('#cFolioPAGODIVERSO').val($('#id_caso').val());
			$("input[id='iva']").val($("input[id='nPorcIVAAplicable']").val());
			$("input[id='nIdConcepto']").val($('#TIPO_CONCEPTO option:selected').val());
			$("input[id='tConcepto2']").val($('#TIPO_CONCEPTO option:selected').text());
			$("input[id='noFactura2']").val($("input[id='cNoFactura']").val());
			$("input[id='tipoOper']").val($('#cIdTipoOperacion option:selected').val());
			$("input[id='impTotal']").val($("input[id='mImporteNeto']").val());
			$("#tipoBenef").val($('#cIdTipoRFC option:selected').text());
			$("#iva").val($("#nPorcIVAAplicable").val());
			$("#poriva").val($("#mImporteIVA").val());
			$("#cIdRFC_RelacionGasto2").val($("#cIdRFC").val());
			$("#cMes").val($("#fRecepcion").val().split("/")[1]);				
			queryFormPost("tRelacionGastClaveBeneficiarioRead","cIdRFC_RelacionGasto2",{async: false });
			var hayError='';
			$('.subtotall').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			$('.paso01').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			$('#cIdRFC').each(function(){
				if ($(this).val()==''){
					hayError = hayError + this.name+', ';
				}
			});
			
			var mensaje = validaCapturaCorreo();
			if (mensaje != "")
				hayError += "\n" + mensaje;
			
			if (hayError==''){

				$("#ID_DESTINO_GASTO").val( $("#destGasto").val() );
				querySelectPost("cat_TipoConceptoPagosDiversosRead", "TIPO_CONCEPTO",{async: false });
				concepto();
				/*
				if(penalizacion > 0.00){
					$("#mImportePenalizacion").val(Math.round(Number(penalizacion * 1.16) * 100 ) / 100);		
				}
				*/
				//Validar REPSE JGDS
				if( validaREPSE() ){
							if ( $("#cNumeroRepse").val() != ''){
								Swal.fire("Esta seguro de haber validado el numero de registro en el portal de la STPS?","Se le recuerda que es su responsabilidad de tener el expediente actualizado y confirmar que el registro del proveedor no haya sido removido/cancelado", "info");
								$("#repseDiv").show();
							} else { 
								Swal.fire("El pago indica que aplica el art. 15D al proveedor", "Sin embargo no se encontro su numero REPSE. Por favor actualice el proveedor con la informacion faltante. ", "warning");
								return false;
							}
							
				}
				
				var auxIB = $("#mImporteBruto").val();
				//$("#mImporteBruto").val(importeRecepcionOriginal);
				queryFormPost("tContratoDiversosFactCreate",{async: false });
				queryFormPost("tPagoDiversoEncabezadoUpdate",{async: false });
				/*Actualizar estatus penas para que no pueda hacerse otro pago con esa pena*/
				if ( $("#noPenalty").val() !="-1")
					queryFormPost("actualizarEstatusPenas",{async: false });
				
				$("#mImporteBruto").val(auxIB);
				
				parent.document.getElementById("pb_save").disabled=true;
				
				$("#nPorcAmortizacion").attr('readOnly', true);//URVP
				$("#mAmortizacionAcumulado").attr('readOnly', true);//URVP
				$("#btncIDContrato").attr('disabled', true);//URVP
				$("#destGasto").attr('disabled', true);//URVP
				$("#TIPO_OPERACION").attr('disabled', true);//URVP
				$("#cNoFactura").attr('readOnly', true);//URVP
				$("#mImporteSancion").attr('readOnly', true);//URVP
				$("#mImporteDevolucion").attr('readOnly', true);//URVP
				$("#mAmortizacionAnticipo").attr('readOnly', true);//URVP
				$("#mImportePenalizacion").attr('readOnly', true);//URVP
				$("#saldoAnticipo").attr('readOnly', true);//URVP
				$("#Descrip_Concepto").attr('readOnly', true);//URVP
				cssReadOnly();
				
				$("#EditaFacturas").css('visibility', 'hidden');
				$("#EditaOpinion").css('visibility', 'hidden');

				$("#ctaBancaria").attr('disabled', true);
				$("#estatusRecepcion").val("Pagada");
				queryFormPost("updateEstatusRecepcion",{async: false });
				$("#noRecepcion").attr('disabled', true);
				$("#noPenalty").attr('disabled', true);
				
				if($("#cObservaciones").val() != ""){
					queryFormPost("tPAGODIVERSOEncObservacionesUpdate",{async: false });
				}
				$("#cObservaciones").attr('disabled', true);
				
				Swal.fire("OK!","Carátula guardada!", "success");
				$("#importeEjercer").val(Number($("#mImpEjercer").val()).toFixed(2));
				
				$(".pasoDos").show();
				$("#numPaso").val('2');
				$("#guardar").attr("disabled", true);
				$("#L03").click();
					
			}else{
				Swal.fire('Capturar','Debe ingresar los siguientes datos: '+ hayError,'warning');
			}
		}
	
	}
	
	function CAPITULO(){	
	var capitulo=$("#ep").val();
	 $("#EPDES").val( capitulo.substring(31,32));
	}

	
	 function Grid(){
		window.open("AyudasEPOBRA.jsp", 'AyudaEPsPagos', 'status=1, width=900px, height=680px, left=100px');
	 	return false;
	}
	
	function BuscaPoliza(){
		var szWhere = " nfolioPagoDiverso = " + $("#id_caso").val();
		var elMonto = "1";
		$("#laPoliza").val("");
		$("#docAplicado").val("");
		var szTabla = "TPAGODIVERSODOCAPLICADOREAD";
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: elMonto, ajax: 'false'}, function(j){
			for (var i = 0; i < j.length; i++) {
				$("#laPoliza").val(j[i].Col0);
				$("#docAplicado").val(j[i].Col1);
			}
		});
	}
	
	function validaPenalizacion(){
		var monto = "";
		monto = $("#mImportePenalizacion").val();
		if (parseInt(monto.indexOf("."),10) != parseInt(monto.lastIndexOf("."),10)){
			Swal.fire({ icon: 'warning',
						text: "No se puede captura mas de punto decimal, introduce el importe correcto." });
			$("#mImportePenalizacion").val("0");
			document.getElementById("mImportePenalizacion").focus();
		}
	}
  	
  	function cargaCtaBancariaRFC(){
		$("#campoRFC").val($.trim($("#cIdRFC").val()));
		querySelectPost("cargaCtaBancariasRFC", "ctaBancaria",{async: false });
		$("#mImporteBruto").val(0);
		
		queryFormPost("cargallevaAnticipoDiverso", {async: false });
		queryFormPost("cargatieneAticipoDiverso", {async: false });
		
		if($("#tieneAnticipo").val() == "0"){
			var tieneAntPlurianual = $("#cIDContrato").val().substr(0, 3);
			if(tieneAntPlurianual == "PLU"){
				queryFormPost("tieneAnticipoDiversoPlurianual", {async: false });
			}
		}
		queryFormPost("esProveedorExtranjero", {async:false});
	}
  	
  	function creaDialogFacturas(){
		togleDivFacts(0);
		modalValidaFact.show();
		
		$("#uploadFacturasDiv").hide();
		
		
		parent.document.getElementById("pb_save").disabled=true;
  	}

	function creaDialogOpinion(){
		
		modalOpinion.show();
		 

  	}
	
	/**
	 * Valida el archivo PDF de Opinión de Cumplimiento (Formato 32D) contra el SAT.
	 * Envía el archivo al backend para verificación y muestra el resultado al usuario.
	 */
	function validarOpinionCumplimiento() {
		
		// Validar que exista el RFC del contrato
		var rfc = $.trim($('#cIdRFC').val());
		if (!rfc || rfc === '') {
			Swal.fire({
				icon: 'warning',
				title: 'RFC requerido',
				text: 'Debe seleccionar un contrato con RFC válido antes de validar la opinión de cumplimiento'
			});
			return;
		}
		
		// Validar que se haya seleccionado un archivo
		var fileInput = $('#pdfOpinionCumplimiento32D')[0];
		if (!fileInput || fileInput.files.length === 0) {
			Swal.fire({
				icon: 'warning',
				title: 'Archivo requerido',
				text: 'Debe seleccionar un archivo PDF con la opinión de cumplimiento'
			});
			return;
		}
		
		var file = fileInput.files[0];
		
		// Validar que sea un archivo PDF
		if (file.type !== 'application/pdf') {
			Swal.fire({
				icon: 'warning',
				title: 'Tipo de archivo inválido',
				text: 'El archivo debe ser de tipo PDF'
			});
			return;
		}
		
		// Cerrar el modal
		modalOpinion.hide();
		
		// Bloquear la pantalla mientras se procesa
		$.blockUI({
			message: '<h4>Validando opinión de cumplimiento con el SAT...</h4><p>Por favor espere.</p>',
			css: { 
				border: 'none', 
				padding: '15px', 
				backgroundColor: '#000', 
				'-webkit-border-radius': '10px', 
				'-moz-border-radius': '10px', 
				opacity: .5, 
				color: '#fff' 
			}
		});
		
		// Crear FormData para enviar el archivo y el RFC
		var formData = new FormData();
		formData.append('pdfOpinionCumplimiento32D', file);
		formData.append('cIdRFC', rfc);
		
		// Enviar al backend
		$.ajax({
			url: '../validateOpinion32D',
			type: 'POST',
			data: formData,
			processData: false,  // No procesar los datos
			contentType: false,  // No establecer contentType (se establece automáticamente con boundary)
			success: function(response) {
				// Desbloquear pantalla
				$.unblockUI();
				
				console.log('Respuesta de validación:', response);
				
				// Verificar si la validación fue exitosa y positiva
				if (response.success && response.isPositive) {
					Swal.fire({
						icon: 'success',
						title: 'Validación Exitosa',
						text: 'El formato 32D se validó exitosamente con el SAT. Puede continuar el pago',
						confirmButtonText: 'Aceptar'
					});
					
					// Limpiar el input de archivo
					$('#pdfOpinionCumplimiento32D').val('');
					
				} else {
					// Opinión negativa o error en la validación
					var errorMessage = 'El archivo con el formato 32D no es procesable o no es positivo, envie al administrador.';
					
					if (response.message) {
						errorMessage += '\n\nDetalle: ' + response.message;
					}
					
					Swal.fire({
						icon: 'error',
						title: 'Error de Validación',
						text: errorMessage,
						confirmButtonText: 'Aceptar'
					});
				}
			},
			error: function(xhr, status, error) {
				// Desbloquear pantalla
				$.unblockUI();
				
				console.error('Error en validación:', status, error);
				console.error('Respuesta del servidor:', xhr.responseText);
				
				var errorMessage = 'El archivo con el formato 32D no es procesable o no es positivo, envie al administrador.';
				
				// Intentar extraer mensaje de error del servidor
				try {
					var errorResponse = JSON.parse(xhr.responseText);
					if (errorResponse.error) {
						errorMessage += '\n\nDetalle: ' + errorResponse.error;
					}
				} catch (e) {
					// Si no se puede parsear, usar mensaje genérico
					if (xhr.status === 401) {
						errorMessage = 'Su sesión ha expirado. Por favor, inicie sesión nuevamente.';
					} else if (xhr.status === 0) {
						errorMessage = 'No se pudo conectar con el servidor. Verifique su conexión a internet.';
					}
				}
				
				Swal.fire({
					icon: 'error',
					title: 'Error',
					text: errorMessage,
					confirmButtonText: 'Aceptar'
				});
			}
		});
	}
  	
  	
  	function aceptarFacturas() {
  		bClicBtn = true;
		var aData = oTablevFact.fnGetData();
		$("#mImporteFacturas").val( quitaFmt( $("#mTotalFacturaV").val() ) );
		$(".subtotall").change();
		calculaIVA();
		modalValidaFact.hide();
		revisaTipoFacturas();
		
		parent.document.getElementById("pb_save").disabled=false;
  	}
  	
  	function cerrarFacturas() {
  		parent.document.getElementById("pb_save").disabled=false;
		calculaIVA();
		revisaTipoFacturas();
  	}
  	
	
	function creaDTFacturas(){
		oTablevFact = $('#grdValidaFacturas').dataTable(
		{
			"bProcessing": true,
			"bServerSide": true,
			"bDestroy": true,
			"bSort": true, 
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vfacturaspagos&qw=folioPago=<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%> AND tipoPago='PAGODIVERSO'" ,
			"bJQueryUI": true,
			"sScrollX": "100%",
			//"sScrollXInner": "100%",
			"sScrollY": "190px",
			"bPaginate": false,
			"bAutoWidth": true,
			"bInfo": true,
			aoColumns: [
						{ sName: "Serie" },
						{ sName: "Factura" },
						{ sName: "Importe" } ]	
		});
	}
	
	
	function dlgJustificacion(){

		
		queryFormPost("retencionEliminada", {async:false});
		
		if( $("#lEliminaRetencion6IVA").val() == 1){
			creaDialogJustificacion(true);
			
		}	
	}	
	
	 function creaDialogJustificacion(abrir){
			$( "#dialog-Justificacion" ).dialog({
				autoOpen: false,
				height: 472,
				width: 558,
				modal: true,
				buttons: {
						"Aceptar": function(){
							togleJustificacion();											
						},
						"Cancelar": function(){
							bClicBtn = true;
							$( this ).dialog( "close" );
						}
					},
				close: function() {	
				},
				open: function(){
				}
			});
			
			if( abrir ){
				$( "#dialog-Justificacion" ).dialog("open");	
			}
	}
	
	function togleJustificacion(){
	
			queryFormPost("validaDoctoJustificacion", {async: false });
	
			if ($("#doctoCapturado").val() > 0) {
	  			parent.document.getElementById("pb_save").disabled = true;
	  			parent.document.getElementById("pb_send").click();
			}
			$( "#dialog-Justificacion" ).dialog("close");
		}	
	
	function togleDivFacts(nIdDiv){
		if( nIdDiv == 0){
			$("#uploadFacturasDiv").hide();
			$("#facturasCapturadasDiv").show();
			creaDTFacturas();
			queryFormPost("readMontoFacturasNeto", {async:false});
		}else{
			if ($("#esExtranjero").val()=="1")
				$('#uploadFacturasFrm').attr('src', "UploadFacturasExtranjeros.jsp");
			else
				$('#uploadFacturasFrm').attr('src', "UploadFacturas.jsp?tipo_pago=PAGODIVERSO&contratoVales="+$("#vales_combustible").val() );
			$("#uploadFacturasDiv").show();
			$("#facturasCapturadasDiv").hide();
		}
			
	}
	function updateFirmantes(){
		queryFormPost("readcDocumentoHaplicadoPago",{async: false });
		if ($("#cEstatusPago").val()!="C"){
			llenaFirmanteVoBo();
			llenaFirmanteAut();
			llenaSuplenteVoBo();
			llenaSuplenteAut();
			
			$("#cNombreEla").val(cNombreElabora);
		 	$("#cPaternoEla").val(cApellidoPaternoElabora);
		 	$("#cMaternoEla").val(cApellidoMaternoElabora);
		 	$("#cPuestoEla").val(cPuestoElabora);
			modalFirmantes.show();
		 	
		}else{
			Swal.fire({ icon: 'warning',
						text: "El pago ya está en estatus de Cancelado, por lo que ya no se puede cambia la firma." });						
		}
	}
	
	function changeIVA(){
		$("#nPorcIVAAplicable").val("0");
	}
	
	let importeNetoRecepcion;
	function readInfoRecepcionMat(){
		
		/*
		 * EHR2015 Al seleccionar el contrato valida el tipo de amortizacion que requiere:
		 * 1 Amortizacion por factor
		 * 0 Amortizacion normal.
		 */
		var tipoRecepcion = $("#noRecepcion").val().substr(0,2);	
		
		queryFormPost("tFactDiversoAnticipoRead", {async: false });
				
		$("#tipoAmortizacion").val("");
		queryFormPost("readTipoAmortiza", {async:false});

		queryFormPost(
			{
				queryName:"readInfoRecepcionMat",
				async:false,
				callback:function(){
					
					//EHR2015 Guarda el monto original para la recepcion
					importeRecepcionOriginal = Number(  quitaFmt( $("#mImporteBruto").val() )  ) * 100 / 100;
					
					//Llamar al recalculo de cosas
					var importee = 0.00;
					importee = Number(quitaFmt($("#mImporteBruto").val()));
					importee = importee.toFixed(2);
					
					$("#mImporteBruto").val(importee);
					$("#importeBrutoAux").val(importee);
					
					importee = Number(quitaFmt($("#mImporteIVA").val()));
					importee = importee.toFixed(2);
					$("#mImporteIVA").val(importee);
					
					importee = Number(quitaFmt($("#mOtrosImpuestos").val()));
					importee = importee.toFixed(2);
					$("#mOtrosImpuestos").val(importee);
					
					importeNetoRecepcion = Number(quitaFmt($("#mImporteNeto").val()));
					importeNetoRecepcion = importeNetoRecepcion.toFixed(2);
					$("#mImporteMasIva").val(importeNetoRecepcion);
					
					importee = Number(quitaFmt($("#mImporteSancion").val()));
					importee = importee.toFixed(2);
					$("#mImporteSancion").val(importee);
					
					calculaIVA();
					
					$("#mImportePenalizacion").val("0.00");
					$("#mImporteDevolucion").val("0.00");
					$("#destGasto").attr('disabled', false);
				}
			}
		);
		
		/*Habilitar combo para penas y deducciones*/
		queryFormPost("penaltyExist", {async:false});
		if($("#tienePenas").val()!="0"){
			$("#penas").css('display', 'block');
		} 
	}
	
	function readInfoPenas(){
		if($("#noPenalty").val() != "-1") {
			queryFormPost({
				queryName:"readPenaltyPayment",
				async:false,
				callback:function(){
					
					var importe = Number(quitaFmt($("#mImportePenalizacion").val()));
					importe = importe.toFixed(2);
					$("#mImportePenalizacion").val(importe);
					calculaIVA();		
				}
			});
		} else {
			$("#mImportePenalizacion").val(0);
			calculaIVA();
		}
	}
	
	function habilitaBtnCargaLayout(){
		queryFormPost("esProveedorBoletajeRead", {async:false});
		
		if($("#esProvBoletaje").val() == "1"){
 			$("#btnLayoutBoletos").show();
		}else{
 			$("#btnLayoutBoletos").hide();
		}
	}
	

	function creaDialogCorreo() {
			
			queryFormPost("mostrarCorreoRead", { async: false });
			modalCorreo.show();
			
	}
	
	function creaDlgCargaBoletaje(){
		$( "#dlgBoletajeAvion" ).dialog({
			autoOpen: false,
			height: 350,
			width: 700,
			modal: true
		});
	}


	
	function validaCapturaLayoutVuelos(){
		var bReturn = true;
		
		if($("#esProvBoletaje").val() == "1"){
			queryFormPost("validaCapturaLayoutVuelosRead", {async:false});
			if($("#capturaLayoutVuelos").val() != "1"){
				Swal.fire({ icon: 'warning',
							text: "No ha capturado Vuelos para el Pago. Verifique!!" });
				bReturn = false;
			}else{
				bReturn = insertarFolioLayout();
			}
		}
		
		return bReturn;
	}
	
	function showDivOficioVoBo(esUpdate){
		var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
	

	
	
	function creaDlgFoliosVuelos(){
		$( "#dlgFoliosVuelos" ).dialog({
			autoOpen: false,
			height: 350,
			width: 700,
			modal: true,
			buttons: {
						"Aceptar": function(){
							if(insertarFolioLayout()){							
								$( this ).dialog( "close" );
							}						
						},
						"Cancelar": function(){							
							$( this ).dialog( "close" );
						}
					},				
				open: function(){				
					
				}
		});
	}
	
	function abrirDlgFoliosVuelos(){
		var folioPago = $("#nFolioPago").val();
		var centroContable = $("#cCentroContable").val();
		var importePago = Number($("#mImpEjercer").val());
		var nFolioVuelos = $("#nFolioVuelos").val();
		
		importePago = importePago.toFixed(2);	
		
		var login = $("#u_login").val();
		
		window.open(
						'SeleccionaVuelos.jsp?folioPago='+folioPago+'&centroContable='+centroContable+'&importePago='+importePago+'&ulogin='+login+'&nFolioVuelos='+nFolioVuelos+'&cTipoPago='+$("#cTipoPago").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=650"
					);
	}		
	
	function insertarFolioLayout(){	
		
		var bReturn = false; 
		
		queryFormPost("importeVuelosDetCapturadosRead", {async:false});
		
		var mImportePago = Number($("#mImpEjercer").val());
		var mImporteVuelos = Number($("#mImporteVuelos").val());
					
		mImportePago = mImportePago.toFixed(2);
		mImporteVuelos = mImporteVuelos.toFixed(2);
		
		if(mImportePago == mImporteVuelos){
			queryFormPost( { queryName:"tPagoDiversoBoletajeAvionCreate", async:false, callback:function(){ bReturn = true;} });					
		}else{
			Swal.fire("Verifique!!","El importe Total de Vuelos es diferente al total del Pago.", "warning");
		}
		
		return bReturn;
	}
	
	function actualizaFolioVueloPagado(){
		queryFormPost("vuelosDetEsPagadoUpdate", {async : false});
	}	
	
	function verReferencia(){
		
		$("#dlgSeleccionaFonden").dialog("open");
		
		if( <%=id_oper%> != 1 ){
			queryFormPost( "proyectoFondenRead", {async:false} );
			$("#verProyectoFondenDiv").css('display', 'block');
			$("#selproyectoFondenDiv").css('display', 'none');
		}		
	}
	
	function borraOfiDelegatorio(){
	
		if($("#firmanteOficioExiste").val() == "Existe"){
			queryFormPost({	queryName : "borrarOficioDelegatorio", async : false, callback : function(){ 															
								msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
								} 
						});
			$("#cFolioOficioUpdate").val("");
			$("#dFechaOficioUpdate").val("");
			$("#cNombreTitularUpdate").val("");
			$("#cApellidoPaternoTitularUpdate").val("");
			$("#cApellidoMaternoTitularUpdate").val("");
			$("#cPuestoTitularUpdate").val("");
		}
		
		if($("#firmanteOficioVoBoExiste").val() == "Existe"){
			queryFormPost({	queryName : "borrarOficioDelegatorioVoBo", async : false, callback : function(){ 															
								msn = "Oficios borrados del pago " + $("#cTipoPago").val() + " no. " + $("#nFolioPago").val();
								} 
						});
			$("#cFolioOficioVoBoUpdate").val("");
			$("#dFechaOficioVoBoUpdate").val("");
			$("#cNombreTitularVoBoUpdate").val("");
			$("#cApellidoPaternoTitularVoBoUpdate").val("");
			$("#cApellidoMaternoTitularVoBoUpdate").val("");
			$("#cPuestoTitularVoBoUpdate").val("");
		}							
		
	}
	
	function muestraCalculoCedular(){
		$("#tieneCedular").val("0");
		
		queryFormPost("tieneImpuestoCedularPDIV_Read", {async : false});
		
		if( $("#tieneCedular").val() == "1" || ( esSAIAlterno  && "S01" == $("#cUnidadResponsable").val() ) ){
			$("#btnCalculaCedular").show();
		}else{
			$("#btnCalculaCedular").hide();
		}		
	}
	
	function calculaCedular(){
		queryFormPost("calculaImpuestoCedularPDIV_Read", {async : false});
		$("#mImporteFacturas").val( Number(quitaFmt( $("#mTotalFacturaV").val() )).toFixed(2) );
	}
	
	
	function llenaFirmanteVoBoUpdate(){
		querySelectPost("cboFirmanteModuloVoBo_Read", "cboVoBoUpdate",{async: false });
		cargaFirmanteVoBoUpdate();
	}
	
	function cargaFirmanteVoBoUpdate(){
		$("#nNumEmpleadoVoBo").val($("#cboVoBoUpdate").val());		
		queryFormPost("firmantesModuloVoBoUpdate_Read", {async:false});
	}
	
	function llenaFirmanteAutUpdate(){
		querySelectPost("cboFirmanteModuloAut_Read", "cboAutorizaUpdate",{async: false });
		cargaFirmanteAutUpdate();
	}
	
	function cargaFirmanteAutUpdate(){
		$("#nNumEmpleadoAut").val($("#cboAutorizaUpdate").val());		
		queryFormPost("firmantesModuloAutUpdate_Read", {async:false});
	}
	
	function llenaSuplenteVoBoUpdate(){
		querySelectPost("cboFirmanteModuloVoBoSuplente_Read", "cboSuplenteVoBoUpdate",{async: false });
		cargaSuplenteVoBoUpdate();
	}
	
	function cargaSuplenteVoBoUpdate(){
		$("#nNumEmpleadoVoBoSuplencia").val($("#cboSuplenteVoBoUpdate").val());		
		queryFormPost("firmantesModuloVoBoSuplenteUpdate_Read", {async:false});
	}
	
	function llenaSuplenteAutUpdate(){
		querySelectPost("cboFirmanteModuloAutSuplente_Read", "cboSuplenteAutUpdate",{async: false });
		cargaSuplenteAutUpdate();
	}
	
	function cargaSuplenteAutUpdate(){
		$("#nNumEmpleadoAutSuplencia").val($("#cboSuplenteAutUpdate").val());		
		queryFormPost("firmantesModuloAutSuplenteUpdate_Read", {async:false});
	}
	
</script>
</head>
<br/>
<body id="dt_example">
	<div id="container" style="width: 90%" class="container">
		<form id="frmcontratoDeObra" method="post">
		 
		 
		 	<input type="hidden" id="esSAIAlterno"       value="<%=esSAIAlterno%>"/>
		 	<input type="hidden" id="vales_combustible"       value="false"/>
			<input type="hidden" id="iva"       value="1.0"/>
			<input type="hidden" id="esEFO"       name="esEFO"       value="N"/>
			<input type="hidden" id="rfcValidar"  name="rfcValidar"  value=""/>
			<input type="hidden" id="lEliminaRetencion6IVA" name="lEliminaRetencion6IVA"  value="0"/>	
			<input type="hidden" id="TipoAutorizacion" name="TipoAutorizacion" value=""/>
			<input type="hidden" id="cNombreVo" name="cNombreVo"/>
			<input type="hidden" id="cPaternoVo" name="cPaternoVo"/>
			<input type="hidden" id="cMaternoVo" name="cMaternoVo"/>
			<input type="hidden" id="cPuestoVo" name="cPuestoVo"/>
			<input type="hidden" id="cEmpleadoVo" name="cEmpleadoVo"/>
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo"/>
			<input type="hidden" id="cEmpleadoA" name="cEmpleadoA"/>
			<input type="hidden" id="doctoCapturado" name="doctoCapturado" value = 0>
			<input type="hidden" id="cProyectoFonden" name="cProyectoFonden" value="" />
			<input type="hidden" id="correcto" name="correcto" value=""/>
			<!-- EHR2015 Para guardar el tipo de amortizacion -->
			<input name="tipoAmortizacion" id="tipoAmortizacion" value="" type="hidden" />
			<input name="mesAbierto" id="mesAbierto" value="" type="hidden" />
			<input name="otrosImpuestos" id="otrosImpuestos" type="hidden" value="0"/>
			<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=c.getFolio().substring( c.getFolio().lastIndexOf( '-' ) + 1 )%>">
			<input name="cTipoPago" id="cTipoPago" type="hidden" value="PAGODIVERSO"/>
			<input name="tipoTramite" id="tipoTramite" type="hidden" value="<%=idTipoCaso%>"/>
			<input name="campoRFC" id="campoRFC" type="hidden"/>
			<input name="totFacturas" id="totFacturas" type="hidden" value="0"/>
			<input name="esRelacionGastos" id="esRelacionGastos" type="hidden" value="N"/>
			<input name="nPorcIVAAplicable2" id="nPorcIVAAplicable2" type="hidden" value="0"/>
			<input name="importeTotalEncabezado" id="importeTotalEncabezado" type="hidden" value="0"/>
			<input name="importeTotalDetalle" id="importeTotalDetalle" type="hidden" value="0"/>
			<input name="importeTotalIvaEnc" id="importeTotalIvaEnc" type="hidden" value="0"/>
			<input name="importeTotalIvaDet" id="importeTotalIvaDet" type="hidden" value="0"/>
			<input name="importeDifIVA" id="importeDifIVA" type="hidden" value="0"/>
			<input name="mOtrosImpuestosEP" type="hidden" id="mOtrosImpuestosEP" value="0"/>
			<input name="llevaAnticipo" id="llevaAnticipo" type="hidden"/>
			<input name="tieneAnticipo" id="tieneAnticipo" type="hidden"/>
			<input name="cEstatusPago" id="cEstatusPago" type="hidden" value=""/>
			<input name="estatusRecepcion" id="estatusRecepcion" type="hidden" value=""/>
			
			<!-- PARA LKS EP -->
			<input type="hidden" value="0" id="m23IVA" name="m23IVA"/>
			<input type="hidden" value="0" id="mIMDT" name="mIMDT"/>
			<input type="hidden" value="0" id="mCNIC" name="mCNIC"/>
			<input type="hidden" value="0" id="mObra5" name="mObra5"/>
			<input type="hidden" value="0" id="mAmortizado" name="mAmortizado"/>
			<input type="hidden" value="0" id="mISRHonorarios" name="mISRHonorarios"/>
			<input type="hidden" value="0" id="mImporteFlete4" name="mImporteFlete4"/>
			<input type="hidden" value="0" id="mISRArrenda" name="mISRArrenda"/>
			<input type="hidden" value="0" id="mPenalizacion" name="mPenalizacion"/>
			<input type="hidden" value="0" id="mRetImpuestoCedular" name="mRetImpuestoCedular"/>
			<input type="hidden" value="0" id="mTesofe" name="mTesofe"/>
			<input type="hidden" value="0" id="DCD_ISR_D" name="DCD_ISR_D"/>
			<input type="hidden" value="0" id="DCD_IVA_D" name="DCD_IVA_D"/>
			<input type="hidden" value="0" id="DCD_MIL5_D" name="DCD_MIL5_D"/>
			<input type="hidden" value="0" id="DCD_MIL2_D" name="DCD_MIL2_D"/>
			<input type="hidden" value="0" id="DCD_CONTRIBUCION_D" name="DCD_CONTRIBUCION_D"/>
			<input type="hidden" value="0" id="DCD_OTRAS_RET_D" name="DCD_OTRAS_RET_D"/>
			<input type="hidden" value="0" id="DCD_PENALIZACION_D" name="DCD_PENALIZACION_D"/>
			<input type="hidden" value="0" id="DCD_SANCION_D" name="DCD_SANCION_D"/>
			<input type="hidden" value="0" id="DCD_DEVOL_D" name="DCD_DEVOL_D"/>
			<input type="hidden" value="0" id="DCD_AMORT_D" name="DCD_AMORT_D"/>
			<input type="hidden" value="0" id="DCD_RETENCION_D" name="DCD_RETENCION_D"/>
			<input type="hidden" value="0" id="mIVA_D" name="mIVA_D"/>
			<input type="hidden" value="0" id="CamInst" name="CamInst"/>
			<input type="hidden" value="0" id="mImporteFlete23" name="mImporteFlete23"/>
			<input type="hidden" value="0" id="mImporteIvaArrenda" name="mImporteIvaArrenda"/>

			<input type="hidden" value="" id="lHaySaldoAnticipo" name="lHaySaldoAnticipo"/>
			<input type="hidden" value="0" id="TotalNeto" name="TotalNeto"/>
			<input type="hidden" value="0" id="TotalNetoN" name="TotalNetoN"/>
			<input type="hidden" id="montoprevio" name="montoprevio"/>
			<input type="hidden" value="0" id="TotalRetenciones" name="TotalRetenciones"/>
			<input type="hidden" value="0" id="campoRetencion" name="campoRetencion"/>
			<input type="hidden" value="0" id="mIVA" name="mIVA"/>
			<input type="hidden" value="DIVERSO" id="TO_TIPO_DOCTO" name="TO_TIPO_DOCTO"/>
			<input type="hidden" value="0" id="m23IVA2" name="m23IVA2"/>
			<input type="hidden" value="0" id="mIMDT2" name="mIMDT2"/>
			<input type="hidden" value="0" id="mCNIC2" name="mCNIC2"/>
			<input type="hidden" value="0" id="mObra52" name="mObra52"/>
			<input type="hidden" value="0" id="mISRHonorarios2" name="mISRHonorarios2"/>
			<input type="hidden" value="0" id="mImporteFlete42" name="mImporteFlete42"/>
			<input type="hidden" value="0" id="mISRArrenda2" name="mISRArrenda2"/>
			<input type="hidden" value="0" id="mPenalizacion2" name="mPenalizacion2" value = "-1"/>
			<input type="hidden" value="0" id="mRetImpuestoCedular2" name="mRetImpuestoCedular2"/>
			<input type="hidden" value="0" id="mTesofe2" name="mTesofe2"/>
			<input type="hidden" value="0" id="CamInst2" name="CamInst2"/>
			<input type="hidden" id="laPoliza"/>
			<input type="hidden" id="docAplicado" value=""/>
			<input type="hidden" value="0" id="mAmortizacionAnticipoMasIva" name="mAmortizacionAnticipoMasIva"/>
			<input type="hidden" value="0" id="mAmortizacionAnticipoMasIvaEP" name="mAmortizacionAnticipoMasIvaEP"/>
			<input type="hidden" value="" id="rowsAffected" name="rowsAffected"/>
			<input type="hidden" id="cFolioPAGODIVERSO" name="cFolioPAGODIVERSO"/>
			<input type="hidden" id="tipoRet" name="tipoRet" value="" />
			<input type="hidden" id="resImpRet" name="resImpRet" value="" />
			<input type="hidden" id="nMes" name="nMes" value="2"/>
			<input type="hidden" id="cIdCuentaContable" name="cIdCuentaContable" value=""/>
			<input type="hidden" id="numPaso" name="numPaso" value="1"/>
			<input type="hidden" id="TOTALSUBCUENTA" name="TOTALSUBCUENTA" />
			<input type="hidden" value="2012" id="cEjercicio" name="cEjercicio"/>
			<input type="hidden" value="" id="tiene" name="tiene"/>
			<input type="hidden" id="cRamo" name="cRamo"/>
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable"/>
			<input type="hidden" id="OIRAUSU" name="OIRAUSU" />
			<input type="hidden" id="cIdRFC_RelacionGasto2" name="cIdRFC_RelacionGasto2"/>
			<input type="hidden" id="sumareten" name="sumareten"/>
			<input type="hidden" id="porcCedular" name="porcCedular" />
			<input type="hidden" id="doster" name="doster" />
			<input type="hidden" id="cIdTipoDocumento" value="0" name="cIdTipoDocumento" />
			<input type="hidden" id="cllave" name="cllave" />
			<input type="hidden" id="nFolioCompromiso" value="0" name="nFolioCompromiso" />
			<input type="hidden" id="cCentroContable" name="cCentroContable"/>
			<input type="hidden" id="cCentroContableOrig" name="cCentroContableOrig"/>
			<input type="hidden" id="cCentroContableContrato" name="cCentroContableContrato"/>
			<input type="hidden" id="cUnidadContrato" name="cUnidadContrato"/>
			<input type="hidden" id="cMes" name="cMes"/>
			<input type="hidden" id="aEjercicioFiscal" value="2012" name="aEjercicioFiscal"/>
			<input type="hidden" id="cEvento" name="cEvento" value=""/>
			<input type="hidden" id="contra2" name="contra2" value=""/>
			<input type="hidden" id="partida" name="partida" value=""/>
			<input type="hidden" id="vOGT" name="vOGT" value="" />
			<input type="hidden" id="obs" name="obs"/>
			<input type="hidden" id="cIdContrato" name="cIdContrato" value=""/>
			<input type="hidden" id="tConcepto2" name="tConcepto2"/>
			<input type="hidden" id="nIdConcepto" name="nIdConcepto"/>
			<input type="hidden" id="codSIAFF" name="codSIAFF" />
			<input type="hidden" id="saldoCompA" name="saldoCompA"/>
			<input type="hidden" id="saldoPorEjercer" name="saldoPorEjercer"/>
			<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/>
			<input type="hidden" id="ID_TIPO_MOVIMIENTO" name="ID_TIPO_MOVIMIENTO"/>
			
			<!-- hidden para la captura de los datos de quien elaboro -->
			<input type="hidden" id="cNombreE" name="cNombreE" />
			<input type="hidden" id="cPaternoE" name="cPaternoE" />
			<input type="hidden" id="cMaternoE" name="cMaternoE" />
			<input type="hidden" id="cPuestoE" name="cPuestoE" />
			<input type="hidden" id="firmanteEla" name="firmanteEla" />
			<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value=""/>
			<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
			<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value=""/>
			<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value=""/>
			<input type="hidden" id="numeroEmpleadoVoBo" name="numeroEmpleadoVoBo" value=""/>
			<input type="hidden" id="numeroEmpleadoAutoriza" name="numeroEmpleadoAutoriza" value=""/>
			
			<input type="hidden" id="campo" name="campo" value="">
			<input type="hidden" id="tablaEnc" name="tablaEnc" value="">
			<input type="hidden" id="campoCondicion" name="campoCondicion" value="">
			<input type="hidden" id="tablaDet" name="tablaDet" value="">
			<input type="hidden" id="tipoAplicar" name="tipoAplicar" value="">
			<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
			<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="">
			<input type="hidden" id="nombre" name="nombre" value="">
			<input type="hidden" id="FOLIO" name="FOLIO" value="<%=c.getFolio()%>" />
			<input type="hidden" id="OPERADOR" name="OPERADOR" value="<%=c.getCasoOperacion( 0 ).getResponsable()%>" />
			<input type="hidden" id="FECHA_CARGA" name="FECHA_CARGA" value="<%=today%>" />
			<input type="hidden" id="cIDContratoObra" name="cIDContratoObra" />
			<input type="hidden" id="cartera" name="cartera" value="">
			<input type="hidden" id="capitulo" name="capitulo" value="">
			<input type="hidden" id="UE" name="UE" value="">
			<input type="hidden" id="EP" name="EP" value="">
			<input type="hidden" id="mImporteCartera" name="mImporteCartera" value="">
			<input type="hidden" id="cValidaFactura" name="cValidaFactura" value="" />
			<input type="hidden" id="cExiste" name="cExiste" value="0" />
			<input type="hidden" id="mImporteBrutoF" name="mImporteBrutoF" value="0" />
			<input type="hidden" id="COMSOCAutoriza" name="COMSOCAutoriza" value="" />
			<input type="hidden" id="partCOMSOC" name="partCOMSOC" value="" />
			<input type="hidden" id="cMsjCOMSOC" name="cMsjCOMSOC" value="">
			<input type="hidden" id="IEsPasivo" name="IEsPasivo" value=""/>
			<input type="hidden" id="cOBGT" name="cOBGT" />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste"/>

			<input type="hidden" id="tipoFirmante" name="tipoFirmante" value="PAGO_VOBO"/>
			<input type="hidden" id="cNombreA" name="cNombreA"/>
			<input type="hidden" id="cPaternoA" name="cPaternoA"/>
			<input type="hidden" id="cMaternoA" name="cMaternoA"/>
			<input type="hidden" id="cPuestoA" name="cPuestoA"/>
			<input type="hidden" id="firmanteAut" name="firmanteAut"/>
			<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" size=15 value="PAGO_AUT"/>
			<input type="hidden" id="facturasDiferentes" name="facturasDiferentes" value="0"/>
			<input type="hidden" id="importeBrutoAux" name="importeBrutoAux"/>
			<input type="hidden" id="cEPborrar" name="cEPborrar"/>
			<input type="hidden" id="mImporteBorrar" name="mImporteBorrar"/>
			<input type="hidden" id="renglonBorrar" name="renglonBorrar"/>
			<input type="hidden" id="esExtranjero" name="esExtranjero" value=""/>
			<input type="hidden" id="mImporteDiferenciaCentavos" name="mImporteDiferenciaCentavos" value="0.00"/>
			<input type="hidden" id="cFolioGestion" name="cFolioGestion" value="<%=c.getFolio()%>"/>

			<!-- hidden para la captura de oficio delegatorio -->
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
			<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>


			<!-- hidden para validar si el contrato tiene remanente para realizar el pago de anticipo -->
			<input type="hidden" id="tieneRemanente" name="tieneRemanente" value=""/>

			<!-- hidden para obtener los pagos de anticipo que se han hecho. -->
			<input type="hidden" id="totalPagosAnticipo" name="totalPagosAnticipo" value="0"/>

			<!-- hidden para validar si el rfc del proveedor es de boletaje de avion. -->
			<input type="hidden" id="esProvBoletaje" name="esProvBoletaje" value=""/>
			<input type="hidden" id="u_login" name="u_login" value="<%=U_LOGIN%>" />
			<input type="hidden" id="capturaLayoutVuelos" name="capturaLayoutVuelos" value=""/>

			<!-- hidden para validar si las EPS pertenecen a CECFOR -->
			<input type="hidden" id="esEPCECFOR" name="esEPCECFOR" value=""/>

			<!-- hidden para validar si la partida es de viaticos -->
			<input type="hidden" name="cPartida" id="cPartida" value=""/>
			<input type="hidden" name="esPartidaViaticos" id="esPartidaViaticos" value="0"/>

			<!-- hidden para la captura de oficio delegatorio VoBo-->
			<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
			<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
			<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
			<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
			<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>

			<input type="hidden" id="nFolioVuelos" name="nFolioVuelos" value=""/>
			<input type="hidden" id="rfcVuelos" name="rfcVuelos" value=""/>
			<input type="hidden" id="numBoleto" name="numBoleto" value=""/>
			<input type="hidden" id="mImporteVuelos" name="mImporteVuelos" value=""/>
			<input type="hidden" id="impteBoleto" name="impteBoleto" value=""/>
			<input type="hidden" id="mImporteRetenPago" name="mImporteRetenPago" value="0"/>
			<input type="hidden" id="tieneCedular" name="tieneCedular" value="0"/>
			<input type="hidden" id="tienePenas" name="tienePenas" value="0"/>
			<input type="hidden" id="mAjustePenas" name="mAjustePenas" value="0"/>

			<!-- HIDDEN PARA VALIDAR SI SE AUTORIZA CON FIEL -->
			<input type="hidden" id="cAplica15D" name="cAplica15D" value="false"/>
			<input type="hidden" id="cNumeroRepse" name="cNumeroRepse" value="false"/>		
			<input type="hidden" id="msgRF" name="msgRF" value=""/>
			
			<!-- hidden para captura de correo-->
			<input type="hidden" id="esPPD" name="esPPD" value=""/>
			<input type="hidden" id="cIDRFC" name="cIDRFC" value=""/>
			<input type="hidden" name="correo" id="correo" />
			<input type="hidden" name="nCorreo" id="nCorreo" />
			<input type="hidden" name="paternoCorreo" id="paternoCorreo" />
			<input type="hidden" name="maternoCorreo" id="maternoCorreo" />
			<input type="hidden" name="cCargo" id="cCargo" />
			<input type="hidden" name="nIdPenaltyDeduction" id="nIdPenaltyDeduction" />
			<div class="alert alert-danger" role="alert" id="repseDiv" style="display: none">
			  <b>Informaci&oacute;n Importante</b><br> El proveedor cuenta con Numero de  REPSE. Por favor valide antes de autorizar el pago que el registro sea vigente en la ruta:
			  <br>
				<a href="https://repse.stps.gob.mx" target="_blank">https://repse.stps.gob.mx</a> 
			</div>
			
			<div class="dvGeneral">
				<div class="card-header"> <h3> Pago con Orden de Compra - Recepci&oacute;n De Documentos </h3> </div>
				
				<div id="ContratoDeObra">
					<div class="row" >					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1 divAutorizar">
							<label for="lbAutoriza"> Autorizar: </label>&nbsp;
							<input type="radio" name="grpAutorizar" id="grpAutorizar1" class="form-check-input" value = "Si" checked="checked"/> Si&nbsp;&nbsp;
							<input type="radio" name="grpAutorizar" id="grpAutorizar2" class="form-check-input" value = "No"/> No
						</div>										
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="divImprimePoliza">
							<img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('PolizaPago');"/> Poliza  
						</div>					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1" id="divImprimeAnexo">
							<img src="imagenes/Imprimir.png" width="25" height="21" onClick="anexo('PolizaPagoN');"/> Anexo  
						</div>						
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>  
						</div>
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<label for="caNoContrarrecibo"> ContraRecibo: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input type="text" id="caNoContrarrecibo" name="caNoContrarrecibo" class="form-control form-control-sm" readonly value="0" />
						</div>											
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
					</div>
					
					<div class="row" >					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="cIDContrato"> No Contrato: </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<div class="input-group">	 														
								<input type="text" name="cIDContrato" id="cIDContrato" onChange="FiltroRetencionesD();changeIVA();" class="form-control form-control-sm AyudaSyC" readonly title="Selecciona un contrato"/>
							</div>							
						</div>	
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="cIDContrato"> Folio: </label>													
						</div>	
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<input name="id_caso" type="text" id="id_caso" class="form-control form-control-sm" readonly/>							
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<input type="button" value="Borrar" onclick="Borrar1();" id="Borrar" name="Borrar" title="Elimina la solicitud temporal" class="btn btn-secondary btn-sm"/>
						</div>
					</div>	
					
					<div class="row" >					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="cIdRFC"> RFC: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
							<input type="text" name="cIdRFC" id="cIdRFC" class="form-control form-control-sm" size="15" maxlength="15" readonly/>
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1"> 
							<input type="text" name="cobjetocontrato" id="cobjetocontrato" class="form-control form-control-sm" size="50" maxlength="50" readonly/>							
						</div>	
					</div>
					
					<div class="row" id="trctaBancaria">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="ctaBancaria"> Cta Bancaria: </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">																			
							<select id="ctaBancaria" name="ctaBancaria" class="form-select form-select-sm"></select>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">																			
							<label for="noRecepcion"> Recepci&oacute;n: </label>
						</div>						
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<select id="noRecepcion" name="noRecepcion" onchange="readInfoRecepcionMat();" class="form-select form-select-sm"></select>
						</div>						
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="fRecepcion"> Fecha Captura: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">								
								<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
								<input type="text" class="form-control form-control-sm paso01" id="fRecepcion" name="fRecepcion" readonly/>
								<input type="hidden" maxlength="10" size="10" id="fRecepcion2" name="fRecepcion2"/> 
								<input type="hidden" maxlength="10" size="10" class="paso01" id="fecha_Pago" name="fecha_Pago"/>
							</div> 							
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">																			
							<label for="TIPO_OPERACION"> Tipo de Operaci&oacute;n: </label>
						</div>						
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<select class="form-select form-select-sm paso01" id="TIPO_OPERACION" name="TIPO_OPERACION"></select>
						</div>
					</div>
					
					<div class="row">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
							<label for="destGasto"> Tipo Destino: </label>
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<select id="destGasto" name="destGasto" class="form-select form-select-sm" disabled></select>						
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<span id="EditaFacturas" style="visibility:hidden">
								<button type="button" class="btn btn-secondary btn-sm" onclick="creaDialogFacturas(); return false;"> Facturas </button>	
							</span>							
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<span id="EditaOpinion" style="visibility:hidden">
								<button type="button" class="btn btn-secondary btn-sm" onclick="creaDialogOpinion(); return false;"> Opinión de Cumplimiento </button>	
							</span>							
						</div>
						
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							<span id="EditaCorreoE" style="visibility:hidden"><a href="#" onclick="creaDialogCorreo();return false;"> Captura Correo </a></span>
							<input type="hidden" maxlength="10" size="10" id="ID_DESTINO_GASTO2" name="ID_DESTINO_GASTO2"/> 
							<input type="hidden" maxlength="10" size="10" id="ID_TIPO_FONDO" name="ID_TIPO_FONDO"/> 
							<input type="hidden" name="cNoEstimacion" id="cNoEstimacion" value="0" class="paso01" size="14" maxlength="14"/>
						</div>					
						<div class="col-12 col-lg-5 col-md-5 col-sm-12">
							<div id="penas">
								<div class="row">									
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">										
										<label for="noPenalty"> Penas/ Deducciones: </label>
									</div>&nbsp;&nbsp;&nbsp;&nbsp;
									<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
										<select id="noPenalty" name="noPenalty" onchange="readInfoPenas();" class="form-select form-select-sm"></select>
									</div>								
								</div>
							</div>
						</div>
					</div>
				</div>	
											
				<div class="row">					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">						
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
						<input type="button" value="Cargar Layout Boletos" onclick="abrirDlgFoliosVuelos();" id="btnLayoutBoletos" name="btnLayoutBoletos" title="Carga Layout Boletos de Vuelos" class="btn btn-secondary btn-sm"/>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" value="Calcula Cedular" onclick="calculaCedular();" id="btnCalculaCedular" name="btnCalculaCedular" title="Calcula Cedular Nayarit" class="btn btn-secondary btn-sm"/>
					</div>
				</div>	
				
				<div class="row">				
    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>														
					<div class="col-12 col-lg-9 col-md-9 col-sm-12 p-1">	
						Observaciones <textarea cols="105" rows="2" name="cObservaciones" onkeypress="return validar(event)" class="form-control form-control-sm" id="cObservaciones"></textarea>								  							
					</div>
				</div>
	
			</div>
			
			



			<div id="multitabs" style="width: 120%">			
				<div id="tabs" style="width: 80%">
				
					<ul>
						<li><a id="L01" href="#tabs-1">Detalle de operaci&oacute;n</a></li>
						<li><a id="L02" href="#tabs-2">Retenciones</a></li>
						<li><a id="L03" href="#tabs-3" class="pasoDos">Devengado</a></li>
						<li><a id="L04" href="#tabs-4" class="pasoTres">Documentaci&oacute;n</a></li>
					</ul>
					<div id="tabs-1">
						<div id="operacion">
							<!--			<a href="javascript:void(0);" onclick="fnClickAddRowA();">Agregar</a>-->
							<select id="NumPagoAMF" name="NumPagoAMF" style="visibility:hidden">
							<option value="***" selected>- Selecciona AMF -</option>
							</select>
							<input type="text" name="nClaveAMF" id="nClaveAMF" style="visibility:hidden"/>
							<input type="text" name="numFolioAMF" id="numFolioAMF" style="visibility:hidden"/>
							<input type="text" name="PagoAMF" id="PagoAMF" style="visibility:hidden"/>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>	
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									&nbsp;
									<input type="text" name="cNoFactura" style="text-align: right" class="form-control form-control-sm paso01" id="cNoFactura" size="16" maxlength="10" style="visibility:hidden" title="Capture el Numero de Factura" />
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									 Amorizacion Anticipo
									 <div class="input-group">
									 	<input name="mAmortizacionAnticipo" style="text-align: right" type="text" class="form-control form-control-sm" id="mAmortizacionAnticipo" maxlength="10" size="10" value="0.0" readonly/>&nbsp;-
									 </div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									% Amortizacion <input type="text" name="nPorcAmortizacion" style="text-align: right" onChange="Amortizacion()" class="form-control form-control-sm" id="nPorcAmortizacion" maxlength="10" size="10" value="0.0" readonly/>
									<input type="hidden" name="nPorcAmortizacion2" id="nPorcAmortizacion2"/> 
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Anticipo <input type="text" name="saldoAnticipo" id="saldoAnticipo" maxlength="10" value="0.0" class="form-control form-control-sm" style="text-align: right" size="10" readonly/>								  							
								</div>
							</div>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>	
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Importe Facturas <input name="mImporteFacturas" type="text" style="text-align: right" class="form-control form-control-sm" id="mImporteFacturas" value="0.0" size="16" readonly>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									 Subtotal
									 <div class="input-group">
									 	<input name="subTotal_2" type="text" value="0.0" class="form-control form-control-sm" style="text-align: right" id="subTotal_2" size="10" readonly/>&nbsp;=
									 </div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Acumulado Amortizacion <input type="text" name="mAmortizacionAcumulado" style="text-align: right" id="mAmortizacionAcumulado" class="form-control form-control-sm" maxlength="10" size="10" value="0.0" readonly/> 
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Acumulado Devoluciones <input name="mImporteDevolucionAcumulado" type="text" value="0.0" onkeypress="return validar2(event)" class="form-control form-control-sm" maxlength="10" size="10" id="mImporteDevolucionAcumulado" style="text-align: right" readonly/>								  							
								</div>
							</div>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>	
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Importe Bruto 
									<div class="input-group">
										<input name="mImporteBruto" onkeypress="return validar2(event)" type="text" style="text-align: right" class="form-control form-control-sm" id="mImporteBruto" value="0.0" size="16" readonly/>&nbsp;-
									</div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									 Monto IVA
									 <div class="input-group">
									 	<input name="mImporteIVA" type="text" class="form-control form-control-sm" id="mImporteIVA" style="text-align: right" maxlength="10" size="10" value="0.0" readonly/>&nbsp;+
									 </div>
									 <input name="mImporteMasIva" type="hidden" class="subtotall" id="mImporteMasIva" value="0"/>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									% IVA <input name="nPorcIVAAplicable" type="text" class="form-control form-control-sm" readonly id="nPorcIVAAplicable" maxlength="10" style="text-align: right" size="10" value="0.0"/>								
								</div>
							</div>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>	
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Otros Impuestos <input type="text" name="mOtrosImpuestos" style="text-align: right" class="form-control form-control-sm paso01" id="mOtrosImpuestos"  size="16" readonly value="0">								
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									 Monto Retenciones
									 <div class="input-group">
									 	<input name="mImporteRetencion" type="text" class="form-control form-control-sm subtotall" readonly id="mImporteRetencion" maxlength="10" style="text-align: right" size="10" value="0.0"/>&nbsp;-
									 </div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Saldo Total Compromiso <input type="text" name="saldoCed" id="saldoCed" maxlength="10" style="text-align: right" class="form-control form-control-sm" value="0.0" size="10" readonly />
									<input type="hidden" name="saldoCed2" id="saldoCed2" maxlength="10" style="text-align: right" value="0" size="10" /> 								
								</div>
							</div>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>	
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Monto Sanciones
									<div class="input-group"> 
										<input name="mImporteSancion" onkeypress="return validar2(event)" type="text" class="form-control form-control-sm subtotall" value="0.0" id="mImporteSancion" style="text-align: right" maxlength="10" size="16" title="Disponible a Devolución"/>&nbsp;+
									</div>								
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									 Monto Penalizaciones
									 <div class="input-group">
									 	<input name="mImportePenalizacion" type="text" class="form-control form-control-sm subtotall" id="mImportePenalizacion" value="0.0" onkeypress="return validar2(event);" maxlength="10" style="text-align: right" size="10" title="No disponible a Devolución"/>&nbsp;-
									 </div>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Saldo del Contrato <input name="mTotal" onkeypress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="mTotal" value="0.0" maxlength="10" readonly size="10"> 								
								</div>
							</div>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>	
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Monto Devoluciones
									<div class="input-group"> 
										<input name="mImporteDevolucion" type="text" value="0.0" onkeypress="return validar2(event)" maxlength="10" size="16" class="form-control form-control-sm subtotall" id="mImporteDevolucion" style="text-align: right" title="Devolución de lo Sancionado"/>&nbsp;=
									</div>								
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									 Importe Neto
									 <div class="input-group">
									 	<input name="mImporteNeto" value="0.0" style="text-align: right" type="text" class="form-control form-control-sm subtotall" readonly id="mImporteNeto" size="10"/>&nbsp;=
									 </div>
									 <input name="mImporteNetoEP" value="0" type="hidden" readonly id="mImporteNetoEP" size="10"/>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Comprometido <input type="text" name="acumulado" id="acumulado" class="form-control form-control-sm" maxlength="10" style="text-align: right" value="0.0" size="10" readonly/> 								
								</div>
							</div>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>	
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Subtotal <input name="subTotal_1" value="0" type="text" style="text-align: right" onkeypress="return validar2(event)" class="form-control form-control-sm" id="subTotal_1" size="16" readonly/>								
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Importe a Ejercer <input name="mImpEjercer" onKeyPress="return validar2(event)" class="form-control form-control-sm" type="text" style="text-align: right" id="mImpEjercer" value="0.0" maxlength="10" size="10" readonly/> 
									<input type="hidden" name="mImporteAnticipoIVA" class="subtotall"	id="mImporteAnticipoIVA" value="0" />								 
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
									Acumulado Sanciones <input type="text" name="mImporteSancionAcumulado" id="mImporteSancionAcumulado" value="0.0" class="form-control form-control-sm" maxlength="10" style="text-align: right" size="10" readonly />
									<input name="mTotal2" type="hidden" style="text-align: right" id="mTotal2" value="0"/>  								
								</div>
							</div>
							
							<div class="row">				
			    				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
								</div>														
								<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">	
									Concepto <textarea cols="80" rows="8" name="Descrip_Concepto" onkeypress="return validar(event)" class="form-control form-control-sm paso01" id="Descrip_Concepto" title="Descripcion del pago" readonly></textarea>								  							
								</div>
							</div>							
						</div>					
					</div>

					<div id="tabs-2">
						<!--<a href="javascript:void(0);" onclick="fnClickAddRowC();">Agregar</a>-->
						<div id="Retenciones">
			    			<div class="row">							
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									<table align="center" id="tretencion" class="table table-striped table-bordered">								
										<thead>
											<tr>
												<th>Codigo</th>
												<th>Retencion</th>
												<th>Porcentaje</th>
											</tr>
										</thead>
										<tbody>
										</tbody>							
									</table>
								</div>
							</div>
						</div>						
					</div>
					
					<div id="tabs-3">						
						<div class="row">						    																	
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
								Tipo De Concepto <select id="TIPO_CONCEPTO" name="TIPO_CONCEPTO" onchange="concepto();" class="form-select form-select-sm"></select>
								<span id="verFondenSPAN" >&nbsp; <a href="#" onclick="verReferencia();return false;">Ver Proyecto FONDEN</a> </span>								  							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
								Tipo De Movimiento <select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" class="form-select form-select-sm"></select>								  							
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Importe a Ejercer <input type="text" id="importeEjercer" name="importeEjercer" value="0" style="text-align:right" size="16" class="form-control form-control-sm" readonly/>
							</div>
						</div>
						
						<div class="row">						    																	
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">	
								Almacén <select id="ALM" name="ALM" class="form-select form-select-sm"></select>								  							
							</div>		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input name="altaAlmacen" type="text" class="form-control form-control-sm" id="altaAlmacen" style="text-align: right;" onKeyPress="valFmt(this,9)" value="0"/>
							</div>					
						</div>
						
						<br/>
						
						<div class="row">						    																	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								<input type="text" id="cAnioFactEP" name="cAnioFactEP" onKeyPress="valFmt(this,9)" class="form-control form-control-sm paso04" size="4" maxlength="4" >								  							
							</div>		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<input type="text" id="nFacturaEP" name="nFacturaEP" style="text-transform:uppercase" class="form-control form-control-sm paso04" size="12" maxlength="12">
							</div>					
						</div>
						
						<div class="row">																						
							<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
								Estructura Programática												 							 					
							</div>															
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								Monto Devegnado																		 							 					
							</div>
						</div>
						
						<div class="row">																			
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">								
								<input type="text" id="ep" name="ep" size="61" readonly class="form-control form-control-sm"/> 								 																		 							 				
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
								<input name="button" type="button" id="nIdClaveEgresos2" onclick="Grid()" value="..." size="5" class="btn btn-secondary btn-sm"/> 																		 							 					
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
								<input type="text" name="montoDev" value='0' id="montoDev" onblur="cambioss();" maxlength="16" style="text-align:right" class="form-control form-control-sm" onkeypress="return validar2(event)"/> 																		 							 					
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
								<input type="text" name="saldoCompM" id="saldoCompM" value="0" maxlength="16" style="text-align:right" class="form-control form-control-sm" readonly /> 																		 							 					
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">	
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
								<input type="button" value="Agregar" onclick=" FiltroMovimientos();" id="agrega2" class="btn btn-secondary btn-sm" name="agrega2" title="Agrega EP" />																																				 							 				
							</div>	
						</div>		
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">		
								<table   class="display" id="grdCompromisos"  >
									<thead>
										<tr>
											<th nowrap>Clave Presupuestaria SIAF</th>
											<th nowrap>Cod. SIF</th>
											<th>Importe</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
									<tfoot>
									</tfoot>
								</table>
							</div>
						</div>
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">							
								<table cellpadding="0" cellspacing="0" style="visibility:hidden" border="0" class="display" id="grdRetClave"  >
									<thead>
										<tr>
											<th>EP</th>
											<th>Mes</th>
											<th>Importe</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
									<tfoot>
									</tfoot>
								</table>
							</div>
						</div>
					</div>

					<div id="tabs-4">
						<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								No. Folio <input name="DCD_FACTURA" type="text" id="DCD_FACTURA" onkeypress="return validar(event)" class="form-control form-control-sm paso03" readonly size="15"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Fecha de factura 
								<div class="input-group">								
									<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
									<input name="DCD_FECHA_FACTURA" type="text" id="DCD_FECHA_FACTURA" class="form-control form-control-sm paso03" maxlength="10" size="10" readonly/>
								</div>
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Tipo de beneficiario <input name="DCD_TBEN" type="text" class="form-control form-control-sm paso03" size="15" id="DCD_TBEN" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Clave de beneficiario <input name="DCD_CBEN" type="text" class="form-control form-control-sm paso03" size="15" id="DCD_CBEN" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	Tipo de operaci&oacute;n
							  	<select name="DCD_TIPO_OPE" id="DCD_TIPO_OPE" class="form-select form-select-sm paso03">
									<option value="85" class="paso03">85 OTROS</option>
								</select>							
							</div>
						</div>
						
						<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Porcentaje IVA <input name="DESCRIPCION20" type="text" class="form-control form-control-sm paso03" id="DESCRIPCION20" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Importe Total <input type="text" id="DCD_IMP_BRUTO" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_IMP_BRUTO" readonly/>								
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								IVA Desglose <input name="DCD_IVA" type="text" id="DCD_IVA" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								IVA Retención <input name="DCD_IVADES" type="text" id="DCD_IVADES" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	ISR <input name="DCD_ISR" type="text" id="DCD_ISR" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
							</div>
						</div>
						
						<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Ret. 0.5% <input name="DCD_MIL5" type="text" class="form-control form-control-sm paso03" id="DCD_MIL5" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Ret. 0.2% <input type="text" id="DCD_MIL2" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_MIL2" readonly/>								
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Beneficio Social <input name="DCD_CONTRIBUCION" type="text" id="DCD_CONTRIBUCION" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Impuesto Cedular <input name="DCD_OTRAS_RET" type="text" id="DCD_OTRAS_RET" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	Penalizaciones <input name="DCD_PENALIZACION" type="text" id="DCD_PENALIZACION" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
							</div>
						</div>
						
						<div class="row">						    				
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Sanción <input name="DCD_SANCION" type="text" class="form-control form-control-sm paso03" id="DCD_SANCION" size="15" onKeyPress="return validar2(event)" value="0" readonly/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Devolución <input type="text" id="DCD_DEVOL" class="form-control form-control-sm paso03" size="15" onkeypress="return validar2(event)" name="DCD_DEVOL" readonly/>								
							</div>							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								Amort. Anticipo <input name="DCD_AMORT" type="text" id="DCD_AMORT" size="15" value="0" class="form-control form-control-sm paso03" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								Total Retención <input name="DCD_RETENCION" type="text" id="DCD_RETENCION" size="15" class="form-control form-control-sm paso03" value="0" onKeyPress="return validar2(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
							  	Neto <input name="DCD_NETO" type="text" id="DCD_NETO" size="15" value="0" class="form-control form-control-sm paso03" onkeypress="return validar2(event)" readonly/>							
							</div>
						</div>
						
						<br/>
						
						<div class="row">						    		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
							</div>		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
								<input type="button" id="Agregar1" name="Agregar1" value="Agregar" onclick="fnClickAddRowComp();" class="btn-secondary btn-sm"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<div class="input-group" id="divImprime" >
									<img src="imagenes/Imprimir.png" width="33" height="28" onClick="cmdImprimir('ContrareciboN');">Contrarecibo
								</div>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12" style="visibility:hidden">	
								<span id="justificacion"><a href="#" onclick="dlgJustificacion(); return false;"> Carga Justificacion </a></span>								
							</div>								
						</div>
						
						<div class="row">												
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">	
								Concepto <textarea cols="50" rows="3" name="DCD_CONCEPTO" onkeypress="return validar(event)" class="form-control form-control-sm paso03" id="DCD_CONCEPTO" ></textarea>								  							
							</div>
						</div>
						
						<div class="rowr">							
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">	
								<table   class="display" id="grdFacturas">
									<thead>
										<tr>
											<th nowrap>&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">Descuentos</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
											<th align="left">&nbsp;</th>
										</tr>
										<tr>
											<th nowrap>No. Factura</th>
											<th align="left">Fecha Factura</th>
											<th align="left">Tipo beneficiario</th>
											<th align="left">Cve Benef</th>
											<th align="left">Tipo oper</th>
											<th align="left">%IVA</th>
											<th align="left">Importe Total</th>
											<th align="left">IVA desglose</th>
											<th align="left">IVA</th>
											<th align="left">ISR</th>
											<th align="left">Ret. 0.5</th>
											<th align="left">Ret 0.2</th>
											<th align="left">Beneficio social</th>
											<th align="left">Impuesto cedular</th>
											<th align="left">Penalizaciones</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
									<tfoot>
									</tfoot>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div id="dialog-form" title="Aplicación Presupuestal/Contable">
				<div id="divEspera" align="center">
					Espere por favor....
					<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
				<div id="divAplica">
					<iframe id="ifAplica" src="about:blank"></iframe>
				</div>
			</div>
			<div id="dialog-Procesando" title="Procesando">
				<div id="divEsperaProcesando" style="visibility: hidden" align="center">
					Espere por favor....
					<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
			
<div class="modal" tabindex="-1" role="dialog" id="dialog-validaOLI" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
	      <div class="modal-header">	
	      	<h6 class="modal-title">Digite el Número de OLI a Validar</h6>		
	      	<button type="button" class="btn-close" data-bs-dismiss="modal" onclick="cerrarOli();" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
	      	<input style="text-align: right;" name="cOLI" type="text" id="cOLI" value="" size="20" maxlength="15" />
	   	</div>
	 </div>
     <div class="modal-footer">
        <button type="button" id="btnAceptarOli" onclick="aceptarOli();" class="btn btn-primary">Aceptar</button>
        <button type="button" class="btn btn-secondary" onclick="cerrarOli();"  data-bs-dismiss="modal">Cerrar</button>
     </div>
  </div>
</div>

<div class="modal" tabindex="-1" role="dialog" id="dialog-validaFact" data-mdb-keyboard="true" data-mdb-backdrop="static">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
	      <div class="modal-header">	
	      	<h6 class="modal-title">Validación de Facturas</h6>		
	      	<button type="button" class="btn-close" data-bs-dismiss="modal" onclick="cerrarFacturas();" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
				<div id="uploadFacturasDiv">
					<iframe id="uploadFacturasFrm" src="UploadFacturas.jsp?tipo_pago=PAGODIVERSO" align="top" frameborder="0" height="360" width="600"> </iframe>
				</div>
				<div id="facturasCapturadasDiv">
					<div class="row">										
						<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
						</div>		
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<a href="#" onclick="togleDivFacts(1);return false;">Cargar Facturas</a>								  							
						</div>
					</div>
					<h5> Facturas Cargadas </h5>
					<hr class="mt-3">
					<div class="rowr">							
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">	
							<table id="grdValidaFacturas">
								<thead>
									<tr>
										<th>Serie</th>
										<th>Factura</th>
										<th>Importe Bruto</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
					
					<div class="rowr">							
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							Total de las facturas:&nbsp;&nbsp;&nbsp;
							<input type="text" style="text-align: right;" name="mTotalFacturaV" id="mTotalFacturaV" value="0.00" size="15" maxlength="15" class="form-control form-control-sm" readonly />
						</div
					</div>										
				</div>
		   </div>
	  </div>
      <div class="modal-footer">
        <button type="button" id="btnAceptarFacturas" onclick="aceptarFacturas(); " class="btn btn-primary">Aceptar</button>
        <button type="button" class="btn btn-secondary" onclick="cerrarFacturas();"  data-bs-dismiss="modal">Cerrar</button>
      </div>
  </div>
</div>
			
			<div id="dialog-Justificacion" title="Ajuntar Justificacion">
				<div id="uploadJustificacion">
					<iframe id="UploadJustificacionIva6" src="UploadJustificacionIva6.jsp?tipo_pago=PAGODIVERSO" align="top" frameborder="0" height="360" width="510"> </iframe>
				</div>
			</div>
			
			
		
			<div id="dlgSeleccionaFonden">
				<fieldset>
					<legend> Seleccione el proyecto de FONDEN </legend>
					<table>
						<tr>
							<td align="right">Proyecto:</td>
							<td>
								<div id="selproyectoFondenDiv">
									<select id="proyectoFonden" name="proyectoFonden">
									</select>
								</div>
								<div id="verProyectoFondenDiv">
									<input type="text" id="proyectoFondenRead" name="proyectoFondenRead" size="24" readonly="readonly" />
								</div>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			
			
 		</form>
	</div>	

	<div class="modal" tabindex="-1" role="dialog" id="dialog-opinion" data-mdb-keyboard="true" data-mdb-backdrop="static">
		<div class="modal-dialog modal-lg" role="document">
	    	<div class="modal-content">
				<div class="modal-header">	
		      		<h6 class="modal-title">Valida Opinion de Cumplimiento</h6>		
		      		<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      	</div>
				
				<div class="modal-body">
					<div class="container-fluid">
						<div class="row mb-3">
							<div class="col-12">
								<label for="pdfOpinionCumplimiento32D" class="form-label">PDF con la opinion de cumplimiento (32D)</label>
								<input type="file" class="form-control" id="pdfOpinionCumplimiento32D" name="pdfOpinionCumplimiento32D" accept="application/pdf"/>
							</div>
						</div>
						<div class="row">
							<div class="col-12 text-end">
								<button type="button" class="btn btn-primary" id="btnValidarOpinion" name="btnValidarOpinion" onclick="validarOpinionCumplimiento(); return false;">Validar Archivo</button>
							</div>
						</div>
					</div>
				</div>
				
			</div>
		</div>
	</div>

	<!-- DIV Para capturar correo -->
	<div class="modal" tabindex="-1" role="dialog" id="dialog-actualizaCorreo" data-mdb-keyboard="true" data-mdb-backdrop="static">
	  <div class="modal-dialog modal-lg" role="document">
	    <div class="modal-content">
		      <div class="modal-header">	
		      	<h6 class="modal-title">Actualiza Correo</h6>		
		      	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      <div class="modal-body">
					<h6>Favor de capturar los siguientes datos para seguimiento del PPD:</h6>
						<div class="row">
							<div class="col-12">
								<label for="nombreCorreo">Nombre</label>
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-person"></i></span>
									<input type="text" id="nombreCorreo" name="nombreCorreo" placeholder="Nombre" class="form-control"/>
									<input type="text" id="aPCorreo" name="aPCorreo" placeholder="Apellido Paterno" class="form-control"/>
									<input type="text" id="aMCorreo" name="aMCorreo" placeholder="Apellido Materno" class="form-control"/>
									
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<label for="cargo">Cargo</label>
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
									<input type="text" id="cargo" name="cargo" class="form-control" placeholder="Cargo"/>
								</div>	
							</div>
						</div>
						<div class="row">
							<div class="col-12">
								<label class="sr-only" for="correoActual">Correo</label>
								<div class="input-group">
						        	<div class="input-group-prepend">
						          		<div class="input-group-text">@</div>
						        	</div>
						        	<input type="text" id="correoActual" name="correoActual" placeholder="Correo electrónico" class="form-control"/>
						      	</div>
							</div>		
					  </div>	      	
			  </div>
			  <div class="modal-footer">
			        <button type="button" id="btnAceptarCorreo" onclick="validarCorreo();" class="btn btn-primary">Aceptar</button>
			        <button type="button" class="btn btn-secondary"  data-bs-dismiss="modal">Cerrar</button>
			  </div>
		</div>
	  </div>
	</div>
			
		<!-- Dialogo Firmantes -->
		<jsp:include page="Firmantes.jsp"></jsp:include>
			
</body>
</html>

