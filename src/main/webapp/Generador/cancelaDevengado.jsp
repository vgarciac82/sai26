<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
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
       
    <title>Cancela Devengado</title>
    
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
		querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
		querySelectPost("vTipoCasoPagos","cIdTipoPago", {async: false });
		$("#cIdUnidadAdministrativa").val( "<%=ur%>" );
		if ( $("#cIdUnidadAdministrativa").val() != "A02" ){ 
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
 			if(pParam == "A02"){
 				pParam = "";
 			}else{
 				pParam = " cUnidadResponsable = '" + pParam + "' and ";
 			}

 			if($( this ).val() != "TODOS"){
 				pParam = pParam + " DOCTO = '" + $( this ).val() + "' and ";
 			}
 			Consultar( pParam );
  	});
	

	$( "#dialog-Motivo" ).dialog({

		autoOpen: false,
		height: 250,
		width: 650,
		modal: true,
		buttons: {
				"Aceptar": function() 
				{
					var nFolioAdefaTabla = $("#nFolioAdefaTabla").val();
					$.ajax({
						url: './cierrePresupuestal.jsp',
						type: 'post',
						dataType: 'json',
						data: {tipo:'cancelaAdefa', nFolioAdefa:nFolioAdefaTabla},
						success: function(data){
								if(data.sinSesion == "sinSesion"){
									location.href = "../index.jsp";
								}else if(data.estatus == "guardado"){
									Swal.fire("OK","Cancelado Correctamente","success");
								}else{
									Swal.fire("Error:","No Se Cancelo Correctamente","error");	
								}
								$("#esperar").attr("style","visibility=hidden");
								$("#btnProcesar").show();
								$("#btnExportar").show();
						}
					});
					$( this ).dialog( "close" );
				},
				"Cancelar": function() {
					$( "#" + $("#Name").val() ).attr('checked',true);
					$( this ).dialog( "close" );
					
					$("#esperar").attr("style","visibility=hidden");
					$("#btnProcesar").show();
					$("#btnExportar").show();
				}
			},
		close: function() {										
		}							
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
	    width: 350,
	    heigth: 200,
	    title: 'Procesando Cancelaciones de Devengado',
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
							sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDevengadoEncabezado&qw=" + pParam + "caNoContrarrecibo != '0' AND caNoContrarrecibo != '' ",
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
							    { sName: "id",					bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter", sWidth:"50px"},
								{ sName: "docto",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter", sWidth:"100px"},
								{ sName: "caNoContrarrecibo",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cIdRFC",				bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cnombre",				bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignLeft", sWidth:"300px"},
								{ sName: "cUnidadResponsable",  bSearchable: true,	bSortable: true	, bVisible: true, sClass: "alignCenter", sWidth:"60px"},
								{ sName: "aEjercicioFiscal",	bSearchable: true,	bSortable: true	, bVisible: false, sClass: "alignLeft"},
								{ sName: "fAplicacionF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
								{ sName: "mImporteNetoF",		bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignRight"},
								{ sName: "nFolioEnc",			bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"},
								{ sName: "cCentroContable",		bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"}
							]
		        		});	
}

function procesar(){
	
	var table = document.getElementById("dataDevengado");
	var data = $('#dataDevengado').dataTable().fnGetNodes();
	var docCuantos = 0;
	var total = 0;
	var tipoDocumento = "";
	var nFolio = "";	
	var oTable = $('#dataDevengado').dataTable();
	var aData = oTable.fnGetData();
	
	for(var i=0; i < data.length; i++){
		var chkbox = $('input', data[i] )[0].checked;
		
		if( chkbox == true ){
			var cCentroContable = aData[i][10];
									
			tipoDocumento += aData[i][1] + "/";
			nFolio += aData[i][9] + "/";
						
			var docSelec = 1;
			docCuantos = docCuantos + 1;	
		}
		
	}
	
	if(docCuantos == 0){
		Swal.fire("Atención:","Seleccione un Documento","info");
		$("#btnProcesar").show();
		return;
	}else{
		Swal.fire({
			  title: '¿Esta seguro de continuar?',
			  text: "Se cancelará el Devengado de \n " + " " + docCuantos + " documentos",
			  icon: 'question',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
				  $("#dialog-procesar" ).dialog( "open" );

					$.ajax({
							url: './cierrePresupuestal.jsp',
							type: 'post',
							dataType: 'json',
							data: { tipo:'cancelaDevengado', tipoDocumento:tipoDocumento, nFolio:nFolio, autorizadoPorFiel: 'false' },
							success: function(data){
									if(data.sinSesion == "sinSesion"){
										location.href = "../index.jsp";
										
									}else if(data.estatus == "guardado"){
										Swal.fire("OK","Documentos Cancelados Correctamente","success");
										
									}else if(data.estatus == "sinInformacion"){
										alert("Error al Buscar Documento");
										
									}else{
										alert("No Guardado");	
									}
									bClicBtn = true;
									$("#dialog-procesar" ).dialog( "close" );
									location.reload();
							}
					});
			  } else if (result.dismiss === Swal.DismissReason.cancel) {
				  	$("#btnProcesar").show();
					$("#btnExportar").show();
					$("#dialog-procesar" ).dialog( "close" );
			  }
			})
		
	}
}

</script>
  </head>
  <body id="dt_example">
  <br/>
		<form id="formAdefas" name="formAdefas" action="../gstnmngr/generaAdefa" method="post" >
			<input type="hidden" id="nFolioAdefa" name="nFolioAdefa">
			<input type="hidden" id="numFolios" name="numFolios" />
			<input type="hidden" id="nFolioAdefaTabla" name="nFolioAdefaTabla" />
						
  			<div id="container" style="width:90%" class="container">
 				
				<div class="card-header"> <h3> Cancela Devengado </h3> </div>
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
				<br/>
				
				<div class="row">
					<div class="col-lg-2 col-md-2 col-sm-12">
						<br>
						<input class="form-check-input" type="checkbox" value="" id="select-all" onChange="seleccionar()"/> 
						<label class="form-check-label" for="select-all"> Seleccionar Todos </label>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12">							
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
						<input type="button" class="btn btn-secondary" id="btnProcesar" value="Procesar"/>
					</div>
				</div>
				
				<br/>
	 				 
	 			<div id="devengado" class="table-responsive">	 
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
									<th></th>
									<th>Fecha Aplicacion</th>
									<th>Importe Neto</th>	
									<th>Folio</th>
									<th>CC</th>
								</tr>	
							</thead>
						</tbody>
					</table>
				</div>
 					
  			</div>
  			
  			<div id="dialog-Motivo" title="Motivo">
		 		<td>Esta Seguro Que Desea Cancelar El Documento Adefa</td>         
			</div>
			<div id="dialog-procesar">
				<div id="esperar" align="center">Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
		</form>
  </body>
</html>

