<%@page import="com.syc.sai.fonden.model.FondenManager"%>
<%@page import="com.syc.sai.fonden.model.FondenBusinessLogic"%>
<%@page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page
	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenManager"%>
<%@page import="com.syc.sai.fonden.Fonden"%>
<%@page import="com.syc.sai.fonden.model.FondenMovimientoBusinessLogic"%>
<%@page import="com.syc.sai.fonden.model.FondenMovimientoManager"%>
<%@page import="com.syc.sai.fonden.FondenMovimiento"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.log4j.Logger"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<%!private Logger log = Logger.getLogger(getClass());%>

<%
	Calendar calendar = Calendar.getInstance();
	int year = calendar.get(Calendar.YEAR);
	FondenBusinessLogic fbl = new FondenBusinessLogic();
	List<Fonden> fondens = FondenManager.readFonden(fbl.getConnection(), year);
	Fonden fonden = null;
	boolean isNew = true;
	if(!fondens.isEmpty()){
		fonden = (Fonden) (fondens.get(0));
		isNew = false;
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	String currentYear = sdf.format(new Date(System.currentTimeMillis()));
	sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
	String today = sdf.format(new Date(System.currentTimeMillis()));
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Reporte General FONDEN</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="-1">

<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<link rel="stylesheet" type="text/css"
	href="Ayudas/css/autocompleta.css"></link>
<style>
.ui-autocomplete {
	max-height: 100px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
}

/* IE 6 doesn't support max-height
				     * we use height instead, but this forces the menu to always be this tall
				     */
* html .ui-autocomplete {
	height: 100px;
}
</style>

<style type="text/css" title="currentStyle">
	@import "css/demo_page.css";
	@import "css/demo_table_jui.css";
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>

<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="js/ContabilidadCentroContable.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/Poliza.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="js/jquery.maskedinput.min.js"></script>
<script type="text/javascript" src="js/jquery.validate-1.9.0.js"></script>
<script type="text/javascript">
	
	var oTable;	
	
	$(document).ready(function() {												
						refreshTable();	
						loading();		
						setSelectableTable();
	});
	
		
	function setSelectableTable(){		
		$("#dataTableReporteGeneral tbody tr").live('click',function(evt1) {
					if ($(this).hasClass('row_selected')) {
						$(this).removeClass('row_selected');						
					} else {
						$('tr.row_selected').removeClass('row_selected');
						$(this).addClass('row_selected');
						var nTr = $(this).parents('tr')[0];
						var aData = oTable.fnGetData( nTr );
						seleccionado = oTable.fnGetPosition(evt1.target.parentNode);						
						//alert(aData[seleccionado][2]);
						agregarFactura(aData[seleccionado][2]);
					}
				});
	}


	function loading() {
		$('#loadingDiv').hide() // hide it initially
		.ajaxStart(function() {
			$("body").addClass("loading");
			$(this).show();
		}).ajaxStop(function() {
			$("body").removeClass("loading");
			$(this).hide();
		});
	}
	
	var crfc = '';
	function buscar(){		
		crfc = $("#crfc").val();
		refreshTable();	
	}	
	
	function refreshTable() {
		oTable = $('#dataTableReporteGeneral').dataTable(
				{
					bRetrive : false,
					bPaginate : true,
					bDestroy : true,
					bLengthChange : false,
					iDisplayLength : 100,
					bFilter : false,
					bSort : true,
					bInfo : false,
					bAutoWidth : true,
					bJQueryUI: true,
					oLanguage : {
						sProcessing : "Procesando... Espere Por Favor",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtered from _MAX_ total entries)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:",
						oPaginate : {
							sFirst : "Primero",
							sPrevious : "Ant.",
							sNext : "Sigte.",
							sLast : "&Uacute;ltimo"
						}
					},
					bServerSide : true,
					sAjaxSource : window.location.protocol + "//"
							+ window.location.host + "/"
							+ window.location.pathname.split("/")[1]
							+ "/crud?rt=t&ql=tFondenMovimiento&qw=cRFC='"+$("#crfc").val()+"' AND cIdFonden="+<%=fonden.getCidFonden()%>,
					bProcessing : true,
					sPaginationType : "full_numbers",
					bJQueryUI : true,
					aaSorting : [ [ 2, "asc" ] ],
					aoColumns : [ 
					{
						sName : "cIdFonden",
						bSearchable : true,
						bSortable : false,
						bVisible : false,
						sClass : "alignCenter",
						sWidth : "200px"
					},  {
						sName : "nIdFondenMovimiento",
						bSearchable : true,
						bSortable : false,
						bVisible : false,
						sClass : "alignCenter"
					}, {
						sName : "nNumCaso",
						bSearchable : true,
						bSortable : false,
						bVisible : true,
						sClass : "alignCenter",
						sWidth : "200px"
					}, {
						sName : "nPrecioUnitario",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter",
						fnRender: function ( o ) {
		                    		return "$"+parseFloat(o.aData[ o.iDataColumn ]).toFixed(2);
		               		 },
                		bUseRendered: false
					}, {
						sName : "ncantidad",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignLeft"
					}, {
						sName : "nImporte",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter",
						fnRender: function ( o ) {
		                    		return "$"+parseFloat(o.aData[ o.iDataColumn ]).toFixed(2);
		               		 },
                		bUseRendered: false
					}, {
						sName : "cProveedor",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter"
					}, {
						sName : "cNumPedido",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter"
					} , {
						sName : "nNetoPedido",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter",
						fnRender: function ( o ) {
		                    		return parseFloat(o.aData[ o.iDataColumn ]).toFixed(2);
		               		 },
                		bUseRendered: false
					} , {
						sName : "nPrecio",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter",
						fnRender: function ( o ) {
		                    		return "$"+parseFloat(o.aData[ o.iDataColumn ]).toFixed(2);
		               		 },
                		bUseRendered: false
					} , {
						sName : "nCantidadTotal",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignLeft"
					}, {
						sName : "nTotal",
						bSearchable : true,
						bSortable : true,
						bVisible : true,
						sClass : "alignCenter",
						fnRender: function ( o ) {
		                    		return "$"+parseFloat(o.aData[ o.iDataColumn ]).toFixed(2);
		               		 },
                		bUseRendered: false
					} ]
				});						
	};
	
	function popup(url,ancho,alto) {				 
		var posicion_x=parseInt((screen.width/2)-(ancho/2));		
		var posicion_y=parseInt((screen.height/2)-(alto/2));
		var settings = "width="+ancho+",height="+alto+",menubar=0,toolbar=0,directories=0,scrollbars=no,resizable=no,left="+posicion_x+",top="+posicion_y+"";				
		window.open(url,"",settings);
	} 
	
	function getFondenReporteGeneral(){		
			window.open(	"../FondenMovimiento/getFondenReporteGeneral?"														
														+ "crfc="+$("#crfc").val()														
														+ "&cIdFonden="+<%=fonden.getCidFonden()%>,
														"popacuse",
														"scrollbars=1, resizable=yes, width=1024, height=768"
												   );
		
	}
	
	function getFondenReporteGeneralPDF(){
		
			window.open(	"../admin/SeguridadCatalogos?"
														+ "catalogo=CONTRARECIBO"
														+ "&accion=run"
														+ "&rn=ReporteGeneralFonden.jasper"
														+ "&crfc="+$("#crfc").val()														
														+ "&cIdFonden="+<%=fonden.getCidFonden()%>,
														"popacuse",
														"scrollbars=1, resizable=yes, width=1024, height=768"
												   );
		
	}
	
</script>
</head>
<body id="dt_example">
	<div id="loadingDiv" class="modal"><img src="imagenes/wait24trans.gif"></div>
	<fieldset>	
		<legend>Reporte General FONDEN</legend>
		<form id="formFondenMovimiento">
			<label>Beneficiario :</label> 
			<input name="crfc" type="text"
				id="crfc" size="25" /> 
			<input type="button" onclick="buscar()" value="Buscar" />

			<input type="button" value="Exportar Excel" 
				onclick="getFondenReporteGeneral()"/> 
			<input type="button"
				onclick="getFondenReporteGeneralPDF()"				
				value="Exportar PDF" />
		</form>

		<table id="dataTableReporteGeneral" name="dataTableReporteGeneral"
			class="display" align="center" width="900px">
			<tbody>
			<thead>
				<th>cIdFonden</th>
				<th>nIdFondenMovimiento</th>
				<th>Num. Caso</th>
				<th>Precio Unitario</th>				
				<th>Cantidad</th>
				<th>Importe</th>
				<th>Proveedor</th>
				<th>Num. Pedido</th>
				<th>Neto Pedido</th>
				<th>Precio</th>
				<th>Cantidad Total</th>
				<th>Total</th>
			</thead>
			</tbody>
		</table>	
	</fieldset>
</body>
</html>
