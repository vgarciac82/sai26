<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	
	String ef = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal(); 
	
	c1.set(Calendar.DAY_OF_MONTH, 1);
	c1.set(Calendar.MONTH, 0);
	c1.set(Calendar.YEAR, Integer.parseInt( ef ));
	
	String inicio = sdf.format(c1.getTime());
	
	if( Integer.parseInt( ef ) != c1.get(Calendar.YEAR) ){
		c1.set( Calendar.DAY_OF_MONTH, 31 );
		c1.set( Calendar.MONTH, 11);
		c1.set( Calendar.YEAR, Integer.parseInt( ef ) );
		
	}else
		c1 = Calendar.getInstance();
	
	String fin = sdf.format(c1.getTime());
	String cUR = "";
	
	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if( u == null ){
		response.sendRedirect("../index.jsp");
		return;
	}
	
	cUR = u.getU_UR();
	boolean esRespuesta = request.getParameter("RESPUESTA") != null ? true: false;	
		
	String msg = (String)session.getAttribute("RESULT");
	session.removeAttribute("RESULT");
	
	String mensaje = "";
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Reporte de Vuelos.</title>

	<!-- Estilos estandar para los controles JQuery -->	
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
	
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" charset="utf-8">		
		 
		$(document).ready(function() {
		
			//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
			$("input.AyudaSyC").subIniciaDlg();
			//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
			//$("input.autoCompletaSyC").subIniciaAutoCompleta();
			
		    $("#fechaI").val(moment().format('yyyy-01-01'));
			$("#fechaF").val(moment().format('yyyy-MM-DD'));
			cargarUnidaEjecutora();
			
			querySelectPost("tCatAerolineasRead", "tipoAerolinea", {async:false});
				
			$("#cbUEjecutora").change(function () {
				var cUe = $(this).val();
					$("#cUnidadEjecutora").val( cUe );			
			});	
			
			$("#btnMostrarVuelos").button();
			$("#btnMostrarVuelos").click(function() { btnMostrarVuelos(); });
			
			$("#btnExportarExcel").button();
			$("#btnExportarExcel").click(function() { exportarExcel(); });
			
			creaDtVuelosDet();
			
			$("#chkVueloComprobado").change(function(){
				if ($("#chkVueloComprobado").prop("checked")){
					$("#tramiteTR").css("display","block");
					$("#chkVueloPendiente").prop("checked",false);			
				}else{
					$("#tramiteTR").css("display","none");
					
				}	
				limpiaTramites();		
			});
			
			$("#chkVueloPendiente").change(function(){
				if ($("#chkVueloPendiente").prop("checked")){
					$("#tramiteTR").css("display","none");
					$("#chkVueloComprobado").prop("checked",false);			
				}
				limpiaTramites();		
			});
		
		}); //Fin del Ready
	
		function cat_beneficiario(){
			$("#cTipoRfc").val("3"); //EMPLEADO CNF
			window.open('CatalogoBeneficiariosRG.jsp?formName=rptelayoutForm&inputRFCTarget=beneficiario', 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
		}
		
		function limpiaTramites(){
			
			$("input:radio[name=Tramite]:checked").each( 
				function(){
					$(this).attr("checked",false);
				}
			); 
		}
	
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
		
		function cargarUnidaEjecutora(){
		
			if ("<%=cUR%>" == "A02") {
				querySelectPost("tCatUnidadesEjecutorasRead", "cbUEjecutora",{async: false });		
			}else{
				$("#cUnidadEjecutora").val("<%=cUR%>");
				$("#dlgUE").hide();
			}
		}
		
		var dtVuelosDet;
		function creaDtVuelosDet(){
			dtVuelosDet = 
			     $('#dtVuelosDet').dataTable({
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
			});				
		}						
		
		function btnMostrarVuelos(){
			$("#fInicio").val($("#fechaI").val().split('-').reverse().join('/'));
			$("#fFin").val($("#fechaF").val().split('-').reverse().join('/'));
			
			if ($("#fechaI").val() == "" || $("#fechaF").val() == ""){
				Swal.fire({ icon: 'warning',
							text: "Favor de seleccionar la fecha de inicio y fecha de fin." });
				return;
			}
				
			var sWhere = " fFechaSalida BETWEEN '" + $("#fInicio").val() + "' AND '" + $("#fFin").val() + "'";
			
			if (!$("#chkVueloComprobado").prop("checked") && !$("#chkVueloPendiente").prop("checked") ){
				Swal.fire({ icon: 'warning',
							text: "Favor de seleccionar el tipo de Vuelo a Mostrar." });					
				return;
			}
			
			if ($("#chkVueloComprobado").prop("checked")){
				sWhere = sWhere + " AND Status IN ('RG', 'S', 'V', 'U' , 'MV')  ";
			}else if ($("#chkVueloPendiente").prop("checked")){
				sWhere = sWhere + " AND Status = 'A' ";
			}
			
			queryFormPost("updateValidacionRG", {async:false});
			queryFormPost("updateValidacionCSV", {async:false});
			var tramite = $("input:radio[name=Tramite]:checked").val();
			
			if( "1" == tramite )
				sWhere = sWhere + " AND Tramite = 'RG'";
			else if( "2" == tramite )
				sWhere = sWhere + " AND Tramite = 'CSV'";
			
			if( $("#vuelosVigentes" ).attr( "checked" ) )
				sWhere = sWhere + " AND vigente = 'S' ";
				
			if( $("#tipoAerolinea").val() !=  "0")
				sWhere = sWhere + " AND nIDTipoAerolinea =  " + $("#tipoAerolinea").val();
				
			if( $("#beneficiario").val() !=  "")
				sWhere = sWhere + " AND RFC ='" + $("#beneficiario").val() + "'";
					
			if ("<%=cUR%>" == "A02") {
				if($("#cUnidadEjecutora").val() != "*"){
					sWhere = sWhere + " AND " + " cUnidadEjecutora = '" + $("#cUnidadEjecutora").val() + "'";
				}		
			}else{
				sWhere = sWhere + " AND " + " cUnidadEjecutora = '<%=cUR%>'";
			}
				
			dtVuelosDet = 
		    $('#dtVuelosDet').dataTable({
		    	"bPaginate" : false,
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
					"fnInitComplete": function() {
						dtVuelosDet.fnAdjustColumnSizing();
					},
					"fnPreDrawCallback": function () {
												$.blockUI({
													title: "Populating Table, please wait...",
													message: "  Please Wait..."
												});
											},
					"fnDrawCallback": function () {
					 	$.unblockUI();
					 }, 				
			        sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_tLayoutVuelosDet_Conciliacion2&qw=" + sWhere,
			        aoColumns   : [
			            { sName: "cReferencia", sClass : "rightCls" },
						{ sName: "RFC", sClass : "rightCls" },
						{ sName: "cNombre" },
						{ sName: "cRuta"},
						{ sName: "fFechaSalida" },
						{ sName: "fFechaRegreso" },
						{ sName: "cDescripcionEstatus", sClass : "centerCls" },
						{ sName: "cEsPagadoDescripcion", sClass : "centerCls" },
						{ sName: "Folio", sClass : "rightCls" },
						{ sName: "cPartida" }		,
						{ sName: "mTotal" }	,
						{ sName: "cUnidadEjecutora" },
						{ sName: "UR" }
					],
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
		
		}
		
	
		
		function exportarExcel() {
			$.blockUI();
			try {
				borraElementos();
	
				var info = dtVuelosDet.fnGetData();
	
				var cont = 0;
	
				createInput( 'reporteExcelForm', 'generaExcel', "1" );
	
				for( i = 0; i < info.length; i++ ) {
					var rowInfo = info[ i ];
					createInput( 'reporteExcelForm', 'boleto', rowInfo[ 0 ] );
					createInput( 'reporteExcelForm', 'rfc', rowInfo[ 1 ] );
					createInput( 'reporteExcelForm', 'nombre', rowInfo[ 2 ] );
					createInput( 'reporteExcelForm', 'ruta', rowInfo[ 3 ] );
					createInput( 'reporteExcelForm', 'fsalida', rowInfo[ 4 ] );
	
					var fechaRegreso = rowInfo[ 5 ];
					if( fechaRegreso == null ) {
						fechaRegreso = "SIN REGRESO";
					}
	
					createInput( 'reporteExcelForm', 'fregreso', fechaRegreso );
	
					createInput( 'reporteExcelForm', 'status', rowInfo[ 6 ] );
					createInput( 'reporteExcelForm', 'pagado', rowInfo[ 7 ] );
					createInput( 'reporteExcelForm', 'folio', rowInfo[ 8 ] );
					createInput( 'reporteExcelForm', 'partida', rowInfo[ 9 ] );
					createInput( 'reporteExcelForm', 'total', rowInfo[10 ] );
					createInput( 'reporteExcelForm', 'ue', rowInfo[ 11 ] );
					createInput( 'reporteExcelForm', 'urE', rowInfo[ 12 ] );
					cont++;
				}
	
				if( cont > 0 ) {
					$( "#reporteExcelForm" ).submit();
					$.unblockUI();
				} else {
					Swal.fire({ icon: 'info',
								text: "No hay resultados para exportar." });					
					$.unblockUI();
				}
			} catch( e ) {
				$.unblockUI();
				Swal.fire({ icon: 'warning',
							text: e });				
			}
		}
	
		function borraElementos() {
			$( '.remove' ).remove();
		}
	
		function createInput( form, name, value ) {
			$( '<input>' ).attr( {
			type : 'hidden',
			name : name,
			value : value
			} ).addClass( 'remove' ).appendTo( '#' + form );
		}
	</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
<br/>
	<form id="reporteExcelForm" method="post" target="_blank" action="../vuelos/reporteVuelos">
	</form>
		
	<form id="rptelayoutForm" action="../vuelos/reporteVuelos" method="POST" enctype="multipart/form-data">
		<input type="hidden" id="cnombre" name="cnombre" value="">
		<input type="hidden" id="cTipoRfc" name="cTipoRfc" value="">
		<input type="hidden" id="cEsPago" name="cEsPago" value="N">	
		<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value="*">	
		<input type="hidden" name="fInicio" id="fInicio" />
		<input type="hidden" name="fFin" id="fFin" />	
				
		<div id="container">					
			<div class="card-header"> <h3> Vuelos </h3> </div>
			<hr class="mt-3"/>
			
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<div class="form-check">
						<input type="checkbox" name="chkVueloComprobado" id="chkVueloComprobado" class="form-check-input" value = "0">
						<label for="chkVueloComprobado" class="form-check-label">Vuelos Comprobados</label>
					</div>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<div class="form-check">
						<input type="checkbox" name="chkVueloPendiente" id="chkVueloPendiente" class="form-check-input" value = "0">
						<label for=chkVueloPendiente class="form-check-label">Vuelos Pendientes</label>
					</div>
				</div>				
			</div>
			
			<div id="tramiteTR" style="display:none;">
				<div class="row">
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">
						<label class="form-label"><b>Seleccione el tramite de comprobacion:</b></label>	
					</div>					
				</div>
				
				<div class="row">					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12">						
						<div class="form-check">								
							<input type="radio" name="Tramite" id="Todos" class="form-check-input" value = "0" />
							<label for="Todos" class="form-check-label">Todos</label>
						</div>
						<div class="form-check">
							<input type="radio" name="Tramite" id="RG" class="form-check-input" value = "1" />								
							<label for="RG" class="form-check-label">Relación de Gastos</label>
						</div>
						<div class="form-check">
							<input type="radio" name="Tramite" id="CSV" class="form-check-input" value = "2" />								
							<label for="CSV" class="form-check-label">Comprobación sin Viaticos</label>
						</div>
						<div class="form-check">
							<input type="checkbox" name="vuelosVigentes" id="vuelosVigentes" class="form-check-input" >
							<label for=vuelosVigentes class="form-check-label">Mostrar vuelos Vigentes</label>
						</div>
					</div>					
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-6">
					<label class="form-label">Tipo de Aerolinea:</label>
					<select name="tipoAerolinea" id="tipoAerolinea" class="form-select form-select-sm" >
						<option id="-1">Seleccione una Opcion</option>																	
					</select>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-6">
					<label class="form-label">Beneficiario:</label>
					<div class="input-group">
						<input type="text" class="form-control" name="beneficiario" id="beneficiario" size="15" >
						<input type="button" class="btn btn-secondary btn-sm" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="cat_beneficiario()">
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<div class="form-group">
						<label for="fechaI">Fecha Inicio:</label>
	                    <div class="input-group date" id="datepicker1">
	                    	<input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI"/>                                    
	                    </div>
	                </div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<div class="form-group">
						<label for="fechaF">Fecha Fin:</label>
	                    <div class="input-group date" id="datepicker1">
	                    	<input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                    
	                    </div>
	                </div>
				</div>
			</div>
			
			<div id="dlgUE">
				<div class="row">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">
						<label class="form-label" id ="lblUEjecutora">Unidad Ejecutora:</label>
						<select name="cbUEjecutora" id="cbUEjecutora" class="form-select form-select-sm" >
							<option value="*"></option>																	
						</select>
					</div>
				</div>
			</div>
		</div>
			<br>						
			<h5> Detalle </h5>
			<hr class="mt-3"/>
			
			<div id="dlgUE">
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<button type="button" id="btnMostrarVuelos" name="btnMostrarVuelos" class="btn btn-secondary btn-sm" >Mostrar Vuelos</button>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<button type="button" id="btnExportarExcel" name="btnExportarExcel" class="btn btn-secondary btn-sm" >Exportar</button>
					</div>
				</div>
			</div>
			
			<br/>	
			
			<div class="table-responsive">	
				<table id="dtVuelosDet" class="table table-striped">					
					<thead>
						<tr>
							<th> Boleto </th>
							<th> RFC </th>
							<th> Nombre </th>
							<th> Ruta </th>
							<th> Salida </th>
							<th> Regreso </th>
							<th> Estatus </th>
							<th> Pagado </th>
							<th> Folio</th>
							<th> Partida </th>
							<th> Total </th>
							<th> UE </th>
							<th> UR Emp </th>
						</tr>
					</thead>
				</table>
			</div>	
		
	</form>
	
</body>
</html>
