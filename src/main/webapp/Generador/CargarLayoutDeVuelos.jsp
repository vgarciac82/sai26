<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>

<%@page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	boolean puedeBorrar = ( u.getRole("ELIMINA_VUELO_LAYOUT") != null );
	boolean puedeEditar = ( u.getRole("EDITA_VUELO_LAYOUT") != null );
	
	boolean esRespuesta = request.getParameter("RESPUESTA") != null ? true: false;	
		
	String msg = (String)session.getAttribute("RESULT");
	session.removeAttribute("RESULT");
	
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Carga Layout de Vuelos.</title>

	<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">	

	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>

	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	
	<script type="text/javascript">
			
	 var RFC = "";
	 var cNombre = "";
	 var cReferencia = "";		
	 var cRuta = "";
	 var cPartida = "";
	 var cUnidadEjecutora = "";
	 var fFechaSalida = "";
	 var fFechaRegreso = "";
	 var nFolioVuelos = "0";
	 var nDocRenglon = "0";
	 var Status = "";
	 
	 var mQ = 0.00;
	 var mTarifa = 0.00;
	 var mIVA = 0.00;
	 var mTUA = 0.00;
	 var mYR = 0.00;
	 var mTotal = 0.00;
	 var cEsPagado = "";
	 
	 var puedeBorrar = <%=puedeBorrar%>;
	 var puedeEditar = <%=puedeEditar%>;
	 var modalLayout;
	 
		$(document).ready(function() {
			modalLayout = new bootstrap.Modal(document.getElementById('dlgBoletajeAvion'), 'data-bs-backdrop');
			
			$("#btnCargar").button();
			$("#btnVerDet").button();
			document.getElementById("btnCargar").disabled = true;

			creaDataTable();
			//creaDlgCargaBoletaje();
			creaDlgDetalleBoletos();
			creaDlgDatosVuelo();
			
			limpiarVGlobales();

			$("#chk_Genera").change(function() {
				if ($("#chk_Genera").prop("checked")) {
					$("#nFolioVuelos").val("*");
					$("#nFolioVuelo").val("*");
					$("#Status").val("");
					$("#chk_Existente").prop("checked", false);
					//$("#chk_Existente").attr('disabled', true);
					document.getElementById("btnCargar").disabled = false;
				}

			});

			$("#chk_Existente").change(function() {
				if ($("#chk_Existente").prop("checked")) {
					creaDataTable(" Status = 'A' ");
					$("#nFolioVuelos").val("");
					$("#nFolioVuelo").val("");
					$("#Status").val("");
					$("#chk_Genera").prop("checked", false);
					//$("#chk_Genera").attr('disabled', true);
				}

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

		function creaDataTable(conds) {
			if (!conds)
				conds = "1<>1";

			oTable = $('#dtFoliosVuelos').dataTable(
					{
						"bPaginate" : false,
						"bLengthChange" : true,
						"bFilter" : true,
						"bSort" : true,
						"bInfo" : true,
						"bAutoWidth" : true,
						"sScrollY" : 300,
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType" : "full_numbers",
						"bScrollCollapse" : true,
						"bServerSide" : true,
						"fnServerData" : function(sSource, aoData, fnCallback) {
											$.ajax({
												"dataType" : 'json',
												"type" : "POST",
												"url" : sSource,
												"data" : aoData,
												"success" : fnCallback
											});
										},
						sAjaxSource : window.location.protocol + "//"
								+ window.location.host + "/"
								+ window.location.pathname.split("/")[1]
								+ "/crud?rt=t&ql=vw_LayoutVuelosHeader&qw="
								+ conds,
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
					});

			$("#dtFoliosVuelos tbody").dblclick(function(e) {
				$(oTable.fnSettings().aoData).each(function() {
					$(this.nTr).removeClass('table-primary');
				});

				$(e.target.parentNode).addClass('table-primary');

				tblLayoutVuelosDblClick(e);
			});

			$("#dtFoliosVuelos tbody").live("click", function(event) {
				clickTablaVuelos(event);
			});
		}

		function tblLayoutVuelosDblClick(event) {
			/* Obtener los valores del renglon para mostrarlos en pantalla */
			var aPos = oTable.fnGetPosition(event.target.parentNode);
			var aData = oTable.fnGetData(aPos);
			var nFolioVuelos = aData[0];
			var Status = aData[3];

			$("#nFolioVuelos").val(nFolioVuelos);
			$("#nFolioVuelo").val(nFolioVuelos);
			$("#Status").val(Status);
			document.getElementById("btnCargar").disabled = false;
		}

		function clickTablaVuelos(event) {
			$(oTable.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('table-primary');
			});

			$(event.target.parentNode).addClass('table-primary');
			var aPost = oTable.fnGetPosition(event.target.parentNode);
			var aData = oTable.fnGetData(aPost);
			var nFolioVuelos = aData[0];

			$("#nFolioVuelos").val(nFolioVuelos);

		}

		function abrirDlgBoletaje() {
			var esPago = $("#cEsPago").val();
			var folioPago = $("#nFolioVuelos").val();

			$('#cargaFrm').attr(
					'src',
					'CargaLayoutVuelos.jsp?pago=' + esPago + '&folioPago='
							+ folioPago);
			
			modalLayout.show();
			//$("#dlgBoletajeAvion").dialog("open");
		}

		function btnAceptarDlg() {
			$('#cargaFrm').attr('src', '');
			$("#dlgBoletajeAvion").dialog("close");
		}

		function verDetalleBoletos() {
			$("#dlgDetalleBoletos").dialog("open");
		}

		function creaDlgDetalleBoletos() {

			var botones = {};
			if( puedeEditar ){
				botones["Editar"] = function(){
					$("#dlgDatosVuelo").dialog("open");
				};
			}
			
			if( puedeBorrar ){
				botones["Eliminar"]= function() {
						eliminarRowVuelo();
					};
			}
			
			botones["Cancelar"] = function() {
						$("#dlgDetalleBoletos").dialog("close");
					};
			
			$("#dlgDetalleBoletos").dialog({
				title : "Detalle de Boletos",
				autoOpen : false,
				height : 600,
				width : 1100,
				modal : true,
				open : function() {
					muestraDetalleBoletos();
				},
				buttons : botones
			});
		}

		var tblDetalleBoletos;
		function muestraDetalleBoletos() {

			var sWhere = " nFolioVuelos = " + $("#nFolioVuelos").val();
			//+ " AND cReferencia NOT IN ( SELECT cNumeroBoleto FROM tInfoBoleto WITH (NOLOCK) WHERE nFolioRelacionGastos = " + $("#folioRG").val() + " ) "; 

			tblDetalleBoletos = $('#tblDetalleBoletos')
					.dataTable(
							{
								"bPaginate" : false,
								"bLengthChange" : true,
								"bFilter" : true,
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
								"fnServerData" : function(sSource, aoData, fnCallback) {
													$.ajax({
														"dataType" : 'json',
														"type" : "POST",
														"url" : sSource,
														"data" : aoData,
														"success" : fnCallback
													});
												},
								sAjaxSource : window.location.protocol
										+ "//"
										+ window.location.host
										+ "/"
										+ window.location.pathname.split("/")[1]
										+ "/crud?rt=t&ql=vw_tLayoutVuelosDet&qw="
										+ sWhere,
								aoColumns : [ {
									sName : "nchkRenglon"
								}, {
									sName : "RFC"
								}, {
									sName : "cNombre"
								}, {
									sName : "cReferencia"
								}, {
									sName : "mTotal"
								}, {
									sName : "fFechaSalida"
								}, {
									sName : "fFechaRegreso"
								}, {
									sName : "cRuta"
								}, {
									sName : "cPartida"
								}, {
									sName : "Status"
								}, {
									sName : "nFolioVuelos",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "nDocRenglon",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "cUnidadEjecutora",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mQ",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mTarifa",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mIVA",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mTUA",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mYR",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "cEsPagado",
									bSearchable : true,
									bSortable : false,
									bVisible : false,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mImporteServicio",
									bSearchable : true,
									bSortable : false,
									bVisible : true,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mImporteIVAServicio",
									bSearchable : true,
									bSortable : false,
									bVisible : true,
									sClass : "alignCenter",
									sWidth : "40px"
								}, {
									sName : "mGranTotal",
									bSearchable : true,
									bSortable : false,
									bVisible : true,
									sClass : "alignCenter",
									sWidth : "40px"
								} ],
								oLanguage : es_mx
							});

			$("#tblDetalleBoletos tbody").live("click", function(event) {
				clickTblDetalleBoletos(event);
			});

		}

		function creaDlgDatosVuelo() {

			$("#dlgDatosVuelo").dialog({
				title : "Detalle de Vuelo",
				autoOpen : false,
				height : 350,
				width : 800,
				modal : true,
				open : function() {
					muestraDatosVuelos();
				},
				buttons : {
					"Aceptar" : function() {
						updateLayoutDet();
						$("#dlgDatosVuelo").dialog("close");
					},
					"Cancelar" : function() {

						$("#dlgDatosVuelo").dialog("close");
					}
				},
				beforeclose : function(){
					muestraDetalleBoletos();
				}
			});
		}

		function clickTblDetalleBoletos(event) {
			$(tblDetalleBoletos.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('table-primary');
			});

			$(event.target.parentNode).addClass('table-primary');
			var aPost = tblDetalleBoletos.fnGetPosition(event.target.parentNode);
			var aData = tblDetalleBoletos.fnGetData(aPost);

			limpiarVGlobales();
			
			var rfc = aData[1];
			var nombre = aData[2];
			var numvuelo = aData[3];
			var total = aData[4];
			var fsalida = aData[5];
			var fRegreso = aData[6];
			var ruta = aData[7];
			var partida = aData[8];
			var status = aData[9];
			var folioVuelos = aData[10];
			var renglon = aData[11];			
			var cUE = aData[12];
						 
		 	var Q = aData[13];
			var Tarifa = aData[14];
			var IVA = aData[15];
			var TUA = aData[16];
			var YR = aData[17];
			
			cEsPagado = aData[18];			
			
			RFC = rfc;
			cNombre = nombre;
			cReferencia = numvuelo;
			fFechaSalida = fsalida;
			fFechaRegreso = fRegreso;
			cRuta = ruta;
			cPartida = partida;
			Status = status;
			nFolioVuelos = folioVuelos;
			nDocRenglon = renglon;			
			cUnidadEjecutora = cUE;
						 
		 	mQ = Q;
			mTarifa = Tarifa;
			mIVA = IVA;
			mTUA = TUA;
			mYR = YR;
			mTotal = total;						
		}
		
		function limpiarVGlobales(){
		
			RFC = "";
	  		cNombre = "";
	  		cReferencia = "";
			fFechaSalida = "";
			fFechaRegreso = "";
			cRuta = "";
			cPartida = "";
			Status = "";
			nFolioVuelos = "0";
			nDocRenglon = "0";			
			cUnidadEjecutora = ""; 
			 
		 	mQ = 0.00;
			mTarifa = 0.00;
			mIVA = 0.00;
			mTUA = 0.00;
			mYR = 0.00;
			mTotal = 0.00;
			
			cEsPagado = "";
		}
		
		function muestraDatosVuelos(){
			
			$("#RFC").val(RFC);
			$("#cNombre").val(cNombre);
			$("#cReferencia").val(cReferencia);
			$("#cRuta").val(cRuta);
			$("#cPartida").val(cPartida);
			$("#estatus").val(Status);
			$("#nFolioVuelos").val(nFolioVuelos);
			$("#nDocRenglon").val(nDocRenglon);
			$("#cUnidadEjecutora").val(cUnidadEjecutora);
			$("#fFechaSalida").val(fFechaSalida);
			$("#fFechaRegreso").val(fFechaRegreso);
			$("#mQ").val(Number(mQ).toFixed(2));
			$("#mTarifa").val(Number(mTarifa).toFixed(2));
			$("#mIVA").val(Number(mIVA).toFixed(2));
			$("#mTUA").val(Number(mTUA).toFixed(2));
			$("#mYR").val(Number(mYR).toFixed(2));
			$("#mTotal").val(Number(mTotal).toFixed(2));
			
			$("#nFolioVuelos").val(nFolioVuelos);
			$("#nDocRenglon").val(nDocRenglon);			
		}
		
		function Sinfrmt(fld) {		
			var valcol = $(fld).val();
			valcol = valcol.replace("$", "");
			valcol = valcol.replace(",", "");		
			$(fld).val(valcol);
		}
		
		function cambiafrmt(fld) {		
			$(fld).formatCurrency();
		}
		
		function quitaFmt(val) {
		   	val = val.replace("$", "");
		   	val = val.replace(/,/g, "");
		
			if (val.indexOf("(") >= 0) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
			}
			return val;
		}
		
		function updateLayoutDet(){
		
			//document.getElementById("dlgDatosVuelo").disabled = true;
			
			$("#RFCAux").val("");
			$("#cNombreAux").val("");
			$("#cReferenciaAux").val("");
			
			$("#cRutaAux").val("");
			$("#cPartidaAux").val("");
			$("#cUEAux").val("");
			$("#fSalidaAux").val("");
			$("#fRegresoAux").val("");
			
			$("#mQAux").val("");
			$("#mTarifaAux").val("");
			$("#mIVAAux").val("");
			$("#mTUAAux").val("");
			$("#mYRAux").val("");
			$("#mTotalAux").val("");
			
			$("#RFCAux").val($("#RFC").val());
			$("#cNombreAux").val($("#cNombre").val());
			$("#cReferenciaAux").val($("#cReferencia").val());
		
			$("#cRutaAux").val($("#cRuta").val());
			$("#cPartidaAux").val($("#cPartida").val());
			$("#cUEAux").val($("#cUnidadEjecutora").val());
			$("#fSalidaAux").val($("#fFechaSalida").val());
			$("#fRegresoAux").val($("#fFechaRegreso").val());
			
			$("#mQAux").val(quitaFmt($("#mQ").val()));
			$("#mTarifaAux").val(quitaFmt($("#mTarifa").val()));
			$("#mIVAAux").val(quitaFmt($("#mIVA").val()));
			$("#mTUAAux").val(quitaFmt($("#mTUA").val()));
			$("#mYRAux").val(quitaFmt($("#mYR").val()));
			$("#mTotalAux").val(quitaFmt($("#mTotal").val()));
			
			if($("#estatus").val() == "S" || $("#estatus").val() == "V"){
				queryFormPost("tComisionesSinComprobacionDetUpdate", {async:false});
			}else if ($("#estatus").val() == "RG"){
				queryFormPost("tInfoBoletoVueloUpdate", {async:false});
			}
			
			queryFormPost("tLayoutVueloDetRowUpdate", {async:false});
			
		}
		
		function eliminarRowVuelo(){
		
			$("#cEsPagado").val(cEsPagado);
			$("#estatus").val(Status);
			$("#nFolioVuelos").val(nFolioVuelos);
			$("#nDocRenglon").val(nDocRenglon);
			
			if($("#estatus").val() == "A" && $("#cEsPagado").val() == "N"){
				queryFormPost("tLayoutVuelosDetRowDelete", {async:false});
				
				muestraDetalleBoletos();
			}else{
				Swal.fire({ icon: "error",
							text: "No se puede eliminar el vuelo por que ya se encuentra Pagado ó esta relacionado a una Comprobación y/o Comisión sin Viaticos."});				
			}
		}
	</script>

</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="layoutForm" action="../layouts/cargaLayoutVuelos" method="POST" enctype="multipart/form-data">
		<input type="hidden" id="cEsPago" name="cEsPago" value="N">
		<input type="hidden" id="nFolioVuelos" name="nFolioVuelos" value="">
		
		<input type="hidden" id="estatus" name="estatus" value="">
		<input type="hidden" id="nDocRenglon" name="nDocRenglon" value="">
		
		<input type="hidden" id="RFCAux" name="RFCAux" value="">
		<input type="hidden" id="cNombreAux" name="cNombreAux" value="">
		<input type="hidden" id="cReferenciaAux" name="cReferenciaAux" value="">
		<input type="hidden" id="cRutaAux" name="cRutaAux" value="">
		<input type="hidden" id="cPartidaAux" name="cPartidaAux" value="">
		<input type="hidden" id="cUEAux" name="cUEAux" value="">
		<input type="hidden" id="fSalidaAux" name="fSalidaAux" value="">
		<input type="hidden" id="fRegresoAux" name="fRegresoAux" value="">
		<input type="hidden" id="mQAux" name="mQAux" value="">
		<input type="hidden" id="mTarifaAux" name="mTarifaAux" value="">
		<input type="hidden" id="mIVAAux" name="mIVAAux" value="">
		<input type="hidden" id="mTUAAux" name="mTUAAux" value="">
		<input type="hidden" id="mYRAux" name="mYRAux" value="">
		<input type="hidden" id="mTotalAux" name="mTotalAux" value="">
		
		<input type="hidden" id="cEsPagado" name="cEsPagado" value="">
		
		<div id="container" class="ms-5" class="container" style="width: 90%"><!--Inicia div container-->		
			<div class="card-header"> <h3> Cargar Layout Boletos Avión </h3> </div>
			<div class="row d-flex mt-2">								
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
					<label for="chk_Genera" class="form-label"> Genera Folio: </label>
					<input type="checkbox" class="form-check-input" id="chk_Genera" name="chk_Genera" value="" >					
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="chk_Existente" class="form-label"> Folio Existente: </label>
					<input type="checkbox" class="form-check-input" id="chk_Existente" name="chk_Existente" value="" >						            
				</div>
			</div>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="nFolioVuelo" class="form-label"> Folio: </label>
					<input type="text" class="form-control form-control-sm" id="nFolioVuelo" name="nFolioVuelo" value="" readonly >					
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="Status" class="form-label"> Estatus: </label>
					<input type="text" class="form-control form-control-sm" id="Status" name="Status" value="" readonly>						            
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<br>	
					<input type="button" id="btnCargar" name="btnCargar" value="Cargar Layout" onclick="abrirDlgBoletaje()" class="btn btn-secondary"/>
				</div>
			</div>

			<span> 
				<label style="font-weight:bold; font-size: 10px; text-align: right;">*Doble click en el renglon para desplegar datos de Folio-Estatus-Layout de Vuelos. </label>					
			</span>	
					
			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
		            <div class="table-responsive">	    				        	
						<table id="dtFoliosVuelos"  class="table table-striped" >
							<thead>
								<tr>
									<th> ID </th>
									<th> Folio </th>
									<th> Fecha Registro </th>							
									<th> Estatus </th>
									<th> Importe Total </th>							
								</tr>
							</thead>							
						</table>
					</div>
				</div>
			</div>	
									
			<span>
				<label style="font-weight:bold; font-size: 10px; text-align: right;">*Para ver detalle del folio; Seleccione el renglon y de click en ver detalle. </label>
			</span>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
					<input type="button" id="btnVerDet" name="btnVerDet" value="Ver Detalle" onclick="verDetalleBoletos()" class="btn btn-secondary"/>
				</div>
			</div>
	
			<div id="dlgDetalleBoletos">				
				<fieldset>
					<legend>Selección de Vuelos</legend>
					<label style="font-size: 11px; font-weight: bold;"> Seleccione el Boleto dando click y despues click en Editar</label>
					
					<div class="row d-flex">													
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
				            <div class="table-responsive">	    				        	
								<table id="tblDetalleBoletos"  class="table table-striped" >							
									<thead>
										<tr>
											<th> Sel. </th>
											<th> RFC </th>
											<th> Nombre </th>
											<th> Núm. Boleto </th>
											<th> Importe </th>
											<th> Fecha Salida </th>
											<th> Fecha Regreso </th>
											<th> Ruta </th>
											<th> Partida </th>
											<th> Estatus </th>
											<th> Folio </th>
											<th> Renglon </th>
											<th> Unidad Ejecutora </th>
											<th> mQ </th>
											<th> mTarifa </th>
											<th> mIVA </th>
											<th> mTUA </th>
											<th> mYR </th>
											<th> Pagado </th>
											<th> Servicio </th>
											<th> IVA Servicio</th>
											<th> Gran Total </th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
					
					<span>
						<label style="font-weight:bold; font-size: 10px; text-align: right;">*Para eliminar el renglon de click sobre el mismo y despues click en Eliminar. </label>
					</span>
				</fieldset>
			</div>
						
		</div>
		
		<div id="dlgDatosVuelo">		
			<h5> Datos de Vuelo </h5>
			<hr class="mt-3"/>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="RFC" class="form-label"> RFC: </label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="RFC" name="RFC" size="15" >									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cNombre" class="form-label"> Nombre: </label>											           
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="cNombre" name="cNombre" size="50" >
				</div>
			</div>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cReferencia" class="form-label"> Núm. Boleto: </label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="cReferencia" name="cReferencia" size="15" >									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cNombre" class="form-label"> Ruta: </label>											           
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="cRuta" name="cRuta" size="20" >
				</div>
			</div>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cPartida" class="form-label"> Partida: </label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="cPartida" name="cPartida" size="12" >									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="cUnidadEjecutora" class="form-label"> Unidad Ejecutora: </label>											           
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="cUnidadEjecutora" name="cUnidadEjecutora" size="12" >
				</div>
			</div>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="fFechaSalida" class="form-label"> Fecha Salida: </label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="date" class="form-control form-control-sm" id="fFechaSalida" name="fFechaSalida" size="12" >									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<label for="fFechaRegreso" class="form-label"> Fecha Regreso: </label>											           
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<input type="date" class="form-control form-control-sm" id="fFechaRegreso" name="fFechaRegreso" size="12" >
				</div>
			</div>

			<br/>
			<h5> Importes Vuelo </h5>
			<hr class="mt-3"/>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<label for="mQ" class="form-label"> Q: </label>									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mQ" name="mQ" size="12" onKeyPress="return valFmt(this,19)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);">									
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<label for="mTarifa" class="form-label"> TARIFA: </label>											           
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mTarifa" name="mTarifa" size="12" onKeyPress="return valFmt(this,19)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<label for="mIVA" class="form-label"> IVA: </label>											           
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mIVA" name="mIVA" size="12" onKeyPress="return valFmt(this,19)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);">
				</div>
			</div>
			
			<div class="row d-flex">								
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<label for="mTUA" class="form-label"> TUA: </label>									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mTUA" name="mTUA" size="12" onKeyPress="return valFmt(this,19)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);">									
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<label for="mYR" class="form-label"> YR: </label>											           
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mYR" name="mYR" size="12" onKeyPress="return valFmt(this,19)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
					<label for="mTotal" class="form-label"> TOTAL: </label>											           
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mTotal" name="mTotal" size="12" onKeyPress="return valFmt(this,19)" onfocus="Sinfrmt(this)" onblur="cambiafrmt(this);">
				</div>
			</div>
			
		</div>		
	</form>

<!-- Dialogo donde se cargara el layout de Boletos de Avión  -->
<div class="modal" tabindex="-1" role="dialog" id="dlgBoletajeAvion" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      	<div class="modal-header">
	        	<h5 class="modal-title">Adjuntar archivo</h5>
	        	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	    	</div>
	    	<div class="modal-body">
		      	<div class="row">
					<div class="col-12">
						          <iframe id="cargaFrm" width="385" height="250"></iframe>
					</div>
				</div>
	    	</div>
	      	<div class="modal-footer">
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      	</div>
    	</div>
  	</div>
</div>
</body>
</html>
