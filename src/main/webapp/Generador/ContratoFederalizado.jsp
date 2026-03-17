<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
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
	
/*para saber si esta activo el radicado*/
ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
boolean radicadoActivo = "S".equalsIgnoreCase(  cabl.getSystemSetting("MUESTRA_RADICADO_RGOC") ); 
String radActivo = "";
if(radicadoActivo){
	radActivo = "S";
}else{
	radActivo = "N";
}	

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
		
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

        <script type="text/javascript" src="../Generador/js/jquery-3.5.1.min.js"></script>
        <script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="../Generador/js/Moment.js"></script>
        <script type="text/javascript" src="../Generador/js/jquery-ui.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
        <script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
        <script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="js/ContratoFederalizado.js"></script>
		<script type="text/javascript" src="js/Contratos.js"></script>
        <script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
        <script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
        <script type="text/javascript" src="js/jquery.blockUI-2.70.0.js" charset="UTF-8"></script>
        
		<script type="text/javascript" charset="utf-8">		
			
	var rowCount = 0 ;
	var nSesion = 0;
	var bCarga = false;
	var bAgregar = false;
	var fechaDia =  new Date();	
	var operacion = <%=id_oper%>;
	var folio = "<%=c.getFolio()%>";
	var Cont1 ;
	var idCC ;
	let modalEP;
	var fechaStr = (fechaDia.getDate() < 10 ? "0" + fechaDia.getDate(): fechaDia.getDate() ) + "/" + ( fechaDia.getMonth() + 1 < 10 ? "0" + (fechaDia.getMonth() + 1) : (fechaDia.getMonth() + 1) ) + "/" + fechaDia.getFullYear();
	
	$(document).ready(function() {
		
			$('#contrato-list a').on('click', function (e) {
			  e.preventDefault()
			  $(this).tab('show')
			})

			//creaDiaglogoDisp();
			$('.currency').blur(function(){
				$('.currency').formatCurrency();
			});
			$(".fecha").each(function(elemento){
				$(this).val(fechaStr);
			});
			$('#cObjetoContrato').bind('copy paste', function (e) {       
				e.preventDefault();
		    });

			init();	
			
			$('#btnAceptar').hide();
		
			modalEP = new bootstrap.Modal(document.getElementById('dialog-form-ep'), 'data-bs-backdrop');
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
							queryFormPost("pContratoFederalizadoSesionCreate", {async: false });
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
			

			cargaInicial();
			creaTablaRetencion();
			
			var Caso = <%=request.getParameter("folio")%>;
     		$("#folio").val(Caso);
			queryFormPost("BuscaFederalizadosPrincipalRead", {async: false });
			queryFormPost("RFCObraDivRead", {async: false });
			
			var Pp = $('#Partida').val();
			Cont1 = $('#cIdContrato').val();
				
			if ($('#cIdContrato').val() == ''){
				$("#Delete").attr("disabled", true);		
			}
			else{
				cargaInformacion();
			} 


	var decimal4 = 0;
	var decimal5 = 0;
	var decimal6 = 0;
	var decimal7 = 0;
	var decimal8 = 0;

	
	
	$("#cIdClaseContratoObra").change(
			function() {
				if ($(this).val() < 2 ){
					$("#cIdClaseContratoObra").val( "0" );
					Swal.fire('Seleccione...',"El Documento Solo Puede Ser Anexo Técnico o Convenio", "warning");
				}
				
			});
	

		
					$( "#pbInactiva" )
						.button()
						.click(function() {
							
							$("#cIdContrato1").val($("#cIdContrato").val());
							$("#cIDRFCSesion1").val($("#cIDRFCSesion").val());
							$("#fSesion1").val($("#fSesion").val());
							$("#nFolioJur1").val($("#nFolioJur").val());
							
							queryFormPost("pContratoFederalizadoSesionUpdate", {async : false});
							
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
							
					});

					$("#mAmortizado").change(
						function() {
							decimal6 = parseFloat( Quitafrmt( $(this).val() ) );
							decimal8 = parseFloat( Quitafrmt( $("#mTotalAnticipo").val() ) );
							if ( decimal6 > decimal8) {
								alert("No se puede exceder el total del anticipo")
								$(this).val("0");
							}
							
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
     				var aData = oTableMov.fnGetData( aPos );
					if (bCarga){
						bAgregar = false;
	     				$("#cIDRFCSesion").val( aData[ 0 ]);
						$("#NombreSesion").val( aData[ 1 ]);
						$("#nFolioJur").val( aData[ 2 ]);
						$("#nFolioJur1").val( aData[ 2 ]);
						$("#fSesion").val( aData[ 3 ]);
						$("#pbInactiva").css("visibility","visible");
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
				

				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();

		
		$(".montoCaptura").each(function() {
			$(this).focus( function(){ 
				onFocusMontoCaptura(this.id);
			});
			$(this).change( function(){ 
				onBlurMontoCaptura(this.id);
			});
		});
		

		$('#dt_clavepresup').dataTable( {
			"bPaginate" : false,
			"bFilter" : false,
			"bSort" : false,
			"bInfo" : false,
			//"sScrollY" : "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			//"sPaginationType" : "full_numbers"
		});
	
	});
				
	
function cargaInicial(){
	querySelectPost("CatalogoEsquemaPrecioRead","id_esquema", {async: false });
	querySelectPost("CatalogoTipoContratoObraRead","cIdTipoContratoObra", {async: false });
	$('#cIdTipoContratoObra option[value="4"]').attr('selected','selected'); //URVP.14102014 SE DEJA POR DEFAULT LA OPCION DE CONTRATO DE CONCERTACION VAL=4
	querySelectPost("CatalogoClaseContratoObraRead2","cIdClaseContratoObra", {async: false });
	querySelectPost("TipoFondoRead","cIdTipoFondo", {async: false });
	$('#cIdTipoFondo option[value="FF"]').attr('selected','selected'); //URVP.14102014 SE DEJA POR DEFAULT LA OPCION DE FONDOS FISCALES
	querySelectPost("PorcIVARead","nPorcIVAAplicable", {async: false });
	querySelectPost("CatalogoTipoAnticipoObraRead","cIdTipoAnticipoObra", {async: false });
	querySelectPost("CatalogoTipoRetencionRead","cIdTipoRetencion", {async: false });
	querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
	queryFormPost("cEjercicioRead", {async: false });
	
}
function foco(elemento) {
 elemento.style.border = "1px solid #FF0000";
 }

 function no_foco(elemento) {
 elemento.style.border = "1px solid #CCCCCC";
 }
 
activo=1;
	

function onLoadPlantilla(){

		if(<%=id_oper==1%>){
			parent.document.getElementById("pb_send").disabled = false;
 			parent.document.getElementById("pb_send").style.visibility='hidden';
 			
 			if( esContratoGuardado() ){
 				iniciaCapturaRetenciones();
 				
 			}
 		}
		
		if(<%=id_oper==2%>){
			parent.document.getElementById("pb_cancel").disabled=true;
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
		}
}

function ResponsableSiguiente(id_oper){
	return "CONSULTA_CONTRATOOBRA";
}

function OperacionSiguiente(id_oper){
	return "consulta_contrato";
}

function fnAgregarRet() {
	if ($("#cIdContrato").val()==''){
		Swal.fire("Capturar","Por favor ingrese el Numero de Documento", "info");
		foco(document.getElementById('cIdContrato'));
		return false;
	}


	queryFormPost("validarTipoRetencionContratoRead", {async : false});
			
	if ($("#retencionExiste").val() == 1){
		Swal.fire("Retencion repetida", "Ya se ingresó ese tipo de retención", "error");
		return;
	}

		queryFormPost({
			queryName : "ContratoFederalizadoRetencionCreate",
			async : false,
			callback : function() {
				Swal.fire("OK","Retencion agregada exitosamente.", "success");
			}
		});	
						
		creaTablaRetencion();
}

function creaTablaRetencion() {
	
	var Contrato = $("#cIdContrato").val();
	$("#dt_retencion").dataTable( {
		"bPaginate" : false,
		"bFilter" : false,
		"bSort" : true,
		"bInfo" : false,
		//"bAutoWidth" : false,
		//"sScrollY" : "100%",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bServerSide" : true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=V_CONSULTARETENCIONES_FED&qw=cIdContrato='" + Contrato + "' AND cIdTipoRetencion<>1",	
		aoColumns: [
			{ sName: "cTipoRetencion"},
			{ sName: "cIdTipoRetencion"},
			{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }]
		} ) ;
}

function crearTablaEp() {
	$('#dt_clavepresup').dataTable( {
						bPaginate: false,
	        			bFilter: false,
	        			bInfo: false,
						sScrollY: 100,
						bJQueryUI: true,
						bDestroy : true,
						bServerSide: true,   
						sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vContratoEPs&qw=cIdContrato='" + Cont1 + idCC,
						aoColumns: [
							{ sName: "ClaveSIAFF" },
							{ sName: "ClaveInterna" },
							{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }]
	                } ) ;
	
}

function cargaInformacion() {
			bCarga = true;
			queryFormPost("fInicioFederalizadosRead", {async: false });
			queryFormPost("fFederalizadosObraRead", {async: false });
			queryFormPost("fAdjudicacionFederalizadosRead", {async: false });
			queryFormPost("fFirmaContratoFederalizadosRead", {async: false });
			queryFormPost("fAnticipoFederalizadosRead", {async: false });			
			queryFormPost("BuscaAnticipoFederalizadosRead", {async: false });
			 
			Cont1 = $('#cIdContrato').val();
			idCC = "' and cIdEntidadContable = '" + $("#cIdEntidadContable").val() + "'"; 

			crearTablaEp();
			
			creaTablaRetencion();
			
              
				deshabilitaCaptura();
				if (<%=id_oper%>=="1"){
					parent.document.getElementById("pb_save").disabled = true;	
				}
				
				//$("#montoTotalCnt").val($("#mTotal").val());
				actualizaMontosCalendarizados();
			}


			


			function fnClickAddRowC() {
				rowCount = $('#dt_clavepresup tr').length;
				var vep = $('#ep').val();
				if (vep == '') {
					return ;
				}

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
			
				$('#dt_clavepresup').dataTable().fnAddData( [
						'<td><input type="text" maxlength="25" size="65" name="ClaveSIAFF" 	readonly  id="ClaveSIAFF" value="' + vep + '" style="background-color:#E0E0F8; text-align:center; border:1px solid #E0E0F8"></td>',
						'<td><input type="text" maxlength="10" size="8" name="ClaveInterna" readonly id="ClaveInterna" value="' + cint + '" style="background-color:#E0E0F8; text-align:center; border:1px solid #E0E0F8">',
						'<td><input type="hidden" maxlength="5" size="5" name="nIdClaveEgresos" readonly id="nIdClaveEgresos" value="' + $('#ep').val() + '" style="background-color:#E0E0F8; text-align:center; border:1px solid #E0E0F8"></td>'+
						'<input type="hidden" name="cEjercicioTbl" value="'+$('#cEjercicio').val()+'">' +
						'<input type="hidden" name="cIdContratoTbl" value="'+$('#cIdContrato').val()+'">' +
						'<input type="hidden" name="cTContratoTbl" value="'+$('#cTContrato').val()+'">' +
						'<input type="hidden" name="cIdEntidadContableTbl" value="'+$('#cIdEntidadContable').val()+'">' +
						'</td>'
					] );

				$('#ep').val("");
			}
			
		$(function() {
			$( "#fAdjudicacion" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
			});
		});


		$(function() {
			$( "#fFirmaContrato" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
			});
		});
		$(function() {
			$( "#fInicio" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
			});
		});
		$(function() {
			$( "#fTermino" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
			});
		});
		$(function() {
			$( "#fAnticipo" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
			});
		});

		$(function() {
			$( "#fSesion" ).datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
			});
		});

	function cambiafrmt( fld )
	{
	    $("#" + fld.id).formatCurrency();
	}

	function Grid()
	{
		window.open('MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGrid', 'status=1, width=900px, height=680px, left=100px');
		if ($.trim(document.FormContrato.ep.value)!=""){
			rellenaCampos();
		}
		
	 	return false;
	}
		
	$( "#ep" ).change(function() {
				rellenaCampos();
				});

	function rellenaCampos() {

					<%for (int i = 0; i < Control.length; i++) {%>
						$('#nOrden').val('<%=i+1%>');
						<%if ((i+1)!=3 && (i+1)!=9 && (i+1)!=10 && (i+1)!=14 && (i+1)!=15 && (i+1)!=16){%>
							querySelectPost("catalogoEPRead", "<%=Control[i]%>");
							$("#<%=Control[i]%>").attr("disabled", true);
						<%}else{%>
							$("#h<%=Control[i]%>").attr("disabled", true);
						<%}%>
					<%}%>
					
     }
	

function Sinespacios(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	return !(keyPressed > 31 && (keyPressed < 45) && keyPressed != 209 && keyPressed != 95);
}

function LetrasNums(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241 || keyPressed == 17){
	Swal.fire("Verifique","Solo se permiten Letras y Numeros","info");
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


function actualiza(){
	$("#cIDRFC").val($("#cIDRFC_contrato").val());
	
	cargaInicial();
	
}


function validaFechaTermino(){
 	var bRegresa = true;   
    var fecha1 = $("#fInicio").val(); 
    var fecha2 = $("#fTermino").val();
    var x = fecha1.split("/");
    var z = fecha2.split("/");
    
    fecha1 = x[2]+x[1]+x[0];
    fecha2 = z[2]+z[1]+z[0];

    //Comparamos las fechas
    if (fecha1 >= fecha2){
    	Swal.fire("Verifique","La fecha Termino no puede ser menor ó igual a la fecha Inicio.", "warning");
        bRegresa = false;
    }
    
    return bRegresa;
}
</script>
</head>

<body id="dt_example" >
	<form action="#" name="FormContrato">
		<input type="hidden" name="compromisos" id="compromisos" value="false"/>	
		<input type="hidden" name="EPBusqueda" id="EPBusqueda" value="false"/>
		<input type="hidden" name="esContratoGuardado" id="esContratoGuardado" value="false"/>
		<input type="hidden" name="contratoDirecto" id="contratoDirecto" value=""/>
		<input type="hidden" name="existeCont" id="existeCont" value=""/>
		<input type="hidden" name="faltanteCalendario" id="faltanteCalendario" value=""/>
		<input type="hidden" name="cIDRFC" id="cIDRFC" value=""/>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value=""/>
		<input type="hidden" name="cTContrato" id="cTContrato" value="FD"/>
		<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=usuario.getNombre()%>" />
		<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>"/>
		<input type="hidden" name="UsrLogin" id="UsrLogin" value="<%=usuario.getLogin()%>" />	
		<input type="hidden" name="cEjercicioTbl2" value=""/>
		<input type="hidden" name="cIdEntidadContableTbl2" value=""/>
		<input type="hidden" name="cIdContratoTbl2" value="0"/>
		<input type="hidden" name="LHaySaldoAnticipo" value=""/>		
		<input type="hidden" id="nOrden" name="nOrden" value=""/>
		<input type="hidden" id="Partida" name="Partida" value="6"/>
		<input type="hidden" id="Partida2" name="Partida2" value=""/>
		<input type="hidden" id="Partida3" name="Partida3" value=""/>					
		<input type="hidden" id="Partida4" name="Partida4" value=""/> 
		<input type="hidden" name="CamInst" id="CamInst" value=""/>
		<input type="hidden" id="cDocumento" name="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
		<input type="hidden" name="cIdEntidadContable" id="cIdEntidadContable" value="<%=cCentroContable%>"/>
		<input type="hidden" name="ValidacionBorrar1" id="ValidacionBorrar1" />
		<input type="hidden" id="nFolioJur1" name="nFolioJur1"/>
		<input type="hidden" id="cIdContrato1" name="cIdContrato1"/>
		<input type="hidden" id="cIDRFCSesion1" name="cIDRFCSesion1"/>
		<input type="hidden" id="fSesion1" name="fSesion1"/>
		<input type="hidden" id="nPorcAsignacion" name="nPorcAsignacion" value="0"/>
		<input type="hidden" id="nPorcAmortizacion" name="nPorcAmortizacion" value="0"/>
		<input type="hidden" name="mImporteAnticipo" id="mImporteAnticipo"  value="0"/>
		<input type="hidden" name="mImporteAnticipoIVA" id="mImporteAnticipoIVA"  value="0"/>
		<input type="hidden" name="mTotalAnticipo" id="mTotalAnticipo"  value="0"/>
		<input type="hidden" name="mAmortizado" id="mAmortizado"  value="0"/>
		<input type="hidden" name="mOAE" id="mOAE" value="0.00"/>
		<input type="hidden" id="radicadoActivo" name="radicadoActivo" value="<%=radActivo%>"/>
		<input type="hidden" name="retencionExiste" id="retencionExiste"  value="0"/>
		<input type="hidden" name="cIdTipoAdjudicacion" id="cIdTipoAdjudicacion" value="" >
		<input type="hidden" name="lEsPlurianual" id="lEsPlurianual" value=""/>
		<input type="hidden" name="cTipoRfc" id="cTipoRfc" value=""/>
							
		<div id="container" class="container">
			<h1>Federalizados (Subsidio)</h1>
				<div class= "row">
					<div class="col-4">
						Número de Documento:
						<input type="text" style="text-transform:uppercase" maxlength="30" size="30" name="cIdContrato"	ID="cIdContrato" class="obligatorio form-control" onKeyPress="return Sinespacios(event)" onBlur="no_foco(this);"/>
					</div>
					<div class="col-6">
						
					</div>
					<div class="col-2">
						No. Folio.:
  					    <input readonly name="folio" type="text" id="folio" size="4" style="text-align:right;" style="background-color:#CCCCCC;" style="text-transform:uppercase" value="1" class="form-control"/>
					</div>
				</div>
				<div class= "row">
					<div class="col-4">
						R.F.C:
						<div class="input-group">
							<input type="text" name="cIDRFC_contrato" ID="cIDRFC_contrato" readonly class="AyudaSyC  obligatorio form-control" onChange="no_foco(this);actualiza();"/>
						</div>
					</div>
					<div class="col-8">
						Nombre
  					    <input readonly type="text" name="cnombre" ID="cnombre" onBlur="no_foco(this);" class="form-control"/>
					</div>
				</div>
				<div class= "row">
					<div class="col-12">
						Unidad Administrativa:
  					    <select name="cIdUnidadAdministrativa" id="cIdUnidadAdministrativa" class="form-select"><option value="<%=cUR%>"></option></select>
					</div>
				</div>
				<div class= "row">
					<div class="col-4">
						Tipo Documento:
						<select name="cIdClaseContratoObra" id="cIdClaseContratoObra" class="form-select"></select>
					</div>
					<div class="col-6">
						Clase Documento:
  					    <select name="cIdTipoContratoObra" id="cIdTipoContratoObra" class="form-select"><option selected value=""></option></select>
  					    <select name="id_esquema" id="id_esquema" style="visibility: hidden"></select>
					</div>
					
				</div>

				<div class="row">
					<div class="col-3">
						Fecha de Registro:
						<div class="input-group">
					      	<span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
					      	<input type="input" readonly id="fAdjudicacion" name="fAdjudicacion" class="obligatorio fecha form-control" style="text-align:center;" onChange="no_foco(this);"/>
						</div>
					</div>
					<div class="col-3">
						Firma Anexo/Convenio:
						<div class="input-group">
					      	<span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
					      	<input type="input" readonly id="fFirmaContrato"	name="fFirmaContrato" class="obligatorio fecha form-control" style="text-align:center;" onChange="no_foco(this);" />
						</div>
					</div>	
					<div class="col-3">
						Fecha Inicio Acciones:
						<div class="input-group">
					      	<span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
					      	<input type="input" readonly id="fInicio" name="fInicio" class="obligatorio fecha form-control" style="text-align:center;" onChange="no_foco(this);"/>
						</div>
					</div>	
					<div class="col-3">
						Fecha Término Acciones:
						<div class="input-group">
					      	<span class="input-group-text"><i class="bi bi-calendar3"></i></span> 
					      	<input type="input" readonly id="fTermino" name="fTermino" class="obligatorio fecha form-control" style="text-align:center;" onChange="no_foco(this);"/>
						</div>
					</div>		
				</div>
		</div>
		<div class="container row d-flex justify-content-center" align ="center">
				<div class="col-md-12">
			      <div class="card">
			        <div class="card-header">
			          <ul class="nav nav-tabs card-header-tabs" id="contrato-list" role="tablist">
			            <li class="nav-item">
			              <a class="nav-link active" href="#tabs-1" role="tab" aria-controls="generales" aria-selected="true">Generales</a>
			            </li>
			            <li class="nav-item">
			              <a class="nav-link"  href="#tabs-4" role="tab" aria-controls="clavesPresupuestales" aria-selected="false">Claves Presupuestales</a>
			            </li>
			            <li class="nav-item">
			              <a class="nav-link" href="#tabPresupuesto" role="tab" aria-controls="Calendario" aria-selected="false" style="display:none">Calendario Presupuesto</a>
			            </li>
			            <li class="nav-item">
			              <a class="nav-link" href="#tabRetencion" role="tab" aria-controls="Retenciones" aria-selected="false" style="display:none">Retenciones</a>
			            </li>
			          </ul>
			        </div>
			        <div class="card-body">
			           <div class="tab-content mt-3">
			            	<div class="tab-pane active" id="tabs-1" role="generales">
			            			
			            			
			            			<div class="row">
			            				<div class="col-8">
			            					Concepto:
			            					<textarea id="cObjetoContrato" rows= 5 onkeydown="return LetrasNums(event)" name="cObjetoContrato" class="form-control"></textarea>
			            				</div>
			            				<div class="col-4">
			            					<div class="row">
					            				<div class="col-12">
					            					Tipo Fondo:
					            					<select name="cIdTipoFondo" id="cIdTipoFondo" class="form-select"> <option selected value="">______________</option></select>
					            				</div>
					            			</div>
					            			<div class="row">
					            				<div class="col-12">
					            					Tipos de Moneda:
					            					<select name="cIdTipoMoneda" id="cIdTipoMoneda" class="form-select"><option selected value="0">PESO MEXICANO</option><option>DOLAR ESTADOUNIDENSE</option></select>
					            				</div>
					            			</div>
					            			<div class="row">
					            				<div class="col-12">
					            					% IVA Aplicable:
					            					<select name="nPorcIVAAplicable" id="nPorcIVAAplicable" class="form-select"><option selected value="0">0%</option></select>
					            				</div>
					            			</div>
			            				</div>
			            			</div> 
									<div class="row">
										<div class="col-4">
											Monto Convenio Anexo:
											<div class="input-group">
												<input type="text" name="mObra" id="mObra" value="0" class="obligatorio monto form-control" onKeyPress="return onlyNumbers(event)"/>
											</div>
										</div>
										<div class="col-4">
											Monto Bruto:
											<input type="text" name="mImporte" id="mImporte" value="0" class="obligatorio monto form-control" onKeyPress="return onlyNumbers(event)" readonly/>
										</div>
										<div class="col-4">
											Monto Ejercicio Vigente(*):
											<input type="text" name="mEjercicio" id="mEjercicio" class="obligatorio monto form-control" onKeyPress="return onlyNumbers(event)" readonly value="0.0"/>
										</div>
									</div>
									<div class="row">
										<div class="col-4">
											Otros Impuestos:
											<input type="text" name="mOtrosImpuestos" id="mOtrosImpuestos" value="0.0" class="obligatorio monto form-control" onKeyPress="return onlyNumbers(event)" />
										</div>
										<div class="col-4">
											Monto I.V.A.:
											<div class="input-group">
												<input type="text" name="mIVA" id="mIVA" readonly="readonly" value="0" onKeyPress="return onlyNumbers(event)" class="obligatorio monto form-control" onblur="onBlurIVA()" />
												<a href="#" onclick="editaIVA();return false;" style="border-color: white;"> <img alt="Editar Monto" src="imagenes/edit.png" width="12" height="12" border="0"></a>
											</div>
										</div>
										<div class="col-4">
											Monto Convenio / Anexo (M.E.):
											<input type="text" name="mContratoME" id="mContratoME" readonly value="0" style="text-align:right;" onKeyPress="return onlyNumbers(event)" class="obligatorio monto form-control"/>
										</div>
									</div>
									<div class="row">
										<div class="col-8">
										</div>
										<div class="col-4">
											Monto Total:
											<input type="text" name="mTotal" readonly id="mTotal" value="0" class="obligatorio monto form-control" onKeyPress="return onlyNumbers(event)"/>
										</div>
									</div>
			            	</div>
			            	<div class="tab-pane" id="tabs-4" role="tabpanel" aria-labelledby="clavesPresupuestales">
			            		<div class="row">
			            			<div class="col-10">
			            				Estructura Programática
			            				<div class="input-group">
				            				<input type="text" id="ep" name="ep" size="65" readonly class="form-control" />
				            				<input type="button" id="nIdClaveEgresos2" size="5"  value="..." onclick="Grid()" onblur="rellenaCampos();" class="btn btn-secondary" />
			            				</div>
			            			</div>
			            			<div class="col-1">
			            				<br>
			            				<input type="button" value="Agregar" name="AgregarEP" id="AgregarEP"	onclick="fnClickAddRowC();" class="btn btn-secondary"/>
			            			</div>
			            		</div>
			            		<div class="row mt-2">
									<table id="dt_clavepresup" class="display">
										<thead>
											<tr align="center">
												<th>Estructura Programática</th>
												<th>Clave Interna</th>
												<th>&nbsp;</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</div>
			            	</div>
			            	<div class="tab-pane" id="tabPresupuesto" role="tabpanel" aria-labelledby="Calendario">
			            		<div id="totalesDiv">
			            			<div class="row">	
				            			<div class="col-4">
					            			Monto Contrato
					            			<input type="text" size="20" id="montoTotalCnt" readonly="readonly" class=" form-control" style="text-align: right;"/>
				            			</div>
				            			<div class="col-4">
					            			Monto Calendarizado
					            			<input type="text" size="20" id="montoCalendarioCnt" readonly="readonly" class=" form-control" style="text-align: right;"/>
				            			</div>
				            			<div class="col-4">
				            				Monto Pendiente
				            				<input type="text" size="20" id="montoPendienteCnt" readonly="readonly" class=" form-control" style="text-align: right;"/>
				            			</div>
			            			</div>
								</div>
								<div id="dtPresupuestoDiv">
									<table class="display" id="dtPresupuesto">
										<thead>
											<tr>
												<th>Estructura Programatica</th>
												<th>Importe</th>
											</tr>
										</thead>
									</table>
								</div>
								<div class="row mt-2">
									<div class="col-10"></div>
									<div class="col-2">
					            		<input type="button" onclick="aceptarBtn()" name="btnAceptar"	id="btnAceptar" value="Guardar" class="btn btn-secondary"/>
					            	</div>
				            	</div>
			            	</div>
			            	<div class="tab-pane" id="tabRetencion" role="tabpanel" aria-labelledby="Retenciones">
			            		<div id="encabezados">
									<table>
										<tr>
											<td align="left">Tipo de Retencion</td>
										</tr>
										<tr>
											<td align="left">
												<select name="cIdTipoRetencion" id="cIdTipoRetencion" class="form-select"></select>
											</td>
											<td>
												<input type="button" id="btAgregaRet" name="btAgregaRet" value="Agregar" onclick="fnAgregarRet()" class="btn btn-secondary"/>
											</td>
										</tr>
									</table>
								</div>
								<br>
								<div id="dtRetencionDiv">
									<table class="display" id="dt_retencion">
										<thead>
											<tr>
												<th>Retencion</th>
												<th>Porcentaje</th>
												<th style="display:none">Contrato</th>
											</tr>
										</thead>
									</table>
								</div>
			            	</div>
			            </div>
			        </div>
				</div>
			</div>
		</div>

			<div id="dialog-Cesion" title="Cesión de Derechos">
					<table border="0">
						<tr>
							<td><input type="text" maxlength="15" size="15" name="cIDRFCSesion" ID="cIDRFCSesion" readonly class="AyudaSyC  obligatorio form-control"/></td>
							<td><input type="text" readonly maxlength="60" size="60" id="NombreSesion" name="NombreSesion" style="text-align:left;" class="form-control"/></td>
						</tr>
						<tr>
							<td><input type="text" maxlength="30" size="30" value=""  id="nFolioJur" name="nFolioJur" class="cesion form-control" style="text-align:left;"/></td>
							<td><input type="text" readonly maxlength="10" size="10" id="fSesion" name="fSesion" style="text-align:center;" class="form-control"/></td>
						</tr>
						<tr>
							<td><input type="button" id="pbInactiva" style="visibility: hidden" value="DesActivar"/></td>							
							<td>&nbsp;</td>
						</tr>
					</table>
			</div>
		<div class="modal" tabindex="-1" role="dialog" id="dialog-form-ep" data-mdb-keyboard="true" data-mdb-backdrop="static">
		  	<div class="modal-dialog modal-lg" role="document">
		    	<div class="modal-content">
			      	<div class="modal-header">
				        <h5 class="modal-title">Agregar Estructura Program&aacute;tica</h5>
				        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      	</div>
		    		<div class="modal-body">
							<div class="row">
								<div class="col-10">
									EP:
									<input type="text" name="epDisp" id="epDisp" value="" class="montoDisp form-control" style="text-align: left"/>
								</div>
							</div>
							<br>
							<div class="row">
								<div class="col-2">	Mes
								</div>
								<div class="col-4">	Disponible
								</div>
								<div class="col-4">	Monto
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Enero
								</div>
								<div class="col-4">	
									<input type="text" id="eneroDisp" value="" class="montoDisp form-control" size="15" readonly="readonly" />
								</div>
								<div class="col-4">	
									<input type="text" id="eneroCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Febrero
								</div>
								<div class="col-4">	
									<input type="text" id="febreroDisp" value="" class="montoDisp form-control"   readonly="readonly" size="15" />
								</div>
								<div class="col-4">	
									<input type="text" id="febreroCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Marzo
								</div>
								<div class="col-4">	
									<input type="text" id="marzoDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	
									<input type="text" id="marzoCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Abril
								</div>
								<div class="col-4">	
									<input type="text" id="abrilDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	
									<input type="text" id="abrilCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Mayo
								</div>
								<div class="col-4">	
									<input type="text" id="mayoDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	
									<input type="text" id="mayoCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Junio
								</div>
								<div class="col-4">	
										<input type="text" id="junioDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	
										<input type="text" id="junioCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Julio
								</div>
								<div class="col-4">	<input type="text" id="julioDisp" value="" class="montoDisp form-control" readonly="readonly" size="15" />
								</div>
								<div class="col-4">	<input type="text" id="julioCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Agosto
								</div>
								<div class="col-4">	<input type="text" id="agostoDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	<input type="text" id="agostoCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Septiembre
								</div>
								<div class="col-4">	<input type="text" id="septiembreDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	<input type="text" id="septiembreCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Octubre
								</div>
								<div class="col-4">	<input type="text" id="octubreDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	<input type="text" id="octubreCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Noviembre
								</div>
								<div class="col-4">	<input type="text" id="noviembreDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	<input type="text" id="noviembreCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Diciembre
								</div>
								<div class="col-4">	<input type="text" id="diciembreDisp" value="" class="montoDisp form-control"  readonly="readonly" size="15" />
								</div>
								<div class="col-4">	<input type="text" id="diciembreCompr" value="" class="montoCaptura form-control" size="15" onKeyPress="return onlyNumbers(event)"/>
								</div>
							</div>
							<div class="row">
								<div class="col-2">	Total Calendarizado:
								</div>
								<div class="col-4">	
								</div>
								<div class="col-4">
									<input type="text" id="totalCalendarizado" readonly value="" class="numerico  form-control" size="15" />
								</div>
							</div>
					</div>
					<div class="modal-footer">
			        		<button type="button" id="btnAceptarFacturas" onclick="guardaCalendarioEP();" class="btn btn-primary">Guardar</button>
			        		<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
			    	</div>
		    	</div>
		  	</div>
		</div>
			</form>
	</body>
</html>
