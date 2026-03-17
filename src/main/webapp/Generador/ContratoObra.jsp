<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%String Control[] = {"EjercicioFiscal","RamoEP",
			"UnidadResponsableEP",
			"GrupoFuncional","Funcion","SubFuncion","ProgramaGeneral","ActividadInstitucional",
			"ProgramaPresupuestario","Partida",
			"TipoGasto","FuenteFinanciamiento","EntidadFederativa","Cartera","UnidadNormativa",
			"cUnidadEjecutora"
			};%>
<%
String DATE_FORMAT = "yyyy-MM-dd";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today= sdf.format(c1.getTime());
String cCentroContable="";
String cUR = "";
String cRamo = "";
boolean bAplicadoCont=false;

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
	if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
		bAplicadoCont = true;
}

String mensaje="";
if(request.getParameter("msg")!=null&&!"".equals(request.getParameter("msg"))){
	mensaje=request.getParameter("msg");
	mensaje=mensaje.replace("[","");
	mensaje=mensaje.replace("]","");
	mensaje=mensaje.replace(",","<br>");
}

if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
    cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

cUR = usuario.getU_UR();

int id_oper = -1;
if(request.getParameter("id_oper")!=null)
	id_oper= new Integer(request.getParameter("id_oper")).intValue();
else
	id_oper=c.getCasoOperacion(0).getIdOperacion();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Contrato de Obra</title>


		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "css/demo_page.css";
			@import "css/demo_table_jui.css";
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>

		<style media="all" type="text/css">     
			.alignRight { text-align: right; } 
			.alignCenter { text-align: center; }
		</style> 
			
		<script type="text/javascript" src="../js/datepickercontrol.js"></script>
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/validaciones.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" charset="utf-8">		
			
	var rowCount = 0 ;
	var nSesion = 0;
	var bCarga = false;
	var bAgregar = false;
	var bClicBtn = false;
	
		$(document).ready(function()
		{
			$('.currency').blur(function()
			{
				$('.currency').formatCurrency();
			});
		});
		
$(document).ready(function() {
			
	$.fn.dataTableExt.oApi.fnReloadAjax = function(oSettings, sNewSource) {
		if (typeof sNewSource != 'undefined')
			oSettings.sAjaxSource = sNewSource;
	
		this.fnClearTable(this);
		this.oApi._fnProcessingDisplay(oSettings, true);
		var that = this;

		$.getJSON(oSettings.sAjaxSource, null, function(json) {
		/* Got the data - add it to the table */
			for ( var i = 0; i < json.aaData.length; i++) {
				that.oApi._fnAddData(oSettings, json.aaData[i]);
			}

			oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
			that.fnDraw(that);
			that.oApi._fnProcessingDisplay(oSettings, false);
		});
	};

	$('#cObjetoContrato').bind('copy paste', function (e) {       
		e.preventDefault();
    });


			
		$( "#dialog-Cesion" ).dialog({
				autoOpen: false,
				height: 200,
				width: 750,
				modal: true,
				beforeClose: function( event, ui ) {
					return bClicBtn;			
				},
				buttons: {
					"Aceptar": function() {
						if ( $("#cIDRFCSesion").val() == "" ){
							alert("Debe capturar el RFC al cual cederán derechos");
							return;
						}
				
						if ( $("#nFolioJur").val() == "" ){
							alert("Debe capturar el Folio Jurídico de cesión de derechos");
							return;
						}
						
						if ( $("#fSesion").val() == "" ){
							alert("Debe capturar la Fecha de cesión de derechos");
							return;
						}
						
						if( <%=id_oper%> == 2 && bAgregar){
							
							if (!confirm("Confirme la Cesión de derechos al RFC")){
								return;
							}
						
							$("#cIdContrato1").val($("#cIdContrato").val());
							$("#cIDRFCSesion1").val($("#cIDRFCSesion").val());
							$("#fSesion1").val($("#fSesion").val());
							$("#nFolioJur1").val($("#nFolioJur").val());
							queryFormPost("ContratoSesionCreate2", {async: false });
							oTableMov.fnReloadAjax();
						}else{
							fnClickAddRowS();	
						}
						
						
						bClicBtn = true;
						$( this ).dialog( "close" );
					},
					"Cancelar": function() {
						bClicBtn = true;
						$( this ).dialog( "close" );
					}
				},
				close: function() {
										
				}
			});

			
			
					$("#tabs").tabs(
									{
									"show" : function(event, ui) {
											var oTable = $(
											'div.dataTables_scrollBody>table.display',
											ui.panel).dataTable();
											if (oTable.length > 0) {
												oTable.fnAdjustColumnSizing();
											}
										}
									});

					$('table.display').dataTable( {
						"sScrollY" : "200px",
						"bScrollCollapse" : true,
						"bPaginate" : false,
						"bJQueryUI" : true,
						"aoColumnDefs" : [ {
							"sWidth" : "10%",
							"aTargets" : [ -1 ]
						} ]
					});

					$('#datosfec').dataTable( {
						"bPaginate" : false,
						"bLengthChange" : false,
						"bFilter" : false,
						"bSort" : false,
						"bInfo" : false,
						"bAutoWidth" : false,
						"sScrollY" : 30,
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType" : "full_numbers"
					});

					$('#dt_anticipos').dataTable( {
						"bPaginate" : false,
						"bLengthChange" : false,
						"bFilter" : false,
						"bSort" : false,
						"bInfo" : false,
						"bAutoWidth" : false,
						"sScrollY" : 100,
						"bScrollCollapse": true,
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType" : "full_numbers"
					});

					$('#dt_retencion').dataTable( {
						"bPaginate" : false,
						"bLengthChange" : false,
						"bFilter" : false,
						"bSort" : false,
						"bInfo" : false,
						"bAutoWidth" : false,
						"sScrollY" : 100,
						"bScrollCollapse": true,
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType" : "full_numbers"
					});

					$('#dt_sesion').dataTable( 
					{
						"iDisplayLength": 20,
						"sScrollY": 100,
						"bPaginate": false,
	        			"bLengthChange": false,
						"bScrollCollapse": true,
						"bFilter": false,
	        			"bSort": false,
	        			"bInfo": false,
	        			"bAutoWidth": false,
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers"
					});

					$('#dt_clavepresup').dataTable( {
						"bPaginate" : false,
						"bLengthChange" : true,
						"bFilter" : false,
						"bSort" : false,
						"bInfo" : false,
						"bAutoWidth" : false,
						"sScrollY" : 100,
//						"sScrollX" : "100%",
						"sScrollXInner" : "100%",
						"bScrollCollapse" : true,
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType" : "full_numbers"

					});
					
		querySelectPost("CatalogoEsquemaPrecioRead","id_esquema", {async: false });
		querySelectPost("CatalogoTipoContratoObraRead","cIdTipoContratoObra", {async: false });
		querySelectPost("CatalogoClaseContratoObraRead","cIdClaseContratoObra", {async: false });
		querySelectPost("CatalogoTipoAdjudicacionRead","cIdTipoAdjudicacion", {async: false });
		querySelectPost("TipoFondoRead","cIdTipoFondo", {async: false });
		querySelectPost("PorcIVARead","nPorcIVAAplicable", {async: false });
		querySelectPost("CatalogoTipoAnticipoObraRead","cIdTipoAnticipoObra", {async: false });
		querySelectPost("CatalogoTipoRetencionRead","cIdTipoRetencion", {async: false });
		querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
		queryFormPost("cEjercicioRead", {async: false });
		document.getElementById('mImporteAnticipo').style.visibility = 'hidden';
		document.getElementById('mImporteAnticipoIVA').style.visibility = 'hidden';
		document.getElementById('mTotalAnticipo').style.visibility = 'hidden';
		document.getElementById('mAmortizado').style.visibility = 'hidden';
		document.getElementById('LabelBruto').style.visibility = 'hidden';
		document.getElementById('LabelIVAAnt').style.visibility = 'hidden';
		document.getElementById('LabelTotalAnt').style.visibility = 'hidden';
		document.getElementById('LabelAmortiza').style.visibility = 'hidden';
			var Caso = <%=request.getParameter("folio")%>;
     		$("#folio").val(Caso);
			queryFormPost("BuscaContratoObraPrincipalRead", {async: false });
			queryFormPost("RFCObraDivRead", {async: false });
			
			var Pp = $('#Partida').val();
			var Cont1 = $('#cIdContrato').val();
				
			if ($('#cIdContrato').val() == '') {
				$("#Delete").attr("disabled", true);
			}else{
				hello();
			} 
     				 
		var decimal1 = 0;
		var decimal2 = 0;
		var decimal3 = 0;
		var decimal4 = 0;
		var decimal5 = 0;
		var decimal6 = 0;
		var decimal7 = 0;
		var decimal8 = 0;

		$("#mObra").change(
				function() {
					$("#mImporte").val($(this).val());
					$("#mEjercicio").val($(this).val());
					$("#mContratoME").val();
					$("#nPorcIVAAplicable").change();
					decimal1=(parseFloat($("#mIVA").val()) + parseFloat($("#mImporte").val()));
					$("#mTotal").val(decimal1.toFixed(2));
					$("#mEjercicio").val($("#mTotal").val());
					$("#mTotal").formatCurrency();
					$("#mEjercicio").formatCurrency();
					$("#nPorcAsignacion").change();
				});

		$( "#pbCesionD" )
			.button()
			.click(function() {
				$('.cesion').attr("disabled", false);
				document.getElementById("nFolioJur").removeAttribute( "readonly", false);								
				$("#pbInactiva").css("visibility","hidden");
				
   				$("#cIDRFCSesion").val( "" );
				$("#NombreSesion").val( "" );
				$("#nFolioJur").val( "" );
				$("#fSesion").val( "" );
				bAgregar = true;
				bClicBtn = false;
				$( "#dialog-Cesion" ).dialog( "open" );
			});
		
		$( "#pbInactiva" )
			.button()
			.click(function() {
				
				$("#cIdContrato1").val($("#cIdContrato").val());
				$("#cIDRFCSesion1").val($("#cIDRFCSesion").val());
				$("#fSesion1").val($("#fSesion").val());
				$("#nFolioJur1").val($("#nFolioJur").val());
				
				queryFormPost("CambioActivoPContratoSesionUpdate", {async : false});
				
				alert("DesActivado");
			});
		

					
					$("#nPorcIVAAplicable").change(
									function() {
									decimal2=($("#mImporte").val()* $("#nPorcIVAAplicable").val());
										$("#mIVA").val(decimal2.toFixed(2));
									decimal3=(parseFloat($("#mImporte").val())* (1 + parseFloat($("#nPorcIVAAplicable").val())));
										$("#mTotal").val(decimal3.toFixed(2));
										$("#mEjercicio").val($("#mTotal").val());
										$("#mTotal").formatCurrency();
										$("#mEjercicio").formatCurrency();
										$("#nPorcAsignacion").change();
									});

					$("#mImporteAnticipo").change(
									function() {
									decimal4=(Quitafrmt( $(this).val() ) *  $("#nPorcIVAAplicable").val());
										$("#mImporteAnticipoIVA").val(decimal4.toFixed(2));
									decimal5=(parseFloat($(this).val())* (1 + parseFloat($("#nPorcIVAAplicable").val())));
										$("#mTotalAnticipo").val(decimal5.toFixed(2)); 
									});

					$("#nPorcAsignacion").change(
						function() {
							decimal6 = Quitafrmt( $("#mObra").val() );
							decimal6 = ( decimal6 * ($(this).val() * .01));
							$("#mImporteAnticipo").val(decimal6.toFixed(2));
							decimal7 = (decimal6 *  $("#nPorcIVAAplicable").val());	
							$("#mImporteAnticipoIVA").val(decimal7.toFixed(2));
							decimal8=(parseFloat(decimal6 * (1 + parseFloat($("#nPorcIVAAplicable").val()))));
							$("#mTotalAnticipo").val(decimal8.toFixed(2));
							$("#mImporteAnticipo").formatCurrency(function() {});
							$("#mImporteAnticipoIVA").formatCurrency(function() {});
							$("#mTotalAnticipo").formatCurrency(function() {});
							$("#mAmortizado").val("0");
							
							if ($(this).val() != "0"){
								document.getElementById('mImporteAnticipo').style.visibility = 'visible';
		  						document.getElementById('mImporteAnticipoIVA').style.visibility = 'visible';
		  						document.getElementById('mTotalAnticipo').style.visibility = 'visible';
		  						document.getElementById('mAmortizado').style.visibility = 'visible';
		  						document.getElementById('LabelBruto').style.visibility = 'visible';
		  						document.getElementById('LabelIVAAnt').style.visibility = 'visible';
		  						document.getElementById('LabelTotalAnt').style.visibility = 'visible';
		  						document.getElementById('LabelAmortiza').style.visibility = 'visible';
							}else {
								document.getElementById('mImporteAnticipo').style.visibility = 'hidden';
		  						document.getElementById('mImporteAnticipoIVA').style.visibility = 'hidden';
		  						document.getElementById('mTotalAnticipo').style.visibility = 'hidden';
		  						document.getElementById('mAmortizado').style.visibility = 'hidden';
		  						document.getElementById('LabelBruto').style.visibility = 'hidden';
		  						document.getElementById('LabelIVAAnt').style.visibility = 'hidden';
		  						document.getElementById('LabelTotalAnt').style.visibility = 'hidden';
		  						document.getElementById('LabelAmortiza').style.visibility = 'hidden';
								$("#nPorcAmortizacion").val("0");								
							}
							
					});

					$("#mAmortizado").change(
						function() {
							decimal6 = parseFloat( Quitafrmt( $(this).val() ) );
							decimal8 = parseFloat( Quitafrmt( $("#mTotalAnticipo").val() ) );
							if ( decimal6 > decimal8) {
								alert("No se puede exceder el total del anticipo")
								$(this).val("0");
							}
							//$(this).formatCurrency(function() {});
					});
				
 				$("#dt_sesion tbody").click(function(event) {
  					$(oTableMov.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTableMov.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTableMov.fnGetData( aPos[0] );
 					});
  					$(event.target.parentNode).addClass('row_selected');
 				});
				

 				$("#dt_sesion tbody").dblclick(function(event) {
 					$(oTableMov.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTableMov.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTableMov.fnGetData( aPos[0] );

 					});
  					$(event.target.parentNode).addClass('row_selected');
     				var aPos = oTableMov.fnGetPosition( event.target.parentNode ); 
     				//event.target.parentNode.parentNode.rowIndex -1 ;
     				var aData = oTableMov.fnGetData( aPos );
					if (bCarga){
						bAgregar = false;
	     				$("#cIDRFCSesion").val( aData[ 0 ]);
						$("#NombreSesion").val( aData[ 1 ]);
						$("#nFolioJur").val( aData[ 2 ]);
						$("#nFolioJur1").val( aData[ 2 ]);
						$("#fSesion").val( aData[ 3 ]);
						$("#pbInactiva").css("visibility","visible");
						//$('#nFolioJur').removeAttr("readonly" );
						bClicBtn = false;
						$( "#dialog-Cesion" ).dialog( "open" );
						oTableMov.fnDraw();
						return;
					}else{
						$("#pbInactiva").css("visibility","hidden");	
					}

     				oTableMov.fnDeleteRow( aPos);
					
					oTableMov.fnDraw();
					nSesion--;					

 				});

				oTableMov = $('#dt_sesion').dataTable( );
				

					$(document).ready(function() {
						$("input.AyudaSyC").subIniciaDlg();
						$("input.autoCompletaSyC").subIniciaAutoCompleta();
					});

				});

function foco(elemento) {
 elemento.style.border = "1px solid #FF0000";
 }

 function no_foco(elemento) {
 elemento.style.border = "1px solid #CCCCCC";
 }
 
function Guardar() {
		if (Number($("#mAmortizado").val()) > 0 || $("#nPorcAsignacion").val()=='0' || $("#nPorcAsignacion").val()=='' || $("#nPorcAsignacion").val()=='0.0' || $("#nPorcAsignacion").val()=='0.00'){
			document.FormContrato.LHaySaldoAnticipo.value = 0;
		}else{
			document.FormContrato.LHaySaldoAnticipo.value = 1;
		}
		if 	(rowCount < 2) {
			alert("Por favor agregue al menos una Clave Presupuestal");
			return false;
		}

		if ($('#cIdClaseContratoObra').val() == '0'){
			alert("Seleccione la Clase de Contrato");
			return false;
		}

		if ($('#id_esquema').val() == '0'){
			alert("Seleccione el Esquema de Precio");
			return false;
		}

        		
		if ($('#cIdTipoRetencion').val()!=''){
			Generales();
			if ( nSesion > 0 ) {
				queryFormPost("ContratoObraAnticipoCreate,ContratoEPCreate,ContratoSesionCreate", {async: false });
			}else{
				queryFormPost("ContratoObraAnticipoCreate,ContratoEPCreate", {async: false });
			}

			alert("Contrato Obra Guardado!");
			return true;
			//document.location.reload();
			}
		else{
			$('#cIdTipoRetencion').val(1)
			$('#dt_retencion').dataTable().fnAddData( [
				'<input type="hidden" name="cEjercicioTbl2" value="'+$('#cEjercicio').val()+'">' +
				'<input type="hidden" name="cIdEntidadContableTbl2" value="'+$('#cIdEntidadContable').val()+'">' +
				'<input type="hidden" name="cIdContratoTbl2" value="'+$('#cIdContrato').val()+'">',
				] );
				Generales();
			if ( nSesion > 0 ) {
				queryFormPost("ContratoObraAnticipoCreate,ContratoEPCreate,ContratoSesionCreate", {async: false });
			}else{
				queryFormPost("ContratoObraAnticipoCreate,ContratoEPCreate", {async: false });
			}
				//queryFormPost("ContratoObraAnticipoCreate,ContratoEPCreate,ContratoSesionCreate", {async: false });
				alert("Contrato Obra Guardado!");
				return true;
		}			
	}



activo=1
function Generales() {
			if(activo==1){ //Aquí comprobamos si se debe o no ejecutar el código
			if(document.FormContrato.lEsPlurianual.checked) {
	       	document.FormContrato.lEsPlurianual.value = '1';
	      }
	    	else {
	      	document.FormContrato.lEsPlurianual.value = '0';
	      	document.FormContrato.lEsPlurianual.checked = true;
	      }
	      if (Number($("#mAmortizado").val()) > 0 || Number($("#nPorcAsignacion").val()) == 0){
			document.FormContrato.LHaySaldoAnticipo.value = 0;
			}
			else{
			document.FormContrato.LHaySaldoAnticipo.value = 1;
			}

			queryFormPost("ContratoObraCreate", {async: false });
			activo=0; //Desabilitamos el código para la próxima vez
		}			
}

	//aki
	 	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
		try{
			//validaciones de la forma
			//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay

			//Guardado de los campos correspondientes a cada variable de caso
			p.gestion.setFolio( "<%=c.getFolio()%>" );
			p.gestion.setOperador( $("#OPERADOR").val() );
			p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
			p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
			p.gestion.setConceptoMov("Aplicación de Contrato Obra");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
//			p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			p.gestion.setAplicadoCont("false");
			
			 valida_campos = Guardar();
			
		}
		catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
  	}


function onLoadPlantilla(){

		if(<%=id_oper==1%>){
			parent.document.getElementById("pb_send").disabled = false;
 			parent.document.getElementById("pb_send").style.visibility='hidden';
 		}
		//parent.document.getElementById("pb_send").value='Aplicar';
		
		if(<%=id_oper==2%>){
			parent.document.getElementById("pb_cancel").disabled=true;
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
		}
		//parent.document.getElementById("pb_save").disabled=true;
		//$("#divMilla2").hide();

}

function ResponsableSiguiente(id_oper){
	//alert("entro R");
	return "CONSULTA_CONTRATOOBRA";
}

function OperacionSiguiente(id_oper){

	//alert("entro O");
	return "consulta_contrato";
}

function onPostDisplay( id_oper ){

	parent.document.getElementById("pb_send").click();
}

function onPostSubmit( id_oper ){

	return true;
}

function fn_actret(pcontrol, pctrlret) {
	var valor = $('#' + pcontrol).val();
	$('#' + pctrlret).val( valor.substring(2) * 100 + "%" ) ;
	if ( $("#cIdTipoRetencion").val() == 2 ) {
		$("#divMilla2").show();
	}
	else {
		$("#divMilla2").hide();
	}
	
}

//			function fnClickAddRow() {
//
//				var rowCount2 = $('#dt_retencion tr').length;
//				var ctrlname = 'cIdTipoRetencion' + rowCount2;
//
//				$('#dt_retencion').dataTable().fnAddData( [
//					'<input type="hidden" name="cEjercicioTbl2" value="'+$('#cEjercicio').val()+'">' +
//					'<input type="hidden" name="cIdEntidadContableTbl2" value="'+$('#cIdEntidadContable').val()+'">' +
//					'<input type="hidden" name="cIdContratoTbl2" value="'+$('#cIdContrato').val()+'">' +
//					'<td><select onchange="' + "fn_actret(\'" + ctrlname + "\', \'nPorcRetencion" + rowCount2 + '\')" name="cIdTipoRetencion" id="' + ctrlname + '"><option selected value=""></option></select></td>',
//					'<td><input type="text" maxlength="5" size="5" name="nPorcRetencion" id="nPorcRetencion' + rowCount2 + '"></td>',
//					
//					'</td> ',
//					] );
//				querySelectPost("CatalogoTipoRetencionRead", ctrlname, {async: false });
//				
//			}

function fnAgregarRet() {
	if ($('#cIdClaseContratoObra').val() == '0'){
		alert("Seleccione la Clase de Contrato");
		return false;
	}

	if ($('#id_esquema').val() == '0'){
		alert("Seleccione el Esquema de Precio");
		return false;
	}


	if ($("#cIdContrato").val()==''){
		alert("Por favor ingrese el Numero de Documento");
		foco(document.getElementById('cIdContrato'));
		return false;
	}

	
	if ( $("#cIdTipoRetencion").val() == "2"){
		if ($("#CamInst").val() == "") {
			alert("Selecccione Camara o Instituto para continuar");
			return false;
		}
	}
	
		if ($("#cIDRFC").val()==''){
			alert("Por favor ingrese un R.F.C.");
			foco(document.getElementById('cIDRFC'));
			foco(document.getElementById('cnombre'));
			return false;
			}
		if ($("#fAdjudicacion").val()==''){
			alert("Por favor ingrese La Fecha de Adjudicacion");
			foco(document.getElementById('fAdjudicacion'));
			return false;
			}
		if ($("#fFirmaContrato").val()==''){
			alert("Por favor ingrese La Fecha de Firma de Contrato");
			foco(document.getElementById('fFirmaContrato'));
			return false;
			}
		if ($("#fInicio").val()==''){
			alert("Por favor ingrese La Fecha de Inicio");
			foco(document.getElementById('fInicio'));
			return false;
			}
		if ($("#fTermino").val()==''){
			alert("Por favor ingrese La Fecha de Termino");
			foco(document.getElementById('fTermino'));
			return false;
			}
		if ($("#mObra").val()=='' || $("#mObra").val()=='0' || $("#mObra").val()=='0.00' || $("#mObra").val()=='$0.00'){
			alert("Por favor Ingrese un Monto de Contrato");
			foco(document.getElementById('mObra'));
			return false;
			}
		if ($("#nPorcAsignacion").val()=='0' || $("#nPorcAsignacion").val()=='' || $("#nPorcAsignacion").val()=='0.0' || $("#nPorcAsignacion").val()=='0.00'){
			document.FormContrato.LHaySaldoAnticipo.value = 0;
			}
		else{
			document.FormContrato.LHaySaldoAnticipo.value = 1;
			}
	Generales();
	var rowCount2 = $('#dt_retencion tr').length;
	var ctrlname = 'cIdTipoRetencion' + rowCount2;					
	var table = document.getElementById('dt_retencion');
    var rowCount = table.rows.length;
    var yaExiste = 0;
	var nu2=0;
	var nombreRet=$("#cIdTipoRetencion").val();
    try{
	for(var i=0; i<rowCount; i++) {
        var row = table.rows[i];
        var chkbox = '';
		try
		{
		  var chkbox = row.cells[0].childNodes[0];
		  nombreRet = row.cells[1].childNodes[0];
		}
		catch(e) {
        	null;
        }
		if(null != chkbox ) {
			$("#cIdTipoRetencion").val(nombreRet.toString());
		}	
    }
    }
    catch(e) {
       alert(e);
    }
				if (yaExiste == 1){
					alert("Ya se ingresó una retención con esa clave");
					return;
				}

			queryFormPost("CatalogoTipoRetencionObtenDetRead", {async: false });
			queryFormPost("ContratoObraRetencionCreate", {async: false });
			var Cont1 = $('#cIdContrato').val();
			$('#dt_retencion').dataTable( {
						bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
					sScrollX: 100,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
				
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_CONSULTARETENCIONES_OBRA&qw=cIdContrato='" + Cont1 + "' AND cIdTipoRetencion<>1",
					aoColumns: [
						{ sName: "cTipoRetencion"},
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }]
                } ) ;


}


function hello() {
			bCarga = true;
			//queryFormPost("BuscaContratoObraPrincipalRead", {async: false });
			queryFormPost("fInicioObraRead", {async: false });
			queryFormPost("fTerminoObraRead", {async: false });
			queryFormPost("fAdjudicacionObraRead", {async: false });
			queryFormPost("fFirmaContratoObraRead", {async: false });
			queryFormPost("fAnticipoObraRead", {async: false });			
			queryFormPost("BuscaAnticipoObraRead", {async: false });
			document.getElementById('mImporteAnticipo').style.visibility = 'visible';
  			document.getElementById('mImporteAnticipoIVA').style.visibility = 'visible';
  			document.getElementById('mTotalAnticipo').style.visibility = 'visible';
  			document.getElementById('LabelBruto').style.visibility = 'visible';
  			document.getElementById('LabelIVAAnt').style.visibility = 'visible';
  			document.getElementById('LabelTotalAnt').style.visibility = 'visible';
			
			$("#mObra").formatCurrency(function() {});
			$("#mImporte").formatCurrency(function() {});
			$("#mEjercicio").formatCurrency(function() {});
			$("#mContratoME").formatCurrency(function() {});
			$("#mIVA").formatCurrency(function() {});
			$("#mTotal").formatCurrency(function() {});
			$("#mOAE").formatCurrency(function() {});
			$("#mImporteAnticipo").formatCurrency(function() {});
			$("#mImporteAnticipoIVA").formatCurrency(function() {});
			$("#mTotalAnticipo").formatCurrency(function() {});
								

			var Cont1 = $('#cIdContrato').val();
			var idCC = "' and cIdEntidadContable = '" + $("#cIdEntidadContable").val() + "'"; 
			
			$('#dt_clavepresup').dataTable( {
						bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
//					sScrollX: 100,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
				
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='" + Cont1 + idCC,
					aoColumns: [
//						{ sName: "EP"},
						{ sName: "ClaveSIAFF" },
						{ sName: "ClaveInterna" },
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }]
                } ) ;
			
			$('#dt_retencion').dataTable( {
						bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
					sScrollX: 100,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
				
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_CONSULTARETENCIONES_OBRA&qw=cIdContrato='" + Cont1 + "' AND cIdTipoRetencion<>1",
					aoColumns: [
						{ sName: "cTipoRetencion"},
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }]
                } ) ;

			
				$('#dt_sesion').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
					//sScrollX: 200,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoObraSesion&qw=cIdContrato='" + Cont1 + "'",
					aoColumns: [
						{ sName: "cIdRFCSesion"},
						{ sName: "NombreSesion"},
						{ sName: "cNumOficioJuridico"},
						{ sName: "fSesion"},
						{ sName: "bActivo"},
						{ sName: "Inputs",	bSearchable: false,	bSortable: false, bVisible: false  },
						{ sName: "nada",	bSearchable: false,	bSortable: false, bVisible: false  }]
                } ) ;
               
				$('#dt_convenio').dataTable( {
					bPaginate: false,
        			bLengthChange: false,
        			bFilter: false,
        			bInfo: false,
					bAutoWidth: false,
					//sScrollX: 200,
					sScrollY: 100,
					bScrollCollapse: true,
					bJQueryUI: true,
					bDestroy : true,
					bServerSide: true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoConvenio&qw=cIdContrato='" + Cont1 + idCC,
					aoColumns: [
						{ sName: "nConsecutivoModificacion"},
						{ sName: "mTotal", sClass: "alignRight"},
						{ sName: "ftermino", sClass: "alignCenter"}]
                } ) ;


			
               if((document.FormContrato.lEsPlurianual.value) == '1') {
	    	   	   document.FormContrato.lEsPlurianual.checked = true;
	    	   	   
	    		}else {
		       	   document.FormContrato.lEsPlurianual.checked = false;
		      	}
               
				$('.obligatorio').attr("readonly", true); 
				$('.obligatorio').css("background", "#f0f0f0"); 
				$('.obligatorio').attr("disabled", true);
				$("Select").attr("disabled", true);
				$("textarea").attr("disabled", true);
				if (<%=id_oper%>=="1"){
					
					parent.document.getElementById("pb_save").disabled = true;	
				}
				
//				document.getElementById("Actualizar").style.visibility='visible';
//				$("#Actualizar").attr("disabled", false);
//				$("#AgregarEP").attr("disabled", false);
			}
			
			function fnClickAddRowS() {
			
			$('#dt_sesion').dataTable().fnAddData( [
					$("#cIDRFCSesion").val(),
					$("#NombreSesion").val(), 
					$("#nFolioJur").val(),
					$("#fSesion").val(),
					'Activo',
					'<td><input type="hidden" name="cIDRFCTbl" id="cIDRFCTbl" value="' + $("#cIDRFCSesion").val() + '">'+
						'<input type="hidden" name="cEjercicioSesion" id="cEjercicioSesion" value="'+$('#cEjercicio').val()+'">' +
						'<input type="hidden" name="cIdEntidadContSes" id="cIdEntidadContSes" value="'+$('#cIdEntidadContable').val()+'">' +
						'<input type="hidden" name="cIdContratoSesion" id="cIdContratoSesion" value="'+$('#cIdContrato').val()+'">' +
						'<input type="hidden" name="cNumOficioJuridicoTbl" id="cNumOficioJuridicoTbl" value="'+$("#nFolioJur").val()+'">' +
						'<input type="hidden" name="fSesionTbl" id="fSesionTbl" value="'+$("#fSesion").val()+'">' +
						'<input type="hidden" name="bActivo" id="bActivo" value="1">' +
						'<input type="hidden" name="cTContratoTbl" id="cTContratoTbl" value="'+$('#cTContrato').val()+'"></td>', ''
				] );
				
				$("#cIDRFCSesion").val( "" );
				$("#fSesion").val( "" );
				$("#NombreSesion").val( "" );
				$("#nFolioJur").val( "" );
				nSesion++;
				
			}
			
			function fnClickAddRowC() {
				rowCount = $('#dt_clavepresup tr').length;
				var vep = $('#ep').val();
				if (vep == '') {
					return ;
				}
//				if ($('#mMovimiento').val() == '') {
//					return ;
//					}
				var oTable = $('#dt_clavepresup').dataTable();
				var nRows = $("#dt_clavepresup tr").length;
				nRows --;
				for ( var i=0 ; i<nRows ; i++ ) {
					var aTrs = oTable.fnGetNodes();
					var jqInputs = $('input', aTrs[i] );
					var vclavepre = jqInputs[ 0 ].value
					if (vclavepre == vep) {
						alert("La Clave Presupuestal ya se encuentra en la lista");
						return;
					}
				}
				
				var cint = vep.substring( vep.length - 8 );
				/*if ($('#ep').val() == $('#nIdClaveEgresos').val()) {
					alert ("Esa clave ya se encuentra Agregada");
						return ;
						}*/
				$('#dt_clavepresup').dataTable().fnAddData( [
//						'<td><input type="hidden" maxlength="5" size="5" name="nIdClaveEgresos" readonly id="nIdClaveEgresos" value="' + $('#ep').val() + '" style="background-color:#E0E0F8; text-align:center; border:1px solid #E0E0F8"></td>',
						'<td><input type="text" maxlength="25" size="65" name="ClaveSIAFF" 	readonly  id="ClaveSIAFF" value="' + vep + '" style="background-color:#E0E0F8; text-align:center; border:1px solid #E0E0F8"></td>',
						'<td><input type="text" maxlength="10" size="8" name="ClaveInterna" readonly id="ClaveInterna" value="' + cint + '" style="background-color:#E0E0F8; text-align:center; border:1px solid #E0E0F8">',
						'<td><input type="hidden" maxlength="5" size="5" name="nIdClaveEgresos" readonly id="nIdClaveEgresos" value="' + $('#ep').val() + '" style="background-color:#E0E0F8; text-align:center; border:1px solid #E0E0F8"></td>'+
						'<input type="hidden" name="cEjercicioTbl" value="'+$('#cEjercicio').val()+'">' +
						'<input type="hidden" name="cIdContratoTbl" value="'+$('#cIdContrato').val()+'">' +
						'<input type="hidden" name="cTContratoTbl" value="'+$('#cTContrato').val()+'">' +
						'<input type="hidden" name="cIdEntidadContableTbl" value="'+$('#cIdEntidadContable').val()+'">' +
						'</td>'
					] );
					
//				$('#nIdClaveEgresos2').val("");
				$('#ep').val("");
//				$('#mMovimiento').val("");
			}
			
		$(function() {
				$( "#fAdjudicacion" ).datepicker({
					dateFormat: "dd/mm/yy",
					showOn: "button",
					buttonImage: "images/calendar.gif",
					buttonImageOnly: true
				});
		});


		$(function() {
			$( "#fFirmaContrato" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
		$(function() {
			$( "#fInicio" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
		$(function() {
			$( "#fTermino" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});
		$(function() {
			$( "#fAnticipo" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});

		$(function() {
			$( "#fSesion" ).datepicker({
				dateFormat: "dd/mm/yy",
				showOn: "button",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});

function Sinfrmt( fld )
	{
	   var valcol = fld.value ;
	   	valcol = valcol.replace("$", "");
	   	valcol = valcol.replace(/,/g, "");
		$("#" + fld.id).val( valcol );
}

		function Quitafrmt( fld ) {
	   		fld = fld.replace("$", "");
	   		fld = fld.replace(/,/g, "");
			return fld;
		}

function cambiafrmt( fld )
	{
	    $("#" + fld.id).formatCurrency();
	}
function Grid()
	{
		window.open('MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGrid', 'status=1, width=900px, height=680px, left=100px');
	if ($.trim(document.FormContrato.ep.value)!=""){
			//window.alert($.trim(document.getElementById("ep").value));
			rellenaCampos();
		}
		//window.location.href="MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>";
	 	return false;
	}
				$( "#ep" )
				.change(function() {
				rellenaCampos();
				});
function rellenaCampos() {
//					querySelectPost("catalogoClaveCNA2Read", "ClaveCNA");
					<%for (int i = 0; i < Control.length; i++) {%>
						$('#nOrden').val('<%=i+1%>');
						<%if ((i+1)!=3 && (i+1)!=9 && (i+1)!=10 && (i+1)!=14 && (i+1)!=15 && (i+1)!=16){%>
							querySelectPost("catalogoEPRead", "<%=Control[i]%>");
							$("#<%=Control[i]%>").attr("disabled", true);
						<%}else{%>
							$("#h<%=Control[i]%>").attr("disabled", true);
						<%}%>
					<%}%>
					//$("#hClaveCNA").attr("disabled", true);
            }
	

</script>
		<script>
function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	if (keyPressed == 47)
	{
	return false;
	 }
	return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

function Sinespacios(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	/*if (keyPressed > 91 && keyPressed != 209 && keyPressed != 95){
	alert ("Solo se Permiten Mayusculas y Numeros");
	 }
	 if (keyPressed == 61 || keyPressed == 63 ||
	 	 keyPressed == 46 || keyPressed == 59 ||
	 	 keyPressed == 58 || keyPressed == 60 ||
	 	 keyPressed == 62)
	{
	return false;
	 }*/
	return !(keyPressed > 31 && (keyPressed < 45) && keyPressed != 209 && keyPressed != 95);
}

function LetrasNums(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241 || keyPressed == 17){
		alert("solo se permiten numero y létras")
		return false;
	 }
	 if (keyPressed == 61 || keyPressed == 63 || keyPressed == 62 ||
	 	 keyPressed == 59 || keyPressed == 58 || keyPressed == 60 ||
	 	 keyPressed == 91 || keyPressed == 92 || keyPressed == 93 ||
	 	 keyPressed == 94 || keyPressed == 95 || keyPressed == 96)
	{
	return false;
	 }
	return !(keyPressed > 32 && (keyPressed < 48 || keyPressed > 122) && keyPressed != 209 && keyPressed != 241);
}

function habilitaMillar2(elPar){
	if (elPar == 0){
		$("#CamInst").val(elPar);
	}
	else{
		$("#CamInst").val(elPar);
	}
}

function actualiza(){
	$("#cIDRFC").val($("#cIDRFC_contrato").val());
}

function currencyFormat(fld, milSep, decSep, e) {
	var sep = 0;
	var key = '';
	var i = j = 0;
	var len = len2 = 0;
	var strCheck = '0123456789';
	var aux = aux2 = '';
	var whichCode = (window.Event) ? e.which : e.keyCode;
	if (whichCode == 13)
		return true; // Enter
	if (whichCode == 8)
		return true; //Backspace
	if (whichCode == 127)
		return true; //suprimir
	key = String.fromCharCode(whichCode);
	if (strCheck.indexOf(key) == -1)
		return false; // Valida que sea numero
	len = fld.value.length;
	for (i = 0; i < len; i++)
		if ((fld.value.charAt(i) != '0') && (fld.value.charAt(i) != decSep))
			break;
	aux = '';
	for (; i < len; i++)
		if (strCheck.indexOf(fld.value.charAt(i)) != -1)
			aux += fld.value.charAt(i);
	aux += key;
	len = aux.length;
	if (len == 0)
		fld.value = '';
	if (len == 1)
		fld.value = '' + decSep + '' + aux;
	if (len == 2)
		fld.value = '0' + decSep + aux;
	if (len > 2) {
		aux2 = '';
		for (j = 0, i = len - 3; i >= 0; i--) {
			if (j == 3) {
				aux2 += milSep;
				j = 0;
			}
			aux2 += aux.charAt(i);
			j++;
		}
		fld.value = '';
		len2 = aux2.length;
		for (i = len2 - 1; i >= 0; i--)
			fld.value += aux2.charAt(i);
		fld.value += decSep + aux.substr(len - 2, len);
	}
	return false;
}


</script>

	</head>

<body id="dt_example" >
	<form action="#" name="FormContrato">
		<div id="container" class="container SyCData">
			<h1>Contrato Obra</h1>
				<table id="clvcont">
					<tr><td style="width:24%">N&uacute;mero de Documento:</td><td><input type="text" style="text-transform:uppercase" maxlength="40" size="70" name="cIdContrato"	ID="cIdContrato" class="obligatorio" onKeyPress="return Sinespacios(event)" onBlur="no_foco(this);"/>
											  No. Folio.:<input readonly name="folio" type="text" id="folio" size="4" style="text-align:right;" style="background-color:#CCCCCC;" style="text-transform:uppercase" value="1"/>
					</td></tr>
					<tr align="left"><td valign="top" colspan="2">R.F.C:
							<input type="text" maxlength="15" size="15" name="cIDRFC_contrato" ID="cIDRFC_contrato" readonly class="AyudaSyC  obligatorio" onChange="no_foco(this);actualiza();"/>
							<input readonly type="text" maxlength="100" size="90"	name="cnombre" ID="cnombre" onBlur="no_foco(this);"/>
							<input type="hidden" name="cIDRFC" id="cIDRFC" value=""/>
							<input type="hidden" name="cEjercicio" id="cEjercicio" value=""/>
							<input type="hidden" name="cTContrato" id="cTContrato" value="OB"/>
							<input name="OPERADOR" type="hidden" id="OPERADOR" size="40" value="<%=usuario.getNombre()%>" />	
							<input name="UsrLogin" type="hidden" id="UsrLogin" size="40" value="<%=usuario.getLogin()%>" />	
							<input name="FECHA_CARGA" type="hidden" id="FECHA_CARGA" size="12" value="<%=today%>"/>
							<input type="hidden" name="cEjercicioTbl2" value=""/>
							<input type="hidden" name="cIdEntidadContableTbl2" value=""/>
							<input type="hidden" name="cIdContratoTbl2" value="0"/>
							<input type="hidden" name="LHaySaldoAnticipo" value=""/>		
							<input type="hidden" id="nOrden" name="nOrden" value=""/>
							<input type="hidden" id="Partida" name="Partida" value="6"/>
							<input type="hidden" id="Partida2" name="Partida2" value="X"/>
							<input type="hidden" id="Partida3" name="Partida3" value="X"/>					
							<input type="hidden" id="Partida4" name="Partida4" value="X"/>	
							<input type="hidden" id="Partida5" name="Partida5" value="X"/>	
							<input type="hidden" id="Partida6" name="Partida6" value="X"/>	
							<input type="hidden" id="Partida7" name="Partida7" value="X"/>	
							<input type="hidden" id="Partida8" name="Partida8" value="X"/>	
							<input type="hidden" id="Partida9" name="Partida9" value="X"/>	
							<input type="hidden" id="Partida10" name="Partida10" value="X"/>	
							<input type="hidden" id="Partida11" name="Partida11" value="X"/>	
							<input name="CamInst" type="hidden" id="CamInst" value=""/>
							<input name="cIdEntidadContable" type="hidden" id="cIdEntidadContable" value="<%=cCentroContable%>"/>
							<input name="ValidacionBorrar1" type="hidden" id="ValidacionBorrar1" size="30" />
							<input type="hidden" id="nFolioJur1" name="nFolioJur1"/>
							<input type="hidden" id="cIdContrato1" name="cIdContrato1"/>
							<input type="hidden" id="cIDRFCSesion1" name="cIDRFCSesion1"/>
							<input type="hidden" id="fSesion1" name="fSesion1"/>
							<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
						</td>
					</tr>
				</table>

				<table id="Generales" width="100%">
					<tr align="left">
						<td>Clase de Contrato:</td><td><select name="cIdClaseContratoObra" id="cIdClaseContratoObra"><option></option></select></td>
					</tr>
			<tr></tr>
			<tr></tr>
			<tr></tr>
					<tr>
					<td>Unidad Administrativa:</td><td><select name="cIdUnidadAdministrativa" id="cIdUnidadAdministrativa" style="width: 35em;"><option value="RHQ"></option></select></td>
					</tr>
			<tr></tr>
			<tr></tr>
			<tr></tr>
					<tr>
						<td valign="top">Tipo de Adjudicaci&oacute;n:</td><td><select name="cIdTipoAdjudicacion" id="cIdTipoAdjudicacion" style="width: 35em;"><option value="0"></option></select></td>
					</tr>
			<tr></tr>
			<tr></tr>
					<tr>
						<td valign="top">PluriAnual:<input type="checkbox" name="lEsPlurianual" id="lEsPlurianual"/></td>
						<td valign="top">Tipo de Contrato:<select name="cIdTipoContratoObra" id="cIdTipoContratoObra" style="width: 10em;"><option selected value=""></option></select></td> 
						<td valign="top">Esquema de Precios:<select name="id_esquema" id="id_esquema" style="width: 10em;"></select></td> 
					</tr>
					
				</table>
		</div>
		<div id="container" class="container SyCData">
			<div id="demo1">
				<table id="datosfec">
					<thead>
						<tr>
							<%--					<th>Tipo de Adjudicaci&oacute;n:</th>--%>
							<th>Fecha Adjudicaci&oacute;n</th>
							<th>Firma Contrato:</th>
							<th>Fecha Inicio:</th>
							<th>Fecha Término:</th>
						</tr>
					</thead>
					<tbody>
						<tr align="left">
							<%--					<td valign="top"><select name="cIdTipoAdjudicacion" id="cIdTipoAdjudicacion"><option value="cIdTipoAdjudicacion"></option></select></td>--%>
							<td valign="top" align="center"><input type="text" readonly maxlength="10" size="7" id="fAdjudicacion" name="fAdjudicacion" class="obligatorio" style="text-align:center;" onChange="no_foco(this);"/></td>
							<td valign="top" align="center"><input type="text" readonly maxlength="10" size="7" id="fFirmaContrato"	name="fFirmaContrato" class="obligatorio" style="text-align:center;" onChange="no_foco(this);"/></td>
							<td valign="top" align="center"><input type="text" readonly maxlength="10" size="7" id="fInicio" name="fInicio" class="obligatorio" style="text-align:center;" onChange="no_foco(this);"/></td>
							<td valign="top" align="center"><input type="text" readonly maxlength="10" size="7" id="fTermino" name="fTermino" class="obligatorio" style="text-align:center;" onChange="no_foco(this);"/></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<div id="container" class="container SyCData">
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1">Generales</a></li>
					<li><a href="#tabs-2">Anticipos</a></li>
					<li><a href="#tabs-3">Retenciones</a></li>
					<li><a href="#tabs-4">Claves Presupuestales</a></li>
					<li><a href="#tabs-5">Cesión de Derechos</a></li>
					<li><a href="#tabs-6">Convenio Modificatorio</a></li>
				</ul>
<div id="tabs-1">
	  <div id="container" class="container SyCData">
			<table>
				<tr><td rowspan="3">Comentario:<br/><textarea id="cObjetoContrato" onkeydown="return LetrasNums(event)" name="cObjetoContrato" style="height: 60px; width: 400px" ></textarea></td>
					<td>Tipo Fondo:</td><td><select name="cIdTipoFondo" id="cIdTipoFondo"><option selected value="">______________</option></select></td>
				</tr>
				
				<tr><td>Tipos de Moneda:</td><td><select name="cIdTipoMoneda" id="cIdTipoMoneda"><option selected value="0">PESO MEXICANO</option><option>DOLAR ESTADOUNIDENSE</option></select></td></tr>
				<tr><td valign="top">% IVA Aplicable:</td><td><select name="nPorcIVAAplicable" id="nPorcIVAAplicable"><option selected value="0">0%</option></select></td></tr>
			</table>
		</div>
		<div id="container" class="container SyCData">
			<table>
				<tr>
					<td>Monto de Obra:</td><td><input type="text" maxlength="12" size="12" name="mObra" id="mObra" value="0" class="obligatorio" style="text-align:right;"	onKeyPress="return onlyNumbers(event)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this); no_foco(this);"/></td> <%--onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)"--%>
					<td>Monto Bruto:</td><td><input type="text" maxlength="12" size="12" name="mImporte" readonly id="mImporte" value="0" class="obligatorio" style="text-align:right;" onKeyPress="return onlyNumbers(event)" style="background-color:#CCCCCC" /></td>
					<td>Monto Ejercicio Vigente(*):</td><td><input type="text" maxlength="12" size="12" name="mEjercicio" readonly	id="mEjercicio" style="text-align:right;" onKeyPress="return onlyNumbers(event)" style="background-color:#CCCCCC"/></td>
				</tr>
				<tr>
					<td>Monto O.A.E.:</td><td><input type="text" maxlength="12" size="12" name="mOAE" id="mOAE" style="text-align:right;" onKeyPress="return onlyNumbers(event)" value="0.00"/></td>
					<td>Monto I.V.A.:</td><td><input type="text" maxlength="12" size="12" name="mIVA" id="mIVA" readonly value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)" style="background-color:#CCCCCC"/></td>
					<td>Monto Contrato (M.E.):</td><td><input type="text" maxlength="12" size="12" name="mContratoME" id="mContratoME" readonly value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)" style="background-color:#CCCCCC"/></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>Monto Total:</td><td><input type="text" maxlength="12" size="12" name="mTotal" readonly id="mTotal" value="0" class="obligatorio" style="text-align:right;" onKeyPress="return onlyNumbers(event)"  style="background-color:#CCCCCC"/></td>
				</tr>
			</table>
		</div>
</div>
				<div id="tabs-2">
					<table id="dt_anticipos" class="display">
						<thead>
							<tr align="center">
								<th>% Anticipo</th>
								<th>% Amortizaci&oacute;n</th>
							</tr>
						</thead>
						<tbody>
							<tr align="right">
							<td align="center"><input type="text" value="0" maxlength="2" size="5" name="nPorcAsignacion" id="nPorcAsignacion" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/></td>
							<td align="center"><input type="text" value="0" maxlength="2" size="5" name="nPorcAmortizacion" id="nPorcAmortizacion" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/></td>
							</tr>
						</tbody>
					</table>
				<table>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
						<td id="LabelBruto">Importe Bruto:</td><td><input type="text"  maxlength="20" size="15" name="mImporteAnticipo" id="mImporteAnticipo" style="text-align:right;"/></td>
						<td id="LabelIVAAnt">IVA Anticipo:</td><td><input type="text"  maxlength="20" size="15" name="mImporteAnticipoIVA" id="mImporteAnticipoIVA" readonly style="text-align:right;"/></td>
						<td id="LabelTotalAnt">Total Anticipo:</td><td><input type="text"  maxlength="20" size="15" name="mTotalAnticipo" id="mTotalAnticipo" readonly style="text-align:right;"/></td>
					</tr>
					<tr>
						<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
						<td id="LabelAmortiza">Importe Amortizado:</td><td><input type="text"  maxlength="20" value="0" size="15" name="mAmortizado" id="mAmortizado" style="text-align:right;"/></td>
						
					</tr>
				</table>
				</div>
					
				<div id="tabs-3">
					<select	onchange="fn_actret('cIdTipoRetencion', 'nPorcRetencion')" name="cIdTipoRetencion" id="cIdTipoRetencion"><option selected value="1">SIN RETENCION</option></select>
					<input type="button" value="Agregar" name="Add1" id="Agregar"	onclick="fnAgregarRet();"/>
					<div id="divMilla2">
						<label>
							Camara :
							<input name="grpMilla2" type="radio" id="grpMilla2"
								onclick="habilitaMillar2(0)" value="Camara"/>
						</label>
						<label>
							Instituto :
							<input type="radio" id="grpMilla2" name="grpMilla2"
								onclick="habilitaMillar2(1)" value="Instituto"/>
						</label>
					</div>
					<table id="dt_retencion" class="display">
						<thead>
							<tr align="center"><th>Clave de retención</th></tr>
						</thead>
						<tbody>
							<tr>
						
							<td><input type="hidden" name="nPorcRetencion" id="nPorcRetencion" value=""/></td>
							</tr>
						</tbody>
					</table>
				</div>

				<div id="tabs-4">
					<table height="66" width="98%" border="0">
						<tr>
							<td>
								<table border="0">
									<tr>
										<td>Estructura Programática</td>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td><input type="text" id="ep" name="ep" size="65" readonly class="" /></td>
										<td><input type="button" id="nIdClaveEgresos2" size="5"  value="..." onclick="Grid()" onblur="rellenaCampos();" /></td>
										<td><input type="button" value="Agregar" name="AgregarEP" id="AgregarEP"	onclick="fnClickAddRowC();"/></td>
									</tr>
								</table>
							</td>
						</tr>
						
						<tr>
							<td>
						<table id="dt_clavepresup" class="display">
							<thead>
								<tr align="center">
<!--									<th>Código SAI</th>-->
									<th>Estructura Programática</th>
									<th>Clave Interna</th>
									<th>&nbsp;</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</table>
					</div>

					<div id="tabs-5">
						<table height="66" width="98%" border="0">
							<tr>
								<td>
									<table border="0">
										<tr>
											<td>Cesión de derechos a:</td>
											<td>&nbsp;</td>
											<!-- <td><input type="button" value="Agregar" name="AgregarSes" id="AgregarSes"	onclick="fnClickAddRowS();"></td> -->
											<td><input type="button" id="pbCesionD" value="Agregar Cesión"/></td>							
										</tr>
									</table>
								</td>
							</tr>
							
							<tr>
								<td>
								<table id="dt_sesion" class="display">
									<thead>
										<tr align="center">
											<th>R. F. C.</th>
											<th>Nombre</th>
											<th>Numero Oficio</th>
											<th>Fecha</th>
											<th>Activo</th>
											<th>&nbsp;</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
						</table>
					</div>

					<div id="tabs-6">
						<table id="dt_convenio" class="display">
							<thead>
								<tr align="center">
									<th>Convenio</th>
									<th>Monto Convenio</th>
									<th>Fecha</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
					
				</div>

			</div>

			<div id="dialog-Cesion" title="Cesión de Derechos">
					<table border="0">
						<tr>
							<td><input type="text" maxlength="15" size="15" name="cIDRFCSesion" ID="cIDRFCSesion" readonly class="AyudaSyC  obligatorio"/></td>
							<td><input type="text" readonly maxlength="60" size="60" id="NombreSesion" name="NombreSesion" style="text-align:left;"/></td>
						</tr>
						<tr>
							<td><input type="text" maxlength="30" size="30" value=""  id="nFolioJur" name="nFolioJur" class="cesion" style="text-align:left;"/></td>
							<td><input type="text" readonly maxlength="10" size="10" id="fSesion" name="fSesion" style="text-align:center;"/></td>
						</tr>
						<tr>
							<td><input type="button" id="pbInactiva" style="visibility: hidden" value="DesActivar"/></td>							
							<td>&nbsp;</td>
						</tr>
					</table>
			</div>
			
			
			</form>
	</body>
</html>
