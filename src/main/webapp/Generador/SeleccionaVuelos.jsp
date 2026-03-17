<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	response.setHeader("Cache-Control","no-cache"); 
	response.setHeader("Pragma","no-cache"); 
	response.setDateHeader ("Expires", -1); 
	String cTipoPago = request.getParameter("cTipoPago");
	String nFolioPago = request.getParameter("folioPago");
	String importePago = request.getParameter("importePago");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="pragma" content="no-cache" />


<title>Seleccion de Vuelos.</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<style type="text/css">
.centerCls {
	text-align: center;
}

.rightCls {
	text-align: right;
}
</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript">
	var oTableVuelos;
	var oTableVuelosDet;
	var oTableVuelosAgregados;
	var tmtecla;
	var allSelected = false;
	
	var cTipoPago = "<%=cTipoPago%>";
	var nFolioPago = "<%=nFolioPago%>";
	var importePago = "<%=importePago%>";

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

	$( document ).ready( function() {
		$("#nFolioPago").val(nFolioPago);
		$("#cTipoPago").val(cTipoPago);
		$("#totalFacturas").val(importePago);
		
		creaDTFoliosVuelos();
		creaDTVuelosAgregados();
		validarCapturaVuelos();
		
		queryFormPost(
			{
				queryName:"totalVuelosCapturadosRead",
				async:false,
				callback:function(){
					var totalFacturas = Number( $("#totalFacturas").val() );
					var totalCapturado = Number( $("#totalAgregado").val() );
					var totalFaltante = totalFacturas-totalCapturado;
					$("#totalFaltante").val(totalFaltante.toFixed( 2 ) );
				}
			}
		);
		
		oTableVuelosDet = $( '#tblBoletosDet' ).dataTable( {
			"bJQueryUI" : true
		} );


		$( ".filtro" ).keyup( function( evt ) {
			clearInterval( tmtecla );
			tmtecla = setInterval( function() {
				fnFilterColumn( evt );
			}, 500 );
		} );

		$( "#btnSelAll" ).button().click( function() {
			seleccionaTodo();
		} );

		$( "#btnAdd" ).button().click( function() {
			agregarSeleccionados();
		} );
		
	} );
	
	function onChangeVuelo(docRenglon){
		var nombreInput = "nchkRenglon_" + docRenglon;

				
		var totalTxt = $("#totalCapturado").val();
		var total = Number(totalTxt);
		var valor = Number( obtenValorDT( docRenglon ) );
		if( !$("#"+nombreInput).attr('checked') )
			valor = -1*valor;
		
		var suma = total + valor;
		$("#totalCapturado").val(suma.toFixed( 2 ));
		
	}
		
	function obtenValorDT( docRenglon ){
			var datos = $("#tblBoletosDet").dataTable().fnGetData();
			var val = 0.0;
			for( cnt = 0; cnt < datos.length; cnt++ ){
				if( datos[cnt][0] == docRenglon ){
					val = datos[cnt][5];
					break;
				}
			}
			
			return val;
	}
		
	function creaDTVuelosAgregados(){
		oTableVuelosAgregados = $( '#tblBoletosAgregados' ).dataTable({
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth" : true,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=vw_tTempVuelosEnCaptura&qw=cTipoPago = '" + cTipoPago + "' and nFolioPago = " + nFolioPago,
			aoColumns : [
						{sName : "cReferencia"}, 
						{sName : "cPartida"},
						{sName : "cUnidadEjecutora"},
						{sName : "total"},
						{sName : "Descartar"},
						{sName : "nDocRenglon",	bSearchable : true, bSortable : false, bVisible : false, sClass : "alignCenter", sWidth : "40px"},
						{sName : "nFolioVuelos", bSearchable : true, bSortable : false, bVisible : false, sClass : "alignCenter", sWidth : "40px"}
						]
		});		
		
	}
	
	function createInput( form, name, value ) {
		$( '<input>' ).attr( {
		type : 'hidden',
		name : name,
		value : value
		} ).addClass( 'remove' ).appendTo( '#' + form );
	}
	
	
	function agregarSeleccionados() {
		createInput('AgregarBoletosFrm', 'nFolioPago', nFolioPago );
		createInput('AgregarBoletosFrm', 'cTipoPago', cTipoPago );
		createInput('AgregarBoletosFrm', 'nFolioVuelos', $("#nFolioVuelos").val() );
		
		$( ".selVuelo:checkbox:checked" ).each( function() {
			var checkName = $( this ).attr( "name" );
			var nDocRenglon = checkName.split( "_" )[ 1 ];
			createInput('AgregarBoletosFrm', 'nDocRenglon', nDocRenglon );
		} );
		
		$.ajax({
			type : "POST",
			url : "../vuelos/AgregaVuelosPago",
			cache : false,
			async : false,
			data : $("#AgregarBoletosFrm").serialize(),
			success : function(RS) {
				var exito = RS.success;
				if ("true" == exito) {
					var msg = RS.data_1.result;
					alert(msg);
					fnFilterColumn();
				} else
					alert(RS.data_1.result);
				borraElementos();
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Ocurrio el siguiente error:" + errorThrown);
				borraElementos();
			}
		});
		creaDTVuelosAgregados();
		validarCapturaVuelos();
	}
	
	function borraElementos(){
		$('.remove').remove();	
	}
	
	function seleccionaTodo() {
	
		$("#totalCapturado").val("0.00");
		
		$( ".selVuelo" ).each( function() {
			$( this ).attr( "checked", !allSelected );
			$( this ).change();
		} );

		allSelected = !allSelected;

		if( allSelected )
			$( "#btnSelAll" ).val( "Deseleccionar todo" );
		else{
			$( "#btnSelAll" ).val( "Seleccionar todo" );
			$("#totalCapturado").val("0.00");
		}
	}

	function creaDTFoliosVuelos() {

		oTableVuelos = $( '#dtFoliosVuelos' ).dataTable( {
		"bPaginate" : false,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bAutoWidth" : true,
		"sScrollY" : 270,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=vw_LayoutVuelosHeader&qw=Status='A'",
		aoColumns : [ {
			sName : "nFolioVuelos"
		}, {
			sName : "cFolioVuelos"
		}, {
			sName : "fRegistro"
		}, {
			sName : "StatusDesc"
		}, {
			sName : "mImporteTotal"
		} ],
		oLanguage : es_mx
		} );

		$( "#dtFoliosVuelos tbody" ).dblclick( function( event ) {
			$( oTableVuelos.fnSettings().aoData ).each( function() {
				$( this.nTr ).removeClass( 'row_selected' );
			} );

			$( event.target.parentNode ).addClass( 'row_selected' );

			tblLayoutVuelosDblClick( event );
			
		} );

		$( "#dtFoliosVuelos tbody" ).live( "click", function( event ) {
			clickTablaVuelos( event );
		} );
		
	}
	
	function clickTablaVuelos( event ) {
		$( $( '#dtFoliosVuelos' ).dataTable().fnSettings().aoData ).each( function() {
			$( this.nTr ).removeClass( 'row_selected' );
		} );

		$( event.target.parentNode ).addClass( 'row_selected' );

		var aPost = oTableVuelos.fnGetPosition( event.target.parentNode );
		var aData = oTableVuelos.fnGetData( aPost );
		var nFolioVuelos = aData[ 0 ];
		var mImporteVuelos = aData[ 4 ];

		$( "#nFolioVuelos" ).val( nFolioVuelos );
		//$( "#mImporteVuelos" ).val( mImporteVuelos );

	}

	function tblLayoutVuelosDblClick( event ) {

		var aPos = oTableVuelos.fnGetPosition( event.target.parentNode );
		var aData = oTableVuelos.fnGetData( aPos );
		var nFolioVuelos = aData[ 0 ];

		$( "#nFolioVuelos" ).val( nFolioVuelos );
		$( "#dlgVuelosDet" ).dialog( "open" );

		$( "#seleccionLayout" ).css( "display", "none" );
		muestraVuelosDet();
	}

	function muestraVuelosDet( condicion ) {

		var sWhere = "Status IN('S', 'RG', 'A', 'V', 'MV') AND cEsPagado = 'N' AND nFolioVuelos = " + $( "#nFolioVuelos" ).val() + "" + ( condicion ? ( condicion ) : "" );

		oTableVuelosDet = $( '#tblBoletosDet' ).dataTable( {
		"bPaginate" : false,
		"bLengthChange" : true,
		"bFilter" : false,
		"bSort" : true,
		"bInfo" : true,
		"bAutoWidth" : false,
		"sScrollY" : 300,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"sScrollX" : "1000",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=vw_tLayoutVuelosDet2&qw=" + sWhere,
		aoColumns : [ {
		sName : "nDocRenglon",
		bVisible : false
		}, {
		sName : "nchkRenglon",
		sClass : "centerCls"
		}, {
			sName : "cReferencia"
		}, {
		sName : "cPartida",
		sClass : "centerCls"
		}, {
		sName : "cUnidadEjecutora",
		sClass : "centerCls"
		}, {
		sName : "mTotal",
		sClass : "rightCls"
		} ],
		oLanguage : {
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
		}
		} );
		
	}

	function fnFilterColumn( evt ) {
		
		clearInterval( tmtecla );
		var value, myWhere, token;
		var column_name = [ "cReferencia", "cPartida", "cUnidadEjecutora", "mTotal" ];
		myWhere = "";
		token = " AND ";

		for( var i = 1; i <= column_name.length; i++ ) {
			value = $( "#col" + i + "_filter" ).val();
			if( value !== "" ) {
				myWhere += token + column_name[ i - 1 ] + " LIKE '%25" + value + "%25'";
				token = " AND ";
			}
		}
		oTable = muestraVuelosDet( myWhere );

		allSelected = false;
		$( "#btnSelAll" ).val( "Seleccionar todo" );
		$("#totalCapturado").val("0.00");
		return true;
	}
	
	function descartar(nDocRenglon) {
		
		$("#nDocRenglon").val("");
		
		var totalAgregado = parseFloat(quitaFrmt($("#totalAgregado").val()));
		if (confirm("Esta seguro de borrar el renglon?")) {
			var info = oTableVuelosAgregados.fnGetData();
			var nFolioLayout = "";
			
			for (i = 0; i < info.length; i++) {
				var renglonInfo = info[i];
				var importe = parseFloat(quitaFrmt(renglonInfo[3]));
				
				if (renglonInfo[5] == nDocRenglon ) {
					nFolioLayout = renglonInfo[6];
										
					$("#nDocRenglon").val(nDocRenglon);
					$("#nFolioVuelos").val(nFolioLayout);
					
					queryFormPost("tTempVuelosEnCapturaRenglonDelete",{async: false });
					break;					 
				}
			}
			creaDTVuelosAgregados();
			validarCapturaVuelos();		
		}
	}	
	
	function quitaFrmt(fld) {
		var valcol = fld.toString();
		valcol = valcol.replace(/[$]/g, "");
		valcol = valcol.replace(/,/g, "");
		return valcol;
	}
	
	function validarCapturaVuelos(){	
		queryFormPost("validaCapturaLayoutVuelosRead", {async:false});
		if($("#capturaLayoutVuelos").val() == "1"){
			queryFormPost("importeVuelosDetCapturadosRead", {async:false});
			$("#totalAgregado").val(Number($("#mImporteVuelos").val()).toFixed(2));
			var totalFacturas = Number( $("#totalFacturas").val() );
			var totalCapturado = Number( $("#totalAgregado").val() );
			var totalFaltante = totalFacturas-totalCapturado;
			$("#totalFaltante").val(totalFaltante.toFixed( 2 ) );
		}
	}
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="AgregarBoletosFrm" method="POST" action="../vuelos/AgregaVuelosPago"></form>
	<form id="FormContrato" name="FormContrato">
		<input type="hidden" name="nFolioVuelos" id="nFolioVuelos">
		<input type="hidden" name="mImporteVuelos" id="mImporteVuelos">
		<input type="hidden" name="cTipoPago" id="cTipoPago">
		<input type="hidden" name="nFolioPago" id="nFolioPago">
		<input type="hidden" name="nDocRenglon" id="nDocRenglon">
		
		<input type="hidden" id="capturaLayoutVuelos" name="capturaLayoutVuelos" value="">
		
		
		<div id="container" class="container">
			<div id="Totales">
				<fieldset>
					<legend>Totales</legend>
					<table>
						<tr>
							<td align="right">Total en Facturas:</td>
							<td align="left"><input type="text" id="totalFacturas" size="11">
							<td align="right">Total Capturado:</td>
							<td align="left"><input type="text" id="totalAgregado" size="11" value="0.0">
							<td align="right">Por comprobar:</td>
							<td align="left"><input type="text" id="totalFaltante" size="11" value="0.0">
						</tr>
					</table>
				</fieldset>
			</div>
			<div id="seleccionLayout">
				<span> <label style="font-weight:bold; font-size: 10px; text-align: right;">*Doble click en el renglon para desplegar datos de Folio--Layout de Vuelos. </label> </span>
				<table id="dtFoliosVuelos" class="display" cellspacing="0" cellpadding="0" align="center">
					<thead>
						<tr>
							<th>ID</th>
							<th>Folio</th>
							<th>Fecha Registro</th>
							<th>Estatus</th>
							<th>Importe Total</th>
						</tr>
					</thead>
				</table>
			</div>
			<!-- Dialogo para seleccionar vuelos a eliminar -->
			<div id="dlgVuelosDet">
				<fieldset>
					<legend>Selección de Vuelos</legend>
					<label style="font-size: 11px; font-weight: bold;"> Seleccione el(los) Boletos a eliminar dando click y despues click en Guardar</label>
					<div id="btnDiv">
						<table align="right">
							<tr>
								<td align="right">Monto Seleccionado:</td>
								<td align="left"><input type="text" id="totalCapturado" name="totalCapturado" value="0.0"  > </td>
								<td align="right"><input type="button" id="btnSelAll" value="Seleccionar Todo"></td>
								<td align="right"><input type="button" id="btnAdd" value="Agregar Seleccionado"></td>
							</tr>
						</table>
					</div>
					<table id="tblBoletosDet" class="display" cellspacing="0" cellpadding="2" align="center">
						<thead>
							<tr>
								<th>nDocRenglon</th>
								<th>Seleccionar</th>
								<th>Núm. Boleto</th>
								<th>Partida</th>
								<th>Unidad</th>
								<th>Importe</th>
							</tr>
						</thead>
						<tbody></tbody>
						<tfoot>
							<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
								<td><input type="text" class="filtro" name="col1_filter" id="col1_filter" /></td>
								<td><input type="text" class="filtro" name="col2_filter" id="col2_filter" size="5" maxlength="5" /></td>
								<td><input type="text" class="filtro" name="col3_filter" id="col3_filter" size="3" maxlength="3" /></td>
								<td><input type="text" class="filtro" name="col4_filter" id="col4_filter" size="7" maxlength="20" /></td>
							</tr>
						</tfoot>
					</table>
				</fieldset>
			</div>
			<div>
				<fieldset>
					<legend>Boletos Agregados para el pago</legend>
					<table id="tblBoletosAgregados" class="display" cellspacing="0" cellpadding="2" align="center">
						<thead>
							<tr>
								<th>Núm. Boleto</th>
								<th>Partida</th>
								<th>Unidad</th>
								<th>Importe</th>
								<th>Descartar</th>
								<th>Renglon</th>
								<th>Folio Layout</th>
							</tr>
						</thead>
					</table>
				</fieldset>
			</div>
		</div>
	</form>
</body>

</html>