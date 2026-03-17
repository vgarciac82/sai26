<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>

<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(
			GestionInterface.ATT_CONEXION);
	String aEjercicioFiscal = adecProy.obtenEjercicioFiscal();

	String folio = c.getFolio();
	int nFolio = new Integer(c.getFolio().substring(
			c.getFolio().lastIndexOf('-') + 1)).intValue();

	String cCentroContable = "";
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Registro de instrumentos jur&iacute;dicos de Subsidios</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js">
	
</script>

<script type="text/javascript" src="../Generador/js/crud.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js">
	
</script>

<script type="text/javascript" src="../Ayudas/js/autoCompleta.js">
	
</script>

<script type="text/javascript" src="../js/jsquery.js">
	
</script>

<script type="text/javascript"
	src="../Generador/js/jquery.dataTables.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery-ui-1.8.16.custom.min.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.ui.datepicker.js">
	
</script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js">
	
</script>
<script type="text/javascript" src="../js/catalogo/general.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.js">
	
</script>
<script type="text/javascript"
	src="../Generador/js/jquery.formatCurrency.all.js">
	
</script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js">
	
</script>
<script type="text/javascript">

	var catalogoEp;
	var anexoEp;
	
	function ResponsableSiguiente(id_oper){
			if( id_oper == 1 )
			{
				return "REVISOR_SUBSIDIO";
			}
			else if ( id_oper == 2)
			{
				if( $("#checkbox").is(':checked') ) //document.datosReintegro.autorizaRein[1].checked
				{
					return "AUTORIZADOR_SUBSIDIO";
				}
				else
				{
					return "CAPTURISTA_SUBSIDIO";
				}
			}
			else if ( id_oper == 3)
			{
				if( $("#checkbox").is(':checked') )
				{
					return "CONSULTA_SUBSIDIO";
				}
				else
				{
					return "REVISOR_SUBSIDIO";
				}
			}
	
	}

	function OperacionSiguiente(id_oper) {
		if( id_oper == 1 )
			{
				return "revisor_subsidio";
			}
			else if ( id_oper == 2)
			{
				
				if($("#checkbox").is(':checked')) //document.datosReintegro.autorizaRein[1].checked
				{
					return "autorizador_subsidio";
				}
				else
				{
					return "captura_subsidio";
				}
			}
			else if ( id_oper == 3)
			{
				if($("#checkbox").is(':checked'))
				{
					return "consulta_subsidio";
				}
				else
				{
					return "revisor_subsidio";
				}
			}
	}
	
	function onLoadPlantilla(id_oper){
		guardar();
		
		$("#autoriza").hide();
		if(id_oper== 2 || id_oper== 3){
			$("#autoriza").show();
		}
		if(id_oper==3 || id_oper==4)
			$(".esconde").hide();
	}
	
	function onSubmit(id_oper){
		var p = window.parent;
		
		if(id_oper==1){
			p.gestion.setFolio($("#FOLIO").val());
			p.gestion.setOperador($("#OPERADOR").val());
			p.gestion.setFechaDocumento($("#FECHA_SOLICITUD").val());
			p.gestion.setEjercicioFiscal($("#EJERCICIO_FISCAL").val());
			p.gestion.setConceptoMov("Instrumento Juridico de los Subsidios");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
			p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
		}
		
		if(id_oper==2 || id_oper==3)
			$("#checkbox").hide();
		guardar();
		guardarDetalle();
		  	
		parent.document.getElementById("pb_save").disabled=true;
		parent.document.getElementById("pb_send").disabled=false;
		
		return true;
	}
	
	function onPostSubmit(id_oper){
		return true;
	}
	
	function onPostDisplay(id_oper){}
	
	$(document).ready(function() {
		setDblClck();
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		$("#buscar").button();
		$("#eliminar").button();
		$("#eliminarAnexo").button();
		$("#editaAnexo").button();
		$("#captura").button();
		querySelectPost("catalogoEntidadFederativaRead", "cEstadoActual");
		querySelectPost("catalogoEntidadFederativaRead", "cEstadoActualE");
		
		$("#motivoRechazo").hide();
		
		$( "#cEstadoActual" ).change(function() {
			var textEdo = $("#cEstadoActual").find('option').filter(':selected').text();
			$("#cEstadoActualTemp").val( textEdo );
			$("#cEstadoActualInpt").val( $( "#cEstadoActual" ).val() );
			querySelectPost("SubsidioMunicipiosActualRead", "cMunicipioActual", {async:false});
			$("#cMunicipioActualInpt").val( $( "#cMunicipioActual" ).val() );
			querySelectPost("LocalidadRead", "localidad", {async:false});
		});

		$( "#cEstadoActualE" ).change(function() {
			var textEdoE = $("#cEstadoActualE").find('option').filter(':selected').text();
			$("#cEstadoActualETemp").val( textEdoE );
			$("#cEstadoActualEInpt").val( $( "#cEstadoActualE" ).val() );
			querySelectPost("SubsidioMunicipiosActualERead", "cMunicipioActualE", {async:false});
			$("#cMunicipioActualEInpt").val( $( "#cMunicipioActualE" ).val() );
			querySelectPost("LocalidadRead", "localidadE", {async:false});
		});

		$( "#cMunicipioActual" ).change(function() {
			var textMpio = $("#cMunicipioActual").find('option').filter(':selected').text();
			$("#cMunicipioActualTemp").val( textMpio );
			$("#cMunicipioActualInpt").val( $( "#cMunicipioActual" ).val() );
			querySelectPost("LocalidadRead", "localidad", {async:false});
		});

		$( "#cMunicipioActualE" ).change(function() {
			var textMpio = $("#cMunicipioActualE").find('option').filter(':selected').text();
			$("#cMunicipioActualETemp").val( textMpio );
			$("#cMunicipioActualEInpt").val( $( "#cMunicipioActualE" ).val() );
			querySelectPost("LocalidadERead", "localidadE", {async:false});
		});
		
		$( "#localidad" ).change(function() {
			var textLoc = $("#localidad").find('option').filter(':selected').text();
			$("#localidadTemp").val( textLoc );
			//alert($("#localidadTemp").val());
		});
		$( "#localidadE" ).change(function() {
			var textLoc = $("#localidadE").find('option').filter(':selected').text();
			$("#localidadETemp").val( textLoc );
			//alert($("#localidadTemp").val());
		});

		$("#fInicio").datepicker({
			dateFormat : "dd/mm/yy",
			showOn : "button",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true
		});

		$("#fFin").datepicker({
			dateFormat : "dd/mm/yy",
			showOn : "button",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true
		});

		$("#fInicioE").datepicker({
			dateFormat : "dd/mm/yy",
			showOn : "button",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true
		});

		$("#fFinE").datepicker({
			dateFormat : "dd/mm/yy",
			showOn : "button",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true
		});
		
		$("#fConvenio").datepicker({
			dateFormat : "dd/mm/yy",
			showOn : "button",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true
		});

		$("#fConvenioE").datepicker({
			dateFormat : "dd/mm/yy",
			showOn : "button",
			buttonImage : "../images/calendar.gif",
			buttonImageOnly : true
		});

		//Crea el DataTable con los campos de config. minimos. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		catalogoEp = $('#dt_catalogo').dataTable({
			        "bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": true,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "1250",
					"bScrollCollapse": true,	
					 oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}
		});

		anexoEp = $('#anexoTecnico').dataTable({
			        "bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": true,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "3000px",
					"sScrollXInner": "3200px",
					"bScrollCollapse": true,	
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}
		});

		var camposWhere = "";
		var elParametro =" nFolioSubsidio = '"+$("#nFolio").val()+"'";
		var order = "";
	
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TSUBSIDIODETALLE", Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){	
			for (var i = 0; i < j.length; i++){
 				var ep = new Array(); 
 				ep[i] = j[i].Col0;
 				
 				var mEnero = new Array(); 
 				mEnero[i] = j[i].Col1;

 				$("#mEneroFormato").val(mEnero[i]);
 				$("#mEneroFormato").formatCurrency();
 				
 				var mFebrero = new Array(); 
 				mFebrero[i] = j[i].Col2;

 				$("#mFebreroFormato").val(mFebrero[i]);
 				$("#mFebreroFormato").formatCurrency();
 				
 				var mMarzo = new Array(); 
 				mMarzo[i] = j[i].Col3;

 				$("#mMarzoFormato").val(mMarzo[i]);
 				$("#mMarzoFormato").formatCurrency();
 				
 				var mAbril = new Array(); 
 				mAbril[i] = j[i].Col4;
 				
 				$("#mAbrilFormato").val(mAbril[i]);
 				$("#mAbrilFormato").formatCurrency();

 				var mMayo = new Array(); 
 				mMayo[i] = j[i].Col5;

 				$("#mMayoFormato").val(mMayo[i]);
 				$("#mMayoFormato").formatCurrency();
 				
 				var mJunio = new Array(); 
 				mJunio[i] = j[i].Col6;

 				$("#mJunioFormato").val(mJunio[i]);
 				$("#mJunioFormato").formatCurrency();
 				
 				var mJulio = new Array(); 
 				mJulio[i] = j[i].Col7;

 				$("#mJulioFormato").val(mJulio[i]);
 				$("#mJulioFormato").formatCurrency();
 				
 				var mAgosto = new Array(); 
 				mAgosto[i] = j[i].Col8;

 				$("#mAgostoFormato").val(mAgosto[i]);
 				$("#mAgostoFormato").formatCurrency();
 				
 				var mSeptiembre = new Array(); 
 				mSeptiembre[i] = j[i].Col9;

 				$("#mSeptiembreFormato").val(mSeptiembre[i]);
 				$("#mSeptiembreFormato").formatCurrency();
 				
 				var mOctubre = new Array(); 
 				mOctubre[i] = j[i].Col10;

 				$("#mOctubreFormato").val(mOctubre[i]);
 				$("#mOctubreFormato").formatCurrency();
 				
 				var mNoviembre = new Array(); 
 				mNoviembre[i] = j[i].Col11;

 				$("#mNoviembreFormato").val(mNoviembre[i]);
 				$("#mNoviembreFormato").formatCurrency();
 				
 				var mDiciembre = new Array(); 
 				mDiciembre[i] = j[i].Col12;
 				
 				$("#mDiciembreFormato").val(mDiciembre[i]);
 				$("#mDiciembreFormato").formatCurrency();

 				var mAnual = new Array(); 
 				mAnual[i] = j[i].Col13;
 				
 				$("#mAnualFormato").val(mAnual[i]);
 				$("#mAnualFormato").formatCurrency();

 				$('#dt_catalogo').dataTable().fnAddData([ep[i], $("#mEneroFormato").val(), $("#mFebreroFormato").val(), $("#mMarzoFormato").val(), $("#mAbrilFormato").val()
 				, $("#mMayoFormato").val(), $("#mJunioFormato").val(), $("#mJulioFormato").val(), $("#mAgostoFormato").val(), $("#mSeptiembreFormato").val(), $("#mOctubreFormato").val()
 				, $("#mNoviembreFormato").val(), $("#mDiciembreFormato").val(), $("#mAnualFormato").val()]);
 				
 				$("#mEneroFormato").val("");
 				$("#mFebreroFormato").val("");
 				$("#mMarzoFormato").val("");
 				$("#mAbrilFormato").val("");
 				$("#mMayoFormato").val("");
 				$("#mJunioFormato").val("");
 				$("#mJulioFormato").val("");
 				$("#mAgostoFormato").val("");
 				$("#mSeptiembreFormato").val("");
 				$("#mOctubreFormato").val("");
 				$("#mNoviembreFormato").val("");
 				$("#mDiciembreFormato").val("");
 				$("#mAnualFormato").val("");

 			}	
		});
		
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla: "TSUBSIDIODETALLEANEXO", Campos:camposWhere, Param:elParametro, Order:order, MaxReg: "10", ajax: 'true'}, function(j){	
			for (var i = 0; i < j.length; i++){
			
			
 				var renglon = new Array(); 
 				renglon[i] = j[i].Col0;
 				
 				var estado = new Array(); 
 				estado[i] = j[i].Col1;
 				
 				var municipio = new Array(); 
 				municipio[i] = j[i].Col2;
 				
 				var localidad = new Array(); 
 				localidad[i] = j[i].Col3;
 				
 				var otro = new Array(); 
 				otro[i] = j[i].Col4;
 				
 				var fInicio = new Array(); 
 				fInicio[i] = j[i].Col5;
 				
 				var fFin = new Array(); 
 				fFin[i] = j[i].Col6;
 				
 				var fConvenio = new Array(); 
 				fConvenio[i] = j[i].Col7;

 				var mImporte = new Array(); 
 				mImporte[i] = j[i].Col8;
 				
 				$("#mImporteFormato").val(mImporte[i]);
 				$("#mImporteFormato").formatCurrency();

 				var mFederal = new Array(); 
 				mFederal[i] = j[i].Col9;
 				
 				$("#mfederalFormato").val(mFederal[i]);
 				$("#mfederalFormato").formatCurrency();

 				var mEstatal = new Array(); 
 				mEstatal[i] = j[i].Col10;
 				
 				$("#mestatalFormato").val(mEstatal[i]);
 				$("#mestatalFormato").formatCurrency();

 				var mMunicipal = new Array(); 
 				mMunicipal[i] = j[i].Col11;
 				
 				$("#mmunicipalFormato").val(mMunicipal[i]);
 				$("#mmunicipalFormato").formatCurrency();

 				var mOtro = new Array(); 
 				mOtro[i] = j[i].Col12;
 				
 				$("#motroFormato").val(mOtro[i]);
 				$("#motroFormato").formatCurrency();

 				var accionMetas = new Array(); 
 				accionMetas[i] = j[i].Col13;
 				
 				var unidadMetas = new Array(); 
 				unidadMetas[i] = j[i].Col14;
 				
 				var cantidadMetas = new Array(); 
 				cantidadMetas[i] = j[i].Col15;

 				$('#anexoTecnico').dataTable().fnAddData([renglon[i],estado[i],municipio[i],localidad[i],otro[i],fInicio[i], fFin[i],fConvenio[i]
 				, $("#mImporteFormato").val(), $("#mfederalFormato").val() , $("#mestatalFormato").val() , $("#mmunicipalFormato").val(), $("#motroFormato").val(), accionMetas[i], unidadMetas[i], cantidadMetas[i]]);
 				
 			//	$('#anexoTecnico').dataTable().fnAddData([i+1,estado[i],municipio[i],localidad[i],otro[i],fInicio[i], fFin[i], mImporte[i], mFederal[i], mEstatal[i], mMunicipal[i], mOtro[i], accionMetas[i], unidadMetas[i], cantidadMetas[i]]);
 			
 			}	
		});
		
		$(".tabs").tabs();
		$('#dlgDetalleSubsidios').dialog({
			autoOpen: false,
				width: 1200,
				heigth: 1800
		});

		$('#div_EditaDetalleSubsidios').dialog({
			autoOpen: false,
				width: 1200,
				heigth: 1800
		});
		
		$("#dt_catalogo tbody").click(function(event) {
			$(catalogoEp.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
			if(catalogoEp.fnGetData().length>0){
				var pos=catalogoEp.fnGetPosition($(event.target.parentNode)[0]);
				var data = catalogoEp.fnGetData(pos);
				$("#ep").val(data[0]);	
			}
		});
		
		$("#anexoTecnico tbody").click(function(event) {
			$(anexoEp.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');
		
			var pos=anexoEp.fnGetPosition($(event.target.parentNode)[0]);
			var data = anexoEp.fnGetData(pos);
			
			
			//llenando la informacion de div de edicion de datos
			$("#nDocRenglonAnexo").val(data[0]);
			$("#cEstadoActualE").val(12);
			querySelectPost("SubsidioMunicipiosActualERead", "cMunicipioActualE", {async:false});
			$("#cMunicipioActualE").val('12021');
			$("#localidadE").val('120025');
			$("#otroE").val(data[4]);
			
			
			
			
			$("#fInicioE").val(data[5]);
			$("#fFinE").val(data[6]);
			$("#fConvenioE").val(data[7]);
			
			$("#mtotalE").val(data[8]);
			$("#mfederalE").val(data[9]);
			$("#mestatalE").val(data[10]);
			$("#mmunicipalE").val(data[11]);
			$("#motroE").val(data[12]);
			
			$("#accionE").val(data[13]);
			$("#unidadE").val(data[14]);
			$("#cantidadE").val(data[15]);
			
						$("#div_EditaDetalleSubsidios").show();

			
			/**********************************************************************************/
		});
			
			
	});
	
	function buscarep(){
		window.open('epSubsidios.jsp', 'AyudaEPsPagos', 'status=1,scrollbars=1, width=800px, height=900px, left=200px');
	}
	
	function guardar(){
		queryFormPost({
				queryName:"encabezadoSubsidiosRead", 
				async:false,
				callback:function(){
					if( $("#encabezadoLleno").val() == ""  )
						queryFormPost("encabezadoSubsidios", {async:false});
				}
			});
	}
	
	function borrarDetalle() {
		if (fnGetSelected(catalogoEp).length <= 0) {
			alert("Se debe seleccionar un registro");
		} else 
		{
			var anSelected = fnGetSelected(catalogoEp);
			catalogoEp.fnDeleteRow(anSelected[0]);
 			queryFormPost('dSubsidiosDetalle', {async: false });
		}
	}
	
	function borrarAnexo() {
	
		if (fnGetSelected(anexoEp).length <= 0) {
			alert("Se debe seleccionar un registro");
		} else 
		{
			var anSelected = fnGetSelected(anexoEp);
			anexoEp.fnDeleteRow(anSelected[0]);
			
 			queryFormPost('dSubsidiosDetalleAnexo', {async: false });
		}
	}
	
	function fnGetSelected(oTableLocal) {
		var aReturn = new Array();
		var aTrs = oTableLocal.fnGetNodes();

		for ( var i = 0; i < aTrs.length; i++) {
			if ($(aTrs[i]).hasClass('row_selected')) 
			{
				aReturn.push(aTrs[i]);
			}
		}

		return aReturn;
	}
	
	function capturaAnexo(){
		$('#dlgDetalleSubsidios').dialog('option', 'modal', true).dialog('open');
	}
	
	function editarAnexo(){
		$('#dlgDetalleSubsidios').dialog('option', 'modal', true).dialog('open');
	}
	
	
	function guardaAnexo(){
	
		
		$("#cEstadoActualInpt").val($("#cEstadoActual").val());
		$("#cMunicipioActualInpt").val($("#cMunicipioActual").val());
		$("#localidadInpt").val($("#localidad").val());
		$("#otroInpt").val($("#otro").val());
		$("#mtotalInpt").val($("#mtotal").val());
		$("#fInicioInpt").val($("#fInicio").val());
		$("#fFinInpt").val($("#fFin").val());
		$("#fConvInpt").val($("#fConvenio").val());
		$("#mfederalInpt").val($("#mfederal").val());
		$("#mestatalInpt").val($("#mestatal").val());
		$("#mmunicipalInpt").val($("#mmunicipal").val());
		$("#motroInpt").val($("#motro").val());
		$("#accionInpt").val($("#accion").val());
		$("#unidadInpt").val($("#unidad").val());
		$("#cantidadInpt").val($("#cantidad").val());
		
		
		
		document.getElementById("totalAnexo").value+= document.getElementById("mtotalInpt").value;
		
		//alert("totalAnexo :"+ document.getElementById("totalAnexo").value);
		
		//alert("totalEP " + document.getElementById("totalEP").value);
		
		if(document.getElementById("totalAnexo").value<= document.getElementById("totalEP").value ){
			queryFormPost("maxAnexoSubsidiosRead", {async:false});
			
				if($("#maxAnexo").val()=="")
					$("#nDocRenglon").val(($("#nDocRenglon").val()*1)+1);
				else
					$("#nDocRenglon").val(($("#maxAnexo").val()*1)+1);
				queryFormPost("anexoSubsidios", {async:false});
				
				$('#dlgDetalleSubsidios').dialog('close');
				
				alert("Información insertada");
		
				$("#mImporteFormato").val($("#mtotal").val());
				$("#mfederalFormato").val($("#mfederal").val());
				$("#mestatalFormato").val($("#mestatal").val());
				$("#mmunicipalFormato").val($("#mmunicipal").val());
				$("#motroFormato").val($("#motro").val());
		 		
		 		$("#mImporteFormato").formatCurrency();
		 		$("#mfederalFormato").formatCurrency();
		 		$("#mestatalFormato").formatCurrency();
		 		$("#mmunicipalFormato").formatCurrency();
		 		$("#motroFormato").formatCurrency();
		 		/*******************************************************************************/		
				/*$('#anexoTecnico').dataTable().fnAddData([$("#nDocRenglon").val(),$("#cEstadoActualInpt").val(),$("#cMunicipioActualInpt").val(),$("#localidadInpt").val()
				,$("#otroInpt").val(),$("#fInicioInpt").val(), $("#fFinInpt").val(), $("#fConvInpt").val(), $("#mImporteFormato").val(), $("#mfederalFormato").val(), $("#mestatalFormato").val()
				,$("#mmunicipalFormato").val(), $("#motroFormato").val(), $("#accionInpt").val(), $("#unidadInpt").val(), $("#cantidadInpt").val()]);*/
				/******************************************************************/
				
				/*****************************************************************************/
				$('#anexoTecnico').dataTable().fnAddData([$("#nDocRenglon").val(),$("#cEstadoActualTemp").val(),$("#cMunicipioActualTemp").val(),$("#localidadTemp").val()
				,$("#otroInpt").val(),$("#fInicioInpt").val(), $("#fFinInpt").val(), $("#fConvInpt").val(), $("#mImporteFormato").val(), $("#mfederalFormato").val(), $("#mestatalFormato").val()
				,$("#mmunicipalFormato").val(), $("#motroFormato").val(), $("#accionInpt").val(), $("#unidadInpt").val(), $("#cantidadInpt").val()]);
				
				
				
				
				/**************************************************/
				
				
				
				$("#mImporteFormato").val("");
				$("#mfederalFormato").val("");
				$("#mestatalFormato").val("");
				$("#mmunicipalFormato").val("");
				$("#motroFormato").val("");
				
				
		}else{
		
				alert("El calculo preliminar muestra fondos insuficientes para la completar los anexos ");
		
		}
		

		
		//alert("Total acumulado" +document.getElementById("totalAnexo").value );
		
		
		
		
		
		
		//$('#anexoTecnico').dataTable().fnAddData([$("#nDocRenglon").val(),$("#cEstadoActualInpt").val(),$("#cMunicipioActualInpt").val(),$("#localidadInpt").val(),$("#otroInpt").val(),$("#fInicioInpt").val(), $("#fFinInpt").val(),$("#fConvInpt").val(), $("#mtotalInpt").val(), $("#mfederalInpt").val(), $("#mestatalInpt").val(),$("#mmunicipalInpt").val(), $("#motroInpt").val(), $("#accionInpt").val(), $("#unidadInpt").val(), $("#cantidadInpt").val()]);

	}
	
	function editaAnexo(){
	
		
		$("#cEstadoActualEInpt").val($("#cEstadoEActual").val());
		$("#cMunicipioActualEInpt").val($("#cMunicipioEActual").val());
		$("#localidadEInpt").val($("#localidadE").val());
		$("#otroEInpt").val($("#otroE").val());
		$("#mtotalEInpt").val($("#mtotalE").val());
		$("#fInicioEInpt").val($("#fInicioE").val());
		$("#fFinEInpt").val($("#fFinE").val());
		$("#fConvEInpt").val($("#fConvenioE").val());
		$("#mfederalEInpt").val($("#mfederalE").val());
		$("#mestatalEInpt").val($("#mestatalE").val());
		$("#mmunicipalEInpt").val($("#mmunicipalE").val());
		$("#motroEInpt").val($("#motroE").val());
		$("#accionInpt").val($("#accionE").val());
		$("#unidadInpt").val($("#unidadE").val());
		$("#cantidadInpt").val($("#cantidadE").val());
		
		
		
		document.getElementById("totalAnexo").value+= document.getElementById("mtotalInpt").value;
		
		//alert("totalAnexo :"+ document.getElementById("totalAnexo").value);
		
		//alert("totalEP " + document.getElementById("totalEP").value);
		
		if(document.getElementById("totalAnexo").value<= document.getElementById("totalEP").value ){
			queryFormPost("maxAnexoSubsidiosRead", {async:false});
			
				if($("#maxAnexo").val()=="")
					$("#nDocRenglon").val(($("#nDocRenglon").val()*1)+1);
				else
					$("#nDocRenglon").val(($("#maxAnexo").val()*1)+1);
				queryFormPost("actualizaAnexoSubsidios", {async:false});
				
				$('#dlgDetalleSubsidios').dialog('close');
				
				alert("Información editada");
		
				$("#mImporteFormato").val($("#mtotal").val());
				$("#mfederalFormato").val($("#mfederal").val());
				$("#mestatalFormato").val($("#mestatal").val());
				$("#mmunicipalFormato").val($("#mmunicipal").val());
				$("#motroFormato").val($("#motro").val());
		 		
		 		$("#mImporteFormato").formatCurrency();
		 		$("#mfederalFormato").formatCurrency();
		 		$("#mestatalFormato").formatCurrency();
		 		$("#mmunicipalFormato").formatCurrency();
		 		$("#motroFormato").formatCurrency();
		 		/*******************************************************************************/		
				/*$('#anexoTecnico').dataTable().fnAddData([$("#nDocRenglon").val(),$("#cEstadoActualInpt").val(),$("#cMunicipioActualInpt").val(),$("#localidadInpt").val()
				,$("#otroInpt").val(),$("#fInicioInpt").val(), $("#fFinInpt").val(), $("#fConvInpt").val(), $("#mImporteFormato").val(), $("#mfederalFormato").val(), $("#mestatalFormato").val()
				,$("#mmunicipalFormato").val(), $("#motroFormato").val(), $("#accionInpt").val(), $("#unidadInpt").val(), $("#cantidadInpt").val()]);*/
				/******************************************************************/
				
				/*****************************************************************************/
				$('#anexoTecnico').dataTable().fnAddData([$("#nDocRenglon").val(),$("#cEstadoActualTemp").val(),$("#cMunicipioActualTemp").val(),$("#localidadTemp").val()
				,$("#otroInpt").val(),$("#fInicioInpt").val(), $("#fFinInpt").val(), $("#fConvInpt").val(), $("#mImporteFormato").val(), $("#mfederalFormato").val(), $("#mestatalFormato").val()
				,$("#mmunicipalFormato").val(), $("#motroFormato").val(), $("#accionInpt").val(), $("#unidadInpt").val(), $("#cantidadInpt").val()]);
				
				
				
				
				/**************************************************/
				
				
				
				$("#mImporteFormato").val("");
				$("#mfederalFormato").val("");
				$("#mestatalFormato").val("");
				$("#mmunicipalFormato").val("");
				$("#motroFormato").val("");
				
				
		}else{
		
				alert("El calculo preliminar muestra fondos insuficientes para la completar los anexos ");
		
		}
		

		
		//alert("Total acumulado" +document.getElementById("totalAnexo").value );
		
		
		
		
		
		
		//$('#anexoTecnico').dataTable().fnAddData([$("#nDocRenglon").val(),$("#cEstadoActualInpt").val(),$("#cMunicipioActualInpt").val(),$("#localidadInpt").val(),$("#otroInpt").val(),$("#fInicioInpt").val(), $("#fFinInpt").val(),$("#fConvInpt").val(), $("#mtotalInpt").val(), $("#mfederalInpt").val(), $("#mestatalInpt").val(),$("#mmunicipalInpt").val(), $("#motroInpt").val(), $("#accionInpt").val(), $("#unidadInpt").val(), $("#cantidadInpt").val()]);

	}

	function guardarDetalle(){
		var arrData = $("#dt_catalogo").dataTable().fnGetData();
		for(var i = 0; i < arrData.length; i++){
			$("#detalleExiste").val("");
			$("#nDocRenglonDetalle").val(i+1);
			$("#ep").val(arrData[i][0]);
			$("#mEnero").val(arrData[i][1]);
			$("#mFebrero").val(arrData[i][2]);
			$("#mMarzo").val(arrData[i][3]);
			$("#mAbril").val(arrData[i][4]);
			$("#mMayo").val(arrData[i][5]);
			$("#mJunio").val(arrData[i][6]);
			$("#mJulio").val(arrData[i][7]);
			$("#mAgosto").val(arrData[i][8]);
			$("#mSeptiembre").val(arrData[i][9]);
			$("#mOctubre").val(arrData[i][10]);
			$("#mNoviembre").val(arrData[i][11]);
			$("#mDiciembre").val(arrData[i][12]);
			$("#mImporte").val(arrData[i][13]);
			queryFormPost({
				queryName:"detalleSubsidiosRead", 
				async:false,
				callback:function(){
					if( $("#detalleExiste").val() == ""  )
						queryFormPost("detalleSubsidios", {async:false});
				}
			});
		}
	}
		
	function validaCheck()
	{
		if($("#checkbox").is(':checked')) //document.datosReintegro.autorizaRein[1].checked
		{
			$("#motivoRechazo").hide();
		}
		else
		{
			$("#motivoRechazo").show();
		}
	}
	
		function setDblClck() {
		$("#anexoTecnico tbody").dblclick(function(evt) {

		var aPos = anexoEp.fnGetPosition(evt.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var arr = anexoEp.fnGetData()[currIndex];
		idAnexo = arr[0];
		
		/********************/
		//cEstadoActualEInpt   para revisar.....
	//	alert("Valores : " + arr[1]+"   "+ arr[2]+"   "+ arr[3]+"   "+ arr[4]+"   "+ arr[5] );
		var estadoNombre= arr[1];
		var arr1 = estadoNombre.split(" ");
		var idEstado = arr1[0];
		var muniNombre= arr[2];
		arr1 = muniNombre.split(" ");
		var idMuni = arr1[0];
		var localNombre= arr[2];
		arr1 = localNombre.split(" ");
		var idLocal = arr1[0];
	
		
		
		$("#cEstadoActualEInpt").val(idEstado);
		$("#cMunicipioActualEInpt").val(idMuni); 
		$("#cMunicipioActualEInpt").val(idLocal);
		
		
		querySelectPost("SubsidioMunicipiosActualERead", "cMunicipioActualE", {async:false});

		querySelectPost("LocalidadERead", "localidadE", {async:false});
		
		/******************/
		actualizaAnexo(idAnexo);
	/**********************************************************************/

		$("#div_EditaDetalleSubsidios").dialog("open");
	});
	
}

	function actualizaAnexo(idAnexo){
				$("#mImporteFormatoE").val("");
				$("#mfederalFormatoE").val("");
				$("#mestatalFormatoE").val("");
				$("#mmunicipalFormatoE").val("");
				$("#motroFormatoE").val("");
			
			$("#readEditaDetalleSubsidios tbody").remove();
				$("#nFolio").val();	
				$("#idAnexo").val(idAnexo);
				
			queryFormPost("readEditaDetalleSubsidios", {
				async : false 
			});
		
				$("#mImporteFormatoE").val($("#mtotalE").val());
				$("#mfederalFormatoE").val($("#mfederalE").val());
				$("#mestatalFormatoE").val($("#mestatalE").val());
				$("#mmunicipalFormatoE").val($("#mmunicipalE").val());
				$("#motroFormatoE").val($("#motroE").val());
		 		
		 		$("#mImporteFormatoE").formatCurrency();
		 		$("#mfederalFormatoE").formatCurrency();
		 		$("#mestatalFormatoE").formatCurrency();
		 		$("#mmunicipalFormatoE").formatCurrency();
		 		$("#motroFormatoE").formatCurrency();

				$("#mtotalE").val($("#mImporteFormatoE").val());
				$("#mfederalE").val($("#mfederalFormatoE").val());
				$("#mestatalE").val($("#mestatalFormatoE").val());
				$("#mmunicipalE").val($("#mmunicipalFormatoE").val());
				$("#motroE").val($("#motroFormatoE").val());
				
	

		}	
	
	function calcula(){
		$("#mtotal").val(parseFloat($("#mfederal").val()) + parseFloat($("#mestatal").val()) + parseFloat($("#mmunicipal").val()) + parseFloat($("#motro").val()));
	}

	function calculaE(){
		$("#mtotalE").val(parseFloat($("#mfederalE").val()) + parseFloat($("#mestatalE").val()) + parseFloat($("#mmunicipalE").val()) + parseFloat($("#motroE").val()));
	}
	
</script>

</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="FormContrato" name="FormContrato">

		<!-- PARA CRUD -->

		<input type="hidden" name="maxAnexo" id="maxAnexo" value="" /> <input
			type="hidden" name="cEstadoActualInpt" id="cEstadoActualInpt"
			value="" /> <input type="hidden" name="cMunicipioActualInpt"
			id="cMunicipioActualInpt" value="" /> <input type="hidden"
			name="localidadInpt" id="localidadInpt" value="" /> <input
			type="hidden" name="cEstadoActualEInpt" id="cEstadoActualEInpt"
			value="" /> <input type="hidden" name="cMunicipioActualEInpt"
			id="cMunicipioActualEInpt" value="" /> <input type="hidden"
			name="localidadEInpt" id="localidadEInpt" value="" /> <input
			type="hidden" value="" id="idAnexo" name="idAnexo"> <input
			type="hidden" name="otroInpt" id="otroInpt" value="" /> <input
			type="hidden" name="mtotalInpt" id="mtotalInpt" value="" /> <input
			type="hidden" name="mfederalInpt" id="mfederalInpt" value="" /> <input
			type="hidden" name="mestatalInpt" id="mestatalInpt" value="" /> <input
			type="hidden" name="mmunicipalInpt" id="mmunicipalInpt" value="" />
		<input type="hidden" name="motroInpt" id="motroInpt" value="" /> <input
			type="hidden" name="accionInpt" id="accionInpt" value="" /> <input
			type="hidden" name="unidadInpt" id="unidadInpt" value="" /> <input
			type="hidden" name="cantidadInpt" id="cantidadInpt" value="" /> <input
			type="hidden" name="fInicioInpt" id="fInicioInpt" value="" /> <input
			type="hidden" name="fFinInpt" id="fFinInpt" value="" /> <input
			type="hidden" name="fConvInpt" id="fConvInpt" value="" /> <input
			type="hidden" name="encabezadoLleno" id="encabezadoLleno" /> <input
			type="hidden" name="detalleExiste" id="detalleExiste" /> <input
			type="hidden" name="nFolio" id="nFolio" value="<%=nFolio%>" /> <input
			type="hidden" name="cFolio" id="cFolio" value="<%=folio%>" /> <input
			type="hidden" name="idCaso" id="idCaso" value="<%=c.getIdCaso()%>" />
		<input type="hidden" name="fCreacion" id="fCreacion"
			value="<%=today%>" /> <input type="hidden" name="cRamo" id="cRamo"
			value="<%=usuario.getU_Ramo()%>" /> <input type="hidden"
			name="cUnidadResponsable" id="cUnidadResponsable"
			value="<%=usuario.getU_UR()%>" /> <input type="hidden"
			name="aEjercicioFiscal" id="aEjercicioFiscal"
			value="<%=aEjercicioFiscal%>" /> <input type="hidden"
			name="cCentroContable" id="cCentroContable"
			value="<%=cCentroContable%>" /> <input type="hidden"
			name="nDocRenglon" id="nDocRenglon" value="0" /> <input
			type="hidden" name="nDocRenglonAnexo" id="nDocRenglonAnexo" value="0" />
		<input type="hidden" name="mEneroFormato" id="mEneroFormato" value="0" />
		<input type="hidden" name="mFebreroFormato" id="mFebreroFormato"
			value="0" /> <input type="hidden" name="mMarzoFormato"
			id="mMarzoFormato" value="0" /> <input type="hidden"
			name="mAbrilFormato" id="mAbrilFormato" value="0" /> <input
			type="hidden" name="mMayoFormato" id="mMayoFormato" value="0" /> <input
			type="hidden" name="mJunioFormato" id="mJunioFormato" value="0" /> <input
			type="hidden" name="mJulioFormato" id="mJulioFormato" value="0" /> <input
			type="hidden" name="mAgostoFormato" id="mAgostoFormato" value="0" />
		<input type="hidden" name="mSeptiembreFormato" id="mSeptiembreFormato"
			value="0" /> <input type="hidden" name="mOctubreFormato"
			id="mOctubreFormato" value="0" /> <input type="hidden"
			name="mNoviembreFormato" id="mNoviembreFormato" value="0" /> <input
			type="hidden" name="mDiciembreFormato" id="mDiciembreFormato"
			value="0" /> <input type="hidden" name="mAnualFormato"
			id="mAnualFormato" value="0" /> <input type="hidden"
			name="mImporteFormato" id="mImporteFormato" value="0" /> <input
			type="hidden" name="mfederalFormato" id="mfederalFormato" value="0" />
		<input type="hidden" name="mestatalFormato" id="mestatalFormato"
			value="0" /> <input type="hidden" name="mmunicipalFormato"
			id="mmunicipalFormato" value="0" /> <input type="hidden"
			name="motroFormato" id="motroFormato" value="0" />
			<input type="hidden"
			name="mImporteFormatoE" id="mImporteFormatoE" value="0" /> <input
			type="hidden" name="mfederalFormatoE" id="mfederalFormatoE" value="0" />
		<input type="hidden" name="mestatalFormatoE" id="mestatalFormatoE"
			value="0" /> <input type="hidden" name="mmunicipalFormatoE"
			id="mmunicipalFormatoE" value="0" /> <input type="hidden"
			name="motroFormatoE" id="motroFormatoE" value="0" /> <input
			type="hidden" name="nDocRenglonDetalle" id="nDocRenglonDetalle"
			value="0" /> <input type="hidden" name="ep" id="ep" /> <input
			type="hidden" name="mImporte" id="mImporte" /> <input type="hidden"
			name="mEnero" id="mEnero" /> <input type="hidden" name="mFebrero"
			id="mFebrero" /> <input type="hidden" name="mMarzo" id="mMarzo" />
		<input type="hidden" name="mAbril" id="mAbril" /> <input
			type="hidden" name="mMayo" id="mMayo" /> <input type="hidden"
			name="mJunio" id="mJunio" /> <input type="hidden" name="mJulio"
			id="mJulio" /> <input type="hidden" name="mAgosto" id="mAgosto" />
		<input type="hidden" name="mSeptiembre" id="mSeptiembre" /> <input
			type="hidden" name="mOctubre" id="mOctubre" /> <input type="hidden"
			name="mNoviembre" id="mNoviembre" /> <input type="hidden"
			name="mDiciembre" id="mDiciembre" />
		<!-- --------------- -->

		<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" />
		<input type="hidden" name="OPERADOR" id="OPERADOR"
			value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo" : c
					.getCasoOperacion(0).getResponsable()%>" />
		<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD"
			value="<%=today%>" /> <input type="hidden" name="EJERCICIO_FISCAL"
			id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" /> <input
			type="hidden" name="FECHA_APLICACION_CONTABLE"
			id="FECHA_APLICACION_CONTABLE" value="<%=today%>" readonly="readonly"
			maxlength="10" size="10" /> <input type="hidden" value=""
			id="buscaVal" name="buscaVal"> <input type="hidden" value=""
			id="existe" name="existe">
		<!------- Creamos la variable que acumula el total sumado de las EP... ----------->
		<input type="hidden" name="totalEP" id="totalEP" value="0" /> <input
			type="hidden" name="totalAnexo" id="totalAnexo" value="0" />
		<!------- Creamos las variables que contienen todo el valor seleccionado de estado,municipio, localidad ... ----------->
		<input type="hidden" name="cEstadoActualTemp" id="cEstadoActualTemp"
			value="" /> <input type="hidden" name="cMunicipioActualTemp"
			id="cMunicipioActualTemp" value="" /> <input type="hidden"
			name="localidadTemp" id="localidadTemp" value="" />

		<!------- Creamos las variables que contienen todo el valor seleccionado de estado,municipio, localidad PARA EDITAR... ----------->
		<input type="hidden" name="cEstadoActualETemp" id="cEstadoActualETemp"
			value="" /> <input type="hidden" name="cMunicipioActualETemp"
			id="cMunicipioActualETemp" value="" /> <input type="hidden"
			name="localidadETemp" id="localidadETemp" value="" />

		<!---********************************************************************---->

		<div id="container" class="container">
			<h1>Registro de instrumentos jur&iacute;dicos de Subsidios</h1>
			<fieldset>
				Número de Folio: <input type="text" name="folio" id="folio"
					value="<%=folio%>" readonly="readonly" /><br />
				<!-- Clave Presupuestal: <input type="text" name="ep" id="ep" size="60"/> -->
			</fieldset>
			<br>
			<fieldset>
				<legend>Clave Presupuestaria</legend>
				<div class="esconde">
					<input type="button" name="buscar" id="buscar"
						value="Buscar Clave Presupuestal" onclick="buscarep();" /> <input
						type="button" name="eliminar" id="eliminar"
						value="Eliminar Clave Presupuestal" onclick="borrarDetalle();" />
				</div>
				<table id="dt_catalogo" class="display" cellspacing="0"
					cellpadding="2" align="center">
					<thead>
						<tr>
							<th>EP</th>
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
						</tr>
					</thead>
				</table>
			</fieldset>
			<fieldset>
				<div style="text-align: right;">
					Captura Anexo T&eacute;cnico <input type="button" name="captura"
						id="captura" value="Captura" onclick="capturaAnexo();" />
				</div>
			</fieldset>
			<fieldset>
				<legend>Detalle Anexo T&eacute;cnico</legend>
				<div class="esconde">
					<input type="button" name="eliminarAnexo" id="eliminarAnexo"
						value="Eliminar Anexo" onclick="borrarAnexo();" />
					<!--<input type="button" name="editaAnexo" id="editaAnexo"
						value="Editar Anexo" onclick="editarAnexo();" />-->
				</div>
				<table id="anexoTecnico" class="display">
					<thead>
						<tr>

							<th>Id.Renglon</th>
							<th>Estado</th>
							<th>Municipio</th>
							<th>Localidad</th>
							<th>Otro</th>
							<th>Fecha Inicio</th>
							<th>Fecha Fin</th>
							<th>Fecha Firma Convenio</th>
							<th>Total</th>
							<th>Federal</th>
							<th>Estatal</th>
							<th>Municipal</th>
							<th>Otro</th>
							<th>Acci&oacute;n</th>
							<th>Unidad de Medida</th>
							<th>Cantidad</th>
						</tr>
					</thead>
				</table>
			</fieldset>

			<div id="autoriza">
				Autoriza: <input name="checkbox" id="checkbox" type="checkbox"
					value="1" checked="checked" onclick="validaCheck();" />
			</div>

			<div id="motivoRechazo">
				Motivo del rechazo:
				<textarea id="tmotivoRechazo" name="tmotivoRechazo" rows="3"
					cols="50"></textarea>
			</div>


			<div id="dlgDetalleSubsidios"
				title="Detalle de Instrumento de Subsidios">
				<fieldset>
					<legend style="text-align: center;">Localizaci&oacute;n
						Geográfica</legend>
					<table>
						<tr>
							<td width="200">Estado:<br> <Select
								name="cEstadoActual" id="cEstadoActual"><option>Estado</option>
							</Select></td>
							<td width="200">Municipio:<br> <Select
								name="cMunicipioActual" id="cMunicipioActual"><option>Municipio</option>
							</Select></td>
							<td width="200">Localidad:<br> <Select name="localidad"
								id="localidad"><option>Localidad</option>
							</Select></td>
							<td width="200">Otro:<br> <input type="text"
								name="otro" id="otro" /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend style="text-align: center;">Calendario de
						Ejecuci&oacute;n</legend>
					<table>
						<tr>
							<td width="200">Fecha de Inicio<input type="text"
								name="fInicio" id="fInicio" />
							</td>
							<td width="200">Fecha de t&eacute;rmino<input type="text"
								name="fFin" id="fFin" />
							</td>
							<td width="200">Fecha de firma convenio<input type="text"
								name="fConvenio" id="fConvenio" />
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend style="text-align: center;">Estructura Financiera</legend>
					<table id="totalesT">
						<tr>
							<th width="200">Total</th>
							<th width="200">Federal</th>
							<th width="200">Estatal</th>
							<th width="200">Municipal</th>
							<th width="200">Otro</th>
						</tr>
						<tr>
							<td width="200"><input type="text" name="mtotal" id="mtotal"
								value="0" readonly="readonly" disabled="disabled" />
							</td>
							<td width="200"><input type="text" name="mfederal"
								id="mfederal" onchange="calcula()" value="0" />
							</td>
							<td width="200"><input type="text" name="mestatal"
								id="mestatal" onchange="calcula()" value="0" />
							</td>
							<td width="200"><input type="text" name="mmunicipal"
								id="mmunicipal" onchange="calcula()" value="0" />
							</td>
							<td width="200"><input type="text" name="motro" id="motro"
								onchange="calcula()" value="0" />
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend style="text-align: center;">Metas</legend>
					<table>
						<tr>
							<th>Acci&oacute;n</th>
							<th>Unidad de Medida</th>
							<th>Cantidad</th>
						</tr>
						<tr>
							<td><textarea name="accion" id="accion" cols="40"></textarea>
							</td>
							<td><textarea name="unidad" id="unidad" cols="40"></textarea>
							</td>
							<td><input type="text" name="cantidad" id="cantidad" />
							</td>
						</tr>
					</table>
				</fieldset>

				<div style="text-align: right;">
					<input type="button" value="Aceptar" name="aceptar" id="aceptar"
						onclick="guardaAnexo()" />
				</div>
			</div>

			<div id="div_EditaDetalleSubsidios"
				title="Editando Detalle de Instrumento de Subsidios">
				<fieldset>
					<legend style="text-align: center;">Localizaci&oacute;n
						Geográfica</legend>
					<table>
						<tr>
							<td width="200">Estado:<br> <Select
								name="cEstadoActualE" id="cEstadoActualE"><option>Estado</option>
							</Select></td>
							<td width="200">Municipio:<br> <Select
								name="cMunicipioActualE" id="cMunicipioActualE"><option>Municipio</option>
							</Select></td>
							<td width="200">Localidad:<br> <Select
								name="localidadE" id="localidadE"><option>Localidad</option>
							</Select></td>
							<td width="200">Otro:<br> <input type="text"
								name="otroE" id="otroE" /></td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend style="text-align: center;">Calendario de
						Ejecuci&oacute;n</legend>
					<table>
						<tr>
							<td width="200">Fecha de Inicio<input type="text"
								name="fInicioE" id="fInicioE" />
							</td>
							<td width="200">Fecha de t&eacute;rmino<input type="text"
								name="fFinE" id="fFinE" />
							</td>
							<td width="200">Fecha de firma convenio<input type="text"
								name="fConvenioE" id="fConvenioE" />
							</td>
						</tr>
					</table>
				</fieldset>

				<fieldset>
					<legend style="text-align: center;">Estructura Financiera</legend>
					<table id="totalesT">
						<tr>
							<th width="200">Total</th>
							<th width="200">Federal</th>
							<th width="200">Estatal</th>
							<th width="200">Municipal</th>
							<th width="200">Otro</th>
						</tr>
						<tr>
							<td width="200"><input type="text" name="mtotalE"
								id="mtotalE" value="0" readonly="readonly" disabled="disabled" />
							</td>
							<td width="200"><input type="text" name="mfederalE"
								id="mfederalE" onchange="calculaE()" value="0" />
							</td>
							<td width="200"><input type="text" name="mestatalE"
								id="mestatalE" onchange="calculaE()" value="0" />
							</td>
							<td width="200"><input type="text" name="mmunicipalE"
								id="mmunicipalE" onchange="calculaE()" value="0" />
							</td>
							<td width="200"><input type="text" name="motroE" id="motroE"
								onchange="calculaE()" value="0" />
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset>
					<legend style="text-align: center;">Metas</legend>
					<table>
						<tr>
							<th>Acci&oacute;n</th>
							<th>Unidad de Medida</th>
							<th>Cantidad</th>
						</tr>
						<tr>
							<td><textarea name="accionE" id="accionE" cols="40"></textarea>
							</td>
							<td><textarea name="unidadE" id="unidadE" cols="40"></textarea>
							</td>
							<td><input type="text" name="cantidadE" id="cantidadE" />
							</td>
						</tr>
					</table>
				</fieldset>

				<div style="text-align: right;">
					<input type="button" value="Guardar" name="aceptar" id="aceptar"
						onclick="editaAnexo()" />
				</div>
			</div>


		</div>
	</form>
</body>
</html>