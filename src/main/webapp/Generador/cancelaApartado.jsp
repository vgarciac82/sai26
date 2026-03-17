<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	if (usuario == null) {
		//response.sendRedirect("../index.jsp");
		//return;
	}
	String ur = usuario.getU_UR();
	String usuLogin = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
       
    <title>Cancela Apartado</title>
    
	<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<script src="js/bootstrap.bundle.min.js"></script>
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";	
	</style>
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		
<script type="text/javascript">
var bClicBtn = false;
var table; 

$(document).ready(function(){
		
		queryFormPost("tEjercicioRead",{async: false });
		querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
		querySelectPost("vTipoCasoPagosAptd","cIdTipoPago", {async: false });
		$("#cIdUnidadAdministrativa").val( "<%=ur%>" );
		if ( "<%=cCentroContable%>" != "10" ){
   			$("#Campos").val( "<%=ur%>" );
   			$("#cIdUnidadAdministrativa").attr('disabled', true);
   		}
   		
	$('#cIdUnidadAdministrativa').change( function () {
 			var pParam = $( this ).val(); 
 			if(pParam == "A02"){
 				pParam = "";
 			}else{
 				pParam = " cUnidadResponsable = '" + pParam + "' and ";
 			}
 			$("#cIdTipoPago").val( "TODOS" );
	
 			Consultar( pParam );
  	});
	
	$('#cIdTipoPago').change( function () {
 			var pParam = $('#cIdUnidadAdministrativa').val();
 			if(pParam == "RHQ"){
 				pParam = "";
 			}else{
 				pParam = " cUnidadResponsable = '" + pParam + "' and ";
 			}

 			if($( this ).val() != "TODOS"){
 				//if( pParam != "" ) pParam = " and ";
 				pParam = pParam + " DOCTO = '" + $( this ).val() + "' and ";
 			}
 			
 			Consultar( pParam );
  	});
	

	$('#btnProcesar')
		.button()
		.click( function() {
    		procesar();
	} );

	
	$('#cIdUnidadAdministrativa').change();


	$('#dialog-procesar').dialog({
	    autoOpen: false,
	    modal: true,
	    resizable: false,
	    width: 500,
	    heigth: 900,
	    title: 'Procesando Cancelaciones de Apartado',
	    show: "blind",
	    hide: "scale",
	    closeOnEscape: false,
		beforeClose: function( event, ui ) {
			return bClicBtn;			
		},
	    overlay: { backgroundColor: '#FFF',
				   opacity: 6.5   
		}
	});
	

});

function seleccionar() {
	 let isChecked = $('#select-all').prop('checked');
     $('.marcar').prop('checked', isChecked);
}
function Consultar( pParam ){
	
			table = $('#dataDevengado').dataTable({         
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
								sSearch: "Buscar:",
								oPaginate: {
									sFirst:    "Primero",
									sPrevious: "Ant.",
									sNext:     "Sigte.",
									sLast:     "&Uacute;ltimo"
								}
							},							
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCancelaApartadoEncabezado&qw=" + pParam + "caNoContrarrecibo != '0' AND caNoContrarrecibo != '' ",
							"bLengthChange" : true,
							"bFilter" : true,
							"bSort" : true,
							"bInfo" : true,
							"bPaginate" : true,
							"bAutoWidth" : true,
							"bScrollCollapse" : true,
							"sScrollXInner": "100%", 
							"sScrollX": "100%",
							"sPaginationType" : "full_numbers",
							"bJQueryUI" : true,
							"bRetrive" : true,
							"bDestroy" : true,
							"bServerSide": true,
							"fnInitComplete": function() {    
								    table.fnAdjustColumnSizing();
								},
							"iDisplayLength": 25, 
							aaSorting: [[ 3, "asc" ]] ,
							aoColumns: [
							    { sName: "id",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "Center"},
								{ sName: "docto",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "cIdRFC",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "cnombre",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "left"},
								{ sName: "cUnidadResponsable",  bSearchable: true,	bSortable: true	, bVisible: true, sClass: "center"},
								{ sName: "aEjercicioFiscal",	bSearchable: true,	bSortable: true	, bVisible: false, sClass: "left"},
								{ sName: "fAplicacionF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "center"},
								{ sName: "mImporteNetoF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "right"},
								{ sName: "nFolioEnc",			bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "cCentroContable",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"},
								{ sName: "nFolioPagoApartado",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "center"}
							]
		        		});	
}

function procesar(){
	
	var data = $('#dataDevengado').dataTable().fnGetNodes();
	var oTable = $('#dataDevengado').dataTable();
	var tipoDocumento = "";
	var nFolio = "";
	var cNombre = "";	
	var cUR = "";	
	var aData = oTable.fnGetData();
	var cCentroContable = "";
	var mImporteNetoF = "";
	var cTipoDocto = "PagoApartado";
	var caNoContrarrecibo = "";
	var cIdRFC = "";
	var nCuantos = 0;
	
	for(var i=0; i < data.length; i++){
		var chkbox = $('input', data[i] )[0].checked;
		if( chkbox == true ){
			nCuantos++
		}

	}

	if(nCuantos == 0){
		alert("Seleccione al menos un Documento");
		$("#btnProcesar").show();
		$("#esperar").attr("style","visibility=hidden");
		return;
	}else{
		Swal.fire({
			  title: '¿Esta seguro de continuar?',
			  text: "Se cancelará \n " + " " + nCuantos + " contrato",
			  icon: 'question',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
					$("#dialog-procesar" ).dialog( "open" );
		
					for(var i=0; i < data.length; i++){
						var chkbox = $('input', data[i] )[0].checked;
						
						if( chkbox == true ){
							tipoDocumento = aData[i][ 1 ];
							caNoContrarrecibo = aData[i][ 2 ];
							cIdRFC = aData[i][ 3 ];
							cNombre = aData[i][ 4 ];
							cUR = aData[i][ 5 ];
							mImporteNetoF = aData[i][ 8 ];
							nFolio = aData[i][ 9 ];
							cCentroContable = aData[i][ 10 ];
							nFolioApartado = aData[i][ 11 ];
							$("#ProcMsg").html( "Cancelando Apartado de " + tipoDocumento + " con Folio " + nFolio );
							$.ajax({
								url: './cierrePresupuestal.jsp',
								type: 'post',
								dataType: 'json',
								async: false,
								data: {tipo:'cancelaEjercidoPagado', nFolioAdefa:nFolioApartado, tipoDocumento:cTipoDocto},
								success: function(data){
										if(data.sinSesion == "sinSesion"){
											location.href = "../index.jsp";
										}else if(data.estatus == "guardado"){
											getNextSequenceVal({seqName: "RCH" + cCentroContable, async: false, callback: setSequenceValRCH});
											if (tipoDocumento == "PAGODIRECTO"){
												$("#noFolio").val( "PDIR-" + cUR + "-" + nFolio );	
											}else{
												$("#noFolio").val( "RELG-" + cUR + "-" + nFolio );
											}
							     			
							     			$("#id_caso_oper").val( 3 );
							     			$("#id_oper").val( 3 );
							     			$("#co_responsable").val( "CONSULTA_" + tipoDocumento );
							     			$("#id_caso").val( nFolio );
											$("#cIdRFC").val( cIdRFC );
							     			$("#caNoContrarrecibo").val( caNoContrarrecibo );
							     			$("#nfolio").val( nFolio );
							     			$("#cIdMotivoCancelacion").val( "1" );
							     			$("#motivo").val( "Cancelado por Cierre de Periodo " );
							     			$("#cnombreRFC").val( cNombre );
							     			$("#fAplicacion").val( "<%=today%>" );								     			
							     			$("#mImporteNeto").val( mImporteNetoF );  
											$("#ProcMsg").html( "Apartado Cancelado con Éxito del " + tipoDocumento + " con Folio " + nFolio );
											if (tipoDocumento == "PAGODIRECTO"){
												queryFormPost("tVolanteRechazoCreate,tPagoDirectoEncCancelaUpdate,AvanzaCasoGenerico", {async: false });	
											}else{
												queryFormPost("tVolanteRechazoCreate,tRelGastosEncCancelaUpdate,AvanzaCasoGenerico", {async: false });
											}
										}else{
											$("#ProcMsg").html( "Apartado No Fue Cancelado " + tipoDocumento + " con Folio " + nFolio );
										}
								}
							});
				
						}
						
					}
					bClicBtn = true;
					$("#ProcMsg").html( "Cancelando Documentos de Apartado" );
					$("#dialog-procesar" ).dialog( "close" );
					Consultar( "" );
				 } else if (result.dismiss === Swal.DismissReason.cancel) {

					$("#dialog-procesar" ).dialog( "close" );
			  }
			})
	}
	
}


function setSequenceValRCH(seqValue){
	seqValue = "000000" + seqValue;
	seqValue = seqValue.substr(seqValue.length - 6);
	seqValue = "<%=cCentroContable%>" + "RCH" + $("#aEjercicioFiscal").val() + seqValue;
	$("#folioDevolucion").val( seqValue );
}
	  	


</script>
  </head>
  <body id="dt_example">
  <br/>
		<form id="formAdefas" name="formAdefas" action="../gstnmngr/generaAdefa" method="post" >
			<input type="hidden" id="nFolioAdefa" name="nFolioAdefa">
			<input type="hidden" id="numFolios" name="numFolios" />
			<input type="hidden" id="nFolioAdefaTabla" name="nFolioAdefaTabla" />
			<input type="hidden" id="folioDevolucion" name="folioDevolucion" />		
			<input type="hidden" id="noFolio" name="noFolio" value="">
			<input type="hidden" id="nfolio" name="nfolio" value="">
			<input type="hidden" id="motivo" name="motivo" value="">
			<input type="hidden" id="mImporteNeto" name="mImporteNeto" value="">
			<input type="hidden" id="fAplicacion" name="fAplicacion" value="">
			<input type="hidden" id="cnombreRFC" name="cnombreRFC" value="">
			<input type="hidden" id="cIdMotivoCancelacion" name="cIdMotivoCancelacion" value="">
			<input type="hidden" name="DPC_FECHA" id="DPC_FECHA"  value="<%=today%>" />
			<input type="hidden" name="caNoContrarrecibo" id="caNoContrarrecibo"  value="" />
			<input type="hidden" name="cIdRFC" id="cIdRFC"  value="" />
			<input type="hidden" name="id_caso" id="id_caso"  value="" />
			<input type="hidden" name="id_caso_oper" id="id_caso_oper"  value="" />
			<input type="hidden" name="id_oper" id="id_oper"  value="" />
			<input type="hidden" name="co_responsable" id="co_responsable"  value="" />
			<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value=""/>
		
  			<div id="container" style="width:90%" class="container">
  			
				<div class="card-header"> <h3> Cancela Apartado </h3> </div>
				<hr class="mt-3"/>
				
 				<div class="row d-flex justify-content-center">
					<div class="col-8 col-lg-8 col-md-8 col-sm-12">
						<label for="cIdUnidadAdministrativa" class="form-label"> Unidad Ejecutora: </label>
						<select id="cIdUnidadAdministrativa" name="cIdUnidadAdministrativa" class="form-select form-select-sm">
								<option value="RHQ"></option>
						</select>
					</div>
					<div class="col-4 col-lg-4 col-md-4 col-sm-12">
						<label for="cIdTipoPago" class="form-label"> Tipo de Pago: </label>
						<select id="cIdTipoPago" name="cIdTipoPago" class="form-select form-select-sm">
								<option value="TODOS">TODOS</option>
						</select>
					</div>
				</div>
				
				<br>
				<div class="row">
					
					<div class="col-lg-2 col-md-2 col-sm-12">
						<br>
						<input class="form-check-input" type="checkbox" value="" id="select-all" onChange="seleccionar()"/> 
						<label class="form-check-label" for="select-all"> Seleccionar Todos </label>
					</div>
					<div class="col-lg-8 col-md-8 col-sm-12">							
					</div>
					<div class="col-lg-2 col-md-2 col-sm-12">							
						<input type="button" class="btn btn-secondary" id="btnProcesar" value="Procesar"/>
					</div>
				</div>
				
				<br/>
							
				<div id="apartado" class="table-responsive">	      
					<table id="dataDevengado" class="table table-striped">						   							
						<tbody>
							<thead>
								<tr>
									<th>-</th>
									<th>Documento</th>
									<th>Recibo</th>	
									<th>RFC</th>
									<th>Beneficiario</th>
									<th>UR</th>
									<th>Ejercicio Fiscal</th>
									<th>Fecha Aplicacion</th>
									<th>Importe Neto</th>	
									<th>Folio</th>
									<th>CC</th>
									<th>Folio Apartado</th>
								</tr>	
							</thead>
						</tbody>
					</table>		
	  			</div>
  			</div>
  			
			<div id="dialog-procesar">
				<div id="esperar" align="center">Espere por favor....
					<div id="ProcMsg" align="center" style="font-size: 10pt">Cancelando Documentos de Apartado</div>
					<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
		</form>
  </body>
</html>

