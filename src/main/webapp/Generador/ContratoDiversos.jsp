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
	String Control[] = {"EjercicioFiscal", "RamoEP",
			"UnidadResponsableEP", "GrupoFuncional", "Funcion",
			"SubFuncion", "ProgramaGeneral", "ActividadInstitucional",
			"ProgramaPresupuestario", "Partida", "TipoGasto",
			"FuenteFinanciamiento", "EntidadFederativa", "Cartera",
			"UnidadNormativa", "cUnidadEjecutora"};
%>
<%
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	int cmdPdfPedido = 0;
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

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	String folioSeq = "CD-" + usuario.getU_UR();
	CFSequenceManager sq = CFSequenceManager
			.getInstance("jdbc/gestion");

	int val = sq.nextVal(folioSeq);

	folioSeq += "-" + val + "/";
	
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean muestraRadicado = "S".equalsIgnoreCase( cabl.getSystemSetting("activa_radicado_arrendamiento") );
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Contrato de Diversos</title>

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
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

<script type="text/javascript" charset="utf-8">

	var rowCount = 0 ;
	var nSesion = 0;
	var bCarga = false;
	var bAgregar = false;
	var muestraRadicado = <%=muestraRadicado%>;

	$(document).ready(function()
	{
		$('.currency').blur(function()
		{
			$('.currency').formatCurrency();
		});
	});

	$(document).ready(function(){
			//document.oncontextmenu = function(){return false};

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
	
		$('#cConceptoContrato').bind('copy paste', function (e) {       
			e.preventDefault();
	    });

		$( "#dialog-Cesion" ).dialog({
					autoOpen: false,
					height: 200,
					width: 750,
					modal: true,
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
								//$("#cEjercicio").val();
								queryFormPost("pContratoDiversoSesionCreate", {async: false });
								oTableMov.fnReloadAjax();
								
							}else{
								fnClickAddRowS();	
							}
							
							
	
							$( this ).dialog( "close" );
						},
						"Cancelar": function() {						
							$( this ).dialog( "close" );
						}
					},
					close: function() {
											
					}
				});
						
			    $("#tabs").tabs( {
			        "show": function(event, ui) {
			            var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
			            if ( oTable.length > 0 ) {
			                oTable.fnAdjustColumnSizing();
			            }
			        }
			    } );
			     
			    $('table.display').dataTable( {
			        "sScrollY": "200px",
			        "bScrollCollapse": true,
			        "bPaginate": false,
			        "bJQueryUI": true,
			        "aoColumnDefs": [
			            { "sWidth": "10%", "aTargets": [ -1 ] }
			        ]
			    } );
				
				$('#dt_retencion').dataTable(
					{         
		   			    "bPaginate": false,
	        			"bLengthChange": false,
	        			"bFilter": false,
	        			"bSort": false,
	        			"bInfo": false,
	        			"bAutoWidth": false, 
						"sScrollY": 100,
						"bScrollCollapse": true,         
						"bJQueryUI": true,    
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers"    
					} );


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


			
				$('#dt_clavepresup').dataTable(
					{         
		   			    "bPaginate": false,
	        			"bLengthChange": false,
	        			"bFilter": false,
	        			"bSort": false,
	        			"bInfo": false,
	        			"bAutoWidth": false, 
						"sScrollY": 100,         
				        "sScrollXInner": "100%",
				        "bScrollCollapse": true,
						"bJQueryUI": true,    
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers"    
					} );
				$('#Agregar').button();
				$('#nIdClaveEgresosX').button();
				$('#Agregar2').button();
				$('#pbCesionD').button();
				$('#pbInactiva').button();

				querySelectPost("CatalogoEsquemaPrecioRead","id_esquema", {async: false });
	
				querySelectPost("TipoAdjudicaciondRead", "cIdTipoAdjudicacion", {async: false });
				querySelectPost("TipoContratoDiversoRead", "cIdTipoContratoDiv", {async: false });
				querySelectPost("TipoFondoRead", "cIdTipoFondo", {async: false });
				querySelectPost("PorcIVARead", "nPorcIVAAplicable", {async: false });
				querySelectPost("TipoAnticipoDiversoRead", "cIdTipoAnticipoDiverso", {async: false });
				querySelectPost("UnidadresponsableRead", "cIdUnidadAdministrativa", {async: false });
				querySelectPost("CatalogoTipoRetencionRead", "cIdTipoRetencion", {async: false });
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
				queryFormPost("BuscaContratoDiversosPrincipalRead", {async: false });
				queryFormPost("RFCObraDivRead", {async: false });
       				 
				var Pp = $('#Partida').val();
				var Pp2 = $('#Partida2').val();
				var Pp3 = $('#Partida3').val();       				 
				var Cont1 = $('#cIdContrato').val();
				if ($('#cIdContrato').val() == ''){
					$("#Delete").attr("disabled", true);
					queryFormPost("FechaRegistroRead", {async: false });
				}
				else{
					setTimeout("hello()",5);
				} 

			var decimal1 = 0;
			var decimal2 = 0;
			var decimal3 = 0;
			var decimal4 = 0;
			var decimal5 = 0;
			var decimal6 = 0;
			var decimal7 = 0;
			var decimal8 = 0;
	

			$("#mImporteContrato").change(function () {
	              $("#mImporteBruto").val( $(this).val() );
	              $("#mContratoMN").val( $(this).val() );
	              $("#nPorcIVAAplicable").change();
	              decimal1=(parseFloat( $("#mImporteHonorarios").val() ) + parseFloat( $(this).val() )  + parseFloat( $("#mImporteViaticos").val() ) + parseFloat( $("#mImporteIVA").val() ) );
				  $("#mImporteTotal").val(decimal1.toFixed(2));
	              $("#mContratoMN").val( $("#mImporteTotal").val() );
	              $("#mImporteTotal").formatCurrency();
	              $("#mContratoMN").formatCurrency();
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
					$( "#dialog-Cesion" ).dialog( "open" );
				});
			
			$( "#pbInactiva" )
				.button()
				.click(function() {
					
					$("#cIdContrato1").val($("#cIdContrato").val());
					$("#cIDRFCSesion1").val($("#cIDRFCSesion").val());
					$("#fSesion1").val($("#fSesion").val());
					$("#nFolioJur1").val($("#nFolioJur").val());
					
					queryFormPost("pContratoDiversoSesionUpdate", {async : false});
					
					alert("DesActivado");
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
						decimal6 = Quitafrmt( $("#mImporteContrato").val() );
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
						//$("input.AyudaSyC").subIniciaDlg();
						$("input.autoCompletaSyC").subIniciaAutoCompleta();
					});
		
		
			$("#nPorcIVAAplicable").change(function () {
				decimal2=( $("#mImporteBruto").val() * $(this).val() );
	          	$("#mImporteIVA").val(decimal2.toFixed(2));
	        	decimal3=( parseFloat( $("#mImporteBruto").val()) * (1 + parseFloat( $(this).val() ) ) );
	            $("#mImporteTotal").val(decimal3.toFixed(2));
	            $("#mContratoMN").val( $("#mImporteTotal").val() );
	            $("#mImporteTotal").formatCurrency();
	            $("#mContratoMN").formatCurrency();
	 			$("#nPorcAsignacion").change();
			});
		
			$("#mImporteAnticipo").change(function () {
				decimal4=( $(this).val() * $("#nPorcIVAAplicable").val() );
	          	$("#mImporteAnticipoIVA").val(decimal4.toFixed(2));
	    	    decimal5=( parseFloat( $(this).val() ) * (1 + parseFloat( $("#nPorcIVAAplicable").val() ) ) );
	            $("#mTotalAnticipo").val(decimal5.toFixed(2));
	
				});
			
		
		    /* Add a click handler to the rows - this could be used as a callback */
		    $("#dt_clavepresup tbody").click(function(event) {
		        $(oTable.fnSettings().aoData).each(function (){
		            $(this.nTr).removeClass('row_selected');
		        });
		        $(event.target.parentNode).addClass('row_selected');
		    });
		     
		    /* Add a click handler for the delete row */
		    $('#delete').click( function() {
		        var anSelected = fnGetSelected( oTable );
		        oTable.fnDeleteRow( anSelected[0] );
		    } );
		     
		    /* Init the table */
		    oTable = $('#dt_clavepresup').dataTable( );
		
			if(!muestraRadicado){
				$("#divRadicado").css("display","none");
				$("#cuentaDisponible").val('82106');
				$("#isRadicado").val(0);
			}
		});
	
		function Quitafrmt( fld ) {
	   		fld = fld.replace("$", "");
	   		fld = fld.replace(/,/g, "");
			return fld;
		}


	function Borrar() {
	
			//queryFormPost("ContratoEPDelete,ContratoDiversoRetencionDelete,ContratoDiversoAnticipoDelete,ContratoDiversoDelete", {async: false });
			//alert("Contrato De Diverso Eliminado!");
	}
	
		
		/* Get the rows which are currently selected */
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

function foco(elemento) {
 elemento.style.border = "1px solid #FF0000";
 }

 function no_foco(elemento) {
 elemento.style.border = "1px solid #CCCCCC";
 }
 
function fn_actret(pcontrol, pctrlret) {				
	var valor = $('#' + pcontrol).val()
	$('#' + pctrlret).val( valor.substring(2) * 100 + "%" ) ;
	if ( $("#cIdTipoRetencion").val() == 2 ) {
		$("#divMilla2").show();
	}
	else {
		$("#divMilla2").hide();
	}
}
			
			
//			function fnClickAddRow() {
//				var rowCount2 = $('#dt_retencion tr').length;
//				var ctrlname = 'cIdTipoRetencion' + rowCount2;
//				$('#dt_retencion').dataTable().fnAddData( [
//					'<input type="hidden" name="cEjercicioTbl2" value="'+$('#cEjercicio').val()+'">' +
//					'<input type="hidden" name="cIdEntidadContableTbl2" value="'+$('#cIdEntidadContable').val()+'">' +
//					'<input type="hidden" name="cIdContratoTbl2" value="'+$('#cIdContrato').val()+'">' +
//					'<td><select onchange="' + "fn_actret(\'" + ctrlname + "\', \'nPorcRetencion" + rowCount2 + '\')" name="cIdTipoRetencion" id="' + ctrlname + '"><option selected value=""></option></select></td>',
//					'<td><input type="text" maxlength="5" size="5" name="nPorcRetencion" id="nPorcRetencion' + rowCount2 + '"></td>', 
//					] );
//				querySelectPost("CatalogoTipoRetencionRead", ctrlname, {async: false });
//				
//			}

			
		


				function hello() {
					bCarga = true;
					queryFormPost("BuscaContratoDiversosPrincipalRead", {async: false });
					queryFormPost("fDocumentoDiversoRead", {async: false });
					queryFormPost("fAdjudicacionDiversoRead", {async: false });
					queryFormPost("fContratoIniDiversoRead", {async: false });
					queryFormPost("fFirmaContratoDiversoRead", {async: false });
					queryFormPost("fContratoFinDiversoRead", {async: false });
					queryFormPost("fAnticipoDiversoRead", {async: false });
					queryFormPost("BuscaAnticipoDiversosRead", {async: false });
					queryFormPost("RFCObraDivRead", {async: false });
					document.getElementById('mImporteAnticipo').style.visibility = 'visible';
  					document.getElementById('mImporteAnticipoIVA').style.visibility = 'visible';
  					document.getElementById('mTotalAnticipo').style.visibility = 'visible';
  					document.getElementById('LabelBruto').style.visibility = 'visible';
  					document.getElementById('LabelIVAAnt').style.visibility = 'visible';
  					document.getElementById('LabelTotalAnt').style.visibility = 'visible';
					document.getElementById('LabelAmortiza').style.visibility = 'visible';
						
					$("#mImporteContrato").formatCurrency(function () {});
              		$("#mImporteBruto").formatCurrency(function() {});
              		$("#mContratoMN").formatCurrency(function() {});
              		$("#mImporteHonorarios").formatCurrency(function(){});
              		$("#mImporteViaticos").formatCurrency(function(){});
              		$("#mImporteIVA").formatCurrency(function(){});
			  		$("#mImporteTotal").formatCurrency(function(){});
			  		$("#mContratoME").formatCurrency(function(){});
			  		$("#mImporteAnticipo").formatCurrency(function() {});
					$("#mImporteAnticipoIVA").formatCurrency(function() {});
					$("#mTotalAnticipo").formatCurrency(function() {});
              		$("#mAmortizado").formatCurrency(function() {});
              		
					var Cont1 = $('#cIdContrato').val();
			
					$('#dt_clavepresup').dataTable( {
						"bLengthChange" : true,
			            "bFilter" : true,
			            "bSort" : true,
			            "bInfo" : true,
			            "bPaginate" : false,
			            "bAutoWidth" : false,
			            "bScrollCollapse" : true,
			            "sScrollXInner": "100%", 
			    		"sScrollX": "100%",
			            "bJQueryUI" : true,
			            "bRetrive" : true,
			            "bDestroy" : true,
			            "bServerSide": true,  
				
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='" + Cont1 + "' and cIdEntidadContable = '" + $("#cIdEntidadContable").val() + "'",
					aoColumns: [
//						{ sName: "EP"},
						{ sName: "ClaveSIAFF" },
						{ sName: "ClaveInterna" },
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }]
                } ) ;
			
					$('#dt_retencion').dataTable( {
						"bLengthChange" : true,
			            "bFilter" : true,
			            "bSort" : true,
			            "bInfo" : true,
			            "bPaginate" : false,
			            "bAutoWidth" : false,
			            "bScrollCollapse" : true,
			            "sScrollXInner": "100%", 
			    		"sScrollX": "100%",
			            "bJQueryUI" : true,
			            "bRetrive" : true,
			            "bDestroy" : true,
			            "bServerSide": true,  
				
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_CONSULTARETENCIONES_DIVERSOS&qw=cIdContrato='" + Cont1 + "'  AND cIdTipoRetencion<>1",
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoConvenio&qw=cIdContrato='" + Cont1 + "' and cIdEntidadContable = '" + $("#cIdEntidadContable").val() + "'",
					aoColumns: [
						{ sName: "nConsecutivoModificacion"},
						{ sName: "mTotal", sClass: "alignRight"},
						{ sName: "ftermino", sClass: "alignCenter"}]
                } ) ;

				/*  if((document.FormContrato.lEsPlurianual.value) == '1') {
	    	   	   document.FormContrato.lEsPlurianual.checked = true;
	    	   	   
	    		}else {
			        document.FormContrato.lEsPlurianual.checked = false;
			       }*/
					$('.obligatorio').attr("readonly", true); 
					$('.obligatorio').css("background", "#f0f0f0"); 
					$('.obligatorio').attr("disabled", true);
					$("Select").attr("disabled", true);
					$("textarea").attr("disabled", true);

					if (<%=id_oper%>=="1"){
						
						parent.document.getElementById("pb_save").disabled = true;	
					}				
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
					var vclavepre = jqInputs[ 0 ].value;
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
			$( "#fAnticipo" ).datepicker({
				showOn: "button",
				dateFormat: "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});


		$(function() {
			$( "#fAdjudicacion" ).datepicker({
				dateFormat: "dd/mm/yy", 			
				changeYear: true, 
				changeMonth: true
			});
		});

		$(function() {
			$( "#fFirmaContrato" ).datepicker({
				dateFormat: "dd/mm/yy", 			
				changeYear: true, 
				changeMonth: true
			});
		});
		$(function() {
			$( "#fContratoIni" ).datepicker({
				dateFormat: "dd/mm/yy", 			
				changeYear: true, 
				changeMonth: true
			});
		});
		$(function() {
			$( "#fContratoFin" ).datepicker({
				dateFormat: "dd/mm/yy", 			
				changeYear: true, 
				changeMonth: true
			});
		});
		$(function() {
			$( "#fDocumento" ).datepicker({
				dateFormat: "dd/mm/yy", 			
				changeYear: true, 
				changeMonth: true
			});
		});

		$(function() {
			$( "#fSesion" ).datepicker({
				dateFormat: "dd/mm/yy", 			
				changeYear: true, 
				changeMonth: true
			});
		});


		function Regresar() {
			self.location="../caso/principal.jsp";
		}
		
	function cmdGuardar() {
		if(validaCamposRequeridos()==false){
			return false;
		}
		if (Number($("#mAmortizado").val()) > 0 || Number($("#nPorcAsignacion").val()) == 0){
			document.FormContrato.LHaySaldoAnticipo.value = 0;
		}else{
			document.FormContrato.LHaySaldoAnticipo.value = 1;
		}

		if ($('#cIdTipoContratoDiv').val() == '0'){
			alert("Seleccione el Tipo de Contrato");
			return false;
		}

		if ($('#id_esquema').val() == '0'){
			alert("Seleccione el Esquema de Precio");
			return false;
		}

		if 	(rowCount < 2) {
			alert("Por favor agregue al menos una Clave Presupuestal");
			return false;
			}
		if(!validaFechaTermino()){
			foco(document.getElementById('fContratoFin'));
			return false;
		}
		
		if ($('#cIdTipoRetencion').val()!=''){
			Generales();
			if ( nSesion > 0 ) {
				queryFormPost("tContratoDiversoAnticipoCreate,ContratoEPCreate,ContratoDivSesionCreate", {async: false });
			}else{
				queryFormPost("tContratoDiversoAnticipoCreate,ContratoEPCreate", {async: false });
			}
			queryFormPost({// se insertan nuevos Campos Contrato de Arrendamiento
  						queryName: "tContratoArrendamientoCreate",
  						async: false,
    					callback: function() {
				alert("Contrato Diverso Guardado!");
				//document.location.reload()
				}
    				});
    		return true;
			//document.location.reload();			
		}else{
			$('#cIdTipoRetencion').val(1);
			$('#dt_retencion').dataTable().fnAddData( [
				'<input type="hidden" name="cEjercicioTbl2" value="'+$('#cEjercicio').val()+'">' +
				'<input type="hidden" name="cIdEntidadContableTbl2" value="'+$('#cIdEntidadContable').val()+'">' +
				'<input type="hidden" name="cIdContratoTbl2" value="'+$('#cIdContrato').val()+'">',
				] );
			Generales();
			if ( nSesion > 0 ) {
				queryFormPost("tContratoDiversoAnticipoCreate,ContratoEPCreate,ContratoDivSesionCreate", {async: false });
			}else{
				queryFormPost("tContratoDiversoAnticipoCreate,ContratoEPCreate", {async: false });
			}
				//queryFormPost("tContratoDiversoAnticipoCreate,ContratoEPCreate,ContratoDivSesionCreate", {async: false });
				queryFormPost({
 						queryName: "tContratoArrendamientoCreate",
 						async: false,
   					callback: function() {
						alert("Contrato Diverso Guardado!");
						return true;
						//document.location.reload()
					}
   				});    		
		}
			
	}
		
activo=1;
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
		queryFormPost("tContratoDiversoCreate", {async: false });
		queryFormPost("tContratosJustCNETCreate", {async: false });
		activo=0; //Desabilitamos el código para la próxima vez
	}			
}		
		
function fnAgregarRet() {
	if ($("#cIdContrato").val()==''){
			alert("Por favor ingrese el Numero de Documento");
			foco(document.getElementById('cIdContrato'));
			return false;
	}

	if ($('#cIdTipoContratoDiv').val() == '0'){
		alert("Seleccione el Tipo de Contrato");
		return false;
	}

	if ($('#id_esquema').val() == '0'){
		alert("Seleccione el Esquema de Precio");
		return false;
	}

	if ( $("#cIdTipoRetencion").val() == "2"){
		if ($("#CamInst").val() == "") {
			alert("Selecccione Camara o Instituto para continuar");
			return false;
		}
	}
		if ($("#cIDRFC").val()==''){
			alert("Por favor ingrese un RFC");
			foco(document.getElementById('cIDRFC'));
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
		if ($("#fContratoIni").val()==''){
			alert("Por favor ingrese La Fecha de Inicio");
			foco(document.getElementById('fContratoIni'));
			return false;
			}
		if ($("#fContratoFin").val()==''){
			alert("Por favor ingrese La Fecha de Termino");
			foco(document.getElementById('fContratoFin'));
			return false;
			}				
		if ($("#mImporteContrato").val()=='' || $("#mImporteContrato").val()=='0' || $("#mImporteContrato").val()=='0.00' || $("#mImporteContrato").val()=='$0.00'){
			alert("Por favor Ingrese un Monto de Contrato");
			foco(document.getElementById('mImporteContrato'));
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
    	var oTableLocal = $('#dt_retencion').dataTable();
		var rowCount = oTableLocal.fnGetNodes().length;
		var idRet=$("#cIdTipoRetencion").val();
		//i: inicia en 2 por que hay dos filas en la tabla al iniciar
		var i=0, noExiste=true;
		var aData;
		//Revisa que no se repita la clave de retención
		while(i<rowCount && noExiste){
			aData = oTableLocal.fnGetData( i );
			
			if(idRet== aData[2])	
				noExiste=false;
			else
				i++;
		}
		// En caso de que ya se haya agregado la clave sale de la función
		if(i!=rowCount){
			alert("Esa clave ya se agregó previamente");
			return false;
		}
		
    }
    catch(e) {
       alert(e);
    }
				

			queryFormPost("CatalogoTipoRetencionObtenDetRead", {async: false });
			queryFormPost("tContratoDiversoRetencionCreate", {async: false });
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
				
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_CONSULTARETENCIONES_DIVERSOS&qw=cIdContrato='" + Cont1 + "' AND cIdTipoRetencion<>1",
					aoColumns: [
						{ sName: "cTipoRetencion"},
						{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  },
						{ sName: "cIdTipoRetencion",	bSearchable: false,	bSortable: false, bVisible: false  }]
                } ) ;
			
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
			p.gestion.setConceptoMov("Aplicación de Contrato Diverso");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
//			p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
			p.gestion.setAplicadoCont("false");
			

//			var nretval = cmdGuardar();
			valida_campos = cmdGuardar();
//			if (nretval == -1) {
				//alert('error al guardar información');
//				return false;
//			}
		}
		catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
  	}
		
	function onLoadPlantilla(){
	querySelectPost("EntidadFederativaRead", "cEstadoFiscal", {async : false});		
		
		if(<%=id_oper == 1%>){
			parent.document.getElementById("pb_send").disabled = false;
 			parent.document.getElementById("pb_send").style.visibility='hidden';
 			$("#cIdContrato").val("<%=folioSeq%>"+$('#cEjercicio').val());
 		}
		
		if(<%=id_oper == 2%>){
			cambiaConsulta();
			var entidadFed = $("#cEntidadFederativa").val();
			if(entidadFed != ""){
				$("#cEstadoFiscal").val(entidadFed);
			}
			
			document.getElementById("iconoPDF").style.visibility = 'visible';
			parent.document.getElementById("pb_cancel").disabled=true;
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			
		}
		
		
		
		
  	}
		
	function ResponsableSiguiente( id_oper ){
	
		return "CONSULTA_CONTRATODIVERSO";
	}
	
	function OperacionSiguiente( id_oper ){
	
		return "consulta_contrato";
	}
	
	function onPostDisplay( id_oper ){
	
		parent.document.getElementById("pb_send").click();
	}
	
	function onPostSubmit( id_oper ){
		return true;
	}

		function Sinfrmt( fld )
	{
	   var valcol = fld.value ;
	   valcol = valcol.replace("$", "");
	   valcol = valcol.replace(",", "");
	   $("#" + fld.id).val( valcol );
}

	function cambiafrmt( fld )
	{
	    $("#" + fld.id).formatCurrency();
	}

	function Grid()
	{
		window.open('MultiReporteGrid.jsp?modulo=CONTRATODIVERSO&id=<%=request.getParameter("id")%>&cuentaDisponible='+$("#cuentaDisponible").val(), 'MultiReporteGrid', 'status=1, width=900px, height=680px, left=100px');
	    if ($.trim(document.FormContrato.ep.value)!=""){
			rellenaCampos();
		}
	 	return false;
	}
				$( "#ep" )
				.change(function() {
				rellenaCampos();
				});
				
function rellenaCampos() {
//					querySelectPost("catalogoClaveCNA2Read", "ClaveCNA");
					<%for (int i = 0; i < Control.length; i++) {%>
						$('#nOrden').val('<%=i + 1%>');
						<%if ((i + 1) != 3 && (i + 1) != 9 && (i + 1) != 10
						&& (i + 1) != 14 && (i + 1) != 15 && (i + 1) != 16) {%>
							querySelectPost("catalogoEPRead", "<%=Control[i]%>");
							$("#<%=Control[i]%>").attr("disabled", true);
						<%} else {%>
							$("#h<%=Control[i]%>").attr("disabled", true);
						<%}%>
					<%}%>
					//$("#hClaveCNA").attr("disabled", true);
            }


		
</script>

<script type="text/javascript">
  $(document).ready(
   function() {
    $("input.AyudaSyC").subIniciaDlg(); 
    $("input.autoCompletaSyC").subIniciaAutoCompleta();
   }
  )
  
   function currencyFormat(fld, milSep, decSep, e) {
    var sep = 0;
    var key = '';
    var i = j = 0;
    var len = len2 = 0;
    var strCheck = '0123456789';
    var aux = aux2 = '';
    var whichCode = (window.Event) ? e.which : e.keyCode;
    if (whichCode == 13) return true; // Enter
	if (whichCode == 8) return true; //Backspace
    if (whichCode == 127) return true; //suprimir
    key = String.fromCharCode(whichCode);
    if (strCheck.indexOf(key) == -1) return false; // Valida que sea numero
    len = fld.value.length;
    for(i = 0; i < len; i++)
     if ((fld.value.charAt(i) != '0') && (fld.value.charAt(i) != decSep)) break;
    aux = '';
    for(; i < len; i++)
     if (strCheck.indexOf(fld.value.charAt(i))!=-1) aux += fld.value.charAt(i);
    aux += key;
    len = aux.length;
    if (len == 0) fld.value = '';
    if (len == 1) fld.value = ''+ decSep + '' + aux;
    if (len == 2) fld.value = '0'+ decSep + aux;
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
    
  function onlyNumbers(evt)
      {
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
	 } || keyPressed > 90*/
	return !(keyPressed > 31 && (keyPressed < 45) && keyPressed != 209 && keyPressed != 95);
}

function LetrasNums(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode

	if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241 || keyPressed == 17){
		alert ("Solo se permiten Letras y Numeros");
	}
	 
	if(  keyPressed == 61 || keyPressed == 63 || keyPressed == 62 ||
	 	 keyPressed == 59 || keyPressed == 58 || keyPressed == 60 ||
	 	 keyPressed == 91 || keyPressed == 92 || keyPressed == 93 ||
	 	 keyPressed == 94 || keyPressed == 95  ){
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

function muestraDispRadicado(){
	if (!confirm('Si desea activar o desactivar el check se perderan las ep´s agregadas, ¿desea continuar?')) {
		activacheck();
		return;
	}
	
	$('#dt_clavepresup').dataTable().fnClearTable();
	$("#cuentaDisponible").val('82106');
	$("#isRadicado").val(0);
	
	if($('#checkDispRadicado').is(':checked')){
		$("#cuentaDisponible").val('82109');
		$("#isRadicado").val(1);
	}
}

function activacheck(){
	$("#checkDispRadicado").attr("checked",false);
	if($("#cuentaDisponible").val()=='82109'){
		$("#checkDispRadicado").attr("checked",true);
	}
}

						
						
		function cmdImprimir(){
		//alert($("#cIdContrato").val());
			window.open(
						"../admin/SeguridadCatalogos?"
							+ "catalogo=REPORTE"
							+ "&accion=run"
							+ "&rn=reporteContratoDiverso.jasper"
							+ "&cIdContrato=" + $("#cIdContrato").val(),
						"popacuse",
						"scrollbars=1, resizable=yes, width=1024, height=768");
		}
		
		//Nuevos Campos Contrato de Arrendamiento
		
	function esRequerido( o, n) {//Funcion para validar si un campo es requerido
				var sTemp = $.trim(o.val());
				o.val(sTemp);
				if ( sTemp.length == 0  ) {
					o.addClass( "ui-state-error" );
					alert(  n + " es un dato requerido." );
					o.focus();
					return false;
				} else {
					o.removeClass( "ui-state-error" );
					o.focus();
					return true;
				}
	}
	

	function validaCamposRequeridos(){//campos nuevos requeridos  para el contrato de Arrendamiento
		var esValido = true;
		esValido=esValido && esRequerido($("#cDireccion"),"Direccion");
		esValido=esValido && esRequerido($("#cContrato"),"No. de Contrato");
		esValido=esValido && esRequerido($("#cConceptoContrato"),"Nombre del Inmueble");
		esValido=esValido && esRequerido( $("#cSuperficie"),"Superficie");
		esValido=esValido && esRequerido($("#cMensualidad"),"Mensualidad");
		return esValido;
	}
	  	
	 function cambiaConsulta(){//Solo lectura Para consulta del Contrato
	 	document.getElementById("cDireccion").className = "notEditable";
		document.getElementById("cDireccion").readOnly = true;
		document.getElementById("cContrato").className = "notEditable";
		document.getElementById("cContrato").readOnly = true;
		document.getElementById("cConceptoContrato").className = "notEditable";
		document.getElementById("cConceptoContrato").readOnly = true;
		document.getElementById("cSuperficie").className = "notEditable";
		document.getElementById("cSuperficie").readOnly = true;
		document.getElementById("cMensualidad").className = "notEditable";
		document.getElementById("cMensualidad").readOnly = true;
		document.getElementById("cnombre").className = "notEditable";
  	}
	
	function validaFechaTermino(){
	 	var bRegresa = true;   
	    var fecha1 = $("#fContratoIni").val(); 
	    var fecha2 = $("#fContratoFin").val();
	    var x = fecha1.split("/");
	    var z = fecha2.split("/");
	    
	    fecha1 = x[2]+x[1]+x[0];
	    fecha2 = z[2]+z[1]+z[0];
	
	    //Comparamos las fechas
	    if (fecha1 >= fecha2){
	    	alert("La fecha Termino no puede ser menor ó igual a la fecha Inicio. Verifique!!");
	        bRegresa = false;
	    }
	    
	    return bRegresa;
	}
 </script>


</head>

<body id="dt_example">
	<form action="#" name="FormContrato">
		<div id="container" class="container" style="width:70%">
			<div class="card-header"> <h3> Contrato Diversos </h3> </div>
			<hr class="mt-3"/>
			
			<input type="hidden" name="cEntidadFederativa" id="cEntidadFederativa" value="">
			
			<div class="row">									
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="cIdContrato"> Número de Documento: </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" id="cIdContrato" name="cIdContrato" class="form-control form-control-sm obligatorio" onKeyPress="return Sinespacios(event)" onBlur="no_foco(this);" readonly />
				</div>											
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<label for="folio"> Folio: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					<input type="text" id="folio" name="folio" class="form-control form-control-sm" readonly value="0" />
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<div id="iconoPDF" style="visibility:hidden">
						<div class="row">									
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">										
								<img src="../imagenes/icono_PDF.jpg" onClick="cmdImprimir()">PDF	
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<h5> Propietario </h5>
			<hr class="mt-3">
			
			<input type="hidden" name="cEjercicio" id="cEjercicio" value=""/> 
			<input type="hidden" name="cIdUsuarioResponsable" id="cIdUsuarioResponsable" value="Usuario"/> 
			<input name="OPERADOR" type="hidden" id="OPERADOR" value="<%=usuario.getNombre()%>"/> 
			<input name="UsrLogin"	type="hidden" id="UsrLogin" value="<%=usuario.getLogin()%>"/> 
			<input name="FECHA_CARGA" type="hidden" id="FECHA_CARGA" value="<%=today%>"/>
			<input type="hidden" name="cTContrato" id="cTContrato" value="DI"/>
			<input type="hidden" name="LHaySaldoAnticipo" value=""/> 
			<input type="hidden" id="nOrden" name="nOrden" value="" /> 
			<input type="hidden" id="Partida" name="Partida" value="32101" /> 
			<input type="hidden" id="Partida2" name="Partida2" value="32201" /> 
			<input type="hidden" id="Partida3" name="Partida3" value="33901" /> 
			<input type="hidden" id="Partida4" name="Partida4" value="31401" /> 
			<input type="hidden" id="Partida5" name="Partida5" value="31501" /> 
			<input type="hidden" id="Partida6" name="Partida6" value="31701" /> 
			<input type="hidden" id="Partida7" name="Partida7" value="32301" /> 
			<input type="hidden" id="Partida8" name="Partida8" value="32701" /> 
			<input type="hidden" id="Partida9" name="Partida9" value="33301" /> 
			<input type="hidden" id="Partida10" name="Partida10" value="35301" /> 
			<input type="hidden" id="Partida11" name="Partida11" value="X" /> 
			<input name="cIdEntidadContable" type="hidden" id="cIdEntidadContable"	value="<%=cCentroContable%>"/> 
			<input name="CamInst"	type="hidden" id="CamInst" value=""/> 
			<input name="cIdTipoContratoDiverso" type="hidden"	id="cIdTipoContratoDiverso" value="6"/> 
			<input type="hidden" id="nFolioJur1" name="nFolioJur1" /> 
			<input type="hidden" id="cIdContrato1" name="cIdContrato1" /> 
			<input type="hidden" id="cIDRFCSesion1" name="cIDRFCSesion1" /> 
			<input type="hidden" id="fSesion1" name="fSesion1" /> 
			<input type="hidden" name="cDocumento" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/> 
			<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" /> 
			<input type="hidden" name="isRadicado" id="isRadicado" value="0" /></td>
			
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<label for="cIdRFC"> RFC: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<div class="input-group">																		
						<input type="text" name="cIDRFC" id="cIDRFC" class="form-control form-control-sm AyudaSyC obligatorio" onChange="no_foco(this);" readonly/>
					</div>
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1"> 
					<input type="text" name="cnombre" id="cnombre" class="form-control form-control-sm" onChange="no_foco(this);" readonly/>							
				</div>	
			</div>
			
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<label for="cDireccion"> Direción: </label>
				</div>
				<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">																			
					<input type="text" name="cDireccion" id="cDireccion" class="form-control form-control-sm"/>
				</div>	
			</div>
			
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<label for="cEstadoFiscal"> Entidda Federativa: </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">																			
					<select id="cEstadoFiscal" name="cEstadoFiscal" class="form-select form-select-sm"></select>
				</div>	
			</div>
			
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">							
					<label for="cContrato"> # Contrato: </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">																			
					<input type="text" name="cContrato" id="cContrato" maxlength="100" class="form-control form-control-sm"/>
				</div>	
			</div>
			
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					Tipo de Documento Diverso:																			
					<select id="cIdTipoContratoDiv" name="cIdTipoContratoDiv" class="form-select form-select-sm" disabled="disabled">
						<option value="6"></option>
					</select>
				</div>	
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
					Unidad Administrativa:																			
					<select id="cIdUnidadAdministrativa" name="cIdUnidadAdministrativa" class="form-select form-select-sm">
						<option value="B01">Direccion gral</option>
					</select>
				</div>	
			</div>
			
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">						
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
					Tipo de Adjudicaci&oacute;n:																			
					<select id="cIdTipoAdjudicacion" name="cIdTipoAdjudicacion" class="form-select form-select-sm">
						<option value="0">cero</option>
					</select>
				</div>	
			</div>
			
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					Plurianual: <input type="checkbox" name="lEsPlurianual" id="lEsPlurianual" class="form-check-input">
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					Esquema de Precios:																			
					<select id="id_esquema" name="id_esquema" class="form-select form-select-sm">						
					</select>
					<input type="hidden" name="caNoCompromiso" id="caNoCompromiso" value="0">
				</div>	
			</div>
			
		</div>
		
		<div id="containerF" class="container">
			<div class="row" >					
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					Fecha Adjund. 
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						 <input type="text" class="form-control form-control-sm" id="fAdjudicacion" name="fAdjudicacion" onChange="no_foco(this);" />
					</div>
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					Fecha Registro
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						<input type="text" class="form-control form-control-sm" id="fDocumento" name="fDocumento" onChange="no_foco(this);"/>
					</div> 
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					Fecha Contrato.
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						<input type="text" class="form-control form-control-sm" id="fFirmaContrato" name="fFirmaContrato" onChange="no_foco(this);"/>
					</div> 
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					Fecha Inicio
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						<input type="text" class="form-control form-control-sm" id="fContratoIni" name="fContratoIni" onChange="no_foco(this);"/>
					</div> 
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					Fecha T&eacute;rmino
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						<input type="text" class="form-control form-control-sm" id="fContratoFin" name="fContratoFin" onChange="no_foco(this);"/>
					</div>
				</div>	
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
				</div>
			</div>	
		</div>
				
		<div id="containerT" class="container" style="width: 80%">
			<div id="multitabs">	
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
						<div class="row" >
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>													
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
								Nombre del Inmueble:
								<textarea id="cConceptoContrato" class="form-control form-control-sm" onkeydown="return LetrasNums(event)" name="cConceptoContrato" style="height: 140px; width: 400px"></textarea>
							</div>	
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<div class="row" >													
									<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
										Tipo Fondo:
										<select name="cIdTipoFondo" id="cIdTipoFondo" class="form-select form-select-sm">
											<option value="0">______________</option>
										</select>
									</div>
								</div>
								<div class="row" >													
									<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
										Tipo Moneda:
										<select name="cIdTipoMoneda" id="cIdTipoMoneda" class="form-select form-select-sm">
											<option selected value="0">PESO MEXICANO</option>
											<option>DOLAR ESTADOUNIDENSE</option>
										</select>
									</div>
								</div>
								<div class="row" >													
									<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
										% IVA Aplicable:
										<select name="nPorcIVAAplicable" id="nPorcIVAAplicable" class="form-select form-select-sm">
											<option selected value="0">0%</option>
										</select>
									</div>
								</div>
							</div>	
						</div>
						
						<div class="row" >	
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>												
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<label for="cSuperficie"> Superficie: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="cSuperficie" id="cSuperficie" maxlength="20" class="form-control form-control-sm"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<label for="cIdTipoPropiedad"> Tipo Propiedad: </label>
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<select id="cIdTipoPropiedad" name="cIdTipoPropiedad" class="form-select form-select-sm">
										<option id="1" value="1" selected>N/A</option>
										<option id="2" value="2">indefininido</option>
								</select>
							</div>						
						</div>							
											
						<div class="row" >													
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<label for="mImporteContrato"> Monto Contrato: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="mImporteContrato" id="mImporteContrato" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this); no_foco(this);"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="mImporteBruto"> Monto Bruto: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="mImporteBruto" id="mImporteBruto" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<label for="mContratoMN"> Monto Contrato Pesos: </label>
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																											
								<input type="text" name="mContratoMN" id="mContratoMN" class="form-control form-control-sm" style="text-align:right;" onKeyPress="return onlyNumbers(event)" readonly/>								
							</div>						
						</div>
						
						<div class="row" >													
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<label for="mImporteIVA"> Monto IVA: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="mImporteIVA" id="mImporteIVA" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="mImporteTotal"> Monto Total: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="mImporteTotal" id="mImporteTotal" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)" readonly/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<label for="mContratoME"> Monto Contrato(M.E.): </label>
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																											
								<input type="text" name="mContratoME" id="mContratoME" class="form-control form-control-sm" style="text-align:right;"/>								
							</div>						
						</div>
						
						<div class="row" >													
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<label for="cMensualidad"> Mensulidad: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="cMensualidad" id="cMensualidad" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/>
								<input type="hidden" maxlength="12" size="12" value="0" name="mImporteViaticos" id="mImporteViaticos" style="text-align:right;" onKeyPress="return onlyNumbers(event)">
								<input type="hidden" maxlength="12" size="12" value="0" name="mImporteHonorarios" id="mImporteHonorarios" style="text-align:right;" onKeyPress="return onlyNumbers(event)">
							</div>
						</div>
					</div> <!-- Fin tabs-1 -->
	
					<div id="tabs-2">						
						<div class="row" >													
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								% Anticipo												
								<input type="text" name="nPorcAsignacion" id="nPorcAsignacion" class="form-control form-control-sm" value="0" maxlength="2" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/>
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								Amortización																			
								<input type="text" name="nPorcAmortizacion" id="nPorcAmortizacion" class="form-control form-control-sm" value="0" maxlength="2" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/>
							</div>
						</div>
						
						<br/>
						
						<div class="row" >													
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<label for="mImporteAnticipo" id="LabelBruto"> Importe Bruto: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="mImporteAnticipo" id="mImporteAnticipo" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="mImporteAnticipoIVA" id="LabelIVAAnt"> Iva Anticipo: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="mImporteAnticipoIVA" id="mImporteAnticipoIVA" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<label for="mTotalAnticipo" id="LabelTotalAnt"> Total Anticipo: </label>
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																											
								<input type="text" name="mTotalAnticipo" id="mTotalAnticipo" class="form-control form-control-sm" style="text-align:right;"/>								
							</div>						
						</div>
						
						<div class="row" >													
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">							
								<label for="mImporteAnticipo" id="LabelAmortiza"> Importe Amortizado: </label>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																			
								<input type="text" name="mAmortizado" id="mAmortizado" class="form-control form-control-sm" value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)"/>
							</div>
						</div>													
					</div> <!-- FIN tabs-2 -->
	
					<div id="tabs-3">											
						<div class="row" >
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<select id="cIdTipoRetencion" name="cIdTipoRetencion" class="form-select form-select-sm" onchange="fn_actret('cIdTipoRetencion', 'nPorcRetencion')">
									<option selected value="1">SIN RETENCION</option>
								</select>
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" value="Agregar" name="Add1" id="Agregar" onclick="fnAgregarRet();" class="btn btn-secondary btn-sm"/>
							</div>
						</div>		
						
						<div id="divMilla2">
							<div class="row" >
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								</div>
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
									<label> Camara : <input name="grpMilla2" type="radio" id="grpMilla2" onclick="habilitaMillar2(0)" value="Camara" class="form-check-input"></label> 
									<label> Instituto : <input type="radio" id="grpMilla2" name="grpMilla2" onclick="habilitaMillar2(1)" value="Instituto" class="form-check-input"></label>
								</div>															
							</div>							
						</div>
						
						<div class="row" >
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
								<input type="hidden" name="nPorcRetencion" id="nPorcRetencion" value="">
								<table id="dt_retencion" class="display">
									<thead>
										<tr align="center">
											<th>Clave de retenci&oacute;n</th>
										</tr>
									</thead>									
								</table>
							</div>							
						</div>										
					</div> <!-- FIN tabs-3 -->
	
					<div id="tabs-4">						
						<div id="divRadicado">
							<div class="row">																			
								<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">																
									Disponible Radicado:
									<input type="checkbox" name="checkDispRadicado" id="checkDispRadicado" onclick="muestraDispRadicado()" class="form-check-input"/>
								</div>								 								 																		 							 								
							</div>
						</div>
						
						<div class="row">												
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
							</div>							
							<div class="col-12 col-lg-7 col-md-7 col-sm-12 d-flex p-1">																
								E.P. &nbsp;
								<div class="input-group">								
									<input type="text" id="ep" name="ep" size="61" readonly class="form-control form-control-sm" readonly/>
									<input type="button" name="nIdClaveEgresosX" id="nIdClaveEgresosX" size="5" value="..." onclick="Grid()" onblur="rellenaCampos();" class="btn btn-secondary btn-sm"/>
								</div>
							</div>		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">	
								<input type="button" value="Agregar" name="Add2" id="Agregar2" onclick="fnClickAddRowC();" class="btn btn-secondary btn-sm"/>
							</div>						 								 																		 							 								
						</div>
						
						<div class="row" >
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
								<input type="hidden" name="nPorcRetencion" id="nPorcRetencion" value="">
								<table id="dt_clavepresup" class="display">
									<thead>
										<tr align="center">
											<th>E.P.</th>
											<th>Clave Interna</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
									</tbody>								
								</table>
							</div>							
						</div>						
					</div> <!-- FIN tabs-4 -->
	
					<div id="tabs-5">
						<div class="row">												
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
							</div>							
							<div class="col-12 col-lg-7 col-md-7 col-sm-12 d-flex p-1">																
								Cesion de derechos a: &nbsp;<input type="button" id="pbCesionD" value="Agregar Cesión" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
						
						<div class="row" >
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">								
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
							</div>							
						</div>											
					</div> <!-- FIN tabs-5 -->
					
					<div id="tabs-6">
						<div class="row" >
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">								
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
					</div> <!-- FIN tabs-6 -->
		
				</div>
			</div>
		</div>
		
		<div id="dialog-Cesion" title="Cesión de Derechos">
			<div class="row" >
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="input-group">
						<input type="text" maxlength="15" size="15" name="cIDRFCSesion" ID="cIDRFCSesion" readonly class="form-control form-control-sm AyudaSyC obligatorio" />
					</div>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
					<input type="text" readonly maxlength="60" size="60" id="NombreSesion" name="NombreSesion" style="text-align:left;" class="form-control form-control-sm">
				</div>
			</div>
			
			<div class="row" >
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" maxlength="30" size="30" value="" id="nFolioJur" name="nFolioJur" style="text-align:left;" class="form-control form-control-sm" >
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						<input type="text" readonly maxlength="10" size="10" id="fSesion" name="fSesion" style="text-align:center;" class="form-control form-control-sm">
					</div>					
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="pbInactiva" style="visibility: hidden" value="DesActivar" class="btn btn-secondary btn-sm"/>
				</div>
			</div>
			
						
			<table border="0">				
				<tr>
					<td><input type="button" id="pbInactiva" style="visibility: hidden" value="DesActivar" class="btnInterfaceBG"/>
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</div>

	</form>


</body>
</html>
